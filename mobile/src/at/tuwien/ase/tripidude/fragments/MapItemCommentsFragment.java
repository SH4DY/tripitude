package at.tuwien.ase.tripidude.fragments;

import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.fragments.MapItemFragment.MapItemPropertySetter;
import at.tuwien.ase.tripidude.models.Comment;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.MapItem;

public class MapItemCommentsFragment extends FragmentController implements MapItemPropertySetter {

	private MapItem mapItem;
	private Comment newComment;
	
	@Override
	protected int onDoCreateViewWithId() {		
		return R.layout.fragment_mapitem_comments;
	}

	@Override
	protected void onCreate() throws Exception {
		
		ui.id(R.id.mapitem_comment_post_btn).getButton().setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				openCommentForm();			
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
	protected void onShow() throws Exception {
		if (mapItem != null) {
			initFields();
		}
	}

	/**
	 * Init content fields
	 */
	private void initFields() {
		
		//set comment list		
		LinearLayout commentsView = (LinearLayout) ui.id(R.id.mapitem_comment_list).getView();
		//remove childs if some exist
		if (commentsView != null && commentsView.getChildCount() > 0) {			
			commentsView.removeAllViews();
		}
		LayoutInflater inflater = (LayoutInflater) App.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//get current date to calculate comment time span
		Date now = new Date(System.currentTimeMillis());
		
		for (int i = mapItem.getComments().size()-1; i >= 0; i--) {
           
			Comment comment = mapItem.getComments().get(i);
			
			View rowView = inflater.inflate(R.layout.list_item_comment, null);
			commentsView.addView(rowView);
			TextView commentUser = (TextView) rowView.findViewById(R.id.comment_user);
			TextView commentTimeAgo = (TextView) rowView.findViewById(R.id.comment_time_ago);
			
			commentUser.setText(comment.getUser().getName());
			commentTimeAgo.setText(DateUtils.getRelativeTimeSpanString(comment.getCreated().getTime(), now.getTime() + 1,  0));
			
			TextView comment_content = (TextView) rowView.findViewById(R.id.comment_content);
			comment_content.setText(comment.getText());
		
		}
		
		//hide comment button if user isn´t logged in
		if (UserAPI.getCurrentUser() == null) {
			ui.id(R.id.mapitem_comment_post_btn).visibility(View.INVISIBLE);
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
	
	/**
	 * Creates comment form in dialog
	 */
	private void openCommentForm() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View commentForm = inflater.inflate(R.layout.dialog_post_comment, null);
		
		builder.setView(commentForm)
			.setTitle(R.string.post_comment)
			.setPositiveButton(R.string.post, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
            	   
            	   //Post comment            			   
            	   EditText commentField = (EditText) commentForm.findViewById(R.id.post_comment_field);
            	   String text = commentField.getText().toString();
            	   if (text.length() > 0) {  
            		 
            		   PostCommentOperation op = new PostCommentOperation();
            		   op.execute(new Object[] {mapItem, text});
            	   }
            	   
               }
           })
           .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   
               }
           });      
		builder.create().show();
		
	}
	
	private class PostCommentOperation extends AsyncTask<Object, Void, String>  {
		@Override
        protected void onPreExecute() {
			Toast.makeText(getActivity(), R.string.post_comment_progress,
					Toast.LENGTH_SHORT).show();
		}
		
		@Override
		protected String doInBackground(Object... params) {
			
			MapItem mapItem = (MapItem) params[0];
			String commentContent = (String) params[1];
			
			String success = Boolean.toString(true);
			
			newComment = null;
			try {
				newComment = MapItemAPI.getInstance().postMapItemComment(mapItem.getId(), commentContent);
			} catch (APIException e) {
				success = Boolean.toString(false);			
			}
			
			if (newComment == null) {
				success = Boolean.toString(false);
			}
			
			return success;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			
			if (Boolean.valueOf(result)) {	
				
				Toast.makeText(getActivity(), R.string.comment_saved,
						Toast.LENGTH_SHORT).show();
				
				//add comment to list
				mapItem.getComments().add(newComment);
				initFields();
			}

		}
	}

	@Override
	public void setMapItemEvents(List<Event> events) {
	}

}
