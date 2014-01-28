
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<script type="text/javascript"
	src="<c:url value="/resources/js/custom/map/mapitemdetails.js" />">
</script>
<script type="text/javascript"
	src="<c:url value="/resources/js/custom/commentview.js" />">
</script>
<script type="text/javascript"
	src="<c:url value="/resources/js/custom/rating.js" />">
</script>
<script type="text/javascript"
	src="<c:url value="/resources/js/bootstrap-paginator.js" />">
</script>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.bootpag.min.js" />">
</script>


<!-- Modal Details Dialog -->
<div class="modal fade" id="mapItemDetails" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header mapItemDetails-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3 id="mapItemDetails-title">Title</h3>
        <h4 id="mapItemDetails-categories">Categories</h4>
        <p id="mapItemDetails-user">User</p>
        <div id="mapItemDetails-gallery"></div>
      </div>
      
      <div class="modal-header mapItemDetails-rating-wrapper">
      	<h4><span class="glyphicon glyphicon-star"></span> Rating</h4> 
      	<div class="mapItemDetails-rating-btn-wrapper">
      		
   			<div class="mapItemDetails-rating-btn">	
      			<button type="button" class="btn btn-success mapItemDetails-rating-up-btn rating-pos-btn"><span class="glyphicon glyphicon-thumbs-up"></span> </button>
      			<div class="mapItemDetails-rating-info mapItemDetails-rating-up-info rating-pos-info"></div>
      		</div>
      		<div class="mapItemDetails-rating-btn">	
      			<button type="button" class="btn btn-danger mapItemDetails-rating-down-btn rating-neg-btn"><span class="glyphicon glyphicon-thumbs-down"></span> </button>
      			<div class="mapItemDetails-rating-info mapItemDetails-rating-down-info rating-neg-info"></div>	
      		</div>	
      	</div>
      </div>
      
      <div class="modal-body">
         
         <div id="mapItemDetails-description-wrapper">
	         <h4><span class="glyphicon glyphicon-book"></span> Description</h4> 
	         <p id="mapItemDetails-description">Description</p>
	         <br />
         </div>
         
         

		
		 <div class="panel-group mapItemDetails-comment-wrapper">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h4 class="panel-title">
					<span class="glyphicon glyphicon-comment"></span> Comments
					</h4>
				</div>
				<div class="panel-body">
					 <div class="">
			         	<sec:authorize access="isAuthenticated()">
							<form id="mapItemDetails-comments-form" class="form-horizontal commentify-comments-form" role="form">
							  	<div class="form-group">
							    	
							    	<div class="col-sm-5">
							      		<textarea id="mapItemDetails-new-comment-text" class="form-control commentify-new-comment-text" rows="1" maxlength="300" placeholder="Your comment"></textarea>
							    	</div>
							    	<button type="button" class="btn btn-primary commentify-new-comment-btn" style="display:none">Post</button>
							  </div>
							</form>
					 	</sec:authorize>
			        	<div id="mapItemDetails-comment-list-wrapper">
				         	<ul id="mapItemDetails-comments" class="commentify-comments">
					                
					         	<li class="mapItemDetails-comment commentify-comment" style="display: none;">
					         	  <div class="comment-info">
					         	  		<span class="glyphicon glyphicon-user"></span> <strong><span class="comment-user commentify-comment-user">User</span></strong> 
					         	  		<span class="glyphicon glyphicon-time"></span> <span class="comment-time-ago commentify-comment-time-ago">Time ago</span>
					         	  </div>
					         	  
					         	  <i>"<span class="comment-text commentify-comment-text">Das ist super</span>"</i>
					         	  <hr />
					         	</li>
					         </ul>
					         <span id="mapItemDetails-no-comments commentify-no-comments" style="display: none;">Currently there are no comments</span>
				         	 <div id="paginator"></div>
				         </div>		         
				      </div>
				   </div>
				</div>
		 </div>
	      
	      
	      
	      
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->