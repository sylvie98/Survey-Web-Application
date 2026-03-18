<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Take Survey" />
<%@ include file="../includes/header.jspf" %>

<section class="form-shell">
    <div class="section-heading">
        <div>
            <span class="eyebrow">Survey Response</span>
            <h1>${survey.title}</h1>
        </div>
        <p>${survey.courseCode} - ${survey.courseName}</p>
    </div>

    <form class="form-card" action="${pageContext.request.contextPath}/surveys/${survey.id}/submit" method="post">
        <p class="helper-copy">${survey.description}</p>

        <c:if test="${survey.accessMode eq 'GUEST_ALLOWED' and empty currentUser}">
            <div class="form-grid">
                <label>
                    Your name
                    <input type="text" name="guestName" placeholder="Enter your full name" required>
                </label>
                <label>
                    Your email
                    <input type="email" name="guestEmail" placeholder="Enter your email address" required>
                </label>
            </div>
        </c:if>

        <c:if test="${not empty currentUser}">
            <div class="meta-panel">
                <p><strong>Responding as:</strong> ${currentUser.fullName} (${currentUser.email})</p>
            </div>
        </c:if>

        <div class="question-stack">
            <c:forEach var="question" items="${survey.questions}">
                <article class="question-card">
                    <h2>${question.displayOrder}. ${question.questionText}</h2>
                    <div class="radio-list">
                        <c:forEach var="option" items="${question.options}">
                            <label class="checkbox-row">
                                <input type="radio" name="question_${question.id}" value="${option.id}" required>
                                <span>${option.optionText}</span>
                            </label>
                        </c:forEach>
                    </div>
                </article>
            </c:forEach>
        </div>

        <button class="button" type="submit">Submit survey</button>
    </form>
</section>

<%@ include file="../includes/footer.jspf" %>
