<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${empty option.id ? 'Add Option' : 'Edit Option'}" />
<%@ include file="../includes/header.jspf" %>

<section class="form-shell narrow">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Question Option</span>
            <h1>${empty option.id ? 'Add a response option' : 'Edit response option'}</h1>
        </div>
        <p>${question.questionText}</p>
    </div>

    <form class="form-card" action="${pageContext.request.contextPath}/initiator/options/save" method="post">
        <c:if test="${not empty option.id}">
            <input type="hidden" name="id" value="${option.id}">
        </c:if>
        <input type="hidden" name="questionId" value="${question.id}">
        <input type="hidden" name="surveyId" value="${survey.id}">
        <label>
            Option text
            <input type="text" name="optionText" value="${option.optionText}" placeholder="Excellent" required>
        </label>
        <label>
            Display order
            <input type="number" name="displayOrder" value="${option.displayOrder}" min="1" required>
        </label>
        <div class="form-actions">
            <button class="button" type="submit">Save option</button>
            <a class="button button-secondary" href="${pageContext.request.contextPath}/initiator/surveys/${survey.id}/edit">Back to survey</a>
        </div>
    </form>
</section>

<%@ include file="../includes/footer.jspf" %>
