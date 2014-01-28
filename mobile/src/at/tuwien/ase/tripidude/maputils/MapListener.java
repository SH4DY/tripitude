package at.tuwien.ase.tripidude.maputils;

import java.util.Date;
import java.util.List;

import at.tuwien.ase.tripidude.models.Coordinate;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.models.Route;

public interface MapListener {
	
	public void addOnPositionUpdateListener(PositionUpdateListener listener);
	public void removeOnPositionUpdateListener(PositionUpdateListener listener);
	public void zoomTo(Coordinate coordinate);
	public void zoomTo(MapItem mapItem);
	public void searchForNewMapItems();
	public void showMapItem(MapItem mapItem);
	public void changeMapItems(List<MapItem> mapItems);
	public void drawRoute(Route route);
	public void clearMap();

	public void setSearchForMapItems(boolean search);
	public boolean isSearchForMapItems();
	public void setSearchParams(String type, String name, String category, boolean events, Date beginDate, Date endDate);
	public String[] getSearchParams();
	public List<MapItem> getMapItems();
	
	public Double[] getBoundingCircle();
	public void reset();
	public Coordinate getCurrentCoordinate();
	
	public boolean isInCreateRouteMode();
	public Route getCurrentlyCreatedRoute();
	
	public void addOnHotpotselectedListener(OnHotspotSelectedListener listener);
	public void removeOnHotpotselectedListener(OnHotspotSelectedListener listener);
}
