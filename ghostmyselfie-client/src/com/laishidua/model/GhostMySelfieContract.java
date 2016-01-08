package com.laishidua.model;

import com.laishidua.R;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This contract defines the metadata for the GhostMySelfieContentProvider,
 * including the provider's access URIs and its "database" constants.
 */
public final class GhostMySelfieContract {
    /**
     * This ContentProvider's unique identifier.
     */
    public static final String CONTENT_AUTHORITY =
        "com.laishidua.ghostmyselfieprovider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which
     * apps will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI =
        Uri.parse("content://"
                  + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible
     * URI's).  For instance, content://com.laishidua/character_table/ is
     * a valid path for looking at Character data.  Conversely,
     * content://com.laishidua/givemeroot/ will fail, as the
     * ContentProvider hasn't been given any information on what to do
     * with "givemeroot".
     */
    public static final String PATH_GHOSTMYSELFIE =
        GhostMySelfieEntry.TABLE_GHOSTMYSELFIE;
    public static final String PATH_SETTINGS =
            GhostMySelfieEntry.TABLE_SETTINGS;

    /*
     * Columns
     */

    /**
     * Inner class that defines the table contents of the Hobbit
     * table.
     */
    public static final class GhostMySelfieEntry implements BaseColumns {
        /**
         * Use BASE_CONTENT_URI to create the unique URI for Acronym
         * Table that apps will use to contact the content provider.
         */
        public static final Uri CONTENT_URI_GHOSTMYSELFIE = 
            BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_GHOSTMYSELFIE).build();
        public static final Uri CONTENT_URI_SETTINGS = 
                BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SETTINGS).build();        
        

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 0..x items.
         */
        public static final String CONTENT_ITEMS_GMS_TYPE =
            "vnd.android.cursor.dir/"
            + CONTENT_AUTHORITY
            + "/" 
            + PATH_GHOSTMYSELFIE;
        public static final String CONTENT_ITEMS_SETTINGS_TYPE =
                "vnd.android.cursor.dir/"
                + CONTENT_AUTHORITY
                + "/" 
                + PATH_SETTINGS;        
            
        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 1 item.
         */
        public static final String CONTENT_ITEM_GMS_TYPE =
            "vnd.android.cursor.item/"
            + CONTENT_AUTHORITY
            + "/" 
            + PATH_GHOSTMYSELFIE;
        public static final String CONTENT_ITEM_SETTINGS_TYPE =
                "vnd.android.cursor.item/"
                + CONTENT_AUTHORITY
                + "/" 
                + PATH_SETTINGS;        

        /**
         * Columns to display.
         */
        public static final String sGMSColumnsToDisplay [] = 
            new String[] {
            GhostMySelfieContract.GhostMySelfieEntry._ID,
            GhostMySelfieContract.GhostMySelfieEntry.COLUMN_TITLE,
            GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CONTENT_TYPE,
            GhostMySelfieContract.GhostMySelfieEntry.COLUMN_STAR_RATING,
            GhostMySelfieContract.GhostMySelfieEntry.COLUMN_LOCAL_PATH,
            GhostMySelfieContract.GhostMySelfieEntry.COLUMN_SERVER_ID
        };
        public static final String sSettingsColumnsToDisplay [] = 
                new String[] {
                GhostMySelfieContract.GhostMySelfieEntry._ID,
                GhostMySelfieContract.GhostMySelfieEntry.COLUMN_GHOST,
                GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_GRAY,
                GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_BLUR,
                GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_DARK,
                GhostMySelfieContract.GhostMySelfieEntry.COLUMN_ACTIVATE_REMINDER,
                GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_HOUR,
                GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_MINUTE,
                GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CURRENT_USER
            };        
    
        /**
         * Resource Ids of the columns to display.
         */
        public static final int[] sColumnResIds = 
            new int[] {
        		R.id.tvSelfieTitle,
        		R.id.tvSelfieRating,
        		R.id.ratingBar
        };

        /**
         * Name of the database table.
         */
        public static final String TABLE_GHOSTMYSELFIE =
            "ghostmyselfie_table";
        public static final String TABLE_SETTINGS =
                "settings_table";        

        /**
         * Columns to store data for GhostMySelfie table.
         */
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT_TYPE = "contentType";
        public static final String COLUMN_STAR_RATING = "star_rating";
        public static final String COLUMN_LOCAL_PATH = "local_path";
        public static final String COLUMN_SERVER_ID = "server_id";
        
        /**
         * Columns to store data for Settings table.
         */
        public static final String COLUMN_GHOST = "show_ghost";
        public static final String COLUMN_FILTER_GRAY = "filter_gray";
        public static final String COLUMN_FILTER_BLUR = "filter_blur";
        public static final String COLUMN_FILTER_DARK = "filter_dark";
        public static final String COLUMN_ACTIVATE_REMINDER = "activate_reminder"; 
        public static final String COLUMN_REMINDER_HOUR = "reminder_hour"; 
        public static final String COLUMN_REMINDER_MINUTE = "reminder_minute";
        public static final String COLUMN_CURRENT_USER = "current_user";

        /**
         * Return a Uri that points to the row containing a given id.
         * 
         * @param id
         * @return Uri
         */
        public static Uri buildUriGhostMySelfie(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI_GHOSTMYSELFIE,
                                              id);
        }
        
        /**
         * Return a Uri that points to the row containing a given id.
         * 
         * @param id
         * @return Uri
         */
        public static Uri buildUriOptions(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI_SETTINGS,
                                              id);
        }        
        
        
    }

    /**
     * The code that is returned when a URI for more than 1 items is
     * matched against the given components.  Must be positive.
     */
    public static final int GHOSTMYSELPHIES = 100;

    /**
     * The code that is returned when a URI for exactly 1 item is
     * matched against the given components.  Must be positive.
     */
    public static final int GHOSTMYSELPHIE = 101;
    
    /**
     * The code that is returned when a URI for more than 1 items is
     * matched against the given components.  Must be positive.
     */
    public static final int SETTINGS = 102;

    /**
     * The code that is returned when a URI for exactly 1 item is
     * matched against the given components.  Must be positive.
     */
    public static final int SETTING = 103;    
    

    /**
     * The URI Matcher used by this content provider.
     */
    public static final UriMatcher sUriMatcher =
        buildUriMatcher();

    /**
     * Helper method to match each URI to the ACRONYM integers
     * constant defined above.
     * 
     * @return UriMatcher
     */
    protected static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code
        // to return when a match is found.  The code passed into the
        // constructor represents the code to return for the rootURI.
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = 
            new UriMatcher(UriMatcher.NO_MATCH);

        // For each type of URI that is added, a corresponding code is
        // created.
        matcher.addURI(GhostMySelfieContract.CONTENT_AUTHORITY,
                       GhostMySelfieContract.PATH_GHOSTMYSELFIE,
                       GHOSTMYSELPHIES);
        matcher.addURI(GhostMySelfieContract.CONTENT_AUTHORITY,
                       GhostMySelfieContract.PATH_GHOSTMYSELFIE
                       + "/#",
                       GHOSTMYSELPHIE);
        matcher.addURI(GhostMySelfieContract.CONTENT_AUTHORITY,
                GhostMySelfieContract.PATH_SETTINGS,
                SETTINGS);
        matcher.addURI(GhostMySelfieContract.CONTENT_AUTHORITY,
                GhostMySelfieContract.PATH_SETTINGS
                + "/#",
                SETTING);       
        return matcher;
    }
}
