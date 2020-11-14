package com.apps.pro.models;

import com.google.gson.annotations.SerializedName;

public class RecognizeData {

	@SerializedName("eid")
	private String eid;

	@SerializedName("name")
	private String name;

	public String getEid(){
		return eid;
	}

	public String getName(){
		return name;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"eid = '" + eid + '\'' + 
			",name = '" + name + '\'' + 
			"}";
		}
}