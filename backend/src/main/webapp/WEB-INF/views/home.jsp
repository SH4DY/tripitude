<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<legend>Welcome to Tripitude</legend>

<spring:message code="message.test"/> 
<<spring:message ></spring:message><p>You are logged in as <strong>${currentUserName} (${currentUserEmail})</strong><p/>


