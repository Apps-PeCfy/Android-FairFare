package com.example.fairfare.ui.home.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GetAllowCityResponse{

	@SerializedName("cities")
	private List<CitiesItem> cities;

	@SerializedName("message")
	private String message;

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

		@SerializedName("id")
		private int id;

		public void setName(String name){
			this.name = name;
		}

		public String getName(){
			return name;
		}

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
							",id = '" + id + '\'' +
							"}";
		}
	}
}