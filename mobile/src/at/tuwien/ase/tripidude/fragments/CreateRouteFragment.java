package at.tuwien.ase.tripidude.fragments;

import android.os.Bundle;
import android.view.View;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;

public class CreateRouteFragment extends FragmentController {

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_create_route;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onCreate() throws Exception {
		if (App.mapListener.isInCreateRouteMode())
			startCreatingRoute(false);
		
		ui.id(R.id.button_create_route).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startCreatingRoute(true);
			}
		});
		ui.id(R.id.button_create_hotspot).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) App.activity)
				.addFragmentController(HotspotListFragment.newInstance(App.mapListener.getBoundingCircle()));
			}
		});
		ui.id(R.id.button_create_route_done).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ui.id(R.id.button_create_route_done).gone();
				ui.id(R.id.button_create_route_cancel).gone();
				ui.id(R.id.button_create_hotspot).gone();
				ui.id(R.id.button_create_route).visible();
				((MainActivity) App.activity)
				.addFragmentController(CreateMapItemFragment
						.newInstance(null, false, true));
			}
		});
		ui.id(R.id.button_create_route_cancel).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).showMap(true);
			}
		});
	}

	@Override
	protected boolean isQueryNeeded() {
		return false;
	}
	@Override
	protected void onQuery() throws Exception {}

	@Override
	protected void onShow() throws Exception {}

	@Override
	protected View getLoadingView() {
		return null;
	}

	@Override
	protected View getRealView() {
		return null;
	}
	
	private void startCreatingRoute(boolean newRoute) {
		if (newRoute)
			App.mapListener.getCurrentlyCreatedRoute().setCoordinate(App.mapListener.getCurrentCoordinate());
		ui.id(R.id.button_create_route_done).visible();
		ui.id(R.id.button_create_route_cancel).visible();
		ui.id(R.id.button_create_hotspot).visible();
		ui.id(R.id.button_create_route).gone();
	}
}
