package com.laishidua.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.laishidua.common.ConfigurableOps;
import com.laishidua.common.ContextView;
import com.laishidua.common.GenericAsyncTask;
import com.laishidua.common.GenericAsyncTaskOps;
import com.laishidua.common.Utils;
import com.laishidua.model.GhostMySelfieContract;
import com.laishidua.model.mediator.GhostMySelfieDataMediator;
import com.laishidua.model.mediator.webdata.GhostMySelfie;
import com.laishidua.model.services.GhostMySelfieService;
import com.laishidua.utils.Constants;
import com.laishidua.view.ui.GhostMySelfieAdapter;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

/**
 * Provides all the GhostMySelfie-related operations.  It implements
 * ConfigurableOps so it can be created/managed by the GenericActivity
 * framework.  It extends GenericAsyncTaskOps so its doInBackground()
 * method runs in a background task.  It plays the role of the
 * "Abstraction" in Bridge pattern and the role of the "Presenter" in
 * the Model-View-Presenter pattern.
 */
public class GhostMySelfieOps
       implements GenericAsyncTaskOps<Void, Void, List<GhostMySelfie>>,
                  ConfigurableOps<GhostMySelfieOps.View> {
    /**
     * Debugging tag used by the Android logger.
     */
    private static final String TAG =
        GhostMySelfieOps.class.getSimpleName();
    
    /**
     * This interface defines the minimum interface needed by the
     * GhostMySelfieOps class in the "Presenter" layer to interact with the
     * GhostMySelfieListActivity in the "View" layer.
     */
    public interface View extends ContextView {
        /**
         * Finishes the Activity the GhostMySelfieOps is
         * associated with.
         */
        void finish();

        /**
         * Sets the Adapter that contains List of Selfies.
         */
        void setAdapter(GhostMySelfieAdapter ghostMySelfieAdapter);       

        /**
         * Display the contents of the cursor as a ListView.
         */
        void displayCursor(Cursor cursor);
    }
        
    /**
     * Used to enable garbage collection.
     */
    private WeakReference<GhostMySelfieOps.View> mGhostMySelfieView;
    
    /**
     * The GenericAsyncTask used to expand an GhostMySelfie in a background
     * thread via the GhostMySelfie web service.
     */
    private GenericAsyncTask<Void,
                             Void,
                             List<GhostMySelfie>,
                             GhostMySelfieOps> mAsyncTask;
    
    /**
     * GhostMySelfieDataMediator mediates the communication between GhostMySelfie
     * Service and local storage on the Android device.
     */
    GhostMySelfieDataMediator mGhostMySelfieMediator;
    
    /**
     * The Adapter that is needed by ListView to show the list of
     * Videos.
     */
    private GhostMySelfieAdapter mAdapter;
    
    /**
     * Reference to the designed Concrete Implementor (i.e., either
     * GhostMySelfieOpsContentResolver or GhostMySelfieOpsContentProviderClient).
     */
    private GhostMySelfieOpsImpl mGhostMySelfieOpsImpl;

    /**
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public GhostMySelfieOps() {
    }
    
    /**
     * Called after a runtime configuration change occurs to finish
     * the initialisation steps.
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

        if (firstTimeIn)
        	mGhostMySelfieOpsImpl = new GhostMySelfieOpsContentResolver();

        mGhostMySelfieOpsImpl.onConfiguration(view,
                firstTimeIn);

        // (Re)set the mGhostMySelfieView WeakReference.
        mGhostMySelfieView =
            new WeakReference<>(view);
        
        if (firstTimeIn) {
            // Create GhostMySelfieDataMediator that will mediate the
            // communication between Server and Android Storage.
            mGhostMySelfieMediator =
                new GhostMySelfieDataMediator();
            
            // Create a local instance of our custom Adapter for our
            // ListView.
            mAdapter = 
                 new GhostMySelfieAdapter(mGhostMySelfieView.get().getApplicationContext());

            // Get the GhostMySelfieList from Server. 
            getGhostMySelfieList();
        }
        
        // Set the adapter to the ListView.
        mGhostMySelfieView.get().setAdapter(mAdapter);
        
    }
    
    public GhostMySelfieAdapter getAdapter(){
    	return mAdapter;
    }

    /**
     * Start a service that Uploads a selfie having given Uri and a notification Id that will triggered for each selfie uploaded.
     *   
     * @param ghostmyselfieUri
     * @param notificationId
     */
    public void uploadGhostMySelfie(Uri ghostmyselfieUri, int notificationId){
        // Sends an Intent command to the GhostMySelfieService.
        mGhostMySelfieView.get().getApplicationContext().startService
        (GhostMySelfieService.makeUploadIntent 
             (mGhostMySelfieView.get().getApplicationContext(),
              ghostmyselfieUri, notificationId));
    }

    /**
     * Get the list of selfies corresponding to the logged user.
     */
    public void getGhostMySelfieList(){
        // if nullified by a failed connection...
        try {
	    	if (mGhostMySelfieMediator == null) {
	    		Log.d("VO.gVL", "renewing mediator...");
	        	mGhostMySelfieMediator = new GhostMySelfieDataMediator();
	    		Log.d("VO.gVL", "done");
	        }
	        mAsyncTask = new GenericAsyncTask<>(this);
	        mAsyncTask.execute();
        }
	    catch (Exception e) {
    		Log.d("VO.gVL", e.getLocalizedMessage());
	    	e.printStackTrace();
	    }
	        
    }
    
    /**
     * Retrieve the List of Selfies by help of GhostMySelfieDataMediator via a
     * synchronous two-way method call, which runs in a background
     * thread to avoid blocking the UI thread.
     */
    @Override
    public List<GhostMySelfie> doInBackground(Void... params) {
    	return mGhostMySelfieMediator.getGhostMySelfieList();
    }

    /**
     * Display the results in the UI Thread.
     */
    @Override
    public void onPostExecute(List<GhostMySelfie> ghostMySelfies) {
    	Log.d(TAG, "Syncing GhostMySelfie List...");
    	Log.d(TAG, ghostMySelfies.toString());
    	dbSyncGhostMySelfieList(ghostMySelfies);
		displayGhostMySelfieList(dbGetGhostMySelfieList());
    }

    /**
     * Display the Selfies in ListView.
     * 
     * @param ghostMySelfies
     */
    public void displayGhostMySelfieList(List<GhostMySelfie> ghostMySelfies) {
        if (ghostMySelfies != null) {
            // Update the adapter with the List of Selfies.
            mAdapter.setGhostMySelfies(ghostMySelfies);

            Utils.showToast(mGhostMySelfieView.get().getActivityContext(),
                            "Selfies available from the GhostMySelfie Service");
        } else {
            Utils.showToast(mGhostMySelfieView.get().getActivityContext(),
                           "Please specify a valid address for the GhostMySelfie Service in the settings");

            // Invalidate the GhostMySelfieMediator
            mGhostMySelfieMediator = null;
            
        }
    }

    /**
     * Return a @a SimpleCursorAdapter that can be used to display the
     * contents of the GhostMySelfie ContentProvider.
     */
    public SimpleCursorAdapter makeCursorAdapter() {
        return mGhostMySelfieOpsImpl.makeCursorAdapter();
    }

    /**
    * Updates the local database, inserting missing selfies and updating the fields of the existing ones
    * If current user loged in is other than the last one, it reload the sqlite database with the selfies that correspond to the current user.
    */
    public void dbSyncGhostMySelfieList(List<GhostMySelfie> ghostMySelfies) {
        if (ghostMySelfies != null) {
        	//Cheking if is current user correspond to current list, if not, database reload.
    		try (Cursor cs = mGhostMySelfieOpsImpl.query(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_SETTINGS,
    				GhostMySelfieContract.GhostMySelfieEntry.sSettingsColumnsToDisplay,
    				GhostMySelfieContract.GhostMySelfieEntry._ID,
    				new String[] { "1" }, null);) {
    			if (cs.moveToFirst()) {
    			   String currentUser = cs.getString(cs.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CURRENT_USER));
                   if(currentUser ==  null || !currentUser.equals(Constants.user)){
	                   	ContentValues values = new ContentValues();
	                	values.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_GHOST , "1");
	                	values.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_BLUR , "0");
	                	values.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_DARK , "0");
	                	values.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_GRAY , "0");
	                	values.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CURRENT_USER , Constants.user);
	                	int res = mGhostMySelfieOpsImpl.update(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_SETTINGS,
	                			values,
	                			GhostMySelfieContract.GhostMySelfieEntry._ID, new String[]{ "1" });
	                	if(res > 0){
	                		Log.d(TAG, "Database reloaded succesfully");
	                	} else {
	                		Log.d(TAG, "Cannot reload database");
	                	}
	                	mGhostMySelfieOpsImpl.delete(
	                			GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_GHOSTMYSELFIE,
	                			null, null);
                   }
    			}
    		} catch (RemoteException e) {
    			e.printStackTrace();
    		}
        	
        	for (GhostMySelfie ghostMySelfie : ghostMySelfies) {
        		try (Cursor c = mGhostMySelfieOpsImpl.query(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_GHOSTMYSELFIE,
    					GhostMySelfieContract.GhostMySelfieEntry.sGMSColumnsToDisplay,
    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_SERVER_ID,
    					new String[] {String.format("%d", ghostMySelfie.getId()) }, null);) {
        			Log.d(TAG, "GhostMySelfie data:" + ghostMySelfie.toString());
					Log.d(TAG, "ins/upd Id: " + ghostMySelfie.getId() + " - Title.: " +  ghostMySelfie.getTitle());
        			
        			Log.d(TAG, "query results: " + c.getCount());
        			if (c.getCount() > 0) { // update
            			Log.d(TAG, "updating");
        		        final ContentValues cvs = new ContentValues();
        		        
        		        cvs.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CONTENT_TYPE,
        		                ghostMySelfie.getContentType());
            			Log.d(TAG, "AverageVote set to:" + ghostMySelfie.getAverageVote());
        		        cvs.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_STAR_RATING,
        		                ghostMySelfie.getAverageVote());
        		        cvs.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_TITLE,
        		                ghostMySelfie.getTitle());

        		        long rowsAffected = mGhostMySelfieOpsImpl.update(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_GHOSTMYSELFIE,
    										 cvs,
    										 GhostMySelfieContract.GhostMySelfieEntry.COLUMN_SERVER_ID,
    										 new String[] {String.format("%d", ghostMySelfie.getId()) });
            			Log.d(TAG, "Rows affected: "+ rowsAffected);
        				
        			}
        			else { // insert
            			Log.d(TAG, "inserting");
    					mGhostMySelfieOpsImpl.insert(ghostMySelfie.getTitle(),
            					ghostMySelfie.getContentType(), ghostMySelfie.getAverageVote(),
            					"", ghostMySelfie.getId());
        			}
        			
				} catch (RemoteException e) {
					e.printStackTrace();
				}
        	}
        }
    }
    
    /**
     * Retrieves the whole selfie list from the local database.
     * 
     * @return ...ahem, what did I just say?
     */
    public List<GhostMySelfie> dbGetGhostMySelfieList() {
        List<GhostMySelfie> selfies = new ArrayList<GhostMySelfie>();
		try (Cursor c = mGhostMySelfieOpsImpl.query(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_GHOSTMYSELFIE,
				GhostMySelfieContract.GhostMySelfieEntry.sGMSColumnsToDisplay,
				GhostMySelfieContract.GhostMySelfieEntry.COLUMN_SERVER_ID,
				null, null);) {
			if (c.moveToFirst()) {
				do {
					GhostMySelfie gms = new GhostMySelfie();
			        gms.setContentType(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_CONTENT_TYPE)));
			        gms.setServerId(c.getLong(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_SERVER_ID)));
			        gms.setAverageVote(c.getDouble(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_STAR_RATING)));
			        gms.setTitle(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_TITLE)));
			        selfies.add(gms);
			    } while (c.moveToNext());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return selfies;
    }
    
}
