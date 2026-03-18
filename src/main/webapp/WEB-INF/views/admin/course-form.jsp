<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${empty course.id ? 'New Course' : 'Edit Course'}" />
<%@ include file="../includes/header.jspf" %>

<section class="form-shell">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Course Management</span>
            <h1>${empty course.id ? 'Create a course' : 'Update course details'}</h1>
        </div>
    </div>

    <form class="form-card" action="${pageContext.request.contextPath}/admin/courses/save" method="post">
        <c:if test="${not empty course.id}">
            <input type="hidden" name="id" value="${course.id}">
        </c:if>
        <div class="form-grid">
            <label>
                Course code
                <input type="text" name="code" value="${course.code}" placeholder="CSC401" required>
            </label>
            <label>
                Course name
                <input type="text" name="name" value="${course.name}" placeholder="Web Application Development" required>
            </label>
        </div>
        <label>
            Description
            <textarea name="description" rows="5" placeholder="Briefly describe the course">${course.description}</textarea>
        </label>
        <div class="form-actions">
            <button class="button" type="submit">Save course</button>
            <a class="button button-secondary" href="${pageContext.request.contextPath}/admin/courses">Cancel</a>
        </div>
    </form>
</section>

<%@ include file="../includes/footer.jspf" %>
