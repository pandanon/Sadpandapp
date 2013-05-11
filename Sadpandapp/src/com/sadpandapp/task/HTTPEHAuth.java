package com.sadpandapp.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.UnsupportedMimeTypeException;

import com.sadpandapp.R;
import com.sadpandapp.util.HttpClientSingleton;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class HTTPEHAuth extends AsyncTask<String, Object, HttpResponse>{
	
	public static final String EH_URL = "http://e-hentai.org/";
	public static final String EXHENTAI_URL = "http://exhentai.org";
	public static final String USERNAME_KEY = "ipb_login_username";
	public static final String PASSWORD_KEY = "ipb_login_password";
	public static final String SUBMINT_KEY = "ipb_login_submit";
	
	private Context ctx;
	private AsyncTaskCompleteListener<HttpResponse> cb;
	private HttpContext localContext = new BasicHttpContext();
	private Exception exception = null;			// In case we get an exception during the AsyncTask
	
	public HTTPEHAuth(Context ctx, AsyncTaskCompleteListener<HttpResponse> cb){
		this.ctx = ctx;
		this.cb = cb;
	}

	@Override
	protected HttpResponse doInBackground(String... buff) {
		// TODO Auto-generated method stub
		
		String userID = buff[0];

		String userPassword = buff[1];
		

		Log.i(getClass().getName(), "Trying to connect as: "+userID);
		
		ConnectivityManager connMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			try {
				//First part, submitting the E-H Login Form and getting the Cookie
				Log.i(getClass().getName(), "Starting Authentication Request");
				HttpClient client = HttpClientSingleton.getSingleton();
				HttpPost post = new HttpPost(EH_URL);
				post.setHeader("Content-Type", "application/x-www-form-urlencoded");
				
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair(USERNAME_KEY, userID));  
				pairs.add(new BasicNameValuePair(PASSWORD_KEY, userPassword));
				
				HttpEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");

				post.setEntity(entity);
				
				//With this we get the cookie
				HttpResponse response = client.execute(post, localContext);	
				// Consume entity, necesary if we want to make more requests
				response.getEntity().consumeContent();
				
				//Second part, HTTP GET exhentai.org (This will redirect to the Auth API)
				HttpGet get = new HttpGet(EXHENTAI_URL);
				response = client.execute(get, localContext);
				
				Log.i(getClass().getName(), "Second part: HTTP "+response.getStatusLine().getStatusCode());
				
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
		
		//No internet connection
		return null;
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		super.onPostExecute(result);
		if(exception == null && result != null){
			
			
			
			cb.onTaskComplete(result);
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
