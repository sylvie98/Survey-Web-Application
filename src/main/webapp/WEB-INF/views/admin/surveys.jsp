<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="All Surveys" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Survey Catalog</span>
            <h1>Surveys created in the system</h1>
        </div>
    </div>

    <div class="table-card">
        <table>
            <thead>
            <tr>
                <th>Survey</th>
                <th>Course</th>
                <th>Initiator</th>
                <th>Access</th>
                <th>Published</th>
                <th>Responses</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="survey" items="${surveys}">
                <tr>
                    <td>${survey.title}</td>
                    <td>${survey.courseCode} - ${survey.courseName}</td>
                    <td>${survey.initiatorName}</td>
                    <td>${survey.accessMode}</td>
                    <td>${survey.published ? 'Yes' : 'No'}</td>
                    <td>${survey.responseCount}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty surveys}">
                <tr>
                    <td colspan="6" class="empty-table">No surveys have been created yet.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="../includes/footer.jspf" %>
