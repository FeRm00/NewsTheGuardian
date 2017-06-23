package com.kikisnight.newstheguardian;

/**
 * {@link News} represents the magnitude, location and time for each earthquake
 * It contains a the magnitude of the earthquake, the location, and the time.
 */

public class News {

    // Keyword "article" used before the variables to differentiate like private variables
    private String articleTitle;
    private String articleTopic;
    private String articleTime;
    private String articleUrl;


    /**
     * Constructs a new {@link News} object.
     *
     * @param title is the tittle of the article on the news
     * @param topic is the topic of the news
     * @param time is the time in milliseconds (from the Epoch) when the
     *  news was published
     * @param url is the website URL to find complet the news
     */
    public News (String title, String topic, String time, String url) {

        articleTitle = title;
        articleTopic = topic;
        articleTime = time;
        articleUrl = url;
    }

    /**
     * Returns the tittle of the news.
     */
    public String getTitle () {
        return articleTitle;
    }

    /**
     * Returns the topic of the news.
     */
    public String getTopic () {
        return articleTopic;
    }

    /**
     * Returns the published date of the news
     */
    public String getPublicationDate() {
        return articleTime;
    }

    /**
     * Return the URL of the earthquake.
     */
    public String getUrl(){
        return articleUrl;
    }

}