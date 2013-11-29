package com.indoorlocator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PathFinder extends Activity implements SensorEventListener {

	Activity act = this;
	final int ACTIVITY_CHOOSE_FILE = 1;
	boolean setFlag = false;
	public static float currentPosition = 0;
	private SensorManager mSensorManager = null;
	private Context globalcon;
	private boolean shouldRun = true;
	private boolean simpleMode = true;
    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    private float[] rData = new float[3];
    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mRo = new float[16];
    private float[] mOrientation = new float[3];
    private int correction;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_path_finder);

        final RelativeLayout entire_view = (RelativeLayout) findViewById(R.id.entire_view);
        entire_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	initPosition((int)event.getX(), (int)event.getY());
                return true;
            }
        });
        
        
        
    	final TextView st = (TextView) findViewById(R.id.subtext);
	    st.setOnClickListener(new OnClickListener(){
	        @Override
	        public void onClick(View view) {
	        	
	          	final ImageView mapback = (ImageView) findViewById(R.id.mapimage);
	        	mapback.setBackgroundResource(R.drawable.grid);
	        	setInvisible();
	        	
	        	
		        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		        Editor editor = sharedPreferences.edit(); 
		        editor.putString("imageUri", "loadGrid"); 
		        editor.commit(); 
		        
		        showSlider();
	        	//Toast.makeText(getApplicationContext(),"Clicked on subtext", Toast.LENGTH_SHORT).show();
	        }
	    });
	    
	    
	    
    	final Button bt = (Button) findViewById(R.id.scaleButton);
	    bt.setOnClickListener(new OnClickListener(){
	        @Override
	        public void onClick(View view) {
	        	setStartPosition();
	        }
	    });
	    
	    
	    
    	final Button sn = (Button) findViewById(R.id.startNavigation);
	    sn.setOnClickListener(new OnClickListener(){
	        @Override
	        public void onClick(View view) {
	        	ImageView imageView = (ImageView)findViewById(R.id.point);
	        	RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
	        	int x = lp.leftMargin;
	        	int y = lp.topMargin;
	        	
	        	if(x==0 && y==0) {
	        		Display display = getWindowManager().getDefaultDisplay();
	        		Point size = new Point();
	        		display.getSize(size);
	        		x = size.x/2;
	        		y = size.y/2;
	        	}
	        	
	            SharedPreferences sharedPreferences = PreferenceManager .getDefaultSharedPreferences(getBaseContext()); 
	            Editor editor = sharedPreferences.edit(); 
	            editor.putInt("posX", x);
	            editor.putInt("posY", y);
	            

	            
	            editor.putFloat("startCompass", currentPosition);
	            editor.commit(); 
	            

	        	//Toast.makeText(getApplicationContext(),String.valueOf(x)+"|"+String.valueOf(y), Toast.LENGTH_SHORT).show();
	            
	            startNavigation();
	            
	        }
	    });
	    

	    setFlag = false;
	    
	    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	    findViewById(R.id.point).setVisibility(View.GONE);
		findViewById(R.id.seekBar1).setVisibility(View.GONE);
		findViewById(R.id.scaleField).setVisibility(View.GONE);
		findViewById(R.id.scaleButton).setVisibility(View.GONE);
		findViewById(R.id.scaleText).setVisibility(View.GONE);
		findViewById(R.id.startNavigation).setVisibility(View.GONE);
		
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); 
        String name = sharedPreferences.getString("imageUri", "false"); 
        
        //Toast.makeText(getApplicationContext(),name, Toast.LENGTH_SHORT).show();        

        if(name.equals("false")) {
        	showFilePicker();
        }  else if(name.equals("loadGrid")) {
        	setInvisible();
          	final ImageView mapback = (ImageView) findViewById(R.id.mapimage);
        	mapback.setBackgroundResource(R.drawable.grid);
	        //showSlider();
        	try {
				loadSettings();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        } else {
        	try {
				loadSettings();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        
    }
    
    
    
    public void startNavigation() {
    	
    	setFlag = false;
    	findViewById(R.id.startNavigation).setVisibility(View.GONE);
   
        //Toast.makeText(getApplicationContext(),"x: "+String.valueOf(posX)+", y:"+String.valueOf(posY) + ", scale: "+scaleField+", seek:"+String.valueOf(seekBar)+ "comp: "+String.valueOf(currentPosition), Toast.LENGTH_SHORT).show();
        
        
        
        Thread thread = new Thread() {
            public void run(){
                int i = 0;

                WiFiScanner.scanHere(act);
                
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(globalcon); 
	            Editor editor = sharedPreferences.edit(); 
	            editor.putInt("signalCounter", 0);
	            editor.commit(); 
                
                while(shouldRun) {
                	
                	if(i%2 == 0) {
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                //float compass = currentPosition;
                            	//Toast.makeText(getApplicationContext(),String.valueOf(posY), Toast.LENGTH_SHORT).show();
                            	findViewById(R.id.point).setVisibility(View.GONE);
                            }
                        });
                	} else {
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                            	int signalCounter = WiFiScanner.getCounter();
                                
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(globalcon); 
                                int oldCounter = sharedPreferences.getInt("signalCounter", 0); 
                            	
                                
                                
                                if(signalCounter > oldCounter) {
                    	            Editor editor = sharedPreferences.edit(); 
                    	            editor.putInt("signalCounter", signalCounter);
                    	            editor.commit(); 
                    	            
                    	            ArrayList<DataSet> oldScan = WiFiScanner.getOldWifiList();
                    	            ArrayList<DataSet> newScan = WiFiScanner.getWifiList();
                    	            ArrayList<DataSet> intersection = new ArrayList<DataSet>();
                    	            
                    	            for(int m=0;m<oldScan.size();m++) {
                    	            	
                    	            	//If the RSSI is too weak, we don't add it.
                    	            	if(oldScan.get(m).getRSS() < -64) {
                    	            		continue;
                    	            	}
                    	            	
                    	            	for(int n=0;n<newScan.size();n++) {
                    	            		if(oldScan.get(m).getBSSID().equals(newScan.get(n).getBSSID())) {
                    	            			intersection.add(oldScan.get(m));
                    	            			intersection.add(newScan.get(n));
                    	            		}
                    	            	}
                    	            }
                    	            
                    	            int totalValues = 0;
                    	            double summarizedDistance = 0;
                    	            
                    	            int p = 0;
                    	            while(p < intersection.size()) {

                    	            	if(simpleMode) {
                    	            		summarizedDistance += Position.getMovedDistance(intersection.get(p+1).getRSS(), intersection.get(p).getRSS());
                        	            	
                    	            	} else {
                    	            		double oldDistance = Position.getDistance(intersection.get(p).getRSS());
                        	            	double newDistance = Position.getDistance(intersection.get(p+1).getRSS());
                        	            	summarizedDistance += Position.getEstimatedMovement(newDistance, oldDistance);
                        	            	
                    	            	}
                    	            	
                    	            	totalValues++;
                    	            	p=p+2;
                    	            }
                    	            
                    	            int meanMovedDistanceInPixel = 0;
                    	            float angel = 0;
                    	            
                    	            if(totalValues > 0) {
                        	            double meanMovedDistance = summarizedDistance/totalValues; 
                        	            
                        	            angel = currentPosition - sharedPreferences.getFloat("startCompass", (float)0.0); 
                                        if(angel < 0) {
                                        	angel = 360+angel;
                                        }
                        	            
                        	            int scaleField = Integer.parseInt(sharedPreferences.getString("scaleFieldText", "0"));
                        	            if(scaleField == 0) {
                        	            	return;
                        	            }
                        	            
                        	            meanMovedDistanceInPixel = (sharedPreferences.getInt("seekBar", 0)*5)/scaleField * (int)meanMovedDistance;
                        	            
                        	            int posX = sharedPreferences.getInt("posX", 0); 
                        	            int posY = sharedPreferences.getInt("posY", 0); 
                        	            
                        	            //Toast.makeText(getApplicationContext(),String.valueOf("meanmoveddistance "+meanMovedDistance+"\n"+"angel "+angel), Toast.LENGTH_SHORT).show();
                        	            
                        	            
                        	            if(meanMovedDistanceInPixel != 0) {
                            	            int[] newCoordinates = new int[2];    
                            	            newCoordinates = Position.getNewPosition(posX, posY, angel, meanMovedDistanceInPixel);
                            	            
                            	            int newX = newCoordinates[0];
                            	            int newY = newCoordinates[1];
                            	            
                            	            editor.putInt("posX", newX);
                            	            editor.putInt("posY", newY);
                            	            editor.commit(); 
                            	            
                            	            setPosition(newX, newY);
                        	            } else {
                            	            setPosition(posX, posY);
                        	            }

                    	            }

                    	            
                    	            
                    	            
                    	            
                    	            String values = "";
                    	       
                    	            values += "startangel " + String.valueOf(Math.abs(sharedPreferences.getFloat("startCompass", (float)0.0))) + "\n";
                    	            values += "currentposition " + String.valueOf(currentPosition) + "\n";
                    	            values += "angel " + String.valueOf(angel) + "\n";
                    	            values += "scalefield " + sharedPreferences.getString("scaleFieldText", "0") + "\n";
                    	            values += "seekbar " + String.valueOf(sharedPreferences.getInt("seekBar", 0)) + "\n";
                    	            values += String.valueOf(meanMovedDistanceInPixel) + "\n";
                    	            
                    	            for(int j=0;j<oldScan.size();j++) {
                    	            	values += oldScan.get(j).getBSSID()+ ", " + oldScan.get(j).getRSS()+"\n";
                    	            }
                    	            
                    	            values += "-----------\n";
                    	            
                    	            for(int j=0;j<newScan.size();j++) {
                    	            	values += newScan.get(j).getBSSID()+ ", " + newScan.get(j).getRSS()+"\n";
                    	            }
                    	            
                    	            Toast.makeText(getApplicationContext(),String.valueOf(values), Toast.LENGTH_SHORT).show();
                               
                                }
                                
                            	findViewById(R.id.point).setVisibility(View.VISIBLE);
                            }
                        });
                        
                	}
                	
                	
                	try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                	i++;
                }  
                
            }
            
            
          };
         
        thread.start();
        

        
    }
    
    
    public void stopScan() {
    	shouldRun = false;
    	WiFiScanner.stopScan();
    	
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
    

    @Override 
    	public void onAccuracyChanged(Sensor sensor, int accuracy) { 
    } 


    @Override 
    public void onSensorChanged(SensorEvent event) { 
    	
    	int type = event.sensor.getType();
        float[] data;
        if (type == Sensor.TYPE_ACCELEROMETER) {
        	correction = (int)event.values[0];
            data = mGData;
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            data = mMData;
        //} else if(type == Sensor.TYPE_ROTATION_VECTOR) {
        //	data = rData;
        } else {
            return;
        }
        for (int i=0 ; i<3 ; i++)
            data[i] = event.values[i];
    	
        SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
        SensorManager.getOrientation(mR, mOrientation);
        float incl = SensorManager.getInclination(mI);
        
        /*
        float[] roationV = new float[16];
        SensorManager.getRotationMatrixFromVector(roationV, rData);

        float[] orientationValuesV = new float[3];
        SensorManager.getOrientation(roationV, orientationValuesV);
        */
        
        final float rad2deg = (float)(180.0f/Math.PI);
        
        /*
        Log.d("Compass", "yaw: " + (int)(mOrientation[0]*rad2deg) +
                "  pitch: " + (int)(mOrientation[1]*rad2deg) +
                "  roll: " + (int)(mOrientation[2]*rad2deg) +
                "  incl: " + (int)(incl*rad2deg)
                );
    	*/
        //float degree = Math.round(event.values[0]);
        
        if(Math.round(mOrientation[0]*rad2deg) < 0) {
        	currentPosition = 360+Math.round(mOrientation[0]*rad2deg);
        } else {
        	currentPosition = Math.round(mOrientation[0]*rad2deg);
        }
        
        if(currentPosition + correction*9 > 360) {
        	currentPosition = currentPosition + correction*9 - 360;
        } else {
        	currentPosition = currentPosition + correction*9;
        }
        
        Log.i("compass", "compassvalue " + String.valueOf(currentPosition) + ",correction " + String.valueOf(correction));
    }

    

    @Override 
    protected void onResume() { 
        super.onResume(); 
        
        Sensor gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor rsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, rsensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);
        
        //mSensorManager.registerListener((SensorEventListener) this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME); 
    } 
    
    

	@Override 
	protected void onPause() {
    	super.onPause(); 
    	mSensorManager.unregisterListener(this); 
	} 



    public void setStartPosition() {
    	closeSlider();
        AlertDialog.Builder ad  = new AlertDialog.Builder(PathFinder.this);                      
        ad.setMessage("Touch the map to set your current position. To calibrate the compass the right way, rotate and adjust your device relative to the map.");
        ad.setTitle("Current position");              
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	setFlag = true;
        		findViewById(R.id.startNavigation).setVisibility(View.VISIBLE);
        		findViewById(R.id.point).setVisibility(View.VISIBLE);
            	dialog.dismiss();
            }
        });
        ad.create().show();
    }
    
    
    
    public void loadSettings() throws FileNotFoundException, IOException {
    	
    	setInvisible();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); 
        String selectedimg = sharedPreferences.getString("imageUri", "loadGrid");

        if(!selectedimg.equals("loadGrid")) {
	    	final ImageView mapback = (ImageView) findViewById(R.id.mapimage);
	    	mapback.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(selectedimg)));
        }

    	setStartPosition();
    }
    
    
    public void resetAll() {
    	  findViewById(R.id.subtext).setVisibility(View.VISIBLE);
      	  findViewById(R.id.headertext).setVisibility(View.VISIBLE);
    	  findViewById(R.id.filePicker).setVisibility(View.VISIBLE);
    	  
    	  final ImageView mapback = (ImageView) findViewById(R.id.mapimage);
    	  mapback.setBackgroundResource(R.drawable.blank);
    	  
    	  SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); 
    	  Editor editor = sharedPreferences.edit(); 
    	  editor.putString("imageUri", "false"); 
    	  editor.commit(); 
    	  
          Intent intent = new Intent();
          setResult(RESULT_OK, intent);
          finish();
    }
    
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.indoor_locator, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.reset:
        	resetAll();
            return true;
            
        case R.id.stopScan:
        	stopScan();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
    public void setInvisible() {
  	  findViewById(R.id.subtext).setVisibility(View.GONE);
  	  findViewById(R.id.headertext).setVisibility(View.GONE);
	  findViewById(R.id.filePicker).setVisibility(View.GONE);
	  findViewById(R.id.startNavigation).setVisibility(View.GONE);
    }
    
    
    
    public void showSlider() {
    	findViewById(R.id.seekBar1).setVisibility(View.VISIBLE);
		findViewById(R.id.scaleField).setVisibility(View.VISIBLE);
		findViewById(R.id.scaleButton).setVisibility(View.VISIBLE);
		findViewById(R.id.scaleText).setVisibility(View.VISIBLE);
    }
    
    
    public void closeSlider() {
    	
    	EditText scaleField = (EditText) findViewById(R.id.scaleField);
    	VerticalSeekBar sb = (VerticalSeekBar) findViewById(R.id.seekBar1);
    	int pos = sb.getProgress();
    	
    	globalcon = this;
    	
        SharedPreferences sharedPreferences = PreferenceManager .getDefaultSharedPreferences(this); 

        if(!scaleField.getText().toString().equals("")) {
	        Editor editor = sharedPreferences.edit(); 
	        editor.putString("scaleFieldText", scaleField.getText().toString());
	        editor.putInt("seekBar", pos); 
	        editor.commit(); 
        }
    	
    	findViewById(R.id.seekBar1).setVisibility(View.GONE);
    	findViewById(R.id.scaleField).setVisibility(View.GONE);
    	findViewById(R.id.scaleButton).setVisibility(View.GONE);
    	findViewById(R.id.scaleText).setVisibility(View.GONE);
    	
    }
    
    
    
    public void initPosition(int x, int y) {
    	
    	//if(setFlag == true) {
    		setPosition(x, y);
    	//} 
    }
    
    
    
    public void setPosition(int x, int y) {
    	//Toast.makeText(getApplicationContext(),String.valueOf(x)+"|"+String.valueOf(y), Toast.LENGTH_SHORT).show();
    	ImageView point = (ImageView)findViewById(R.id.point);

    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(point.getLayoutParams());
    	lp.setMargins(x, y, 0, 0);
    	
        SharedPreferences sharedPreferences = PreferenceManager .getDefaultSharedPreferences(getBaseContext()); 
        Editor editor = sharedPreferences.edit(); 
        editor.putInt("posX", x);
        editor.putInt("posY", y);
        editor.commit(); 
    	
    	point.setLayoutParams(lp);
    }
    
    
    public void showFilePicker() {
    	
    	final ImageView mapback = (ImageView) findViewById(R.id.mapimage);
    	mapback.setBackgroundResource(R.drawable.blank);
    	
    	
    	final TextView subtxt = (TextView) findViewById(R.id.subtext);
    	subtxt.setText(Html.fromHtml("No thanks, <u><font color='blue'>continue with a blank white grid.</font></u>"));
    	
        Button btn = (Button) this.findViewById(R.id.filePicker);
        btn.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  Intent intent = new Intent();  
        	  intent.setType("image/*");  
        	  intent.setAction(Intent.ACTION_GET_CONTENT);  
        	  startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 1);

          }
        });
      }

      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
          case ACTIVITY_CHOOSE_FILE: {
            if (resultCode == RESULT_OK){
            	
              final ImageView mapback = (ImageView) findViewById(R.id.mapimage);	
              Uri selectedimg = data.getData();
              try {
				mapback.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg));
				
		        SharedPreferences sharedPreferences = PreferenceManager .getDefaultSharedPreferences(this); 
		        Editor editor = sharedPreferences.edit(); 
		        editor.putString("imageUri", selectedimg.toString()); 
		        editor.commit(); 

		        showSlider();
		        
			  } catch (FileNotFoundException e) {
				  e.printStackTrace();
			  } catch (IOException e) {
				  e.printStackTrace();
			  }

        	  setInvisible();
        	  
              //Toast.makeText(getApplicationContext(),selectedimg.toString(), Toast.LENGTH_SHORT).show();
            }
          }
        }
     }
      
 
    
}
