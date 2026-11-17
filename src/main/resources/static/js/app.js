$(function () {
    const cfg = window.LIBRARY || {};
    const t = cfg.i18n || {};
    function esc(value) {
        return String(value ?? '')
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
    }

    const money = new Intl.NumberFormat(cfg.lang === 'en' ? 'en-US' : 'es-MX',
                                        { style: 'currency', currency: 'MXN' });
    const AVATAR_COLORS = ['#16357a', '#8a5a00', '#1f6f5c', '#7a2e5c', '#4b3f8f', '#9a3b22', '#2f5f8a'];

    function avatar(name) {
        const initials = String(name).split(/\s+/).filter(Boolean)
            .slice(0, 2).map(p => p[0].toUpperCase()).join('');
        let hash = 0;
        for (const ch of String(name)) { hash = (hash * 31 + ch.charCodeAt(0)) >>> 0; }
        const color = AVATAR_COLORS[hash % AVATAR_COLORS.length];
        return `<span class="avatar" style="--avatar:${color}">${esc(initials)}</span>`;
    }
    const LANG_ES = {
        processing: 'Cargando...',
        lengthMenu: 'Mostrar _MENU_',
        zeroRecords: 'No hay libros que coincidan',
        emptyTable: 'No hay libros registrados',
        info: 'Mostrando _START_ a _END_ de _TOTAL_ libros',
        infoEmpty: 'Mostrando 0 libros',
        infoFiltered: '(filtrado de _MAX_)',
        search: 'Buscar:',
        paginate: { first: '«', last: '»', next: '›', previous: '‹' },
        select: { rows: { _: '%d libros seleccionados', 0: '', 1: '1 libro seleccionado' } },
        columnControl: {
            orderAsc: 'Ordenar ascendente',
            orderDesc: 'Ordenar descendente',
            orderClear: 'Quitar orden',
            searchClear: 'Limpiar búsqueda',
            searchDropdown: 'Buscar',
            searchList: 'Filtrar',
            list: { all: 'Todos', none: 'Ninguno', search: 'Buscar...', empty: 'Sin opciones' },
            search: {
                text: {
                    contains: 'Contiene', notContains: 'No contiene', equal: 'Igual a',
                    notEqual: 'Distinto de', starts: 'Empieza con', ends: 'Termina con',
                    empty: 'Vacío', notEmpty: 'No vacío'
                },
                number: {
                    equal: 'Igual a', notEqual: 'Distinto de', greater: 'Mayor que',
                    greaterOrEqual: 'Mayor o igual que', less: 'Menor que',
                    lessOrEqual: 'Menor o igual que', empty: 'Vacío', notEmpty: 'No vacío'
                }
            }
        }
    };
    function menu(searchType) {
        return ['order', ['orderAsc', 'orderDesc', 'orderClear', 'spacer', searchType]];
    }
    const VIEWS = ['auto', 'cards', 'table'];
    const $sheet = $('#booksSheet');

    function savedView() {
        try {
            const v = localStorage.getItem('library.view');
            return VIEWS.includes(v) ? v : 'auto';
        } catch (e) {
            return 'auto';
        }
    }

    function setView(view, dt) {
        $sheet.removeClass('view-auto view-cards view-table').addClass('view-' + view);
        try { localStorage.setItem('library.view', view); } catch (e) { }
        VIEWS.forEach(v => dt.button('view-' + v + ':name').active(v === view));
    }

    function viewButton(view, text) {
        return {
            text: text,
            name: 'view-' + view,
            action: (e, dt) => setView(view, dt)
        };
    }
    const $table = $('#booksTable');

    if ($table.length) {
        const table = $table.DataTable({
            serverSide: true,
            processing: true,
            ajax: { url: cfg.base + '/data' },
            searchDelay: 350,
            language: cfg.lang === 'es' ? LANG_ES : {},
            order: [[2, 'asc']],
            pageLength: 10,
            lengthMenu: [10, 25, 50],
            ordering: { indicators: false, handler: false },

            select: { style: 'multi', selector: 'td:first-child' },

            layout: {
                topStart: {
                    buttons: [
                        {
                            text: `<i class="mdi mdi-plus"></i> ${t.add}`,
                            className: 'btn-ink',
                            action: () => { window.location = cfg.base + '/new'; }
                        },
                        {
                            extend: 'selectedSingle',
                            text: `<i class="mdi mdi-pencil-outline"></i> ${t.edit}`,
                            action: (e, dt) => {
                                const book = dt.row({ selected: true }).data();
                                window.location = cfg.base + '/' + book.id + '/edit';
                            }
                        },
                        {
                            extend: 'selectedSingle',
                            text: `<i class="mdi mdi-trash-can-outline"></i> ${t.remove}`,
                            action: (e, dt) => openDeleteModal(dt.row({ selected: true }).data())
                        },
                        {
                            extend: 'collection',
                            text: `<i class="mdi mdi-eye-outline"></i> ${t.view}`,
                            className: 'btn-view',
                            autoClose: true,
                            buttons: [
                                viewButton('auto', t.viewAuto),
                                viewButton('cards', t.viewCards),
                                viewButton('table', t.viewTable)
                            ]
                        }
                    ]
                },
                topEnd: 'search',
                bottomStart: ['info', 'pageLength'],
                bottomEnd: 'paging'
            },

            columns: [
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: DataTable.render.select(),
                    className: 'cell-check',
                    columnControl: []
                },
                {
                    data: 'id',
                    className: 'text-muted cell-id',
                    columnControl: menu('searchNumber')
                },
                {
                    data: 'name',
                    className: 'cell-name',
                    columnControl: menu('searchText'),
                    render: (d, type) => type === 'display'
                        ? `<span class="book-title">${esc(d)}</span>` : d
                },
                {
                    data: 'author',
                    className: 'cell-author',
                    columnControl: menu('searchList'),
                    render: (d, type) => type === 'display'
                        ? `<span class="book-author">${avatar(d)}${esc(d)}</span>` : d
                },
                {
                    data: 'price',
                    className: 'text-end cell-price',
                    columnControl: menu('searchNumber'),
                    render: (d, type) => type === 'display'
                        ? `<span class="price-tag">${money.format(d)}</span>` : d
                }
            ]
        });
        table.on('init', () => setView(savedView(), table));
        table.on('xhr', function (e, settings, json) {
            if (json) {
                $('#bookCount').text(json.recordsFiltered);
            }
        });
    }
    function openDeleteModal(book) {
        const form = $('#deleteForm');
        $('#deleteBookName').text(book.name);
        form.attr('action', form.data('base') + book.id);
        bootstrap.Modal.getOrCreateInstance(document.getElementById('deleteModal')).show();
    }
    $('#bookForm').on('input', '.is-invalid', function () {
        $(this).removeClass('is-invalid');
    });
});
