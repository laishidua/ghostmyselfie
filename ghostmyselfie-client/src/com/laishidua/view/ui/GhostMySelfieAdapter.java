package com.laishidua.view.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.laishidua.model.GhostMySelfieContract;
import com.laishidua.model.mediator.webdata.GhostMySelfie;
import com.laishidua.view.SettingsActivity;
import com.laishidua.R;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Show the view for each GhostMySelfie's meta-data in a ListView.
 */
public class GhostMySelfieAdapter
       extends BaseAdapter {
    /**
     * Allows access to application-specific resources and classes.
     */
    private final Context mContext;
        
    private static final String TAG =
    		GhostMySelfieAdapter.class.getSimpleName();

    /**
     * ArrayList to hold list of Selfies that is shown in ListView.
     */
    private List<GhostMySelfie> ghostmyselfieList =
        new ArrayList<>();
        
    /**
     * ArrayList to hold list of Selfies that is shown in ListView.
     */
    private List<String> modifierList =
        new ArrayList<>();        

    /**
     * Constructor that stores the Application Context.
     * 
     * @param Context
     */
    public GhostMySelfieAdapter(Context context) {
        super();
        mContext = context;
    }

    /**
     * Method used by the ListView to "get" the "view" for each row of
     * data in the ListView.
     * 
     * @param position
     *            The position of the item within the adapter's data
     *            set of the item whose view we want. convertView The
     *            old view to reuse, if possible. Note: You should
     *            check that this view is non-null and of an
     *            appropriate type before using. If it is not possible
     *            to convert this view to display the correct data,
     *            this method can create a new view. Heterogeneous
     *            lists can specify their number of view types, so
     *            that this View is always of the right type (see
     *            getViewTypeCount() and getItemViewType(int)).
     * @param parent
     *            The parent that this view will eventually be
     *            attached to
     * @return A View corresponding to the data at the specified
     *         position.
     */
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        GhostMySelfie ghostMySelfie = ghostmyselfieList.get(position);
        Log.d("VA.gV", ghostMySelfie.toString());

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =
                mInflater.inflate(R.layout.ghostmyselfie_list_item, null);
        }

        TextView titleText =
            (TextView) convertView.findViewById(R.id.tvSelfieTitle);
        TextView ratingText =
                (TextView) convertView.findViewById(R.id.tvSelfieRating);
        RatingBar ratingBar =
                (RatingBar) convertView.findViewById(R.id.ratingBar);
        titleText.setText(ghostMySelfie.getTitle());
        ratingText.setText("Star rating: " + String.format("%.1f", ghostMySelfie.getAverageVote()));
        ratingBar.setRating((float) ghostMySelfie.getAverageVote());
        CheckBox chkModifier = (CheckBox) convertView.findViewById(R.id.chkModifier); 
        final String selfieTitle = ghostMySelfie.getTitle();
        chkModifier.setOnClickListener(new OnClickListener() {
      	  @Override
      	  public void onClick(View v) {	
  	    		if (((CheckBox) v).isChecked()) {
  	    	        modifierList.add(selfieTitle);
  	    		} else {	
  	    			modifierList.remove(selfieTitle);
  	    		}
      	  }
      	});        
        
        return convertView;
    } 
    
    public List<String> getModifierCheckedBoxList(){
    	return modifierList;
    }   

    /**
     * Adds a GhostMySelfie to the Adapter and notify the change.
     */
    public void add(GhostMySelfie ghostMySelfie) {
        ghostmyselfieList.add(ghostMySelfie);
        notifyDataSetChanged();
    }

    /**
     * Removes a GhostMySelfie from the Adapter and notify the change.
     */
    public void remove(GhostMySelfie ghostMySelfie) {
        ghostmyselfieList.remove(ghostMySelfie);
        notifyDataSetChanged();
    }

    /**
     * Get the List of Selfies from Adapter.
     */
    public List<GhostMySelfie> getGhostMySelfies() {
        return ghostmyselfieList;
    }

    /**
     * Set the Adapter to list of Selfies.
     */
    public void setGhostMySelfies(List<GhostMySelfie> ghostMySelfies) {
        this.ghostmyselfieList = ghostMySelfies;
        notifyDataSetChanged();
    }

    /**
     * Get the no of selfies in adapter.
     */
    public int getCount() {
        return ghostmyselfieList.size();
    }

    /**
     * Get selfie from a given position.
     */
    public GhostMySelfie getItem(int position) {
        return ghostmyselfieList.get(position);
    }

    /**
     * Get Id of selfie from a given position.
     */
    public long getItemId(int position) {
        return position;
    }
}
