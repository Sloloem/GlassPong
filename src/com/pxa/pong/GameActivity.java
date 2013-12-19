package com.pxa.pong;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.google.android.glass.timeline.TimelineManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameActivity extends Activity {
	
	private static final String LiveCardId = "ponggame";
	private LiveCard _card;
	private PongSurfaceView _surface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this._surface = new PongSurfaceView(this);
		this.setContentView(this._surface);
	}
	@Override
	protected void onResume() {
		super.onResume();
		this._surface.onResumeMySurfaceView();
	}
	 
	@Override
	protected void onPause() {
		super.onPause();
		this._surface.onPauseMySurfaceView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	private class PongSurfaceView extends SurfaceView implements Runnable
	{
		Thread thread = null;
	    SurfaceHolder surfaceHolder;
	    volatile boolean running = false;
	    Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		public PongSurfaceView(Context context) {
			super(context);
			this.surfaceHolder = getHolder();
			whitePaint.setColor(Color.WHITE);
			whitePaint.setStyle(Paint.Style.STROKE);
			whitePaint.setStrokeWidth(200);
		}
		
		public void onResumeMySurfaceView(){
			running = true;
			thread = new Thread(this);
			thread.start();
		}
			   
		public void onPauseMySurfaceView(){
			boolean retry = true;
			running = false;
			while(retry){
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void run() {
			while(running)
			{
				if(surfaceHolder.getSurface().isValid()){
					Canvas canvas = surfaceHolder.lockCanvas();
					//... actual drawing on canvas
					
					int w = canvas.getWidth();
					int h = canvas.getHeight();
					
					int cw = w/2;
					int ch = h/2;
					
					canvas.drawRect(cw-10, ch-10, cw+10, ch+10, this.whitePaint);
					
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
		
	}

}
