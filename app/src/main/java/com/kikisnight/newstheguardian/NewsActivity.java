package com.kikisnight.newstheguardian;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static final String LOG_TAG = NewsActivity.class.getName();

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /** Adapter for the list of news */
    private NewsAdapter mAdapter;

    /** URL for news data from the The Guardian dataset */
    private static final String API_REQUEST_URL =
            "https://content.guardianapis.com/search";

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** Loader for background thread */
    private static LoaderManager loaderManager;

    /* Message TextView to know the status of the connection for the user */
    TextView messageTextView;

    /* Progressbar to know when is obtaining data */
    ProgressBar progressBar;

    /* NetworkInfor to know the status of the connection in the LOG */
    NetworkInfo networkInfo;

    /*Refresh the layout*/
    SwipeRefreshLayout swipeRefreshLayout;

    /*String for search a new topic, default "all" */
    private String keyWordforSearch = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize TextView and Spinner
        messageTextView = (TextView) findViewById(R.id.empty_view);
        progressBar = (ProgressBar) findViewById(R.id.loading_indicator);


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Show message for fetching data
            messageTextView.setText(getString(R.string.message_obtain_data));

            // Initialize Loader and News Adapter
            initializeLoaderAndAdapter();

        } else {
            // Hide progressBar
            progressBar.setVisibility(View.GONE);

            // Display error
            messageTextView.setText(getString(R.string.message_no_internet_connection));
        }
    }



    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        // Get an instance of SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get order by preference
        String orderBy = sharedPreferences.getString(getString(R.string
                .settings_order_by_key), getString(R.string.settings_order_by_default));

        // Build the Uri based on the preferences
        Uri baseIri = Uri.parse(API_REQUEST_URL);
        Uri.Builder uriBuilder = baseIri.buildUpon();

        uriBuilder.appendQueryParameter("q", keyWordforSearch);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "test");
        Log.v("MainActivity", "Uri: " + uriBuilder);

        // Create a new loader with the supplied Url
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsItems) {
        // If there is a valid list of {@link New}s, then add them to the adapter's
        if (newsItems != null && !newsItems.isEmpty()) {
            mAdapter.addAll(newsItems);
            // Hide loading indicator because the data has been loaded
            progressBar.setVisibility(View.GONE);
            // Hide message text
            messageTextView.setText("");

        } else {
            // Hide loading indicator because the data has been loaded
            progressBar.setVisibility(View.GONE);
            // Set message text to display "No articles found!"
            messageTextView.setText(getString(R.string.message_no_news));
        }
        Log.v("MainActivity","Loader completed.");
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clearAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeLoaderAndAdapter() {
        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        // Lookup the recyclerView in activity layout
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // Create adapter passing the data
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        // Attach the adapter to the recyclerView to populate items
        recyclerView.setAdapter(mAdapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    /*
    * Search for a new topic
    */
    public void searchKeyword (View v) {
        //Find a references to the EditText
        EditText editText = (EditText) findViewById(R.id.edit_text);
        String enterOfKeyword = editText.getText().toString().trim().toLowerCase();
        //Change the keyWord for search a new topic
        keyWordforSearch = enterOfKeyword;

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();
        Log.v("MainActivity", "networkInfo: " + networkInfo);

        if (networkInfo != null && networkInfo.isConnected()) {
            // Show message text
            messageTextView.setText(getString(R.string.message_refresh));
            // Show loading indicator
            progressBar.setVisibility(View.VISIBLE);

            // Check if newsAdapter is not null (which will happen if on launch there was no
            // connection)
            if (mAdapter != null) {
                // Clear the adapter
                mAdapter.clearAll();
            }
            if (loaderManager != null) {
                // Restart Loader
                loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
            } else {
                initializeLoaderAndAdapter();
            }

        } else {
            // Hide progressBar
            progressBar.setVisibility(View.GONE);

            // Check if newsAdapter is not null (which will happen if on launch there was no connection)
            if (mAdapter != null) {
                // Clear the adapter
                mAdapter.clearAll();
            }

            // Display error
            messageTextView.setText(getString(R.string.message_no_internet_connection));
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}