<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${empty survey.id ? 'Create Survey' : 'Edit Survey'}" />
<%@ include file="../includes/header.jspf" %>

<section class="form-shell">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Survey Design</span>
            <h1>${empty survey.id ? 'Create a new survey' : 'Update survey details'}</h1>
        </div>
        <c:if test="${not empty survey.id}">
            <a class="button button-secondary" href="${pageContext.request.contextPath}/initiator/surveys/${survey.id}/results">View results</a>
        </c:if>
    </div>

    <form class="form-card" action="${pageContext.request.contextPath}/initiator/surveys/save" method="post">
        <c:if test="${not empty survey.id}">
            <input type="hidden" name="id" value="${survey.id}">
        </c:if>
        <div class="form-grid">
            <label>
                Course
                <select name="courseId" required>
                    <option value="">Select a course</option>
                    <c:forEach var="course" items="${courses}">
                        <option value="${course.id}" ${course.id eq survey.courseId ? 'selected' : ''}>
                            ${course.code} - ${course.name}
                        </option>
                    </c:forEach>
                </select>
            </label>
            <label>
                Access mode
                <select name="accessMode" required>
                    <option value="AUTHENTICATED" ${survey.accessMode eq 'AUTHENTICATED' ? 'selected' : ''}>Authenticated users only</option>
                    <option value="GUEST_ALLOWED" ${survey.accessMode eq 'GUEST_ALLOWED' ? 'selected' : ''}>Guests allowed</option>
                </select>
            </label>
        </div>
        <label>
            Survey title
            <input type="text" name="title" value="${survey.title}" placeholder="End of term evaluation" required>
        </label>
        <label>
            Description
            <textarea name="description" rows="4" placeholder="Explain the purpose of this survey">${survey.description}</textarea>
        </label>
        <label class="checkbox-row">
            <input type="checkbox" name="published" ${survey.published ? 'checked' : ''}>
            <span>Publish this survey so respondents can see it</span>
        </label>
        <div class="form-actions">
            <button class="button" type="submit">Save survey</button>
            <a class="button button-secondary" href="${pageContext.request.contextPath}/initiator">Back to dashboard</a>
        </div>
    </form>
</section>

<c:if test="${not empty survey.id}">
    <section class="content-section">
        <div class="section-heading">
            <div>
                <span class="eyebrow">Survey Questions</span>
                <h2>Questions and options</h2>
            </div>
            <a class="button" href="${pageContext.request.contextPath}/initiator/surveys/${survey.id}/questions/new">Add question</a>
        </div>

        <div class="question-stack">
            <c:forEach var="question" items="${survey.questions}">
                <article class="question-card">
                    <div class="question-header">
                        <div>
                            <span class="badge">Order ${question.displayOrder}</span>
                            <h3>${question.questionText}</h3>
                        </div>
                        <div class="table-actions">
                            <a class="button button-small" href="${pageContext.request.contextPath}/initiator/questions/${question.id}/edit">Edit</a>
                            <a class="button button-small button-secondary" href="${pageContext.request.contextPath}/initiator/questions/${question.id}/options/new">Add option</a>
                            <form action="${pageContext.request.contextPath}/initiator/questions/${question.id}/delete" method="post">
                                <input type="hidden" name="surveyId" value="${survey.id}">
                                <button class="button button-small button-danger" type="submit">Delete</button>
                            </form>
                        </div>
                    </div>

                    <ul class="option-list">
                        <c:forEach var="option" items="${question.options}">
                            <li>
                                <span>${option.displayOrder}. ${option.optionText}</span>
                                <span class="inline-actions">
                                    <a href="${pageContext.request.contextPath}/initiator/options/${option.id}/edit">Edit</a>
                                    <form action="${pageContext.request.contextPath}/initiator/options/${option.id}/delete" method="post">
                                        <input type="hidden" name="surveyId" value="${survey.id}">
                                        <button type="submit">Delete</button>
                                    </form>
                                </span>
                            </li>
                        </c:forEach>
                        <c:if test="${empty question.options}">
                            <li class="empty-option">No options added yet.</li>
                        </c:if>
                    </ul>
                </article>
            </c:forEach>
            <c:if test="${empty survey.questions}">
                <article class="info-card empty-state">
                    <h3>No questions yet</h3>
                    <p>Add questions and options to make the survey ready for respondents.</p>
                </article>
            </c:if>
        </div>
    </section>
</c:if>

<%@ include file="../includes/footer.jspf" %>
