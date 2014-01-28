$(document).ready(function() {
	
	//init elements
	mapItemDetailsWindow.initFields();
	modalMapView.init();
	
	$(document).on('showMapModalMapView', function(e) {
	
		modalMapView.showModalMapView(e.id);
	
	});	
});


var modalMapView = {
			
	init : function() {
		
		$('body').on('shown.bs.modal', '#map-modal', function () {
			
			element = $(window);
			$(element).height();
			$('#map-modal .modal-body').height($(element).height() - 250);
			
			mapView.setMaxMapHeight($('#map-modal .modal-body'));
			google.maps.event.trigger(mapView.map, "resize");
		    
		    mapView.fitBounds();
		});
	},
	
	showModalMapView : function(mapItemId) {
		//reset filter
		filterQuery.ids = new Array();
		filterQuery.ids.push(mapItemId);
		filterQuery.boundingCircle = null;
		
		mapView.initMap();
		mapView.lockRefreshing=true;
		mapView.displayMapItems(null);

		$('#map-modal').modal('show');
	}		
};