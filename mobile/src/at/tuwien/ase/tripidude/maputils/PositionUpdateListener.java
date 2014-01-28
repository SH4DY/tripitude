package at.tuwien.ase.tripidude.maputils;

import at.tuwien.ase.tripidude.models.Coordinate;

public interface PositionUpdateListener {
	public void onPositionUpdate(Coordinate coordinate);
	public void onCameraPositionUpdate();
}
