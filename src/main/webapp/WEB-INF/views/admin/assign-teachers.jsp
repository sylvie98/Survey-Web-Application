<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Assign Teachers" />
<%@ include file="../includes/header.jspf" %>

<section class="form-shell">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Teacher Assignment</span>
            <h1>Assign teachers to ${course.code} - ${course.name}</h1>
        </div>
    </div>

    <form class="form-card" action="${pageContext.request.contextPath}/admin/courses/${course.id}/assign" method="post">
        <div class="checkbox-list">
            <c:forEach var="teacher" items="${teachers}">
                <c:set var="isAssigned" value="false" />
                <c:forEach var="assignedId" items="${assignedTeacherIds}">
                    <c:if test="${assignedId eq teacher.id}">
                        <c:set var="isAssigned" value="true" />
                    </c:if>
                </c:forEach>
                <label class="checkbox-row">
                    <input type="checkbox" name="teacherIds" value="${teacher.id}"
                        ${isAssigned ? 'checked' : ''}>
                    <span>${teacher.fullName} (${teacher.email})</span>
                </label>
            </c:forEach>
            <c:if test="${empty teachers}">
                <p class="helper-copy">There are no approved teachers available for assignment yet.</p>
            </c:if>
        </div>
        <div class="form-actions">
            <button class="button" type="submit">Save assignments</button>
            <a class="button button-secondary" href="${pageContext.request.contextPath}/admin/courses">Back to courses</a>
        </div>
    </form>
</section>

<%@ include file="../includes/footer.jspf" %>
