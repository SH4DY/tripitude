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
	
	
	mapView.initMap();
	//mapView.fitBoundsAfterRefresh = false;
	mapView.setPositionToClientLocation();
	mapView.displayMapItems(null);
	mapView.setMaxMapHeight(null);
	// on resize browser window 
	$(window).resize(function() {
		mapView.setMaxMapHeight(null);
	});
	searchForm.initForm();
});
/**
 * Searchform object
 */
var searchForm = {
	
	form : $('form#map-search-form'),
	
	mapItemCategoriesForm : $('form#map-search-form input:checkbox.mapItemCategory'),
	
	filterQuery : new Array(),
	
	initMapitemCategoryBehaviours: function() {
		
		$('input:checkbox.mapItemCategory', this.form).change(function(e) {
		    
			//reset filter
			filterQuery.categoryIds = new Array();
			
			searchForm.mapItemCategoriesForm.each(function() {
				
				if ($(this).is(':checked')) {						
					//set filter
				    filterQuery.categoryIds.push($(this).val());
				}
			});			
			
			//prevent fit to bounts
			mapView.fitBoundsAfterRefresh = false;
			mapView.displayMapItems();		
		});
		
		//keep dropdown open
		$('#mapItemCategories-list li').click(function(e) {
			e.stopPropagation();
		});
		
		//all button behavior
		$('#mapItemCategories-list a.all-button').click(function() {
			searchForm.mapItemCategoriesForm.each(function() {
				$(this).removeAttr('checked');
			});
			
	        //trigger change event
			$('input', this.form).trigger('change');
		});
	},
	
	initMapitemNameBehaviours: function() {
		
		
		$('#mapItemName input').typeahead({
			name: 'MAPITEMS',
			remote: baseVars.baseUrl + 'api/mapitem?title_like=%QUERY',
			valueKey: 'title',
		});
		
		$('#mapItemName input').on('typeahead:selected', function (evt, object) {
				
			selectedMapItem = object;		       
	    	mapView.clearAllMarkers();
	    	mapView.resetCurrentRoutePath();
	    	mapView.fitBoundsAfterRefresh = true;
	    	mapView.lockRefreshingByZoom = true;
	    	mapView.createMarkers([selectedMapItem]);
	    	mapView.map.setZoom(15);
	    	
	    	//show infow window
	    	mapView.showInfoWindow(mapView.markers[0]);
		});
				
		//reset map if empty
		$('#mapItemName input').keyup(function() {
			if (!$(this).val().length) {
								
				mapView.fitBoundsAfterRefresh = false;
				mapView.displayMapItems(null);
				mapView.map.setZoom(12);
			}
		});
	},
	/*
	 * Type filter behaviours
	 */
	initMapitemTypeBehaviours : function() {
		
		$('#mapItemType button').click(function() {
			
			$('#mapItemType button').removeClass('active');
			
			$(this).addClass('active');
			
			if ($(this).attr('rel') != 'all') {
				filterQuery.types[0] = $(this).attr('rel');
			}
			else {
				//all filter 
				filterQuery.types = new Array();
			}
			
			mapView.fitBoundsAfterRefresh = false;
			mapView.displayMapItems();
		});
	},
	/**
	 * init route mode behaviours
	 */
	initRouteModeBehaviours : function() {
		$(document).on('routeMode', function(e) {
			if (e.enabled) {
				//enable close button
				$('#map-close-route-btn').fadeIn();
				searchForm.setFormStatus(false);
				
				
			}
			else {
				$('#map-close-route-btn').fadeOut();
				searchForm.setFormStatus(true);
				
				//show all other markers
				mapView.fitBoundsAfterRefresh = false;
				mapView.displayMapItems();	
			}
			
		});
	},
	
	/**
	 * init close route behaviours
	 */
	initCloseRouteBehaviours : function() {
		//on close route listener
		$('#map-close-route-btn').click(function() {
			mapView.setRouteMode(false);
		});
	},
	
	/**
	 * disable/enable form
	 */
	setFormStatus : function(enabled) {
		
		
		var elements = $('#mapItemCategories, #mapItemType .btn');
		
		enabled ? elements.removeClass('disabled') : elements.addClass('disabled');
		enabled ? $('#mapItemCategories > a').attr('data-toggle', 'dropdown') : $('#mapItemCategories > a').attr('data-toggle', '');
		
		//text input
		$('#mapItemName input').prop('disabled', !enabled);

	}, 
	/**
	 * init hole form
	 */
	initForm : function() {
		this.form = $('form#map-search-form');
		this.mapItemCategoriesForm = $('form#map-search-form input:checkbox.mapItemCategory');
		
		this.initMapitemCategoryBehaviours();
		this.initMapitemNameBehaviours();
		this.initMapitemTypeBehaviours();
		this.initRouteModeBehaviours();
		this.initCloseRouteBehaviours();
		
		this.form.submit(function() {
			return false;
		});

	}
};