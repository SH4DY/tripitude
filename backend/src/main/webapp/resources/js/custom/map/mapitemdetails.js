$(document).ready(function() {
	
	//init elements
	mapItemDetailsWindow.initFields();
	
	$(document).on('showMapItemDetails', function(e) {	
		mapItemDetailsWindow.initWithMapItem(e.id);	
	});
	
	$(document).on('commentAdded', function(e) {
		
		if (e.id == "mapItemDetails") {			
			mapItemDetailsWindow.initPagination();
		}
	});
	
	//load category properties
	jQuery.i18n.properties({
		name:'mapitem_categories', 
		path: baseVars.baseUrl + 'resources/messages/', 
		mode:'both', 
		callback: function() {
		}
	});
	
	//load messages
	jQuery.i18n.properties({
		name:'messages', 
		path: baseVars.baseUrl + 'resources/messages/', 
		mode:'both', 
		callback: function() {
		}
	});
});




/**
 * Mapitem Details View 
 */
var mapItemDetailsWindow = {
		
	mapItem : null,
	
	titleField : null,
	
	categoriesField : null,
	
	userField : null,
	
	descriptionField : null,
	
	galleryField : null,
	
	
	/**
	 * Init view wth mapitem
	 */
	initWithMapItem : function(mapItemId) {	
		this.getMapItem(mapItemId);
	},
	/**
	 * Init related fields
	 */
	initFields : function() {
		
		this.titleField = $('#mapItemDetails-title');	
		this.categoriesField = $('#mapItemDetails-categories');	
		this.userField = $('#mapItemDetails-user');	
		this.descriptionField = $('#mapItemDetails-description');
		this.galleryField = $('#mapItemDetails-gallery');
		
		$('body').on('shown.bs.modal', '#mapItemDetails', function () {
			
			element = $(document);
			$(element).height();
			$('#mapItemDetails .modal-body').css('max-heiht', $(element).height() - 
												$('#mapItemDetails .modal-content').offset().top * 2 - 
												$('#mapItemDetails .modal-header').outerHeight() - 
												$('#mapItemDetails .modal-footer').outerHeight() -												
												($('#mapItemDetails .modal-body').outerHeight() - 
												$('#mapItemDetails .modal-body').height())*2 );
			
			
		});
	},
	
	/**
	 * Get mapitem by id and fill view
	 */
	getMapItem : function (mapItemId) {
		
		jQuery.ajax({
	         type: "GET",
	         url: baseVars.baseUrl + "api/mapitem/" + mapItemId,
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	 
	        	mapItemDetailsWindow.mapItem = data;
	        	mapItemDetailsWindow.setFields();
	         },

	         error: function (jqXHR, status) {
	        	 alert('something went wrong');
	         }
		});
	},
	/**
	 * Set fields
	 */
	setFields : function() {
		
		//set title field
		var iconClass = 'icon-hotspot';
		if (this.mapItem.type.indexOf("Route") != -1) {
			iconClass = 'icon-route';
		}
		this.titleField.html(this.mapItem.title).addClass(iconClass);
		
		//set categories
		var categories = new Array();
		$.each(this.mapItem.categories, function (key, cat) {		
			categories.push(jQuery.i18n.prop('mapitem_categories.' + cat.name));
		});
		this.categoriesField.html(categories.join(', '));
		
		//set user
		this.userField.html( jQuery.i18n.prop('message.created_by_user', this.mapItem.user.name));
		
		//set description
		this.descriptionField.html(this.mapItem.description);
		
		var newCommentWrapper = $('.mapItemDetails-comment-wrapper').clone()
		$('.mapItemDetails-comment-wrapper').remove();
		$('#mapItemDetails-description-wrapper').after(newCommentWrapper);
		$('.mapItemDetails-comment-wrapper').removeClass('comment-processed');
		
		$('.mapItemDetails-comment-wrapper').commentify({
			postUrl : baseVars.baseUrl + "api/mapitem/" + mapItemDetailsWindow.mapItem.id + "/comment",
			comments : this.mapItem.comments,
			maxLength: 500,
			instanceName : 'mapItemDetails',
		});
		
		var newRatingWrapper = $('.mapItemDetails-rating-wrapper').clone()
		$('.mapItemDetails-rating-wrapper').remove();
		$('#mapItemDetails .mapItemDetails-header').after(newRatingWrapper);
		$('.mapItemDetails-rating-wrapper').removeClass('rating-processed');
		
		$('.mapItemDetails-rating-wrapper').rating({
			rateUrl : baseVars.baseUrl + "api/mapitem/" + mapItemDetailsWindow.mapItem.id + "/rating",
			currentUserRatingUrl : baseVars.baseUrl + 'api/mapitem/' + mapItemDetailsWindow.mapItem.id + '/rating/user',
			initialRating : mapItemDetailsWindow.mapItem.ratingCache
		});
		
		//init pagination
		mapItemDetailsWindow.initPagination();
		
		mapItemDetailsWindow.galleryField.html('');
		//set gallery
		$.each(mapItemDetailsWindow.mapItem.pictures, function(index, p) {
			
			var thumbHtml = '<span class="mapitem-thumb-wrapper image-thumb" rel="' + p.id + '"><a href="' + baseVars.baseUrl + p.location  + '" data-lightbox="gal">' +
			
			'<img class="mapitem-thumb" src="' + baseVars.baseUrl + p.thumbLocation + '">' + 
			'</a>' + '</span>';
			
			
			mapItemDetailsWindow.galleryField.append(thumbHtml);		
		});	
	},
	/**
	 * Init pagination
	 */
	initPagination : function() {
		
		$('#paginator').remove();
		$('#mapItemDetails-comment-list-wrapper').append('<div id="paginator"></div>');
		
		$('#mapItemDetails-comments li').hide().eq(1).show();
		
		$('#paginator').bootpag({
			total: $('#mapItemDetails-comments li').length-1,
			page: 1,
			maxVisible: 10 
        }).on("page", function(event, num){
        	$('#mapItemDetails-comments li').hide();
        	$('#mapItemDetails-comments li').eq(num).show();
        });
	}
	
};

