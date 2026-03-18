<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Courses" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Course Management</span>
            <h1>Manage courses</h1>
        </div>
        <a class="button" href="${pageContext.request.contextPath}/admin/courses/new">Add course</a>
    </div>

    <div class="table-card">
        <table>
            <thead>
            <tr>
                <th>Code</th>
                <th>Name</th>
                <th>Description</th>
                <th>Assigned teachers</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="course" items="${courses}">
                <tr>
                    <td>${course.code}</td>
                    <td>${course.name}</td>
                    <td>${course.description}</td>
                    <td>${empty course.teacherNames ? 'Not assigned' : course.teacherNames}</td>
                    <td class="table-actions">
                        <a class="button button-small" href="${pageContext.request.contextPath}/admin/courses/${course.id}/edit">Edit</a>
                        <a class="button button-small button-secondary" href="${pageContext.request.contextPath}/admin/courses/${course.id}/assign">Assign teachers</a>
                        <form action="${pageContext.request.contextPath}/admin/courses/${course.id}/delete" method="post">
                            <button class="button button-small button-danger" type="submit">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty courses}">
                <tr>
                    <td colspan="5" class="empty-table">No courses have been created yet.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="../includes/footer.jspf" %>
