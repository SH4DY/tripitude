package at.tuwien.ase.tripidude.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.MainActivity.OnActivityResultListener;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.Hotspot;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.models.MapItemCategory;
import at.tuwien.ase.tripidude.models.Route;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.MediaFileHandler;

import com.google.android.gms.maps.model.LatLng;

/**
 * This fragment is currently used to create a new hotspot or route (all fields
 * are empty and can be edited by the user, addtionally he/she can add
 * pictures). The fragment is ALSO used to edit existing hotspots. Navigation
 * workflow is MapFragment - MapItemDescriptionFragment(embedded in
 * MapItemFragment) - this. In the "editmode" the user sees the values of this
 * mapitem (freshly recieved from the server) but can not add or remove
 * pictures.
 * 
 * @author Shady
 * 
 */
public class CreateMapItemFragment extends FragmentController implements OnActivityResultListener {

	static LatLng _coords;
	private List<String> _mapItemCategories;

	// In case this fragment is used to edit an existing MapItem
	public static final String EDITMODE_IDENTIFIER = "editmode";
	private boolean editmode;
	private Long mapitemId;
	private MapItem mapitemToUpdate;

	// For saving photos
	private List<Bitmap> addedPhotos;
	private static final int CAPTURE_IMAGE_REQCODE = 100;
	private static final int PICK_IMAGE_REQCODE = 200;
	private Uri fotoUri;
	private static final int IMAGE_RESOLUTION_WIDTH = 800;
	private static final int IMAGE_RESOLUTION_HEIGHT = 800;

	private SaveHotspot save;

	private LinearLayout gallery;
	private Route route;
	private String mapItemTypeName;

	static private String _title;
	static private String _category;
	static private String _description;
	static private List<String> _categories;
	static private Double _cost;
	static private boolean _isRoute;
	static private boolean _addToRoute;

	public static FragmentController newInstance(LatLng coords,
			boolean addToRoute, boolean isRoute) {
		Bundle bundle = new Bundle();

		FragmentController events = new CreateMapItemFragment();
		events.setArguments(bundle);

		_coords = coords;
		_isRoute = isRoute;
		_addToRoute = addToRoute;
		return events;
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_hotspot;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (_isRoute) {
			route = App.mapListener.getCurrentlyCreatedRoute();
			mapItemTypeName = getString(R.string.route);
		} else {
			mapItemTypeName = getString(R.string.hotspot);
			if (args.getLong(EDITMODE_IDENTIFIER) != 0L) {
				mapitemId = args.getLong(EDITMODE_IDENTIFIER);
				editmode = true;
			}
		}
	}

	@Override
	protected void onCreate() throws Exception {
		//Register with the main activity to intercept photo intents
		((MainActivity) getActivity()).addActivityResultListener(this);
		
		addedPhotos = new ArrayList<Bitmap>();
		// Save button
		ui.id(R.id.save_hotspot_button).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String costAsString = ui.id(R.id.cost_field).getText()
						.toString();
				_title = ui.id(R.id.name_field).getText().toString();
				_category = ui.id(R.id.category_spinner).getSelectedItem()
						.toString();
				_description = ui.id(R.id.description_field).getText()
						.toString();

				_categories = new ArrayList<String>();
				_categories.add(_category);

				_cost = 0.0;
				try {
					if (!costAsString.equals("")) {
						_cost = Double.valueOf(costAsString);
					}// else cost stays on 0.0 or an exc is thrown
				} catch (NumberFormatException e) {
					Log.debug("HotspotFragment",
							"Error during parsing from string to double");

					Toast.makeText(getActivity(),
							getString(R.string.cost_error), Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (!_title.equals("")) {
					if(editmode){
						UpdateHotspot updateHotspot = new UpdateHotspot();
						updateHotspot.execute("");
						Toast.makeText(getActivity(), getString(R.string.updating),
								Toast.LENGTH_SHORT).show();
					}else{
						save = new SaveHotspot();
						save.execute();
						Toast.makeText(getActivity(), getString(R.string.saving),
								Toast.LENGTH_SHORT).show();
					}
					

					// Alert for successful creation of hotSpot
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getActivity());
					alertDialogBuilder.setTitle(String.format("%s %s",
							mapItemTypeName, getString(R.string.saved)));
					alertDialogBuilder.setPositiveButton(
							getString(R.string.back_to_map),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (_isRoute) {
										// reset Map
										((MainActivity) getActivity())
												.showMap(true);
									} else {
										getActivity().onBackPressed();
									}
								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();

				} else {
					Toast.makeText(getActivity(),
							getString(R.string.name_title_error),
							Toast.LENGTH_LONG).show();
				}

			}
		});

