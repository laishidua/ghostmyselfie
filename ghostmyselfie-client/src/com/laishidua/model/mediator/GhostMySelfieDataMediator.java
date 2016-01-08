package com.laishidua.model.mediator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.laishidua.model.GhostMySelfieContract;
import com.laishidua.model.mediator.webdata.AverageGhostMySelfieRating;
import com.laishidua.model.mediator.webdata.GhostMySelfie;
import com.laishidua.model.mediator.webdata.GhostMySelfieServiceProxy;
import com.laishidua.model.mediator.webdata.GhostMySelfieStatus;
import com.laishidua.model.mediator.webdata.SecuredRestBuilder;
import com.laishidua.model.mediator.webdata.UnsafeHttpsClient;
import com.laishidua.model.mediator.webdata.GhostMySelfieStatus.GhostMySelfieState;
import com.laishidua.model.mediator.webdata.User;
import com.laishidua.model.mediator.webdata.UserCredentialsStatus;
import com.laishidua.utils.Constants;
import com.laishidua.utils.GhostMySelfieMediaStoreUtils;
import com.laishidua.utils.GhostMySelfieStorageUtils;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import com.laishidua.R;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Mediates communication between the GhostMySelfie Service and the local
 * storage on the Android device.  The methods in this class block, so
 * they should be called from a background thread (e.g., via an
 * AsyncTask).
 */
public class GhostMySelfieDataMediator {
    /**
     * Status code to indicate that file is successfully
     * uploaded.
     */
    public static final String STATUS_UPLOAD_SUCCESSFUL =
        "Retrieved Modified Image Succeeded !!";
    
    /**
     * Status code to indicate that file upload failed 
     * due to large selfie size.
     */
    public static final String STATUS_UPLOAD_ERROR_FILE_TOO_LARGE =
        "Upload failed: File too big";
    
    /**
     * Status code to indicate that file upload failed.
     */
    public static final String STATUS_UPLOAD_ERROR =
        "Upload failed";
    
    /**
     * Status code to indicate that file is successfully
     * uploaded.
     */
    public static final String STATUS_DOWNLOAD_SUCCESSFUL =
        "Download succeeded";
    
    /**
     * Status code to indicate that file upload failed.
     */
    public static final String STATUS_DOWNLOAD_ERROR =
        "Download failed";
    
    /**
     * Defines methods that communicate with the GhostMySelfie Service.
     */
    private GhostMySelfieServiceProxy mGhostMySelfieServiceProxy;
    
    /**
     * Constructor that initializes the GhostMySelfieDataMediator.
     * 
     * @param context
     */

    class MyErrorHandler implements ErrorHandler {
    	  @Override public Throwable handleError(RetrofitError cause) {
    		  Log.d("VDM.MEH",cause.getLocalizedMessage());
    		  if (cause.getLocalizedMessage().contains("401")
    				  || cause.getLocalizedMessage().contains("400")) {
    			  return new Exception("Invalid credentials.");
        		  //return null;
    		  }
    		  else
    			  return cause;
    		  }

    }
    
    private MyErrorHandler mErrorHandler = new MyErrorHandler();
    
    public GhostMySelfieDataMediator() {
        // Initialize the GhostMySelfieServiceProxy.
    	try {
    		Log.d("VDM.VDM","Initializing...");
	    	mGhostMySelfieServiceProxy = new SecuredRestBuilder()
	        .setLoginEndpoint(Constants.SERVER_URL
	            + GhostMySelfieServiceProxy.TOKEN_PATH)
	        .setUsername(Constants.user)
	        .setPassword(Constants.pass)
	        .setClientId("mobile")
	        .setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
	        .setEndpoint(Constants.SERVER_URL)
	        .setErrorHandler(mErrorHandler)
	        .setLogLevel(RestAdapter.LogLevel.FULL).build()
	        .create(GhostMySelfieServiceProxy.class);
  		  	Log.d("VDM.VDM","Initialized");
    	}
        catch (Exception e) {
  		  Log.d("VDM.VDM","Why don't we ever get here?");
        	
        }
    }

