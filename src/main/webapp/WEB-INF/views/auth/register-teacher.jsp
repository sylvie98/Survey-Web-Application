<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Teacher Registration" />
<%@ include file="../includes/header.jspf" %>

<section class="form-shell">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Teacher Registration</span>
            <h1>Request teacher access</h1>
        </div>
        <p>After you submit this form, an administrator must approve your account.</p>
    </div>

    <form class="form-card" action="${pageContext.request.contextPath}/auth/teacher-register" method="post">
        <div class="form-grid">
            <label>
                Full name
                <input type="text" name="fullName" placeholder="Jane Doe" required>
            </label>
            <label>
                Username
                <input type="text" name="username" placeholder="jdoe" required>
            </label>
            <label>
                Email address
                <input type="email" name="email" placeholder="jane@example.com" required>
            </label>
            <label>
                Password
                <input type="password" name="password" placeholder="Choose a password" required>
            </label>
        </div>
        <button class="button" type="submit">Submit registration</button>
    </form>
</section>

<%@ include file="../includes/footer.jspf" %>
