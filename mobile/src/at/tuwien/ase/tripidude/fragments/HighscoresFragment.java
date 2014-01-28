package at.tuwien.ase.tripidude.fragments;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.TripitudeAPI;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.User;
import at.tuwien.ase.tripidude.utils.UiQuery;
import at.tuwien.ase.tripidude.utils.Utils;

public class HighscoresFragment extends FragmentController {

	private List<User> users;
	
	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_highscores;
	}

	@Override
	protected void onCreate() throws Exception {
		ui.id(R.id.highscores_loading).visible();
	}

	@Override
	protected void onQuery() throws Exception {
		try {
			users = UserAPI.getInstance().getHighscoreUsers();
		} catch (APIException e) {
			users = new ArrayList<User>();
			Toast.makeText(getActivity(), "Failed to load Users for Highscorelist", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onShow() throws Exception {
		initFields();
		ui.id(R.id.highscores_loading).gone();
	}
	
	private void initFields() {
		if (Utils.isNullOrEmpty(users)) {
			users = new ArrayList<User>();
		}
		
		LinearLayout usersList = (LinearLayout) ui.id(R.id.highscore_user_list).getView();

		if (usersList != null && usersList.getChildCount() > 0) {
			usersList.removeAllViews();
		}
		
		for (final User u : users) {
			// set standard values
			if(Utils.isNullOrEmpty(u.getRank())) u.setRank("not ranked");
			UiQuery uiq = new UiQuery(getActivity().getLayoutInflater().inflate(R.layout.list_item_highscore_user, null));
			if(u.getPicture() != null)
				uiq.id(R.id.highscore_user_avatar).image(TripitudeAPI.getBaseUrlWeb() + u.getPicture().getLocation(), false, false);
			else
				uiq.id(R.id.highscore_user_avatar).image(R.drawable.glyphicons_user);
			uiq.id(R.id.highscore_user).text(u.getName());
			uiq.id(R.id.highscore_user_rank).text(u.getRank());
			uiq.id(R.id.highscore_user_points).text(u.getPoints().toString() + " Points");
			usersList.addView(uiq.getRootView());
		}
	}

	@Override
	protected View getLoadingView() {
		return null;
	}

	@Override
	protected View getRealView() {
		return null;
	}
}
