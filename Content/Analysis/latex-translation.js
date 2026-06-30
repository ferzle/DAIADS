document.addEventListener('DOMContentLoaded', () => {
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
});
