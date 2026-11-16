// Hero 3D scene: a wireframe compass rose, orbit rings and a particle field
// rendered with Three.js on a transparent canvas over the hero video.
// Built into assets/js/hero3d.js with `npm run build` (esbuild, tree-shaken).
import {
  WebGLRenderer, Scene, PerspectiveCamera, Group, Color,
  TorusGeometry, MeshBasicMaterial, Mesh, LineBasicMaterial, LineSegments,
  BufferGeometry, Float32BufferAttribute, Points, PointsMaterial,
  ConeGeometry, AdditiveBlending, Vector2, MathUtils, Clock, RingGeometry, DoubleSide,
  Sprite, SpriteMaterial, CanvasTexture, SRGBColorSpace,
} from 'three';

export function mountHero(canvas, opts = {}) {
  const reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  const gold = new Color(opts.gold || '#dcb560');
  const pale = new Color(opts.pale || '#f4e7c6');

  const renderer = new WebGLRenderer({ canvas, alpha: true, antialias: true, powerPreference: 'high-performance' });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 1.5));
  renderer.setClearColor(0x000000, 0);

  const scene = new Scene();
  const camera = new PerspectiveCamera(38, 1, 0.1, 100);
  camera.position.set(0, 0, 9);

  const rig = new Group();      // mouse parallax tilt
  const rose = new Group();     // compass rose + rings
  rig.add(rose);
  scene.add(rig);

  // Outer ring and tilted orbit rings
  const ringMat = new MeshBasicMaterial({ color: gold, transparent: true, opacity: 0.8 });
  const outer = new Mesh(new TorusGeometry(2.6, 0.012, 8, 160), ringMat);
  rose.add(outer);
  const orbitMat = new MeshBasicMaterial({ color: gold, transparent: true, opacity: 0.3 });
  const orbit1 = new Mesh(new TorusGeometry(3.1, 0.008, 6, 160), orbitMat);
  orbit1.rotation.x = Math.PI / 2.4;
  const orbit2 = new Mesh(new TorusGeometry(3.4, 0.006, 6, 160), orbitMat);
  orbit2.rotation.x = Math.PI / 2.4; orbit2.rotation.y = Math.PI / 3;
  rig.add(orbit1, orbit2);

  // Tick marks (360 degree dial)
  const tickPos = [];
  for (let i = 0; i < 72; i++) {
    const a = (i / 72) * Math.PI * 2;
    const len = i % 18 === 0 ? 0.32 : i % 6 === 0 ? 0.2 : 0.1;
    const r0 = 2.45, r1 = 2.45 - len;
    tickPos.push(Math.cos(a) * r0, Math.sin(a) * r0, 0, Math.cos(a) * r1, Math.sin(a) * r1, 0);
  }
  const tickGeo = new BufferGeometry();
  tickGeo.setAttribute('position', new Float32BufferAttribute(tickPos, 3));
  rose.add(new LineSegments(tickGeo, new LineBasicMaterial({ color: gold, transparent: true, opacity: 0.85 })));

  // Eight-point star (compass rose) as line art
  const starPos = [];
  const pts = 8;
  for (let i = 0; i < pts; i++) {
    const a = (i / pts) * Math.PI * 2 + Math.PI / 2;
    const major = i % 2 === 0;
    const r = major ? 1.9 : 1.1;
    const w = major ? 0.22 : 0.14;
    const tip = [Math.cos(a) * r, Math.sin(a) * r, 0];
    const l = [Math.cos(a + Math.PI / 2) * w, Math.sin(a + Math.PI / 2) * w, 0];
    const rr = [Math.cos(a - Math.PI / 2) * w, Math.sin(a - Math.PI / 2) * w, 0];
    starPos.push(...tip, ...l, ...tip, ...rr, ...l, 0, 0, 0, ...rr, 0, 0, 0);
  }
  const starGeo = new BufferGeometry();
  starGeo.setAttribute('position', new Float32BufferAttribute(starPos, 3));
  rose.add(new LineSegments(starGeo, new LineBasicMaterial({ color: pale, transparent: true, opacity: 0.9 })));


  // Outer bezel: fine ticks every 3 degrees, plus a second thin ring
  const bezelPos = [];
  for (let i = 0; i < 120; i++) {
    const a = (i / 120) * Math.PI * 2;
    const len = i % 10 === 0 ? 0.16 : 0.07;
    const r0 = 2.92, r1 = r0 - len;
    bezelPos.push(Math.cos(a) * r0, Math.sin(a) * r0, 0, Math.cos(a) * r1, Math.sin(a) * r1, 0);
  }
  const bezelGeo = new BufferGeometry();
  bezelGeo.setAttribute('position', new Float32BufferAttribute(bezelPos, 3));
  const bezel = new LineSegments(bezelGeo, new LineBasicMaterial({ color: gold, transparent: true, opacity: 0.55 }));
  rose.add(bezel);
  rose.add(new Mesh(new TorusGeometry(2.98, 0.006, 6, 200), new MeshBasicMaterial({ color: gold, transparent: true, opacity: 0.45 })));
  rose.add(new Mesh(new TorusGeometry(2.02, 0.006, 6, 160), new MeshBasicMaterial({ color: pale, transparent: true, opacity: 0.35 })));

  // Cardinal letters as small serif sprites
  function letter(ch, size) {
    const c = document.createElement('canvas'); c.width = c.height = 128;
    const g = c.getContext('2d');
    g.fillStyle = ch === 'N' ? '#f4e7c6' : '#dcb560';
    g.font = '500 96px "Source Serif 4", Georgia, serif';
    g.textAlign = 'center'; g.textBaseline = 'middle';
    g.fillText(ch, 64, 70);
    const tex = new CanvasTexture(c); tex.colorSpace = SRGBColorSpace;
    const sp = new Sprite(new SpriteMaterial({ map: tex, transparent: true, depthWrite: false }));
    sp.scale.set(size, size, 1);
    return sp;
  }
  [['N', Math.PI / 2, 0.42], ['E', 0, 0.3], ['S', -Math.PI / 2, 0.3], ['W', Math.PI, 0.3]].forEach(([ch, a, sz]) => {
    const sp = letter(ch, sz);
    sp.position.set(Math.cos(a) * 2.48, Math.sin(a) * 2.48, 0.01);
    rose.add(sp);
  });

  // Needle: north half gold, south half dim
  const needle = new Group();
  const nMat = new MeshBasicMaterial({ color: gold });
  const sMat = new MeshBasicMaterial({ color: 0x8aa0c0, transparent: true, opacity: 0.8 });
  const north = new Mesh(new ConeGeometry(0.11, 1.75, 4), nMat);
  north.position.y = 0.875;
  const south = new Mesh(new ConeGeometry(0.11, 1.75, 4), sMat);
  south.position.y = -0.875; south.rotation.z = Math.PI;
  needle.add(north, south);
  const hub = new Mesh(new RingGeometry(0.08, 0.16, 32), new MeshBasicMaterial({ color: pale, side: DoubleSide }));
  needle.add(hub);
  rose.add(needle);

  // Particle field
  const COUNT = reduced ? 300 : 1400;
  const pPos = new Float32Array(COUNT * 3);
  const pSeed = new Float32Array(COUNT);
  for (let i = 0; i < COUNT; i++) {
    const r = 2.2 + Math.random() * 6.5;
    const a = Math.random() * Math.PI * 2;
    pPos[i * 3] = Math.cos(a) * r;
    pPos[i * 3 + 1] = (Math.random() - 0.5) * 7;
    pPos[i * 3 + 2] = Math.sin(a) * r - 1.5;
    pSeed[i] = Math.random() * Math.PI * 2;
  }
  const pGeo = new BufferGeometry();
  pGeo.setAttribute('position', new Float32BufferAttribute(pPos, 3));
  const particles = new Points(pGeo, new PointsMaterial({
    color: pale, size: 0.035, transparent: true, opacity: 0.7, blending: AdditiveBlending, depthWrite: false, sizeAttenuation: true,
  }));
  scene.add(particles);

  // Interaction state
  const mouse = new Vector2(0, 0);
  const target = new Vector2(0, 0);
  let needleTarget = 0;      // radians, 0 = north
  let idleSince = performance.now();
  let scroll = 0;            // 0..1 hero scroll progress
  let visible = true;
  let raf = 0;
  const clock = new Clock();

  function onPointer(e) {
    const x = (e.clientX / window.innerWidth) * 2 - 1;
    const y = -(e.clientY / window.innerHeight) * 2 + 1;
    target.set(x, y);
    needleTarget = Math.atan2(-x, y); // needle points toward the cursor
    idleSince = performance.now();
  }
  if (!reduced) window.addEventListener('pointermove', onPointer, { passive: true });

  function resize() {
    const w = canvas.clientWidth || window.innerWidth;
    const h = canvas.clientHeight || window.innerHeight;
    renderer.setSize(w, h, false);
    camera.aspect = w / h;
    camera.updateProjectionMatrix();
    const s = w < 720 ? 0.82 : w < 1100 ? 1 : 1.12;
    scene.scale.setScalar(s);
    rig.position.x = 0; // the dial is the centrepiece, centred behind the headline
  }
  resize();
  window.addEventListener('resize', resize);

  const io = new IntersectionObserver(([en]) => {
    visible = en.isIntersecting;
    if (visible && !raf) loop();
  }, { threshold: 0.02 });
  io.observe(canvas);

  function render(dt, t) {
    mouse.lerp(target, 0.06);
    rig.rotation.y = mouse.x * 0.35 + Math.sin(t * 0.15) * 0.08;
    rig.rotation.x = -mouse.y * 0.25 + Math.cos(t * 0.12) * 0.05;
    rose.rotation.z = -scroll * 1.2;
    rig.position.y = -scroll * 2.2 + (window.innerWidth >= 720 ? -0.32 : 0.25);
    rig.scale.setScalar(1 - scroll * 0.25);
    outer.rotation.z += dt * 0.05;
    bezel.rotation.z -= dt * 0.03;
    orbit1.rotation.z += dt * 0.12;
    orbit2.rotation.z -= dt * 0.09;

    // Needle: follow cursor, settle to north when idle ("find your north")
    const idle = performance.now() - idleSince > 1800;
    const goal = idle ? 0 : needleTarget;
    let d = goal - needle.rotation.z;
    d = Math.atan2(Math.sin(d), Math.cos(d));
    needle.rotation.z += d * (idle ? 0.035 : 0.08) + (idle ? Math.sin(t * 2.2) * 0.0015 : 0);

    // Particle drift
    const pos = pGeo.attributes.position.array;
    for (let i = 0; i < COUNT; i++) {
      pos[i * 3 + 1] += Math.sin(t * 0.4 + pSeed[i]) * 0.0012;
      const x = pos[i * 3], z = pos[i * 3 + 2] + 1.5;
      const a = Math.atan2(z, x) + dt * 0.02;
      const r = Math.hypot(x, z);
      pos[i * 3] = Math.cos(a) * r;
      pos[i * 3 + 2] = Math.sin(a) * r - 1.5;
    }
    pGeo.attributes.position.needsUpdate = true;
    particles.material.opacity = MathUtils.lerp(0.7, 0.15, scroll);

    renderer.render(scene, camera);
  }

  function loop() {
    if (!visible || document.hidden) { raf = 0; return; }
    const dt = Math.min(clock.getDelta(), 0.05);
    render(dt, clock.elapsedTime);
    raf = requestAnimationFrame(loop);
  }
  document.addEventListener('visibilitychange', () => { if (!document.hidden && !raf) loop(); });

  if (reduced) {
    render(0, 0);
  } else {
    loop();
  }

  return {
    setScroll(p) { scroll = MathUtils.clamp(p, 0, 1); if (reduced) render(0, 0); },
    destroy() { cancelAnimationFrame(raf); io.disconnect(); renderer.dispose(); },
  };
}

// Auto-mount
const el = document.getElementById('hero-canvas');
if (el) {
  const api = mountHero(el);
  window.__heroScene = api;
  el.dataset.ready = '1';
}
