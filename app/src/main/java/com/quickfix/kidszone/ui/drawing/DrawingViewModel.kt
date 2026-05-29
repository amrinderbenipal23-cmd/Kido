package com.quickfix.kidszone.ui.drawing

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.ui.components.BrushType
import com.quickfix.kidszone.ui.components.DrawPath
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DrawingUiState(
    val paths: List<DrawPath> = emptyList(),
    val currentPath: DrawPath? = null,
    val selectedColor: Color = Color(0xFF845EC2),
    val strokeWidth: Float = 12f,
    val brushType: BrushType = BrushType.NORMAL,
    val showSavedMessage: Boolean = false,
    val undoStack: List<List<DrawPath>> = emptyList(),
)

val brushColors = listOf(
    Color(0xFFFF6B6B), Color(0xFFFF6B9D), Color(0xFF845EC2), Color(0xFF4ECDC4),
    Color(0xFFFFBE0B), Color(0xFF06D6A0), Color(0xFF0077B6), Color(0xFF1A1A2E),
    Color.White,
)

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val tts: KiddoTextToSpeech,
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrawingUiState())
    val uiState: StateFlow<DrawingUiState> = _uiState.asStateFlow()

    fun onPathStart(offset: Offset) {
        _uiState.update { state ->
            state.copy(
                currentPath = DrawPath(
                    points = listOf(offset),
                    color = state.selectedColor,
                    strokeWidth = state.strokeWidth,
                    brushType = state.brushType,
                )
            )
        }
    }

    fun onPathContinue(offset: Offset) {
        _uiState.update { state ->
            val current = state.currentPath ?: return@update state
            state.copy(currentPath = current.copy(points = current.points + offset))
        }
    }

    fun onPathEnd() {
        _uiState.update { state ->
            val current = state.currentPath ?: return@update state
            state.copy(
                paths = state.paths + current,
                currentPath = null,
                undoStack = state.undoStack + listOf(state.paths),
            )
        }
    }

    fun undo() {
        _uiState.update { state ->
            if (state.undoStack.isEmpty()) return@update state
            val previousPaths = state.undoStack.last()
            state.copy(
                paths = previousPaths,
                undoStack = state.undoStack.dropLast(1),
            )
        }
    }

    fun clearCanvas() {
        _uiState.update { it.copy(paths = emptyList(), currentPath = null, undoStack = emptyList()) }
    }

    fun selectColor(color: Color) {
        _uiState.update { it.copy(selectedColor = color, brushType = BrushType.NORMAL) }
    }

    fun selectBrush(type: BrushType) {
        _uiState.update { it.copy(brushType = type) }
    }

    fun setStrokeWidth(width: Float) {
        _uiState.update { it.copy(strokeWidth = width) }
    }

    fun saveDrawing(context: Context) {
        viewModelScope.launch {
            val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "KiddoArt_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/KiddoLearn")
                }
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
                _uiState.update { s -> s.copy(showSavedMessage = true) }
                settingsDataStore.addStars(1)
                tts.speak("Great drawing! Saved to your gallery!")
            }
            kotlinx.coroutines.delay(2000)
            _uiState.update { it.copy(showSavedMessage = false) }
        }
    }
}
