package edu.orangecoastcollege.cs273.vnguyen629.occlostandfound;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Allows an administrator to view any the details of a user account
 *
 * @author Benjamin Nguyen
 */
public class AdminViewUserActivity extends AppCompatActivity {

    private TextView adminViewUsernameTextView;
    private TextView adminViewStudentIDTextView;
    private TextView adminViewEmailTextView;
    private TextView adminViewPhoneTextView;

    private Sensor accelerometer;
    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;

    /**
     * Starts up the activity and links up the TextViews and sets them up with
     * the user's information. Also prepares the ShakeDetector to monitor any
     * movements that constitute as shakes where the AdminItemListActivity may load
     * up if found.
     * @param savedInstanceState The state of the application saved into a bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        final UserAccount user = getIntent().getExtras().getParcelable("SelectedUser");

        adminViewUsernameTextView = (TextView) findViewById(R.id.adminViewUsernameTextView);
        adminViewStudentIDTextView = (TextView) findViewById(R.id.adminViewStudentIDTextView);
        adminViewEmailTextView = (TextView) findViewById(R.id.adminViewEmailTextView);
        adminViewPhoneTextView = (TextView) findViewById(R.id.adminViewPhoneTextView);

        adminViewPhoneTextView.setText(user.getStudentUserName());
        adminViewStudentIDTextView.setText(user.getStudentID());
        adminViewEmailTextView.setText(user.getStudentEmail());
        adminViewPhoneTextView.setText(user.getStudentPhoneNum());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            /**
             * When a 3D motion that the sensors constitute as a shake has been detected,
             * the ItemListActivity is loaded
             */
            @Override
            public void onShake() {
                startActivity(new Intent(AdminViewUserActivity.this, AdminItemListActivity.class));
            }
        });
    }

    /**
     *
     * @param view
     */
    public void sendSMS(View view) {
        Intent smsIntent = new Intent(AdminViewUserActivity.this, SendSMSActivity.class);
        smsIntent.putExtra("UserPhoneNumber", adminViewPhoneTextView.getText().toString());
        startActivity(smsIntent);
    }

    /**
     *
     * @param view
     */
    public void viewFeedbacks(View view) {
        final UserAccount user = getIntent().getExtras().getParcelable("SelectedUser");
        Intent viewFeedbackIntent = new Intent(this, ViewUserFeedbackActivity.class);
        viewFeedbackIntent.putExtra("UserFeedback", user);
        startActivity(viewFeedbackIntent);
    }

    /**
     * When the user re-enters the app, the sensors start back up and begin
     * monitoring device movements/g-forces in a 3D (x-y-z) span.
     */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(shakeDetector, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * When the user switches apps or clicks on the home button without closing the app,
     * all the sensors that monitor device movements and g-forces are then paused
     * to preserve battery life.
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeDetector);
    }
}
