<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:if test="${error == true}">
	 <div class="alert alert-danger">Invalid login or password.</div>
  </c:if>
<legend>Login</legend>


<form class="form-horizontal" role="form" method="post" action="<c:url value='/j_spring_security_check'/>">
  <div class="form-group">
    <div class="col-sm-4">
      <input type="text" class="form-control" name="j_username" id="j_username" placeholder="Email" size="30" maxlength="100">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-4">
      <input type="password" class="form-control" name="j_password" id="j_password" placeholder="Password" size="30" maxlength="100">
    </div>
  </div>
  	
  <div class="form-group">
    <div class="col-sm-1">
      <button type="submit" class="btn btn-success"">
      <span class="glyphicon glyphicon-log-in"></span>
      Login
      </button>
    </div>
  </div>
</form>
