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
	
	historyItemView.init();
	historyItemView.setMaxHeight();
	
	// on resize browser window 
	$(window).resize(function() {
		historyItemView.setMaxHeight();
	});
	
});	
	

/**
 * HistoryItemList Object
 */
var historyItemView = {
	
    historyItemList : null,
    
    mapUtil : null,
	/**
	 * Init view
	 */	
	init : function() {
		
		
		this.mapUtil = MapUtil;
		this.initFields();
		this.getHistoryItems();
		this.initShowMapItemDetailsButtonListener();
	},
	/**
	 * Init view fields
	 */
	initFields : function() {
		
		this.historyItemList = $('#historyitems-list');
		
		$('body').on('click', '#historyitems .historyitem-mapitem-map', function() {
			$.event.trigger({
 				type: "showMapModalMapView",
 				id: $(this).attr('rel'),
 			 });
		});
	},
	/**
	 * Get and set history items
	 */
	getHistoryItems : function() {
		
		jQuery.ajax({
	         type: "GET",
	         url: baseVars.baseUrl + "api/user/historyitems",
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	 
	        	 historyItemView.displayHistoryItems(data);
	         },

	         error: function (jqXHR, status) {
	        	 alert('something went wrong');
	         }
		});
	},
	/**
	 * Display history items
	 */
	displayHistoryItems : function(historyItems) {
		
		
		if (historyItems.length) {
			$('#historyitems-empty').hide();
		}
		else {
			$('#historyitems-empty').show();
		}
		
		var historyItemTpl = $('.historyitems-list-item');
		
		$.each(historyItems, function(index, h) {
			
			var historyItem = historyItemTpl.clone();
			
			$('.historyitem-time', historyItem).html($.format.date(h.time, "yyyy-MM-dd H:mm"));
			$('.historyitem-mapitem-title', historyItem).html(Helper.truncate(h.mapItem.title, 30));
			$('.historyitem-mapitem-map', historyItem).attr('src', historyItemView.mapUtil.getStaticMapUrlByCoordinates(h.mapItem.coordinate.latitude, h.mapItem.coordinate.longitude, 80, 80, 13));
			$('.historyitem-mapitem-map', historyItem).attr('rel', h.mapItem.id);
			$('.historyitem-mapitem-details-link', historyItem).attr('rel', h.mapItem.id);
			
			var iconClass = 'icon-hotspot';
			if (h.mapItem.type.indexOf("Route") != -1) {
				iconClass = 'icon-route';
			}
			$('.historyitem-mapitem-title').addClass(iconClass);
			
			historyItem.attr('rel', h.id);
			historyItem.show();
			historyItemView.historyItemList.append(historyItem);
			
		});
		
		historyItemView.initDraggable();
	},
	/**
	 * Set view max height
	 */
	setMaxHeight : function() {
		
		$(window).height();
		$('#historyitems-scroll-pane').height($(window).height() - $('#historyitems-scroll-pane').offset().top - 30);
	},
	/**
	 * init show details button listener
	 */
	initShowMapItemDetailsButtonListener : function() {
		
		$(document).on('click', '.historyitem-mapitem-details-link', function() {			
			
			var mapItemId = $(this).attr('rel');
			
			//trigger event
			$.event.trigger({
				type: "showMapItemDetails",
				id: mapItemId,
			});		
		});
		
	},
	/**
	 * Init draggable histroy item
	 */
	initDraggable : function() {
		
		$( "#historyitems-list li" ).draggable({
			opacity: 0.7, 
			helper: "clone",
			appendTo: "body",
		      start: function(e) {
		      },
		      drag: function() {
		      },
		      stop: function() {
		      }
		    });

	},
	
};

