<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<script type="text/javascript"
   src="http://maps.googleapis.com/maps/api/js?libraries=geometry&sensor=false">
</script>
<script type="text/javascript"
	src="<c:url value="/resources/js/custom/map/maputil.js" />">
</script>
<script type="text/javascript"
	src="<c:url value="/resources/js/custom/map/mapview.js" />">
</script>
<script type="text/javascript"
	src="<c:url value="/resources/js/custom/map/searchform.js" />">
</script>


<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/map.css" />">

	<!-- <legend>Map</legend> -->
	
	<%-- <sec:authentication var="principal" property="principal" /> --%>


<!-- Search form -->
<form id="map-search-form">
	<div id="map-searchbar">
      <ul class="nav nav-pills">
        
        <li id="mapItemName" class="searchFilter">       
        	<div class="input-append">
        		<input type="text" autocomplete="off" autocorrect="off" autocapitalize="off" spellcheck="false" class="form-control" placeholder="Search by name" data-provide="typeahead">
        	</div>
        </li>
        
        <li class="divider-vertical"></li>
        
        <li id="mapItemType">       
        	<div class="btn-group">        	    
        	    <button type="button" class="btn btn-default active" rel="all">All</button>
				<button type="button" class="btn btn-default" rel="hotspot">Hotspots</button>
				<button type="button" class="btn btn-default" rel="route">Routes</button>
			</div>
        </li>
        
        <li id="mapItemEvent">       
        	<div class="btn-group">        	    
				<button type="button" class="btn btn-default" rel="event">Events</button>
			</div>
        </li>
        
        
        <li id="mapItemCategories" class="dropdown searchFilter">
          <a role="button" data-toggle="dropdown" href="#">Categories<b class="caret"></b></a>
          <ul id="mapItemCategories-list" class="dropdown-menu" role="menu" aria-labelledby="mapItemCategories">
            <li role="presentation">
            	<a role="menuitem" tabindex="-1" class="all-button">All</a>
            </li>
            <li role="presentation" class="divider"></li>           
            <c:forEach items="${mapItemCategories}" var="mapItemCategory"> 
            	<li role="presentation" class="mapItemCategories-list-item">
				  	<label class="checkbox-inline">
				  		<input type="checkbox" class="mapItemCategory" name="mapItemCategory[]" value="${mapItemCategory.id}"> 
				  		<spring:message code="mapitem_categories.${mapItemCategory.name}"/> 
					</label>
				</li>
  			</c:forEach>
	  	    
          </ul>
        </li>
        <li id="map-close-route">
        
		      <button id="map-close-route-btn" class="btn btn-lg btn-danger" type="button"><span class="glyphicon glyphicon-remove"></span> Close route</button>
        </li>
        
      </ul> <!-- /tabs -->
    </div>  	
</form>

<div id="map-wrapper">
	<div id="map_canvas" style="width:100%; height:500px;"></div>
</div>

<%@ include file="mapitemdetails.jsp" %> 


