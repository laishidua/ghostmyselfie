package com.laishidua.presenter;

import java.io.File;
import java.lang.ref.WeakReference;
import com.laishidua.common.ConfigurableOps;
import com.laishidua.common.ContextView;
import com.laishidua.model.GhostMySelfieContract;
import com.laishidua.model.mediator.GhostMySelfieDataMediator;
import com.laishidua.model.services.GhostMySelfieService;
import com.laishidua.utils.Settings;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

/**
 * Provides all the GhostMySelfie-related operations.  It implements
 * ConfigurableOps so it can be created/managed by the GenericActivity
 * framework.  It extends GenericAsyncTaskOps so its doInBackground()
 * method runs in a background task.  It plays the role of the
 * "Abstraction" in Bridge pattern and the role of the "Presenter" in
 * the Model-View-Presenter pattern.
 */
public class SettingsOps
       implements ConfigurableOps<SettingsOps.View> {
    /**
     * Debugging tag used by the Android logger.
     */
    private static final String TAG =
        SettingsOps.class.getSimpleName();
    
    private SettingsOpsContentResolver mOptionsOpsContentResolver;
    
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
    private WeakReference<SettingsOps.View> mGhostMySelfieView;
    
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
    public SettingsOps() {

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
                
                mOptionsOpsContentResolver = new SettingsOpsContentResolver();

                mOptionsOpsContentResolver.onConfiguration(view,
                        firstTimeIn);
            }
		
	}


    /**
     * Show the Selfie
     * 
     * @param filePath : the absolute selfie file path
     */
    public void showGhostMySelfie(String filePath) {
		// plays the video
    	Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(filePath)), "image/*");
		mGhostMySelfieView.get().getActivityContext().startActivity(intent);
    	
    }

    public Settings getSettingsFromDB(){
    	Settings settings = new Settings();
		try (Cursor c = mOptionsOpsContentResolver.query(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_SETTINGS,
				GhostMySelfieContract.GhostMySelfieEntry.sSettingsColumnsToDisplay,
				GhostMySelfieContract.GhostMySelfieEntry._ID,
				new String[] { "1" }, null);) {
			if (c.moveToFirst()) {
                   if(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_GHOST)).contains("1")){
                	   settings.setGhost(true);
                   }
                   if(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_GRAY)).contains("1")){
                	   settings.setGray(true);
                   }  
                   if(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_BLUR)).contains("1")){
                	   settings.setBlur(true);
                   } 
                   if(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_DARK)).contains("1")){
                	   settings.setDark(true);
                   } 
                   if(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_ACTIVATE_REMINDER)).contains("1")){
                	   settings.setReminder(true);
                   } 
                   settings.setReminderHour(
                		   c.getInt(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_HOUR))
                		   );
                   settings.setReminderMinute(
                		   c.getInt(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_MINUTE))
                		   );                   
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return settings;
    }
    
    public int checkSetting (String column, boolean bol) throws RemoteException {
    	String set = "0";
    	if(bol) {
    		set = "1";
    	}
    	ContentValues values = new ContentValues();
    	values.put(column , set);
    	int res = mOptionsOpsContentResolver.update(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_SETTINGS,
    			values,
    			GhostMySelfieContract.GhostMySelfieEntry._ID, new String[]{ "1" });
        return res;
    }
    
    public int setReminderTime(int hour, int minute) throws RemoteException {
    	ContentValues values = new ContentValues();
    	values.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_HOUR , hour);
    	values.put(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_REMINDER_MINUTE , minute);
    	int res = mOptionsOpsContentResolver.update(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_SETTINGS,
    			values,
    			GhostMySelfieContract.GhostMySelfieEntry._ID, new String[]{ "1" });
        return res;
    }    

    
}
