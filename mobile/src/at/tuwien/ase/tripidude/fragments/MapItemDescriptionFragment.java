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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.MainActivity.OnActivityResultListener;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.api.TripitudeAPI;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.fragments.MapItemFragment.MapItemPropertySetter;
import at.tuwien.ase.tripidude.models.Coordinate;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.File;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.models.Rating;
import at.tuwien.ase.tripidude.models.RatingCache;
import at.tuwien.ase.tripidude.models.RatingCacheMapItem;
import at.tuwien.ase.tripidude.models.utils.MapItemUtils;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.MediaFileHandler;
import at.tuwien.ase.tripidude.utils.RequestCodeGenerator;

public class MapItemDescriptionFragment extends FragmentController implements MapItemPropertySetter, OnActivityResultListener {

	private static final Integer POSITIVE_RATING = 1;
	private static final Integer NEGATIVE_RATING = -1;
	
	private MapItem mapItem;
	private Boolean staticMapLoaded = false;
	private Rating userRating = null;
	
	//For saving photos
	LinearLayout gallery;
	private static final int CAPTURE_IMAGE_REQCODE = 100;
	private static final int PICK_IMAGE_REQCODE = 200;
	private Uri fotoUri;
	private static final int IMAGE_RESOLUTION_WIDTH = 800;
	private static final int IMAGE_RESOLUTION_HEIGHT = 800;
	final int freshReqID = RequestCodeGenerator.getFreshInt();
	
	@Override
	protected int onDoCreateViewWithId() {		
		return R.layout.fragment_mapitem_description;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		((MainActivity) getActivity()).removeActivityResultListener(this);
	}

