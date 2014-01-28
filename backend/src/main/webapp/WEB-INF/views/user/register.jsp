
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<legend>Register</legend>
<c:url value="/user/register" var="formAction"/>
<form:form action="${formAction}" class="form-horizontal" role="form" commandName="user">
	<div class="form-group">
		<div class="col-sm-4">
			<label for="name">Name: </label>
			<form:input path="name"  class="form-control"></form:input>
			<form:errors path="name" class="form-error"></form:errors>
		</div>
	</div>

	<div class="form-group">
		<div class="col-sm-4">
			<label for="email">Email: </label>
			<form:input path="email" class="form-control"></form:input>
			<form:errors path="email" class="form-error"></form:errors>
		</div>
	</div>
	
	<div class="form-group">
		<div class="col-sm-4">
			<label for="password">Password: </label>
			<form:password path="password" class="form-control"></form:password>
			<form:errors path="password" class="form-error"></form:errors>
		</div>
	</div>
	
	<div class="form-group">
		<div class="col-sm-4">
			<label for="passwordConfirmation">Re-enter Password: </label>
			<form:password path="passwordConfirmation" class="form-control"></form:password>
			<form:errors path="passwordConfirmation" class="form-error"></form:errors>
		</div>
	</div>

	<div class="form-group">
		<div class="col-sm-1">
			<button type="submit" class="btn btn-success"">
				<span class="glyphicon glyphicon-log-in"></span> 
				Register
			</button>
		</div>
	</div>

</form:form>