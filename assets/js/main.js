/* KIDO Security & Consulting — site behaviour
   Depends on gsap, ScrollTrigger and Lenis loaded before this file (deferred). */
(function () {
  'use strict';
  const reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  const finePointer = window.matchMedia('(hover: hover) and (pointer: fine)').matches;
  const isMobile = window.innerWidth < 768;
  const conn = navigator.connection || {};
  const saveData = !!conn.saveData || /2g/.test(conn.effectiveType || '');
  document.documentElement.classList.remove('no-js');
  if (CSS.supports('animation-timeline: view()') && !window.gsap) document.documentElement.classList.add('css-sda');

  /* ---------- Smooth scroll ---------- */
  let lenis = null;
  if (window.Lenis && !reduced) {
    lenis = new Lenis({ lerp: 0.09, smoothWheel: true });
    if (window.gsap && window.ScrollTrigger) {
      lenis.on('scroll', ScrollTrigger.update);
      gsap.ticker.add((t) => lenis.raf(t * 1000));
      gsap.ticker.lagSmoothing(0);
    } else {
      const raf = (t) => { lenis.raf(t); requestAnimationFrame(raf); };
      requestAnimationFrame(raf);
    }
  }
  // Anchor links through Lenis
  document.querySelectorAll('a[href^="#"]').forEach((a) => {
    a.addEventListener('click', (e) => {
      const id = a.getAttribute('href');
      if (id.length < 2) return;
      const el = document.querySelector(id);
      if (!el) return;
      e.preventDefault();
      closeMenu();
      if (lenis) lenis.scrollTo(el, { offset: -70, duration: 1.4 });
      else el.scrollIntoView({ behavior: reduced ? 'auto' : 'smooth' });
      history.replaceState(null, '', id);
    });
  });

  /* ---------- Nav ---------- */
  const nav = document.getElementById('nav');
  const toggle = document.getElementById('navToggle');
  const menu = document.getElementById('mobileMenu');
  const onScroll = () => nav.classList.toggle('is-scrolled', window.scrollY > 30);
  window.addEventListener('scroll', onScroll, { passive: true });
  onScroll();
  function closeMenu() {
    if (!menu || menu.hidden) return;
    menu.hidden = true;
    toggle.setAttribute('aria-expanded', 'false');
    toggle.setAttribute('aria-label', 'Open menu');
    if (lenis) lenis.start();
  }
  toggle && toggle.addEventListener('click', () => {
    const open = menu.hidden;
    menu.hidden = !open;
    toggle.setAttribute('aria-expanded', String(open));
    toggle.setAttribute('aria-label', open ? 'Close menu' : 'Open menu');
    if (lenis) open ? lenis.stop() : lenis.start();
  });
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeMenu(); });
  document.getElementById('year').textContent = new Date().getFullYear();

  /* ---------- Background videos: choose source, lazy load, pause off-screen ---------- */
  const canPlayWebm = !!document.createElement('video').canPlayType('video/webm; codecs="vp9"');
  function attachSources(v) {
    if (v.dataset.loaded) return;
    const small = isMobile || saveData;
    const webm = small ? v.dataset.webmM : v.dataset.webm;
    const mp4 = small ? v.dataset.mp4M : v.dataset.mp4;
    if (canPlayWebm && webm) { const s = document.createElement('source'); s.src = webm; s.type = 'video/webm'; v.appendChild(s); }
    if (mp4) { const s = document.createElement('source'); s.src = mp4; s.type = 'video/mp4'; v.appendChild(s); }
    v.dataset.loaded = '1';
    v.load();
  }
  const videos = Array.from(document.querySelectorAll('.bg-video'));
  const hero = document.getElementById('heroVideo');
  const heroPause = document.getElementById('heroPause');
  let userPaused = false;
  if (reduced || saveData) {
    // Poster only. Keep the control so users can opt in.
    if (heroPause) { heroPause.setAttribute('aria-pressed', 'true'); heroPause.setAttribute('aria-label', 'Play background video'); userPaused = true; }
  } else if (hero) {
    attachSources(hero);
    hero.play().catch(() => {});
  }
  const vio = new IntersectionObserver((entries) => {
    entries.forEach((en) => {
      const v = en.target;
      if (en.isIntersecting) {
        if (!(reduced || saveData)) { attachSources(v); if (!(v === hero && userPaused)) v.play().catch(() => {}); }
      } else if (!v.paused) { v.pause(); }
    });
  }, { rootMargin: '200px 0px' });
  videos.forEach((v) => vio.observe(v));
  heroPause && heroPause.addEventListener('click', () => {
    userPaused = !userPaused;
    heroPause.setAttribute('aria-pressed', String(userPaused));
    heroPause.setAttribute('aria-label', userPaused ? 'Play background video' : 'Pause background video');
    if (userPaused) hero.pause(); else { attachSources(hero); hero.play().catch(() => {}); }
  });

  /* ---------- Custom cursor ---------- */
  const cursor = document.querySelector('.cursor');
  if (cursor && finePointer && !reduced) {
    const dot = cursor.querySelector('.cursor__dot'), ring = cursor.querySelector('.cursor__ring');
    let x = 0, y = 0, rx = 0, ry = 0;
    window.addEventListener('pointermove', (e) => { x = e.clientX; y = e.clientY; dot.style.transform = `translate(${x}px,${y}px) translate(-50%,-50%)`; }, { passive: true });
    (function tick() { rx += (x - rx) * 0.18; ry += (y - ry) * 0.18; ring.style.transform = `translate(${rx}px,${ry}px) translate(-50%,-50%)`; requestAnimationFrame(tick); })();
    document.querySelectorAll('a, button, .card, .tilt').forEach((el) => {
      el.addEventListener('pointerenter', () => document.body.classList.add('is-hovering'));
      el.addEventListener('pointerleave', () => document.body.classList.remove('is-hovering'));
    });
  }

  /* ---------- Magnetic buttons ---------- */
  if (finePointer && !reduced && window.gsap) {
    document.querySelectorAll('.magnetic').forEach((el) => {
      const strength = 0.35;
      el.addEventListener('pointermove', (e) => {
        const r = el.getBoundingClientRect();
        const dx = e.clientX - (r.left + r.width / 2), dy = e.clientY - (r.top + r.height / 2);
        gsap.to(el, { x: dx * strength, y: dy * strength, duration: 0.4, ease: 'power3.out' });
      });
      el.addEventListener('pointerleave', () => gsap.to(el, { x: 0, y: 0, duration: 0.7, ease: 'elastic.out(1,0.4)' }));
    });
  }

  /* ---------- 3D tilt cards ---------- */
  if (finePointer && !reduced) {
    document.querySelectorAll('.tilt').forEach((el) => {
      let raf = 0;
      el.addEventListener('pointermove', (e) => {
        cancelAnimationFrame(raf);
        raf = requestAnimationFrame(() => {
          const r = el.getBoundingClientRect();
          const px = (e.clientX - r.left) / r.width - 0.5, py = (e.clientY - r.top) / r.height - 0.5;
          el.style.transform = `perspective(900px) rotateX(${(-py * 7).toFixed(2)}deg) rotateY(${(px * 9).toFixed(2)}deg) translateZ(0)`;
          el.style.setProperty('--gx', `${(px + 0.5) * 100}%`);
          el.style.setProperty('--gy', `${(py + 0.5) * 100}%`);
        });
      });
      el.addEventListener('pointerleave', () => { cancelAnimationFrame(raf); el.style.transform = ''; });
    });
  }

  /* ---------- Scroll animations (GSAP) ---------- */
  if (window.gsap && window.ScrollTrigger && !reduced) {
    gsap.registerPlugin(ScrollTrigger);

    // Hero title words
    gsap.to('.hero__title .word', { y: 0, duration: 1.1, ease: 'power4.out', stagger: 0.12, delay: 0.2 });
    gsap.fromTo('.hero .reveal', { opacity: 0, y: 24 }, { opacity: 1, y: 0, duration: 0.9, ease: 'power3.out', stagger: 0.1, delay: 0.7, onComplete() { document.querySelectorAll('.hero .reveal').forEach((el) => el.classList.add('is-revealed')); } });

    // Hero scroll progress → 3D scene + parallax
    ScrollTrigger.create({
      trigger: '.hero', start: 'top top', end: 'bottom top', scrub: true,
      onUpdate(self) {
        if (window.__heroScene) window.__heroScene.setScroll(self.progress);
        gsap.set('.hero__content', { y: self.progress * 120, opacity: 1 - self.progress * 1.1 });
        gsap.set('.hero__media .bg-video', { scale: 1 + self.progress * 0.12, y: self.progress * 60 });
      },
    });

    // Split headings into characters and reveal
    document.querySelectorAll('.h2.split').forEach((h) => {
      const text = h.textContent;
      h.setAttribute('aria-label', text);
      h.innerHTML = text.split(' ').map((w) => `<span class="word" style="display:inline-block;white-space:nowrap">${[...w].map((c) => `<span class="char" aria-hidden="true">${c}</span>`).join('')}</span>`).join(' ');
      gsap.from(h.querySelectorAll('.char'), {
        scrollTrigger: { trigger: h, start: 'top 85%' },
        yPercent: 100, opacity: 0, rotateX: -40, transformOrigin: '50% 100%', duration: 0.8, ease: 'power4.out', stagger: 0.015,
      });
    });

    // Generic reveal
    document.querySelectorAll('.section .reveal, .marquee .reveal').forEach((el) => {
      gsap.to(el, {
        scrollTrigger: { trigger: el, start: 'top 90%', once: true },
        opacity: 1, y: 0, duration: 0.9, ease: 'power3.out', onComplete() { el.classList.add('is-revealed'); },
      });
    });

    // Section background parallax
    document.querySelectorAll('.section__bg .bg-video').forEach((v) => {
      gsap.fromTo(v, { yPercent: -8 }, { yPercent: 8, ease: 'none', scrollTrigger: { trigger: v.closest('.section'), start: 'top bottom', end: 'bottom top', scrub: true } });
    });

    // Counters
    document.querySelectorAll('[data-count]').forEach((el) => {
      const target = +el.dataset.count;
      ScrollTrigger.create({
        trigger: el, start: 'top 85%', once: true,
        onEnter() { gsap.fromTo({ v: 0 }, { v: 0 }, { v: target, duration: 1.6, ease: 'power2.out', onUpdate() { el.textContent = Math.round(this.targets()[0].v); } }); },
      });
    });
  } else {
    // No GSAP or reduced motion: show everything.
    document.querySelectorAll('.reveal').forEach((el) => el.classList.add('is-revealed'));
    document.querySelectorAll('.hero__title .word').forEach((el) => { el.style.transform = 'none'; });
    document.querySelectorAll('[data-count]').forEach((el) => { el.textContent = el.dataset.count; });
  }

  /* ---------- Directions filter ---------- */
  const chips = document.querySelectorAll('.filters .chip');
  const cards = document.querySelectorAll('#cards .card');
  chips.forEach((chip) => chip.addEventListener('click', () => {
    chips.forEach((c) => { c.classList.toggle('is-active', c === chip); c.setAttribute('aria-selected', String(c === chip)); });
    const f = chip.dataset.filter;
    cards.forEach((card) => {
      const show = f === 'all' || (card.dataset.cat || '').split(' ').includes(f);
      card.classList.toggle('is-hidden', !show);
    });
    if (window.ScrollTrigger) ScrollTrigger.refresh();
  }));

  /* ---------- Aptitude sample test ---------- */
  const QUESTIONS = [
    { cat: 'Numerical', q: 'A guard post is manned 24 hours a day in 8-hour shifts. How many guard-shifts are needed for 7 days?', opts: ['14', '21', '24', '28'], a: 1, dir: 'Financial Management' },
    { cat: 'Logical', q: 'All supervisors are trained. Some trained people are ex-servicemen. Which statement must be true?', opts: ['All supervisors are ex-servicemen', 'Some ex-servicemen are supervisors', 'All trained people are supervisors', 'None of these must be true'], a: 3, dir: 'Cybersecurity' },
    { cat: 'Numerical', q: 'An investment grows 10% in year one and falls 10% in year two. Compared with the start, it is now:', opts: ['Unchanged', '1% lower', '1% higher', '2% lower'], a: 1, dir: 'Investment Decisions' },
    { cat: 'Spatial', q: 'A compass needle pointing North is rotated 270° clockwise. Where does it point now?', opts: ['East', 'South', 'West', 'North'], a: 2, dir: 'Robotics' },
    { cat: 'Verbal', q: 'Choose the word closest in meaning to VIGILANT.', opts: ['Careless', 'Watchful', 'Hostile', 'Tired'], a: 1, dir: 'Human Resources' },
    { cat: 'Logical', q: 'Which number comes next: 2, 6, 12, 20, 30, ?', opts: ['36', '40', '42', '44'], a: 2, dir: 'Data Science' },
    { cat: 'Numerical', q: 'A field yields 18 quintals per acre. After a new practice the yield is 22.5 quintals. The increase is:', opts: ['20%', '22.5%', '25%', '30%'], a: 2, dir: 'Agriculture' },
    { cat: 'Verbal', q: 'Pick the odd one out.', opts: ['Ledger', 'Invoice', 'Receipt', 'Drone'], a: 3, dir: 'AI / ML' },
  ];
  const quiz = {
    i: 0, score: 0, hits: {}, timer: null, left: 90,
    els: {
      intro: document.getElementById('quizIntro'), stage: document.getElementById('quizStage'), result: document.getElementById('quizResult'),
      prog: document.getElementById('quizProgress'), counter: document.getElementById('quizCounter'), timer: document.getElementById('quizTimer'),
      cat: document.getElementById('quizCat'), q: document.getElementById('quizQ'), opts: document.getElementById('quizOpts'),
      score: document.getElementById('quizScore'), verdict: document.getElementById('quizVerdict'), north: document.getElementById('quizNorth'),
    },
    start() {
      this.i = 0; this.score = 0; this.hits = {}; this.left = 90;
      this.els.intro.hidden = true; this.els.result.hidden = true; this.els.stage.hidden = false;
      clearInterval(this.timer);
      this.timer = setInterval(() => { this.left--; this.els.timer.textContent = this.left + 's'; if (this.left <= 0) this.finish(); }, 1000);
      this.render();
    },
    render() {
      const item = QUESTIONS[this.i];
      this.els.prog.style.width = ((this.i) / QUESTIONS.length * 100) + '%';
      this.els.counter.textContent = `${this.i + 1} / ${QUESTIONS.length}`;
      this.els.cat.textContent = item.cat;
      this.els.q.textContent = item.q;
      this.els.opts.innerHTML = '';
      item.opts.forEach((o, idx) => {
        const b = document.createElement('button'); b.type = 'button'; b.textContent = o;
        b.addEventListener('click', () => this.answer(idx, b));
        this.els.opts.appendChild(b);
      });
      if (window.gsap && !reduced) gsap.from(this.els.opts.children, { opacity: 0, y: 12, stagger: 0.06, duration: 0.4, ease: 'power2.out' });
    },
    answer(idx, btn) {
      const item = QUESTIONS[this.i];
      Array.from(this.els.opts.children).forEach((b) => (b.disabled = true));
      if (idx === item.a) { this.score++; this.hits[item.dir] = (this.hits[item.dir] || 0) + 1; btn.classList.add('is-right'); }
      else { btn.classList.add('is-wrong'); this.els.opts.children[item.a].classList.add('is-right'); }
      setTimeout(() => { this.i++; this.i < QUESTIONS.length ? this.render() : this.finish(); }, 650);
    },
    finish() {
      clearInterval(this.timer);
      this.els.stage.hidden = true; this.els.result.hidden = false;
      this.els.prog.style.width = '100%';
      this.els.score.textContent = this.score;
      const pct = this.score / QUESTIONS.length;
      this.els.verdict.textContent = pct >= 0.85 ? 'Excellent. Sharp reasoning under time pressure, the kind we look for in supervisors and analysts.'
        : pct >= 0.6 ? 'Strong. Solid fundamentals with room to specialise; a full assessment will show where.'
        : pct >= 0.35 ? 'Developing. The full assessment maps your strengths in detail and suggests a learning path.'
        : 'A starting point. Everyone has a North; our counsellors help you find yours.';
      const best = Object.entries(this.hits).sort((a, b) => b[1] - a[1])[0];
      this.els.north.textContent = best ? best[0] : 'Aptitude & Career Counselling';
      if (window.ScrollTrigger) ScrollTrigger.refresh();
    },
  };
  document.getElementById('quizStart') && document.getElementById('quizStart').addEventListener('click', () => quiz.start());
  document.getElementById('quizRetry') && document.getElementById('quizRetry').addEventListener('click', () => quiz.start());

  /* ---------- Quote form ---------- */
  const form = document.getElementById('quoteForm');
  const note = document.getElementById('formNote');
  if (form) {
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      if (!form.reportValidity()) return;
      const key = form.querySelector('[name="access_key"]').value;
      const data = new FormData(form);
      if (!key || key === 'YOUR_WEB3FORMS_ACCESS_KEY') {
        // No form backend configured yet: fall back to a pre-filled email.
        const body = ['Name: ' + data.get('name'), 'Phone: ' + data.get('phone'), 'Email: ' + data.get('email'), 'Organisation: ' + data.get('organisation'), 'Service: ' + data.get('service'), '', data.get('message')].join('\n');
        window.location.href = 'mailto:hello@example.com?subject=' + encodeURIComponent('Quote request: ' + data.get('service')) + '&body=' + encodeURIComponent(body);
        note.textContent = 'Opening your email app with the request pre-filled.';
        return;
      }
      note.textContent = 'Sending…';
      try {
        const res = await fetch(form.action, { method: 'POST', body: data, headers: { Accept: 'application/json' } });
        const json = await res.json();
        if (json.success) { note.textContent = 'Received. We will call you within 24 hours.'; form.reset(); }
        else throw new Error(json.message || 'Failed');
      } catch (err) {
        note.textContent = 'Could not send right now. Please call or WhatsApp us.';
      }
    });
  }
})();
