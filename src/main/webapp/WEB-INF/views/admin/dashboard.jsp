<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Dashboard" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Administrator</span>
            <h1>Platform overview</h1>
        </div>
    </div>

    <div class="stats-grid">
        <article class="stat-card">
            <span>Pending teachers</span>
            <strong>${pendingTeachers}</strong>
        </article>
        <article class="stat-card">
            <span>Courses</span>
            <strong>${totalCourses}</strong>
        </article>
        <article class="stat-card">
            <span>Users</span>
            <strong>${totalUsers}</strong>
        </article>
        <article class="stat-card">
            <span>Surveys</span>
            <strong>${totalSurveys}</strong>
        </article>
    </div>

    <div class="action-grid">
        <a class="action-tile" href="${pageContext.request.contextPath}/admin/teachers">
            <h2>Teacher approvals</h2>
            <p>Approve or reject teacher registrations.</p>
        </a>
        <a class="action-tile" href="${pageContext.request.contextPath}/admin/courses">
            <h2>Course management</h2>
            <p>Create, edit, delete, and assign teachers to courses.</p>
        </a>
        <a class="action-tile" href="${pageContext.request.contextPath}/admin/users">
            <h2>System users</h2>
            <p>View the accounts that exist in the platform.</p>
        </a>
        <a class="action-tile" href="${pageContext.request.contextPath}/admin/surveys">
            <h2>Survey catalog</h2>
            <p>See all surveys created in the system.</p>
        </a>
    </div>
</section>

<%@ include file="../includes/footer.jspf" %>
