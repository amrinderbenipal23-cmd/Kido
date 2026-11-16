# Front-end implementation playbook: 3D motion marketing site as a static (no-build) page — 2026

Scope: static HTML/CSS/JS + pinned CDN libraries for a security agency + multi-service consultancy (security, financial management, cybersecurity, HR, data science, AI/ML, robotics, resume making, agriculture, banking, investment, "PRC at home", aptitude tests). Tagline motif: compass ("Find your North" / "Find your direction").

Research date: 2026-11-16. Note on method: the research sandbox could not open threejs.org, gsap.com, web.dev, MDN, schema.org, developers.google.com, cdnjs or jsDelivr directly, so library facts were taken from the npm registry (package metadata + the actual published tarballs, which are byte-identical to what jsDelivr/unpkg serve), and browser/platform facts from web search summaries of primary pages. Where a claim could not be tied to a primary page it is listed under Gaps.

---

## Key Question 1 — Current stable versions of Three.js, GSAP, Lenis, Spline runtime (and Lottie, Vanta, Splitting) and how to load them from CDNs with import maps

### Takeaway
As of 2026-11-16 the `latest` npm tags are: three 0.187.0 (= r187, published 2026-10-30), gsap 3.15.1 (2026-11-04; a 4.0.0-beta.4 also exists under the `beta` tag), lenis 1.3.27 (2026-10-15; 2.0.0-dev.x prereleases exist), @splinetool/runtime and @splinetool/viewer 2.0.184, lottie-web 5.13.0, @lottiefiles/dotlottie-wc 0.12.2, vanta 0.5.24, splitting 1.1.0. Three.js must be loaded as ES modules through an import map (`three` → `build/three.module.js`, `three/addons/` → `examples/jsm/`); GSAP, Lenis, Vanta and Splitting ship classic UMD `.min.js` files for `<script>` tags; Spline runtime is ESM-only.

