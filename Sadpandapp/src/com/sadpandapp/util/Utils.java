package com.sadpandapp.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Utils {


	/**
	 * 	Get a JSONObject from an HTTPEntity
	 */
	public static JSONObject parseJSON(HttpResponse response) throws UnsupportedEncodingException, IllegalStateException, IOException, JSONException {
		String json = EntityUtils.toString(response.getEntity());
		JSONTokener tokener = new JSONTokener(json);
		return new JSONObject(tokener);
	}
	
	/**
	 * Create an HTTP GET with some generic headers
	 */
	public static HttpGet createHttpRequest(String url, String host) {
		HttpGet get = new HttpGet(url);
		get.addHeader("Host", host);
		get.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.addHeader("Accept-Encoding", "gzip, deflate");
		get.addHeader("Connection", "keep-alive");
		return get;
	}
}
