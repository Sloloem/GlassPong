package com.pxa.pong;

import java.util.Vector;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.google.android.glass.timeline.TimelineManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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
	
	private class Position
	{
		public int x;
		public int y;
		
		public Position(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	private class PongSurfaceView extends SurfaceView implements Runnable, SensorEventListener
	{
		Thread thread = null;
	    SurfaceHolder surfaceHolder;
	    volatile boolean running = false;
	    Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    Position dot;
	    Position PC;
	    Position NPC;
		
		public PongSurfaceView(Context context) {
			super(context);
			this.surfaceHolder = getHolder();
			whitePaint.setColor(Color.WHITE);
			whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
			whitePaint.setStrokeWidth(0);
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
					
					int w = canvas.getWidth();
					int h = canvas.getHeight();
					
					if (this.dot == null)
					{
						this.dot = new Position(w/2, h/2);
						this.PC = new Position(w-20, h/2);
						this.NPC = new Position(20, h/2);
					}
					
					//draw dashed line
					this.whitePaint.setStrokeWidth(5);
					this.whitePaint.setStyle(Paint.Style.STROKE);
					this.whitePaint.setPathEffect(new DashPathEffect(new float[]{10,20},0));
					this.whitePaint.setColor(Color.GRAY);
					canvas.drawLine(w/2, 0, w/2, h, this.whitePaint);
					this.whitePaint.setStrokeWidth(0);
					this.whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
					this.whitePaint.setPathEffect(null);
					this.whitePaint.setColor(Color.WHITE);
					
					//draw dot
					canvas.drawRect(this.dot.x-5, this.dot.y-5, this.dot.x+5, this.dot.y+5, this.whitePaint);
					
					//draw PC paddle
					canvas.drawRect(this.PC.x-5, this.PC.y-30, this.PC.x+5, this.PC.y+30, this.whitePaint);
					
					//draw NPC paddle
					canvas.drawRect(this.NPC.x-5, this.NPC.y-30, this.NPC.x+5, this.NPC.y+30, this.whitePaint);
					
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
