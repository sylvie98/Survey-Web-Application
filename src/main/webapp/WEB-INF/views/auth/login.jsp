<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login" />
<%@ include file="../includes/header.jspf" %>

<section class="form-shell narrow">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Authentication</span>
            <h1>Sign in to continue</h1>
        </div>
    </div>

    <form class="form-card" action="${pageContext.request.contextPath}/auth/login" method="post">
        <input type="hidden" name="target" value="${target}">
        <label>
            Username
            <input type="text" name="username" placeholder="Enter your username" required>
        </label>
        <label>
            Password
            <input type="password" name="password" placeholder="Enter your password" required>
        </label>
        <button class="button" type="submit">Login</button>
    </form>

    <p class="helper-copy">
        Teachers without accounts can
        <a href="${pageContext.request.contextPath}/auth/teacher-register">register here</a>.
    </p>
</section>

<%@ include file="../includes/footer.jspf" %>
