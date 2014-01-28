package at.tuwien.ase.tripidude.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;

public class DrawerLayout extends android.support.v4.widget.DrawerLayout {
	
	public DrawerLayout(Context context) {
		super(context);
	}

	public DrawerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			if (this.isDrawerOpen(Gravity.START)) {
				this.closeDrawers();
				return true;
			}
			if (this.isDrawerOpen(Gravity.END)) {
				this.closeDrawers();
				return true;
			}			
		}
		return super.onKeyUp(keyCode, event);
	}
}
