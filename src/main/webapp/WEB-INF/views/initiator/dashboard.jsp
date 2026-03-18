<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Initiator Dashboard" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Survey Initiator</span>
            <h1>Manage your surveys</h1>
        </div>
        <a class="button" href="${pageContext.request.contextPath}/initiator/surveys/new">Create survey</a>
    </div>

    <div class="card-grid">
        <c:forEach var="survey" items="${surveys}">
            <article class="info-card">
                <span class="badge">${survey.accessMode}</span>
                <h2>${survey.title}</h2>
                <p>${survey.description}</p>
                <dl class="meta-list">
                    <dt>Course</dt>
                    <dd>${survey.courseCode} - ${survey.courseName}</dd>
                    <dt>Published</dt>
                    <dd>${survey.published ? 'Yes' : 'No'}</dd>
                    <dt>Responses</dt>
                    <dd>${survey.responseCount}</dd>
                </dl>
                <div class="stack-actions">
                    <a class="button" href="${pageContext.request.contextPath}/initiator/surveys/${survey.id}/edit">Edit survey</a>
                    <a class="button button-secondary" href="${pageContext.request.contextPath}/initiator/surveys/${survey.id}/results">View results</a>
                    <form action="${pageContext.request.contextPath}/initiator/surveys/${survey.id}/delete" method="post">
                        <button class="button button-danger" type="submit">Delete survey</button>
                    </form>
                </div>
            </article>
        </c:forEach>
        <c:if test="${empty surveys}">
            <article class="info-card empty-state">
                <h2>No surveys yet</h2>
                <p>Create your first course evaluation survey to start collecting responses.</p>
            </article>
        </c:if>
    </div>
</section>

<%@ include file="../includes/footer.jspf" %>
