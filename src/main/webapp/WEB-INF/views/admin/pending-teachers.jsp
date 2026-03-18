<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Teacher Approval" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Administrator</span>
            <h1>Pending teacher registrations</h1>
        </div>
    </div>

    <div class="table-card">
        <table>
            <thead>
            <tr>
                <th>Name</th>
                <th>Username</th>
                <th>Email</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="teacher" items="${pendingTeachers}">
                <tr>
                    <td>${teacher.fullName}</td>
                    <td>${teacher.username}</td>
                    <td>${teacher.email}</td>
                    <td>${teacher.approvalStatus}</td>
                    <td class="table-actions">
                        <form action="${pageContext.request.contextPath}/admin/teachers/${teacher.id}/approve" method="post">
                            <button class="button button-small" type="submit">Approve</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin/teachers/${teacher.id}/reject" method="post">
                            <button class="button button-small button-secondary" type="submit">Reject</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty pendingTeachers}">
                <tr>
                    <td colspan="5" class="empty-table">There are no pending teacher registrations right now.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="../includes/footer.jspf" %>
