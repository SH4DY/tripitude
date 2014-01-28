package at.tuwien.ase.tripidude.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.models.Comment;
import at.tuwien.ase.tripidude.utils.UiQuery;

public class CommentListAdapter extends ArrayAdapter<Comment> {
	private final Context context;
	private final List<Comment> comments;
	
	protected static UiQuery aq;
 
	public CommentListAdapter(Context context, List<Comment> values) {
		super(context, R.layout.list_item_comment, values);
		this.context = context;
		this.comments = values;
		
		aq = new UiQuery(App.activity);
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.list_item_comment, parent, false);
		
		Comment comment = comments.get(position);
		aq.id(R.id.comment_user).text(comment.getUser().getName());
		aq.id(R.id.comment_content).text(comment.getText());
 
		return rowView;
	}
}
