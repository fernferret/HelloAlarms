package com.fernferret.helloalarms;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HelloAlarms extends Activity {
	
	private static final int SINGLE_ALARM_RC = 0;
	private static final int MULTI_ALARM_RC = 1;
	private static final int NO_FLAGS = 0;
	private static final int TIME_IN_SECONDS_FOR_SINGLE_ALARM = 10;
	private static final int TIME_IN_SECONDS_FOR_MULTI_ALARM = 7;
	
	// Shared Toast
	private Toast mToast;
	
	// Single Alarm Variables
	private long mSingleStartTime;
	private Handler mSingleHandler = new Handler();
	private TextView mTimeTillSingle;
	private Button mSingleButton;
	
	// Multi Alarm Variables
	private long mMultiStartTime;
	private Handler mMultiHandler = new Handler();
	private Button mMultiStartButton;
	private Button mMultiStopButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mSingleButton = (Button) findViewById(R.id.single_alarm);
		mSingleButton.setOnClickListener(mSingleAlarmListener);
		
		mMultiStartButton = (Button) findViewById(R.id.start_button);
		mMultiStartButton.setOnClickListener(mMultiAlarmStartListener);
		
		mMultiStopButton = (Button) findViewById(R.id.stop_button);
		mMultiStopButton.setOnClickListener(mMultiAlarmStopListener);
		
		mTimeTillSingle = (TextView) findViewById(R.id.time_till_single);
	}
	
	private OnClickListener mSingleAlarmListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			
			mSingleButton.setEnabled(false);
			
			Intent singleAlarmIntent = new Intent(HelloAlarms.this, SingleAlarm.class);
			
			PendingIntent singleAlarmPendingIntent = PendingIntent.getBroadcast(HelloAlarms.this, SINGLE_ALARM_RC, singleAlarmIntent, NO_FLAGS);
			long currentTime = System.currentTimeMillis();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(currentTime);
			
			calendar.add(Calendar.SECOND, TIME_IN_SECONDS_FOR_SINGLE_ALARM);
			
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), singleAlarmPendingIntent);
			
			if (mSingleStartTime == 0L) {
				mSingleStartTime = currentTime;
				mSingleHandler.removeCallbacks(mUpdateTimersTask);
				mSingleHandler.postDelayed(mUpdateTimersTask, 100);
			}
			
			/**
			 * By using a shared toast, we can cancel a toast!
			 */
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(HelloAlarms.this, R.string.one_shot_scheduled, Toast.LENGTH_SHORT);
			mToast.show();
		}
	};
	
	private OnClickListener mMultiAlarmStartListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// Allow the user to stop the multi alarm, but not start it again
			mMultiStartButton.setEnabled(false);
			mMultiStopButton.setEnabled(true);
			
			Intent multiAlarmIntent = new Intent(HelloAlarms.this, MultiAlarm.class);
			
			PendingIntent multiAlarmPendingIntent = PendingIntent.getBroadcast(HelloAlarms.this, MULTI_ALARM_RC, multiAlarmIntent, NO_FLAGS);
			
			// Set the first alarm time to fire at now plus whatever the time we have set for the alarm in seconds
			long firstTime = SystemClock.elapsedRealtime();
			firstTime += (TIME_IN_SECONDS_FOR_MULTI_ALARM * 1000);
			
			
			
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, TIME_IN_SECONDS_FOR_MULTI_ALARM * 1000, multiAlarmPendingIntent);
			
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(HelloAlarms.this, "Starting Repeating Task", Toast.LENGTH_SHORT);
			mToast.show();
			
		}
	};
	
	private OnClickListener mMultiAlarmStopListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private Runnable mUpdateTimersTask = new Runnable() {
		@Override
		public void run() {
			final long start = mSingleStartTime;
			long millis = SystemClock.uptimeMillis() + 50;
			double displayTime = TIME_IN_SECONDS_FOR_SINGLE_ALARM - ((System.currentTimeMillis() - (mSingleStartTime + TIME_IN_SECONDS_FOR_SINGLE_ALARM)) / 1000.0);
			int roundedTime = (int) displayTime;
			if (displayTime < -2.0) {
				mTimeTillSingle.setText("");
				mSingleHandler.removeCallbacks(mUpdateTimersTask);
				mSingleStartTime = 0L;
				mSingleButton.setEnabled(true);
				mSingleButton.setText(getString(R.string.single_alarm_button_text));
			} else if (displayTime < 0.0) {
				mTimeTillSingle.setText("Event just fired!");
				mSingleButton.setText(getString(R.string.single_alarm_button_text_with_param, roundedTime + 2));
			} else {
				mTimeTillSingle.setText(getString(R.string.time_till_single, displayTime));
				mSingleButton.setText(getString(R.string.single_alarm_button_text_with_param, roundedTime + 2));
			}
			
			mSingleHandler.postAtTime(this, millis);
		}
	};
}