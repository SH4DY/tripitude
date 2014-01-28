<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/resources/images/favicon.png"  />"/>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery-2.0.3.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.i18n.properties.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.maxlength-min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/custom/helper.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.timeago.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/bootstrap.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/typeahead.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/lightbox/js/lightbox-2.6.min.js" />"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/js/lightbox/css/lightbox.css" />">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/bootstrap.css" />">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/layout.css" />">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/typeahead.custom.css" />">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/form.css" />">
<title><tiles:insertAttribute name="title" ignore="true" /></title>
</head>
<body>
	<!-- BASE JS VARS -->
	<c:set var="baseURL" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/"/>
    <h1><c:url value=""/></h1>
    <script type="text/javascript">
      var baseVars = {
    	 baseUrl : "${baseURL}",
      };  
    </script>
    
    <!--[if lte IE 9]>
	  <script src="//cdnjs.cloudflare.com/ajax/libs/html5shiv/3.6.2/html5shiv.js"></script>
	  <script src="//cdnjs.cloudflare.com/ajax/libs/respond.js/1.3.0/respond.js"></script>
	<![endif]-->

	<div>
		<tiles:insertAttribute name="header" />
	</div>
	<div class="container">

		<tiles:insertAttribute name="messages" />
		
		<tiles:insertAttribute name="body" />
	</div>
	
	<tiles:insertAttribute name="footer" />
</body>
</html>
