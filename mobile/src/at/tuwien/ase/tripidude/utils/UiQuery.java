package at.tuwien.ase.tripidude.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.VideoView;

import com.androidquery.AbstractAQuery;

/**
 * Warps the great Android Query library
 * 
 * @author Matthias Schštta
 */
public class UiQuery extends AbstractAQuery<UiQuery> {
	public Activity activity;
	public View rootView;

	public UiQuery(View view) {
		super(view);
		rootView = view;
	}

	public UiQuery(Activity act) {
		super(act);
		activity = act;
	}

	public UiQuery(View view, Activity act) {
		super(act);
		activity = act;
		rootView = view;
	}

	public UiQuery layout(int id, int parentViewId) {
		if (activity == null) {
			return null;
		}

		ViewGroup container = (ViewGroup) id(parentViewId).getView()
				.getRootView();
		View layout = activity.getLayoutInflater()
				.inflate(id, container, false);

		if (layout == null) {
			return null;
		}

		return new UiQuery(layout);
	}

	/**
	 * this method translates just existing text into caps
	 * 
	 * @return this
	 */
	@SuppressLint("DefaultLocale")
	public UiQuery upperCase() {
		if (view == null) {
			return this;
		}
		if (!(view instanceof TextView))
			return this;

		CharSequence txt = ((TextView) view).getText();
		((TextView) view).setText(txt.toString().toUpperCase());

		return this;
	}

	public UiQuery padding(int left, int top, int right, int bottom){
		if (view == null) {
			return this;
		}
		view.setPadding(left, top, right, bottom);
		
		return this;
	}
	
	public UiQuery button(int resid) {
		if (view == null) {
			return this;
		}
		if (view instanceof CompoundButton) {
			((CompoundButton) view).setButtonDrawable(resid);
		}
		return this;
	}

	/**
	 * sets the alpha of the background drawable
	 * @param alpha (from 0 to 1)
	 * @return
	 */
	public UiQuery alpha(float alpha) {
		if (view == null) {
			return this;
		}

		if (view.getBackground()!=null)
			view.getBackground().setAlpha((int) (alpha * 255));

		return this;
	}

	public UiQuery addView(View v){
		if (view == null) {
			return this;
		}
		if (!(view instanceof ViewGroup)){
			return this;
		}
		((ViewGroup)view).addView(v);
		
		return this;
	}
	
	public UiQuery textOrGone(int textId) {
		if (view == null) {
			return this;
		}

		if (textId == 0 || view.getResources().getString(textId) == null
				|| view.getResources().getString(textId).length() == 0) {
			view.setVisibility(View.GONE);

		} else {
			((TextView) view).setText(textId);
			view.setVisibility(View.VISIBLE);
		}
		return this;
	}

	public UiQuery textOrGone(String text) {
		if (view == null) {
			return this;
		}

		if (text == null || text.length() == 0) {
			view.setVisibility(View.GONE);

		} else {
			((TextView) view).setText(text);
			view.setVisibility(View.VISIBLE);
		}
		return this;
	}

	public UiQuery textOrGone(Spanned text) {
		if (view == null) {
			return this;
		}

		if (text == null || text.length() == 0) {
			view.setVisibility(View.GONE);

		} else {
			((TextView) view).setText(text);
			view.setVisibility(View.VISIBLE);
		}
		return this;
	}

	public UiQuery htmlText(String text) {
		if (view == null) {
			return this;
		}

		if (text == null || text.length() == 0) {
			view.setVisibility(View.GONE);

		} else {
			((TextView) view).setText(Html.fromHtml(text));
			view.setVisibility(View.VISIBLE);
		}
		return this;
	}

