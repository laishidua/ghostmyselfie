package com.laishidua.view;

import com.laishidua.common.GenericActivity;
import com.laishidua.model.services.GhostMySelfieService;
import com.laishidua.presenter.GhostMySelfieDetailOps;
import com.laishidua.R;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;
import com.laishidua.view.ui.FloatingGenericButton;

/**
 * This Activity is used to show the detail of each selected selfie
 */
public class GhostMySelfieDetailActivity 
       extends GenericActivity<GhostMySelfieDetailOps.View, GhostMySelfieDetailOps>
       implements GhostMySelfieDetailOps.View {

    /**
     * The Broadcast Receiver that registers itself to receive the
     * result from GhostMySelfieService when a selfie upload completes.
     */
    private UploadResultReceiver mUploadResultReceiver;
    
    private TextView mTextData;
    
    private RatingBar mRatingBar;

    private ImageView mThumbnail;
    
    private FloatingGenericButton mDeleteRowItem;

    private long mId;
    
    private String mTitle;

    private String mFilePath;

    private static class GhostMySelfieQueryHandler extends AsyncQueryHandler {

		public GhostMySelfieQueryHandler(ContentResolver cr) {
			super(cr);
		}
    	
    }
    
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
        setContentView(R.layout.ghostmyselfie_detail_activity);

        // Receiver for the notification.
        mUploadResultReceiver =
            new UploadResultReceiver();
        
        mTextData =
        	(TextView) findViewById(R.id.tvDetailTitle);
        
        mDeleteRowItem =
        		(FloatingGenericButton) findViewById(R.id.deleteRowItem);

        mRatingBar =
            	(RatingBar) findViewById(R.id.rbDetailRatingBar);

        mThumbnail =
            	(ImageView) findViewById(R.id.ivThumbnail);

        mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				// cast vote to the server
				if (fromUser) {
					getOps().rateGhostMySelfie(mId, Math.round(rating + 0.5));
				}
				
			}
		});


        mThumbnail.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (mFilePath != null) {
					getOps().showGhostMySelfie(mFilePath);
				}
				else {
					getOps().downloadGhostMySelfie(mId, mTitle);
				}

			}
		});
        
        mDeleteRowItem.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
					getOps().deleteGhostMySelfie(mId, mTitle);
			}
		});
        
        Bundle data = getIntent().getExtras();
        mId = data.getLong("Id");
        mTitle = data.getString("Title");
		Log.d(TAG,"Title: " + mTitle);
        mTextData.setText(setInfo(mTitle, data.getDouble("AverageVote")));
        mRatingBar.setRating((float) data.getDouble("AverageVote"));
        
        // Invoke the special onCreate() method in GenericActivity,
        // passing in the GhostMySelfieDetailOps class to instantiate/manage and
        // "this" to provide GhostMySelfieDetailOps with the GhostMySelfieDetailOps.View instance.
        super.onCreate(savedInstanceState,
                       GhostMySelfieDetailOps.class,
                       this);

        // Query mediastore for title and set thumbnail and mFilePath if found
        // TODO set this in Ops.
        GhostMySelfieQueryHandler mGhostMySelfieQueryHandler = new GhostMySelfieQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
              if (cursor.moveToNext()) {
            	  Log.d(TAG, "GhostMySelfie found locally: ");
            	  Log.d(TAG, "_ID: " + cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
            	  Log.d(TAG, "DATA: " + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            	  Log.d(TAG, "DISPLAY_NAME: " + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            	  
            	  mFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
  				  mThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mFilePath), 500, 300));
              }
              else {
            	  Log.d(TAG, "No image found locally");
              }
            }
       
        };
        Log.d(TAG,"Querying title: " + mTitle);
        mGhostMySelfieQueryHandler.startQuery(0, null, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        		new String[] { MediaStore.Images.Media._ID, 
        					   MediaStore.Images.Media.DATA, 
        					   MediaStore.Images.Media.DISPLAY_NAME },
        		MediaStore.Images.Media.DISPLAY_NAME + " = ? ", new String[] { mTitle }, null);
        

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
    }
    
    /**
     * Register a BroadcastReceiver that receives a result from the
     * GhostMySelfieService when a selfie download completes.
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

    private String setInfo(String title, double rating) {
    	return title + "\n\n  Star Rating: "
        		+ String.format("%2.1f", rating);
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
			Log.d(TAG, "Received something...");
            // Check which kind of result we've got
            // and act accordingly
			String operation = "";
    		try {
    			operation = intent.getStringExtra("Operation");
    			Log.d(TAG, operation);
    		}
    		catch (Exception e) {
    			Log.d(TAG, "No Operation Extra found in intent");
    		}
    		
    		if (operation.equals(GhostMySelfieService.ACTION_RATE)) {
    			// display updated data
    			double rating = intent.getDoubleExtra(GhostMySelfieService.DATA_RATING, 0D);
    			Log.d(TAG, "Id = " + String.format("%2d", intent.getLongExtra(GhostMySelfieService.DATA_ID, 0L)));
    			Log.d(TAG, "Rating = " + String.format("%2.1f", rating));
    	        mTextData.setText(setInfo(mTitle, rating));
    			mRatingBar.setRating((float) rating);
    		}
    		else if (operation.equals(GhostMySelfieService.ACTION_DOWNLOAD)) {
    			mFilePath = intent.getStringExtra(GhostMySelfieService.DATA_PATH);
    			Log.d(TAG, "Path = " + mFilePath);
				mThumbnail.setImageBitmap(
						ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mFilePath), 500, 300));
    		}
        }
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
     * Hook method called whenever an item in your options menu is
     * selected
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
			startActivity(intent);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
