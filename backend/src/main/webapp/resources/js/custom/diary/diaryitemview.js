/**
 * Document ready listener
 */
$(document).ready(function() {
    
	//load category properties
	jQuery.i18n.properties({
		name:'mapitem_categories', 
		path:baseVars.baseUrl + '/resources/messages/', 
		mode:'both', 
		callback: function() {
		}
	});
	
	//load messages
	jQuery.i18n.properties({
		name:'messages', 
		path:baseVars.baseUrl + '/resources/messages/', 
		mode:'both', 
		callback: function() {
		}
	});	
	
	$(document).on('updateCurrentDiary', function(e) {
		diaryItemView.initWithDiaryId(e.id);
	});
	
	diaryItemView.init();

	//diaryItemView.initDiaryDropZone(3, $('#diaryitem-picture-dropzone'));
});	

/**
 * Diary View Object
 */
var diaryItemView = {
	
	currentDiary : null,
	diaryItemList : null,
	diaryItemListEmpty : null,
	diaryFormDeleteBtn : null,
	diaryIdToDelete : null,
	diaryDeleteModal : null,
	diaryItemFormWrapper : null,
	diaryItemInfoWrapper : null,
	diaryItemCreateCustomBtn : null,
	diaryItemSharingWrapper : null,
	diaryItemShareLink : null,
	diaryItemFbShareBtn : null,
	
	publicMode : false,
	accessHash : null,	
	uploadCounter : 0,
	uploadError : new Array(),
	
	eventListenerInitialized : false,
	
	/**
	 * Init
	 */
	init : function() {
		
		this.initFields();
		
	},
	
	/**
	 * Init with diary id
	 */
	initWithDiaryId : function(id) {
		
		this.initFields();

		diaryItemView.clearDiaryItemList();
		
		if (!this.publicMode) {
			if (id != null) {			
				diaryItemView.setCurrentDiary(id);
				diaryItemView.setCurrentDiary(id);
				this.initDiaryItemFormListener();
			}
			else {		
				diaryItemView.diaryItemCreateCustomBtn.hide();
				diaryItemView.diaryItemListEmpty.hide();	
				diaryItemView.diaryItemList.removeClass('rounded-corner-box').removeClass('no-items');				
			}
			//set sharing options
			this.initDroppable();
		}
		else {
			if (id != null) {			
				diaryItemView.setCurrentDiary(id);
				this.initDiaryItemFormListener();
			}
		}
		
	},
	/**
	 * Init fields
	 */
	initFields : function() {
		
		this.diaryItemList = $('#diaryitems-list');
		this.diaryItemListEmpty = $('#diaryitems-empty');
		this.diaryFormDeleteBtn = $('#do-delete-btn');
		this.diaryDeleteModal = $('#delete-modal');
		this.diaryItemFormWrapper = $('.diaryitem-form-wrapper');
		this.diaryItemInfoWrapper = $('.diaryitem-info');
		this.diaryItemCreateCustomBtn = $('#diaryitem-create-custom-btn');
		this.diaryItemSharingWrapper = $('#diaryitem-sharing-wrapper');
		this.diaryItemShareLink = $('#diaryitem-sharing-link');
		this.diaryItemFbShareBtn = $('.fb-share-button');
	},
	/**
	 * Init event listener 
	 */
	initDiaryItemFormListener: function() {
		
		if (diaryItemView.eventListenerInitialized) {
			return;
		}
		
		diaryItemView.eventListenerInitialized = true;
		
		$('body').on('click', '.diaryitem-delete-btn', function() {
			$('#delete-modal #myModalLabel').html(jQuery.i18n.prop('message.delete_diaryitem_title'));
			$('#delete-modal .modal-body p').html(jQuery.i18n.prop('message.delete_diaryitem_body'));
			$('#delete-modal #do-delete-btn').html(jQuery.i18n.prop('message.delete_diaryitem_title'));			
			$('#delete-modal').attr('rel', 'delete_diaryitem');
			diaryItemView.diaryIdToDelete = $(this).attr('rel');
		});
		
		diaryView.diaryFormDeleteBtn.click(function() {
			
			if ($('#delete-modal').attr('rel') == 'delete_diaryitem') {				
				diaryItemView.deleteDiaryItem(diaryItemView.diaryIdToDelete);
				diaryItemView.diaryIdToDelete = null;
			}
		});
		
		$('body').on('click', '.diaryitem-edit-btn', function() {			
			diaryItemView.openDiaryItemForm($(this).attr('rel'));
		});
		
		$('body').on('click', '.diaryitem-cancel-btn', function() {			
			diaryItemView.closeDiaryItemForm($(this).attr('rel'));
		});
		
		$('body').on('click', '.diaryitem-create-btn', function() {			
			diaryItemView.updateDiaryItem($(this).attr('rel'));
		});
		
		$('body').on('click', '#diaryitems .historyitem-mapitem-map', function() {
			$.event.trigger({
 				type: "showMapModalMapView",
 				id: $(this).attr('rel'),
 			});
		});
		
		$('body').on('click', '.diaryitem-image-remove', function() {		
						
			var args = $(this).attr('rel').split(',');
			diaryItemView.deleteDiaryItemPicture(args[0], args[1]);
		});
		
		$('body').on('click', '#diaryitem-create-custom-btn', function() {
			diaryItemView.addDiaryItem(null);
		});	
		
		$('body').on('click', '.historyitem-mapitem-details-link', function() {			
			
			var mapItemId = $(this).attr('rel');
			
			//trigger event
			$.event.trigger({
				type: "showMapItemDetails",
				id: mapItemId,
			});		
		});
		
		//upload by click listener
		$('body').on('change', ':file', function(evt) {
			
			var diaryItemId = $(this).attr('rel');
			
			var files = $(this).prop('files');
			
			var validItem = false;
    	    diaryItemView.uploadCounter = 0;
			
			for (var i = 0; i < files.length; i++) {
                (function (file) {
                    if (file.type.indexOf("image") == 0) {
                    	                   	
                        var fileReader = new FileReader();
                        fileReader.onload = function (f) {
                            
                        	validItem = true;
                        	diaryItemView.uploadCounter++;
                        	diaryItemView.uploadDiaryItemPicture(diaryItemId, f.target.result);
                        };
 
                        fileReader.readAsDataURL(file);
                    }
                })(files[i]);
            }
			
			if (validItem) {
            	$(element).addClass('diaryitem-picture-dropzone-uploading');
            	$('.diaryitem-picture-dropzone-text', $(element)).html(jQuery.i18n.prop('message.diaryitem_dropzone_uploading'));
            }
		});
		
		$('body').on('focus', '.diaryitem-text-input', function() {
			$(this).attr('rows', 6);
		});
		
		$('body').on('focusout', '.diaryitem-text-input', function() {
			$(this).attr('rows', 1);
		});
	},
	/**
	 * Init droppable zone
	 */
	initDroppable : function() {
		diaryItemView.diaryItemList.droppable({
		    
			hoverClass: 'drop-highlight',
			drop: function(event, ui) {
		        
				var historyItemId = ui.draggable.attr('rel');
		        
		        if (historyItemId > 0) {
		        	diaryItemView.addDiaryItem(historyItemId);
		        }
		    }
		});
	},
	/**
	 * Open diary edit form
	 */
	openDiaryItemForm : function(diaryItemId) {
		
		var diaryItemHtml = $('li.diaryitems-list-item[rel="' + diaryItemId + '"]');	
		
		$('.diaryitem-info', diaryItemHtml).fadeOut('fast', function() {
			$('.diaryitem-edit-btn', diaryItemHtml).hide();
			diaryItemHtml.addClass('opened');
			$('.diaryitem-form-wrapper', diaryItemHtml).slideDown('fast');			
		});
	},
	/**
	 * Close diary form
	 */
    closeDiaryItemForm : function(diaryItemId) {
		
		var diaryItemHtml = $('li.diaryitems-list-item[rel="' + diaryItemId + '"]');
				
		diaryItemHtml.removeClass('opened');
		$('.diaryitem-form-wrapper', diaryItemHtml).slideUp('fast', function() {
			$('.diaryitem-edit-btn', diaryItemHtml).show();
			$('.diaryitem-info', diaryItemHtml).fadeIn('fast');
		});
	},
	/**
	 * Update diary item by id
	 */
	updateDiaryItem : function(diaryItemId) {
		
		
		var diaryItemHtml = $('li.diaryitems-list-item[rel="' + diaryItemId + '"]');
		
		var diaryItem = new Object();
		diaryItem.name = $('.diaryitem-name-input', diaryItemHtml).val().length ? $('.diaryitem-name-input', diaryItemHtml).val() : 'tmp';
		diaryItem.text = $('.diaryitem-text-input', diaryItemHtml).val().length ? $('.diaryitem-text-input', diaryItemHtml).val() : 'tmp';
		
		//check if custom diaraitem
		d = diaryItemView.getDiaryItemByIdFromCurrentDiary(diaryItemId);
		if (d.historyItem == null) {
			diaryItem.time = moment($('.diaryitem-time-input-wrapper input', diaryItemHtml).val()).unix() + "000";
		}
		
		
		jQuery.ajax({
	         type: 'PUT',
	         url: baseVars.baseUrl + "api/diaryitem/" + diaryItemId + "/update",
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         data: JSON.stringify(diaryItem),
	         success: function (data, status, jqXHR) {
	        	 
	        	 diaryItemView.closeDiaryItemForm(diaryItemId);
	        	 diaryItemView.replaceDiaryItemInCurrentDiary(data);
	 			 diaryItemView.sortDiaryItems();
	 			 var position = diaryItemView.getDiaryItemPosition(data.id);
	        	 diaryItemView.appendDiaryItemToList(data, null, position, diaryItemHtml);
	        	 
	        	//scroll to item
        		 $('html, body').animate({
        			    scrollTop: $(diaryItemHtml).offset().top - 100
        		 }, 1000);
        		 
        		 diaryItemView.triggerUpdatedDiary();	        	 
	         },

	         error: function (jqXHR, status) {
	        	 alert(jqXHR.responseText);
	         }
		});
	},
	/**
	 * Delete diary item
	 */
	deleteDiaryItem : function(diaryItemId) {
		
		if (diaryItemId != null) {
			
			jQuery.ajax({
		         type: "DELETE",
		         url: baseVars.baseUrl + "api/diaryitem/" + diaryItemId + "/delete",
		         contentType: "application/json; charset=utf-8",
		         dataType: "json",
		         success: function (data, status, jqXHR) {
		        	 
		        	 
		        	 diaryItemView.diaryDeleteModal.modal('hide');		        	 		        	 
		        	 diaryItemView.removeDiaryItemFromToList(diaryItemId);
		        	 //StatusMessages.setMessage('success', jQuery.i18n.prop('message.diaryitem_deleted'), '2000');
		        	 diaryItemView.triggerUpdatedDiary();
		         },

		         error: function (jqXHR, status) {
		        	 diaryItemView.diaryDeleteModal.modal('hide');
		        	 alert('something went wrong');
		         }
			});
		}
	},
	/**
	 * Add/post diary item
	 */
	addDiaryItem : function(historyItemId) {
		
		if (diaryItemView.currentDiary == null) {
			return;
		}
		
		var newDiaryItem = new Object();
		newDiaryItem.name = 'tmp';
		newDiaryItem.text = 'tmp';
		
		var url = "api/diaryitem/" + diaryItemView.currentDiary.id + "/" + historyItemId;
		
		//custom diary item without history item
		if (historyItemId == null) {
			url = "api/diaryitem/" + diaryItemView.currentDiary.id;
		}
		
		jQuery.ajax({
	         type: "POST",
	         url: baseVars.baseUrl + url,
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         data: JSON.stringify(newDiaryItem),
	         success: function (data, status, jqXHR) {
	        	 
	        	 if (data != null && diaryItemView.currentDiary != null) {
	        		 diaryItemView.appendDiaryItemToDiary(data);
	        		 
	        		 //open edit form
	        		 diaryItemView.openDiaryItemForm(data.id);
	        		 //scroll to item
	        		 $('html, body').animate({
	        			    scrollTop: $('.diaryitems-list-item[rel="' + data.id  + '"]').offset().top - 100
	        		 }, 1000);
	        		 
	        		 diaryItemView.triggerUpdatedDiary();
	        	 }
	         },

	         error: function (jqXHR, status) {
	        	 StatusMessages.setMessage('danger', jQuery.i18n.prop('message.historyitem_already_added'), '2000');
	        	 //$('html, body').animate({scrollTop:0}, 'slow');
	         }
		});
	},
	/**
	 * 
	 */
	appendDiaryItemToDiary : function(diaryItem) {
		
		if (diaryItemView.currentDiary != null && diaryItem != null) {
			diaryItemView.currentDiary.diaryItems.push(diaryItem);
			diaryItemView.sortDiaryItems();
			
			//append item to right position
			$.each(diaryItemView.currentDiary.diaryItems, function(index, d) {
				
				if (d.id == diaryItem.id) {
					diaryItemView.appendDiaryItemToList(d, $($('.diaryitems-list-item')[0]), index, null);	
				}
			});
		}
	},
	/**
	 * Get diary item by id and set current diary item
	 */
	setCurrentDiary : function(id) {
		
		var url = baseVars.baseUrl + "api/diary/" + id;
		
		if (diaryView.publicMode && diaryView.accessHash != null) {
			url += '/' + diaryView.accessHash;
		}
		
		jQuery.ajax({
	         type: "GET",
	         url: url,
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	 
	        	 diaryItemView.currentDiary = data;
	        	 diaryItemView.sortDiaryItems();
	        	 diaryItemView.updateDiaryItemList();
	        	 
	        	 if (!diaryItemView.publicMode) {
	        		 diaryItemView.setSharing();
	        	 }
	        	 
	         },

	         error: function (jqXHR, status) {
	        	 alert('something went wrong');
	         }
		});
		
	},
	/**
	 * Sorts diary item array by time
	 */
	sortDiaryItems : function() {
		
		var toSort = diaryItemView.currentDiary.diaryItems;
		
		toSort.sort(function(a, b){
		    
			var a1= a.time, b1= b.time;
		    if(a1==b1) return 0;
		    return a1 < b1 ? -1: 1;
		});
		
		diaryItemView.currentDiary.diaryItems = toSort;
	},
	/**
	 * Update diary item view list
	 */
	updateDiaryItemList : function() {
		
		$('.diaryitems-list-item:not(:eq(0))').remove();
		var diaryItemTpl = $('.diaryitems-list-item');
		$('.diaryitems-list-item').hide();
		diaryItemView.diaryItemCreateCustomBtn.show();
		
		if (diaryItemView.currentDiary.diaryItems.length <= 0) {
			diaryItemView.diaryItemListEmpty.show();
			diaryItemView.diaryItemList.addClass('rounded-corner-box no-items');
			return;
		}
	
		$.each(diaryItemView.currentDiary.diaryItems, function(index, d) {
			diaryItemView.appendDiaryItemToList(d, diaryItemTpl, null, null);
		});
		
		
	},
	/**
	 * Set sharing options
	 */
	setSharing : function() {
		
		
		jQuery.ajax({
	         type: "GET",
	         url: baseVars.baseUrl + 'api/diary/' + diaryItemView.currentDiary.id + '/accesshash',
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	 
	        	 if (data != null) {
	        		 var sharingUrl = baseVars.baseUrl + 'diary/' + diaryItemView.currentDiary.id + '/' + data;
	        		 diaryItemView.diaryItemShareLink.attr('href', sharingUrl).html(sharingUrl);
	        		 diaryItemView.diaryItemFbShareBtn.attr('data-href', sharingUrl);
	        		// FB.XFBML.parse();
	        	 }
	         },

	         error: function (jqXHR, status) {
	        	 
	        	 //$('html, body').animate({scrollTop:0}, 'slow');
	         }
		});
		
		diaryItemView.diaryItemSharingWrapper.show();
	},
	/**
	 * Append diary item to lists
	 */
	appendDiaryItemToList : function(diaryItem, diaryItemTpl, position, diaryItemHtmlToUpdate) {
		
		var d = diaryItem;
		
	    diaryItemHtml = null;
		if (diaryItemHtmlToUpdate != null) {
			diaryItemHtml = diaryItemHtmlToUpdate;
		}
		else if (diaryItemTpl != null) {
			diaryItemHtml = diaryItemTpl.clone();
		}
		
		if (diaryItem == null) {
			return;
		}
		
		$('.diaryitem-time', diaryItemHtml).html($.format.date(d.time, "yyyy-MM-dd H:mm"));
		
		if (d.name != '' && d.name != 'tmp') {			
			$('.diaryitem-name', diaryItemHtml).html(d.name);
			$('.diaryitem-name-input', diaryItemHtml).val(d.name);
		}
		else {
			$('.diaryitem-name', diaryItemHtml).html("");
			$('.diaryitem-name-input', diaryItemHtml).val("");
		}
		
		if (d.text != '' && d.text != 'tmp') {					
			$('.diaryitem-text', diaryItemHtml).html(Helper.nl2br(d.text));
			$('.diaryitem-text-input', diaryItemHtml).val(d.text);
			$('.diaryitem-text-item', diaryItemHtml).show();
		}
		else {
			$('.diaryitem-text', diaryItemHtml).html("");
			$('.diaryitem-text-input', diaryItemHtml).val("");
			$('.diaryitem-text-item', diaryItemHtml).hide();
		}
		//init upload dropzone
		diaryItemView.initDiaryItemDropZone(d.id, $('.diaryitem-picture-dropzone', diaryItemHtml));
		
		
		if (d.historyItem != null) {
			
			
			$('.historyitem-mapitem-title', diaryItemHtml).html(Helper.truncate(d.historyItem.mapItem.title, 100));
			$('.historyitem-mapitem-map', diaryItemHtml).attr('src', MapUtil.getStaticMapUrlByCoordinates(d.historyItem.mapItem.coordinate.latitude, d.historyItem.mapItem.coordinate.longitude, 220, 120, 13));
			$('.historyitem-mapitem-map', diaryItemHtml).attr('rel', d.historyItem.mapItem.id);
			$('.historyitem-mapitem-details-link', diaryItemHtml).attr('rel', d.historyItem.mapItem.id);
		
			var iconClass = 'icon-hotspot';
			if (d.historyItem.mapItem.type.indexOf("Route") != -1) {
				iconClass = 'icon-route';
			}
			$('.historyitem-mapitem-title', diaryItemHtml).addClass(iconClass);
			$('.diaryitem-time-input-wrapper', diaryItemHtml).remove();
		}
		else {
			$('.diary-historyitem', diaryItemHtml).hide();
			$('.diaryitem-time-input-wrapper input', diaryItemHtml).val($.format.date(d.time, "yyyy-MM-dd H:mm"));
		}
		
		$('.diaryitem-edit-btn', diaryItemHtml).attr('rel', d.id);
		$('.diaryitem-delete-btn', diaryItemHtml).attr('rel', d.id);
		$('.diaryitem-cancel-btn', diaryItemHtml).attr('rel', d.id);
		$('.diaryitem-create-btn', diaryItemHtml).attr('rel', d.id);
		$('.diaryitem-form', diaryItemHtml).attr('rel', d.id);
		$('.diaryitem-picture-input', diaryItemHtml).attr('rel', d.id);
		diaryItemHtml.attr('rel', d.id);
			
		
		
		
		if (position == null) {			
			diaryItemView.diaryItemList.append(diaryItemHtml);
			diaryItemHtml.show();
		}
		else {
			diaryItemHtml.remove();
			//remove class if only update
			$('.diaryitem-comments-form-wrapper', diaryItemHtml).removeClass('comment-processed');
			diaryItemView.diaryItemList.children().each(function(index) {
				
				if (position == index) {
					$(this).after(diaryItemHtml);
					diaryItemHtml.fadeIn();
				}
			});
		}

		//init comments
		$('.diaryitem-comments-form-wrapper', diaryItemHtml).commentify({
			postUrl : baseVars.baseUrl + "api/diaryitem/" + d.id + "/comment",
			comments : d.comments
		});
		
		//initDatepicker
		$('.diaryitem-time-input', diaryItemHtml).datetimepicker();
		
		//init-collapsable
		var collapsableId = 'comment-accordion-' + d.id;
		$('.comment-accordion', diaryItemHtml).attr('id', collapsableId);
		$('.comment-accordion .panel-heading a', diaryItemHtml).attr('data-parent', '#' + collapsableId).attr('href', '#' + collapsableId + '-link');
		$('.collapse', diaryItemHtml).attr('id', collapsableId + '-link').collapse('hide');
		
		
		
		if (diaryItemHtmlToUpdate != null) {
			return;
		}
		
		//init thumbs
		diaryItemView.updateDiaryItemGallery(d.id, d.pictures);
		
		//init maxlength plugin
		$('.diaryitem-name-input', diaryItemHtml).maxlength({   
			maxCharacters: 100,
			slider: true // True Use counter slider    
		});
		$('.diaryitem-text-input', diaryItemHtml).maxlength({   
			maxCharacters: 1000,
		    slider: true // True Use counter slider    
		});
		
		
		
		diaryItemView.diaryItemListEmpty.hide();	
		diaryItemView.diaryItemList.removeClass('rounded-corner-box').removeClass('no-items');
	},
	/**
	 * Remove diary item from view list
	 */
	removeDiaryItemFromToList : function(diaryItemId) {
		
		var indexToDelete = null;
		$.each(diaryItemView.currentDiary.diaryItems, function(index, d) {
			
			if (diaryItemView.currentDiary.diaryItems[index].id == diaryItemId) {				
				indexToDelete = index;
			}
		});
				
		//delete item from object array
		if (indexToDelete != null) {
			diaryItemView.currentDiary.diaryItems.splice(indexToDelete, 1 );
		}
		diaryItemView.diaryItemList.children().each(function(index) {
			
			if ($(this).attr('rel') == diaryItemId) {
				$(this).fadeOut('fast', function() {
					$(this).remove();
					
					if (diaryItemView.currentDiary.diaryItems.length <= 0) {
						diaryItemView.diaryItemListEmpty.show();
						diaryItemView.diaryItemList.addClass('rounded-corner-box no-items');
					}
				});
			}
		});
	},
	/**
	 * Remove all diary items from view list
	 */
	clearDiaryItemList : function() {
		$('.diaryitems-list-item:not(:eq(0))').remove();
		$('.diaryitems-list-item').hide();
	},
	/*
	 * Init image drop zone behaviours
	 */
	initDiaryItemDropZone : function(diaryItemId, element) {
		
		if ($(element).hasClass('file-drop-processed')) {
			return;
		}
		$(element).addClass('file-drop-processed');
		
		$(element).fileDrop({
            decodeBase64 : false,
            removeDataUriScheme : false,
            onFileRead : function(fileCollection){
                    
                    //Loop through each file that was dropped
            	    var validItem = false;
            	    diaryItemView.uploadCounter = 0;
                    $.each(fileCollection, function(i){
                    	
                    	//if(this.type.indexOf('image') >= 0) {
                    		diaryItemView.uploadCounter++;
                    		validItem = true;
                    		diaryItemView.uploadDiaryItemPicture(diaryItemId, this.data, this.name);
                       
                    });
                    
                    if (validItem) {
                    	$(element).addClass('diaryitem-picture-dropzone-uploading');
                    	$('.diaryitem-picture-dropzone-text', $(element)).html(jQuery.i18n.prop('message.diaryitem_dropzone_uploading'));
                    }
            },
		    overClass: 'drag-over',
		});
	},
	
	/**
	 * Upload picture
	 */
	uploadDiaryItemPicture : function(diaryItemId, picture, name) {
		
	
		jQuery.ajax({
	         type: "POST",
	         url: baseVars.baseUrl + "api/diaryitem/" + diaryItemId + "/file",
	         contentType: "application/json; charset=utf-8",
	         data: JSON.stringify(picture.split(',')[1]),
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	 diaryItemView.uploadCounter--;
	        	 diaryItemView.updateDiaryItemGallery(diaryItemId, [data]);
	         },

	         error: function (jqXHR, status) {
	        	 diaryItemView.uploadCounter--;
	        	 diaryItemView.updateDiaryItemGallery(diaryItemId, []);
	        	 
	        	 StatusMessages.setMessage('danger', jQuery.i18n.prop('message.file_could_not_be_uploaded', name), '2000');
	         }
		});
	},
	/**
	 * Update gallery view
	 */
	updateDiaryItemGallery : function(diaryItemId, pictures) {
		
		var diaryItemHtml = $('li.diaryitems-list-item[rel="' + diaryItemId + '"]');
		
		$.each(pictures, function(index, p) {
			
			var classes = new Array('.diaryitem-gallery-edit', '.diaryitem-gallery');
			
			$.each(classes, function(index, clasz) {
				var thumbHtml = '<span class="diaryitem-thumb-wrapper image-thumb" rel="' + p.id + '"><a href="' + baseVars.baseUrl + p.location  + '" data-lightbox="' + clasz + diaryItemId + '">' +
				
				'<img class="diaryitem-thumb" src="' + baseVars.baseUrl + p.thumbLocation + '">' + 
				'</a>' + '<span class="diaryitem-image-remove" rel="' + diaryItemId + ',' + p.id + '"></span></span>';
				
				
				$(clasz, diaryItemHtml).append(thumbHtml);
			});			
		});
		
		if (diaryItemView.uploadCounter <= 0) {
			$('.diaryitem-picture-dropzone', diaryItemHtml).removeClass('diaryitem-picture-dropzone-uploading');
			$('.diaryitem-picture-dropzone-text', diaryItemHtml).html(jQuery.i18n.prop('message.diaryitem_dropzone_text'));
   	 	}
	},
	
	deleteDiaryItemPicture : function(diaryItemId, pictureId) {
				
		jQuery.ajax({
	         type: "DELETE",
	         url: baseVars.baseUrl + "api/diaryitem/" + diaryItemId + "/file/" + pictureId + "/delete",
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	         	 
	        	 $('.diaryitems-list-item[rel="' + diaryItemId + '"] .diaryitem-thumb-wrapper[rel="' + pictureId + '"]').fadeOut(function() {
	        		 $(this).remove();
	        	 });
	         },

	         error: function (jqXHR, status) {
	        	 diaryItemView.diaryDeleteModal.modal('hide');
	        	 alert('something went wrong');
	         }
		});
	},
	
	getDiaryItemByIdFromCurrentDiary : function(id) {
		
		var diaryItem = null;
		$.each(diaryItemView.currentDiary.diaryItems, function(index, d) {
			
			if (d.id == id) {				
				diaryItem = d;
			}
		});
		
		return diaryItem;
	},
	
	replaceDiaryItemInCurrentDiary : function(diaryItem) {
		
		$.each(diaryItemView.currentDiary.diaryItems, function(index, d) {	     			
 			if (d.id == diaryItem.id) {				
 				diaryItemView.currentDiary.diaryItems[index] = diaryItem;
 			}
 		});
	},
	
	getDiaryItemPosition : function(id) {
		
		var position = null;
		$.each(diaryItemView.currentDiary.diaryItems, function(index, d) {	     			
 			if (d.id == id) {				
 				position = index;
 			}
 		});
		return position;
	},
	
	triggerUpdatedDiary : function(id) {
		$.event.trigger("updatedDiaryItem",[diaryItemView.currentDiary.diaryItems]);
	}

};
