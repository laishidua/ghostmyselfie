package com.laishidua.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import retrofit.client.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

/**
 * Helper class that contains methods to store and get GhostMySelfie 
 * from Android Local Storage.
 */
public class GhostMySelfieStorageUtils {
    /** 
     * Create a file Uri for saving a recorded selfie
     */ 
    @SuppressLint("SimpleDateFormat")
    public static Uri getRecordedGhostMySelfieUri(Context context) {

        // Check to see if external SDCard is mounted or not.
        if (isExternalStorageWritable()) {
            // Create a path where we will place our recorded selfie in
            // the user's public DCIM directory. Note that you should
            // be careful about what you place here, since the user
            // often manages these files.
            final File ghostmyselfieStorageDir =
                Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM);

            // Create the storage directory if it does not exist
            if (!ghostmyselfieStorageDir.exists()) {
                if (!ghostmyselfieStorageDir.mkdirs()) {
                    return null;
                }
            }

            // Create a TimeStamp for the selfie file.
            final String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            
            // Create a selfie file name from the TimeStamp.
            final File ghostmyselfieFile =
                new File(ghostmyselfieStorageDir.getPath() + File.separator + "IMG_"
                         + timeStamp + ".jpg");

            // Always notify the MediaScanners after storing
            // the GhostMySelfie, so that it is immediately available to
            // the user.
            notifyMediaScanners(context, ghostmyselfieFile);
            
            //Return Uri from GhostMySelfie file.
            return Uri.fromFile(ghostmyselfieFile);

        } else 
            //Return null if no SDCard is mounted.
            return null;
    } 
    
    /**
     * Stores the GhostMySelfie in External Downloads directory in Android.
     */
    public static File storeGhostMySelfieInExternalDirectory(Context context,
                                                     Response response,
                                                     String ghostmyselfieName) {
        // Try to get the File from the Directory where the GhostMySelfie
        // is to be stored.
        final File file =
            getGhostMySelfieStorageDir(ghostmyselfieName);
        if (file != null) {
            try {
                // Get the InputStream from the Response.
                final InputStream inputStream =
                    response.getBody().in();
                
                // Get the OutputStream to the file
                // where GhostMySelfie data is to be written.
                final OutputStream outputStream =
                    new FileOutputStream(file);
                
                // Write the GhostMySelfie data to the File.
                IOUtils.copy(inputStream,
                             outputStream);
                
                // Close the streams to free the Resources used by the
                // stream.
                outputStream.close();
                inputStream.close();

                // Always notify the MediaScanners after Downloading
                // the GhostMySelfie, so that it is immediately available to
                // the user.
                notifyMediaScanners(context,
                                    file);
                return file;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    /**
     * Notifies the MediaScanners after Downloading the GhostMySelfie, so it
     * is immediately available to the user.
     */
    public static void notifyMediaScanners(Context context,
                                            File ghostmyselfieFile) {
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile
            (context,
             new String[] { ghostmyselfieFile.toString() },
             null,
             new MediaScannerConnection.OnScanCompletedListener() {
                 public void onScanCompleted(String path, 
                                             Uri uri) {
                 }
             });
    }

    /**
     * Checks if external storage is available for read and write.
     * 
     * @return True-If the external storage is writable.
     */
    private static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals
            (Environment.getExternalStorageState());
    }

    /**
     * Get the External Downloads Directory to 
     * store the Selfies.
     * 
     * @param ghostmyselfieName
     */
    public static File getGhostMySelfieStorageDir(String ghostmyselfieName) {
        //Check to see if external SDCard is mounted or not.
        if (isExternalStorageWritable()) {
            // Create a path where we will place our selfie in the
            // user's public Downloads directory. Note that you should be
            // careful about what you place here, since the user often 
            // manages these files.
            final File path =
                Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS);
            final File file = new File(path,
                                 ghostmyselfieName);
            // Make sure the Downloads directory exists.
            path.mkdirs();
            return file;
        } else {
            return null;
        }
    }

    /**
     * Make GhostMySelfieStorageUtils a utility class by preventing instantiation.
     */
    private GhostMySelfieStorageUtils() {
        throw new AssertionError();
    }
}
