package com.indoorlocator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
 
public class Measurement extends Activity {
	
	/*
    String val = null;
    ArrayList<String> wifiList2 = new ArrayList<String>();
    ArrayAdapter itemAdapter = null;
    ListView listView = null;
    Context con = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        

        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("MAC");
            
            Pattern regex = Pattern.compile("\\{(.*?)\\}");
            Matcher regexMatcher = regex.matcher(value);
            while (regexMatcher.find()) {
            	
            	String prep = regexMatcher.group();
            	val = prep.substring(1, prep.length()-1);
            	
            }
        }
        
	    listView = (ListView)findViewById(R.id.listView2);
        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,wifiList2);
	    wifiList2.clear();

	    con = this;
		
        final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
                listView.setAdapter(itemAdapter);
            	
        		WiFiScanner wifi = new WiFiScanner(itemAdapter, wifiList2);
        		try {
					wifi.startMeasurement(con, val);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
        	    Toast.makeText(getApplicationContext(),"Clicked on start", Toast.LENGTH_SHORT).show();
            }
        });
        
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        	    Toast.makeText(getApplicationContext(),"Clicked on stop", Toast.LENGTH_SHORT).show();
            }
        });
        
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	wifiList2.clear();
            	itemAdapter.notifyDataSetChanged();
        	    Toast.makeText(getApplicationContext(),"Clicked on reset", Toast.LENGTH_SHORT).show();
            }
        });

		
	
        
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    */
}
