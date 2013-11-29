package com.indoorlocator;

public class DataSet {
	private int signalStrength;
	private String SSID;
	private int frequency;
	private String BSSID;
	
	public DataSet(int ss, String id, int fre, String bid) {
		signalStrength = ss;
		SSID = id;
		frequency = fre;
		BSSID = bid;
	}
	
	public int getRSS() {
		return signalStrength;
	}
	
	public String getSSID() {
		return SSID;
	}
	
	public String getBSSID() {
		return BSSID;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	
}
