package com.indoorlocator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class IndoorLocator extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//This removes header bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_indoor_locator);
		
		/*
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "IL");
		wl.acquire();
		*/
		
		final Button button = (Button) findViewById(R.id.startPF);
		
	    button.setOnClickListener(new OnClickListener(){
	        @Override
	        public void onClick(View view) {

	        	//Toast.makeText(getApplicationContext(),"Clicked", Toast.LENGTH_SHORT).show();
	   		    Intent intent = new Intent(getApplicationContext(), PathFinder.class);
	   		    startActivityForResult(intent, 0);
	        }
	    });

		
		/*
	    ListView listView = (ListView)findViewById(R.id.listView1);
		
	    ArrayList<String> wifiList = new ArrayList<String>();
	    wifiList.clear();

        ArrayAdapter itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,wifiList);
        listView.setAdapter(itemAdapter);

		WiFiScanner wifi = new WiFiScanner(itemAdapter, wifiList);
		wifi.scanHere(this);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    // When clicked, show a toast with the TextView text
			    //Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			    
			    String txt = ((TextView) view).getText().toString();
			    
	   		    Intent intent = new Intent(getApplicationContext(),Measurement.class);
			    intent.putExtra("MAC", txt);
			    startActivity(intent);

			}
		});
		 */
		
		
		
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.indoor_locator, menu);
		return true;
	}
	*/

}
