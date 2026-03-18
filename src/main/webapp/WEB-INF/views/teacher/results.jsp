<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Survey Results" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Teacher Results View</span>
            <h1>${report.survey.title}</h1>
        </div>
        <a class="button button-secondary" href="${pageContext.request.contextPath}/teacher">Back to dashboard</a>
    </div>

    <div class="meta-panel">
        <p><strong>Course:</strong> ${report.survey.courseCode} - ${report.survey.courseName}</p>
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
                <p>Once students or guests answer the survey, the summary will appear here.</p>
            </article>
        </c:if>
    </div>
</section>

<%@ include file="../includes/footer.jspf" %>
