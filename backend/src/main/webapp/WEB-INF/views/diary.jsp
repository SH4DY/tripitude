<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?libraries=geometry&sensor=false"></script>
<script type="text/javascript" src="<c:url value="/resources/js/custom/map/maputil.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/custom/map/mapview.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/custom/map/modalmapview.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery-ui-1.10.3.custom.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jQuery.FileDrop.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/moment-with-langs.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/datetimepicker/js/bootstrap-datetimepicker.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery-dateFormat.min.js" />"></script>


<c:if test="${publicMode == null}">
	<script type="text/javascript" src="<c:url value="/resources/js/custom/diary/historyitemview.js" />"></script>
</c:if>

<script type="text/javascript" src="<c:url value="/resources/js/custom/diary/diaryview.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/custom/diary/diaryitemview.js" />"></script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/diary.css" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/map.css" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/js/datetimepicker/css/bootstrap-datetimepicker.min.css" />">

<!-- Init views sccording to mode -->
<c:choose>
	<c:when test="${publicDiaryId != null && accessHash != null}">
		<script type="text/javascript">	
			$(document).ready(function() {
			      diaryView.publicMode = true;
			      diaryItemView.publicMode = true;	      
			      diaryView.accessHash = "${accessHash}";
			      diaryItemView.accessHash = "${accessHash}";
			      diaryView.initWithDiaryId("${publicDiaryId}");
			});
		</script>
	</c:when>
	<c:otherwise>
        <script type="text/javascript">	
			$(document).ready(function() {
			      diaryView.init();	
			});
		</script>
    </c:otherwise>
</c:choose>

<div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/all.js#xfbml=1&appId=658229330890201";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>

<c:if test="${publicMode == null}">
	<div id="historyitems" class="rounded-corner-box col-md-3">
		<h4>Trip timeline</h4>
		<hr />
		<div id="historyitems-scroll-pane">
			<span id="historyitems-empty" style="display: none;">Currently
				there is no timeline entry</span>
			<ul id="historyitems-list">
				<li class="historyitems-list-item rounded-corner-box"
					style="display: none">
					<div class="historyitems-list-item-info">
						<span class="glyphicon glyphicon-time"></span> <span
							class="historyitem-time"></span>
						<div>
							<a href="#" class="historyitem-mapitem-details-link"
								data-toggle="modal" data-target="#mapItemDetails"> <span
								class="historyitem-mapitem-title"></span>
							</a>
						</div>
						<img class="historyitem-mapitem-map pull-right" />
					</div>
				</li>
			</ul>
		</div>
	</div>
</c:if>

