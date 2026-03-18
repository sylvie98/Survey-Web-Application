<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${empty question.id ? 'Add Question' : 'Edit Question'}" />
<%@ include file="../includes/header.jspf" %>

<section class="form-shell narrow">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Survey Question</span>
            <h1>${empty question.id ? 'Add a question' : 'Edit question'}</h1>
        </div>
        <p>${survey.title}</p>
    </div>

    <form class="form-card" action="${pageContext.request.contextPath}/initiator/questions/save" method="post">
        <c:if test="${not empty question.id}">
            <input type="hidden" name="id" value="${question.id}">
        </c:if>
        <input type="hidden" name="surveyId" value="${survey.id}">
        <label>
            Question text
            <textarea name="questionText" rows="4" placeholder="How would you rate the clarity of the course content?" required>${question.questionText}</textarea>
        </label>
        <label>
            Display order
            <input type="number" name="displayOrder" value="${question.displayOrder}" min="1" required>
        </label>
        <div class="form-actions">
            <button class="button" type="submit">Save question</button>
            <a class="button button-secondary" href="${pageContext.request.contextPath}/initiator/surveys/${survey.id}/edit">Back to survey</a>
        </div>
    </form>
</section>

<%@ include file="../includes/footer.jspf" %>
