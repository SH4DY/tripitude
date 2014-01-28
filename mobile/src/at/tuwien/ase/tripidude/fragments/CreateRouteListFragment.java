package at.tuwien.ase.tripidude.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.maputils.OnHotspotSelectedListener;
import at.tuwien.ase.tripidude.models.Coordinate;
import at.tuwien.ase.tripidude.models.Hotspot;
import at.tuwien.ase.tripidude.utils.ModelsAdapter;
import at.tuwien.ase.tripidude.utils.Utils;

public class CreateRouteListFragment extends FragmentController implements OnHotspotSelectedListener {

	private static final String HOTSPOT_ID_KEY = "hotspotIdKey";

	private Hotspot firstHotspot;
	private List<Hotspot> hotspotList;
	private ModelsAdapter<Hotspot> hotspotAdapter;
	private CreateRouteTask createRouteTask;

	public static FragmentController newInstance(long hotspotID) {
		FragmentController hsDetailFragment = new CreateRouteListFragment();
		Bundle args = new Bundle();
		args.putLong(HOTSPOT_ID_KEY, hotspotID);
		hsDetailFragment.setArguments(args);
		return hsDetailFragment;
	}
	
	@Override
	public void onDestroy() {
		if (createRouteTask != null)
			createRouteTask.cancel(true);
		createRouteTask = null;
		super.onDestroy();
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_create_route_list;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onCreate() throws Exception {
		ui.id(R.id.button_create_route).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Utils.isNullOrEmpty(hotspotList) && hotspotList.size() > 1) {
					Toast.makeText(getActivity(), "Route could not be created - add more hotspots", Toast.LENGTH_SHORT).show();
					return;
				}		
				ui.id(R.id.view).gone();
				if (createRouteTask != null)
					createRouteTask.cancel(true);
				createRouteTask = new CreateRouteTask();
				createRouteTask.execute();
			}
		});

		ui.id(R.id.button_add_hotspot).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				App.mapListener.addOnHotpotselectedListener(CreateRouteListFragment.this);
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				ft.hide(CreateRouteListFragment.this);
				ft.commit();
			}
		});
	}

	@Override
	protected void onQuery() throws Exception {
		firstHotspot = (Hotspot) MapItemAPI.getInstance().getHotspot(getArguments().getLong(HOTSPOT_ID_KEY));
		hotspotList = new ArrayList<Hotspot>();
		hotspotList.add(firstHotspot);
	}

	@Override
	protected void onShow() throws Exception {
		if(!hotspotList.isEmpty()) {
			// fill adapter
			hotspotAdapter = new ModelsAdapter<Hotspot>(getActivity(), R.layout.list_item_mapitem_search, hotspotList) {
				@Override
				protected void fillView(int position, Hotspot model, View view, UiHolder ui) {
					// title
					ui.id(R.id.mapitem_title).text(model.getTitle());	
					// description
					ui.id(R.id.mapitem_desc).textOrGone(model.getDescription());	
					// category
					if(!Utils.isNullOrEmpty(model.getCategories()))
						ui.id(R.id.mapitem_category).textOrGone(model.getCategories().get(0).getName());
				}
			};
			// set adapter
			ui.id(R.id.hotspot_list).adapter(hotspotAdapter);
		}
	}

	@Override
	protected View getLoadingView() {
		return ui.id(R.id.loading).getView();
	}

	@Override
	protected View getRealView() {
		return ui.id(R.id.view).getView();
	}

	@Override
	public void onHotspotSelected(Hotspot hotspot) {
		App.mapListener.removeOnHotpotselectedListener(this);

		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.show(this);
		ft.commit();

		hotspotList.add(hotspot);
		hotspotAdapter.changeModels(hotspotList);
	}

	private class CreateRouteTask extends android.os.AsyncTask<Integer, Integer, List<Coordinate>> {

		@Override
		protected  List<Coordinate> doInBackground(Integer... params) {
			List<Coordinate> coordinates = new ArrayList<Coordinate>();
			for (int i = 0; i < hotspotList.size()-1; i++) {
				coordinates.addAll(MapItemAPI.getInstance().getDirections(hotspotList.get(i), hotspotList.get(i+1)));
			}
			return coordinates;
		}
		
		@Override
		protected void onPostExecute(List<Coordinate> result) {
			Coordinate coord = new Coordinate(
					hotspotList.get(0).getCoordinate().getLatitude(), 
					hotspotList.get(0).getCoordinate().getLongitude());
			
			App.mapListener.getCurrentlyCreatedRoute().setCoordinate(coord);
			App.mapListener.getCurrentlyCreatedRoute().setHotspots(hotspotList);
			App.mapListener.getCurrentlyCreatedRoute().getCoordinates().addAll(result);
			((MainActivity) App.activity)
			.addFragmentController(CreateMapItemFragment
					.newInstance(null, false, true));
			super.onPostExecute(result);
		}
	}
}
