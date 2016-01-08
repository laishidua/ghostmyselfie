package com.laishidua.view;

import com.laishidua.common.GenericActivity;
import com.laishidua.common.Utils;
import com.laishidua.model.mediator.webdata.GhostMySelfie;
import com.laishidua.model.services.GhostMySelfieService;
import com.laishidua.presenter.GhostMySelfieOps;
import com.laishidua.utils.GhostMySelfieStorageUtils;
import com.laishidua.view.ui.FloatingGenericButton;
import com.laishidua.view.ui.GhostMySelfieAdapter;
import com.laishidua.view.ui.UploadGhostMySelfieDialogFragment;

import com.laishidua.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * This Activity can be used upload a selected selfie to a GhostMySelfie
 * Service and also displays a list of selfies available at the GhostMySelfie
 * Service.  The user can record a selfie or get a selfie from gallery
 * and upload it.  It implements OnGhostMySelfieSelectedListener that will
 * handle callbacks from the UploadGhostMySelfieDialog Fragment.  It extends
 * GenericActivity that provides a framework for automatically
 * handling runtime configuration changes of an GhostMySelfieOps object, which
 * plays the role of the "Presenter" in the MVP pattern.  The
 * GhostMySelfieOps.View interface is used to minimize dependencies between
 * the View and Presenter layers.
 */
