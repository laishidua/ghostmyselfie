package com.laishidua.presenter;

import java.io.File;
import java.lang.ref.WeakReference;

import com.laishidua.common.ConfigurableOps;
import com.laishidua.common.ContextView;
import com.laishidua.model.mediator.GhostMySelfieDataMediator;
import com.laishidua.model.services.GhostMySelfieService;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Provides all the GhostMySelfie-related operations.  It implements
 * ConfigurableOps so it can be created/managed by the GenericActivity
 * framework.  It extends GenericAsyncTaskOps so its doInBackground()
 * method runs in a background task.  It plays the role of the
 * "Abstraction" in Bridge pattern and the role of the "Presenter" in
 * the Model-View-Presenter pattern.
 */
public class GhostMySelfieDetailOps
       implements ConfigurableOps<GhostMySelfieDetailOps.View> {
    /**
     * Debugging tag used by the Android logger.
     */
    private static final String TAG =
        GhostMySelfieOps.class.getSimpleName();
    
    private GhostMySelfieDetailOpsContentResolver mGhostMySelfieDetailOpsContentResolver;
    
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
    }
        
    /**
     * Used to enable garbage collection.
     */
    private WeakReference<GhostMySelfieDetailOps.View> mGhostMySelfieView;
    
    /**
     * GhostMySelfieDataMediator mediates the communication between GhostMySelfie
     * Service and local storage on the Android device.
     */
    GhostMySelfieDataMediator mGhostMySelfieMediator;
    
	String mFilePath;

    /**
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public GhostMySelfieDetailOps() {

    }

    /**
     * Called after a runtime configuration change occurs to finish
     * the initialisation steps.
     * @return 
     */
	@Override
	public void onConfiguration(View view, boolean firstTimeIn) {

		final String time =
                firstTimeIn 
                ? "first time" 
                : "second+ time";
            
            Log.d(TAG,
                  "onConfiguration() called the "
                  + time
                  + " with view = "
                  + view);

            // (Re)set the mGhostMySelfieView WeakReference.
            mGhostMySelfieView =
                new WeakReference<>(view);
            
            if (firstTimeIn) {
                // Create GhostMySelfieDataMediator that will mediate the
                // communication between Server and Android Storage.
                mGhostMySelfieMediator =
                    new GhostMySelfieDataMediator();
                
                mGhostMySelfieDetailOpsContentResolver = new GhostMySelfieDetailOpsContentResolver();

                mGhostMySelfieDetailOpsContentResolver.onConfiguration(view,
                        firstTimeIn);
            }
		
	}

    /**
     * Start a service that downloads the selfie having given Id and title to save in the MediaStore.
     *   
     * @param id	 : the Server Id of the GhostMySelfie
     * @param title  : the GhostMySelfie title to save in the MediaStore
     */
    public void downloadGhostMySelfie(long id, String title) {
        // Sends a Download Intent command to the GhostMySelfieService.
        mGhostMySelfieView.get().getApplicationContext().startService
            (GhostMySelfieService.makeDownloadIntent
                 (mGhostMySelfieView.get().getApplicationContext(),
                  id, title));
    }
    
    /**
     * Start a service that deletes the selfie in client and server having given Id (title param is not used).
     *   
     * @param id	 : the Server Id of the GhostMySelfie
     * @param title  : the GhostMySelfie title to save in the MediaStore
     */
    public void deleteGhostMySelfie(long id, String title) {
        // Sends a Download Intent command to the GhostMySelfieService.
        mGhostMySelfieView.get().getApplicationContext().startService
            (GhostMySelfieService.makeDeleteIntent
                 (mGhostMySelfieView.get().getApplicationContext(),
                  id));
    }    

    /**
     * Start a service that rates a Selfie having given Id and rate value. it represents in the view of detail activity as stars.
     * 
     * @param id	 : the Server Id of the GhostMySelfie
     * @param rating : the user's vote
     */
    public void rateGhostMySelfie(long id, long rating) {
        // Sends a Rate Intent command to the GhostMySelfieService.
        mGhostMySelfieView.get().getApplicationContext().startService
        (GhostMySelfieService.makeRateIntent 
             (mGhostMySelfieView.get().getApplicationContext(),
              id, rating));
    }


    /**
     * start an Activity to show a Selfie in fullscreen.
     * 
     * @param filePath : the absolute selfie file path
     */
    public void showGhostMySelfie(String filePath) {
		// show Selfie
    	Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(filePath)), "image/*");
		mGhostMySelfieView.get().getActivityContext().startActivity(intent);
    	
    }



    
}
