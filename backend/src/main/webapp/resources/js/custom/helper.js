var Helper = {
	
	truncate : function(text, size) {
		
		if (text.length > size) {
			text = text.substring(0,size) + '...';
		}
		return text;
	},
	
	nl2br : function(str, is_xhtml) {
	    var breakTag = (is_xhtml || typeof is_xhtml === 'undefined') ? '<br />' : '<br>';
	    return (str + '').replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, '$1' + breakTag + '$2');
	},
};