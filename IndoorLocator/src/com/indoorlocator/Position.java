package com.indoorlocator;

public class Position {

	private static double pathLossExponent = 2;
	private static double rssAtBaseStation = -30;
	private static double movementTreshold = 1.7;
	
	
	
	public static double getDistance(double signalStrength) {
		double exp = (signalStrength-rssAtBaseStation)/(-10*pathLossExponent);
		return Math.pow(10.0, exp);
	}

	public static double getMovedDistance(double currentRSS, double previouseRSS) {
		double change = Math.abs(previouseRSS-currentRSS);
		
		if(change <= 4) {
			return 0;
		} else {
			double exp = (rssAtBaseStation+-change-rssAtBaseStation)/(-10*pathLossExponent);
			return Math.pow(10.0, exp);
		}
	}
	
	public static double getEstimatedMovement(double currentDistance, double previouseDistance) {
		double upperBound = currentDistance+previouseDistance;
		double lowerBound = Math.abs(previouseDistance - currentDistance);
		
		//If the change of distance between currentDistance and previouseDistance is too small, we assume the user hasn't moved
		if(Math.max(previouseDistance, currentDistance)-Math.min(previouseDistance, currentDistance) < movementTreshold) {
			return 0;
		} else {
			return (upperBound+lowerBound)/2;
		}
	}
	
	public static int[] getNewPosition(int currentX, int currentY, float angel, double length) {
		int[] coordinates = new int[2];
		
		coordinates[0] = (int)(currentX+Math.cos(Math.toRadians(Math.abs(90-angel)))*length);
		coordinates[1] = (int)(currentY+Math.sin(Math.toRadians(Math.abs(90-angel)))*length);

		return coordinates;
	}
	
}