### Cited Findings
- `three` latest = 0.187.0, MIT; recent releases: 0.184.0 (2026-04-16), 0.185.0 (2026-06-25), 0.186.0 (2026-09-08), 0.186.1 (2026-09-24), 0.187.0 (2026-10-30) — [npm registry: three](https://registry.npmjs.org/three)
- three@0.187.0 `build/` contains only `three.cjs`, `three.core.js`, `three.module.js`, `three.tsl.js`, `three.webgpu.js`, `three.webgpu.nodes.js` — there is **no** `three.min.js` and **no** `three.module.min.js` in the package. `three.module.js` does `import … from './three.core.js'` (relative), so both files are fetched. Measured: three.module.js 658,900 B raw / 129,326 B gzip; three.core.js 1,474,799 B raw / 289,866 B gzip (≈419 KB gzip unminified total) — [npm tarball three-0.187.0.tgz](https://registry.npmjs.org/three/-/three-0.187.0.tgz)
- three@0.187.0 package `exports` map includes `.`, `./tsl`, `./addons`, `./addons/*`, `./webgpu`, `./examples/jsm/*` — [npm registry: three/0.187.0](https://registry.npmjs.org/three/0.187.0)
- Addons present in the tarball: `examples/jsm/controls/OrbitControls.js`, `examples/jsm/postprocessing/EffectComposer.js`, `RenderPass.js`, `UnrealBloomPass.js`, `examples/jsm/lines/Line2.js`, `examples/jsm/geometries/TextGeometry.js`; addons import the bare specifier `three`, which is why an import map is mandatory — [npm tarball three-0.187.0.tgz](https://registry.npmjs.org/three/-/three-0.187.0.tgz)
- The three.js manual's no-build install pattern maps `three` to `https://cdn.jsdelivr.net/npm/three@<version>/build/three.module.js` and `three/addons/` to `https://cdn.jsdelivr.net/npm/three@<version>/examples/jsm/`; the manual warns every three dependency must come from the same version and same CDN — [three.js manual mirror](https://neofixer.arizona.edu/css/CSSOrbit/asteroidJS/three/docs/manual/en/introduction/Installation.html); working r149 example — [three.js Discourse](https://discourse.threejs.org/t/npm-free-threejs/50097/2); official manual URL — [threejs.org/manual/en/installation.html](https://threejs.org/manual/en/installation.html)
- Import maps are Baseline "newly available" since 2023-03-27 (Safari 16.4); Chrome/Edge 89, Firefox 108, Safari 16.4; feature-detect with `HTMLScriptElement.supports('importmap')`; es-module-shims is the usual polyfill — [web.dev: import maps cross-browser](https://web.dev/blog/import-maps-in-all-modern-browsers); [caniuse import-maps](https://caniuse.com/import-maps); [web-features explorer](https://web-platform-dx.github.io/web-features-explorer/features/import-maps)
- jsDelivr generates a minified file when you append `.min` to a JS/CSS path and none exists; first request can be slow; generated files carry a header saying they were minified with Terser and warn against using SRI with them — [jsDelivr GitHub readme](https://github.com/jsdelivr/jsdelivr); [jsDelivr SRI docs](https://www.jsdelivr.com/?docs=sri)
- `gsap` latest = 3.15.1 (2026-11-04); dist-tags `{latest: 3.15.1, beta: 4.0.0-beta.4, next: 3.0.0-beta.11}`; 4.0.0-beta.3 published 2026-11-05, 4.0.0-beta.4 on 2026-11-11; license "Standard 'no charge' license" — [npm registry: gsap](https://registry.npmjs.org/gsap); [gsap 4.0.0-beta.4](https://registry.npmjs.org/gsap/4.0.0-beta.4)
- gsap@3.15.1 `dist/` ships `gsap.min.js` (73,357 B raw / 28,408 B gz), `ScrollTrigger.min.js` (44,683 / 18,018), `SplitText.min.js` (7,955 / 3,645), `ScrollSmoother.min.js` (13,373 / 5,528), plus `Observer`, `ScrollToPlugin`, `MotionPathPlugin`, `TextPlugin` `.min.js` — [npm tarball gsap-3.15.1.tgz](https://registry.npmjs.org/gsap/-/gsap-3.15.1.tgz)
- GSAP README: "Thanks to Webflow, GSAP is now 100% FREE including ALL of the bonus plugins like SplitText, MorphSVG … even for commercial use"; ScrollTrigger is "the standard" for scroll-driven animation with ScrollSmoother as companion — [gsap README on jsDelivr](https://cdn.jsdelivr.net/npm/gsap@3.15.0/README.md); announcement — [Webflow blog: GSAP becomes free](https://webflow.com/blog/gsap-becomes-free) (end of April 2025); release notes — [gsap.com/blog/3-13](https://gsap.com/blog/3-13/); SplitText rewrite — [Webflow engineering: SplitText rewrite](https://origin.webflow.com/blog/gsap-splittext-rewrite)
- `lenis` latest = 1.3.27 (2026-10-15), MIT; 2.0.0-dev.3…dev.6 prereleases (Sep–Oct 2026) — [npm registry: lenis](https://registry.npmjs.org/lenis)
- Lenis README install snippet (no build): `<script src="https://unpkg.com/lenis@1.3.27/dist/lenis.min.js"></script>` and `<link rel="stylesheet" href="https://unpkg.com/lenis@1.3.27/dist/lenis.css">`; "no-code" one-liner `new Lenis({ autoRaf: true, autoToggle: true, anchors: true, allowNestedScroll: true, naiveDimensions: true, stopInertiaOnNavigate: true })`; `dist/lenis.min.js` measured 18,815 B raw / 5,417 B gz — [lenis README (in npm tarball)](https://registry.npmjs.org/lenis/-/lenis-1.3.27.tgz); GitHub — [darkroomengineering/lenis](https://github.com/darkroomengineering/lenis)
- Lenis + GSAP ScrollTrigger integration (from README): `lenis.on('scroll', ScrollTrigger.update); gsap.ticker.add((time) => lenis.raf(time * 1000)); gsap.ticker.lagSmoothing(0);` — [lenis README](https://github.com/darkroomengineering/lenis)
- Lenis limitations: no CSS scroll-snap (use lenis/snap), capped to 60 fps on Safari and 30 fps in low-power mode, stops over iframes, `syncTouch` unstable on iOS < 16 — [lenis README](https://github.com/darkroomengineering/lenis)
- `@splinetool/runtime` and `@splinetool/viewer` latest = 2.0.184 (no license field in registry). Runtime `package.json` is `"type": "module"` with `exports` `.` → `./build/runtime.js` (import) / `./build/runtime.cjs` (require), plus `./build/runtime.standalone.js`, `runtime.standalone.webgl.js`, `runtime.standalone.webgpu.js`, `runtime.standalone.webgpu.min.js`, `runtime.standalone.webgpu.micro.js`, `./build/physics.js` + `physics.wasm`. `build/runtime.js` is ESM (`export { … as Application, Axis, Easing, … }`) and measures 131,747 B raw / 37,798 B gz as the entry (it lazy-loads sibling chunks such as bevel.js, boolean.js, physics.js, navmesh.js, opentype.js, howler.js; full tarball is 19 MB) — [npm registry: @splinetool/runtime](https://registry.npmjs.org/@splinetool/runtime); [runtime tarball](https://registry.npmjs.org/@splinetool/runtime/-/runtime-2.0.184.tgz)
- Spline runtime README: "loads a .splinecode file exported from the Spline editor and renders it on a <canvas> (WebGPU, with a WebGL fallback)"; usage `import { Application } from '@splinetool/runtime'; const spline = new Application(canvas); spline.load('https://prod.spline.design/<id>/scene.splinecode')`; objects via `findObjectByName` / `findObjectById`; CORS fix = download and self-host the `.splinecode`; "Only .splinecode files should be loaded through this API" — [@splinetool/runtime README](https://www.npmjs.com/package/@splinetool/runtime); docs — [docs.spline.design](https://docs.spline.design)
- Spline `<spline-viewer>` web component quickstart: `<script type="module" src="https://cdn.spline.design/@splinetool/viewer/build/spline-viewer.js"></script> <spline-viewer url="https://prod.spline.design/<id>/scene.splinecode"></spline-viewer>`; "pin a version with @splinetool/viewer@<version>"; the default `spline-viewer.js` "contains both rendering engines" and measures 3,629,891 B raw / 1,000,445 B gzip — [@splinetool/viewer README + tarball](https://registry.npmjs.org/@splinetool/viewer/-/viewer-2.0.184.tgz)
- `lottie-web` latest = 5.13.0, MIT; builds `build/player/lottie.min.js` (305,704 B / 76,045 B gz), `lottie_light.min.js` (168,394 B / 46,395 B gz), `lottie_svg.min.js` — [npm registry: lottie-web](https://registry.npmjs.org/lottie-web); tarball — [lottie-web-5.13.0.tgz](https://registry.npmjs.org/lottie-web/-/lottie-web-5.13.0.tgz)
- `@lottiefiles/dotlottie-wc` latest = 0.12.2, MIT (dotlottie-web 0.83.2); usage `<dotlottie-wc src="….lottie" autoplay="true" loop="true"></dotlottie-wc>` after importing the module; `dist/dotlottie-wc.js`, `base-dotlottie-wc.js` (22,727 B / 7,444 B gz), `dotlottie-worker-wc.js`; README documents a "Custom WASM URL" and "Strict Content-Security-Policy" section (the renderer is WASM loaded at runtime) — [npm registry: @lottiefiles/dotlottie-wc](https://registry.npmjs.org/@lottiefiles/dotlottie-wc)
- `vanta` latest = 0.5.24, MIT; dist effects: `vanta.birds|dots|fog|globe|halo|net|rings|topology|waves.min.js`; README: effects are rendered by three.js or p5.js; "Total additional file size is ~120kb minified and gzipped (mostly three.js)"; README's script-tag example loads `https://cdnjs.cloudflare.com/ajax/libs/three.js/r134/three.min.js` **before** `https://cdn.jsdelivr.net/npm/vanta/dist/vanta.waves.min.js`; "Make sure three.js or p5.js has already been included via <script> tag"; or pass `THREE` into the effect (`VANTA.GLOBE({ el, THREE })`). `vanta.globe.min.js` measures 14,520 B / 4,768 B gz; GLOBE default options `color: 0xff3f81, color2: 0xffffff, size: 1, backgroundColor: 0x23153c` and the globe is a `THREE.SphereGeometry(18*size, 18, 14)` drawn with `LineBasicMaterial` (wireframe) — [npm registry: vanta](https://registry.npmjs.org/vanta); tarball — [vanta-0.5.24.tgz](https://registry.npmjs.org/vanta/-/vanta-0.5.24.tgz)
- `splitting` latest = 1.1.0, MIT; `dist/splitting.min.js` (3,871 B / 1,766 B gz), `splitting-lite.min.js`, `splitting.css`, `splitting-cells.css`; "split … words, characters, child nodes … `<span>`s populated with CSS variables and data attributes" — [npm registry: splitting](https://registry.npmjs.org/splitting); guide — [splitting.js.org/guide.html](https://splitting.js.org/guide.html); demos — [CodePen collection](https://codepen.io/collection/43588e4b7beaaf25ede7e38e61441e54/)

### Inferences
- **Pinned CDN manifest (all paths verified to exist in the published npm tarballs; jsDelivr/unpkg serve the tarball verbatim):**

```html
<!-- Three.js r187 — ES modules via import map (jsDelivr; add .min.js to let jsDelivr serve an auto-minified copy) -->
<script type="importmap">
{
  "imports": {
    "three": "https://cdn.jsdelivr.net/npm/three@0.187.0/build/three.module.min.js",
    "three/addons/": "https://cdn.jsdelivr.net/npm/three@0.187.0/examples/jsm/"
  }
}
</script>
<!-- Note: three.module.js imports ./three.core.js relatively; jsDelivr resolves that automatically. Do NOT mix a second three version (e.g. Vanta's r134 global). -->

<!-- GSAP 3.15.1 (free incl. ScrollTrigger + SplitText) — classic scripts, cdnjs or jsDelivr -->
<script src="https://cdn.jsdelivr.net/npm/gsap@3.15.1/dist/gsap.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/gsap@3.15.1/dist/ScrollTrigger.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/gsap@3.15.1/dist/SplitText.min.js"></script>
<!-- cdnjs equivalents: https://cdnjs.cloudflare.com/ajax/libs/gsap/3.15.1/gsap.min.js etc. -->

<!-- Lenis 1.3.27 -->
<link rel="stylesheet" href="https://unpkg.com/lenis@1.3.27/dist/lenis.css">
<script src="https://unpkg.com/lenis@1.3.27/dist/lenis.min.js"></script>

<!-- Splitting.js 1.1.0 (alternative/complement to SplitText; CSS-var based) -->
<link rel="stylesheet" href="https://unpkg.com/splitting@1.1.0/dist/splitting.css">
<link rel="stylesheet" href="https://unpkg.com/splitting@1.1.0/dist/splitting-cells.css">
<script src="https://unpkg.com/splitting@1.1.0/dist/splitting.min.js"></script>

<!-- Lottie: prefer dotLottie web component (small JS + WASM) over lottie-web 76 KB gz -->
<script type="module" src="https://cdn.jsdelivr.net/npm/@lottiefiles/dotlottie-wc@0.12.2/dist/dotlottie-wc.js"></script>
<!-- or classic: <script src="https://cdnjs.cloudflare.com/ajax/libs/lottie-web/5.13.0/lottie_light.min.js"></script> -->

<!-- Spline runtime 2.0.184 (ESM only; import inside a module script) -->
<script type="module">
  import { Application } from 'https://unpkg.com/@splinetool/runtime@2.0.184/build/runtime.js';
</script>
```

- **Vanta is incompatible with the modern module build.** Vanta expects a global `THREE` from a legacy `three.min.js` (r134 in its README), and three@0.187.0 no longer ships a global/UMD build at all. Choose one: (a) Vanta + its own pinned `three@0.134`/r134 global for a zero-code background, or (b) a hand-written Three.js r187 module scene (recommended for the compass/globe hero). Loading both means two copies of three.js (~120 KB + ~170 KB gz).
- **Prefer jsDelivr for Three** because the manual's own snippet uses it and because it resolves `./three.core.js` and the `examples/jsm/` folder from the same version; cdnjs's three.js package may lag or lay files out differently (unverified this session).
- **GSAP 4 is in public beta** (4.0.0-beta.4, Nov 2026). Stay on 3.15.1 for production; note that the npm description for 4.x mentions "springs", so API changes are coming.
- Lenis 2.0 is still `-dev`; pin 1.3.27.
- Spline's hosted viewer is a ~1 MB gzip download; on a marketing page prefer the `runtime.js` ESM entry (≈38 KB gz + lazily-loaded chunks) or, better, a hand-rolled Three.js scene; reserve Spline for a single showcase object.

### Gaps
- Could not fetch cdnjs's file list for three.js 0.187.0 or confirm cdnjs hosts a `three.module.min.js` for that version (cdnjs/jsDelivr/unpkg hosts were blocked in the sandbox). The jsDelivr URLs above are inferred from the verified tarball paths + jsDelivr's documented `npm/<pkg>@<ver>/<path>` scheme.
- Could not read threejs.org's live Installation page; the import-map snippet is confirmed only via a mirror and a Discourse post.
- Spline runtime license is "None" in the registry metadata; check Spline's terms (docs.spline.design) for commercial-use/attribution rules.
- GSAP 4 beta changelog not found (no public release notes surfaced in search).

---

## Key Question 2 — Concise working pattern for a Three.js hero: rotating wireframe compass/globe + particle field following the cursor and scroll, with graceful fallback

### Takeaway
The proven pattern is: one `WebGLRenderer` on a fixed full-bleed `<canvas>`, a `Group` holding a wireframe `SphereGeometry` (globe) plus a flat compass ring/needle built from `RingGeometry`/`Line2`, and a `Points` cloud using a `BufferGeometry` of 2–5k vertices; mouse is normalised to −1…1 and lerped into group rotation each frame; scroll progress (via GSAP ScrollTrigger `scrub` or Lenis's `scroll` event) drives rotation/zoom; render only while the hero is on screen; bail out to a static poster when WebGL is unavailable or `prefers-reduced-motion` is set.

### Cited Findings
- Codrops "Interactive Particles with Three.js" (Jan 2019) teaches drawing a large number of particles and making them react to mouse/touch via an off-screen texture; companion repo nsarno/particles — [Codrops](https://tympanus.net/codrops/2019/01/17/interactive-particles-with-three-js/); [GitHub nsarno/particles](https://github.com/nsarno/particles)
- Mouse-reactive particle sphere example repo: SafarSoFar/sphere-particle-wrap — [r/threejs post](https://cal1.lr.ggtyler.dev/r/threejs/comments/1giflxp/interactive_particle_sphere)
- A WebGPU/TSL interactive scene write-up exists on the three.js forum for teams wanting the WebGPU renderer path — [three.js Discourse](https://discourse.threejs.org/t/interactive-3d-scene-built-with-three-js-webgpu-and-tsl/90492)
- GSAP forum thread "help rotating particles on mouse" shows combining GSAP tweening with three.js object rotation — [gsap.com forums](https://gsap.com/community/forums/topic/21454-help-rotating-particles-on-mouse/)
- Vanta's GLOBE effect is literally a `THREE.SphereGeometry(18*size, 18, 14)` rendered with `LineBasicMaterial` plus a vertex-coloured point cloud and `color2` lines — a useful reference for the "wireframe globe + particles" aesthetic — [vanta src/vanta.globe.js (tarball)](https://registry.npmjs.org/vanta/-/vanta-0.5.24.tgz)
- Lenis exposes `lenis.on('scroll', e => …)` with scroll/progress/velocity data and `lenis.prefersReducedMotion`; by default `respectReducedMotion: true` forces `lerp` to 1 and makes `scrollTo` instant while keeping the loop running so WebGL sync stays intact — [lenis README](https://github.com/darkroomengineering/lenis)
- GSAP README points to `gsap.matchMedia()` for "responsive, accessibility-friendly animations" (i.e. media-query-scoped tweens incl. `prefers-reduced-motion`) — [gsap README](https://cdn.jsdelivr.net/npm/gsap@3.15.0/README.md); docs — [gsap.matchMedia()](https://gsap.com/docs/v3/GSAP/gsap.matchMedia())

### Inferences
- **Reference implementation (author-written against three r187 public API; names verified against the tarball: `three.module.js`, `examples/jsm/postprocessing/UnrealBloomPass.js` etc.):**

```html
<section class="hero">
  <video class="hero__video" autoplay muted loop playsinline preload="metadata" poster="/media/hero-poster.avif">
    <source src="/media/hero-loop.webm" type="video/webm">
    <source src="/media/hero-loop.mp4"  type="video/mp4">
  </video>
  <canvas id="hero-gl" class="hero__gl" aria-hidden="true"></canvas>
  <img class="hero__fallback" src="/media/hero-poster.avif" alt="" hidden>
  <div class="hero__copy">
    <h1 data-split>Find your North</h1>
    <p>Security, finance, talent and technology — one compass.</p>
    <a class="btn btn--magnetic" href="#quote">Get a quote</a>
  </div>
</section>

<script type="module">
import * as THREE from 'three';

const reduce = matchMedia('(prefers-reduced-motion: reduce)').matches;
const canvas = document.getElementById('hero-gl');
const fallback = document.querySelector('.hero__fallback');

function webglOK() {
  try { const c = document.createElement('canvas');
        return !!(c.getContext('webgl2') || c.getContext('webgl')); } catch { return false; }
}
if (!webglOK() || reduce) { canvas.remove(); fallback.hidden = false; }
else init();

function init() {
  const renderer = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true, powerPreference: 'high-performance' });
  renderer.setPixelRatio(Math.min(devicePixelRatio, 2));          // cap DPR for mobile GPUs
  const scene  = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(45, 1, 0.1, 100);
  camera.position.z = 6;

  // --- Globe (wireframe) ---
  const globe = new THREE.Mesh(
    new THREE.SphereGeometry(1.6, 32, 24),
    new THREE.MeshBasicMaterial({ color: 0x2dd4bf, wireframe: true, transparent: true, opacity: 0.35 })
  );

  // --- Compass ring + needle ---
  const ring = new THREE.Mesh(
    new THREE.TorusGeometry(2.1, 0.015, 8, 128),
    new THREE.MeshBasicMaterial({ color: 0xf5f5f4 })
  );
  ring.rotation.x = Math.PI / 2;
  const needle = new THREE.Mesh(
    new THREE.ConeGeometry(0.08, 1.4, 4),
    new THREE.MeshBasicMaterial({ color: 0xf59e0b })
  );
  needle.position.y = 0.9;                 // points "north"
  const compass = new THREE.Group();
  compass.add(globe, ring, needle);
  scene.add(compass);

  // --- Particle field ---
  const N = 2500;
  const pos = new Float32Array(N * 3);
  for (let i = 0; i < N; i++) {
    const r = 3 + Math.random() * 6, t = Math.random() * Math.PI * 2, p = Math.acos(2 * Math.random() - 1);
    pos[i*3]   = r * Math.sin(p) * Math.cos(t);
    pos[i*3+1] = r * Math.sin(p) * Math.sin(t);
    pos[i*3+2] = r * Math.cos(p);
  }
  const pts = new THREE.Points(
    new THREE.BufferGeometry().setAttribute('position', new THREE.BufferAttribute(pos, 3)),
    new THREE.PointsMaterial({ color: 0x94a3b8, size: 0.02, sizeAttenuation: true, transparent: true, opacity: 0.8 })
  );
  scene.add(pts);

  // --- Input: mouse (lerped) + scroll progress ---
  const target = { x: 0, y: 0 }, cur = { x: 0, y: 0 };
  addEventListener('pointermove', e => {
    target.x = (e.clientX / innerWidth) * 2 - 1;
    target.y = -(e.clientY / innerHeight) * 2 + 1;
  }, { passive: true });

  let scrollP = 0;                          // 0..1 while the hero scrolls out
  if (window.ScrollTrigger) {
    ScrollTrigger.create({ trigger: '.hero', start: 'top top', end: 'bottom top', scrub: true,
                           onUpdate: st => { scrollP = st.progress; } });
  }

  // --- Resize ---
  function resize() {
    const { clientWidth: w, clientHeight: h } = canvas.parentElement;
    renderer.setSize(w, h, false); camera.aspect = w / h; camera.updateProjectionMatrix();
  }
  new ResizeObserver(resize).observe(canvas.parentElement); resize();

  // --- Render only while visible ---
  let visible = true, raf;
  new IntersectionObserver(([e]) => { visible = e.isIntersecting; if (visible) loop(); else cancelAnimationFrame(raf); },
                           { threshold: 0 }).observe(canvas);
  const clock = new THREE.Clock();
  function loop() {
    if (!visible) return;
    const t = clock.getElapsedTime();
    cur.x += (target.x - cur.x) * 0.05; cur.y += (target.y - cur.y) * 0.05;
    compass.rotation.y = t * 0.15 + cur.x * 0.6 + scrollP * Math.PI;
    compass.rotation.x = cur.y * 0.3 + scrollP * 0.4;
    needle.rotation.z  = Math.sin(t * 0.8) * 0.05 - cur.x * 0.4;   // needle "searching" then settling
    pts.rotation.y = -t * 0.03 - cur.x * 0.1;
    camera.position.z = 6 + scrollP * 4;                            // pull back as user scrolls
    renderer.render(scene, camera);
    raf = requestAnimationFrame(loop);
  }
  loop();
  document.addEventListener('visibilitychange', () => document.hidden ? cancelAnimationFrame(raf) : loop());
}
</script>
```

- Optional bloom: `import { EffectComposer } from 'three/addons/postprocessing/EffectComposer.js'; import { RenderPass } from 'three/addons/postprocessing/RenderPass.js'; import { UnrealBloomPass } from 'three/addons/postprocessing/UnrealBloomPass.js';` — all three files exist in r187. Skip bloom on mobile (`matchMedia('(max-width: 768px)')` or `navigator.hardwareConcurrency <= 4`) because post-processing doubles fill-rate cost.
- Fallback ladder: WebGL2/WebGL available → scene; not available or reduced-motion → static AVIF/WebP poster (`.hero__fallback`); JS disabled → the `<video poster>` and `<img>` still render via HTML.
- To make the particle field truly "follow" the cursor (displace points near the pointer), raycast against an invisible plane and offset positions in the vertex buffer, or use the Codrops off-screen-texture technique above; the simple lerped-rotation version is cheaper and adequate for a hero.
- If using Lenis instead of ScrollTrigger for scroll progress: `lenis.on('scroll', ({ progress }) => scrollP = Math.min(1, lenis.scroll / innerHeight))`.

### Gaps
- No single CodePen was found that combines wireframe globe + compass + cursor-reactive particles; the snippet above is synthesised from three.js public API and the cited tutorials, not copied from a verified live demo. Test on a low-end Android device.
- threejs.org examples (e.g. `webgl_points_sprites`, `webgl_buffergeometry_points`, `webgl_interactive_points`) were not reachable from the sandbox to confirm current URLs.

---

## Key Question 3 — CSS-only scroll-driven animation support (animation-timeline: scroll()/view()) in Chrome/Edge/Safari/Firefox, late 2026

### Takeaway
Scroll-driven animations became Baseline "newly available" in October 2026: Chrome/Edge since 115 (July 2023), Safari since 26 (Sept 2025; threaded/compositor SDA in 26.4), and Firefox 159 (Oct 2026) enabled them by default. Ship them behind `@supports (animation-timeline: scroll())` with a GSAP ScrollTrigger or IntersectionObserver fallback because a meaningful share of Firefox < 159 and Safari < 26 users remain.

### Cited Findings
- web.dev "New to the web platform in October [2026]": Firefox 159 enables scroll-driven animations by default; with Chrome, Safari and Firefox 159 the feature is Baseline Newly available; Safari support listed from Safari 26 — [web.dev blog, Oct 2026](https://web.dev/blog/web-platform-10-2026)
- Chrome/Edge shipped scroll-driven animations unflagged in Chrome 115 (July 2023) — [buildmvpfast.com](https://www.buildmvpfast.com/blog/css-scroll-driven-animations-replace-js-2026); [dev.to](https://dev.to/chengyixu/css-scroll-driven-animations-the-end-of-javascript-scroll-listeners-4bij)
- Safari 26 landed the feature in September 2025 and added threaded scroll-driven animations in 26.4 — [lilting.ch](https://lilting.ch/en/articles/chrome-146-scroll-animation-safari-grid-lanes)
- As of Firefox 152 (June 2026) the feature was still behind `layout.css.scroll-driven-animations.enabled` — [dev.to (raxxostudios)](https://dev.to/raxxostudios/css-scroll-driven-animations-6-patterns-i-ship-in-2026-4bm6); Mozilla Connect idea thread tracked the request — [connect.mozilla.org](https://connect.mozilla.org/t5/ideas/implement-css-scroll-driven-animations-animation-timeline/idc-p/119842)
- Feature status page — [web-features explorer: scroll-driven-animations](https://web-platform-dx.github.io/web-features-explorer/features/scroll-driven-animations)
- Lenis's `autoToggle` requires `transition-behavior` support: Safari > 17.3, Chrome > 116, Firefox > 128 — [lenis README](https://github.com/darkroomengineering/lenis)

### Inferences
- **Pattern (progressive enhancement):**

```css
/* Reveal cards as they enter the viewport — no JS */
@supports (animation-timeline: view()) {
  .reveal {
    animation: reveal linear both;
    animation-timeline: view();
    animation-range: entry 10% entry 60%;
  }
  @keyframes reveal { from { opacity: 0; transform: translateY(40px) } to { opacity: 1; transform: none } }

  /* Scroll progress bar / compass needle sweep tied to document scroll */
  .progress { transform-origin: left; animation: grow linear both; animation-timeline: scroll(root); }
  @keyframes grow { from { transform: scaleX(0) } to { transform: scaleX(1) } }
}
@media (prefers-reduced-motion: reduce) {
  .reveal, .progress { animation: none; opacity: 1; transform: none; }
}
```

```js
// JS fallback only where CSS SDA is missing
if (!CSS.supports('animation-timeline: view()')) {
  gsap.utils.toArray('.reveal').forEach(el =>
    gsap.from(el, { y: 40, opacity: 0, scrollTrigger: { trigger: el, start: 'top 85%', end: 'top 50%', scrub: true } }));
}
```

- **Important interaction with Lenis:** Lenis wraps native scroll, so `animation-timeline: scroll()` still works (it reads the real scroll position); but smoothing introduces lag between wheel input and the CSS timeline. If you use CSS SDA heavily, consider dropping Lenis or using GSAP's ScrollSmoother (also free) which is designed to coexist with ScrollTrigger.
- Use `view()` for element reveals, `scroll(root)` for page-level progress (compass needle, header shrink), and `animation-range: entry … exit …` to scope ranges; keep JS ScrollTrigger for pinning/scrubbed timelines that CSS can't express (pinned storytelling sections, canvas scrub).

### Gaps
- Firefox 159's exact release date and any remaining Firefox limitations (e.g. `view-timeline-inset`, named timelines across scrollers) were not confirmed from a Mozilla primary source.
- caniuse.com page for `css-scroll-driven-animations` could not be fetched; percentages of global support not available here.

---

## Key Question 4 — Code-level tutorials / CodePens for the headline effects (magnetic buttons, 3D tilt, text reveal, bento grid, video + WebGL layering)

### Takeaway
The canonical open-source references are Codrops "Magnetic Buttons" (MIT repo codrops/MagneticButtons), Codrops "Interactive Particles with Three.js", Osmo's magnetic-hover resource, Motion.dev's JS tilt-card tutorial / vanilla-tilt.js for tilt, GSAP SplitText (now free) or Splitting.js for text reveals, and Lenis's README for the smooth-scroll + ScrollTrigger glue. No single tutorial covers the full stack; the implementation patterns below are assembled from these.

### Cited Findings
- Codrops "Magnetic Buttons" (Aug 2020) — circular button that fills with colour, shadow element for depth; inspired by Cuberto; companion repo MIT — [Codrops](https://tympanus.net/codrops/2020/08/05/magnetic-buttons); [repo mirror listing (license MIT)](https://awesome.ecosyste.ms/projects/github.com%2Fcodrops%2FMagneticButtons); GitHub — [codrops/MagneticButtons](https://github.com/codrops/MagneticButtons)
- Codrops 2025 case study of Eduard Bodak's portfolio animations (GSAP-heavy, good for current-year motion language) — [Codrops](https://tympanus.net/codrops/?p=96161)
- Osmo Supply "Magnetic hover effect" resource (GSAP, Webflow/vanilla) — [osmo.supply](https://www.osmo.supply/resource/magnetic-hover-effect)
- Motion.dev "JS tilt card" tutorial — [motion.dev](https://motion.dev/tutorials/js-tilt-card); a Medium tutorial notes `perspective: 800px` + `transform-style: preserve-3d` on the card are what make the tilt read as 3D — [Medium](https://medium.com/@francesco.saviano87/how-to-create-a-3d-tilt-effect-on-a-card-with-html-css-and-javascript-1d0b0ab5a9d7); vanilla-tilt example with `max: 25` — [CodePen](https://codepen.io/cpandya/pen/MWrGNXY)
- GSAP SplitText rewrite is free and documented — [Webflow engineering](https://origin.webflow.com/blog/gsap-splittext-rewrite); docs — [gsap.com SplitText](https://gsap.com/docs/v3/Plugins/SplitText); ScrollTrigger docs — [gsap.com ScrollTrigger](https://gsap.com/docs/v3/Plugins/ScrollTrigger/)
- Splitting.js splits into spans with CSS variables (`--char-index`, `--word-index`) enabling pure-CSS staggered reveals — [splitting.js.org guide](https://splitting.js.org/guide.html)
- Codrops "Interactive Particles with Three.js" + repo — [Codrops](https://tympanus.net/codrops/2019/01/17/interactive-particles-with-three-js/); [nsarno/particles](https://github.com/nsarno/particles)
- Lenis README: GSAP ScrollTrigger sync snippet and "no-code" one-liner — [lenis README](https://github.com/darkroomengineering/lenis)
- web.dev: a `<video>` element's `poster` is an LCP candidate; preload the poster with `<link rel="preload" fetchpriority="high">`; `preload="none"` on `<video>` prevents unnecessary initial bandwidth — [web.dev video performance](https://web.dev/learn/performance/video-performance); [web.dev lazy-loading video](https://web.dev/articles/lazy-loading-video)

### Inferences — implementation patterns

**Layering AI-generated hero video under a WebGL overlay (z-stack):**
```css
.hero { position: relative; min-height: 100svh; isolation: isolate; overflow: clip; background: var(--bg); }
.hero__video, .hero__gl, .hero__fallback { position: absolute; inset: 0; width: 100%; height: 100%; }
.hero__video { object-fit: cover; z-index: 0; filter: saturate(.8) brightness(.55); }
.hero__gl    { z-index: 1; pointer-events: none; mix-blend-mode: screen; }   /* additive particles over video */
.hero::after { content: ""; position: absolute; inset: 0; z-index: 2;
               background: radial-gradient(60% 60% at 50% 40%, transparent, var(--bg) 100%); } /* vignette for legibility */
.hero__copy  { position: relative; z-index: 3; }
```
```html
<link rel="preload" as="image" href="/media/hero-poster.avif" fetchpriority="high">
<video autoplay muted loop playsinline preload="metadata" poster="/media/hero-poster.avif" disablepictureinpicture>
  <source src="/media/hero-loop-1080.webm" type="video/webm; codecs=av01.0.08M.08">
  <source src="/media/hero-loop-1080.webm" type="video/webm">
  <source src="/media/hero-loop-1080.mp4"  type="video/mp4">
</video>
```
Use `muted` + `playsinline` (iOS requires both for autoplay); the WebGL canvas is `alpha: true` with `mix-blend-mode: screen` so particles/wireframe glow over the footage. Give the `<video>` the first-frame AVIF/WebP as `poster` so LCP is the poster, not the video.

**Magnetic button (GSAP, ~25 lines):**
```js
document.querySelectorAll('.btn--magnetic').forEach(btn => {
  const strength = 0.35, label = btn.querySelector('span') || btn;
  btn.addEventListener('pointermove', e => {
    const r = btn.getBoundingClientRect();
    const x = e.clientX - r.left - r.width / 2, y = e.clientY - r.top - r.height / 2;
    gsap.to(btn,   { x: x * strength, y: y * strength, duration: 0.4, ease: 'power3.out' });
    gsap.to(label, { x: x * strength * 0.5, y: y * strength * 0.5, duration: 0.4, ease: 'power3.out' });
  });
  btn.addEventListener('pointerleave', () => gsap.to([btn, label], { x: 0, y: 0, duration: 0.8, ease: 'elastic.out(1, 0.4)' }));
});
```
Gate it with `matchMedia('(hover: hover) and (pointer: fine)')` so touch devices skip it.

**3D card tilt with glare (no library):**
```css
.card { transform-style: preserve-3d; transition: transform .2s ease; will-change: transform;
        --rx: 0deg; --ry: 0deg; --mx: 50%; --my: 50%;
        transform: perspective(900px) rotateX(var(--rx)) rotateY(var(--ry)); }
.card::after { content:""; position:absolute; inset:0; border-radius:inherit; pointer-events:none;
        background: radial-gradient(circle at var(--mx) var(--my), rgb(255 255 255 / .18), transparent 55%); }
```
```js
document.querySelectorAll('.card').forEach(card => {
  card.addEventListener('pointermove', e => {
    const r = card.getBoundingClientRect(), px = (e.clientX - r.left) / r.width, py = (e.clientY - r.top) / r.height;
    card.style.setProperty('--ry', `${(px - .5) * 16}deg`);
    card.style.setProperty('--rx', `${(.5 - py) * 16}deg`);
    card.style.setProperty('--mx', `${px * 100}%`); card.style.setProperty('--my', `${py * 100}%`);
  });
  card.addEventListener('pointerleave', () => { card.style.setProperty('--rx','0deg'); card.style.setProperty('--ry','0deg'); });
});
```

**Bento grid for 13 service lines (CSS Grid, mobile-first):**
```css
.bento { display: grid; gap: var(--space-3); grid-template-columns: repeat(2, 1fr); grid-auto-rows: minmax(140px, auto); }
@media (min-width: 768px)  { .bento { grid-template-columns: repeat(4, 1fr); } }
@media (min-width: 1200px) { .bento { grid-template-columns: repeat(6, 1fr); } }
.bento > .is-feature { grid-column: span 2; grid-row: span 2; }   /* Security agency, Cybersecurity */
.bento > .is-wide    { grid-column: span 2; }                      /* Financial mgmt, AI/ML, Aptitude tests */
.bento > * { background: var(--surface-1); border: 1px solid var(--border); border-radius: var(--radius-lg);
             padding: var(--space-4); position: relative; overflow: clip; }
```
Suggested weighting: Security agency (feature), Cybersecurity (feature), Financial management (wide), AI/ML (wide), Aptitude tests (wide, with interactive demo), the rest 1×1 (HR, Data science, Robotics, Resume making, Agriculture, Banking, Investment decisions, PRC at home).

**Text reveal (SplitText, free):**
```js
gsap.registerPlugin(ScrollTrigger, SplitText);
document.fonts.ready.then(() => {
  const split = SplitText.create('[data-split]', { type: 'words,chars', mask: 'words' });
  gsap.from(split.chars, { yPercent: 110, opacity: 0, stagger: 0.02, duration: 0.8, ease: 'power4.out' });
});
```
Alternative with Splitting.js + CSS only: `Splitting();` then `.splitting .char { animation: slide-up .6s both; animation-delay: calc(var(--char-index) * 30ms); }`.

**Smooth scroll glue (Lenis + ScrollTrigger), from the Lenis README:**
```js
const lenis = new Lenis({ anchors: true, autoToggle: true });
lenis.on('scroll', ScrollTrigger.update);
gsap.ticker.add(t => lenis.raf(t * 1000));
gsap.ticker.lagSmoothing(0);
```

**Dark-theme design tokens (suggested, compass/security brand):**
```css
:root {
  --bg: #0b0f14;            /* near-black navy */
  --surface-1: #11161d;     --surface-2: #171e27;
  --border: rgb(255 255 255 / .08);
  --text: #e6e9ef;          --muted: #94a3b8;
  --accent: #2dd4bf;        /* compass teal */
  --accent-2: #f59e0b;      /* needle amber (north) */
  --danger: #ef4444;
  --radius-lg: 20px; --radius-md: 12px;
  --space-1: .25rem; --space-2: .5rem; --space-3: 1rem; --space-4: 1.5rem; --space-5: 2.5rem; --space-6: 4rem;
  --font-display: "Space Grotesk", system-ui, sans-serif;
  --font-body: "Inter", system-ui, sans-serif;
  --ease-out: cubic-bezier(.22,1,.36,1); --dur-fast: 180ms; --dur-base: 400ms;
  color-scheme: dark;
}
body { background: var(--bg); color: var(--text); font-family: var(--font-body); }
```

### Gaps
- No tutorial was found that specifically documents layering a WebGL canvas with `mix-blend-mode` over an autoplaying video; the pattern above is standard CSS stacking and should be validated for GPU cost on mid-range Android (blend modes force an extra compositing pass).
- Live CodePen URLs for GSAP's own SplitText/ScrollTrigger demos (gsap.com/demos) could not be fetched for verification.

---

## Key Question 5 — What "PRC at home" most plausibly means in an Indian education/career-consulting context

### Takeaway
No web source uses the phrase "PRC at home" as a branded or generic service. The only widely attested Indian meaning of "PRC" is **Permanent Residence Certificate** — a state-issued domicile document (Assam, Arunachal Pradesh, Nagaland, Manipur, Meghalaya, J&K) required for education quotas, state jobs and reservations — so "PRC at home" most plausibly means doorstep assistance with PRC applications and documentation. Career-counselling expansions (Pre-Recruitment Counselling, Psychological Readiness Check, Police/Para-military Recruitment Coaching) are unconfirmed candidates.

### Cited Findings
- The exact phrase "PRC at home" returned no matching service, provider or programme in search — [search summary over yourstory/wadhwani/karangupta results](https://www.karangupta.com/guides/online-career-counselling)
- PRC = Permanent Residence Certificate: "a government document that serves as a proof of residence of a citizen"; "particularly important in states with domicile-based reservations in India like Assam, Arunachal Pradesh, Nagaland, and Manipur" — [Vedantu](https://www.vedantu.com/general-knowledge/permanent-residency-certificate)
- Assam: issued by the Deputy Commissioner; apply at Circle Office / CSCs / Public Facilitation Centre; eligibility example "parents or forefather … permanent residence in Assam for minimum 50 years; applicant residing … minimum 20 years"; documents include father's PRC, photos, birth certificate — [Vikaspedia (Dhubri, Assam)](https://vikaspedia.in/aspirational-districts/assam/dhubri/citizen-services/apply-for-prc-permanent-residence-certificate); [Sribhumi district portal](https://sribhumi.assam.gov.in/information-and-services/apply-prc); PRC "for Higher Educational Purpose" exists as a distinct service — [Dima Hasao district portal](https://dimahasao.assam.gov.in/information-and-services/apply-permanent-residence-certificate-higher-educational-purpose)
- Arunachal Pradesh (Namsai) also issues PRC through district e-services — [Vikaspedia (Namsai)](https://vikaspedia.in/aspirational-districts/arunachal-pradesh/namsai/citizen-services/apply-for-prc-permanent-residence-certificate)
- J&K: Permanent Residence Certificate ("State Subject") under 1968 rules, ~30-day processing — [IndiaFilings](https://www.indiafilings.com/learn/jammu-and-kashmir-permanent-resident-certificate)
- No source defines PRC as "pre-recruitment" in Indian security-guard hiring; the regulatory context there is PSARA 2005 with character/antecedent verification and photo-ID issuance — [Compliance Calendar: PSARA](https://www.compliancecalendar.in/learn/psara-licence-managing-security-guards-client-locations)
- Context: ~99% of Indian schools lack dedicated career counsellors; NEP 2020 anchors structured counselling — [YourStory](https://ts.yourstory.com/mystory/the-great-indian-career-counselling-crisis-oaupao5t5o); online counselling from home is a growing format — [Karan Gupta guide](https://www.karangupta.com/guides/online-career-counselling)

### Inferences
- Candidate expansions, ranked by evidence: (1) **Permanent Residence Certificate** assistance at home (doorstep document collection, form filling, e-District submission, follow-up) — strongly attested term, fits a "banking/investment/resume/aptitude" general-services consultancy and is especially relevant in the North-East and J&K; (2) **Pre-Recruitment Counselling/Coaching** for security/para-military/police recruitment at home (fits a security agency that recruits guards; no source); (3) **Psychological Readiness Check** or **Personality/Psychometric Readiness Counselling** delivered online (fits aptitude tests; no source); (4) **Police/Para-military Recruitment Coaching** (no source).
- Site copy should spell the expansion out on first use and in the `<title>`/schema `Service.name`, because "PRC" is ambiguous even in India; recommend confirming with the client before launch.

### Gaps
- The client's intended meaning is unknown; no public listing for a "PRC at home" service exists. Treat (2)–(4) as hypotheses only.

---

## Key Question 6 — Performance budgets (LCP, total JS, video), reduced-motion handling and mobile fallbacks

### Takeaway
Target Core Web Vitals "good" thresholds at p75 — LCP ≤ 2.5 s, INP ≤ 200 ms, CLS ≤ 0.1 — which for this stack means: make the AVIF/WebP poster (not the video or canvas) the LCP element and preload it, keep the hero loop a muted 8–15 s 1080p file of ≈2–5 MB (WebM AV1/VP9 + MP4 H.264 fallback), defer Three.js until after first paint, and keep first-load JS to roughly ≤ 250–300 KB gzip (GSAP core+ScrollTrigger+SplitText ≈ 50 KB gz, Lenis 5 KB, Three ≈ 170–420 KB gz depending on minification — load it `type="module"` which is deferred by default).

### Cited Findings
- Core Web Vitals "good" thresholds: LCP ≤ 2.5 s, INP ≤ 200 ms, CLS ≤ 0.1; "needs improvement" ceilings 4.0 s / 500 ms / 0.25; measured at the 75th percentile across mobile and desktop; INP replaced FID in March 2024 — [web.dev: defining CWV thresholds](https://web.dev/articles/defining-core-web-vitals-thresholds); [Ahrefs CWV guide](https://ahrefs.com/blog/core-web-vitals/)
- A `<video>` `poster` is an LCP candidate; preload it with `<link rel="preload" fetchpriority="high">`; `preload="none"` on `<video>` prevents unnecessary initial bandwidth — [web.dev video performance](https://web.dev/learn/performance/video-performance); [web.dev lazy-loading video](https://web.dev/articles/lazy-loading-video)
- Background-loop sizing guidance from practitioner sources (no official budget exists): "720p–1080p background loop, a few MB"; one hero-video guide says under 5 MB; AV1 ≈ 50% smaller than H.264, VP9 30–50% smaller; removing the audio track saves 10–30%; AV1 playback in Safari depends on Apple hardware decoder (full support late 2024) — [BlazingCDN](https://blog.blazingcdn.com/en-us/mp4-vs-webm-best-video-format-for-web-playback); [Adventist Connect hero video best practices](https://support.adventistconnect.org/help/hero-video-best-practices); [Elfsight](https://elfsight.com/guides/optimize-video-for-website/); [Swarmify](https://swarmify.com/blog/best-video-format-for-website/)
- Poster under ~50 KB is a low-authority rule of thumb — [quickfixseo](https://quickfixseo.pages.dev/articles/optimize-hero-images-for-lcp)
- Measured library weights (gzip): three.module.js + three.core.js unminified ≈ 419 KB; gsap.min 28.4 KB; ScrollTrigger.min 18.0 KB; SplitText.min 3.6 KB; ScrollSmoother.min 5.5 KB; lenis.min 5.4 KB; lottie.min 76 KB / lottie_light.min 46 KB; vanta.globe.min 4.8 KB (+ its own three r134 ≈ 120 KB per Vanta README); splitting.min 1.8 KB; Spline runtime.js entry 37.8 KB (+ lazy chunks); spline-viewer.js 1.0 MB — [npm tarballs, see KQ1 sources](https://registry.npmjs.org/three/-/three-0.187.0.tgz)
- `prefers-reduced-motion`: detect in CSS with `@media (prefers-reduced-motion: reduce)` and in JS with `window.matchMedia('(prefers-reduced-motion: reduce)')`, listening for `change`; autoplaying background video should be gated on `no-preference` or replaced by the poster; reduced-motion does **not** satisfy WCAG 2.2.2 — looping content still needs an explicit pause control — [hidde.blog: 2.2.2 Pause, Stop, Hide](https://hidde.blog/meeting-2-22-pause-stop-hide-with-prefers-reduced-motion/); [Scott O'Hara: reduced-motion video](https://www.scottohara.me/note/2019/07/12/reduced-motion-video.html); [OpenReplay](https://blog.openreplay.com/prefers-reduced-motion-accessible-animation/); [Tatiana Mac: no-motion-first](https://www.tatianamac.com/posts/prefers-reduced-motion)
- Lenis honours reduced motion by default (`respectReducedMotion: true`, lerp forced to 1, instant `scrollTo`), exposes `lenis.prefersReducedMotion`, and picks the preference up live — [lenis README](https://github.com/darkroomengineering/lenis)
- GSAP recommends `gsap.matchMedia()` for responsive/accessible animation scoping — [gsap README](https://cdn.jsdelivr.net/npm/gsap@3.15.0/README.md)

### Inferences — budgets and patterns
- **Proposed budgets for this site:** LCP ≤ 2.5 s on a mid-range Android over 4G (poster ≤ 60 KB AVIF, preloaded); CLS ≤ 0.05 (reserve hero height with `min-height: 100svh`, canvas/video `position:absolute`); INP ≤ 200 ms (keep pointer handlers passive, lerp in rAF, never animate layout properties); first-party + CDN JS executed before interaction ≤ 300 KB gzip; Three.js deferred (module scripts are deferred; additionally gate on `requestIdleCallback` or first scroll); hero video ≤ 4 MB WebM + ≤ 6 MB MP4, 1080p, 24–30 fps, 8–12 s loop, no audio track; Lottie assets ≤ 100 KB each, dotLottie preferred; total page weight ≤ 8 MB with video, ≤ 1.5 MB without.
- **Reduced-motion / mobile ladder (JS):**
```js
const rm = matchMedia('(prefers-reduced-motion: reduce)');
const lowEnd = navigator.hardwareConcurrency <= 4 || (navigator.deviceMemory && navigator.deviceMemory <= 4);
const saveData = navigator.connection?.saveData === true;
const video = document.querySelector('.hero__video');
function applyMotion() {
  if (rm.matches || saveData) { video.pause(); video.removeAttribute('autoplay'); /* poster shows */ }
  else video.play().catch(() => {});
}
rm.addEventListener('change', applyMotion); applyMotion();
// Three.js: skip entirely on reduced motion or saveData; reduce particle count + DPR on lowEnd
```
```js
let mm = gsap.matchMedia();
mm.add({ ok: '(prefers-reduced-motion: no-preference)', reduce: '(prefers-reduced-motion: reduce)', desktop: '(min-width: 1024px)' },
  ctx => { const { ok, desktop } = ctx.conditions;
           if (ok) { /* full ScrollTrigger timelines */ }
           if (ok && desktop) { /* pinned sections, parallax */ }
           /* reduce: set final states directly, no tweens */ });
```
- Always provide a visible pause/play toggle for the hero video (WCAG 2.2.2) and expose it to keyboard users.
- Mobile fallbacks: serve a 720p video via `<source media="(max-width: 768px)">`, cut particle count by half, cap `setPixelRatio` at 1.5, disable bloom and magnetic hover, use `100svh`, and replace the WebGL canvas with the poster on `saveData`.

### Gaps
- No official (web.dev/Google) numeric budget exists for hero video or poster size; the MB figures above are practitioner consensus, not standards.
- Minified three.js r187 gzip size could not be measured (no `.min` in package; jsDelivr's auto-minified size unverified). Historic minified core is ~160–180 KB gz; treat as estimate.

---

## Key Question 7 — Aptitude-test interactive demo and a contact/quote form that works on a static host

### Takeaway
An aptitude demo is pure client-side JS (questions in a JSON file, timer, scoring, result card, optional `localStorage` resume); the quote form should POST to a form backend — Web3Forms (≈250 free submissions/month, hidden `access_key`, honeypot `botcheck`, hCaptcha) is the best free fit for a non-Netlify static host; Formspree (~50/month free) and Netlify Forms (only on Netlify hosting) are alternatives.

### Cited Findings
- Web3Forms free tier: 250 submissions/month, unlimited forms and domains, hCaptcha, custom redirects; no overage — submissions stop until next month — [splitforms comparison](https://splitforms.com/blog/best-free-form-backend-services-2026); [Web3Forms vs Formspree page](https://web3forms.com/alternatives/formspree-alternative)
- Formspree free ≈ 50 submissions/month; webhooks are paid; works with any static site — [forminit comparison](https://forminit.com/blog/best-form-backend-services-2026/); [toolradar Formspree vs Netlify](https://toolradar.com/compare/formspree-vs-netlify-forms)
- Netlify Forms: older listings 100/month; one 2026 source says form submissions are free/unlimited under the credit-based plans but tied to Netlify-hosted sites — [merginit comparison](https://merginit.com/blog/24062026-free-form-backend-services-comparison); [formgrid on Netlify forms pricing](https://formgrid.dev/docs/netlify-forms-is-getting-expensive-here-are-the-best-alternatives-in-2026)
- Web3Forms docs home (quickstart with `access_key`, `botcheck`, `redirect`) — [docs.web3forms.com](https://docs.web3forms.com/getting-started/quickstart) (not fetchable from sandbox; fields confirmed by the comparison pages above)

### Inferences — patterns
**Quote form (Web3Forms, progressive: works without JS, enhanced with fetch):**
```html
<form id="quote" action="https://api.web3forms.com/submit" method="POST" class="form">
  <input type="hidden" name="access_key" value="YOUR-WEB3FORMS-KEY">
  <input type="hidden" name="subject" value="New quote request — website">
  <input type="hidden" name="redirect" value="https://example.in/thanks.html">
  <input type="checkbox" name="botcheck" class="hidden" style="display:none" tabindex="-1" autocomplete="off">
  <label>Name <input name="name" required autocomplete="name"></label>
  <label>Phone <input name="phone" type="tel" inputmode="tel" pattern="[+0-9 \-]{10,15}" required autocomplete="tel"></label>
  <label>Email <input name="email" type="email" autocomplete="email"></label>
  <label>Service
    <select name="service" required>
      <option>Security agency</option><option>Cybersecurity</option><option>Financial management</option>
      <option>HR</option><option>Data science</option><option>AI/ML</option><option>Robotics</option>
      <option>Resume making</option><option>Agriculture</option><option>Banking</option>
      <option>Investment decisions</option><option>PRC at home</option><option>Aptitude tests</option>
    </select></label>
  <label>Details <textarea name="message" rows="4"></textarea></label>
  <button class="btn btn--magnetic" type="submit"><span>Request a quote</span></button>
  <p role="status" aria-live="polite" class="form__status"></p>
</form>
<script>
const f = document.getElementById('quote'), status = f.querySelector('.form__status');
f.addEventListener('submit', async e => {
  e.preventDefault(); status.textContent = 'Sending…';
  const res = await fetch(f.action, { method: 'POST', body: new FormData(f), headers: { Accept: 'application/json' } });
  const json = await res.json().catch(() => ({}));
  status.textContent = res.ok ? 'Thanks — we will call you within one business day.' : (json.message || 'Something went wrong. Please WhatsApp us.');
  if (res.ok) f.reset();
});
</script>
```
Netlify variant: `<form name="quote" method="POST" data-netlify="true" netlify-honeypot="bot-field">` plus `<input type="hidden" name="form-name" value="quote">`. Formspree variant: `action="https://formspree.io/f/{id}"`. Add a WhatsApp deep link (`https://wa.me/91XXXXXXXXXX?text=…`) as a secondary CTA — common for Indian SMB leads.

**Aptitude-test demo (static, 10 questions, timed, scored):**
```html
<section id="aptitude" class="card is-wide">
  <h3>Try a 3-minute aptitude sample</h3>
  <div class="quiz" data-src="/data/aptitude-sample.json"></div>
</section>
<script type="module">
const root = document.querySelector('.quiz');
const qs = await (await fetch(root.dataset.src)).json();   // [{id, type:'numerical'|'verbal'|'logical', q, options:[...], answer:2}]
let i = 0, score = 0, t0 = performance.now(), timer;
const state = JSON.parse(localStorage.getItem('aptitude') || 'null') || { i: 0, score: 0 };
({ i, score } = state);
function render() {
  if (i >= qs.length) return finish();
  const q = qs[i];
  root.innerHTML = `<p class="quiz__meta">${q.type} · ${i+1}/${qs.length} · <span class="quiz__clock">180</span>s</p>
    <h4>${q.q}</h4><div class="quiz__opts">${q.options.map((o, k) =>
      `<button class="quiz__opt" data-k="${k}">${o}</button>`).join('')}</div>`;
  gsap.from('.quiz__opt', { y: 12, opacity: 0, stagger: .05, duration: .3 });
}
root.addEventListener('click', e => {
  const b = e.target.closest('.quiz__opt'); if (!b) return;
  if (+b.dataset.k === qs[i].answer) score++;
  i++; localStorage.setItem('aptitude', JSON.stringify({ i, score })); render();
});
function finish() {
  clearInterval(timer); localStorage.removeItem('aptitude');
  const pct = Math.round(score / qs.length * 100), secs = Math.round((performance.now() - t0) / 1000);
  root.innerHTML = `<h4>You scored ${pct}% in ${secs}s</h4>
    <p>${pct >= 70 ? 'Strong — book a full assessment.' : 'Good start — our coaches can lift this.'}</p>
    <a class="btn" href="#quote" onclick="document.querySelector('[name=service]').value='Aptitude tests'">Book the full test</a>`;
}
let left = 180; timer = setInterval(() => { left--; const c = root.querySelector('.quiz__clock'); if (c) c.textContent = left; if (left <= 0) finish(); }, 1000);
render();
</script>
```
Keep questions in `/data/aptitude-sample.json` so content can be edited without touching code; mark the demo as a sample ("not a certified psychometric instrument") to avoid over-claiming.

### Gaps
- Could not open Web3Forms/Formspree/Netlify docs directly; submission limits are from 2026 third-party comparisons and may change. Verify on each vendor's pricing page before launch.
- Netlify's current forms allowance under its credit model is reported inconsistently (100/month vs. "free and unlimited").

---

## Key Question 8 — Deployment options for the static site

### Takeaway
Cloudflare Pages is the best default (unmetered static bandwidth, 500 builds/month, 25 MiB per-file and 20,000-file hard limits — the per-file cap matters for the hero video); GitHub Pages is unsuitable because its terms exclude commercial/business sites and cap at 1 GB / 100 GB-month soft bandwidth; Vercel Hobby is non-commercial only; Netlify's free tier is now credit-based (~300 credits/month) and pauses sites on exhaustion.

### Cited Findings
- Cloudflare Pages: "maximum file size for a single Cloudflare Pages site asset is 25 MiB"; "sites can contain up to 20,000 files"; drag-and-drop direct upload capped at 1,000 files — [Cloudflare Pages limits](https://developers.cloudflare.com/pages/platform/limits); [direct upload docs](https://developers.cloudflare.com/pages/get-started/direct-upload/index.md); static asset bandwidth unmetered, 500 builds/month, Pages Functions share Workers free quota (100k req/day) — [dev.to comparison](https://dev.to/build996/vercel-vs-netlify-vs-cloudflare-pages-free-frontend-hosting-compared-3h9g); [hosting-ranked](https://hosting-ranked.pages.dev/cloudflare-pages-vs-vercel-vs-netlify)
- GitHub Pages: source repos recommended ≤ 1 GB; published sites may be no larger than 1 GB; soft bandwidth 100 GB/month; 10 builds/hour; deployments time out after 10 min; not permitted "as free hosting for online businesses, e-commerce sites, and any other website that is primarily directed at either facilitating commercial transactions or providing commercial software as a service" — [GitHub Docs: Pages limits](https://docs.github.com/pages/getting-started-with-github-pages/github-pages-limits); 100 MB is Git's push limit, not a Pages limit — [Hugo Discourse](https://discourse.gohugo.io/t/using-github-constraint/38788)
- Netlify free: credit model, ~300 credits/month, sites pause on exhaustion; estimates ~30 GB bandwidth / ~20 builds — [dev.to comparison](https://dev.to/_6638a39c349d7e9c85ee20/vercel-vs-netlify-vs-cloudflare-pages-2026-best-hosting-for-developers-22ma); [agentdeals free-tier comparison](https://agentdeals.dev/hosting-free-tier-comparison-2026)
- Vercel Hobby: 100 GB transfer, 1M edge requests, non-commercial use only — [dev.to comparison](https://dev.to/_d7eb1c1703182e3ce1782/vercel-vs-netlify-vs-cloudflare-pages-best-frontend-hosting-for-small-projects-2120)

### Inferences
- Recommended: **Cloudflare Pages** (connect the GitHub repo, build command none, output dir `/`), custom `.in` domain on Cloudflare DNS, `_headers` file for `Cache-Control: public, max-age=31536000, immutable` on `/media/*` and `/assets/*`, and `_redirects` for the thanks page. Keep every video file < 25 MiB (a 4–6 MB hero loop is far under). Because the libraries come from public CDNs, the repo itself stays tiny.
- If a paid tier is acceptable later, Netlify Pro or Vercel Pro add forms/analytics, but a form backend (Web3Forms) + Cloudflare Web Analytics (free, cookieless) covers the same needs at zero cost.
- GitHub Pages can still serve as a free **staging** preview, but not as the production host for a commercial agency site.

### Gaps
- Official pricing pages for Netlify and Vercel were not fetched; the 2026 figures are third-party and change frequently.

---

## Key Question 9 — SEO basics for an Indian local business (schema.org LocalBusiness / SecurityService JSON-LD, Google Business Profile)

### Takeaway
Use JSON-LD (Google's recommended format) with `@type: ["LocalBusiness", "ProfessionalService"]` (the existence of a schema.org `SecurityService` type could **not** be confirmed in this session — do not rely on it without checking schema.org), include NAP that exactly matches the Google Business Profile, `openingHoursSpecification`, `geo`, `areaServed`, `priceRange`, and a `hasOfferCatalog` listing each of the 13 services as `Service` items; verify the Google Business Profile (video verification is now the default for most new Indian listings) and place the pin with floor/building/landmark detail.

### Cited Findings
- Google supports JSON-LD, Microdata and RDFa; JSON-LD is recommended because it is easier to add, update and scale — [Infidigit local schema guide](https://www.infidigit.com/?p=177730)
- Standardise Name, Address, Phone and Hours so they match the Google Business Profile exactly before writing schema; create individual schema for each location — [webguruz LocalBusiness schema 2025](https://webguruz.in/blog/step-by-step-guide-to-building-local-business-schema-in-2025); [Whitespark local schema guide](https://whitespark.ca/?p=3021)
- A "Service" type with reviews can surface star ratings in snippets for Indian service businesses — [BrainGuru: schema for Indian businesses 2026](https://www.brainguru.in/blog/2026/05/schema-markup-indian-businesses-2026/)
- schema.org `LocalBusiness` = "a particular physical business or branch of an organization"; `ProfessionalService` is a confirmed LocalBusiness subtype; Google documents `EmergencyService` as a LocalBusiness extension with no unique properties — [schema.org/LocalBusiness](https://schema.org/LocalBusiness); [schema.org/ProfessionalService (attic mirror)](https://attic.schema.org/ProfessionalService); [Google EmergencyService type](https://developers.google.cn/gmail/markup/reference/types/EmergencyService?hl=es)
- Google Business Profile 2026: postcard verification largely phased out in 2025; video verification is the default for most new listings; must be one continuous, unedited take recorded in the Google Maps app showing exterior/surroundings, interior, and proof of management; avoid faces and sensitive documents; reviews typically 3–5 business days; Google has tightened checks in markets like India (virtual offices/call-centre numbers flagged); include floor/suite, building name and landmark for pin placement — [Wiremo GBP video verification checklist 2026](https://wiremo.co/blog/google-business-profile-video-verification/); [Dalton Luka GBP verification 2026](https://daltonluka.com/blog/google-my-business-verification); [BrightLocal verifying GBP](https://www.brightlocal.com/learn/verifying-google-business-profile/); [Bluehost GBP 2026 guide](https://www.bluehost.com/blog/?p=246433)
- Google's LocalBusiness structured-data documentation (required/recommended properties, example) — [Google Search Central: Local business](https://developers.google.com/search/docs/appearance/structured-data/local-business) (not fetchable from sandbox)

### Inferences — JSON-LD template
```html
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": ["LocalBusiness", "ProfessionalService"],
  "@id": "https://example.in/#business",
  "name": "North Compass Security & Consulting",
  "url": "https://example.in/",
  "logo": "https://example.in/assets/logo.png",
  "image": ["https://example.in/media/office-1x1.jpg", "https://example.in/media/office-4x3.jpg", "https://example.in/media/office-16x9.jpg"],
  "telephone": "+91-98XXXXXXXX",
  "email": "hello@example.in",
  "priceRange": "₹₹",
  "address": { "@type": "PostalAddress", "streetAddress": "2nd Floor, <Building>, <Road>", "addressLocality": "<City>",
               "addressRegion": "<State>", "postalCode": "<PIN>", "addressCountry": "IN" },
  "geo": { "@type": "GeoCoordinates", "latitude": 00.0000, "longitude": 00.0000 },
  "areaServed": [{ "@type": "City", "name": "<City>" }, { "@type": "State", "name": "<State>" }],
  "openingHoursSpecification": [{ "@type": "OpeningHoursSpecification",
    "dayOfWeek": ["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"], "opens": "09:00", "closes": "19:00" }],
  "sameAs": ["https://www.google.com/maps?cid=XXXXXXXX", "https://www.linkedin.com/company/…", "https://www.instagram.com/…"],
  "slogan": "Find your North",
  "hasOfferCatalog": { "@type": "OfferCatalog", "name": "Services", "itemListElement": [
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Security guard & agency services (PSARA-licensed)", "serviceType": "Security service" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Cybersecurity consulting" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Financial management" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "HR & recruitment consulting" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Data science" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "AI/ML solutions" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Robotics" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Resume making" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Agriculture advisory" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Banking assistance" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Investment decision advisory" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "PRC (Permanent Residence Certificate) assistance at home" } },
    { "@type": "Offer", "itemOffered": { "@type": "Service", "name": "Aptitude testing & career counselling" } }
  ]}
}
</script>
```
- Other on-page basics for a static site: one `<h1>` per page, descriptive `<title>` ≤ 60 chars with city + primary service, `<meta name="description">`, `<link rel="canonical">`, Open Graph/Twitter images (1200×630), `hreflang` only if adding Hindi/regional pages, `sitemap.xml` + `robots.txt`, `lang="en-IN"`, real HTML text for the hero headline (not canvas text) so SplitText animations keep crawlable content, and `<noscript>` content for the services grid.
- Add a `FAQPage` block for PSARA/licensing and pricing questions; add `Organization` with `foundingDate`, GST/PSARA licence numbers in visible footer text (trust signals), and `AggregateRating` only if backed by real reviews shown on-page.
- Local signals: embed the Google Maps place, keep NAP identical across site/GBP/JustDial/IndiaMART/Sulekha, and get the GBP primary category "Security guard service" with secondary categories for consulting lines.

### Gaps
- Could not confirm whether `SecurityService` exists on schema.org (schema.org blocked; searches found no such type). If it exists it would be the more specific `@type`; otherwise use `LocalBusiness` + `ProfessionalService` and `serviceType` strings as above. Validate with Google's Rich Results Test before launch.
- Google's official LocalBusiness required/recommended property list was not fetched; the template follows widely documented practice and should be validated.
- No Google primary source confirms 2026 GBP verification rules; blog sources disagree on video length (60–90 s vs 2–5 min).
