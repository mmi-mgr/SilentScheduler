package jp.co.mmi_sc.ringtone;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InitializeReceiver extends BroadcastReceiver {
    private static final String TAG = "InitializeReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "onReceive()");
        restartSchedule(context);
    }

    /**
     * スケジュールの再始動。
     * 
     * @param context
     */
    public static void restartSchedule(final Context context) {
        cancelPendingSchedule(context);
        Schedule[] allSchedule = getAllSchedule(context);
        if (allSchedule != null && allSchedule.length > 0) {
            Schedule nextSchedule = allSchedule[0];
            Calendar next = nextSchedule.getNext();
            for (int i = 1; i < allSchedule.length; i++) {
                Calendar other = allSchedule[i].getNext();
                if (next.after(other)) {
                    next = other;
                    nextSchedule = allSchedule[i];
                }
            }
            Log.d(TAG, nextSchedule.dump() + " at " + next.getTime().toString());
            PendingIntent pending = getPendingIntent(context, nextSchedule.isSilent());
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pending);
        }
    }

    private static void cancelPendingSchedule(final Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, ScheduleReceiver.class), 0));
    }

    private static PendingIntent getPendingIntent(final Context context, final boolean silent) {
        Intent intent = new Intent(context, ScheduleReceiver.class);
        intent.putExtra(Schedule.EXTRA_RINGER_MODE, silent);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        return pending;
    }

    private static Schedule[] getAllSchedule(final Context context) {
        Schedule[] array = null;
        SQLiteDatabase db = new DbHelper(context).getReadableDatabase();
        Cursor cursor = DbHelper.createCursor(db);
        List<Schedule> list = new ArrayList<Schedule>(cursor.getCount());
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                list.add(DbHelper.getEntryFromCursor(cursor));
                if (!cursor.moveToNext()) {
                    break;
                }
            }
            array = new Schedule[list.size()];
            list.toArray(array);
        }
        cursor.close();
        db.close();
        return array;
    }
}
