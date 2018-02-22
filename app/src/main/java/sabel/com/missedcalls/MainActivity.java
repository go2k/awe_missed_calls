package sabel.com.missedcalls;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    // DATA FIELDS
    private TextView textView;
    private Button button;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RQ_Call = 7411;

    // OVERRIDE onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });
    } // END OVERRIDE onCreate

    // OVERRIDE onStart
    @Override
    protected void onStart() {
        super.onStart();
        button.setVisibility(View.GONE);
        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // APP hat noch nicht die Permission
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG)) {
                textView.setText(R.string.explain_accept);
                button.setVisibility(View.VISIBLE);
            } else {
                // APP hat noch keine Permissions => Permission anfordern
                requestPermission();
            } // END INNER IF-ELSE
        } else {
            // App hat bereits die Permission => weiter im Programm
            showMissedCalls();
        } // END IF-ELSE
    } // END @Override protected void onStart()

    private void showMissedCalls() {
        textView.setText(getString(R.string.template, getIntMissedCalls()));
    } // END private void showMissedCalls()

    private int getIntMissedCalls() {
        int missedCalls = 0;
        String[] projection = {CallLog.Calls._ID};
        String selection = CallLog.Calls.TYPE + " = ?";
        String[] selectionArgs = {Integer.toString(CallLog.Calls.MISSED_TYPE)};
        ContentResolver contentResolver = getContentResolver();
        try {
            Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, null);
            if (cursor != null) {
                missedCalls = cursor.getCount();
                cursor.close();
            } // END IF
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException", e);
        } // END TRY-CATCH

        return missedCalls;
    } // END private int getIntMissedCalls()

    private void requestPermission() {
        String[] permissions = new String[] {Manifest.permission.READ_CALL_LOG};
        requestPermissions(permissions, RQ_Call);
    } // END private void requestPermission()

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RQ_Call) {
            button.setVisibility(View.GONE);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMissedCalls();
            } else {
                textView.setText(R.string.explain_denied);
            } // END INNER IF
        } // END IF
    } //END OVERRIDE onRequestPermissionsResult

} // END CLASS
