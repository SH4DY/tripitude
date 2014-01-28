/**
 * Custom rating plugin 
 * COMMENTIFY by Martin Dietl
 * @param $
 */
(function ( $ ) {
	 
    $.fn.rating = function( options ) {
 
        // This is the easiest way to have default options.
        var s = $.extend({
        	rateUrl: null,
        	positiveBtn: '.rating-pos-btn',
        	negativeBtn: '.rating-neg-btn',
        	positiveInfo: '.rating-pos-info',
        	negativeInfo: '.rating-neg-info',
        	initialRating: null,
        	currentUserRatingUrl: null,
        }, options );

	    return this.each(function() {
	    	return init($(this));
	    });
	    
	    function init(el) { 
	    	
	    	
	    	el.find(s.negativeBtn).prop('disabled', false);
	    	el.find(s.positiveBtn).prop('disabled', false);
	    	el.find(s.positiveInfo).html(0);
	    	el.find(s.negativeInfo).html(0);
	    	
	    	initRatingInfo(el, s.initialRating);    
	    	initCurrentUserRating(el);
	    	initListener(el);
	    }
	    
	    function initRatingInfo(el, ratingCache) {
	    	
	    	if (ratingCache != null) {
	    		var neg = (ratingCache.numRatings - ratingCache.sum)/2; 
				var pos = ratingCache.numRatings - neg;
	    		setRatingInfo(el, pos, neg);
	    	}    	
	    }
	    
	    function setRatingInfo(el, pos, neg) {
	    	el.find(s.positiveInfo).html(pos);
	    	el.find(s.negativeInfo).html(neg);
	    }
	    
	    function initCurrentUserRating(el) {
	    	
	    	if (s.currentUserRatingUrl == null) {
	    		return;
	    	}
	    	
	    	jQuery.ajax({
		         type: "GET",
		         url: s.currentUserRatingUrl,
		         contentType: "application/json; charset=utf-8",
		         dataType: "json",
		         success: function (data, status, jqXHR) {
		        	 
		        	 setBtnActivity(el, data.rating);
		         },

		         error: function (jqXHR, status) {
		        	 if (jqXHR.status = 404 && jqXHR.responseJSON.message == 'user not found') {
		        		 el.find(s.negativeBtn).prop('disabled', true);
		     	    	 el.find(s.positiveBtn).prop('disabled', true);
		        	 }
		         }
			});
	    }
	    
	    function setBtnActivity(el, val) {
	    	
	    	el.find(s.negativeBtn).prop('disabled', false);
	    	el.find(s.positiveBtn).prop('disabled', false);
	    	
	    	if (val > 0) {
	    		el.find(s.positiveBtn).prop('disabled', true);
	    	}
	    	else {
	    		el.find(s.negativeBtn).prop('disabled', true);
	    	}
	    }
	    
	    function initListener(el) {
	    	
	    	el.find(s.positiveBtn).click(function() {
	    		postRating(el, 1);
	    	});
	    	
	    	el.find(s.negativeBtn).click(function() {
	    		postRating(el, -1);
	    	});
	    }
	    
	    function postRating(el, ratingVal) {
	    	
	    	if (s.rateUrl == null) {
	    		return;
	    	}
	    	
	    	var rating = new Object();
	    	rating.rating= ratingVal;
	    	
	    	jQuery.ajax({
		         type: "POST",
		         url: s.rateUrl,
		         contentType: "application/json; charset=utf-8",
		         dataType: "json",
		         data: JSON.stringify(rating),
		         success: function (data, status, jqXHR) {
		        	 initRatingInfo(el, data);
		        	 initCurrentUserRating(el);
		         },

		         error: function (jqXHR, status) {
		        	 
		         }
	    	});
	    }
    
  };

}( jQuery ));
        