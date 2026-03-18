<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Survey Results" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Survey Results</span>
            <h1>${report.survey.title}</h1>
        </div>
        <a class="button button-secondary" href="${pageContext.request.contextPath}/initiator">Back to dashboard</a>
    </div>

    <div class="meta-panel">
        <p><strong>Course:</strong> ${report.survey.courseCode} - ${report.survey.courseName}</p>
        <p><strong>Access:</strong> ${report.survey.accessMode}</p>
        <p><strong>Total respondents:</strong> ${report.survey.responseCount}</p>
    </div>

    <div class="question-stack">
        <c:forEach var="entry" items="${report.resultsByQuestion}">
            <article class="question-card">
                <h2>${entry.key}</h2>
                <div class="table-card">
                    <table>
                        <thead>
                        <tr>
                            <th>Option</th>
                            <th>Votes</th>
                            <th>Percentage</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="result" items="${entry.value}">
                            <tr>
                                <td>${result.optionText}</td>
                                <td>${result.voteCount}</td>
                                <td><fmt:formatNumber value="${result.percentage}" maxFractionDigits="1" />%</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </article>
        </c:forEach>
        <c:if test="${empty report.resultsByQuestion}">
            <article class="info-card empty-state">
                <h2>No results yet</h2>
                <p>Share the survey with respondents to start collecting feedback.</p>
            </article>
        </c:if>
    </div>

    <section class="content-section compact">
        <div class="section-heading">
            <div>
                <span class="eyebrow">Participation</span>
                <h2>Respondent activity</h2>
            </div>
        </div>
        <div class="table-card">
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Submitted at</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="response" items="${report.responses}">
                    <tr>
                        <td>${response.respondentName}</td>
                        <td>${response.respondentEmail}</td>
                        <td>${response.submittedAt}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty report.responses}">
                    <tr>
                        <td colspan="3" class="empty-table">No responses have been submitted yet.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>
</section>

<%@ include file="../includes/footer.jspf" %>
