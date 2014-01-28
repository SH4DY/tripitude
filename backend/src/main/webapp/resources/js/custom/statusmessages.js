/**
 * Document ready listener
 */
$(document).ready(function() {
    
	StatusMessages.initFields();
});

var StatusMessages = {
	
	messageDanger : null,
	
	messageWarning : null,

	messageInfo : null,
	
	messageSuccess : null,
		
	initFields : function() {
		this.messageDanger = $('.alert-danger-js');
		this.messageWarning = $('.alert-warning-js');
		this.messageInfo = $('.alert-info-js');
		this.messageSuccess = $('.alert-success-js');
	},	

    setMessage : function(type, content, duration) {
    	
    	var messageItem = null;
    	
    	switch (type) {
	        case 'danger':
	        	messageItem = StatusMessages.messageDanger;
	        	break;
	        case 'warning':
	        	messageItem = StatusMessages.messageWarning;
	        	break;
	        case 'info':
	        	messageItem = StatusMessages.messageInfo;
	        	break;
	        case 'success':
	        	messageItem = StatusMessages.messageSuccess;
	        	break;
        }
    	
    	messageItem.html(content).fadeIn().delay(duration).fadeOut();
  	
    }
};