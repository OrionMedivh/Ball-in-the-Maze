package myMaze;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MainActivity extends Activity implements SensorEventListener 
{
    /** Called when the activity is first created. */
	private SensorManager mSensorManager;
	private SurfaceHolder mHolder;
	private float xSensor=0;
	private float ySensor=0;
	private Maze numMaze;
	private Ball ball;
	private int height=0,width=0;//global variable, not good, but seems no other way.
	private StringBuffer str1,str2;
	private int timer;
	private boolean interrupt = false;
	private boolean touch=false; // whether touched screen after finish?
	private boolean finish=false; // whether finish the game?
	private boolean best=false; //whether found a better score?

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
        
        //Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		// add listener. 
        mSensorManager.registerListener(this, 
        		mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
    }
    
	private int loadSavedPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		return sharedPreferences.getInt("Btime", 86400000);
	}
	
	private void savePreferences(String key, int value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
    
    class MyView extends SurfaceView implements SurfaceHolder.Callback
    {
    	public MyView(Context context) 
    	{
    		super(context);
    		mHolder = getHolder();
    		mHolder.addCallback(this);
    	}
    	@Override
    	public void surfaceChanged(SurfaceHolder holder, int format, int width,
    			int height) {
    	}
    	@Override
    	public void surfaceCreated(SurfaceHolder holder) 
    	{	
    		height=getHeight();
    		width=getWidth();
    		numMaze=new Maze();
    		numMaze.setSize(20,20);
    		numMaze.setWindow(height,width);
    		numMaze.createMaze(height,width);
    		ball = new Ball();
    		ball.setRadius(numMaze.getSize()/2);
    		ball.setCorner(numMaze.getTop(), numMaze.getBottom(), numMaze.getLeft(), numMaze.getRight()); 	
   			ball.initialize();
   			timer= 0;
    		new Thread(new MyThread()).start(); 		
    	}
    	@Override
    	public void surfaceDestroyed(SurfaceHolder holder) {
    		
    	}
    }
    
    @Override
    protected void onDestroy() {
    	interrupt = true;
    	mSensorManager.unregisterListener(this);
        super.onDestroy();
//        Thread.currentThread().interrupt();
    }  
    
    
    
    
    class MyThread implements Runnable{
    	@Override
        public void run() {
    		Canvas canvas = null;
    		
           while (true) {
        	   if(interrupt){
        		   break;
        	   }else{
        		   int Btime=loadSavedPreferences();
	        	   while (ball.getI()!=numMaze.getRow()-1 || ball.getJ()!=numMaze.getColumn()-1){
	               try {
		           		canvas = mHolder.lockCanvas();//get Canvas
		           		canvas.drawColor(Color.WHITE);//Set background color
		           		Paint mPaint = new Paint();
		          
		           		mPaint.setColor(Color.BLACK);//set pen color
		//           		mPaint.setStyle(Paint.Style.STROKE);//framed instead of filled.
		           		
		           		str1=new StringBuffer();
		           		str2=new StringBuffer();
		           		
		           		str1.append("Best Time= "+Btime/1000%(60*60*60)/(60*60)+":"+Btime/1000%(60*60)/60+":"+Btime/1000%60+"."+Btime/100%10);
		           		str2.append("Time = "+timer/1000%(60*60*60)/(60*60)+":"+timer/1000%(60*60)/60+":"+timer/1000%60+"."+timer/100%10);
		           		mPaint.setTextSize(24); 
		           		canvas.drawText(str1.toString(), (width-12*str1.toString().length())/2, (height-numMaze.getRight()+numMaze.getOffset())/2, mPaint);
		           		canvas.drawText(str2.toString(), (width-12*str2.toString().length())/2, (height+numMaze.getRight()+numMaze.getOffset())/2, mPaint);
		       			numMaze.drawMaze(canvas, mPaint);
		       			ball.setxAcc(xSensor);
		       			ball.setyAcc(ySensor);
		       			ball.updatePosition(numMaze);
		           		mPaint.setColor(Color.CYAN);//set pen color
		           		ball.drawBall(canvas, mPaint);
		                  Thread.sleep(33);
		                  if (timer<86400000)//86400000 is the limit of timer, which is a day.
		                  timer+=33;
		//                  Log.d("MainActivity", "runinng");
		               } catch (Exception e) {
		               } finally {
		            	   if (canvas!=null)
		            		   mHolder.unlockCanvasAndPost(canvas);//unlock the Canvas and post.
		               }
	        	   }
	           		canvas = mHolder.lockCanvas();//get Canvas
	            	if (canvas!=null){
	           		canvas.drawColor(Color.WHITE);//Set background color
	           		Paint mPaint = new Paint();
	           		mPaint.setColor(Color.BLACK);//set pen color
	           		str1=new StringBuffer();
	           		str2=new StringBuffer();
	           		if (best==false && timer<Btime){//it's in while loop, so the comparison will be on-going, so add a flag.
	           			best=true;
	           			savePreferences("Btime",timer);
	           		}
	           		if (best==true)
		           	str1.append("New best score!!");
	           		str2.append("Your Time = "+timer/1000%(60*60*60)/(60*60)+":"+timer/1000%(60*60)/60+":"+timer/1000%60+"."+timer/100%10);
	           		mPaint.setTextSize(24); 
	           		canvas.drawText(str1.toString(), (width-12*str1.toString().length())/2, (height-numMaze.getRight()+numMaze.getOffset())/2, mPaint);
	           		canvas.drawText(str2.toString(), (width-12*str2.toString().length())/2, (height+numMaze.getRight()+numMaze.getOffset())/2, mPaint);
	       			numMaze.drawMaze(canvas, mPaint);
	           		mPaint.setColor(Color.CYAN);//set pen color
	           		ball.drawBall(canvas, mPaint);
	           		mHolder.unlockCanvasAndPost(canvas);//unlock the Canvas and post.
	            	}
	            	   finish=true;
	        	   if (touch==true){
	        		   numMaze=new Maze();
	        		   numMaze.setSize(20,20);
	        		   numMaze.setWindow(height,width);
	        		   numMaze.createMaze(height,width);
	        		   ball.initialize();
	        		   ball.updatePosition(numMaze);
	        		   timer=0;
	        		   best=false;
	        		   finish=false;
	        		   touch=false;
	        		   continue;
	        	   }
        	   }
           }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
        	if (finish==true)
        	touch=true;
        }
        return true;
    }    
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
    
    public void onSensorChanged(SensorEvent event){
    	
    	// check sensor type
    	if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
    		
    		// assign directions
    		xSensor=-event.values[0];//x is a reversed direction
    		ySensor=event.values[1];
    	}
    }
}