    /**
     * Uploads the GhostMySelfie having the given Uri.  This Uri is the Uri of
     * GhostMySelfie in Android GhostMySelfie Content Provider.
     * 
     * @param ghostmyselfieUri
     *            Uri of the GhostMySelfie to be uploaded.
     *
     * @return A String indicating the status of the selfie upload operation.
     */
    public String uploadGhostMySelfie(Context context,
                              Uri ghostmyselfieUri) {
        // Get the path of selfie file from ghostmyselfieUri.
        String filePath = GhostMySelfieMediaStoreUtils.getPath(context,
                                                       ghostmyselfieUri);
        Log.d("VDM.uV", "filePath: " + filePath);
        
        // Get the GhostMySelfie from Android GhostMySelfie Content Provider having
        // the given filePath.
        GhostMySelfie androidSelfie = 
            GhostMySelfieMediaStoreUtils.getGhostMySelfie(context,
                                          filePath);
        
        // Check if any such GhostMySelfie exists in Android GhostMySelfie Content
        // Provider.
        if (androidSelfie != null) {
            Log.d("VDM.uV", "Id: " + androidSelfie.getId());
            
            // Prepare to Upload the GhostMySelfie data.

            // Create an instance of the file to upload.
            File ghostmyselfieFile = new File(filePath);

            // Check if the file size is less than the size of the
            // selfie that can be uploaded to the server.
            if (ghostmyselfieFile.length() < Constants.MAX_SIZE_MEGA_BYTE) {

                try {
                    // Add the metadata of the GhostMySelfie to the GhostMySelfie Service
                    // and get the resulting GhostMySelfie that contains
                    // additional meta-data (e.g., Id and ContentType)
                    // generated by the GhostMySelfie Service.
                    Log.d("VDM.uV", "Id: " + androidSelfie.getId());
                    
            		List<String> filters =  new ArrayList<String>();
            		Cursor c = context.getContentResolver().query(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_SETTINGS,
            				GhostMySelfieContract.GhostMySelfieEntry.sSettingsColumnsToDisplay,
            				GhostMySelfieContract.GhostMySelfieEntry._ID,
            				new String[] { "1" }, null);
            		if (c.moveToFirst()) {
                               if(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_GHOST)).contains("1")){
                            	   filters.add("ghost");                               }
                               if(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_GRAY)).contains("1")){
                            	   filters.add("gray");
                               }  
                               if(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_BLUR)).contains("1")){
                            	   filters.add("blur");
                               } 
                               if(c.getString(c.getColumnIndex(GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_DARK)).contains("1")){
                            	   filters.add("dark");
                               }                    
            		}        		
            		androidSelfie.setFilters(filters);
            		
                    GhostMySelfie receivedSelfie =
                        mGhostMySelfieServiceProxy.addGhostMySelfie(androidSelfie);
                    	Log.d("VDM.uV", "Server Id: " + receivedSelfie.getId());

                    // Check if the Server returns any GhostMySelfie metadata.
                    if (receivedSelfie != null) {
                    	
                    	// Finally, upload the GhostMySelfie data to the server
                        // and get the status of the uploaded selfie data.
                        GhostMySelfieStatus status =
                            mGhostMySelfieServiceProxy.setGhostMySelfieData
                                (receivedSelfie.getId(),
                                 new TypedFile(receivedSelfie.getContentType(),
                                               ghostmyselfieFile));

                        // Check if the Status of the GhostMySelfie or not.
                        if (status.getState() == GhostMySelfieState.READY) {
                        	ghostmyselfieFile.delete();
                        	GhostMySelfieStorageUtils.notifyMediaScanners(context, ghostmyselfieFile);
                        	File file = downloadGhostMySelfie(context, 
                        			receivedSelfie.getId(), receivedSelfie.getTitle());
                        	
                        	if (file != null) {
                               // GhostMySelfie successfully uploaded.
                               return STATUS_UPLOAD_SUCCESSFUL;
                        	} else {
                        		return STATUS_UPLOAD_ERROR;
                        	}
                        }
                    }
                } catch (Exception e) {
                    // Error occured while uploading the selfie.
                    return STATUS_UPLOAD_ERROR;
                }
            } else
                // GhostMySelfie can't be uploaded due to large selfie size.
                return STATUS_UPLOAD_ERROR_FILE_TOO_LARGE;
        }
        
        // Error occured while uploading the selfie.
        return STATUS_UPLOAD_ERROR;
    }

    /**
     * Downloads the GhostMySelfie having the given Id.
     * 
     * @param Id
     *            Id of the GhostMySelfie to be downloaded.
     *            
     * @param title
     * 			  filename of the selfie.
     *     
     * @return A String indicating the status of the selfie download operation.
     */
    public File downloadGhostMySelfie(Context context,
            				long id, String title) {
    	
    	Response response = mGhostMySelfieServiceProxy.getGhostMySelfieData(id);
    	try {
	    	File file = GhostMySelfieStorageUtils.storeGhostMySelfieInExternalDirectory(context, response, title);
	    	return file;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
	    	return null;
    	}
    }
    
    /**
     * Delete the GhostMySelfie having the given Id from server.
     * 
     * @param Id
     *            Id of the GhostMySelfie to be downloaded.
     *            
     * @param title
     * 			  filename of the selfie.
     *     
     * @return A String indicating the status of the selfie download operation.
     */
    public String deleteGhostMySelfie(Context context,
            				long id) {
    	String deletedText = "";
    	try {	
    		Integer res = mGhostMySelfieServiceProxy.deleteGhostMySelfieById(id);
    		if (res == 1) {
        		int dLocal = context.getContentResolver().delete(GhostMySelfieContract.GhostMySelfieEntry.CONTENT_URI_GHOSTMYSELFIE,
        				GhostMySelfieContract.GhostMySelfieEntry.COLUMN_SERVER_ID,
        				new String[] { "" + id });  
        		if (dLocal == 0) { //Local error.
        			deletedText = context.getString(R.string.delete_item_text2);
        		} else { //Success.
    			    deletedText = context.getString(R.string.delete_item_text1);
        		}
	    	} else { //Cannot delete on server.
	    		deletedText = context.getString(R.string.delete_item_text2);
	    	}
    		return deletedText;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
	    	return context.getString(R.string.delete_item_text2);
    	}
    }    
    
    /**
     * Rates the GhostMySelfie having the given Id.
     * 
     * @param Id
     *            Id of the GhostMySelfie to be downloaded.
     *            
     * @param rating
     * 			  the user's vote for the selfie.
     *     
     * @return The updated selfie rating.
     */
    public AverageGhostMySelfieRating rateGhostMySelfie(long id, long rating) {

    	try {
    		AverageGhostMySelfieRating avr = mGhostMySelfieServiceProxy.rateGhostMySelfie(id, rating);
			return avr;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
	    	return null;
    	}
    }


    /**
     * Get the List of GhostMySelfies from GhostMySelfie Service.
     *
     * @return the List of GhostMySelfies from Server or null if there is
     *         failure in getting the Selfies.
     */
    public List<GhostMySelfie> getGhostMySelfieList() {
        try {
            return (ArrayList<GhostMySelfie>)
                        mGhostMySelfieServiceProxy.getGhostMySelfieList();
        } catch (Exception e) {
        	Log.d("VDM.gVL", e.getLocalizedMessage());
    		e.printStackTrace();
           return null; 
        }
    }
    
    public User addUser(User user){
        try {
        	return mGhostMySelfieServiceProxy.addUser(user);    
        } catch (Exception e) {
        	Log.d("SignUp Error", e.getLocalizedMessage());
    		e.printStackTrace();
           return null; 
        }   	
    }
    
    public UserCredentialsStatus checkCredentialsState(User user) {
        try {
        	UserCredentialsStatus ucs= 
        			mGhostMySelfieServiceProxy
        			.checkCredentialsState(user);
        	return ucs;     
        } catch (Exception e) {
        	Log.d("SignUp Error", e.getLocalizedMessage());
    		e.printStackTrace();
           return null; 
        }       	
    }
    
    public Integer isConnectionEstablished(){
        try {
        	Integer res= 
        			mGhostMySelfieServiceProxy
        			.conectionEstablished();
        	return res;     
        } catch (Exception e) {
        	Log.d("SignUp Error", e.getLocalizedMessage());
    		e.printStackTrace();
           return 0; 
        }      	
    }
    
}
