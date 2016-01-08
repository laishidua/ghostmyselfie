package com.laishidua.model.services;

import java.io.File;

import com.laishidua.model.mediator.GhostMySelfieDataMediator;
import com.laishidua.model.mediator.webdata.AverageGhostMySelfieRating;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Intent Service that runs in background and executes various requests
 * to the server.  After the operation, it broadcasts the Intent to send
 * the result of the request to the calling Activity.
 */
public class GhostMySelfieService 
       extends IntentService {
    /**
     * Custom Action that will be used to send Broadcast to the
     * calling Activity.
     */
    public static final String ACTION_UPLOAD_SERVICE_RESPONSE =
                "com.laishidua.services.GhostMySelfieService.RESPONSE";
    
    /**
     * It is used by Notification Manager to send Notifications.
     */
    private static final String NOTIFICATION_ID = "Notification";
    
    public static final int NOTIFICATION_DEFAULT_ID = 1;

    public static final String ACTION_UPLOAD = "Upload";

    public static final String ACTION_DOWNLOAD = "Download";
    
    public static final String ACTION_DELETE = "Delete";

    public static final String ACTION_RATE = "Rate";

    public static final String DATA_OP = "Operation";

    public static final String DATA_ID = "Id";

    public static final String DATA_TITLE = "Title";

    public static final String DATA_PATH = "Path";

    public static final String DATA_VOTE = "Vote";

    public static final String DATA_RATING = "Rating";

    /**
     * GhostMySelfieDataMediator mediates the communication between GhostMySelfie
     * Service and local storage in the Android device.
     */
    private GhostMySelfieDataMediator mGhostMySelfieMediator;
    
    /**
     * Manages the Notification displayed in System UI.
     */
    private NotificationManager mNotifyManager;
    
    /**
     * Builder used to build the Notification.
     */
    private Builder mBuilder;
    
    /**
     * Constructor for GhostMySelfieService.
     * 
     * @param name
     */
    public GhostMySelfieService(String name) {
        super("GhostMySelfieService");     
    }
    
    /**
     * Constructor for GhostMySelfieService.
     * 
     * @param name
     */
    public GhostMySelfieService() {
        super("GhostMySelfieService");     
    }
    
    /**
     * Factory method that makes the explicit intent another Activity uses to
     *  call this Service for uploading a selfie.
     *  
     * @param context
     * @param ghostmyselfieUri
     * @return
     */
    public static Intent makeUploadIntent(Context context,
            							Uri ghostmyselfieUri, int notificationId) {
    	Intent intent = new Intent(context, 
    						GhostMySelfieService.class)
    				.setData(ghostmyselfieUri);
    	intent.putExtra(DATA_OP, ACTION_UPLOAD);
    	intent.putExtra(NOTIFICATION_ID, notificationId);
    	return intent;
    }
    
    /**
     * Factory method that makes the explicit intent another Activity
     * uses to call this Service for downloading a selfie.
     * 
     * @param context
     * @param id
     * @param title
     * @return
     */
    public static Intent makeDownloadIntent(Context context,
    		long id, String title) {
    	Intent intent = new Intent(context, 
    						GhostMySelfieService.class);
    	intent.putExtra(DATA_OP, ACTION_DOWNLOAD);
    	intent.putExtra(DATA_ID, id);
    	intent.putExtra(DATA_TITLE, title);
    	return intent;
    }
    
    /**
     * Factory method that makes the explicit intent another Activity uses to
     *  call this Service for delete a selfie.
     * 
     * @param context
     * @param id
     * @param title
     * @return
     */
    public static Intent makeDeleteIntent(Context context,
    		long id) {
    	Intent intent = new Intent(context, 
    						GhostMySelfieService.class);
    	intent.putExtra(DATA_OP, ACTION_DELETE);
    	intent.putExtra(DATA_ID, id);
    	return intent;
    }    
       
    /**
     * Factory method that makes the explicit intent another Activity uses to
     *  call this Service for rating a selfie.
     * 
     * @param context
     * @param id
     * @param vote
     * @return
     */
    public static Intent makeRateIntent(Context context,
										long id, long vote) {
    	Intent intent = new Intent(context, 
    						GhostMySelfieService.class);
    	intent.putExtra(DATA_OP, ACTION_RATE);
    	intent.putExtra(DATA_ID, id);
    	intent.putExtra(DATA_VOTE, vote);
    	return intent;
    }

    /**
     * Hook method that is invoked on the worker thread with a request
     * to process. Only one Intent is processed at a time, but the
     * processing happens on a worker thread that runs independently
     * from other application logic.
     * 
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
    	String operation = "";
    	String title = "";
    	long id = 0;
    	long vote = 0;
    	double rating = 0;
    	try {
    		operation = intent.getStringExtra(DATA_OP);	
    		Log.d("VS.oHI", "operation: " +  operation);
    	}
    	catch (Exception e) {
    		Log.d("VS.oHI", "No operation found ");
    	}
    	
        switch (operation) {
        case ACTION_UPLOAD:
        	// Starts the Notification to show the progress of selfie
            // upload.
            startNotification(true, intent.getIntExtra(NOTIFICATION_ID,0));
            
            // Create GhostMySelfieDataMediator that will mediate the communication
            // between Server and Android Storage.
            if (null == mGhostMySelfieMediator)
            	mGhostMySelfieMediator = new GhostMySelfieDataMediator(); 

            // Check if GhostMySelfie Upload is successful.
            finishNotification(mGhostMySelfieMediator.uploadGhostMySelfie(getApplicationContext(),
                                                       intent.getData()), true, intent.getIntExtra(NOTIFICATION_ID,0));

            sendOperationBroadcast(intent.getStringExtra(DATA_OP), 0D, "");
                 
        	break;
        	
        case ACTION_DELETE:
        	// Starts the Notification to show the progress of selfie
            // upload.
            startNotification(false, NOTIFICATION_DEFAULT_ID);

            // Create GhostMySelfieDataMediator that will mediate the communication
            // between Server and Android Storage.
            if (null == mGhostMySelfieMediator)
            	mGhostMySelfieMediator = new GhostMySelfieDataMediator(); 

            // Check if GhostMySelfie Download is successful.
    		id = intent.getLongExtra(DATA_ID, 0);	
            Log.d("VDM.oHI", "Id = " + id);
            String res = mGhostMySelfieMediator.deleteGhostMySelfie(getApplicationContext(), id);
            String notificationDeleteText = res;         
            finishNotification(notificationDeleteText, false, NOTIFICATION_DEFAULT_ID);
            // Send the Broadcast to GhostMySelfieListActivity that the GhostMySelfie
            // Download is completed.
            sendOperationBroadcast(intent.getStringExtra(DATA_OP), 0, "");

            break;        	
        	
        case ACTION_DOWNLOAD:
        	// Starts the Notification to show the progress of selfie
            // upload.
            startNotification(false, NOTIFICATION_DEFAULT_ID);

            // Create GhostMySelfieDataMediator that will mediate the communication
            // between Server and Android Storage.
            if (null == mGhostMySelfieMediator)
            	mGhostMySelfieMediator = new GhostMySelfieDataMediator(); 

            // Check if GhostMySelfie Download is successful.
    		id = intent.getLongExtra(DATA_ID, 0);	
            Log.d("VDM.oHI", "Id = " + id);
    		title = intent.getStringExtra(DATA_TITLE);	
            Log.d("VDM.oHI", "Title = " + title);
            File file = mGhostMySelfieMediator.downloadGhostMySelfie(getApplicationContext(), id, title);
            String filePath = file.getAbsolutePath();
            Log.d("VDM.oHI", "Path = " + filePath);
            intent.putExtra("Path", filePath);
            finishNotification(mGhostMySelfieMediator.STATUS_DOWNLOAD_SUCCESSFUL, false, NOTIFICATION_DEFAULT_ID);

            // Send the Broadcast to GhostMySelfieListActivity that the GhostMySelfie
            // Download is completed.
            sendOperationBroadcast(intent.getStringExtra(DATA_OP), 0, filePath);

            break;
        	
        case ACTION_RATE:
        	try {
	            // Create GhostMySelfieDataMediator that will mediate the communication
	            // between Server and Android Storage.
	            if (null == mGhostMySelfieMediator)
	            	mGhostMySelfieMediator = new GhostMySelfieDataMediator(); 
	
	            // Check if GhostMySelfie Rating is successful.
	    		id = intent.getLongExtra(DATA_ID, 0);	
	    		vote = intent.getLongExtra(DATA_VOTE, 0L);	
	        	AverageGhostMySelfieRating avr = mGhostMySelfieMediator.rateGhostMySelfie(id, vote);
	            rating = avr.getRating();
	            Log.d("VDM.oHI", "New rating = " + String.format("%2.1f", rating));
	
	            // Send the Broadcast to GhostMySelfieListActivity that the GhostMySelfie
	            // Rating is completed.
	            sendOperationBroadcast(intent.getStringExtra(DATA_OP), rating, "");
        	} catch (NullPointerException e) {}
        	break;
        default:
        }
        
    }
    
    /**
     * Send the Broadcast to Activity that the GhostMySelfie Upload is
     * completed.
     */
    private void sendOperationBroadcast(String operation, double rating, String filePath) {
        // Use a LocalBroadcastManager to restrict the scope of this
        // Intent to the SelfieUploadClient application.
        Intent intent = new Intent(ACTION_UPLOAD_SERVICE_RESPONSE)
        	.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(DATA_OP, operation);
        
        // inject extra data based on the operation
        if (operation.equals(ACTION_RATE))
        	intent.putExtra(DATA_RATING, rating);
        else if (operation.equals(ACTION_DOWNLOAD))
        	intent.putExtra(DATA_PATH, filePath);

        
    	LocalBroadcastManager.getInstance(this)
             .sendBroadcast(intent);
    }
    
    /**
     * Finish the Notification after the GhostMySelfie is Uploaded.
     * 
     * @param status
     */
    private void finishNotification(String status, boolean upload, int intentId) {
        // When the loop is finished, updates the notification.
        mBuilder.setContentTitle(status)
                // Removes the progress bar.
                .setProgress (0,
                              0,
                              false)
                .setSmallIcon(upload ? android.R.drawable.stat_sys_upload_done : android.R.drawable.stat_sys_download_done)
                .setContentText("") 
                .setTicker(status);

        // Build the Notification with the given
        // Notification Id.
        mNotifyManager.notify(intentId,
                              mBuilder.build());
    }
    
    /**
     * Starts the Notification to show the progress of selfie upload.
     */
    private void startNotification(boolean upload, int intentId) {
        // Gets access to the Android Notification Service.
        mNotifyManager = (NotificationManager)
            getSystemService(Context.NOTIFICATION_SERVICE); 

        // Create the Notification and set a progress indicator for an
        // operation of indeterminate length.
        mBuilder = new NotificationCompat
                       .Builder(this)
                       .setContentTitle(upload ? "GhostMySelfie Upload" : "GhostMySelfie Download") 
                       .setContentText(upload ? "Upload in progress" : "Download in progress") 
                       .setSmallIcon(upload ? android.R.drawable.stat_sys_upload : android.R.drawable.stat_sys_download)
                       .setTicker(upload ? "Uploading selfie" : "Downloading selfie")
                       .setProgress(0,
                                    0,
                                    true);
 
        // Build and issue the notification.
        mNotifyManager.notify(intentId,
                              mBuilder.build());
    }
}