<div id="diaries" class="rounded-corner-box col-md-8 <c:if test="${publicMode != null}">public-mode</c:if>">

	<c:if test="${publicMode == null}">
		
		<div id="diaries-bar">
			<ul class="nav nav-pills">
				<li>
					<h4>Trip diaries</h4>
				</li>
				<li>
				
				<select id="diary-select" class="form-control">
						<option value="null">Select a diary</option>
				</select></li>
				<li>
					<button type="button" id="diary-open-form-btn" class="btn btn-primary"><span class="glyphicon glyphicon-plus"></span> Create new diary</button>
				</li>
			</ul>
		</div>
	
		<div id="diaries-form-wrapper" style="display: none;">
			<form id="diary-form" role="form">
				<div class="form-group">
					<label for="diary-name-input">Title</label>
					<input id="diary-name-input" class="form-control" placeholder="Enter a title" maxlength="30"></input>		
				</div>
				
				<div class="form-group">	
					<label for="diary-description-input">Description</label>
				    <textarea id="diary-description-input" class="form-control"
						rows="4" maxlength="500" placeholder="Enter a description"></textarea>			
				</div>
				
				<div class="form-group button-group">
					<button id="diary-create-cancel-btn" type="button" class="btn btn-default">Cancel</button>
					<button id="diary-create-btn" type="button" class="btn btn-primary">Create diary</button>
				</div>
			</form>
	
		</div>
	</c:if>
	
	<c:if test="${publicMode != null}">
		<h4>Trip diary <span class="diary-creator">by ${diaryCreator}</span></h4>
		<hr />
	</c:if>
	
	
	<div id="diary-info" style="display: none;">
		<c:if test="${publicMode == null}">
			<hr />
			<span id="diaries-empty" style="display: none;">Select or create a diary</span>
			<div id="diary-action-buttons">
		        <button id="diary-edit-btn" type="button" class="btn btn-primary btn-xs"><span class="glyphicon glyphicon-pencil"></span> Edit diary info</button>
		        <button id="diary-delete-btn" type="button" class="btn btn-danger btn-xs" data-toggle="modal" data-target="#delete-modal"><span class="glyphicon glyphicon-remove"></span> Delete diary</button>
			</div>
        </c:if>
		<h4 id="diary-name" >Name</h4>
		<h5 id="diary-period"></h5>
		<p><span id="diary-description">Description</span></p>
	</div>

	<div id="diaryitems" style="">
		<c:if test="${publicMode == null}">
			
			
			<div id="diary-item-actions">
			<button type="button" id="diaryitem-create-custom-btn" class="btn btn-primary" style="display: none;"><span class="glyphicon glyphicon-plus"></span> Create custom diary entry</button>
			<div id="diaryitem-sharing-wrapper" style="display: none;">
				<strong><span class="glyphicon glyphicon-share"></span> Share you diary:</strong> 
				<a id="diaryitem-sharing-link" href="" target="_blank"></a>  
				<div class="fb-share-button" data-href="http://www.gmx.net" data-type="button"></div>
			</div>
			</div>
			
			<span id="diaryitems-empty" style="display: none;"><i>Add a timline item to the diary by drag and drop.</i></span>
		</c:if>
		
		
		<ul id="diaryitems-list">
			<li class="diaryitems-list-item rounded-corner-box" style="display: none">
				<div class="diaryitems-list-item-info">
					
					<span class="glyphicon glyphicon-time"></span> 
					<span class="diaryitem-time"></span>		
					<img class="historyitem-mapitem-map pull-right" />
					
					<c:if test="${publicMode == null}">					
						<button type="button" class="diaryitem-delete-btn btn btn-danger btn-xs pull-right" data-toggle="modal" data-target="#delete-modal"><span class="glyphicon glyphicon-remove"></span></button>					
						<button type="button" class="diaryitem-edit-btn btn btn-primary btn-xs pull-right"><span class="glyphicon glyphicon-pencil"></span></button>
					</c:if>
					
					<div class="diary-historyitem">
						<div>
							<a href="#" class="historyitem-mapitem-details-link"
								data-toggle="modal" data-target="#mapItemDetails"> <span
								class="historyitem-mapitem-title"></span>
							</a>
						</div>
						
					</div>
					<div class="diaryitem-info">
						<p>
							<h4><span class="diaryitem-name"></span></h4>						
						</p>
											
						<div class="diaryitem-gallery"> </div>
					 
						
						<p style="clear: left;">
							<span class="glyphicon glyphicon-book diaryitem-text-item" style="display: none"></span> 
							<span class="diaryitem-text"></span>
						</p>	
					</div>
					
						
					<c:if test="${publicMode == null}">
					
						<div class="diaryitem-form-wrapper" style="display: none;">
							<form class="diaryitem-form" role="form">
								
								<div class="form-group diaryitem-time-input-wrapper">
									
									<label for="diary-description-input">Date/Time</label>
									<div class='input-group date diaryitem-time-input'>
					                    <input type='text' class="form-control" data-format="YYYY-MM-DD hh:mm" name="2"/>
					                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
					                    </span>
					                </div>
								</div>
													
								<div class="form-group">
									<label for="diaryitem-name-input-id">Title</label>
									<input class="form-control diaryitem-name-input" placeholder="Enter a title" maxlength="30" name="1"></input>		
								</div>
								
								<div class="form-group">
									<label>Pictures</label>
									<div class="diaryitem-picture-dropzone ">
									<div class="diaryitem-gallery-edit"></div>
									
										<div class="diaryitem-picture-dropzone-text-wrapper">
											<span class="glyphicon glyphicon-picture"></span> 
											<span class="diaryitem-picture-dropzone-text">Drop a picture to upload (JPG, PNG, GIF)</span>
										</div>
									</div>
									<input class="diaryitem-picture-input" type="file" name="img" multiple style="display: none">
								</div>
						
								
								
								<div class="form-group">	
									<label for="diaryitem-text-input-id">Text</label>
								    <textarea  class="form-control diaryitem-text-input"
										rows="1" maxlength="1000" placeholder="Enter a text"></textarea>			
								</div>
								
								<div class="form-group button-group">
									<button type="button" class="diaryitem-cancel-btn btn btn-default">Cancel</button>
									<button type="button" class="diaryitem-create-btn btn btn-primary">Save diary entry</button>
								</div>
							</form>
					
						</div>
					</c:if>
					
					
					<hr />
		        
		         	<div class="panel-group comment-accordion">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" data-parent="#accordion"
										href="#collapseOne"><span class="glyphicon glyphicon-comment"></span> Comments</a>
								</h4>
							</div>
							<div id="collapseOne" class="panel-collapse collapse in">
								<div class="panel-body">


									<div class="diaryitem-comments-form-wrapper">
										<form
											class="form-horizontal diaryitem-comments-form commentify-comments-form"
											role="form">

											<sec:authorize access="!hasRole('AUTHENTICATED')">
												<div class="form-group">
													<div class="col-sm-3">
<!-- 														<label>Name</label>  -->
														<input class="form-control diaryitem-new-comment-name commentify-new-comment-name"
															placeholder="Enter your name" maxlength="30"></input>
													</div>
												</div>
											</sec:authorize>
											<div class="form-group">
												<div class="col-sm-7">
<!-- 													<label>Text</label> -->
													<textarea
														class="form-control diaryitem-new-comment-text commentify-new-comment-text"
														rows="1" maxlength="300" placeholder="Enter a comment"></textarea>
												</div>
												<button type="button"
													class="btn btn-primary diaryitem-new-comment-btn commentify-new-comment-btn"
													style="display: none">Post</button>
											</div>
										</form>

										<ul class="diaryitem-comments commentify-comments">

											<li class="diaryitem-comment commentify-comment comment"
												style="display: none;">
												<div class="comment-info">
													<span class="glyphicon glyphicon-user"></span> <strong><span
														class="comment-user commentify-comment-user">User</span></strong> <span
														class="glyphicon glyphicon-time"></span> <span
														class="comment-time-ago commentify-comment-time-ago">Time
														ago</span>
												</div> 
												
												<i>"<span class="comment-text commentify-comment-text">Das ist super</span>"</i>
				         	 					<hr />
											</li>
										</ul>
										<span class="diaryitem-no-comments commentify-no-comments"
											style="display: none;">Currently there are no comments</span>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</div>
</div>


<div class="modal fade" id="delete-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Delete diary</h4>
      </div>
      <div class="modal-body">
        <p>Do you really want to delete this diary?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" id="do-delete-btn" class="btn btn-danger">Delete diary</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="map-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Map</h4>
      </div>
      <div class="modal-body">
        <div id="map-wrapper">
			<div id="map_canvas" style="width:100%; height:500px;"></div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<%@ include file="mapitemdetails.jsp"%>






