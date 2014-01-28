<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>

<div class="navbar navbar-default navbar-fixed-top">
				<img class="tripitude-logo" src="<c:url value='/resources/images/logo.png'/>"/>
	<div class="container">
		<div class="navbar-header tripitude-header">
			<a href="<c:url value="/"/>" class="navbar-brand">
				Tripitude
			</a>
			<button class="navbar-toggle" type="button" data-toggle="collapse"
				data-target="#navbar-main">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<div class="navbar-collapse collapse" id="navbar-main">			
			<ul class="nav navbar-nav">
				<li class="dropdown"><a href="<c:url value='/map'/>">Map</a></li>
			</ul>
			
			<ul class="nav navbar-nav">
				<li class="dropdown"><a href="<c:url value='/diary'/>">Travel Diary</a></li>
			</ul>

			<ul class="nav navbar-nav navbar-right">

				<sec:authorize access="isAnonymous()">
					<li><a href="<c:url value="/user/login"/>"> <span
							class="glyphicon glyphicon-log-in"></span> Login
					</a></li>
					<li><a href="<c:url value="/user/register"/>"> <span
							class="glyphicon glyphicon-plus-sign"></span> Register
					</a></li>
				</sec:authorize>
				<sec:authorize access="isAuthenticated()">
					<li><a href="<c:url value="/j_spring_security_logout" />">
							<span class="glyphicon glyphicon-log-out"></span> Logout
					</a></li>
				</sec:authorize>
				</li>
			</ul>

		</div>
	</div>
</div>