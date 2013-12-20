package com.pxa.pong;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameActivity extends Activity {
	
	private PongSurfaceView _surface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this._surface = new PongSurfaceView(this);
		this.setContentView(this._surface);
		this._surface.setKeepScreenOn(true);
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
	    Position dotVector;
	    int h;
	    int w;
	    int PCScore;
	    int NPCScore;

	    float originalZPosition;

		private final SensorManager _sensorManager;
	    private final Sensor _vectorRotationSensor;
		
		public PongSurfaceView(Context context) {
			super(context);
			this.surfaceHolder = getHolder();
			whitePaint.setColor(Color.WHITE);
			whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
			whitePaint.setStrokeWidth(0);
			whitePaint.setTextSize(40);
			
			this._sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
			this._vectorRotationSensor = this._sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			
			this.dotVector = new Position(5, 1);
		}
		
		public void onResumeMySurfaceView(){
			running = true;
			this._sensorManager.registerListener(this, this._vectorRotationSensor, SensorManager.SENSOR_DELAY_UI);
			thread = new Thread(this);
			thread.start();
		}
			   
		public void onPauseMySurfaceView(){
			boolean retry = true;
			running = false;
			this._sensorManager.unregisterListener(this);
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
					canvas.drawColor(Color.BLACK);
					
					this.w = canvas.getWidth();
					this.h = canvas.getHeight();
					
					if (this.dot == null)
					{
						this.dot = new Position(w/2, h/2);
						this.PC = new Position(w-20, h/2);
						this.NPC = new Position(20, h/2);
					}
					
					//move dot
					this.dot.x += this.dotVector.x;
					this.dot.y += this.dotVector.y;
					
					//check for top and bottom bounce
					if ((this.dot.y >= this.h) || (this.dot.y <= 0))
					{
						Log.v("DRAW","TopOrBottom Bounce!");
						this.dotVector.y *= -1;
					}
					
					//check for paddle collision
					if ((this.PC.x - this.dot.x) <= 5 && (this.PC.x - this.dot.x) >= 0)
					{ //Less than 5 and greater than zero should be an appropriate range for a hit
						Log.v("DRAW", "Near paddle");
						//Is it near enough on the Y?
						int yDelta = this.PC.y - this.dot.y;
						//0 is dead center, between -30 and 30 should be somewhere along the surface.
						//more extreme angles should result in a change of angle of vector
						switch ((int)Math.abs(yDelta/10))
						{
						case 0:
							break;
						case 1:
							break;
						case 2:
							break;
						}
						
						if (Math.abs(yDelta) < 30)
						{
							Log.v("DRAW", "Side paddle bounce");
							this.dotVector.x *= -1;
						}
					}
					
					//check for falling off the side
					if (this.dot.x < 0)
					{
						Log.v("DRAW","Dot has fallen off NPC side");
						this.PCScore++;
						this.resetDot();
					}
					else if (this.dot.x > this.w)
					{
						Log.v("DRAW","Dot has fallen off PC side");
						this.NPCScore++;
						this.resetDot();
					}
					
					//draw scores
					canvas.drawText(String.format("%02d", this.PCScore), (this.w/2)-65, 40, this.whitePaint);
					canvas.drawText(String.format("%02d", this.NPCScore), (this.w/2)+45, 40, this.whitePaint);
					
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
		
		private void resetDot()
		{
			this.dot.x = this.w/2;
			this.dot.y = this.h/2;
			this.dotVector = new Position(5, 1);
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			Log.v("GameActivity", "Accuracy Changed: "+arg1);
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (this.PC == null)
				return; //We haven't managed to initialize the screen yet.
			if (this.originalZPosition == 0)
			{ //Fake some auto-calibration by assuming wherever we are at first is level.
				this.originalZPosition = event.values[2];
			}
			//Translate change relative to original Z to absolute Y pos of PC paddle.
			float delta = this.originalZPosition - event.values[2];
			
			this.PC.y = (int) ((this.h/2) - (delta*1500));
		}
		
	}

}
