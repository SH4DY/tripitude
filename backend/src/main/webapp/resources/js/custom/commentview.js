/**
 * Custom jquery plugin 
 * COMMENTIFY by Martin Dietl
 * @param $
 */
(function ( $ ) {
	 
    $.fn.commentify = function( options ) {
 
        // This is the easiest way to have default options.
        var s = $.extend({
        	comments : new Array(),
        	noCommentsField : '.commentify-no-comments',	
        	newCommentTextField : '.commentify-new-comment-text',
        	commentsField : '.commentify-comments',
        	newCommentNameField : '.commentify-new-comment-name',
        	newCommentButton : '.commentify-new-comment-btn',
        	postUrl: null,
        	comment: '.commentify-comment',
        	nameRequired: false,	
        	postErrorOnForbiddenMessage : 'Please enter your name!',
        	maxLength: 300,
        	instanceName: null,
        }, options );
        
       
        var ele = null;
        return this.each(function() {
        	
        	ele = $(this);
        	
        	return init(ele);
        });

        
        function init(el) {
        	
        	var i= 1;
    		//set comments
    		el.find(s.noCommentsField).hide();
    		
    		//reset comment form
    		el.find(s.newCommentTextField).val('').attr('rows', 1).attr('maxlength', s.maxLength);
    		el.find(s.newCommentButton).hide();
    		
    		initTextFieldBehaviours(el);		
    		displayComments();
    	}
        
        function initTextFieldBehaviours(el) {
        	
        	if (el.hasClass("comment-processed")) {
    			return;
    		}
        	el.addClass("comment-processed");
    		
    		//init maxlength plugin
        	el.find(s.newCommentTextField).maxlength({   
    			maxCharacters: s.maxLength,
    		    slider: true // True Use counter slider    
    		}); 
    		
    		
    		/**
    		 * textfield on fovus
    		 */
        	el.find(s.newCommentTextField).focus(function() {
    			$(this).attr('rows', 4);
    			el.find(s.newCommentButton).show();
    		});
    		/**
    		 * Textfield on focus out
    		 */
        	el.find(s.newCommentTextField).focusout(function() {
    			
    			if (!$(this).val().length) {
    				
    				$(this).attr('rows', 1);
    				el.find(s.newCommentButton).hide();
    			}
    		});
    		/**
    		 * Comment submit
    		 */
        	el.find(s.newCommentButton).click(function() {
    			
    			if (el.find(s.newCommentTextField).val().length) {
    							
    				var comment = new Object();
    				
    				if (el.find(s.newCommentNameField) != null) {					
    					comment.name = el.find(s.newCommentNameField).val();
    				}
    				
    				comment.text = el.find(s.newCommentTextField).val();
    				
    				//hide button
    				el.find(s.newCommentButton).hide();
    	
    				jQuery.ajax({
    			         type: "POST",
    			         url: s.postUrl,
    			         contentType: "application/json; charset=utf-8",
    			         dataType: "json",
    			         data: JSON.stringify(comment),
    			         success: function (data, status, jqXHR) {
    			        	 
    			        	//reset field 
    			        	 el.find(s.newCommentTextField).val('').attr('rows', 1);
    			        	 s.comments.push(data);
    			        	 displayComments();
    			        	 
    			        	//scroll to item
    			        	 el.find(s.commentsField).animate({
    		        			    scrollTop: 0
    		        		 }, 500);
    			        	 //trigger event
    			        	 $.event.trigger({
     			  				type: "commentAdded",
     			  				id: s.instanceName,
     			  			 });
    			         },
    	
    			         error: function (jqXHR, status) {
    			        	 if (jqXHR.status = 403) {
    			        		 StatusMessages.setMessage('danger', s.postErrorOnForbiddenMessage, 2000);
    			        	 }
    			         }
    				});
    			}
    		});
        	
        }
        
        function displayComments() {
        	
        	el = ele;
        	
        	//clear old comments
        	el.find('.comment-copy').remove();
    		
    		if (s.comments == null || !s.comments.length) {
    			s.comments = new Array();
    			el.find(s.noCommentsField).show();
    		}
    		else {
    			el.find(s.noCommentsField).hide();
    			var commentTpl = el.find(s.comment);
    			
    			for (var i = s.comments.length-1; i >= 0; i--) {	
    				
    				var comment = s.comments[i];
    				var commentItem = commentTpl.clone();
    				commentItem.addClass('comment-' +  (s.comments.length-i));
    				
    				if (comment.user != null) {					
    					$('.comment-user', commentItem).html(comment.user.name);
    				}
    				else if (comment.name != null) {
    					$('.comment-user', commentItem).html(comment.name);
    				}
    				
    				$('.comment-time-ago', commentItem).html($.timeago(comment.created));
    				$('.comment-text', commentItem).html(comment.text);
    				commentItem.addClass('comment-copy').show();
    				el.find(s.commentsField).append(commentItem);
    			}		
    		}        	
        }
 
    };
 
}( jQuery ));