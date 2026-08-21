/* chapterScripts.js */

/**
 * Show the tab panel matching the clicked button.
 * @param {Object} evt  An object with `currentTarget` pointing at the clicked tab button.
 * @param {string} lang The panel ID to show (must match `.code-container#lang`).
 */
 function openTab(evt, lang) {
  const btn   = evt.currentTarget;
  const group = btn.closest('.tab-group');
  if (!group) return;

  group.querySelectorAll('.code-container').forEach(panel => {
    panel.classList.remove('active');
    panel.hidden = true;
  });
  group.querySelectorAll('.tablink').forEach(tab => {
    tab.classList.remove('active');
    tab.setAttribute('aria-selected', 'false');
    tab.tabIndex = -1;
  });

  const panel = Array.from(group.querySelectorAll('.code-container'))
    .find(candidate => candidate.dataset.tabLang === lang);
  if (panel) {
    panel.classList.add('active');
    panel.hidden = false;
  }
  btn.classList.add('active');
  btn.setAttribute('aria-selected', 'true');
  btn.tabIndex = 0;
}

/**
 * For each .tab-group, measure all panels, lock in their size,
 * hide them, and then “open” the default tab.
 */
function initTabGroups() {
  document.querySelectorAll('.tab-group').forEach((group, groupIndex) => {
    const panels = Array.from(group.querySelectorAll('.code-container'));
    const tabs = Array.from(group.querySelectorAll('.tablink'));

    const tabList = group.querySelector('.tabs');
    if (tabList) tabList.setAttribute('role', 'tablist');

    panels.forEach((panel, panelIndex) => {
      const originalId = panel.id || `panel-${panelIndex + 1}`;
      panel.dataset.tabLang = originalId;
      panel.id = `code-panel-${groupIndex + 1}-${panelIndex + 1}`;
      panel.setAttribute('role', 'tabpanel');
      panel.tabIndex = 0;
    });

    tabs.forEach((tab, tabIndex) => {
      const lang = tab.dataset.lang || panels[tabIndex]?.dataset.tabLang || `panel-${tabIndex + 1}`;
      const panel = panels.find(candidate => candidate.dataset.tabLang === lang) || panels[tabIndex];
      tab.dataset.lang = lang;
      tab.id = `code-tab-${groupIndex + 1}-${tabIndex + 1}`;
      tab.setAttribute('role', 'tab');
      if (panel) {
        tab.setAttribute('aria-controls', panel.id);
        panel.setAttribute('aria-labelledby', tab.id);
      }
    });

      // 1) Temporarily show them so we can measure
      panels.forEach(p => {
        p.style.display  = 'inline-block';
        p.style.width    = 'auto';
        p.style.maxWidth = 'none';
      });

    let maxH = 0, maxW = 0;
    panels.forEach(p => {
      maxH = Math.max(maxH, p.offsetHeight);
      const pre = p.querySelector('pre');
      if (pre) {
        maxW = Math.max(maxW, pre.scrollWidth + 10);
      }
    });

    // 1a) Remove the temporary inline styles so CSS can hide/show by class
    panels.forEach(p => {
      p.style.removeProperty('display');
      p.style.removeProperty('maxWidth');
    });
    
    // 1b) Then remove “active” so CSS will hide non-active panels
    panels.forEach(p => p.classList.remove('active'));

    // 2) Lock dimensions as before…
    panels.forEach(p => {
      p.style.minHeight = maxH + 'px';
      //p.style.width     = maxW + 'px';
    });

    // Wire up this group's tabs without duplicating handlers on other groups.
    tabs.forEach((btn, tabIndex) => {
      btn.addEventListener('click', evt => {
        const lang = btn.dataset.lang;
        openTab({ currentTarget: btn }, lang);
      });

      btn.addEventListener('keydown', event => {
        let nextIndex = null;
        if (event.key === 'ArrowRight' || event.key === 'ArrowDown') {
          nextIndex = (tabIndex + 1) % tabs.length;
        } else if (event.key === 'ArrowLeft' || event.key === 'ArrowUp') {
          nextIndex = (tabIndex - 1 + tabs.length) % tabs.length;
        } else if (event.key === 'Home') {
          nextIndex = 0;
        } else if (event.key === 'End') {
          nextIndex = tabs.length - 1;
        }

        if (nextIndex === null) return;
        event.preventDefault();
        const nextTab = tabs[nextIndex];
        openTab({ currentTarget: nextTab }, nextTab.dataset.lang);
        nextTab.focus();
      });
    });

    // 3) “Click” the default tab
    const btn = tabs.find(tab => tab.classList.contains('active')) || tabs[0];
    if (btn) {
      const lang = btn.dataset.lang || btn.id.split('-')[1];
      openTab({ currentTarget: btn }, lang);
    }
  });
}


function initCodeCopyButtons() {
  document.querySelectorAll('.code-container.copyable-code').forEach(container => {
    const pre = container.querySelector('pre');
    const code = container.querySelector('code');
    if (!pre || !code) return;

    const button = document.createElement('button');
    button.className = 'copy-code-button';
    button.type = 'button';
    button.textContent = 'Copy';

    button.addEventListener('click', async () => {
      try {
        await navigator.clipboard.writeText(code.innerText);
        button.textContent = 'Copied';
        setTimeout(() => {
          button.textContent = 'Copy';
        }, 1200);
      } catch (err) {
        button.textContent = 'Copy failed';
        setTimeout(() => {
          button.textContent = 'Copy';
        }, 1200);
      }
    });
    container.insertBefore(button, pre);
  });
}

function initLatexAnswerToggles() {
  document.querySelectorAll('[data-answer-target]').forEach((button) => {
    const target = document.getElementById(button.dataset.answerTarget);
    if (!target) return;
    button.addEventListener('click', () => {
      const willShow = target.hidden;
      target.hidden = !willShow;
      button.textContent = willShow ? 'Hide Answer' : 'Show Answer';
      button.setAttribute('aria-expanded', String(willShow));
      if (willShow && window.MathJax && typeof MathJax.typesetPromise === 'function') {
        MathJax.typesetPromise([target]).catch(() => {});
      }
    });
  });
}

/**
 * Hook up the “Show/Hide Answers” button.
 */
function initAnswerToggle() {
  const btn = document.getElementById('toggleAnswers');
  const ans = document.getElementById('answers');
  if (!btn || !ans) return;

  btn.addEventListener('click', () => {
    const isVisible = !ans.hidden && ans.style.display === 'block';
    ans.hidden = isVisible;
    ans.style.display = isVisible ? 'none' : 'block';
    btn.textContent = isVisible ? 'Show Answers' : 'Hide Answers';
    btn.setAttribute('aria-expanded', String(!isVisible));
  });
}

document.addEventListener('DOMContentLoaded', () => {
   document.body.classList.add('js-enabled');
   initAnswerToggle();

  // highlight.js current API
  if (window.hljs && typeof hljs.highlightAll === 'function') {
    hljs.highlightAll();
  }
  initTabGroups();
  initCodeCopyButtons();
  initLatexAnswerToggles();
});
