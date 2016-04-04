package jp.co.mmi_sc.ringtone;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;

import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import jp.co.mmi_sc.ringtone.Schedule.Pattern;

public class ScheduleAdapter extends CursorAdapter {

    private static final String FORMAT_12H = "KK:mm a";
    private static final String FORMAT_24H = "HH:mm";

    private Context mContext;

    private LayoutInflater mInflater;
    private final Pattern[] mPatterns;
    private boolean mTime24hour;
    private SimpleDateFormat timeFormat = new SimpleDateFormat(FORMAT_12H);

    public ScheduleAdapter(final Context context, final Cursor c, final boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPatterns = Pattern.values();
    }

    public void setTime24(final boolean time24) {
        this.mTime24hour = time24;
        if (time24) {
            timeFormat = new SimpleDateFormat(FORMAT_24H);
        } else {
            timeFormat = new SimpleDateFormat(FORMAT_12H);
        }
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        View view = this.mInflater.inflate(R.layout.schedule_row, null);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor csr) {
        final SQLiteCursor cursor = (SQLiteCursor) csr;
        final Schedule entry = DbHelper.getEntryFromCursor(cursor);
        if (entry != null) {
            final TextView dayLabel = (TextView) view.findViewById(R.id.dayLabel);
            dayLabel.setText(toDayLabel(entry.getPattern()));
            dayLabel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    int nextpos = entry.getPattern().ordinal() + 1;
                    if (mPatterns.length <= nextpos) {
                        nextpos = 0;
                    }
                    entry.setPattern(mPatterns[nextpos]);
                    DbHelper.update(cursor.getDatabase(), entry);
                    dayLabel.setText(toDayLabel(mPatterns[nextpos]));
                    InitializeReceiver.restartSchedule(context);
                }
            });

            final TextView timeLabel = (TextView) view.findViewById(R.id.timeLabel);
            timeLabel.setText(timeFormat.format(entry.getTime()));
            timeLabel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    TimePickerDialog dlg = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
                            entry.setHour(hourOfDay);
                            entry.setMinute(minute);
                            DbHelper.update(cursor.getDatabase(), entry);
                            timeLabel.setText(timeFormat.format(entry.getTime()));
                            InitializeReceiver.restartSchedule(context);
                        }
                    }, entry.getHour(), entry.getMinute(), mTime24hour);
                    dlg.show();
                }

            });

            final ImageView silentIcon = (ImageView) view.findViewById(R.id.silentIcon);
            silentIcon.setImageResource(entry.isSilent() ? R.drawable.ic_lock_silent_mode : R.drawable.ic_lock_silent_mode_off);
            silentIcon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    boolean nextSilent = !entry.isSilent();
                    entry.setSilent(nextSilent);
                    DbHelper.update(cursor.getDatabase(), entry);
                    silentIcon.setImageResource(nextSilent ? R.drawable.ic_lock_silent_mode : R.drawable.ic_lock_silent_mode_off);
                    InitializeReceiver.restartSchedule(context);
                }
            });
        }
    }

    private String toDayLabel(final Pattern pattern) {
        if (pattern == null) {
            throw new InvalidParameterException("Long label is null.");
        }
        String label = null;
        switch (pattern) {
        case Everyday:
            label = mContext.getString(R.string.everyday);
            break;
        case Weekday:
            label = mContext.getString(R.string.weekday);
            break;
        case Weekend:
            label = mContext.getString(R.string.weekend);
            break;
        case Monday:
            label = mContext.getString(R.string.monday);
            break;
        case Tuesday:
            label = mContext.getString(R.string.tuesday);
            break;
        case Wednesday:
            label = mContext.getString(R.string.wednesday);
            break;
        case Thursday:
            label = mContext.getString(R.string.thursday);
            break;
        case Friday:
            label = mContext.getString(R.string.friday);
            break;
        case Saturday:
            label = mContext.getString(R.string.saturday);
            break;
        case Sunday:
            label = mContext.getString(R.string.sunday);
            break;
        }
        return label;
    }
}
