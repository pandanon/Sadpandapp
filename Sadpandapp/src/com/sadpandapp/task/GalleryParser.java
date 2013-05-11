package com.sadpandapp.task;

import java.io.IOException;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sadpandapp.util.HttpClientSingleton;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class GalleryParser extends AsyncTask<String, Object, String> {
	
	private Context ctx;
	private AsyncTaskCompleteListener<String> callback;
	public GalleryParser(Context ctx, AsyncTaskCompleteListener<String> callback){
		this.ctx = ctx;
		this.callback = callback;
	}
	
	
	@Override
	protected String doInBackground(String... params) {
		String galleryUrl = (String) params[0];
		
		Log.i(getClass().toString(), "Connecting to: " + galleryUrl);
		
		ConnectivityManager connMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			
			try {
				Log.i(getClass().getName(), "Starting HTTP Request");
				
				String firstPageUrl = getFirstPageUrl(galleryUrl);
				return getFirstPageImg(firstPageUrl);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	
	
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		callback.onTaskComplete(result);
	}


	private String getFirstPageUrl(String url) throws IOException{
		Connection connection = getJsoupConnection(url);
		
		//Get Parser
		Document doc = connection.get();
		Log.i(getClass().getName(), "HTTP Request ended");
		Elements elements = doc.getElementsByClass("gdtm");
		Element link = elements.first();	//Only interested in the first page (for now)
		link = link.getElementsByTag("a").first();
		return link.attr("href");
	}
	
	private String getFirstPageImg(String url) throws IOException{
		//Take the cookies from the HTTPClient to use with Jsoup
		Connection connection = getJsoupConnection(url);
		
		Document doc = connection.get();
		Element element = doc.select("div#i3 > a > img").first();	//<img> element
		
		return element.attr("src");
	}

	/**
	 * Copies the cookies from the singleton HttpClient to a Jsoup Connection
	 * @param url URL that is going to be parsed
	 * @return Jsoup Connection
	 */
	private Connection getJsoupConnection(String url) {
		Connection connection = Jsoup.connect(url);
		DefaultHttpClient client = HttpClientSingleton.getSingleton();
		List<Cookie> cookies = client.getCookieStore().getCookies();
		for(Cookie cookie : cookies){
			connection.cookie(cookie.getName(), cookie.getValue());
		}
		return connection;
	}
	
	

}
