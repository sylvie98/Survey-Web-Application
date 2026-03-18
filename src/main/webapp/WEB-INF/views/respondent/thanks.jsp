<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Thank You" />
<%@ include file="../includes/header.jspf" %>

<section class="content-section compact">
    <article class="info-card">
        <span class="eyebrow">Submission Complete</span>
        <h1>Thank you for your feedback</h1>
        <p>Your survey response has been recorded successfully.</p>
        <c:if test="${not empty submissionReceipt}">
            <p>${submissionReceipt.emailStatus}</p>
            <c:if test="${not empty submissionReceipt.respondentEmail}">
                <p><strong>Respondent email:</strong> ${submissionReceipt.respondentEmail}</p>
            </c:if>
        </c:if>
        <a class="button" href="${pageContext.request.contextPath}/surveys">Back to surveys</a>
    </article>
</section>

<%@ include file="../includes/footer.jspf" %>
