<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<header class="page-head d-flex flex-column flex-md-row justify-content-between align-items-md-end gap-3">
    <div>
        <span class="eyebrow"><spring:message code="book.list.eyebrow"/></span>
        <h1 class="mb-1">
            <spring:message code="book.list.title"/>
            <span class="count-pill" id="bookCount">…</span>
        </h1>
        <p class="text-muted mb-0"><spring:message code="book.list.subtitle"/></p>
    </div>
    <a class="btn btn-ink" href="<c:url value='/books/new'/>">
        <i class="mdi mdi-plus"></i> <spring:message code="nav.new"/>
    </a>
</header>

<c:if test="${not empty message}">
    <div class="alert alert-flash alert-dismissible fade show d-flex align-items-center gap-2" role="alert">
        <i class="mdi mdi-check-circle-outline fs-5"></i>
        <spring:message code="${message}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<section class="sheet view-auto" id="booksSheet">
    <table id="booksTable" class="table table-hover align-middle w-100">
        <thead>
            <tr>
                <th></th>
                <th><spring:message code="book.field.id"/></th>
                <th><spring:message code="book.field.name"/></th>
                <th><spring:message code="book.field.author"/></th>
                <th class="text-end"><spring:message code="book.field.price"/></th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th></th>
                <th><spring:message code="book.field.id"/></th>
                <th><spring:message code="book.field.name"/></th>
                <th><spring:message code="book.field.author"/></th>
                <th class="text-end"><spring:message code="book.field.price"/></th>
            </tr>
        </tfoot>
    </table>
</section>

<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-body text-center p-4">
                <div class="modal-icon mx-auto mb-3"><i class="mdi mdi-trash-can-outline"></i></div>
                <h5 class="modal-title mb-2"><spring:message code="delete.confirm.title"/></h5>
                <p class="text-muted mb-1"><spring:message code="delete.confirm.body"/></p>
                <p class="fw-semibold mb-0" id="deleteBookName"></p>
            </div>
            <div class="modal-footer justify-content-center border-0 pt-0 pb-4">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                    <spring:message code="action.cancel"/>
                </button>
                <form id="deleteForm" method="post" data-base="<c:url value='/books/'/>">
                    <input type="hidden" name="_method" value="delete">
                    <button type="submit" class="btn btn-danger">
                        <i class="mdi mdi-trash-can-outline"></i> <spring:message code="delete.confirm.yes"/>
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    window.LIBRARY = {
        base: '<c:url value="/books"/>',
        lang: '${lang}',
        i18n: {
            add:     '<spring:message code="nav.new" javaScriptEscape="true"/>',
            edit:    '<spring:message code="action.edit" javaScriptEscape="true"/>',
            remove:  '<spring:message code="action.delete" javaScriptEscape="true"/>',
            view:      '<spring:message code="action.view" javaScriptEscape="true"/>',
            viewAuto:  '<spring:message code="action.view.auto" javaScriptEscape="true"/>',
            viewCards: '<spring:message code="action.view.cards" javaScriptEscape="true"/>',
            viewTable: '<spring:message code="action.view.table" javaScriptEscape="true"/>',
            title:   '<spring:message code="book.list.title" javaScriptEscape="true"/>'
        }
    };
</script>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
