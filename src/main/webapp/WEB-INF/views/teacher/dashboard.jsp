<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Teacher Dashboard" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Teacher Dashboard</span>
            <h1>Surveys linked to your courses</h1>
        </div>
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
                <a class="button" href="${pageContext.request.contextPath}/teacher/surveys/${survey.id}/results">View results</a>
            </article>
        </c:forEach>
        <c:if test="${empty surveys}">
            <article class="info-card empty-state">
                <h2>No assigned surveys yet</h2>
                <p>Ask the administrator to assign you to a course and the initiator to publish surveys for it.</p>
            </article>
        </c:if>
    </div>
</section>

<%@ include file="../includes/footer.jspf" %>
