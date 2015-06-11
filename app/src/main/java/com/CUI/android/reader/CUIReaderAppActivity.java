package com.CUI.android.reader;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.CUI.android.reader.data.RssItem;
import com.CUI.android.reader.listeners.ListListener;
import com.CUI.android.reader.util.RssReader;

/**
 * Main application activity.
 * 
 * Update: Downloading RSS data in an async task 
 * 
 *
 *
 */


public class CUIReaderAppActivity extends Activity {


    private CUIReaderAppActivity local;
	
	/** 
	 * This method creates main application view
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNetworkOnline()){

			start(savedInstanceState);
		}
		else {
            setContentView(R.layout.main);
			Toast.makeText(this,"Check your connection! If you are on campus connect to the campus wifi",Toast.LENGTH_LONG).show();
		}



	}

	protected boolean isNetworkOnline() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}else {
			return false;
		}
	}



	private void start(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		// Set view
		setContentView(R.layout.main);

		// Set reference to this activity
		local = this;

		GetRSSDataTask task = new GetRSSDataTask();

		// Start download RSS task
		task.execute("http://cuindependent.com/feed/");

		// Debug the thread name
		Log.d("CUIRssReader", Thread.currentThread().getName());

	}

	private class GetRSSDataTask extends AsyncTask<String, Void, List<RssItem> > {
		@Override

		protected List<RssItem> doInBackground(String... urls) {


            // Debug the task thread name
			Log.d("CUIRssReader", Thread.currentThread().getName());
            if (isNetworkOnline()){

			try {
				// Create RSS reader
				RssReader rssReader = new RssReader(urls[0]);

				// Parse RSS, get items
				return rssReader.getItems();

			} catch (Exception e) {
				Log.e("CUIRssReader", e.getMessage());
			}}
            else {
                Toast.makeText(getApplicationContext(),"network is not good", Toast.LENGTH_LONG).show();


            }
            return null;
		}




		@Override
		protected void onPostExecute(List<RssItem> result) {
			if (result!=null){
			// Get a ListView from main view
			ListView itcItems = (ListView) findViewById(R.id.listMainView);
			// Create a list adapter
			ArrayAdapter<RssItem> adapter = new ArrayAdapter<RssItem>(local,android.R.layout.simple_list_item_1, result);
			// Set list adapter for the ListView
			itcItems.setAdapter(adapter);
						
			// Set list view item click listener
			itcItems.setOnItemClickListener(new ListListener(result, local));
			}
            else{
                Toast.makeText(getApplicationContext(),"network is not good", Toast.LENGTH_LONG).show();

            }

		}
	}


}