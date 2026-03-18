<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Available Surveys" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Respondent Portal</span>
            <h1>Available surveys</h1>
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
                    <dt>Responses</dt>
                    <dd>${survey.responseCount}</dd>
                </dl>
                <a class="button" href="${pageContext.request.contextPath}/surveys/${survey.id}">Open survey</a>
            </article>
        </c:forEach>
        <c:if test="${empty surveys}">
            <article class="info-card empty-state">
                <h2>No surveys available</h2>
                <p>Published surveys will appear here once the initiator makes them available.</p>
            </article>
        </c:if>
    </div>
</section>

<%@ include file="../includes/footer.jspf" %>
