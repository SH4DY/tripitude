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
	
	
});

/**
 * MapView Object
 */
var mapView = {

	map : null,
	
	infoWindows : new Array(),
	
	currentInfoWindow : null,
	
	markers : new Array(),
	
	bounds : new google.maps.LatLngBounds(),
	
	fitBoundsAfterRefresh : true,
	
	lockRefreshingByZoom : false,
	
	lockRefreshing : false,
	
	resetCurrentRoutePathAfterRefresh : true,
	
	currentRoutePath : null,
	
	routeMode : false,
	
	selectedMarker : null,

	/**
	 * Initialize map
	 */
	initMap : function() {
		
		var mapUtil = MapUtil;
		this.map = mapUtil.initMap("#map_canvas", 45, 0, 3);
		this.setBoundsChangedListener();
		this.initFullRouteButtonListener();
		this.initShowDetailsButtonListener();
		
		//this.setMaxMapHeight();
	},
    
	/**
	 * Wrapper for getMapItems
	 */
	displayMapItems : function() {
		
		//get and set markers		
		this.getMapItems();		
	},
	/**
	 * Get mapitems by filter query
	 */
	getMapItems: function() {
			   
	    jQuery.ajax({
	         type: "GET",
	         url: baseVars.baseUrl + "api/mapitem" + filterQuery.getFilterQueryString(),
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	 
	        	//clear all markers
	     		mapView.clearAllMarkers();	     		
	        	mapView.createMarkers(data);
	         },

	         error: function (jqXHR, status) {
	        	 alert('something went wrong');
	         }
		});
	},
	/**
	 * Draw markers
	 */
	createMarkers: function(mapItems) {
		
		mapView.bounds = new google.maps.LatLngBounds();
        
		$.each(mapItems, function(key, mapItem) {
			 
			 var latLng = new google.maps.LatLng(mapItem.coordinate.latitude, mapItem.coordinate.longitude);
			 
			 mapView.bounds.extend(latLng);
			 		 			 
			 var marker = new google.maps.Marker({
				  clickable: true,
			      position: latLng,
			      map: mapView.map,
			      title: mapItem.title,
			      id: mapItem.id,
			      type: mapItem.type,
			      icon: mapView.getMarkerIcon(mapItem.type, 'standard'),
			 });

			 
			 mapView.markers.push(marker);
			 
			 //event listener on click
			 google.maps.event.addListener(marker, 'click', function() {
				 
				 mapView.selectedMarker = marker;
				 mapView.selectedMarker = marker;
				 
				 
				 if (mapView.routeMode) {
					 mapView.setMarkerSingleSelection(marker, 'standard');
				 }
				 else {					 
					 mapView.setMarkerSingleSelection(marker, 'inactive');
				 }

				 
				 mapView.showInfoWindow(marker);				 
				 
		     });
			 
			 mapView.createInfoWindow(mapItem);
		});
		
		if (mapView.fitBoundsAfterRefresh) {			
			mapView.fitBounds();
		}
	},
	
	/**
	 * Display single info window
	 */
	showInfoWindow : function (marker) {
		if (mapView.currentInfoWindow) {
			mapView.currentInfoWindow.close();
		}
		 
		infoWindow = mapView.infoWindows[marker.id];
		infoWindow.open(mapView.map, marker);
		mapView.currentInfoWindow = infoWindow;
	},
	/**
	 * Fit bounds to content
	 */
	fitBounds : function(previous) {
		//fit bounds
		if (mapView.markers.length == 1) {
			mapView.map.setCenter(mapView.markers[0].getPosition());
			mapView.map.setZoom(15);
		}
		else {			
			mapView.map.fitBounds(mapView.bounds);
			mapView.previousBounds = mapView.bounds;
		}
	},
	/**
	 * Create info window
	 */
	createInfoWindow : function(mapItem) {
		
		var categories = new Array();
		
		$.each(mapItem.categories, function (key, cat) {		
			categories.push(jQuery.i18n.prop('mapitem_categories.' + cat.name));
		});
		
		
		var contentString = '<div>' + 
        '<h2>' + mapItem.title + '</h2>' +
        '<div class="map-info-window-categories"><h4>' +
        categories.join(', ') +
        '</h4></div>' +
        '<div class="map-info-window-link-bar">' +    
        '<button href="#" data-toggle="modal" data-target="#mapItemDetails"' + 
		'class="btn btn-xs btn-info show-details" rel="' + mapItem.id  + '" type="button">' + 
		 jQuery.i18n.prop('message.show_details') + '</button>' +
        
        ((mapItem.type.indexOf("Route") != -1) ? '<button href="#" ' + 
        		'class="btn btn-xs btn-info show-full-route" rel="' + mapItem.id  + '" type="button">' + 
        		 jQuery.i18n.prop('message.show_route') + '</button>' : 
        	     '') +
        
        '</div>'+
        '</div>';

	    var infowindow = new google.maps.InfoWindow({
	        content: contentString
	    });
	    
	    this.infoWindows[mapItem.id] = infowindow;	    
	},
	/**
	 * Clear all markers
	 */
	clearAllMarkers : function() {
		
		$.each(this.markers, function(key, marker){
			marker.setMap(null);
		});
		this.markers = new Array();
	},
	/**
	 * Set map position to client position
	 */
	setPositionToClientLocation : function() {
		
		if(navigator.geolocation) {
			
		    browserSupportFlag = true;
		    navigator.geolocation.getCurrentPosition(function(position) {
		      
		    	initialLocation = new google.maps.LatLng(position.coords.latitude,position.coords.longitude);		      
		        //set position
		    	mapView.map.setCenter(initialLocation);
		    	mapView.map.setZoom(12);
		      
		    }, function() {
		    	mapView.fitBounds();
		    });
		}
	},
	
	/**
	 * Set event listeners on zoom and drag
	 */
	setBoundsChangedListener : function () {
		
			
		google.maps.event.addListener(this.map, "dragend", function() {
			
			if (mapView.lockRefreshing) {
				return;
			}
			//enable refreshing by zoom
			mapView.lockRefreshingByZoom = false;
			mapView.currentZoomLevel = mapView.map.getZoom();
			mapView.refreshMapToBoundingCircle(mapView.map.getBounds());
		});
		
		google.maps.event.addListener(this.map, "zoom_changed", function() {
			
			if (mapView.lockRefreshing) {
				return;
			}
			
			if (!mapView.lockRefreshingByZoom) {
				
				console.log("map zoom: " + mapView.map.getZoom() + "; current zoom:" + mapView.currentZoomLevel);
				mapView.refreshMapToBoundingCircle(mapView.map.getBounds());
				mapView.currentZoomLevel = mapView.map.getZoom();
			}
		});
	},
	/**
	 * Refresh mapitems 
	 */
	refreshMapToBoundingCircle : function(bounds) {
		
		if (mapView.routeMode || mapView.lockRefreshing) {
			return;
		}
		
		//calculate radius
		var radius = google.maps.geometry.spherical.computeDistanceBetween(bounds.getCenter(), bounds.getNorthEast());
		
		filterQuery.boundingCircle = new Array(bounds.getCenter().lat(), bounds.getCenter().lng(), radius);
		
		mapView.fitBoundsAfterRefresh = false;
		mapView.displayMapItems();	
		
	},
	/**
	 * Display full route
	 */
	displayFullRoute : function(id) {
		
		jQuery.ajax({
	         type: "GET",
	         url: baseVars.baseUrl + "api/route/" + id,
	         contentType: "application/json; charset=utf-8",
	         dataType: "json",
	         success: function (data, status, jqXHR) {
	        	 
	        	 mapView.drawRoutePath(data);
	        	 mapView.createMarkers(data.hotspots);
	         },

	         error: function (jqXHR, status) {
	        	 alert('something went wrong');
	         }
		});
		
	},
	/**
	 * returns marker icon  for type and status
	 */
	getMarkerIcon : function (type, status) {
				
		if(type.indexOf("Route") != -1){				 
			 type = 'route';
		}
		else if(type.indexOf("Hotspot") != -1){				 
			 type = 'hotspot';
		}
		
		return baseVars.baseUrl + 'resources/images/pin_' + status + '_' +  type + '.png';
	},
	/**
	 * Single selection behaviour
	 */
	setMarkerSingleSelection: function (marker, statusOthers) {
		
		$.each(mapView.markers, function (key, m) {
			m.setIcon(mapView.getMarkerIcon(m.type, statusOthers));
		});

		marker.setIcon(mapView.getMarkerIcon(marker.type, 'choosen'));
		 
		
	},
	
    setMultipleMarkerSingleSelection: function (marker, statusOthers) {
		
		$.each(mapView.markers, function (key, m) {
			m.setIcon(mapView.getMarkerIcon(m.type, statusOthers));
		});

		marker.setIcon(mapView.getMarkerIcon(marker.type, 'choosen'));
		 
		
	},
	
	/**
	 * draw path with polyline
	 */
	drawRoutePath : function(route) {
		
		var path = new Array();
		
		//reset path
		mapView.resetCurrentRoutePath();
		
		$.each(route.coordinates, function(key, coordinate) {
			path.push(new google.maps.LatLng(coordinate.latitude, coordinate.longitude));		
		});
		
		mapView.currentRoutePath = new google.maps.Polyline({
		    path: path,
		    geodesic: true,
		    strokeColor: '#FF0000',
		    strokeOpacity: 1.0,
		    strokeWeight: 2
		});
		
		mapView.currentRoutePath.setMap(mapView.map);
		
	}, 
	/**
	 * reset/hide current path
	 */
	resetCurrentRoutePath: function() {
		
		if (mapView.currentRoutePath != null) {			
			mapView.currentRoutePath.setMap(null);
		} 
	},
	
	/**
	 * Full route button listener
	 */
	initFullRouteButtonListener : function() {
		
		$(document).on('click', '.show-full-route', function() {
			
			var routeId = $(this).attr('rel');
				
			mapView.setRouteMode(true);
			
			//clear all markers except curent route
			$.each(mapView.markers, function(key, marker){
				
				if (marker.id != routeId) {					
					marker.setMap(null);
				}
			});
			this.markers = new Array();
			
			mapView.displayFullRoute(routeId);
			
		});
		
	},
	
	initShowDetailsButtonListener : function() {
		
		$(document).on('click', '.show-details', function() {			
			
			var mapItemId = $(this).attr('rel');
			
			//trigger event
			$.event.trigger({
				type: "showMapItemDetails",
				id: mapItemId,
			});		
		});
		
	},
	
	/**
	 * Set route mode
	 */
	setRouteMode : function(enabled) {
		
		mapView.routeMode = enabled;
		
		if (!enabled) {
			mapView.resetCurrentRoutePath();
		}
		
		//trigger event
		$.event.trigger({
			type: "routeMode",
			enabled: enabled,
		});
	},
	
	setMaxMapHeight : function(element) {
		
		if (element != null) {
			
			$(element).height();
			$('#map_canvas').height($(element).height());			
		}
		else {
			
			element = $(window);
			$(element).height();
			$('#map_canvas').height($(element).height() - $('#map_canvas').offset().top - 20);
		}
	}
};


/**
 * Filter query
 */ 
var filterQuery = {
	
	nameLike : new Array(),
	
	types: new Array(),
	
	categoryIds : new Array(),
	
	boundingCircle : new Array(),
	
	maxResults: 200,
	
	ids : new Array(), 
	
	getFilterQueryString : function() {
		
		var filterQueryString = "";
		
		var filters = new Array();
		
		if ($.isArray(this.types)  && this.types.length) {
			filters.push('types=' + this.types.join(','));
		}
		
		if ($.isArray(this.categoryIds)  && this.categoryIds.length) {
			filters.push('category_ids=' + this.categoryIds.join(','));
		}
		
		if ($.isArray(this.boundingCircle)  && this.boundingCircle.length == 3) {
			filters.push('bounding_circle=' + this.boundingCircle.join(','));
		}
		
		if ($.isNumeric(this.maxResults)) {
			filters.push('max_results=' + this.maxResults);
		}
		
		if ($.isArray(this.ids)  && this.ids.length) {
			filters.push('ids=' + this.ids.join(','));
		}
		
		if (filters.length) {			
			filterQueryString = '?' + filters.join('&');
		}
		
		return filterQueryString;
	}
};





