package at.tuwien.ase.tripidude.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.models.utils.MapItemUtils;
import at.tuwien.ase.tripidude.utils.Utils;

import com.google.android.gms.maps.model.LatLng;

public class HotspotListFragment extends SearchListFragment {
	
	private static final String TYPE_HOTSPOT = "hotspot";
	private static final String TYPE = "type";
	private static final String NAME = "name";
	private static final String CATEGORY = "category";
	private static final String BOUNDINGCIRCLE = "boundingCircle";

	public static FragmentController newInstance(Double[] boundingCircle) {
		FragmentController list = new HotspotListFragment();
		list.setUseLoadingBackground(false);
		Bundle args = new Bundle();
		args.putString(TYPE, TYPE_HOTSPOT);
		args.putString(NAME, "");
		args.putString(CATEGORY, MapItemAPI.CATEGORIES_ALL);
		args.putDoubleArray(BOUNDINGCIRCLE, Utils.getPrimitive(boundingCircle));
		list.setArguments(args);
		return list;		
	}
	
	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_hotspot_list;
	}
	
	@Override
	protected void onListItemClick(MapItem item) {
		App.mapListener.getCurrentlyCreatedRoute().getHotspots().add(MapItemUtils.createHotspot(item));
		Toast.makeText(getActivity(), getString(R.string.saving), Toast.LENGTH_SHORT).show();
		getActivity().onBackPressed();
	}
	
	@Override
	protected void onCreate() throws Exception {
		super.onCreate();
		ui.id(R.id.add_new_hotspot).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
				((MainActivity) getActivity()).addFragmentController(
						CreateMapItemFragment.newInstance(
								new LatLng(App.mapListener.getCurrentCoordinate().getLatitude(), App.mapListener.getCurrentCoordinate().getLongitude()),
								true, false));
			}
		});
	}
}
