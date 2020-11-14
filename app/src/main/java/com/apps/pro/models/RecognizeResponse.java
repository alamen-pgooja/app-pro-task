package com.apps.pro.models;

import com.google.gson.annotations.SerializedName;

public class RecognizeResponse{

	@SerializedName("data")
	private RecognizeData data;

	@SerializedName("success")
	private String success;

	@SerializedName("message")
	private String message;

	public RecognizeData getData(){
		return data;
	}

	public String getSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}

	@Override
 	public String toString(){
		return 
			"RecognizeResponse{" + 
			"data = '" + data + '\'' + 
			",success = '" + success + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}