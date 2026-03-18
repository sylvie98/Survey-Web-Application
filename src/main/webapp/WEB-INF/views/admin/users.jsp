<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Users" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">System Users</span>
            <h1>Registered accounts</h1>
        </div>
    </div>

    <div class="table-card">
        <table>
            <thead>
            <tr>
                <th>Name</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td>${user.fullName}</td>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>${user.role}</td>
                    <td>${user.approvalStatus}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="../includes/footer.jspf" %>
