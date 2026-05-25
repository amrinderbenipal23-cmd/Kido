package com.kido.app

import android.app.Application
import com.kido.app.core.audio.NarrationService
import com.kido.app.core.content.ContentRepository
import com.kido.app.core.profile.ChildProfileStore

/**
 * Manual service-locator for v0. Replace with Hilt/Koin once the surface area
 * grows past a handful of services.
 */
class KidoApp : Application() {

    lateinit var narration: NarrationService
        private set
    lateinit var content: ContentRepository
        private set
    lateinit var profile: ChildProfileStore
        private set

    override fun onCreate() {
        super.onCreate()
        narration = NarrationService(this)
        content = ContentRepository(this)
        profile = ChildProfileStore(this)
    }

    override fun onTerminate() {
        narration.shutdown()
        super.onTerminate()
    }
}
