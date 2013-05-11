package com.sadpandapp.util;

import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

import com.loopj.android.http.PersistentCookieStore;

public class HttpClientSingleton {
	private static DefaultHttpClient httpClient = null;
	private static Context ctx;
	private HttpClientSingleton(){
	}
	
	public static void setContext(Context ctx){
		HttpClientSingleton.ctx = ctx;
	}
	
	public static DefaultHttpClient getSingleton(){
		if(ctx == null)
			return null;
		if(httpClient == null){
			httpClient = new DefaultHttpClient();
			PersistentCookieStore myCookieStore = new PersistentCookieStore(ctx);
			httpClient.setCookieStore(myCookieStore);

		}
		return httpClient;
	}
}
