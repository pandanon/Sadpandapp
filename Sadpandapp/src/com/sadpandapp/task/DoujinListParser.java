package com.sadpandapp.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sadpandapp.R;
import com.sadpandapp.util.HttpClientSingleton;


import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class DoujinListParser extends AsyncTask<Object, Object, List<String>> {

	private Context ctx;
	private AsyncTaskCompleteListener<List<String>> callback;
	private Exception exception;
	
	public static final String DOUJINLINK_SELECTOR = "div.it3";

	public DoujinListParser(Context ctx, AsyncTaskCompleteListener<List<String>> callback){
		this.ctx = ctx;
		this.callback = callback;
	}
	@Override
	protected List<String> doInBackground(Object... buff) {
		// TODO Auto-generated method stub
		String url = (String) buff[0];
		Log.i(getClass().toString(), "Connecting to: " + url);
		
		ConnectivityManager connMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			try {
				Log.i(getClass().getName(), "Starting HTTP Request");
				
				//Take the cookies from the HTTPClient to use with Jsoup
				Connection connection = Jsoup.connect(url);
				DefaultHttpClient client = HttpClientSingleton.getSingleton();
				List<Cookie> cookies = client.getCookieStore().getCookies();
				for(Cookie cookie : cookies){
					connection.cookie(cookie.getName(), cookie.getValue());
				}
				Document doc = connection.get();
				Log.i(getClass().getName(), "HTTP Request ended");
				Elements links = doc.select(DOUJINLINK_SELECTOR);
				List<String> doujins = new ArrayList<String>();
				for(Element link : links){
					Element urlElement;


					//Make sure to ignore download arrow
					if(link.childNodeSize()== 1){
						urlElement = link.child(0);
					}
					else{
						urlElement = link.child(1);
					}
					String galleryUrl = urlElement.attr("href");
					if(!galleryUrl.contains("gallerytorrents")) {
						doujins.add(urlElement.attr("href"));
						Log.i(getClass().getName(), "Found doujin: "+link.text());
						Log.i(getClass().getName(), "Doujin URL: "+urlElement.attr("href"));
						
					}
					else {
						Log.i(getClass().getName(), "Something");

					}
					
					//IDEA Use the URL with the JSON API
				}
				Log.i(getClass().getName(), "Parsing ended");
				return doujins;
			} catch (UnsupportedMimeTypeException e1) {
				// This happens when we get the infamous Sadpanda (Content type = image.gif)
				exception = e1;
				// Get rid of the cookie
				DefaultHttpClient client = HttpClientSingleton.getSingleton();
				// For some reason there is no method for removing just one cookie
				client.getCookieStore().clear();			
				
				
				
			} catch (IOException e2) {
				//Connection problem to ExHentai
				exception = e2;
			}
		}
		else {
			/*AlertDialog.Builder dialog = new AlertDialog.Builder(_appContext);
			dialog.setTitle("Information");
			dialog.show();*/
			//publishProgress(null);
		}
		
		
		return null;
	}

	
	@Override
	protected void onPostExecute(List<String> result) {
		
		if(exception == null && result != null){
			callback.onTaskComplete(result);
		}
		else {
			String errorMessage;
			if(exception instanceof UnsupportedMimeTypeException){
				errorMessage = ctx.getString(R.string.error_sadpanda);
			}
			else {
				errorMessage = ctx.getString(R.string.error_connection);
			}
			
			//Build Alert Dialog
	        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
	        builder.setMessage(errorMessage)
	        	.setTitle(R.string.error_title);

	        
	        //Show Alert Dialog
	        builder.create().show();
		}
	}
	
	
}