		ui.id(R.id.hotspot_cancel_button).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});

	}

	@Override
	protected void onQuery() throws Exception {
		// Retrieve categories from server
		List<MapItemCategory> temp = MapItemAPI.getInstance().getCategories();
		_mapItemCategories = new ArrayList<String>();
		for (MapItemCategory mapItemCategory : temp) {
			_mapItemCategories.add(mapItemCategory.getName());
		}
		// Get the editable hotSpot if in editMode
		if (editmode) {
			mapitemToUpdate = MapItemAPI.getInstance().getHotspot(mapitemId);
		}
	}

	@Override
	protected void onShow() throws Exception {

		// Category spinner
		Spinner spinner = (Spinner) getView().findViewById(
				R.id.category_spinner);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, _mapItemCategories);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		if (editmode) {
			preFillFields(adapter);
			return;
		}

		// Horizontal Scrollview for Gallery
		gallery = (LinearLayout) getView().findViewById(R.id.gallery);

		// Add photo button
		View addFotoView = insertPhoto(R.drawable.ic_action_add_to_queue);
		ui.id(addFotoView).clicked(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(getActivity(),
						R.anim.image_click));
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle("Capture");
				alertDialogBuilder.setPositiveButton("From Camera",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);

								fotoUri = MediaFileHandler
										.getOutputMediaFileUri(1, getActivity());
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										fotoUri);

								// start the image capture Intent
								getActivity().startActivityForResult(intent,
										CAPTURE_IMAGE_REQCODE);
							}
						}).setNegativeButton("From Gallery",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent i = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								getActivity().startActivityForResult(i, PICK_IMAGE_REQCODE);
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});
		gallery.addView(addFotoView);
	}

	private void preFillFields(ArrayAdapter<String> adapter) {
		ui.id(R.id.name_field).getEditText().setText(mapitemToUpdate.getTitle());
		ui.id(R.id.description_field).getEditText()
				.setText(mapitemToUpdate.getDescription());
	}

	@Override
	protected View getLoadingView() {
		return null;
	}

	@Override
	protected View getRealView() {
		return null;
	}

	private View insertPhoto(int id) {
		Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(),
				id);
		addedPhotos.add(bm);

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(250, 250));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getActivity());
		imageView.setLayoutParams(new LayoutParams(220, 220));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
	}

	private View insertPhoto(String path) {
		Bitmap bm = MediaFileHandler.decodeSampledBitmapFromUri(path,
				IMAGE_RESOLUTION_WIDTH, IMAGE_RESOLUTION_HEIGHT);
		addedPhotos.add(bm);

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(250, 250));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getActivity());
		imageView.setLayoutParams(new LayoutParams(220, 220));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CAPTURE_IMAGE_REQCODE) {
			if (resultCode == Activity.RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(getActivity(),
						"Image saved to:\n" + fotoUri.getPath(),
						Toast.LENGTH_LONG).show();
				gallery.addView(insertPhoto(fotoUri.getPath()));
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getActivity(), "Camera intent cancelled",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(), "Image not saved",
						Toast.LENGTH_LONG).show();
			}
		}

		if (requestCode == PICK_IMAGE_REQCODE
				&& resultCode == Activity.RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			gallery.addView(insertPhoto(picturePath));
		} else if ((requestCode == PICK_IMAGE_REQCODE && resultCode == Activity.RESULT_CANCELED)
				|| (requestCode == PICK_IMAGE_REQCODE && data != null)) {
			Toast.makeText(getActivity(), "Image could not be selected",
					Toast.LENGTH_LONG).show();
		}

	}

	private class SaveHotspot extends
			android.os.AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			MapItem savedMapItem = null;
			if (_isRoute) {
				route.setTitle(_title);
				route.setDescription(_description);
				savedMapItem = MapItemAPI.getInstance().createRoute(route);
			} else {
				try {
					savedMapItem = MapItemAPI.getInstance().createHotspot(
							_title, _description, _categories, _cost,
							_coords.latitude, _coords.longitude);
				} catch (APIException e) {
					Log.error("CreateMapItem",
							"Error creating hotspot (possibly something went wrong receiving categories)");
					e.printStackTrace();
				}
			}
			if (savedMapItem.getId() == null || savedMapItem == null) {
				return false;
			}

			if (!_isRoute && _addToRoute) {
				App.mapListener.getCurrentlyCreatedRoute().getHotspots()
						.add((Hotspot) savedMapItem);
			}

			// First image is the AddButton, we dont want this one to be saved
			// with the MapItem
			List<Bitmap> photosToSend = addedPhotos.subList(1,
					addedPhotos.size());

			if (photosToSend.size() > 0) {

				boolean imagesSaved;

				try {
					imagesSaved = MapItemAPI.getInstance().saveImagesToMapItem(
							savedMapItem.getId().toString(), photosToSend);
				} catch (APIException e) {
					e.printStackTrace();
					return false;
				}
				return imagesSaved;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

	private class UpdateHotspot extends
			android.os.AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			Hotspot hotspotToUpdate = (Hotspot) mapitemToUpdate;
			MapItem updatedMapItem = null;
			
			try {
				mapitemToUpdate.setTitle(_title);
				mapitemToUpdate.setDescription(_description);
				updatedMapItem = MapItemAPI.getInstance().updateHotspot(hotspotToUpdate);
			} catch (APIException e) {
				Log.error("CreateMapItem",
						"Error updating hotspot");
				e.printStackTrace();
			}

			if (updatedMapItem.getId() == null || updatedMapItem == null) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

	@Override
	public void onActivityResultFromActivity(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == CAPTURE_IMAGE_REQCODE) {
			if (resultCode == Activity.RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(getActivity(),
						"Image saved to:\n" + fotoUri.getPath(),
						Toast.LENGTH_LONG).show();
				gallery.addView(insertPhoto(fotoUri.getPath()));
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getActivity(), "Camera intent cancelled",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(), "Image not saved",
						Toast.LENGTH_LONG).show();
			}
		}

		if (requestCode == PICK_IMAGE_REQCODE
				&& resultCode == Activity.RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			gallery.addView(insertPhoto(picturePath));
		} else if ((requestCode == PICK_IMAGE_REQCODE && resultCode == Activity.RESULT_CANCELED)
				|| (requestCode == PICK_IMAGE_REQCODE && data != null)) {
			Toast.makeText(getActivity(), "Image could not be selected",
					Toast.LENGTH_LONG).show();
		}		
	}
}
