<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Home" />
<%@ include file="includes/header.jspf" %>

<section class="hero-panel">
    <div>
        <span class="eyebrow">Assignment Scenario</span>
        <h1>Collect course feedback with clear role-based workflows.</h1>
        <p>
            This web application supports teacher approval, course management, survey design,
            survey participation, and survey results for academic course evaluations.
        </p>
    </div>
    <div class="hero-card">
        <h2>Included roles</h2>
        <ul class="simple-list">
            <li>Administrator approves teachers, manages courses, and monitors the platform.</li>
            <li>Survey Initiator creates surveys, questions, and options, then reviews results.</li>
            <li>Teacher sees surveys linked to assigned courses and monitors participation.</li>
            <li>Respondent completes published surveys as an authenticated user or guest.</li>
        </ul>
    </div>
</section>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Published Surveys</span>
            <h2>Available course evaluations</h2>
        </div>
        <a class="button button-secondary" href="${pageContext.request.contextPath}/surveys">Open survey list</a>
    </div>

    <div class="card-grid">
        <c:forEach var="survey" items="${publishedSurveys}">
            <article class="info-card">
                <span class="badge">${survey.accessMode}</span>
                <h3>${survey.title}</h3>
                <p>${survey.description}</p>
                <dl class="meta-list">
                    <dt>Course</dt>
                    <dd>${survey.courseCode} - ${survey.courseName}</dd>
                    <dt>Responses</dt>
                    <dd>${survey.responseCount}</dd>
                </dl>
                <a class="button" href="${pageContext.request.contextPath}/surveys/${survey.id}">Respond</a>
            </article>
        </c:forEach>
        <c:if test="${empty publishedSurveys}">
            <article class="info-card empty-state">
                <h3>No published surveys yet</h3>
                <p>Create a survey as the initiator and publish it to make it appear here.</p>
            </article>
        </c:if>
    </div>
</section>

<%@ include file="includes/footer.jspf" %>
