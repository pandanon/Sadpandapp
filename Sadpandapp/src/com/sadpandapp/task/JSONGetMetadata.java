package com.sadpandapp.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sadpandapp.Gallery;
import com.sadpandapp.util.HttpClientSingleton;
import com.sadpandapp.util.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class JSONGetMetadata extends AsyncTask<String, Object, List<Gallery>> {

	Context ctx;
	private AsyncTaskCompleteListener<List<Gallery>> callback;
	
	public static final String EH_API_URL = "http://g.e-hentai.org/api.php";
	public static final String DOUJINLINK_SELECTOR = "div.it3";

	public JSONGetMetadata(Context ctx, AsyncTaskCompleteListener<List<Gallery>> callback){
		this.ctx = ctx;
		this.callback = callback;
	}
	@Override
	protected List<Gallery> doInBackground(String... buff) {
		
		ConnectivityManager connMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			try {
				Log.i(getClass().getName(), "Starting JSON Request");
				HttpClient client = HttpClientSingleton.getSingleton();
				HttpPost post = new HttpPost(EH_API_URL);
				
				JSONArray jsonObjects = new JSONArray();
				for(String url : buff){
					Log.i(getClass().getName(), "URL: "+url);
					String[] tokens = url.split("/");
					if(tokens.length >= 6){
						Log.i(getClass().getName(),"JSON: "+tokens[4]+", "+tokens[5]);
						jsonObjects.put(new JSONArray().put(tokens[4]).put(tokens[5]));
					}
				}
				Log.i(getClass().getName(),"JSON Request: "+jsonObjects.getString(0).toString());
				JSONObject json = new JSONObject();
				json.put("method", "gdata");
				json.putOpt("gidlist", jsonObjects);
				
				Log.i(getClass().getName(),"JSON Request: "+jsonObjects.toString());
				
				StringEntity se = new StringEntity(json.toString());
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				
				post.setEntity(se);
				HttpResponse response = client.execute(post);
				 if(response!=null){
                     JSONObject jsonResponse = Utils.parseJSON(response);
     				 Log.i(getClass().getName(), "JSON Response: "+jsonResponse.toString());

                     JSONArray jsonArrayMetadata = (JSONArray)jsonResponse.get("gmetadata");
                     ArrayList<Gallery> result = new ArrayList<Gallery>();
                     for(int i=0; i<jsonArrayMetadata.length(); i++){
                    	 try{
                    	 JSONObject row = jsonArrayMetadata.getJSONObject(i);
                    	 String gid = row.getString("gid");	//Get Gallery Id
                    	 String name = row.getString("title");
                    	 String thumb = row.getString("thumb");
                    	 String token = row.getString("token");
                    	 int fileNumber = row.getInt("filecount");
                    	 double rating = row.getInt("rating");
                    	 Log.i(getClass().getName(), "Title: "+name);
                    	 //Log.i(getClass().getName(), "Thumb url: "+thumb);
                    	 List<String> tags = new ArrayList<String>();
                    	 JSONArray jsonTags = row.getJSONArray("tags");
                    	 //Log.i(getClass().getName(), "Number of tags: "+jsonTags.length());
                    	 for(int j=0; j<jsonTags.length(); j++){
                    		 tags.add(jsonTags.getString(j));

                        	 //Log.i(getClass().getName(), "Tag number "+j+": "+tags.get(j));
                    	 }
                    	 
                    	 //Add Gallery to result list
                    	 result.add(new Gallery(gid, name, thumb, token, null, fileNumber, rating, tags));
                    	 }catch(JSONException e){
                    		 Log.i(getClass().getName(), "JSON Reques nº "+i+" failed");
                    		 continue;
                    	 }
                    	 
                     }
                     return result;
                 }
				 return null;
				
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (JSONException e2){
				e2.printStackTrace();
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
	protected void onPostExecute(List<Gallery> result) {
		
		callback.onTaskComplete(result);
	}
	
	
}
