package com.laishidua.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.net.Uri;
import android.util.Log;

/**
 * Content Provider implementation that uses SQLite to manage GhostMySelfies.
 * This class plays the role of the "Concrete  Implementor" in the
 * Bridge pattern and the "Concrete Class" in the TemplateMethod pattern.
 */
public class GhostMySelfieProviderImplSQLite 
       extends GhostMySelfieProviderImpl  {
    /**
     * Use HobbitDatabaseHelper to manage database creation and version
     * management.
     */
    private GhostMySelfieDatabaseHelper mOpenHelper;

    /**
     * Constructor initializes the super class.
     */
    public GhostMySelfieProviderImplSQLite(Context context) {
        super(context);
    }

    /**
     * Return true if successfully started.
     */
    public boolean onCreate() {
    	Log.d(TAG, "main oncreate called");
    	// Create the GhostMySelfieDatabaseHelper.
        mOpenHelper =
            new GhostMySelfieDatabaseHelper(mContext);
        return true;
    }

    /**
     * Method called to handle insert requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public Uri insertGhostMySelfies(Uri uri,
                                ContentValues cvs) {
        final SQLiteDatabase db =
            mOpenHelper.getWritableDatabase();

        long id =
            db.insert(GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE,
                      null,
                      cvs);

        // Check if a new row is inserted or not.
        if (id > 0)
            return GhostMySelfieContract.GhostMySelfieEntry.buildUriGhostMySelfie(id);
        else
            throw new android.database.SQLException
                ("Failed to insert row into " 
                 + uri);
    }

    /**
     * Method that handles bulk insert requests.  This plays the role
     * of the "concrete hook method" in the Template Method pattern.
     */
    @Override
    public int bulkInsertGhostMySelfies(Uri uri,
                                    ContentValues[] cvsArray) {
        // Create and/or open a database that will be used for reading
        // and writing. Once opened successfully, the database is
        // cached, so you can call this method every time you need to
        // write to the database.
        final SQLiteDatabase db =
            mOpenHelper.getWritableDatabase();

        int returnCount = 0;

        // Begins a transaction in EXCLUSIVE mode. 
        db.beginTransaction();
        try {
            for (ContentValues cvs : cvsArray) {
                final long id =
                    db.insert(GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE,
                              null,
                              cvs);
                if (id != -1)
                    returnCount++;
            }

            // Marks the current transaction as successful.
            db.setTransactionSuccessful();
        } finally {
            // End a transaction.
            db.endTransaction();
        }
        return returnCount;
    }


    /**
     * Method called to handle query requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public Cursor queryGhostMySelfies(Uri uri,
                                  String[] projection,
                                  String selection,
                                  String[] selectionArgs,
                                  String sortOrder) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     "OR");
        return mOpenHelper.getReadableDatabase().query
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE,
             projection,
             selection,
             selectionArgs,
             null,
             null,
             sortOrder);
    }

    /**
     * Method called to handle query requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public Cursor queryGhostMySelfie(Uri uri,
                                 String[] projection,
                                 String selection,
                                 String[] selectionArgs,
                                 String sortOrder) {
        // Query the SQLite database for the particular rowId based on
        // (a subset of) the parameters passed into the method.
        return mOpenHelper.getReadableDatabase().query
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE,
             projection,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs,
             null,
             null,
             sortOrder);
    }
    
    /**
     * Method called to handle query requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public Cursor querySettings(Uri uri,
                                  String[] projection,
                                  String selection,
                                  String[] selectionArgs,
                                  String sortOrder) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     "OR");
        return mOpenHelper.getReadableDatabase().query
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_SETTINGS,
             projection,
             selection,
             selectionArgs,
             null,
             null,
             sortOrder);
    }    
    
    /**
     * Method called to handle query requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public Cursor querySetting(Uri uri,
                                 String[] projection,
                                 String selection,
                                 String[] selectionArgs,
                                 String sortOrder) {
        // Query the SQLite database for the particular rowId based on
        // (a subset of) the parameters passed into the method.
        return mOpenHelper.getReadableDatabase().query
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_SETTINGS,
             projection,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs,
             null,
             null,
             sortOrder);
    }    

    /**
     * Method called to handle update requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int updateGhostMySelfies(Uri uri,
                                ContentValues cvs,
                                String selection,
                                String[] selectionArgs) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     " OR ");
        return mOpenHelper.getWritableDatabase().update
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE,
             cvs,
             selection,
             selectionArgs);
    }

    /**
     * Method called to handle update requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int updateGhostMySelfie(Uri uri,
                               ContentValues cvs,
                               String selection,
                               String[] selectionArgs) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection,
                                     selectionArgs,
                                     " OR ");
        // Just update a single row in the database.
        return mOpenHelper.getWritableDatabase().update
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE,
             cvs,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs);
    }
    
    /**
     * Method called to handle update requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int updateOptions(Uri uri,
                                ContentValues cvs,
                                String selection,
                                String[] selectionArgs) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     " OR ");
        return mOpenHelper.getWritableDatabase().update
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_SETTINGS,
             cvs,
             selection,
             selectionArgs);
    }    
    
    /**
     * Method called to handle update requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int updateOption(Uri uri,
                               ContentValues cvs,
                               String selection,
                               String[] selectionArgs) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection,
                                     selectionArgs,
                                     " OR ");
        // Just update a single row in the database.
        return mOpenHelper.getWritableDatabase().update
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_SETTINGS,
             cvs,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs);
    }    

    /**
     * Method called to handle delete requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int deleteGhostMySelfies(Uri uri,
                                String selection,
                                String[] selectionArgs) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     " OR ");
        return mOpenHelper.getWritableDatabase().delete
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE,
             selection,
             selectionArgs);
    }

    /**
     * Method called to handle delete requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int deleteGhostMySelfie(Uri uri,
                               String selection,
                               String[] selectionArgs) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     " OR ");
        // Just delete a single row in the database.
        return mOpenHelper.getWritableDatabase().delete
            (GhostMySelfieContract.GhostMySelfieEntry.TABLE_GHOSTMYSELFIE,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs);
    }

    /**
     * Return a selection string that concatenates all the
     * @a selectionArgs for a given @a selection using the given @a
     * operation.
     */
    private String addSelectionArgs(String selection,
                                    String [] selectionArgs,
                                    String operation) {
        // Handle the "null" case.
        if (selection == null
            || selectionArgs == null)
            return null;
        else {
            String selectionResult = "";

            // Properly add the selection args to the selectionResult.
            for (int i = 0;
                 i < selectionArgs.length - 1;
                 ++i)
                selectionResult += (selection 
                           + " = ? " 
                           + operation 
                           + " ");
            
            // Handle the final selection case.
            selectionResult += (selection
                       + " = ?");

            // Output the selectionResults to Logcat.
            Log.d(TAG,
                  "selection = "
                  + selectionResult
                  + " selectionArgs = ");
            for (String args : selectionArgs)
                Log.d(TAG,
                      args
                      + " ");

            return selectionResult;
        }
    }        

    /**
     * Helper method that appends a given key id to the end of the
     * WHERE statement parameter.
     */
    private static String addKeyIdCheckToWhereStatement(String whereStatement,
                                                        long id) {
        String newWhereStatement;
        if (TextUtils.isEmpty(whereStatement)) 
            newWhereStatement = "";
        else 
            newWhereStatement = whereStatement + " AND ";

        // Append the key id to the end of the WHERE statement.
        return newWhereStatement 
            + GhostMySelfieContract.GhostMySelfieEntry._ID
            + " = '"
            + id 
            + "'";
    }
}
