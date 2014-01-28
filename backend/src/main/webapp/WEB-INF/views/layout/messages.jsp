<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<script type="text/javascript"
	src="<c:url value="/resources/js/custom/statusmessages.js" />">	
</script>

<c:if test="${not empty errorMessage}">
	<div class="alert alert-danger">${errorMessage}</div>
</c:if>
<c:if test="${not empty warningMessage}">
	<div class="alert alert-warning">${warningMessage}</div>
</c:if>
<c:if test="${not empty infoMessage}">
	<div class="alert alert-info">${infoMessage}</div>
</c:if>
<c:if test="${not empty successMessage}">
	<div class="alert alert-success">${successMessage}</div>
</c:if>


<div class="alert alert-danger alert-danger-js alert-js" style="display: none"></div>
<div class="alert alert-warning alert-warning-js alert-js" style="display: none"></div>
<div class="alert alert-info alert-info-js alert-js" style="display: none"></div>
<div class="alert alert-success alert-success-js alert-js" style="display: none"></div>
