/////
package jp.co.mmi_sc.ringtone;

import java.security.InvalidParameterException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import jp.co.mmi_sc.ringtone.Schedule.Pattern;

/**
 * Schedule database.
 * 
 * @author y-magara
 * 
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "db";
    private static final String TABLE_NAME = "schedule";

    public enum Columns {
        _id, pattern, hour, minute, silent
    };

    public static final int idColumn = Columns._id.ordinal();
    public static final int patternColumn = Columns.pattern.ordinal();
    public static final int hourColumn = Columns.hour.ordinal();
    public static final int minuteColumn = Columns.minute.ordinal();
    public static final int silentColumn = Columns.silent.ordinal();

    private static final String[] PROJECTION = { Columns._id.name(), Columns.pattern.name(), Columns.hour.name(), Columns.minute.name(), Columns.silent.name() };

    private static final String SQL_CREATE = "CREATE TABLE schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " pattern TEXT, hour INTEGER, minute INTEGER, silent BOOLEAN)";

    /**
     * 新規追加エントリ。
     */
    public static final long NEW_ENTRY = 0;

    /**
     * コンストラクタ。
     * 
     * @param context
     *            アプリケーション・コンテキスト
     */
    public DbHelper(final Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // TODO 自動生成されたメソッド・スタブ

    }

    public static Cursor createCursor(final SQLiteDatabase db) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME);
        Cursor cursor = builder.query(db, PROJECTION, null, null, null, null, null);
        return cursor;
    }

    /**
     * カーソル位置のスケジュール情報を取得する。
     * 
     * @param cursor
     *            取得位置を示すカーソル
     * @return スケジュール情報から構築したScheduleオブジェクト
     */
    public static Schedule getEntryFromCursor(final Cursor cursor) {
        Schedule entry = new Schedule();
        entry.setRecordId(cursor.getLong(idColumn));
        entry.setPattern(Pattern.valueOf(cursor.getString(patternColumn)));
        entry.setHour(cursor.getInt(hourColumn));
        entry.setMinute(cursor.getInt(minuteColumn));
        entry.setSilent(cursor.getInt(silentColumn) != 0);
        return entry;
    }

    /**
     * 新規レコードのinsert。
     * 
     * @param db
     *            insert対象テーブルを持ったDB
     * @param entry
     *            insertる内容を持ったScheduleオブジェクト
     * @return insertされたレコードのID
     */
    public static long insert(final SQLiteDatabase db, final Schedule entry) {
        long id = entry.getRecordId();
        if (id != NEW_ENTRY) {
            throw new InvalidParameterException("Not a new entry(id=" + id + ").");
        }
        id = db.insert(TABLE_NAME, null, creaetValuesFromEntry(entry));
        entry.setRecordId(id);
        return id;
    }

    /**
     * レコードを更新。
     * 
     * @param db
     *            対象レコードを格納したDB
     * @param entry
     *            更新内容を持ったエントリ
     * @return 更新されたレコード数
     */
    public static int update(final SQLiteDatabase db, final Schedule entry) {
        long id = entry.getRecordId();
        if (id < 1) {
            throw new InvalidParameterException("Invalid record id(id=" + id + ").");
        }

        String[] whereArgs = new String[] { Long.toString(entry.getRecordId()) };
        int affected = db.update(TABLE_NAME, creaetValuesFromEntry(entry), "_id=?", whereArgs);
        return affected;
    }

    /**
     * レコードの削除。
     * 
     * @param db
     *            対象レコードを格納したDB
     * @param id
     *            削除対象のレコードID
     * @return 削除されたレコード数
     */
    public static int delete(final SQLiteDatabase db, final long id) {
        if (id < 1) {
            throw new InvalidParameterException("Invalid record id(id=" + id + ").");
        }
        int affected = db.delete(TABLE_NAME, "_id=?", new String[] { Long.toString(id) });
        return affected;
    }

    /**
     * Scheduleエントリからinsert/update用のContentValuesを構築する。
     * 
     * @param entry
     *            ContenValuesの元となるSchedule
     * @return entryの値を持ったContentValues
     */
    private static ContentValues creaetValuesFromEntry(final Schedule entry) {
        ContentValues values = new ContentValues();
        values.put(Columns.pattern.name(), entry.getPattern().name());
        values.put(Columns.hour.name(), entry.getHour());
        values.put(Columns.minute.name(), entry.getMinute());
        values.put(Columns.silent.name(), entry.isSilent());
        return values;
    }
}
