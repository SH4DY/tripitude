package at.tuwien.ase.tripidude.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public abstract class ModelsAdapter<ModelType> extends
		BaseAdapter {
	private LayoutInflater layoutInflater;
	private int layoutId;
	protected List<ModelType> models;

	public ModelsAdapter(Context context, int layoutId,
			List<ModelType> models) {
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
		setModels(models);
	}

	public ModelsAdapter(List<ModelType> models) {
		setModels(models);
	}

	private void setModels(List<ModelType> stores) {
		this.models = stores;
	}

	public void changeModels(List<ModelType> models) {
		setModels(models);
		notifyDataSetChanged();
	}

	public List<ModelType> getModels() {
		return models;
	}

	@Override
	public int getCount() {
		if (models == null)
			return 0;
		else
			return models.size();
	}

	@Override
	public ModelType getItem(int position) {
		return models.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (position >= models.size())
			return 0;
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ModelType model = models.get(position);
		convertView = createView(position, model, convertView, parent);
		UiHolder ui = (UiHolder) convertView.getTag();
		fillView(position, model, convertView, ui);
		return convertView;
	}

	protected View createView(int position, ModelType model, View convertView,
			ViewGroup parent) {
		if (convertView != null)
			return convertView;
		if (layoutInflater == null || layoutId == 0)
			return convertView;
		convertView = layoutInflater.inflate(layoutId, parent, false);
		UiHolder ui = new UiHolder(convertView);
		convertView.setTag(ui);
		return convertView;
	}

	@SuppressLint("UseSparseArrays")
	public class UiHolder extends UiQuery {

		Map<Integer, View> viewHolder = new HashMap<Integer, View>();

		public UiHolder(View view) {
			super(view);
		}

		public UiHolder id(int id) {
			View result = null;
			if (this.viewHolder != null)
				result = viewHolder.get(id);
			if (result == null) {
				result = super.id(id).getView();
				viewHolder.put(id, result);
			} else
				super.id(result);
			return this;
		}

		/**
		 * Gets the current view as a CheckedTextView.
		 * 
		 * @return CheckedTextView
		 */
		public CheckedTextView getCheckedTextView() {
			return (CheckedTextView) view;
		}
	}

	protected abstract void fillView(int position, ModelType model, View view,
			UiHolder ui);
}
