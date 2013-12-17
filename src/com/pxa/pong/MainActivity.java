package com.pxa.pong;

import com.google.android.glass.app.Card;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v(this.getLocalClassName(), "onKeyDown called with: "+keyCode);
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			Log.v(this.getLocalClassName(), "CENTER, showing options");
            openOptionsMenu();
            return true;
        }
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			super.onKeyDown(keyCode, event);
			return true;
		}
        return false;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pong, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        switch (item.getItemId()) {
        	case R.id.play_new:
        		return true;
        	case R.id.stop:
        		this.finish();
        		return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Implement if needed
		return true;
    }

}
