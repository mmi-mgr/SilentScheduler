package jp.co.mmi_sc.ringtone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public class ScheduleReceiver extends BroadcastReceiver {
    private static final String TAG = "InitializeReceiver";
    private static final int TOAST_DURATION = 5000;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "onReceive()");
        boolean silent = intent.getBooleanExtra(Schedule.EXTRA_RINGER_MODE, false);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(silent ? AudioManager.RINGER_MODE_SILENT : AudioManager.RINGER_MODE_NORMAL);
        //String msg = silent ? R.string.set_silent_mode : R.string.set_normal_mode;
        Toast.makeText(context, "Set " + (silent ? "silent" : "normal") + " mode.", TOAST_DURATION).show();
        InitializeReceiver.restartSchedule(context);
    }

}
