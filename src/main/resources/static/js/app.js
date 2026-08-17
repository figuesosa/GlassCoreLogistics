if ('serviceWorker' in navigator) {
  window.addEventListener('load', function () {
    navigator.serviceWorker.register('/sw.js').then(function () {
      if (location.pathname.indexOf('/logistica') === 0 || location.pathname === '/') {
        fetch('/api/campo/viajes', { credentials: 'same-origin' }).catch(function () {
          /* sin permiso o sin red: la caché queda como esté */
        });
      }
    }).catch(function () {
      /* sin SW no se bloquea la app */
    });
  });
}

function gcFormTitle(form, editing) {
  var box = form.closest('.card');
  var title = box ? box.querySelector('[data-form-title]') : null;
  if (!title) {
    return;
  }
  title.textContent = editing
      ? (title.getAttribute('data-edit-label') || 'Editar')
      : (title.getAttribute('data-new-label') || 'Nuevo');
}

function gcResetForm(form) {
  form.reset();
  var id = form.querySelector('[name="id"]');
  if (id) {
    id.value = '';
  }
  gcFormTitle(form, false);
  var cancel = form.querySelector('[data-reset-form]');
  if (cancel) {
    cancel.hidden = true;
  }
}

document.addEventListener('click', function (e) {
  var edit = e.target.closest('[data-edit]');
  if (edit) {
    var form = document.querySelector(edit.getAttribute('data-edit'));
    if (!form) {
      return;
    }
    Array.prototype.forEach.call(form.elements, function (el) {
      if (!el.name) {
        return;
      }
      if (el.type === 'password') {
        el.value = '';
        return;
      }
      var val = edit.getAttribute('data-' + el.name.toLowerCase());
      if (val !== null) {
        el.value = val;
      }
    });
    gcFormTitle(form, true);
    var cancel = form.querySelector('[data-reset-form]');
    if (cancel) {
      cancel.hidden = false;
    }
    form.scrollIntoView({ behavior: 'smooth', block: 'start' });
    return;
  }

  var reset = e.target.closest('[data-reset-form]');
  if (reset) {
    e.preventDefault();
    var target = document.querySelector(reset.getAttribute('data-reset-form')) || reset.closest('form');
    if (target) {
      gcResetForm(target);
    }
  }
});

document.addEventListener('submit', function (e) {
  var form = e.target.closest('form[data-confirm]');
  if (!form) {
    return;
  }
  if (!window.confirm(form.getAttribute('data-confirm'))) {
    e.preventDefault();
  }
});

document.addEventListener('input', function (e) {
  if (!e.target.matches('.table-search')) {
    return;
  }
  var q = e.target.value.trim().toLowerCase();
  var table = document.querySelector(e.target.getAttribute('data-table'));
  if (!table) {
    return;
  }
  Array.prototype.forEach.call(table.querySelectorAll('tbody tr'), function (tr) {
    tr.hidden = q.length > 0 && tr.textContent.toLowerCase().indexOf(q) === -1;
  });
});
