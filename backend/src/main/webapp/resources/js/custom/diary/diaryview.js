/**
 * Document ready listener
 */
$(document).ready(function() {
    
	//load category properties
	jQuery.i18n.properties({
		name:'mapitem_categories', 
		path:baseVars.baseUrl + 'resources/messages/', 
		mode:'both', 
		callback: function() {
		}
	});
	
	//load messages
	jQuery.i18n.properties({
		name:'messages', 
		path:baseVars.baseUrl + 'resources/messages/', 
		mode:'both', 
		callback: function() {
		}
	});	
	
	
	$(document).on('updatedDiaryItem', function(e, diaryItems) {
		diaryView.currentDiary.diaryItems = diaryItems;
		diaryView.updateDiaryInfo();
		
	});
});	

/**
 * Diary View Object
 */
var diaryView = {
	
	diarySelect : null,
	
	diaryOpenDiaryFormBtn : null, 
	
	diaryOpenDiaryEditFormBtn : null, 
	
	diaryFormCancelBtn : null,
	
	diaryFormSubmitBtn : null,
	
	diaryFormNameInput : null,
	
	diaryFormDeleteBtn : null,
	
	diaryFormDescriptionInput : null,
	
	diaryName : null,
	
	diaryPeriod : null,
	
	diaryDescription : null,
	
	currentDiary : null,
	
	diaryDeleteModal : null,
	
	publicMode : false,
	
	accessHash : null,
		
	init : function() {
		
		if (this.publicMode) {
			return;
		}
		
		this.initFields();
		this.getDiaries();
		this.initDiaryFormListener();
	},
	
	initWithDiaryId : function(id) {
		
		this.initFields();
		this.getDiary(id);	
		
		if (diaryView.publicMode) {
			this.initDiaryFormListener();						
		}
	},
	
	initFields : function() {
		
		this.diarySelect = $('#diary-select');
		this.diaryOpenDiaryFormBtn = $('#diary-open-form-btn');
		this.diaryOpenDiaryEditFormBtn = $('#diary-edit-btn');
		this.diaryFormCancelBtn = $('#diary-create-cancel-btn');
		this.diaryFormSubmitBtn = $('#diary-create-btn');
		this.diaryFormNameInput = $('#diary-name-input');
		this.diaryFormDeleteBtn = $('#do-delete-btn');
		this.diaryFormDescriptionInput = $('#diary-description-input');
		this.diaryName = $('#diary-name');
		this.diaryPeriod = $('#diary-period');
		this.diaryDescription = $('#diary-description');
		this.diaryDeleteModal = $('#delete-modal');
		
				
		//init maxlength plugin
		this.diaryFormNameInput.maxlength({   
			maxCharacters: 30,
		    slider: true // True Use counter slider    
		});
		
		this.diaryFormDescriptionInput.maxlength({   
			maxCharacters: 500,
		    slider: true // True Use counter slider    
		});
		
	},
	
	initDiaryFormListener : function() {
		
		diaryView.diaryOpenDiaryFormBtn.click(function() {
			diaryView.openDiaryForm('create');
		});
		
		diaryView.diaryOpenDiaryEditFormBtn.click(function() {
			diaryView.openDiaryForm('update');
		});
		
		diaryView.diaryFormCancelBtn.click(function() {		
			diaryView.closeDiaryForm();
		});
		
		diaryView.diaryFormSubmitBtn.click(function() {			
			diaryView.updateSubmitDiary();
		});
		
		
		diaryView.diaryFormNameInput.keyup(function() {
			if (diaryView.diaryFormNameInput.val().length > 0) {
				diaryView.diaryFormSubmitBtn.prop('disabled', false);
			}
			else {
				diaryView.diaryFormSubmitBtn.prop('disabled', true);
			}
		});
		
		diaryView.diarySelect.change(function() {
			
			if ($(this).val() > 0) {
				diaryView.closeDiaryForm();
				diaryView.setCurrentDiary($(this).val());
			}
			else {
				
				if (diaryView.currentDiary != null) {					
					diaryView.diarySelect.val(diaryView.currentDiary.id);
				}
				else {
					diaryView.diarySelect.val('null');
				}
			}
		});
		
		
		$('body').on('click', '#diary-delete-btn', function() {
			$('#delete-modal #myModalLabel').html(jQuery.i18n.prop('message.delete_diary_title'));
			$('#delete-modal .modal-body p').html(jQuery.i18n.prop('message.delete_diary_body'));
			$('#delete-modal #do-delete-diary-btn').html(jQuery.i18n.prop('message.delete_diary_title'));
			$('#delete-modal').attr('rel', 'delete_diary');
		});
		
		diaryView.diaryFormDeleteBtn.click(function() {
			
			if ($('#delete-modal').attr('rel') == 'delete_diary') {				
				diaryView.deleteCurrentDiary();
			}
		});
	},
	
	updateSubmitDiary : function() {
		
		
		var newDiary = new Object();
		newDiary.name = diaryView.diaryFormNameInput.val();
		newDiary.description = diaryView.diaryFormDescriptionInput.val();
		
		url = 'api/diary';
		method = 'POST';
		if (diaryView.diaryFormSubmitBtn.attr('rel') == 'update') {
			
			if (diaryView.currentDiary != null)
			
			url = 'api/diary/' + diaryView.currentDiary.id + '/update';
			method = 'PUT';
		}
		
		jQuery.ajax({
	         type: method,
	         url: baseVars.baseUrl + url,
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         data: JSON.stringify(newDiary),
	         success: function (data, status, jqXHR) {

	        	 diaryView.currentDiary = data;
	        	 diaryView.getDiaries();
	        	 diaryView.closeDiaryForm();        	 
	        	 diaryView.updateDiaryInfo();
	        	 diaryView.diaryFormSubmitBtn.attr('rel', '');
	        	 
	        	 $.event.trigger({
	 				type: "updateCurrentDiary",
	 				id: data.id,
	 		     });
	         },

	         error: function (jqXHR, status) {
	        	 alert(jqXHR.responseText);
	         }
		});
	},
	
	closeDiaryForm : function() {
		
		$('#diaries-form-wrapper').slideUp("fast");
		
		diaryView.diaryFormNameInput.val("");
		diaryView.diaryFormDescriptionInput.val("");
		
		diaryView.diaryOpenDiaryFormBtn.show();
		
		if (diaryView.currentDiary != null) {
			$('#diary-info').show();
		}
	},
	
	openDiaryForm : function(mode) {
			
		
		if (mode == 'create') {
			$('#diary-info').hide();
			diaryView.diaryFormSubmitBtn.html(jQuery.i18n.prop('message.create_new_diary'));
		}
		else if (mode == 'update'){
			diaryView.diaryFormSubmitBtn.attr('rel', 'update');	
			diaryView.diaryFormSubmitBtn.html(jQuery.i18n.prop('message.update_diary_info'));
			diaryView.diaryFormNameInput.val(diaryView.currentDiary.name);
			diaryView.diaryFormDescriptionInput.val(diaryView.currentDiary.description);
		}
		
		if (diaryView.diaryFormNameInput.val().length <= 0) {
			diaryView.diaryFormSubmitBtn.prop('disabled', true);
		}
		else {
			diaryView.diaryFormSubmitBtn.prop('disabled', false);
		}
		$('#diaries-form-wrapper').slideDown("fast");
		diaryView.diaryOpenDiaryFormBtn.hide();
	},
	
	getDiaries : function() {
		
		jQuery.ajax({
	         type: "GET",
	         url: baseVars.baseUrl + "api/user/diary",
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	 
	        	 diaryView.updateDiaryFormSelect(data);
	         },

	         error: function (jqXHR, status) {
	        	 alert('something went wrong');
	         }
		});
	},
	
	getDiary : function(id) {
		
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
	        	 
	        	 diaryView.currentDiary = data;
	        	 diaryView.updateDiaryInfo();
	        	 
	        	 $.event.trigger({
	 				type: "updateCurrentDiary",
	 				id: diaryView.currentDiary.id,
	 		     });	
	         },

	         error: function (jqXHR, status) {
	        	 alert('something went wrong');
	         }
		});
	},
	
	updateDiaryInfo : function() {
		
		if (diaryView.currentDiary != null) {
			
			diaryView.diaryName.html(diaryView.currentDiary.name);
			
			var period = diaryView.getDiaryPeriod();
			if (period.length) {				
				diaryView.diaryPeriod.html(jQuery.i18n.prop('message.diary_period', period[0], period[1]));
			}
			else {
				diaryView.diaryPeriod.html('');
			}
			
			diaryView.diaryDescription.html(Helper.nl2br(diaryView.currentDiary.description)); 
			$('#diary-info').show();
		}
		else {
			$('#diary-info').hide();
		}
	},
	
	getDiaryPeriod : function() {
				
		var ret = new Array();
		if (diaryView.currentDiary != null &&
		    diaryView.currentDiary.diaryItems.length > 0) {
			
			var max = 0;
			var min = Number.MAX_VALUE;
			
			for (var i = 0; i < diaryView.currentDiary.diaryItems.length ; i++) {
				
				var d = diaryView.currentDiary.diaryItems[i];
				if (d.time > max) {
					max = d.time;
				}
				if (d.time < min) {
					min = d.time;
				}
			}
			
			var format = "yyyy-MM-dd";
			ret.push($.format.date(min, format));
			ret.push($.format.date(max, format));
		}
		
		return ret;
	},
	
	setCurrentDiary : function(id) {
		
	
		jQuery.ajax({
	         type: "GET",
	         url: baseVars.baseUrl + "api/diary/" + id,
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	 
	        	 diaryView.currentDiary = data;
	        	 diaryView.updateDiaryInfo();
	        	 
	        	 $.event.trigger({
	 				type: "updateCurrentDiary",
	 				id: diaryView.currentDiary.id,
	 		     });	
	         },

	         error: function (jqXHR, status) {
	        	 alert('something went wrong');
	         }
		});
		
	},
	
	updateDiaryFormSelect : function(diaries) {
		
		
		diaryView.diarySelect.html(diaryView.diarySelect.children(0)[0]);
		
		$.each(diaries, function(index, d) {
			
			diaryView.diarySelect.append('<option value="' + d.id + '">' + d.name  + '</option>');			
		});
		
		if (diaryView.currentDiary != null) {
			 diaryView.diarySelect.val(diaryView.currentDiary.id);
		}
	},

	deleteCurrentDiary : function() {
		
		if (diaryView.currentDiary != null) {
			
			jQuery.ajax({
		         type: "DELETE",
		         url: baseVars.baseUrl + "api/diary/" + diaryView.currentDiary.id + "/delete",
		         contentType: "application/json; charset=utf-8",
		         dataType: "json",
		         success: function (data, status, jqXHR) {
		        	 
		        	 diaryView.currentDiary = null;
		        	 diaryView.updateDiaryInfo();
		        	 diaryView.diarySelect.val('null');
		        	 diaryView.getDiaries();
		        	 diaryView.diaryDeleteModal.modal('hide');
		        	 		        	 
		        	 $.event.trigger({
		 				type: "updateCurrentDiary",
		 				id: null,
		 			 });	
		         },

		         error: function (jqXHR, status) {
		        	 diaryDeleteModal.hide();
		        	 diaryView.diaryDeleteModal.modal('hide');
		         }
			});
		}
	},	
};