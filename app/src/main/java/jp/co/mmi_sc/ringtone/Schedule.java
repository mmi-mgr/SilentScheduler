package jp.co.mmi_sc.ringtone;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 設定内容。
 * 
 * @author y-magara
 * 
 */
public class Schedule {
    public static final String EXTRA_RINGER_MODE = Schedule.class.getPackage().toString() + ".RingerMode";

    public enum Pattern {
        /** 毎日。 */
        Everyday,
        /** 平日(月-金)。 */
        Weekday,
        /** 休日(土日)。 */
        Weekend,

        /** 月曜日。 */
        Monday,
        /** 火曜日。 */
        Tuesday,
        /** 水曜日。 */
        Wednesday,
        /** 木曜日。 */
        Thursday,
        /** 金曜日。 */
        Friday,
        /** 土曜日。 */
        Saturday,
        /** 日曜日。 */
        Sunday
    }

    /**
     * SQLite上のID。
     */
    private long mRecordId;

    /**
     * Hour part of time.
     */
    private int mHour;

    /**
     * Minute part of time.
     */
    private int mMinute;

    /**
     * 繰り返しのパターン。
     */
    private Pattern mPattern;

    /**
     * サイレントモード。
     */
    private boolean mSilent;

    public Schedule() {
        this.mPattern = Pattern.Everyday;
    }

    public long getRecordId() {
        return mRecordId;
    }

    public void setRecordId(final long recordId) {
        this.mRecordId = recordId;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(final int hour) {
        this.mHour = hour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(final int minute) {
        this.mMinute = minute;
    }

    public Date getTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, mHour);
        cal.set(Calendar.MINUTE, mMinute);
        return cal.getTime();
    }

    public boolean isSilent() {
        return mSilent;
    }

    public void setSilent(final boolean silent) {
        this.mSilent = silent;
    }

    public Pattern getPattern() {
        return mPattern;
    }

    public void setPattern(final Pattern mPattern) {
        this.mPattern = mPattern;
    }

    public boolean isDayOfWeek(final int day) {
        boolean result = false;
        if (this.mPattern == Pattern.Everyday) {
            result = true;
        } else {
            switch (day) {
            case Calendar.MONDAY:
                result = (this.mPattern == Pattern.Weekday || this.mPattern == Pattern.Monday);
                break;
            case Calendar.TUESDAY:
                result = (this.mPattern == Pattern.Weekday || this.mPattern == Pattern.Tuesday);
                break;
            case Calendar.WEDNESDAY:
                result = (this.mPattern == Pattern.Weekday || this.mPattern == Pattern.Wednesday);
                break;
            case Calendar.THURSDAY:
                result = (this.mPattern == Pattern.Weekday || this.mPattern == Pattern.Thursday);
                break;
            case Calendar.FRIDAY:
                result = (this.mPattern == Pattern.Weekday || this.mPattern == Pattern.Friday);
                break;
            case Calendar.SATURDAY:
                result = (this.mPattern == Pattern.Weekend || this.mPattern == Pattern.Saturday);
                break;
            case Calendar.SUNDAY:
                result = (this.mPattern == Pattern.Weekend || this.mPattern == Pattern.Sunday);
                break;
            }
        }
        return result;
    }

    /**
     * 次のスケジュール日時を取得する.
     * 
     * 現在時刻以降で直近のスケジュール日時を取得する.
     * 
     * @return 次回の日時
     */
    public Calendar getNext() {
        Calendar now = new GregorianCalendar();
        now.set(Calendar.SECOND, 0);
        Calendar next = (Calendar) now.clone();
        next.set(Calendar.HOUR_OF_DAY, mHour);
        next.set(Calendar.MINUTE, mMinute);
        if (!next.after(now)) {
            next.add(Calendar.HOUR_OF_DAY, 24);
        }
        for (int i = 0; i <= 7; i++) {
            if (this.isDayOfWeek(next.get(Calendar.DAY_OF_WEEK))) {
                break;
            }
            next.add(Calendar.HOUR_OF_DAY, 24);
        }

        return next;
    }

    public String dump() {
        return String.format("[%d] %s %02d:%02d %s", this.mRecordId, this.mPattern.toString(), this.mHour, this.mMinute, Boolean.toString(this.mSilent));
    }
}
