package com.laishidua.presenter;

import java.lang.ref.WeakReference;

import com.laishidua.model.GhostMySelfieContract;

import com.laishidua.R;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

/**
 * Class that implements the operations for inserting, querying,
 * updating, and deleting characters from the HobbitContentProvider.
 * This class plays the role of the "Implementor" in the Bridge
 * pattern and the "Abstract Class" in the Template Method pattern.
 * It's also an example of the "External Polymorphism" pattern.
 */
public abstract class GhostMySelfieOpsImpl {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        GhostMySelfieOpsImpl.class.getSimpleName();

    /**
     * Stores a Weak Reference to the HobbitOps.View so the garbage
     * collector can remove it when it's not in use.
     */
    protected WeakReference<GhostMySelfieOps.View> mGhostMySelfieView;

    /**
     * Contains the most recent result from a query so the display can
     * be updated after a runtime configuration change.
     */

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the HobbitOpsImpl object after it's been created.
     *
     * @param view     The currently active HobbitOps.View.
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    public void onConfiguration(GhostMySelfieOps.View view,
                                boolean firstTimeIn) {
        final String time =
                firstTimeIn 
                ? "first time" 
                : "second+ time";

        Log.d(TAG,
                "onConfiguration() called the "
                + time
                + " with view = "
                + view);
        
        // Create a WeakReference to the GhostMySelfieView.
        mGhostMySelfieView = new WeakReference<>(view);
        
    }
    
    /**
    * Release resources to prevent leaks.
    */
    public void close() {
        // No-op.
    }

    /**
     * Return a @a SimpleCursorAdapter that can be used to display the
     * contents of the GhostMySelfie ContentProvider.
     */
    public SimpleCursorAdapter makeCursorAdapter() {
        return new SimpleCursorAdapter
            (mGhostMySelfieView.get().getActivityContext(),
             R.layout.ghostmyselfie_list_item, 
             null,
             GhostMySelfieContract.GhostMySelfieEntry.sGMSColumnsToDisplay,
             GhostMySelfieContract.GhostMySelfieEntry.sColumnResIds,
             1);
    }

    /**
     * Insert a GhostMySelfie into the GhostMySelfieContentProvider.
     * Plays the role of a "template method"
     * in the Template Method pattern.
     */
    public Uri insert(String title, String contentType,
    			double starRating, String localPath,
    			long serverID) throws RemoteException {
        final ContentValues cvs = new ContentValues();

        // Insert data.
        cvs.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_TITLE,
                title);
        cvs.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CONTENT_TYPE,
        		contentType);
        cvs.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_STAR_RATING,
        		starRating);
        cvs.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_LOCAL_PATH,
        		localPath);
        cvs.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_SERVER_ID,
        		serverID);

        // Call to the hook method.
        return insert(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_GHOSTMYSELFIE,
                      cvs);
    }

    /**
     * Insert @a ContentValues into the GhostMySelfieContentProvider at
     * the @a uri.  Plays the role of an "abstract hook method" in the
     * Template Method pattern.
     */
    protected abstract Uri insert(Uri uri,
                                  ContentValues cvs)
        throws RemoteException;

    /**
     * Insert an array of @a ContentValues into the
     * GhostMySelfieContentProvider at the @a uri.  Plays the role of an
     * "abstract hook method" in the Template Method pattern.
     */
    protected abstract int bulkInsert(Uri uri,
                                      ContentValues[] cvsArray)
        throws RemoteException;

    /**
     * Return a Cursor from a query on the GhostMySelfieContentProvider at
     * the @a uri.  Plays the role of an "abstract hook method" in the
     * Template Method pattern.
     */
    public abstract Cursor query(Uri uri,
                                 String[] projection,
                                 String selection,
                                 String[] selectionArgs,
                                 String sortOrder) 
        throws RemoteException;

    /**
     * Delete the @a selection and @a selectionArgs with the @a
     * ContentValues in the GhostMySelfieContentProvider at the @a uri.
     * Plays the role of an "abstract hook method" in the Template
     * Method pattern.
     */
    public abstract int update(Uri uri,
                               ContentValues cvs,
                               String selection,
                               String[] selectionArgs)
        throws RemoteException;

    /**
     * Delete the @a selection and @a selectionArgs from the
     * GhostMySelfieContentProvider at the @a uri.  Plays the role of an
     * "abstract hook method" in the Template Method pattern.
     */
    protected abstract int delete(Uri uri,
                                  String selection,
                                  String[] selectionArgs)
        throws RemoteException;

    /**
     * Delete all characters from the GhostMySelfieContentProvider.  Plays
     * the role of a "template method" in the Template Method pattern.
     */
    public int deleteAll() 
        throws RemoteException {
        return delete(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_GHOSTMYSELFIE,
                      null,
                      null);
    }

}
