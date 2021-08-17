package com.fairfareindia.ui.trackRide.snaptoRoad;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SnapTORoadResponse{

	@SerializedName("snappedPoints")
	private List<SnappedPointsItem> snappedPoints;

	public void setSnappedPoints(List<SnappedPointsItem> snappedPoints){
		this.snappedPoints = snappedPoints;
	}

	public List<SnappedPointsItem> getSnappedPoints(){
		return snappedPoints;
	}

	@Override
 	public String toString(){
		return 
			"SnapTORoadResponse{" + 
			"snappedPoints = '" + snappedPoints + '\'' + 
			"}";
		}

	public class SnappedPointsItem{

		@SerializedName("placeId")
		private String placeId;

		@SerializedName("originalIndex")
		private int originalIndex;

		@SerializedName("location")
		private Location location;

		public void setPlaceId(String placeId){
			this.placeId = placeId;
		}

		public String getPlaceId(){
			return placeId;
		}

		public void setOriginalIndex(int originalIndex){
			this.originalIndex = originalIndex;
		}

		public int getOriginalIndex(){
			return originalIndex;
		}

		public void setLocation(Location location){
			this.location = location;
		}

		public Location getLocation(){
			return location;
		}

		@Override
		public String toString(){
			return
					"SnappedPointsItem{" +
							"placeId = '" + placeId + '\'' +
							",originalIndex = '" + originalIndex + '\'' +
							",location = '" + location + '\'' +
							"}";
		}
	}

	public class Location{

		@SerializedName("latitude")
		private double latitude;

		@SerializedName("longitude")
		private double longitude;

		public void setLatitude(double latitude){
			this.latitude = latitude;
		}

		public double getLatitude(){
			return latitude;
		}

		public void setLongitude(double longitude){
			this.longitude = longitude;
		}

		public double getLongitude(){
			return longitude;
		}

		@Override
		public String toString(){
			return
					"Location{" +
							"latitude = '" + latitude + '\'' +
							",longitude = '" + longitude + '\'' +
							"}";
		}
	}
}