package at.tuwien.ase.tripidude.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.MainActivity.OnActivityResultListener;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.api.TripitudeAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.maputils.MapListener;
import at.tuwien.ase.tripidude.maputils.OnHotspotSelectedListener;
import at.tuwien.ase.tripidude.maputils.PositionUpdateListener;
import at.tuwien.ase.tripidude.models.Coordinate;
import at.tuwien.ase.tripidude.models.Hotspot;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.models.Route;
import at.tuwien.ase.tripidude.models.utils.MapItemUtils;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.Utils;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseView.ConfigOptions;
import com.espian.showcaseview.targets.ActionViewTarget;
import com.espian.showcaseview.targets.ActionViewTarget.Type;
import com.espian.showcaseview.targets.Target;
import com.espian.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapFragment extends FragmentController implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, 
MapListener, OnActivityResultListener {

	public static final Double HOTSPOT_CHECKIN_DISTANCE = 150D;

	private static final String MAPITEM_TYPE_ROUTE = "ac.tuwien.ase08.tripitude.entity.Route";
	private static final String MAPITEM_TYPE_HOTSPOT = "ac.tuwien.ase08.tripitude.entity.Hotspot";

	private static final int CAMERAZOOM = 16;
	private static final long DEFAULT_UPDATE_LOCATION_INTERVAL = 15 * 1000; // update every 15 seconds
	private static final long DEFAULT_TERMINATE_SAT_FINDING = 1 * 60 * 60 * 1000; // for 1 hour

	private static final int SHOW_TUTORIAL_CODE = 99;
	private static final int SHOW_TUTORIAL_ACTION = 98;

	private MapView mapView;
	private GoogleMap map;
	private Bundle bundle;

	private LocationClient mLocationClient;
	private boolean isFindUserLocation = true;
	private boolean upadateMapItems = true;

	private boolean searchForMapItems = false;
	private String searchType;
	private String searchName;
	private String searchCategory;
	private boolean searchEvents = false;
	private Date searchBeginDate;
	private Date searchEndDate;

	private double lat = 0;
	private double lng = 0;

	private List<MapItem> mapItems;
	private List<PositionUpdateListener> positionUpdateListeners;
	private List<OnHotspotSelectedListener> onHotspotSelectedListeners;
	private HashMap<Marker, MapItem> markerMapItemMap = new HashMap<Marker, MapItem>();
	private Marker chosenMarker;

	private Route currentlyCreatedRoute;
	private boolean currentlyTrackingRoute;
	private Route currentlySelectedRoute;
	private boolean currentlyFollowingRoute;
	private UpdateMapItems updateMapItemsTask;
	
	private boolean updateView = true;

	private ShowcaseView sv1;
	private ShowcaseView sv2;
	private ShowcaseView sv3;
	private ConfigOptions co;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = savedInstanceState;

		// Get location manager
		mLocationClient = new LocationClient(getActivity().getApplicationContext(), this, this);
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_map;
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
		// Get the current location
		if(mLocationClient != null)
			mLocationClient.connect();
	}

	@Override
	public void onPause() {
		mapView.onPause();
		// remove listener in order to save resource
		if(mLocationClient != null)
			mLocationClient.disconnect();
		super.onPause();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	public void onDestroy() {
		// remove listener in order to save resource
		if(mLocationClient != null)
			mLocationClient.disconnect();
		if (updateMapItemsTask != null)
			updateMapItemsTask.cancel(true);
		updateMapItemsTask = null;
		super.onDestroy();
	}

	@Override
	protected void onCreate() throws Exception {

		co = new ShowcaseView.ConfigOptions();
		Target mapViewTarget = new ViewTarget(ui.id(R.id.mapView).getView());
		sv1 = ShowcaseView.insertShowcaseView(mapViewTarget,getActivity(),R.string.showcase_title2, R.string.showcase_message2,co);
		Target homeButtonTarget = new ActionViewTarget(getActivity(), Type.HOME);
		sv2 = ShowcaseView.insertShowcaseView(homeButtonTarget, getActivity(),R.string.showcase_title1, R.string.showcase_message1,co);
		Target overflowButtonTarget =  new ActionViewTarget(getActivity(), Type.OVERFLOW);
		sv3 = ShowcaseView.insertShowcaseView(overflowButtonTarget, getActivity(), R.string.showcase_title3, R.string.showcase_message3, co);

		sv1.hide();
		sv2.hide();
		sv3.hide();

		//Register listener (to know when to show tutorial)
		((MainActivity) getActivity()).addActivityResultListener(this);

		// find our mapview
		mapView = (MapView) ui.id(R.id.mapView).getView();
		mapView.onCreate(bundle);

		// init map
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);

		setInfoWindowListener();

		try {
			MapsInitializer.initialize(this.getActivity());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}

		// set onMarkerClickListener
		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {

				updateView = false;

				// animate camera to marker
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(marker.getPosition());
				map.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {

					@Override
					public void onCancel() {
						updateView = true;
					}

					@Override
					public void onFinish() {
						updateView = true;
					}
				});
				

				// reset previous selected marker
				if(chosenMarker != null) {
					MapItem previous = markerMapItemMap.get(chosenMarker);
					if (previous.getType().equals(MAPITEM_TYPE_ROUTE))
						chosenMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_standard_route));
					else
						chosenMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_standard_hotspot));					
				}

				// set marker bitmap & show info window
				chosenMarker = marker;
				MapItem current = markerMapItemMap.get(chosenMarker);
				Log.info(MapFragment.this, current.getTitle() + " / " + current.getDescription());
				if (current.getType().equals(MAPITEM_TYPE_ROUTE)) { 
					chosenMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_choosen_route));
				} else {
					chosenMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_choosen_hotspot));
				}

				chosenMarker.showInfoWindow();
				return true;
			}
		});

		map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				if (updateView)
					searchForNewMapItems();

				if (positionUpdateListeners == null)
					return; 
				for (PositionUpdateListener positionUpdateListener : positionUpdateListeners) {
					positionUpdateListener.onCameraPositionUpdate();
				}
			}
		});

		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng point) {
				displayHotspotDialog(point);
			}
		});

		updateMapItemsTask = new UpdateMapItems();
		updateMapItemsTask.execute(getBoundingCircle());
	}

	//set to public to call from searchfragment
	public void searchForNewMapItems() {
		if (upadateMapItems && (updateMapItemsTask == null || updateMapItemsTask.getStatus() == Status.FINISHED || updateMapItemsTask.getStatus() == Status.PENDING)) {
			updateMapItemsTask = new UpdateMapItems();
			updateMapItemsTask.execute(getBoundingCircle());
		}
	}

	@Override
	protected boolean isQueryNeeded() {
		return false;
	}

	@Override
	protected void onQuery() throws Exception {}

	@Override
	protected void onShow() throws Exception {
		currentlyCreatedRoute = null;
		currentlyTrackingRoute = false;
		currentlySelectedRoute = null; 
		currentlyFollowingRoute = false;
		mapItems = new ArrayList<MapItem>();
		showMapItems(mapItems);
		searchForNewMapItems();
	}

	private void addMapItems(List<MapItem> newMapItems) {
		if (mapItems == null)
			mapItems = new ArrayList<MapItem>();

		for(int i = 0; i < newMapItems.size(); i++) {
			MapItem mapItem = newMapItems.get(i);
			if (mapItems.contains(mapItem))
				continue;
			else
				mapItems.add(mapItem);

			int drawableId = R.drawable.pin_standard_hotspot;
			if (mapItem.getType().equals(MAPITEM_TYPE_ROUTE))
				drawableId = R.drawable.pin_standard_route;

			if (mapItem.getCoordinate() != null) {

				double longitude = mapItem.getCoordinate().getLongitude();
				double latitude = mapItem.getCoordinate().getLatitude();
				if(longitude != 0 && latitude != 0) {

					Marker marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(latitude, longitude))
					.title(mapItem.getTitle())
					.snippet(mapItem.getDescription())
					.icon(BitmapDescriptorFactory.fromResource(drawableId)));

					//add markers to hash map
					markerMapItemMap.put(marker, mapItem);

				}
			}
		}
		ui.id(R.id.loading_small).gone();
	}

	private void showMapItems(List<MapItem> newMapItems) {
		if (map == null)
			return;
		// draw mapItems
		chosenMarker = null;
		map.clear();
		this.mapItems = new ArrayList<MapItem>(newMapItems);
		markerMapItemMap = new HashMap<Marker, MapItem>();

		for(int i = 0; i < mapItems.size(); i++) {

			MapItem mapItem = mapItems.get(i);

			int drawableId = R.drawable.pin_standard_hotspot;
			if (mapItem.getType().equals(MAPITEM_TYPE_ROUTE))
				drawableId = R.drawable.pin_standard_route;

			if (mapItem.getCoordinate() != null) {

				double longitude = mapItem.getCoordinate().getLongitude();
				double latitude = mapItem.getCoordinate().getLatitude();
				if(longitude != 0 && latitude != 0) {

					Marker marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(latitude, longitude))
					.title(mapItem.getTitle())
					.snippet(mapItem.getDescription())
					.icon(BitmapDescriptorFactory.fromResource(drawableId)));

					//add markers to hash map
					markerMapItemMap.put(marker, mapItem);
				}
			}
		}
		ui.id(R.id.loading_small).gone();
	}

	@Override
	protected View getLoadingView() {
		return ui.id(R.id.loading).getView();
	}

	@Override
	protected View getRealView() {
		return ui.id(R.id.mapView).getView();
	}

	/**
	 * @category Location
	 */

	public void disconnectLocation() {
		mLocationClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.error(this, "Connection failed listener.");
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.info(this, "Location Connected.");
		// Get last known location
		Location lastLocation = mLocationClient.getLastLocation();
		mLocationListener.onLocationChanged(lastLocation);

		// Create location request
		LocationRequest locationRequest = LocationRequest.create()
				.setInterval(DEFAULT_UPDATE_LOCATION_INTERVAL)
				.setExpirationDuration(DEFAULT_TERMINATE_SAT_FINDING)
				.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationClient.requestLocationUpdates(locationRequest, mLocationListener);
	}

	@Override
	public void onDisconnected() {
		Log.info(this, "Location Disconnected.");
		if(mLocationClient != null  &&  mLocationClient.isConnected())
			mLocationClient.removeLocationUpdates(mLocationListener);

	}
	/**
	 * Set Infowindow Click listener
	 */
	private void setInfoWindowListener() {

		map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {

				final MapItem mapItem = markerMapItemMap.get(marker);

				if (!Utils.isNullOrEmpty(onHotspotSelectedListeners)) {
					for(OnHotspotSelectedListener listener : onHotspotSelectedListeners)
						listener.onHotspotSelected(MapItemUtils.createHotspot(mapItem));
					return;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setCancelable(true);

				int itemListId = R.array.info_window_options_hotspot;
				if (mapItem.getType().equals(MAPITEM_TYPE_ROUTE)) 
					itemListId = R.array.info_window_options_route;

				builder.setItems(itemListId, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						//set bundle args
						Bundle args = new Bundle();
						args.putLong(MapItemFragment.KEY_MAPITEM_ID, mapItem.getId());
						MapItemFragment fragment = new MapItemFragment();

						if (mapItem.getType().equals(MAPITEM_TYPE_ROUTE)) {
							if (which == 0) {			            			
								args.putString(MapItemFragment.KEY_TYPE_MODE, MapItemFragment.ROUTE_MODE);
								fragment.setArguments(args);
								((MainActivity) App.activity).addFragmentController(fragment, 1);
							}
							else if (which == 1) {
								ShowRouteDetail routeDetail = new ShowRouteDetail();
								routeDetail.execute(mapItem.getId());
							}
						}
						//in case of type hotSpot
						else  {
							if (which == 0) {	
								args.putString(MapItemFragment.KEY_TYPE_MODE, MapItemFragment.HOTSPOT_MODE);
								fragment.setArguments(args);
								((MainActivity) App.activity).addFragmentController(fragment, 1);
							} else if (which == 1) {
								((MainActivity) App.activity).addFragmentController(CreateRouteListFragment.newInstance((mapItem.getId())), 1);
							}
						}
					}
				});

				builder.create().show();
			}
		});
	}

	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			if(location == null)
				return;

			if(lat == location.getLatitude()  &&  lng == location.getLongitude()) {
				return;
			}

			lat = location.getLatitude();
			lng = location.getLongitude();

			Log.debug(MapFragment.this, "Location changed to (" + lat + ", " + lng + ")");

			if (!Utils.isNullOrEmpty(positionUpdateListeners))
				for (PositionUpdateListener positionUpdateListener : positionUpdateListeners) 
					positionUpdateListener.onPositionUpdate(new Coordinate(lat, lng));

			if (!isFindUserLocation && !currentlyTrackingRoute && !currentlyFollowingRoute)
				return;

			isFindUserLocation = false;

			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), CAMERAZOOM);
			map.animateCamera(cameraUpdate); 

			if (currentlySelectedRoute != null) {
				List<Hotspot> nearHotspots = getNearHotspotsByDistance(currentlySelectedRoute.getHotspots(), 50.0);
				if (!Utils.isNullOrEmpty(nearHotspots)) {
					for (Hotspot hotspot : nearHotspots) {
						if (!hotspot.isVisited()) { 
							showInfoNearHotspot(hotspot);
							break;
						}
					}
				}
			}

			if (currentlyCreatedRoute == null) 
				return;
			currentlyCreatedRoute.getCoordinates().add(new Coordinate(location.getLatitude(), location.getLongitude()));	
			drawRoute(currentlyCreatedRoute);
		}
	};

	private class ShowRouteDetail extends AsyncTask<Long, Void, Route>  {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			App.activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ui.id(R.id.loading_small).visible();
				}
			});
		}

		@Override
		protected Route doInBackground(Long... params) {
			try {
				return MapItemAPI.getInstance().getRouteDetail(params[0]);
			} catch (APIException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Route result) {
			ui.id(R.id.loading_small).gone();
			if (result == null) 
				return;
			else 
				showRoute(result);

		}
	}

	private void showRoute(final Route route) {
		upadateMapItems = false;
		ui.id(R.id.button_close_route).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ui.id(R.id.loading_small).visible();
				reset();
			}
		}).visible();

		ui.id(R.id.button_follow_route).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ui.id(R.id.button_follow_route).gone();
				currentlySelectedRoute = route;
				currentlyFollowingRoute = true;

				// check In
				CheckInOperation check = new CheckInOperation();
				check.execute(route);
			}
		}).visible();

		List<MapItem> routeMapItems = new ArrayList<MapItem>();
		routeMapItems.add(route);
		for (Hotspot hotspot : route.getHotspots()) {
			routeMapItems.add(hotspot);
		}
		showMapItems(routeMapItems);
		drawRoute(route);
	} 

	private void showInfoNearHotspot(final Hotspot hotspot) {
		hotspot.setVisited(true);

		AlertDialog.Builder nearHotspotDialog = new AlertDialog.Builder(
				getActivity());
		nearHotspotDialog.setTitle("You are near " + hotspot.getTitle());
		nearHotspotDialog.setMessage(
				"Do you want to learn more about this spot?")
				.setCancelable(false);

		nearHotspotDialog.setPositiveButton("Yes, tell me more!",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				((MainActivity) App.activity).addFragmentController(MapItemFragment.newInstance(hotspot.getId(), true), 1);
			}
		}).setNegativeButton("No, thank you.",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		// create alert dialog & show it
		nearHotspotDialog.create().show();

		// check In
		CheckInOperation check = new CheckInOperation();
		check.execute(hotspot);
	}

	private class UpdateMapItems extends AsyncTask<Double, Void, List<MapItem>>  {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			App.activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ui.id(R.id.loading_small).visible();
				}
			});
		}

		@Override
		protected List<MapItem> doInBackground(Double... params) {
			try {
				List<MapItem> mapItems = null;
				if(App.mapListener.isSearchForMapItems()) {
					String[] searchParams = App.mapListener.getSearchParams();
					String bounding = params[0].toString()+","+params[1].toString()+","+params[2].toString();
					Date beginDate = searchBeginDate;
					Date endDate = searchEndDate;

					if(searchEvents) {
						Log.debug(MapFragment.this, "Searching for Mapitems with Events between "+beginDate+" and "+endDate +
								" and with parameters: "+ searchParams[0]+", "+searchParams[1]+", "+searchParams[2]+", "+bounding);
						mapItems = new ArrayList<MapItem>(MapItemAPI.getInstance().getHotspotsWithEvents(searchParams[0], searchParams[1], searchParams[2], bounding, beginDate, endDate));
					}
					else {
						Log.debug(MapFragment.this, "Searching for Mapitems with following params: "+searchParams[0]+", "+searchParams[1]+", "+searchParams[2]+", "+bounding);
						mapItems = new ArrayList<MapItem>(MapItemAPI.getInstance().getHotspots(searchParams[0], searchParams[1], searchParams[2], bounding));
					}
				} else {
					Log.debug(MapFragment.this, "Searching for Mapitems with current BoundingCircle.");
					mapItems = new ArrayList<MapItem>(MapItemAPI.getInstance().getMapItemsForBoundingCircle(params));
				}
				return mapItems;
			} catch (APIException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<MapItem> result) {
			ui.id(R.id.loading_small).gone();
			if (result == null) 
				return;
			else {
				if(App.mapListener.isSearchForMapItems()) {
					changeMapItems(result);
				} else {
					addMapItems(result);
				}
			}

		}
	}

	@Override
	public void zoomTo(Coordinate coordinate) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()), CAMERAZOOM);
		map.animateCamera(cameraUpdate); 
	}

	@Override
	public void zoomTo(MapItem mapItem) {
		// zoom
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mapItem.getCoordinate().getLatitude(), mapItem.getCoordinate().getLongitude()), CAMERAZOOM);
		map.animateCamera(cameraUpdate); 
		// show InfoWindow
		getMarkerForVisibleMapItem(mapItem).showInfoWindow();
	}

	public void setSearchParams(String type, String name, String category, boolean events, Date beginDate, Date endDate) {
		if(!Utils.isNullOrEmpty(type)) {
			this.searchType = type;
		} else {
			this.searchType = "all";
		}
		if(!Utils.isNullOrEmpty(name)) {
			this.searchName = name;
		} else {
			this.searchName = "";
		}
		if(!Utils.isNullOrEmpty(category)) {
			this.searchCategory = category;
		} else {
			this.searchCategory = "All Categories";
		}
		this.searchEvents = events;
		this.searchBeginDate = beginDate;
		this.searchEndDate = endDate;
	}

	public String[] getSearchParams() {
		String[] params = {this.searchType, this.searchName, this.searchCategory};
		return params;
	}

	@Override
	public void showMapItem(MapItem mapItem) {
		List<MapItem> newList = new ArrayList<MapItem>(); 
		newList.add(mapItem);
		addMapItems(newList);
		zoomTo(mapItem);
	}

	public void setSearchForMapItems(boolean search) {
		if (!search) {
			searchForNewMapItems();
		}
		this.searchForMapItems = search;
	}

	public boolean isSearchForMapItems() {
		return searchForMapItems;
	}

	@Override
	public Double[] getBoundingCircle() {
		if (map == null)
			return null;
		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
		Location center = new Location("center");
		center.setLatitude(bounds.getCenter().latitude);
		center.setLongitude(bounds.getCenter().longitude);
		Location northEast = new Location("northEast");
		northEast.setLatitude(bounds.northeast.latitude);
		northEast.setLongitude(bounds.northeast.longitude);
		double distance = center.distanceTo(northEast);		
		Double[] boudingCircle = {center.getLatitude(), center.getLongitude(), distance};
		return boudingCircle;
	}

	@Override
	public void changeMapItems(List<MapItem> mapItems) {
		showMapItems(mapItems);
	}

	public List<MapItem> getMapItems() {
		return this.mapItems;
	}

	@Override
	public void addOnPositionUpdateListener(PositionUpdateListener listener) {
		if (positionUpdateListeners == null)
			positionUpdateListeners = new ArrayList<PositionUpdateListener>();
		positionUpdateListeners.add(listener);
	}

	@Override
	public void removeOnPositionUpdateListener(PositionUpdateListener listener) {
		if (positionUpdateListeners == null)
			return;
		positionUpdateListeners.remove(listener);
	}

	@Override
	public void drawRoute(Route route) {
		if (route == null || Utils.isNullOrEmpty(route.getCoordinates()))
			return;
		PolylineOptions routeOptions = new PolylineOptions();
		for (Coordinate coord : route.getCoordinates()) {
			routeOptions.add(new LatLng(coord.getLatitude(), coord.getLongitude()));
		}
		routeOptions.color(getActivity().getResources().getColor(R.color.route));
		routeOptions.geodesic(true);
		routeOptions.width(4);
		map.addPolyline(routeOptions);
	}

	@Override
	public void clearMap() {
		map.clear();
	}

	@Override
	public void reset() {
		upadateMapItems = true;
		ui.id(R.id.button_close_route).gone();
		ui.id(R.id.button_follow_route).gone();
		currentlyCreatedRoute = null;
		currentlyTrackingRoute = false;
		currentlySelectedRoute = null; 
		currentlyFollowingRoute = false;
		forceRequery();
		show();
	}

	@Override
	public Coordinate getCurrentCoordinate() {
		return new Coordinate(lat, lng);
	}

	@Override
	public boolean isInCreateRouteMode() {
		return currentlyTrackingRoute;
	}

	@Override
	public Route getCurrentlyCreatedRoute() {
		if (currentlyCreatedRoute == null)
			currentlyCreatedRoute = new Route();
		return currentlyCreatedRoute;
	}

	private void displayHotspotDialog(final LatLng latlng) {

		AlertDialog.Builder createEventDialog = new AlertDialog.Builder(
				getActivity());
		createEventDialog.setTitle(getString(R.string.create_hotspot_here));
		createEventDialog.setMessage(getString(R.string.create_hotspot_location).replace("#location", latlng.toString()))
		.setCancelable(false);

		createEventDialog.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				((MainActivity) App.activity)
				.addFragmentController(CreateMapItemFragment
						.newInstance(latlng, false, false));
			}
		}).setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		// create alert dialog & show it
		createEventDialog.create().show();
	}

	private Marker getMarkerForVisibleMapItem(MapItem mapItem) {		
		for (Entry<Marker, MapItem> entry : markerMapItemMap.entrySet()) 
			if (mapItem.equals(entry.getValue())) 
				return entry.getKey();
		return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 
	 * @param list of mapitems
	 * @param distance in meter
	 * @return a filtered list of mapItems which are near the distance of your current location
	 */
	public  List<MapItem> getNearMapItemsByDistance(List<MapItem> list, double distance) {
		List<MapItem> result = new ArrayList<MapItem>();

		if(Utils.isNullOrEmpty(list)) {
			return result;
		} 

		Coordinate currPosition = getCurrentCoordinate();

		for(MapItem item : list) {
			if (item.getType().equals(MAPITEM_TYPE_HOTSPOT)
					&& TripitudeAPI
					.distance(item.getCoordinate(), currPosition) <= distance) {
				result.add(item);
			}
		}

		return result;
	}

	/**
	 * 
	 * @param list of mapitems
	 * @param distance in meter
	 * @return a filtered list of mapItems which are near the distance of your current location
	 */
	public  List<Hotspot> getNearHotspotsByDistance(List<Hotspot> list, double distance) {
		List<Hotspot> result = new ArrayList<Hotspot>();

		if(Utils.isNullOrEmpty(list)) {
			return result;
		} 

		Coordinate currPosition = getCurrentCoordinate();

		for(Hotspot item : list) {
			if (item.getType().equals(MAPITEM_TYPE_HOTSPOT)
					&& TripitudeAPI
					.distance(item.getCoordinate(), currPosition) <= distance) {
				result.add(item);
			}
		}

		return result;
	}

	@Override
	public void addOnHotpotselectedListener(OnHotspotSelectedListener listener) {
		if (onHotspotSelectedListeners == null)
			onHotspotSelectedListeners = new ArrayList<OnHotspotSelectedListener>();
		onHotspotSelectedListeners.add(listener);
	}

	@Override
	public void removeOnHotpotselectedListener(OnHotspotSelectedListener listener) {
		if (Utils.isNullOrEmpty(onHotspotSelectedListeners))
			return;
		onHotspotSelectedListeners.remove(listener);
	}
	@Override
	public void onActivityResultFromActivity(int requestCode, int resultCode,
			Intent data) {
		if(requestCode ==  SHOW_TUTORIAL_CODE){
			checkIfTutorialSequenceNeeded();
		}else if(requestCode == SHOW_TUTORIAL_ACTION){
			showTutorialSequence();
		}
	}

	/**
	 * This method is called to check if the tutorial should be started 
	 * depending on the settings (saved in SharedPreferences)
	 */
	private void checkIfTutorialSequenceNeeded() {

		SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		boolean showTutorial = false;

		int highScore = sharedPref.getInt(getString(R.string.tutorial_showed), 0);
		if(highScore == 0){
			showTutorial = true;
		}

		//Change here for debugging reason - always show tutorial...
		if (showTutorial) {
			showTutorialSequence();
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(getString(R.string.tutorial_showed), 1);
			editor.commit();
		}
	}

	private void showTutorialSequence() {
		try{
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		}catch(NullPointerException e){
			Log.debug(this, "Could not hide keyboard because Context returned null for current window");
		}
		sv1.overrideButtonClick(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				sv2.setShowcaseIndicatorScale(0.6F);
				sv1.hide();
				co.fadeInDuration = 2000;
				co.fadeOutDuration = 2000;
				sv2.animateGesture(-600.0F, 0.0F, 200.0F, 300.0F);
				sv2.show();
			}
		});
		sv1.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(getActivity(), getString(R.string.nicely_done), Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		sv2.overrideButtonClick(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				sv2.hide();
				sv3.setShowcaseIndicatorScale(0.6F);
				sv3.show();
			}
		});
		sv1.show();
	}

	private class CheckInOperation extends AsyncTask<MapItem, Void, String> {
		private String result;

		@Override
		protected String doInBackground(MapItem... params) {
			result = MapItemAPI.getInstance().createHistoryItem(params[0].getId());
			return result;
		}

		protected void onPostExecute(String result) {
			Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
		}
	}
}
