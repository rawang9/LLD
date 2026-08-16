/** Renders the embedded #notes-source text → HTML without altering source text (structure + escape only). */

function escapeHtml(s) {
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function slugifyHeading(text) {
  return text
    .toLowerCase()
    .replace(/[^\w\s-]/g, '')
    .replace(/\s+/g, '-')
    .replace(/-+/g, '-')
    .slice(0, 64);
}

function inlineFormat(text) {
  let s = escapeHtml(text);
  s = s.replace(/`([^`]+)`/g, '<code class="notes-inline-code">$1</code>');
  s = s.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
  return s;
}

function notesTextToHtml(text) {
  const lines = text.split('\n');
  const out = [];
  let inCode = false;
  let codeLines = [];
  let inUl = false;
  let paraBuf = [];
  const headings = [];

  function flushPara() {
    if (!paraBuf.length) return;
    out.push(`<p>${inlineFormat(paraBuf.join(' '))}</p>`);
    paraBuf = [];
  }

  function closeUl() {
    if (inUl) {
      out.push('</ul>');
      inUl = false;
    }
  }

  function flushCode() {
    if (!codeLines.length) return;
    out.push(`<pre class="notes-code"><code>${escapeHtml(codeLines.join('\n'))}</code></pre>`);
    codeLines = [];
  }

  function pushHeading(level, raw) {
    flushPara();
    closeUl();
    const clean = raw.replace(/^#+\s*/, '').trim();
    const id = slugifyHeading(clean);
    headings.push({ id, text: clean, level });
    const tag = level === 1 ? 'h2' : level === 2 ? 'h3' : 'h4';
    const cls = level === 1 ? 'notes-h2' : level === 2 ? 'notes-h3' : 'notes-h4';
    out.push(`<${tag} id="${id}" class="${cls}">${inlineFormat(clean)}</${tag}>`);
  }

  for (let i = 0; i < lines.length; i += 1) {
    const line = lines[i];

    if (line.startsWith('```')) {
      if (!inCode) {
        flushPara();
        closeUl();
        inCode = true;
        codeLines = [];
      } else {
        inCode = false;
        flushCode();
      }
      continue;
    }

    if (inCode) {
      codeLines.push(line);
      continue;
    }

    if (/^---+\s*$/.test(line)) {
      flushPara();
      closeUl();
      out.push('<hr class="notes-rule" />');
      continue;
    }

    if (/^#\s/.test(line) && !/^##\s/.test(line)) {
      pushHeading(1, line);
      continue;
    }
    if (/^##\s/.test(line) && !/^###\s/.test(line)) {
      pushHeading(2, line);
      continue;
    }
    if (/^###\s/.test(line)) {
      pushHeading(3, line);
      continue;
    }

    if (/^>\s?/.test(line)) {
      flushPara();
      closeUl();
      out.push(`<blockquote class="notes-quote">${inlineFormat(line.replace(/^>\s?/, ''))}</blockquote>`);
      continue;
    }

    if (/^[\*\-]\s/.test(line)) {
      flushPara();
      if (!inUl) {
        out.push('<ul class="notes-list">');
        inUl = true;
      }
      out.push(`<li>${inlineFormat(line.replace(/^[\*\-]\s/, ''))}</li>`);
      continue;
    }

    if (line.trim() === '') {
      flushPara();
      closeUl();
      continue;
    }

    closeUl();
    paraBuf.push(line);
  }

  flushPara();
  closeUl();
  if (inCode) flushCode();

  return { html: out.join('\n'), headings: headings.filter((h) => h.level === 1) };
}

function buildNotesToc(headings) {
  if (!headings.length) return '';
  const items = headings
    .map((h) => `<li><a href="#${h.id}">${escapeHtml(h.text)}</a></li>`)
    .join('');
  return `<nav class="notes-toc" aria-label="Notes sections"><p class="notes-toc-label">In this article</p><ol>${items}</ol></nav>`;
}

function loadNotesSection() {
  const contentEl = document.getElementById('notes-content');
  const tocEl = document.getElementById('notes-toc');
  const sourceEl = document.getElementById('notes-source');
  if (!contentEl) return;

  try {
    if (!sourceEl) throw new Error('#notes-source not found');
    const { html, headings } = notesTextToHtml(sourceEl.textContent);
    contentEl.className = 'notes-article';
    contentEl.innerHTML = html;
    if (tocEl) tocEl.innerHTML = buildNotesToc(headings);
  } catch (err) {
    contentEl.className = 'notes-article notes-error';
    contentEl.textContent = 'Could not render notes.';
    console.warn('Notes render failed:', err);
  }
}

function initDropboxBlast() {
  const stage = document.getElementById('dropbox-stage');
  const btn = document.getElementById('dropbox-blast-btn');
  const modules = stage?.querySelector('.dropbox-modules');
  if (!stage || !btn) return;

  btn.addEventListener('click', () => {
    const open = stage.classList.toggle('dropbox-exploded');
    btn.setAttribute('aria-expanded', open ? 'true' : 'false');
    if (modules) modules.setAttribute('aria-hidden', open ? 'false' : 'true');
  });
}
