package com.sadpandapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sadpandapp.R;
import com.sadpandapp.task.AsyncTaskCompleteListener;
import com.sadpandapp.task.DoujinListParser;
import com.sadpandapp.task.HTTPEHAuth;
import com.sadpandapp.task.JSONGetMetadata;
import com.sadpandapp.util.HttpClientSingleton;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

public class MainActivity extends ListActivity {

	private String username;
	private String password;
	
	private List<Gallery> doujinsMeta;
	private GalleryAdapter doujinMetaAdapter;
	
	private String currentSearch = null;
	private int currentPage = 0;
	
	public static final String EXHENTAI_URL = "http://exhentai.org/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		doujinsMeta = new ArrayList<Gallery>();
		
		//Create Adapter
		doujinMetaAdapter = new GalleryAdapter(this, R.layout.row, doujinsMeta);
		ListView lv = getListView();
		lv.setAdapter(doujinMetaAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Gallery doujin = (Gallery)arg0.getItemAtPosition(arg2);
				openGallery(doujin);
			}
			
		});
		
		HttpClientSingleton.setContext(this.getApplicationContext());
		loadSharedPreferences();



		
		//Authentication Test
		DefaultHttpClient client = HttpClientSingleton.getSingleton();
		List<Cookie> cookies = client.getCookieStore().getCookies();
		boolean foundCookie = false;
		
		
	    Intent intent = getIntent();
	    //Activity created from search
	    if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	//Do the Search
	    	currentSearch = intent.getStringExtra(SearchManager.QUERY);
	    }
	    else {
	    	//Load first page
	    	currentSearch = null;
	    	currentPage = 0;
	    }
		
		//Check for ExHentai's Cookie
		for (Cookie cookie : cookies){
			//ExHentai Cookie
			if(cookie.getName().equals("yay")){
				foundCookie = true;
			}
		}
		if(!foundCookie) {
			//If cookie isn't found, authenticate
			HTTPEHAuth authTask = new HTTPEHAuth(this, onAuthFinished);
			authTask.execute(username, password);	
		}
		else{
		    loadPage();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		// Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    // Get the SearchView
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    
	    //TODO Do something with the search
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			// Start settings activity
			Intent i = new Intent(this, SettingsActivity.class);
			startActivityForResult(i, 0);

			return true;
		case R.id.action_refresh:
			loadPage();
			return true;
		case R.id.action_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	//TODO Implement hardware search button
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event){
//		if(keyCode == KeyEvent.KEYCODE_SEARCH){
//			get
//		    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//		    searchView.
//		}
//	}
	
	
	/**
	 * Load shared preferences to variables
	 */
	public void loadSharedPreferences(){
		
		final SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		username = sharedPref.getString(SettingsActivity.KEY_USERNAME, "");
		password = sharedPref.getString(SettingsActivity.KEY_PASSWORD, "");
	}
	
	
	/**
	 * Do the Gallery Search
	 * ExHentai's searches works like this: \<url\>/?_search=\<tags\>
	 */
	public void doSearch(String searchQuery){
		//TODO Implement options to filter by category so I can disable pig disgusting western art
		String url = new String(EXHENTAI_URL).concat("?f_search="+searchQuery);
		//Make AsyncTask
		DoujinListParser task = new DoujinListParser(MainActivity.this, onPageParsed);
		task.execute(url);
	}
	
	/**
	 * Open the gallery. URL is http://exhentai.org/g/<id>/<token>/
	 * @param id Gallery ID
	 * @param token Gallery Token
	 */
	public void openGallery(Gallery doujin){
		//Toast.makeText(this, "Doujin "+doujin.getName(), Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, GalleryActivity.class);
		i.putExtra("gallery", doujin);
		startActivity(i);
	}
	
	/**
	 * 
	 */
	public void loadPage() {
		String url = EXHENTAI_URL+"?page="+currentPage;
		
		if(currentSearch != null) {
			url += "?search="+"?f_search="+currentSearch;
		}
		
		new DoujinListParser(MainActivity.this, onPageParsed).execute(url);
	}
	
	AsyncTaskCompleteListener<HttpResponse> onAuthFinished = new AsyncTaskCompleteListener<HttpResponse>(){

		@Override
		public void onTaskComplete(HttpResponse result) {
			//Make AsyncTask
			loadPage();
			
		}
		
	};
	
	AsyncTaskCompleteListener<List<String>> onPageParsed = new AsyncTaskCompleteListener<List<String>>(){

		@Override
		public void onTaskComplete(List<String> result) {
			JSONGetMetadata task = new JSONGetMetadata(MainActivity.this, onMetaObtained);
			
			//FIXME Maybe avoid this type conversion
			task.execute(result.toArray(new String[0]));
			
		}
		
	};
	
	AsyncTaskCompleteListener<List<Gallery>> onMetaObtained = new AsyncTaskCompleteListener<List<Gallery>>(){

		@Override
		public void onTaskComplete(List<Gallery> result) {
			doujinsMeta.addAll(result);
			doujinMetaAdapter.addAll(result);
			getListView().setAdapter(doujinMetaAdapter);
			
		}
		
	};
	
}
