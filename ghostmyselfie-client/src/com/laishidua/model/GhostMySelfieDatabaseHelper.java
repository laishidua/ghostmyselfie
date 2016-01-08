package com.laishidua.model;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The database helper used by the GhostMySelfie Content Provider to create
 * and manage its underling database.
 */
public class GhostMySelfieDatabaseHelper extends SQLiteOpenHelper {
    /**
     * Database name.
     */
    private static String DATABASE_NAME =
        "com_laishidua_ghostmyselfie_db";

    /**
     * Database version number, which is updated with each schema
     * change.
     */
    private static int DATABASE_VERSION = 1;

    /*
     * SQL create table statements.
     */

    /**
     * SQL statement used to create the GhostMySelfie table.
     */
    final String SQL_CREATE_GHOSTMYSELFIE_TABLE =
        "CREATE TABLE "
        + GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE + " (" 
        + GhostMySelfieContract.GhostMySelfieEntry._ID + " INTEGER PRIMARY KEY, " 
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_TITLE + " TEXT NOT NULL, " 
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CONTENT_TYPE + " TEXT, "
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_STAR_RATING + " DOUBLE, "
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_LOCAL_PATH + " TEXT, "
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_SERVER_ID + " LONG "
        + " );";
    
    /**
     * SQL statement used to create the GhostMySelfie table.
     */
    final String SQL_CREATE_OPTIONS_TABLE =
        "CREATE TABLE "
        + GhostMySelfieContract.GhostMySelfieEntry.TABLE_SETTINGS + " (" 
        + GhostMySelfieContract.GhostMySelfieEntry._ID + " INTEGER PRIMARY KEY, " 
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_GHOST + " BOOLEAN NOT NULL CHECK (" + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_GHOST + " IN (0,1)), " 
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_GRAY + " BOOLEAN NOT NULL CHECK (" + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_GRAY + " IN (0,1)), "
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_BLUR + " BOOLEAN NOT NULL CHECK (" + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_BLUR + " IN (0,1)), "        
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_DARK + " BOOLEAN NOT NULL CHECK (" + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_DARK + " IN (0,1)), "        
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_ACTIVATE_REMINDER + " BOOLEAN NOT NULL CHECK (" + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_ACTIVATE_REMINDER + " IN (0,1)), " 
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_HOUR + " INTEGER NOT NULL,"
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_MINUTE + " INTEGER NOT NULL,"
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CURRENT_USER + " TEXT "
        + " );";    

    /**
     * SQL statement used to create the GhostMySelfie table.
     */
    final String SQL_INSERT_SETTINGS =
        "INSERT INTO "
        + GhostMySelfieContract.GhostMySelfieEntry.TABLE_SETTINGS + " (" 
        + GhostMySelfieContract.GhostMySelfieEntry._ID + ","
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_GHOST + ", " 
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_GRAY + ", "
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_BLUR + ", "        
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_DARK + ", "      
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_ACTIVATE_REMINDER + ", " 
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_HOUR + ", "
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_MINUTE + ", "
        + GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CURRENT_USER
        + " ) VALUES (1,1,0,0,0,0,0,0, null);";     
    
     /**
     * Constructor - initialize database name and version, but don't
     * actually construct the database (which is done in the
     * onCreate() hook method). It places the database in the
     * application's cache directory, which will be automatically
     * cleaned up by Android if the device runs low on storage space.
     * 
     * @param context
     */
    public GhostMySelfieDatabaseHelper(Context context) {
        super(context, 
              context.getCacheDir()
              + File.separator 
              + DATABASE_NAME, 
              null,
              DATABASE_VERSION);
    }

    /**
     * Hook method called when the database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tables.
        db.execSQL(SQL_CREATE_GHOSTMYSELFIE_TABLE);
        db.execSQL(SQL_CREATE_OPTIONS_TABLE);
        db.execSQL(SQL_INSERT_SETTINGS);
    }

    /**
     * Hook method called when the database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        // Delete the existing tables.
        db.execSQL("DROP TABLE IF EXISTS "
                   + GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE);
        db.execSQL("DROP TABLE IF EXISTS "
                + GhostMySelfieContract.GhostMySelfieEntry.TABLE_SETTINGS);        
        // Create the new tables.
        onCreate(db);
    }
}
