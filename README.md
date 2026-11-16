# KIDO Security & Consulting — website

Static marketing site for a Haryana (India) PSARA-licensed private security agency that also offers thirteen consulting
"directions", led by a retired Indian Air Force Squadron Leader. Tagline: **Find Your North.**

## What is in here

| Path | Purpose |
| --- | --- |
| `index.html` | Single-page site: hero, security bento, 13 directions, founder, compliance & licence, aptitude sample test, sectors, contact, footer |
| `assets/css/styles.css` | Design tokens (navy + gold), layout, reduced-motion rules |
| `assets/js/main.js` | Lenis smooth scroll, GSAP ScrollTrigger 3D entrances scrubbed by scroll, scroll-velocity skew, word-by-word lead reveals, global particle field with parallax, compass scroll indicator and progress bar, lazy background video, tilt cards, magnetic buttons, custom cursor, filters, counters, quiz, form |
| `src/hero3d.js` → `assets/js/hero3d.js` | Three.js compass rose + particle field over the hero video (built with esbuild, see below) |
| `assets/video/*` | Twenty-four looping clips generated with Higgsfield (Seedance 2.5): six section backgrounds, six security-service cards and one animated clip per direction card (image-to-video from the service renders). All crossfade-looped and encoded to WebM/MP4 at desktop and mobile sizes, with poster frames |
| `assets/img/*` | Thirteen service-card renders and the social preview image, generated with Higgsfield (GPT Image 2.6), converted to WebP |
| `assets/vendor/*` | Pinned GSAP 3.15.1, ScrollTrigger, Lenis 1.3.27 (self-hosted, no CDN dependency) |
| `research_notes/`, `reports/` | Deep-research notes and the synthesized report behind the design, Higgsfield workflow and PSARA compliance copy |

## Run locally

```bash
python3 -m http.server 8080
```

Then open <http://localhost:8080>.

## Rebuild the 3D hero bundle

```bash
npm install
npm run build
```

## Before going live: replace the placeholders

Search `index.html` for square-bracket placeholders and the `example.com` links:

- Company name (currently **KIDO**) and the `og:`/canonical URLs
- `Sqn Ldr [Founder Name] (Retd)`, service years and specialisation
- PSARA licence number, validity dates, area of operation and registered office (copy them exactly from the licence)
- Phone, WhatsApp, email and address (in the contact section, footer and JSON-LD)
- `YOUR_WEB3FORMS_ACCESS_KEY` in the quote form (free key from web3forms.com). Until it is set, the form opens a pre-filled email instead
- Founder photo: drop an image into `.founder__photo`
- "PRC at Home" copy: confirm what PRC stands for in your practice and expand it on first use

## Deploy

Any static host works: Cloudflare Pages, Netlify, Vercel or your own server. Upload the repository root (without `node_modules`, `src`, `research_notes`, `reports`).
The site has no build step at runtime.

## Regenerating motion assets with Higgsfield

Section backgrounds were generated as 8-second, 1080p, 16:9 silent text-to-video with Seedance 2.5. Service and direction
cards use 5-second 720p clips (the direction cards animate the existing service renders via image-to-video with the render as
`start_image`). Every clip is made seamless with an ffmpeg crossfade (last second blended into the first). Encode recipe per clip:

```bash
ffmpeg -i in.mp4 -filter_complex "[0:v]trim=1:8,setpts=PTS-STARTPTS[a];[0:v]trim=0:1,setpts=PTS-STARTPTS[b];[a][b]xfade=transition=fade:duration=1:offset=6[v]" -map "[v]" -an -c:v libx264 -crf 14 loop.mp4
```

```bash
ffmpeg -i loop.mp4 -an -vf scale=1440:-2 -c:v libvpx-vp9 -b:v 0 -crf 34 -row-mt 1 hero.webm
```

```bash
ffmpeg -i loop.mp4 -an -vf scale=1440:-2 -c:v libx264 -preset slow -crf 27 -pix_fmt yuv420p -movflags +faststart hero.mp4
```
