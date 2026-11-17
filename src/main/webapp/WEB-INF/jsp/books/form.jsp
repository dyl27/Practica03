<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<c:set var="isNew" value="${empty book.id}"/>
<c:choose>
    <c:when test="${isNew}"><c:url var="formAction" value="/books"/></c:when>
    <c:otherwise><c:url var="formAction" value="/books/${book.id}"/></c:otherwise>
</c:choose>

<div class="row justify-content-center">
    <div class="col-12 col-lg-7 col-xl-6">

        <header class="page-head">
            <span class="eyebrow"><spring:message code="book.form.eyebrow"/></span>
            <h1 class="mb-1">
                <spring:message code="${isNew ? 'book.form.new' : 'book.form.edit'}"/>
                <c:if test="${not isNew}"><span class="count-pill">#${book.id}</span></c:if>
            </h1>
            <p class="text-muted mb-0"><spring:message code="book.form.subtitle"/></p>
        </header>

        <section class="sheet">

            <form:form modelAttribute="book" action="${formAction}" method="${isNew ? 'post' : 'put'}"
                       id="bookForm" novalidate="novalidate">

                <div class="mb-3">
                    <label for="name" class="form-label"><spring:message code="book.field.name"/></label>
                    <div class="input-group has-validation">
                        <span class="input-group-text"><i class="mdi mdi-book-open-variant"></i></span>
                        <form:input path="name" cssClass="form-control" cssErrorClass="form-control is-invalid" maxlength="200"/>
                        <form:errors path="name" cssClass="invalid-feedback" element="div"/>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="author" class="form-label"><spring:message code="book.field.author"/></label>
                    <div class="input-group has-validation">
                        <span class="input-group-text"><i class="mdi mdi-feather"></i></span>
                        <form:input path="author" cssClass="form-control" cssErrorClass="form-control is-invalid" maxlength="150"/>
                        <form:errors path="author" cssClass="invalid-feedback" element="div"/>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="price" class="form-label"><spring:message code="book.field.price"/></label>
                    <div class="input-group has-validation">
                        <span class="input-group-text"><i class="mdi mdi-currency-usd"></i></span>
                        <form:input path="price" type="number" step="0.01" min="0"
                                    cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                        <span class="input-group-text">MXN</span>
                        <form:errors path="price" cssClass="invalid-feedback" element="div"/>
                    </div>
                </div>

                <div class="d-flex flex-column-reverse flex-sm-row justify-content-end gap-2">
                    <a class="btn btn-outline-secondary" href="<c:url value='/books'/>">
                        <spring:message code="action.cancel"/>
                    </a>
                    <button type="submit" class="btn btn-ink">
                        <i class="mdi mdi-content-save-outline"></i> <spring:message code="action.save"/>
                    </button>
                </div>
            </form:form>
        </section>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
