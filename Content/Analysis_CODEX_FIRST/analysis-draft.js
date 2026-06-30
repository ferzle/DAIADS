document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('[data-answer-target]').forEach((button) => {
    const targetId = button.getAttribute('data-answer-target');
    const target = document.getElementById(targetId);
    if (!target) return;

    button.setAttribute('aria-controls', targetId);
    button.setAttribute('aria-expanded', 'false');

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
});
