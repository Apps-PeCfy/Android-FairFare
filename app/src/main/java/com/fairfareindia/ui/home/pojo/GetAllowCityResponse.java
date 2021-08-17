package com.fairfareindia.ui.home.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GetAllowCityResponse{

	@SerializedName("cities")
	private List<CitiesItem> cities;

	@SerializedName("message")
	private String message;

	@SerializedName("allowCity")
	private String allowCity;

	@SerializedName("allowCityName")
	private String allowCityName;


@SerializedName("currentAddress")
	private String currentAddress;

	public void setCities(List<CitiesItem> cities){
		this.cities = cities;
	}

	public List<CitiesItem> getCities(){
		return cities;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setallowCity(String allowCity){
		this.allowCity = allowCity;
	}

	public String getallowCity(){
		return allowCity;
	}

	public void setallowCityName(String allowCityName){
		this.allowCityName = allowCityName;
	}

	public String getallowCityName(){
		return allowCityName;
	}

	public void setcurrentAddress(String currentAddress){
		this.currentAddress = currentAddress;
	}

	public String getcurrentAddress(){
		return currentAddress;
	}

	@Override
 	public String toString(){
		return
			"GetAllowCityResponse{" +
			"cities = '" + cities + '\'' +
			",message = '" + message + '\'' +
			"}";
		}






	public class CitiesItem{

		@SerializedName("name")
		private String name;

		@SerializedName("nightFromHours")
		private String nightFromHours;

		@SerializedName("nightToHours")
		private String nightToHours;

		@SerializedName("id")
		private int id;

		public void setName(String name){
			this.name = name;
		}

		public String getName(){
			return name;
		}

		public void setNightFromHours(String nightFromHours){ this.nightFromHours = nightFromHours; }

		public String getNightFromHours(){ return nightFromHours; }

		public void setNightToHours(String nightToHours){ this.nightToHours = nightToHours; }

		public String getNightToHours(){ return nightToHours; }

		public void setId(int id){
			this.id = id;
		}

		public int getId(){
			return id;
		}


		@Override
		public String toString(){
			return
					"CitiesItem{" +
							"name = '" + name + '\'' +
							"nightFromHours = '" + nightFromHours + '\'' +
							"nightToHours = '" + nightToHours + '\'' +
							",id = '" + id + '\'' +
							"}";
		}
	}
}