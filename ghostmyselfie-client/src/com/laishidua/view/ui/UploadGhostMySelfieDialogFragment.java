package com.laishidua.view.ui;

import com.laishidua.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * UploadGhostMySelfieDialog Fragment shows user a Dialog that lists various
 * means of uploading a GhostMySelfie.
 */
public class UploadGhostMySelfieDialogFragment extends DialogFragment {
    /**
     * The various means of uploading a GhostMySelfie.
     */
    public enum OperationType {
        /**
         * Position of GhostMySelfie Gallery Option in List.
         */
        GHOSTMYSELFIE_GALLERY,

        /**
         * Position of Record GhostMySelfie Option in List.
         */
        SHOT_GHOSTMYSELFIE
    };

    /**
     * Array to hold List items.
     */
    private String[] listItems = { 
        "Selfie Gallery",
        "Take a Selfie" 
    };

    /**
     * Callback that will send the result to Activity that implements
     * it, when the Option is selected.
     */
    private OnGhostMySelfieSelectedListener mListener;

    /**
     * Container Activity must implement this interface
     */
    public interface OnGhostMySelfieSelectedListener {
        public void onGhostMySelfieSelected(OperationType which);
    }

    /**
     * Hook method called when a fragment is first attached to its
     * activity. onCreate(Bundle) will be called after this.
     * 
     * @param activity
     */
    @Override
        public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener =
                (OnGhostMySelfieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException
                (activity.toString()
                 + " must implement OnGhostMySelfieSelectedListener");
        }
    }

    /**
     * This method will be called after onCreate(Bundle) and before
     * onCreateView(LayoutInflater, ViewGroup, Bundle).  The default
     * implementation simply instantiates and returns a Dialog class.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Builder for creating a new Dialog.
        AlertDialog.Builder builder = 
            new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_upload_selfie)
               .setItems(listItems,
                         //Set OnClick listener for the Dialog.
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog,
                                                 int which) {
                                 UploadGhostMySelfieDialogFragment.OperationType type =
                                     UploadGhostMySelfieDialogFragment.OperationType
                                                              .values()[which];
                                 // Select the means of uploading a selfie.
                                 mListener.onGhostMySelfieSelected(type);
                             }
                         });

        // Use the Builder pattern to create the Dialog.
        return builder.create();
    }
}
