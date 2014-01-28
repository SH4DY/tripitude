////init map
//	 var mapOptions = {
//	          center: new google.maps.LatLng(48.195092,16.369889),
//	          zoom: 17,
//	          mapTypeId: google.maps.MapTypeId.ROADMAP
//	        };
//	 var test = $("#map_canvas");
//	 var map = new google.maps.Map($("#map_canvas")[0], mapOptions);
//	 
//	 var myLatlng = new google.maps.LatLng(48.195092,16.369889);
//	 
//	 var marker = new google.maps.Marker({
//	      position: myLatlng,
//	      map: map,
//	      title:"IR1 Meeting"
//	 });


	 
 var MapUtil = {
	 
	 initMap : function(sel, lat, lng, zoom) {
		 var mapOptions = {
		          center: new google.maps.LatLng(lat, lng),
		          zoom: zoom,
		          mapTypeId: google.maps.MapTypeId.ROADMAP
		        };

		 var map = new google.maps.Map($(sel)[0], mapOptions);
		 
		 return map;
     },
 
     getStaticMapUrlByCoordinates : function(lat, lng, width, height, zoom) {
    	 
    	 return 'http://maps.google.com/maps/api/staticmap?center=' + lat + ',' + lng + '&zoom=' + zoom + '&markers=color:red|' + lat + ',' + lng + 
    	        '&path=color:0x0000FF80|weight:5|' + lat + ',' + lng + '&size=' + width + 'x' + height + '&sensor=true';
     } 	
 };