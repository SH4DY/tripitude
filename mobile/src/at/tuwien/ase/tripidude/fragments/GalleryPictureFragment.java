
package at.tuwien.ase.tripidude.fragments;

import android.os.Bundle;
import android.view.View;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.core.FragmentController;

public class GalleryPictureFragment extends FragmentController {

	public static final String PICTURE_URL = "pictureUrl";
	
	private Bundle args;
	private String url;
	
	@Override
	protected int onDoCreateViewWithId() {		
		return R.layout.fragment_gallery_picture;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		args = getArguments();		
		url = args.getString(PICTURE_URL);
	}
	
	@Override
	protected void onCreate() throws Exception {}
	
	@Override
	protected boolean isQueryNeeded() {
		return false;
	}

	@Override
	protected void onQuery() throws Exception {}

	@Override
	protected void onShow() throws Exception {
		ui.id(R.id.galler_picture).image(url);
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
