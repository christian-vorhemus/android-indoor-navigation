package com.indoorlocator;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ArrayAdapter;

public class WiFiScanner {

	//private ArrayList<String[]> parameters = null;
	private static ArrayList<DataSet> wifiList = new ArrayList<DataSet>();
	private static ArrayList<DataSet> oldWifiList = new ArrayList<DataSet>();
	private ArrayAdapter itemAdapter = null;
	//private ArrayList<String> measurementList = new ArrayList<String>();
	//private Context fcon = null;
	WifiManager globalwifiManager = null;
	//private int counter = 0;
	static Context globalcon = null;
	static BroadcastReceiver wifiScanReceiver;
	private static int counter = 0;
	
	/*
	public WiFiScanner(ArrayAdapter iA, ArrayList<DataSet> wL) {
		itemAdapter = iA;
		wifiList = wL;
	}
	*/
	
	public WiFiScanner() {
		
	}
	
	/*
	public void startMeasurement(Context con, final String mac) throws InterruptedException {
	
		counter = 0;
		fcon = con;
		globalwifiManager = (WifiManager)con.getSystemService(Context.WIFI_SERVICE);
		
		if(!globalwifiManager.isWifiEnabled()) {
			globalwifiManager.setWifiEnabled(true);
			
			try {
			    Thread.sleep(2000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
	    	
		}
		
		IntentFilter ifilter = new IntentFilter();
	    ifilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION); 
	    
	     
	    BroadcastReceiver wifiScanReceiver;
	    wifiScanReceiver = new BroadcastReceiver() {
	    	
	        public void onReceive(Context c, Intent inte) { 
	    	    //Toast.makeText(c,"RECEIVED", Toast.LENGTH_SHORT).show();
	        	List<ScanResult> sr = globalwifiManager.getScanResults();
	        	
	            for(int i = 0; i < sr.size(); i++){
	            	
		        	if(sr.get(i).BSSID.toString().equals(mac)) {
		        		String timestamp = String.valueOf(System.currentTimeMillis()/1000);
		        		wifiList.add(Integer.toString(sr.get(i).level)+" | "+timestamp);
		        	}

	                itemAdapter.notifyDataSetChanged();
	                try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

	                counter += 1;
	                
	                if(counter < 10) {
		                globalwifiManager.startScan();
	                }

	            }
	            
	        }
	    };
	    
	    con.registerReceiver(wifiScanReceiver, ifilter);
	    
	    globalwifiManager.startScan();
			
		
	}
	*/
	
	
	public static void scanHere(Context con) {
	
		globalcon = con;
		
		final WifiManager wifiManager = (WifiManager)con.getSystemService(Context.WIFI_SERVICE);
	
		wifiList = new ArrayList<DataSet>();
		
		if(!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
			
			try {
			    Thread.sleep(2000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
	    	
		}
		
		
		IntentFilter ifilter = new IntentFilter();
	    ifilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION); 
	    
	    wifiScanReceiver = new BroadcastReceiver() {
	    	
	        public void onReceive(Context c, Intent inte) { 
	        	List<ScanResult> sr = wifiManager.getScanResults();
	        	
	        	oldWifiList = wifiList;
	        	wifiList = new ArrayList<DataSet>();
	        	
	            for(int i = 0; i < sr.size(); i++){
	            	DataSet ds = new DataSet(sr.get(i).level, sr.get(i).SSID, sr.get(i).frequency, sr.get(i).BSSID);
	            	wifiList.add(ds);
	            }
	            
	            //globalcon.unregisterReceiver(wifiScanReceiver);
	            counter++;
	    		wifiManager.startScan();
	        }
	    };
	    
	    globalcon.registerReceiver(wifiScanReceiver, ifilter);
		wifiManager.startScan();

		
	}
	
	
	
	
	public static ArrayList<DataSet> getWifiList() {
		return wifiList;
	}
	
	public static ArrayList<DataSet> getOldWifiList() {
		return oldWifiList;
	}
	
	public static int getCounter() {
		return counter;
	}
	
	public static void stopScan() {
		globalcon.unregisterReceiver(wifiScanReceiver);
	}
	
	
	
}