public class GhostMySelfieListActivity 
       extends GenericActivity<GhostMySelfieOps.View, GhostMySelfieOps>
       implements UploadGhostMySelfieDialogFragment.OnGhostMySelfieSelectedListener,
       				GhostMySelfieOps.View {
    /**
     * The Request Code needed in Implicit Intent start GhostMySelfie
     * Recording Activity.
     */
    private final int REQUEST_GHOSTMYSELFIE_CAPTURE = 0;

    /**
     * The Request Code needed in Implicit Intent to get GhostMySelfie from
     * Gallery.
     */
    private final int REQUEST_GET_GHOSTMYSELFIE = 1;

    private final int REQUEST_SETTINGS = 2;

    private final int REQUEST_LOGIN = 3;

    /**
     * The Broadcast Receiver that registers itself to receive the
     * result from GhostMySelfieService when a selfie upload completes.
     */
    private UploadResultReceiver mUploadResultReceiver;

    /**
     * The Floating Action Button that will show a Dialog Fragment to
     * upload GhostMySelfie when user clicks on it.
     */
    private FloatingGenericButton mUploadGhostMySelfieButton;
    private FloatingGenericButton mModifierButton;
    
    /**
     * The ListView that contains a list of Videos available from
     * the GhostMySelfie Service.
     */
    private ListView mGhostMySelfiesList;

    /**
     * Used to display the results of contacts queried from the
     * VideoContentProvider.
     */
    private SimpleCursorAdapter mAdapter;

    /**
     * Uri to store the Recorded GhostMySelfie.
     */
    private Uri mRecordGhostMySelfieUri;
    
    private GhostMySelfie selectedGhostMySelfie;
    
    private static Context mContext;
    
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., storing Views.
     * 
     * @param Bundle
     *            object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the default layout.
        setContentView(R.layout.ghostmyselfie_list_activity);

        // Receiver for the notification.
        mUploadResultReceiver =
            new UploadResultReceiver();
        
        // Get reference to the ListView for displaying the results
        // entered.
        mGhostMySelfiesList =
            (ListView) findViewById(R.id.ghostmyselfieList);

        // Get reference to the Floating Action Button.
        mUploadGhostMySelfieButton =
                    (FloatingGenericButton) findViewById(R.id.fabButton);
        
        // Show the UploadGhostMySelfieDialog Fragment when user clicks the
        // button.
        mUploadGhostMySelfieButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new UploadGhostMySelfieDialogFragment()
                         .show(getFragmentManager(),
                               "uploadGhostMySelfie");
                }
            }); 
        
        // Get reference to the Floating Action Button.
        mModifierButton =
                    (FloatingGenericButton) findViewById(R.id.fabModifierButton);        
  
        mModifierButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	GhostMySelfieAdapter mSelfieAdapter = getOps().getAdapter();
            	if(mSelfieAdapter.getModifierCheckedBoxList().size() == 0){
            		Utils.showToast(GhostMySelfieListActivity.this,
                            "Check at least one Selfie to modify. Go to settings menu to change values.");
            		return;
            	}
        		Utils.showToast(GhostMySelfieListActivity.this,
                        "Modifying Selfies.");            	
            	int i = 1;
        		for (String modifiersChecked : mSelfieAdapter.getModifierCheckedBoxList()) {
                	Uri guf = Uri.fromFile(GhostMySelfieStorageUtils.getGhostMySelfieStorageDir(modifiersChecked));
                	getOps().uploadGhostMySelfie(guf, i);
                	i++;
        		}            	
            }
        });         
        
        mContext = getActivityContext();

        // Invoke the special onCreate() method in GenericActivity,
        // passing in the GhostMySelfieOps class to instantiate/manage and
        // "this" to provide GhostMySelfieOps with the GhostMySelfieOps.View instance.
        super.onCreate(savedInstanceState,
                       GhostMySelfieOps.class,
                       this);

        // Initialize the SimpleCursorAdapter.
        mAdapter = getOps().makeCursorAdapter();
        
        mGhostMySelfiesList.setAdapter(mAdapter);
        
        getOps().onConfiguration(this, true);       

    }

    /**
     *  Hook method that is called when user resumes activity
     *  from paused state, onPause(). 
     */
    @Override
    protected void onResume() {
        // Call up to the superclass.
        super.onResume();

        // Register BroadcastReceiver that receives result from
        // GhostMySelfieService when a selfie upload completes.
        registerReceiver();
        getOps().getGhostMySelfieList();
    }
    
    /**
     * Register a BroadcastReceiver that receives a result from the
     * GhostMySelfieService when a selfie upload completes.
     */
    private void registerReceiver() {
        
        // Create an Intent filter that handles Intents from the
        // GhostMySelfieService.
        IntentFilter intentFilter =
            new IntentFilter(GhostMySelfieService.ACTION_UPLOAD_SERVICE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
               .registerReceiver(mUploadResultReceiver,
                                 intentFilter);
    }

    
    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads. onDestroy() may not always be called-when
     * system kills hosting process
     */
    @Override
    protected void onPause() {
        // Call onPause() in superclass.
        super.onPause();
        
        // Unregister BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
          .unregisterReceiver(mUploadResultReceiver);
    }

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from GhostMySelfieService.
     */
    private class UploadResultReceiver 
            extends BroadcastReceiver {
        /**
         * Hook method that's dispatched when the UploadService has
         * uploaded the GhostMySelfie.
         */
        @Override
        public void onReceive(Context context,
                              Intent intent) {
            // Starts an AsyncTask to get a fresh GhostMySelfie list from the
            // database.
    		try {
    			Log.d(TAG, intent.getStringExtra("Operation"));
    		}
    		catch (Exception e) {
    			Log.d(TAG, "No Operation Extra found in intent");
    			
    		}
    		
    		if (intent.getStringExtra("Operation").equals(GhostMySelfieService.ACTION_UPLOAD))
    			getOps().getGhostMySelfieList();
        }
    }

    /**
     * The user selected option to get Selfie from UploadGhostMySelfieDialogFragment.
     * Based on what the user selects either to take a Selfie or get a Selfie from the ImageGallery
     */
    @Override
    public void onGhostMySelfieSelected(UploadGhostMySelfieDialogFragment.OperationType which) {
        switch (which) {
        case GHOSTMYSELFIE_GALLERY:
            // Create an intent that will start an Activity to get
            // GhostMySelfie from Gallery.
            final Intent ghostmyselfieGalleryIntent = 
                new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .putExtra(Intent.EXTRA_LOCAL_ONLY,
                          true);

            // Verify the intent will resolve to an Activity.
            if (ghostmyselfieGalleryIntent.resolveActivity(getPackageManager()) != null) 
                // Start an Activity to get the GhostMySelfie from GhostMySelfie
                // Gallery.
                startActivityForResult(ghostmyselfieGalleryIntent,
                                       REQUEST_GET_GHOSTMYSELFIE);
            break;
            
        case SHOT_GHOSTMYSELFIE:
            // Create a file to save the video.
            mRecordGhostMySelfieUri =
                GhostMySelfieStorageUtils.getRecordedGhostMySelfieUri
                                   (getApplicationContext());  
            
            // Create an intent that will start an Activity to
            // take a Selfie.
            final Intent recordGhostMySelfieIntent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT,
                          mRecordGhostMySelfieUri);

            // Verify the intent will resolve to an Activity.
            if (recordGhostMySelfieIntent.resolveActivity(getPackageManager()) != null) 
                // Start an Activity to take a Selfie.
                startActivityForResult(recordGhostMySelfieIntent,
                                       REQUEST_GHOSTMYSELFIE_CAPTURE);
            break;
        }
    }

    /**
     * Hook method called when an activity you launched exits, giving
     * you the requestCode you started it with, the resultCode it
     * returned, and any additional data from it.
     * 
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {
        Uri ghostmyselfieUri = null; 

        // Check if the Result is Ok and upload the GhostMySelfie to the GhostMySelfie
        // Service.
        if (resultCode == Activity.RESULT_OK) {
            
        	if (requestCode == REQUEST_SETTINGS) {
        		getOps().getGhostMySelfieList();
        	}

        	// GhostMySelfie picked from the Gallery.
            if (requestCode == REQUEST_GET_GHOSTMYSELFIE)
                ghostmyselfieUri = data.getData();
                
            // GhostMySelfie is recorded.
            else if (requestCode == REQUEST_GHOSTMYSELFIE_CAPTURE)
                ghostmyselfieUri = mRecordGhostMySelfieUri;
              
            if (ghostmyselfieUri != null){
                
            	// notify media scanners about the new GhostMySelfie
            	mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, ghostmyselfieUri));
            	
            	Utils.showToast(this,
                                "Uploading Selfie"); 
            
                // Upload the GhostMySelfie.
                getOps().uploadGhostMySelfie(ghostmyselfieUri, GhostMySelfieService.NOTIFICATION_DEFAULT_ID);
            }
        }

        // Pop a toast if we couldn't get a selfie to upload.
        if ((ghostmyselfieUri == null) && (requestCode != REQUEST_SETTINGS))
            Utils.showToast(this,
                            "Could not get Selfie to upload");
    }


    /**
     * Sets the Adapter that contains List of Videos to the ListView.
     */
    @Override
    public void setAdapter(GhostMySelfieAdapter ghostMySelfieAdapter) {
        mGhostMySelfiesList.setAdapter(ghostMySelfieAdapter);
        
        final GhostMySelfieAdapter mSelfieAdapter = ghostMySelfieAdapter;
        
        mGhostMySelfiesList.setOnItemClickListener(new AdapterView.OnItemClickListener () {

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view,
    				int position, long id) {
    			selectedGhostMySelfie = mSelfieAdapter.getItem(position);
    			Intent intent = new Intent(mContext, GhostMySelfieDetailActivity.class);
    			Log.d(TAG,"id: " + selectedGhostMySelfie.getId());
    			intent.putExtra("Id", selectedGhostMySelfie.getServerId());
    			intent.putExtra("Title", selectedGhostMySelfie.getTitle());
    			intent.putExtra("AverageVote", selectedGhostMySelfie.getAverageVote());
    			intent.putExtra("ContentType", selectedGhostMySelfie.getContentType());
    			startActivity(intent);
    		}
        	
        });
    }

    /**
     * Finishes this Activity.
     */
    @Override
    public void finish() {
        super.finish();
    }
    
    /**
     * Hook method called to initialize the contents of the Activity's
     * standard options menu.
     * 
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it
        // is present.
    	super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ghostmyselfie_list,
                                  menu);
        return true;
    }

    /**
     * Hook method called whenever a settings button (config button) is selected in action bar.
     * 
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
    		//Log.d(TAG, AppSettings.SERVER_URL);
        	Intent intent = new Intent(this.getApplicationContext(), SettingsActivity.class);
			startActivityForResult(intent, REQUEST_SETTINGS);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void displayCursor(Cursor cursor) {
        mAdapter.changeCursor(cursor);
	}
		
}
