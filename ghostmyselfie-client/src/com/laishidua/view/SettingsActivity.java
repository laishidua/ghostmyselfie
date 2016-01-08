package com.laishidua.view;

import java.util.Calendar;

import com.laishidua.R;
import com.laishidua.common.GenericActivity;
import com.laishidua.model.GhostMySelfieContract;
import com.laishidua.presenter.SettingsOps;
import com.laishidua.utils.AlarmNotificationReceiver;
import com.laishidua.utils.Settings;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Settings Activity
 */

public class SettingsActivity 
            extends GenericActivity<SettingsOps.View, SettingsOps>
            implements SettingsOps.View {
	
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
    		SettingsActivity.class.getSimpleName();	
	
	private CheckBox chkGhost, chkGray, chkBlur, chkDark, chkActivateReminder;
    private TimePicker reminderTime;
	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent;    
	
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
        setContentView(R.layout.settings_activity);
        
		// Get the AlarmManager Service
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		// Create an Intent to broadcast to the AlarmNotificationReceiver
		mNotificationReceiverIntent = new Intent(SettingsActivity.this,
				AlarmNotificationReceiver.class);

		// Create an PendingIntent that holds the NotificationReceiverIntent
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
				SettingsActivity.this, 0, mNotificationReceiverIntent, 0);        
        
        // Invoke the special onCreate() method in GenericActivity,
        // passing in the GhostMySelfieOps class to instantiate/manage and
        // "this" to provide GhostMySelfieOps with the GhostMySelfieOps.View instance.
        super.onCreate(savedInstanceState,
                SettingsOps.class,
                this);
        getOps().onConfiguration(this, true);

        final Settings settings = getOps().getSettingsFromDB();
        
        chkGhost = (CheckBox) findViewById(R.id.chkGhost);
        chkGray = (CheckBox) findViewById(R.id.chkGray);
        chkBlur = (CheckBox) findViewById(R.id.chkBlur);
        chkDark = (CheckBox) findViewById(R.id.chkDark);
        chkActivateReminder = (CheckBox) findViewById(R.id.chkActivateReminder);
        reminderTime = (TimePicker) findViewById(R.id.reminderTime);
        
        if(settings.isGhost()){
        	chkGhost.setChecked(true);
        } else {
        	chkGhost.setChecked(false);
        }
        
        if(settings.isBlur()){
        	chkBlur.setChecked(true);
        } else {
        	chkBlur.setChecked(false);
        }
        
        if(settings.isGray()){
        	chkGray.setChecked(true);
        } else {
        	chkGray.setChecked(false);
        }
        
        if(settings.isDark()){
        	chkDark.setChecked(true);
        } else {
        	chkDark.setChecked(false);
        }
        
        if(settings.isReminder()){
        	chkActivateReminder.setChecked(true);
        } else {
        	chkActivateReminder.setChecked(false);
        }     
        
        reminderTime.setCurrentHour(settings.getReminderHour());
        reminderTime.setCurrentMinute(settings.getReminderMinute());
        
        addListenerOnChkGhost();
        addListenerOnChkBlur();
        addListenerOnChkGray();
        addListenerOnChkDark();
        addListenerOnChkActivateReminder();
        addListenerOnReminderTime();
        
    }
    
    public void addListenerOnChkGhost() {
    	chkGhost.setOnClickListener(new OnClickListener() {
    	  @Override
    	  public void onClick(View v) {
    		try {
            //is chkGhost checked?
    		int res = 0;   		
	    		if (((CheckBox) v).isChecked()) {
	    			res = getOps().checkSetting(
	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_GHOST,
	    					true);
	    		} else {	
					res = getOps().checkSetting(
	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_GHOST,
	    					false);
	    		}
	    		Log.d(TAG, "Result CheckGhost: " + res);
	    		if (res < 1) {
	    			throw new RemoteException();
	    		}
			} catch (RemoteException e) {
				Toast.makeText(
						SettingsActivity.this,
						"Error updating settings, try again.",
						Toast.LENGTH_SHORT).show();
			}
    	  }
    	});
     } 
    
    public void addListenerOnChkBlur() {
    	chkBlur = (CheckBox) findViewById(R.id.chkBlur);
    	chkBlur.setOnClickListener(new OnClickListener() {
    	  @Override
    	  public void onClick(View v) {

      		try {
                //is chkBlur checked?
        		int res = 0;   		
    	    		if (((CheckBox) v).isChecked()) {
    	    			res = getOps().checkSetting(
    	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_BLUR,
    	    					true);
    	    		} else {	
    					res = getOps().checkSetting(
    	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_BLUR,
    	    					false);
    	    		}
    	    		if (res < 1) {
    	    			throw new RemoteException();
    	    		}
    	    		Log.d(TAG, "Result CheckGhost: " + res);
    			} catch (RemoteException e) {
    				Toast.makeText(
    						SettingsActivity.this,
    						"Error updating settings, try again.",
    						Toast.LENGTH_SHORT).show();
    			}    		  

    	  }
    	});
     }  
    
    public void addListenerOnChkDark() {
    	chkDark = (CheckBox) findViewById(R.id.chkDark);
    	chkDark.setOnClickListener(new OnClickListener() {
    	  @Override
    	  public void onClick(View v) {
  
      		try {
                //is chkDark checked?
        		int res = 0;   		
    	    		if (((CheckBox) v).isChecked()) {
    	    			res = getOps().checkSetting(
    	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_DARK,
    	    					true);
    	    		} else {	
    					res = getOps().checkSetting(
    	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_DARK,
    	    					false);
    	    		}
    	    		if (res < 1) {
    	    			throw new RemoteException();
    	    		}
    	    		Log.d(TAG, "Result CheckGhost: " + res);
    			} catch (RemoteException e) {
    				Toast.makeText(
    						SettingsActivity.this,
    						"Error updating settings, try again.",
    						Toast.LENGTH_SHORT).show();
    			}

    	  }
    	});
     }      
    
    public void addListenerOnChkGray() {
    	chkGray = (CheckBox) findViewById(R.id.chkGray);
    	chkGray.setOnClickListener(new OnClickListener() {
    	  @Override
    	  public void onClick(View v) {

      		try {
                //is chkGray checked?
        		int res = 0;   		
    	    		if (((CheckBox) v).isChecked()) {
    	    			res = getOps().checkSetting(
    	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_GRAY,
    	    					true);
    	    		} else {	
    					res = getOps().checkSetting(
    	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_FILTER_GRAY,
    	    					false);
    	    		}
    	    		if (res < 1) {
    	    			throw new RemoteException();
    	    		}
    	    		Log.d(TAG, "Result CheckGhost: " + res);
    			} catch (RemoteException e) {
    				Toast.makeText(
    						SettingsActivity.this,
    						"Error updating settings, try again.",
    						Toast.LENGTH_SHORT).show();
    			}    		  

    	  }
    	});
     }        

    public void addListenerOnChkActivateReminder() {
    	chkActivateReminder = (CheckBox) findViewById(R.id.chkActivateReminder);
    	chkActivateReminder.setOnClickListener(new OnClickListener() {
    	  @Override
    	  public void onClick(View v) {

      		try {
                //is chkActivateReminder checked?
        		int res = 0;   		
    	    		if (((CheckBox) v).isChecked()) {
    	    			res = getOps().checkSetting(
    	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_ACTIVATE_REMINDER,
    	    					true);
    	                Calendar cal = Calendar.getInstance();
    	                cal.set (Calendar.HOUR_OF_DAY, reminderTime.getCurrentHour());
    	                cal.set (Calendar.MINUTE, reminderTime.getCurrentMinute());    	    			
    					// Set single alarm
    					mAlarmManager.set(AlarmManager.RTC_WAKEUP,
    							cal.getTimeInMillis(),
    							mNotificationReceiverPendingIntent);
    					// Show Toast message
    					Toast.makeText(getApplicationContext(), "Single Scream Alarm Set",
    							Toast.LENGTH_LONG).show();    	    			
    	    		} else {	
    					res = getOps().checkSetting(
    	    					GhostMySelfieContract.GhostMySelfieEntry.COLUMN_ACTIVATE_REMINDER,
    	    					false);
    					mAlarmManager.cancel(mNotificationReceiverPendingIntent);
    					// Show Toast message
    					Toast.makeText(getApplicationContext(), "Scream Alarm Canceled.",
    							Toast.LENGTH_LONG).show();    					
    	    		}
    	    		if (res < 1) {
    	    			throw new RemoteException();
    	    		}
    			} catch (RemoteException e) {
    				Toast.makeText(
    						SettingsActivity.this,
    						"Error updating settings, try again.",
    						Toast.LENGTH_SHORT).show();
    			}    		  

    	  }
    	});
     }     
    
    public void addListenerOnReminderTime() {
    	reminderTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker v, int hour, int minute) {
            	try {
            		int res = 0;
					res = getOps().setReminderTime(hour, minute);
					if (chkActivateReminder.isChecked()) {
					//mAlarmManager.cancel(mNotificationReceiverPendingIntent);
	                Calendar cal = Calendar.getInstance();
	                cal.set (Calendar.HOUR_OF_DAY, reminderTime.getCurrentHour());
	                cal.set (Calendar.MINUTE, reminderTime.getCurrentMinute());    	    			
					// Set single alarm
					mAlarmManager.set(AlarmManager.RTC_WAKEUP,
							cal.getTimeInMillis(),
							mNotificationReceiverPendingIntent);
					// Show Toast message
					//Toast.makeText(getApplicationContext(), "Single Scream Alarm Set",
						//	Toast.LENGTH_LONG).show(); 
					}
					if (res < 1) {
    	    			throw new RemoteException();
    	    		}					
				} catch (RemoteException e) {
    				Toast.makeText(
    						SettingsActivity.this,
    						"Error updating settings, try again.",
    						Toast.LENGTH_SHORT).show();
				}
            }

        });    	
    }
    
    /**
     *  Hook method that is called when user resumes activity
     *  from paused state, onPause(). 
     */
    @Override
    protected void onResume() {
        // Call up to the superclass.
        super.onResume();

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
    }


    /**
     * Finishes this Activity.
     */
    @Override
    public void finish() {
        setResult(RESULT_OK);
    	super.finish();
    }

}