	public int dipToPx(float dip) {
		if (view == null) {
			return 0;
		}

		DisplayMetrics metrics = view.getResources().getDisplayMetrics();

		return Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dip, metrics));
	}

	public UiQuery dither(boolean dither) {
		if (view == null)
			return this;
		if (view instanceof ImageView)
			((ImageView) view).getDrawable().setDither(dither);
		view.getBackground().setDither(dither);
		return this;
	}

	/**
	 * for accessibility support
	 * 
	 * @param contentDescription
	 * @return
	 */
	public UiQuery contentDescription(CharSequence contentDescription) {
		if (view == null)
			return this;

		view.setContentDescription(contentDescription);
		return this;
	}

	public UiQuery selected(boolean selected) {
		if (view == null) {
			return this;
		}

		if (view instanceof ImageView) {
			((ImageView) view).setSelected(selected);
		}

		if (view instanceof TextView) {
			((TextView) view).setSelected(selected);
		}
		return this;
	}
	
	public UiQuery activated(boolean activated) {
		if (view == null) {
			return this;
		}

		view.setActivated(activated);

		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UiQuery adapter(ListAdapter adapter) {
		if (view == null) {
			return this;
		}

		if (view instanceof AdapterView) {
			if (((AdapterView) view).getAdapter() != adapter) {
				((AdapterView) view).setAdapter(adapter);
			}
		}
		return this;
	}

	public UiQuery adapter(android.support.v4.view.PagerAdapter adapter) {
		if (view == null)
			return this;
		if (view instanceof ViewPager)
			if (((ViewPager) view).getAdapter() != adapter)
				((ViewPager) view).setAdapter(adapter);
		return this;
	}

	@SuppressWarnings("rawtypes")
	public Adapter getListAdapter() {
		if (view == null)
			return null;
		if (view instanceof AdapterView)
			return ((AdapterView) view).getAdapter();
		return null;
	}

	public android.support.v4.view.PagerAdapter getPagerAdapter() {
		if (view == null)
			return null;
		if (view instanceof ViewPager)
			return ((ViewPager) view).getAdapter();
		return null;
	}

	public ViewPager getViewPager() {
		if (view == null)
			return null;
		if (view instanceof ViewPager)
			return (ViewPager) view;
		return null;
	}

	public VideoView getVideoView() {
		if (view == null) {
			return null;
		}

		return (VideoView) view;
	}

	public boolean is(Class<?> viewClass) {
		return viewClass.isInstance(view);
	}

	@SuppressWarnings("unchecked")
	public <T> T as(Class<T> viewClass) {
		return (T) view;
	}

	public Rect scaleLike(int width, int height) {
		if (view == null) {
			return null;
		}

		Rect rect = new Rect();
		rect.left = 0;
		rect.top = 0;
		rect.right = view.getMeasuredWidth();
		rect.bottom = (int) (((float) height / (float) width) * view
				.getMeasuredWidth());

		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.height = rect.height();
		view.setLayoutParams(layoutParams);

		return rect;
	}

	public UiQuery background(Resources res, Bitmap bitmap) {
		if (view == null)
			return this;
		Utils.setBackground(view, new BitmapDrawable(res, bitmap));
		return this;
	}

	public View getRootView() {
		return rootView;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public UiQuery touched(View.OnTouchListener l) {
		this.view.setOnTouchListener(l);
		return this;
	}

	public ViewGroup getLayout() {
		if (rootView instanceof ViewGroup)
			return (ViewGroup) rootView;
		return null;
	}

	public UiQuery typefacePath(String fontPath) {
		if (view == null || Utils.isNullOrEmpty(fontPath))
			return this;
		Typeface titleFace = Typeface.createFromFile(new File(fontPath));
		((TextView) view).setTypeface(titleFace);
		return this;
	}
	
	public UiQuery toggleVisibility() {
		if (view == null)
			return this;

		if (view.isShown())
			gone();
		else
			visible();

		return this;
	}

	public UiQuery textGravity(int textGravity) {
		if (view == null)
			return this;
		((TextView) view).setGravity(textGravity);
		return this;
	}

	public void cleanUp() {
		if (view != null)
			cleanUp(view);
		if (rootView != null)
			cleanUp(rootView);
	}

	private void cleanUp(View view) {
		if (view != null) {
			if (view.getBackground() != null)
				view.getBackground().setCallback(null);

			if (view instanceof ImageView)
				if (((ImageView) view).getDrawable() != null)
					((ImageView) view).getDrawable().setCallback(null);

			if (view instanceof ImageButton)
				if (((ImageButton) view).getDrawable() != null)
					((ImageButton) view).getDrawable().setCallback(null);

			if (view instanceof TextView)
				((TextView) view).setTypeface(null);

			if (view instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
					cleanUp(((ViewGroup) view).getChildAt(i));

				try {
					((ViewGroup) view).removeAllViews();
				} catch (Exception e) {

				}
			}
		}
	}

	public UiQuery animate(Animation anim, AnimationListener listener) {
		if (anim == null)
			return this;
		anim.setAnimationListener(listener);
		return animate(anim);
	}

	public int getWidth() {
		if (view == null)
			return 0;
		return view.getWidth();
	}

	public int getHeight() {
		if (view == null)
			return 0;
		return view.getHeight();
	}
}