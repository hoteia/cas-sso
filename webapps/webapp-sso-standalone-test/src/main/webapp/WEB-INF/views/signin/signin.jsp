<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<form class="form-narrow form-horizontal" action='<c:out value="${casLoginUrl}"/>' method="get">
    <c:if test="${not empty param['error']}">
        <div class="alert alert-error">
            Sign in error. Please try again.
        </div>
    </c:if>
    <fieldset>
        <legend>Please Sign In</legend>
        <div class="form-group">
            <label for="inputEmail" class="col-lg-2 control-label">Email</label>
            <div class="col-lg-10">
                <input type="text" class="form-control" id="inputEmail" placeholder="Email" name="username">
            </div>
        </div>
        <div class="form-group">
            <label for="inputPassword" class="col-lg-2 control-label">Password</label>
            <div class="col-lg-10">
                <input type="password" class="form-control" id="inputPassword" placeholder="Password" name="password">
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-10">
                <div class="checkbox">
                    <label>
                        <input type="checkbox" name="_spring_security_remember_me"> Remember me
                    </label>
                </div>
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-10">
                <button type="submit" class="btn btn-default">Sign in</button>
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-10">
                <p>New here? <a href='<s:url value="/signup"/>'>Sign Up</a></p>
            </div>
        </div>

        <input type="hidden" name="auto" value="true" />
        <input type="hidden" name="service" value='<c:out value="${casServiceUrl}"/>' />
        <input type="hidden" name="error-redirect" value='<s:url value="/signin?error=loginError"/>'/>
        <input type="hidden" name="loginTicket" value='<%= request.getParameter("loginTicket") %>' />
    </fieldset>
</form>