	@Override
	protected void onCreate() throws Exception {
		//identify fragment
		//set initial rating
		userRating = new Rating();
		userRating.setRating(0);
		//positive rating 
		ui.id(R.id.mapitem_rating_plus_btn).getView().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				if (userRating == null || userRating.getRating() <= 0) {
					PostRatingOperation postRatingOperation = new PostRatingOperation();
					postRatingOperation.execute(new Object[] {POSITIVE_RATING});
					userRating.setRating(1);
					updateUserRating();
				}
			}
		});
		
		((MainActivity) getActivity()).addActivityResultListener(this);

		//negative rating
		ui.id(R.id.mapitem_rating_minus_btn).getView().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (userRating == null || userRating.getRating() >= 0) {
					PostRatingOperation postRatingOperation = new PostRatingOperation();
					postRatingOperation.execute(new Object[] {NEGATIVE_RATING});
					userRating.setRating(-1);
					updateUserRating();
				}			
			}
		});
	}

	@Override
	protected void onQuery() throws Exception {
	}

	@Override
	protected void onShow() throws Exception {
		if (mapItem != null) {
			initFields();
			if (!staticMapLoaded) {
				setStaticMapImage(mapItem.getCoordinate());
				staticMapLoaded = true;
			}
		}
		ui.id(R.id.loading).visibility(View.GONE);
	}
	
	@Override
	public void onActivityResultFromActivity(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_REQCODE + freshReqID) {
			if (resultCode == Activity.RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(getActivity(),
						"Image saved to:\n" + fotoUri.getPath(),
						Toast.LENGTH_LONG).show();
				gallery.addView(insertPhoto(fotoUri.getPath()));
				//Send newly added picture to server
				Bitmap bitmap = BitmapFactory.decodeFile(fotoUri.getPath());
				SendNewlyAddedPictures task = new SendNewlyAddedPictures();
				task.execute(bitmap);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getActivity(), "Camera intent cancelled",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(), "Image not saved",
						Toast.LENGTH_LONG).show();
			}
		}

		if (requestCode == PICK_IMAGE_REQCODE + freshReqID
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
			
			//Send newly added picture to server
			Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
			SendNewlyAddedPictures task = new SendNewlyAddedPictures();
			task.execute(bitmap);
		} else if ((requestCode == PICK_IMAGE_REQCODE && resultCode == Activity.RESULT_CANCELED)
				|| (requestCode == PICK_IMAGE_REQCODE && data != null)) {
			Toast.makeText(getActivity(), "Image could not be selected",
					Toast.LENGTH_LONG).show();
		}

	}
	
	/**
	 * Init content fields
	 */
	private void initFields() {
		ui.id(R.id.mapitem_title).text(mapItem.getTitle());
		ui.id(R.id.mapitem_categories).text(MapItemUtils.getStringListForMapItemCategoryList(mapItem.getCategories()));
		
		setPictures();
		
		ui.id(R.id.mapitem_user).text(R.string.created_by_title, mapItem.getUser().getName()); 
		ui.id(R.id.mapitem_description).text(mapItem.getDescription());
		
		//update rating view
		updateUserRating();
		
		//get user rating
		GetRatingFromCurrentUserOperation getRatingOperation = new GetRatingFromCurrentUserOperation();
		getRatingOperation.execute("");
		
		//If the user created this MapItem --> show edit button
		if(UserAPI.getCurrentUser().getId() == mapItem.getUser().getId()){
			ui.id(R.id.edit_hotspot_button).visible();
			ui.id(R.id.edit_hotspot_button).clicked(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle args = new Bundle();
					args.putLong(CreateMapItemFragment.EDITMODE_IDENTIFIER, mapItem.getId());
					CreateMapItemFragment fragment = new CreateMapItemFragment();
					fragment.setArguments(args);
					((MainActivity) App.activity).addFragmentController(fragment, 1);
				}
			});
		} else {
			List<MapItem> temp = new ArrayList<MapItem>();
			temp.add(mapItem);
			temp = ((MainActivity) getActivity()).getMapFragment().getNearMapItemsByDistance(temp, MapFragment.HOTSPOT_CHECKIN_DISTANCE);
			if(temp.contains(mapItem)) {
				ui.id(R.id.checkin_hotspot_button).visible();
				ui.id(R.id.checkin_hotspot_button).clicked(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Toast.makeText(getActivity(), "Try to Check-in",
								Toast.LENGTH_LONG).show();
						CheckInOperation check = new CheckInOperation();
						check.execute(mapItem);
					}
				});
			}
		}
	}
	
	private void setPictures() {
		//set gallery view		
		gallery = (LinearLayout) ui.id(R.id.mapitem_gallery).getView();
		//remove childs if some exist
		if (gallery.getChildCount() > 0) {
			gallery.removeAllViews();
		}
		
		addPictureButton(gallery);
		
		int c = 1;
		for (File file : mapItem.getPictures()) {
			LinearLayout layout = new LinearLayout(getActivity());
			layout.setLayoutParams(new LayoutParams(240, 220));
			layout.setGravity(Gravity.CENTER);

			ImageView imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new LayoutParams(220, 220));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setId(c++);
			ui.id(imageView).image(TripitudeAPI.getBaseUrlWeb() + file.getLocation());
			
			layout.addView(imageView);
			Log.info(this, TripitudeAPI.getBaseUrlWeb() + file.getLocation());
			gallery.addView(layout);
			
			//set listener
			ui.id(imageView).getView().setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					List<String> urlList = new ArrayList<String>();
					for (File file : mapItem.getPictures()) {
						urlList.add(TripitudeAPI.getBaseUrlWeb() + file.getLocation());
					}
					
					Bundle args = new Bundle();
					args.putStringArrayList(GalleryFragment.PICTURE_URLS, (ArrayList<String>) urlList);
					args.putInt(GalleryFragment.INITIAL_PAGE, v.getId());
					GalleryFragment fragment = new GalleryFragment();
					fragment.setArguments(args);
					
					((MainActivity) App.activity).addFragmentController(fragment, 1);
				}
			});
		}
	}
	
	private View insertPhoto(String path){
		Bitmap bm = MediaFileHandler.decodeSampledBitmapFromUri(path,
				IMAGE_RESOLUTION_WIDTH, IMAGE_RESOLUTION_HEIGHT);

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(240, 240));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getActivity());
		imageView.setLayoutParams(new LayoutParams(220, 220));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
	}
	
	private void addPictureButton(LinearLayout gallery){
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(240, 220));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getActivity());
		imageView.setLayoutParams(new LayoutParams(140, 140));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setId(0);
		
		ui.id(imageView).image(R.drawable.ic_action_add_to_queue);
		ui.id(imageView).clicked(new OnClickListener() {
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
										CAPTURE_IMAGE_REQCODE + freshReqID);
							}
						}).setNegativeButton("From Gallery",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								getActivity().startActivityForResult(intent, PICK_IMAGE_REQCODE + freshReqID);
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});
		layout.addView(imageView);
		gallery.addView(layout);
	}
	
	/**
	 * Set static image 
	 * @param coord
	 * @throws Exception
	 */
	private void setStaticMapImage(Coordinate coord) throws Exception {		
		String url = "http://maps.googleapis.com/maps/api/staticmap?markers=";
		url += coord.getLatitude().toString() + ","+ coord.getLongitude().toString();
		url += "&zoom=17&size=180x180&sensor=false";
		
		ui.id(R.id.static_map_image).image(url);
	}
	
	/**
	 * update user rating view
	 */
	private void updateUserRating() {
		if (ui == null) {
			return;
		}
		
		//set rating counters
		RatingCache ratingCache = mapItem.getRatingCache();
		
		String plusCounter = "0";
		String minusCounter = "0";
		
		if (ratingCache != null) {
			Double minus = (ratingCache.getNumRatings() - ratingCache.getSum())/2; 
			Double plus = ratingCache.getNumRatings() - minus;
			
			plusCounter = String.valueOf(plus.intValue());
			minusCounter = String.valueOf(minus.intValue());			
		}
		
		ui.id(R.id.mapitem_rating_plus_counter).text(plusCounter);
		ui.id(R.id.mapitem_rating_minus_counter).text(minusCounter);
		
		if (userRating == null) {
			return;
		}
		//set rating from current user by setting the button background
		ui.id(R.id.mapitem_rating_minus_btn).background(R.drawable.btn_rating);
		ui.id(R.id.mapitem_rating_plus_btn).background(R.drawable.btn_rating);
		if (userRating.getRating() >= POSITIVE_RATING) {
			ui.id(R.id.mapitem_rating_plus_btn).background(R.drawable.btn_rating_plus);
			
		}
		else if (userRating.getRating() <= NEGATIVE_RATING) {
			ui.id(R.id.mapitem_rating_minus_btn).background(R.drawable.btn_rating_minus);		
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

	@Override
	public void setMapItem(MapItem mapItem) {
		this.mapItem = mapItem;
	}

	@Override
	public void setMapItemEvents(List<Event> events) {
	}
	
	/**
	 * Get user rating operation
	 * @author dietl_ma
	 *
	 */
	private class GetRatingFromCurrentUserOperation extends AsyncTask<String, Void, Rating>  {

		@Override
		protected Rating doInBackground(String... params) {
			Rating rating = null;
			try {
				rating = MapItemAPI.getInstance().getRatingByMapItemAndUser(mapItem.getId(), UserAPI.getCurrentUser().getId());
			} catch (APIException e) {
				Log.error("MapItemDescriptionFragment", "Error loading rating");
			}
			return rating;
		}

		@Override
		protected void onPostExecute(Rating result) {
			
			if(result != null){
				userRating = result;
				updateUserRating();
			}
		}
	}
	
	/**
	 * Get user rating operation
	 * @author dietl_ma
	 *
	 */
	private class PostRatingOperation extends AsyncTask<Object, Void, RatingCacheMapItem>  {
		
		@Override
        protected void onPreExecute() {
		}
		
		@Override
		protected RatingCacheMapItem doInBackground(Object... params) {
			RatingCacheMapItem ratingCacheMapItem = null;
			try {
				ratingCacheMapItem = MapItemAPI.getInstance().postMapiItemRating(mapItem.getId(), (Integer) params[0]);
		
			} catch (APIException e) {
				Log.error("MapItemDescriptionFragment", "Error loading rating");
			}
			return ratingCacheMapItem;
		}

		@Override
		protected void onPostExecute(RatingCacheMapItem result) {
			if(result != null){
				mapItem.setRatingCache(result);
				updateUserRating();
			}
		}
	}
	
	private class SendNewlyAddedPictures extends AsyncTask<Bitmap, Void, Boolean>  {
		
		@Override
        protected void onPreExecute() {
		}
		
		@Override
		protected Boolean doInBackground(Bitmap... bitmaps) {
			try {
				List<Bitmap> bitmapsList = new ArrayList<Bitmap>();
				for (Bitmap bitmap : bitmaps) {
					bitmapsList.add(bitmap);
				}
				Log.debug("SendNewlyAddedPics", "Execute task to send new picture. Size of bitmapsList " + bitmapsList.size());
				MapItemAPI.getInstance().saveImagesToMapItem(mapItem.getId().toString(), bitmapsList);
			} catch (APIException e) {
				Log.error("MapItemDescriptionFragment", "Error while saving newly added Pictures to MapItem");
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
		}
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
