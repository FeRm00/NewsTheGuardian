package com.kikisnight.newstheguardian;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.widget.Toast;

import static com.kikisnight.newstheguardian.NewsActivity.LOG_TAG;


/**
 * {@link NewsAdapter} is an {@link NewsAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link News} objects.
 * A ViewHolder will be used to support the ReclyView
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> articleNews;
    private Context articleContext;



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView newsTitle;
        TextView newsTopic;
        TextView newsPublicationDate;
        TextView newsPublicationTime;

        private Context context;

        public ViewHolder(Context context, View itemView){

            super(itemView);
            this.context = context;

            itemView.setOnClickListener(this);

            newsTitle = (TextView) itemView.findViewById(R.id.article_title);
            newsTopic = (TextView) itemView.findViewById(R.id.article_topic);
            newsPublicationDate = (TextView) itemView.findViewById(R.id.article_date);
            newsPublicationTime = (TextView) itemView.findViewById(R.id.article_time);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            News news = articleNews.get(position);

            // Get the Url from the current News
            String articleUrl = news.getUrl();

            // Convert the String Url into a URI object
            Uri newsURI = Uri.parse(articleUrl);

            // Create new intent to view the article's URL
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsURI);
            //Check if there is an app installed on the phone, able to handle the event, before launch it
            if (websiteIntent.resolveActivity(context.getPackageManager()) != null) {
                // Start the intent
                context.startActivity(websiteIntent);
            } else {
                Toast.makeText(getContext(), "No browser found to open the website.", Toast.LENGTH_SHORT).show();
            }

        }
    }


    // Pass in the contact array into the constructor
    public NewsAdapter(Context context, List<News> newsItems) {
        articleContext = context;
        articleNews = newsItems;
    }

    private Context getContext() {
        return articleContext;
    }

    // Inflating the layout from XML and returning the holder
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout and return ViewHolder
        View listView = inflater.inflate(R.layout.news_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(articleContext, listView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder viewHolder, int position) {

        News newsItem = articleNews.get(position);

        // Declaration of the view
        TextView newsTitleTextView = viewHolder.newsTitle;
        TextView newsTopicTextView = viewHolder.newsTopic;
        TextView newsPublicationDateTextView = viewHolder.newsPublicationDate;
        TextView newsPublicationTimeTextView = viewHolder.newsPublicationTime;

        newsTitleTextView.setText(newsItem.getTitle());
        newsTopicTextView.setText(newsItem.getTopic());
        newsPublicationDateTextView.setText(formatDate(newsItem.getPublicationDate()));
        newsPublicationTimeTextView.setText(formatTime(newsItem.getPublicationDate()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return articleNews.size();
    }

    // Adds new items to articleNews and refreshes the layout
    public void addAll(List<News> newsItemList) {
        articleNews.clear();
        articleNews.addAll(newsItemList);
        notifyDataSetChanged();
    }

    // Clears articleNews
    public void clearAll() {
        articleNews.clear();
        notifyDataSetChanged();
    }

    // Convert JSON webPublicationDate to Date and Time
    public String formatDate(String completDate){
        //Remove the last letter "Z" from the webPublicationData
        completDate = completDate.substring(0, completDate.length() - 1);
        //Format of the old and new date
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        String newDate = "";
        try {
            date = oldDateFormat.parse(completDate);
            newDate = newDateFormat.format(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error creating the Date: " + e);
        }
        return newDate;
    }

    public String formatTime(String completDate){
        //Remove the last letter "Z" from the webPublicationData
        completDate = completDate.substring(0, completDate.length() - 1);
        //Format of the old and new date
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("HH:mm");
        Date date = null;
        String newTime = "";
        try {
            date = oldDateFormat.parse(completDate);
            newTime = newDateFormat.format(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error creating the Time: " + e);
        }
        return newTime;
    }
}