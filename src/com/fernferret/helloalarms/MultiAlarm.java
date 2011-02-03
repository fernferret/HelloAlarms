package com.fernferret.helloalarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MultiAlarm extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, R.string.multi_alarm_is_firing, Toast.LENGTH_SHORT).show();
	}
	
}
