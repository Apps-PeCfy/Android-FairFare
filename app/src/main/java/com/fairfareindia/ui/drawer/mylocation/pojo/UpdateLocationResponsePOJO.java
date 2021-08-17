package com.fairfareindia.ui.drawer.mylocation.pojo;

import com.google.gson.annotations.SerializedName;

public class UpdateLocationResponsePOJO{

	@SerializedName("data")
	private Data data;

	@SerializedName("message")
	private String message;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
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
			"UpdateLocationResponsePOJO{" + 
			"data = '" + data + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}


	public class Data{

		@SerializedName("country")
		private String country;

		@SerializedName("updated_at")
		private String updatedAt;

		@SerializedName("user_id")
		private int userId;

		@SerializedName("city")
		private String city;

		@SerializedName("created_at")
		private String createdAt;

		@SerializedName("id")
		private int id;

		@SerializedName("state")
		private String state;

		@SerializedName("full_address")
		private String fullAddress;

		@SerializedName("category")
		private String category;

		@SerializedName("type")
		private String type;

		@SerializedName("deleted_at")
		private Object deletedAt;

		@SerializedName("place_id")
		private String placeId;

		public void setCountry(String country){
			this.country = country;
		}

		public String getCountry(){
			return country;
		}

		public void setUpdatedAt(String updatedAt){
			this.updatedAt = updatedAt;
		}

		public String getUpdatedAt(){
			return updatedAt;
		}

		public void setUserId(int userId){
			this.userId = userId;
		}

		public int getUserId(){
			return userId;
		}

		public void setCity(String city){
			this.city = city;
		}

		public String getCity(){
			return city;
		}

		public void setCreatedAt(String createdAt){
			this.createdAt = createdAt;
		}

		public String getCreatedAt(){
			return createdAt;
		}

		public void setId(int id){
			this.id = id;
		}

		public int getId(){
			return id;
		}

		public void setState(String state){
			this.state = state;
		}

		public String getState(){
			return state;
		}

		public void setFullAddress(String fullAddress){
			this.fullAddress = fullAddress;
		}

		public String getFullAddress(){
			return fullAddress;
		}

		public void setCategory(String category){
			this.category = category;
		}

		public String getCategory(){
			return category;
		}

		public void setType(String type){
			this.type = type;
		}

		public String getType(){
			return type;
		}

		public void setDeletedAt(Object deletedAt){
			this.deletedAt = deletedAt;
		}

		public Object getDeletedAt(){
			return deletedAt;
		}

		public void setPlaceId(String placeId){
			this.placeId = placeId;
		}

		public String getPlaceId(){
			return placeId;
		}

		@Override
		public String toString(){
			return
					"Data{" +
							"country = '" + country + '\'' +
							",updated_at = '" + updatedAt + '\'' +
							",user_id = '" + userId + '\'' +
							",city = '" + city + '\'' +
							",created_at = '" + createdAt + '\'' +
							",id = '" + id + '\'' +
							",state = '" + state + '\'' +
							",full_address = '" + fullAddress + '\'' +
							",category = '" + category + '\'' +
							",type = '" + type + '\'' +
							",deleted_at = '" + deletedAt + '\'' +
							",place_id = '" + placeId + '\'' +
							"}";
		}
	}
}