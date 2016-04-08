package jp.co.mmi_sc.ringtone;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * スケジュール編集のActivity。
 * 
 * @author y-magara
 * 
 */
public class SilentScheduler extends ListActivity {

    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private ScheduleAdapter mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDb = (new DbHelper(this)).getWritableDatabase();
        mCursor = DbHelper.createCursor(mDb);
        mAdapter = new ScheduleAdapter(this, mCursor, true);
        setListAdapter(mAdapter);
        registerForContextMenu(getListView());
        InitializeReceiver.restartSchedule(getApplicationContext());
    }

    /**
     * 時刻表示形式が24時間制か否(12時間制)か。
     * 
     * @return システムの時刻表示形式
     */
    private boolean isTime24() {
        // 24/12 hour format
        String value = Settings.System.getString(getApplicationContext().getContentResolver(), Settings.System.TIME_12_24);
        boolean time24hour = (value == null || value.length() == 0 || "24".equals(value));
        return time24hour;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 12/24時間制の設定を更新
        mAdapter.setTime24(isTime24());
        mCursor.requery();

        if (mAdapter.getCount() == 0) {
            Toast.makeText(this, R.string.please_add, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setListAdapter(null);
        mCursor.deactivate();
        mDb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.option, menu);

        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add:
            DbHelper.insert(mDb, new Schedule());
            mCursor.requery();
            InitializeReceiver.restartSchedule(getApplicationContext());
            if (mAdapter.getCount() == 1) {
                Toast.makeText(getApplicationContext(), R.string.how_to_delete, Toast.LENGTH_LONG).show();
            }
            break;
        case R.id.cancel:
            // nothing
            break;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        new MenuInflater(this).inflate(R.menu.context, menu);

    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.delete:
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            DbHelper.delete(mDb, info.id);
            mCursor.requery();
            InitializeReceiver.restartSchedule(getApplicationContext());
            return (true);
        }

        return (super.onOptionsItemSelected(item));
    }
}
