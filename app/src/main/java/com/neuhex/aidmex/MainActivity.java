package com.neuhex.aidmex;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private AlertDialog enableNotificationListenerAlertDialog;

    public static final String EXTRA_BLUETOOTH_DEVICE = "BT_DEVICE";
    private BluetoothDevice mDevice;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;

    ScrollView scrollview;

    Button Button2;
    Button Button3;
    Button Button4;
    Button Button5;
    Button Button6;
    Button Button_getHR;
    Button Button_sendCMD;

    ImageButton Button1;
    ImageButton Button_AIDme;
    ImageButton Button_Data;

    CheckBox checkBox;
    CheckBox checkBox1;

    TextView tvContent;
    TextView txt_StepCount;
    TextView txt_HRData;
    TextView txt_HRTime;
    TextView txt_stepDistance;
    TextView txt_KCal;
    TextView am_battery_level;
    TextView am_BLEstatus;
    TextView am_BLEaddress;

    EditText cmd_line;


    SharedPreferences SharedPref_AIDmeBLE;
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            Intent intent = new Intent(this, com.neuhex.aidmex.Settings.class);
            startActivityForResult(intent, 34);
        } else if (v.getId() == R.id.button5) {
            Intent intent = new Intent(this, com.neuhex.aidmex.httplogset.class);
            startActivityForResult(intent, 2);
        } else if (v.getId() == R.id.button1) {
            Intent intent = new Intent(this, com.neuhex.aidmex.ScanActivity.class);
            startActivityForResult(intent, 2);
        } else if (v.getId() == R.id.button3) {
            clearLog();
            load();
            scrollDown();
        } else if (v.getId() == R.id.button4) {
            Intent intent = new Intent(this, com.neuhex.aidmex.NofiticationPicker.class);
            startActivityForResult(intent, 2);
            scrollDown();
        }else if(v.getId()==R.id.button_AIDme){
            Intent intent= new Intent(MainActivity.this, AIDmeSettings.class);
            startActivity(intent);
        }else if (v.getId() == R.id.checkBox) {
            SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
            editor.putBoolean("isNotificationEnabled", checkBox.isChecked());
            editor.apply();
            stopService(new Intent(this, com.neuhex.aidmex.BLEservice.class));
            if (checkBox.isChecked() && !isMyServiceRunning()) startService(new Intent(this, com.neuhex.aidmex.BLEservice.class));
        } else if (v.getId() == R.id.checkBox1) {
            SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
            editor.putBoolean("isDebugEnabled", checkBox1.isChecked());
            editor.apply();
        }else if(v.getId() == R.id.button_data){
            Intent intent= new Intent(MainActivity.this, VitalData.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cmd_line= (EditText) findViewById(R.id.cmd_line);
        txt_StepCount = (TextView) findViewById(R.id.step_count);
        txt_HRData = (TextView) findViewById(R.id.HR_data);
        txt_HRTime = (TextView) findViewById(R.id.HR_time);
        txt_stepDistance= (TextView) findViewById(R.id. step_distance);
        txt_KCal= (TextView) findViewById(R.id. step_kcal);
        am_battery_level = (TextView) findViewById(R.id.battery_level);
        am_BLEstatus = (TextView) findViewById(R.id.watch_BLEstatus);
        am_BLEaddress = (TextView) findViewById(R.id.watch_BLEaddress);
        tvContent = (TextView) findViewById(R.id.tv_content);
        scrollview = (ScrollView) findViewById(R.id.scroll);
        Button_sendCMD=findViewById(R.id. button_send);
        Button_sendCMD.setOnClickListener(this);
        Button_AIDme = findViewById(R.id.button_AIDme);
        Button_AIDme.setOnClickListener(this);
        Button_getHR = findViewById(R.id.button_getHR);
        Button_getHR.setOnClickListener(this);
        Button_Data= findViewById(R.id.button_data);
        Button_Data.setOnClickListener(this);
        Button1 = findViewById(R.id.button);
        Button1.setOnClickListener(this);
        Button2 = findViewById(R.id.button1);
        Button2.setOnClickListener(this);
        Button4 = findViewById(R.id.button3);
        Button4.setOnClickListener(this);
        Button5 = findViewById(R.id.button4);
        Button5.setOnClickListener(this);
        Button6 = findViewById(R.id.button5);
        Button6.setOnClickListener(this);
        checkBox = findViewById(R.id.checkBox);
        checkBox.setOnClickListener(this);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox1.setOnClickListener(this);


        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        checkBox.setChecked(prefs.getBoolean("isNotificationEnabled", true));
        checkBox1.setChecked(prefs.getBoolean("isDebugEnabled", false));

        SharedPref_AIDmeBLE = getSharedPreferences("AIDmeBLEPref", Context.MODE_PRIVATE);

        SharedPreferences AM_SP = getApplicationContext().getSharedPreferences("AIDmeBLEPref", Context.MODE_PRIVATE);

        String saved_BLEstatus = AM_SP.getString("AMSP_BLEstatus", "UNKNOWN");

        am_BLEstatus.setText(saved_BLEstatus);
        am_BLEaddress.setText(prefs.getString("MacId", "00:00:00:00:00:00"));
        am_battery_level.setText(prefs.getString("BatteryPercent", "?") + "% ");
        txt_StepCount.setText(prefs.getString("Steps", "---"));
        txt_HRData.setText(prefs.getString("HR_Data", "---"));
        txt_HRTime.setText(prefs.getString("HR_Time", "--:--"));

        txt_stepDistance.setText(prefs.getString("stepDistance","--")+"m");
        txt_KCal.setText(prefs.getString("KCal","--")+"KCal");



        Button3 = (Button) findViewById(R.id.button2);
        checkLocationPermissions();
        checkSMSPermissions();

        Button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://sites.google.com/view/neuhex/aidme-app-privacy-policy"));
                startActivity(intent);
            }
        });

        Button_getHR.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendBLEcmd("AT+HR:getHR");
            }
        });

        Button_sendCMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBLEcmd(cmd_line.getText().toString());
                KLog(cmd_line.getText().toString());
            }
        });

        if (!isNotificationServiceEnabled()) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String logmsg = intent.getStringExtra("ToActivity");
                if(AIDme_isInCMD(logmsg, "AT+PACE")){
                    txt_StepCount.setText(logmsg.substring(8));
                }else if(AIDme_isInCMD(logmsg,"AT+BATT")){
                    am_battery_level.setText(logmsg.substring(8)+"%");
                }else if(AIDme_isInCMD(logmsg,"Conn")){
                    am_BLEstatus.setText("CONNECTED");
                    SharedPreferences.Editor editor= SharedPref_AIDmeBLE.edit();
                    editor.putString("AMSP_BLEstatus","CONNECTED");
                    editor.commit();
                }else if(AIDme_isInCMD(logmsg,"Discon")){
                    am_BLEstatus.setText("DISCONNECTED");
                    SharedPreferences.Editor editor= SharedPref_AIDmeBLE.edit();
                    editor.putString("AMSP_BLEstatus","DISCONNECTED");
                    editor.commit();
                }else if(AIDme_isInCMD(logmsg,"AT+BLEid")){
                    am_BLEaddress.setText(logmsg.substring(9));
                }else if(AIDme_isInCMD(logmsg,"AT+HRD")){
                    txt_HRData.setText(logmsg.substring(7));
                }else if(AIDme_isInCMD(logmsg, "AT+HRT")){
                    txt_HRTime.setText(logmsg.substring(7));
                }
                load();
            }
        };
        //if(!isMyServiceRunning() && prefs.getBoolean("isNotificationEnabled", true))startService(new Intent(this, BLEservice.class));

    }

    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private AlertDialog buildNotificationServiceAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Welcome to The AIDme App");
        alertDialogBuilder.setMessage("Please allow Notification for This App in the Upcoming window and press the back button");
        alertDialogBuilder.setPositiveButton("Open settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("No Abort",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        return (alertDialogBuilder.create());
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter("ToActivity1"));

    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == 4) {
                if (data.getExtras() != null) {
                    mDevice = data.getExtras().getParcelable(EXTRA_BLUETOOTH_DEVICE);
                }

                KLog("Got Device:" + mDevice.getName() + " " + mDevice.getAddress());
                SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                editor.putString("MacId", mDevice.getAddress());
                editor.apply();
                stopService(new Intent(this, com.neuhex.aidmex.BLEservice.class));
                if (checkBox.isChecked() && !isMyServiceRunning()) startService(new Intent(this, com.neuhex.aidmex.BLEservice.class));
            }
        } else if (requestCode == 34) {

        }
    }



    public void KLog(String TEXT) {
        tvContent.append("\n" + TEXT + "\n");
        scrollDown();
    }

    void scrollDown() {
        Thread scrollThread = new Thread() {
            public void run() {
                try {
                    sleep(200);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            scrollview.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        scrollThread.start();
    }

    public void sendResult(String message) {
        Intent intent = new Intent("MSGtoServiceIntent");
        if (message != null)
            intent.putExtra("MSGtoService", message);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void load() {
        tvContent.setText("Please tap on CONNECT to select your Watch, you need to accept Location services to enable Bluetooth access to this app.\r\n\r\nYou can Enable or Disable the notification for certain apps via the PICK APP Button.\r\n\r\nFeedback and RATE the app.\r\n\r\n Stay Safe! :)\r\n");
        FileInputStream fis = null;
        try {
            fis = openFileInput("AIDmeNotification.log");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            KLog(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void clearLog() {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("AIDmeNotification.log", MODE_PRIVATE);
            fos.write("".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (com.neuhex.aidmex.BLEservice.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean AIDme_isInCMD(String fullCmd, String searchedCmd) {
        int searchedLength = searchedCmd.length();
        return fullCmd.substring(0, searchedLength).equals(searchedCmd);
    }


    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    42);
        }
    }

    private void checkSMSPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    42);
        }
    }

    public void sendBLEcmd(String message) {
        Intent intent = new Intent("MSGtoServiceIntentBLEcmd");
        if (message != null)
            intent.putExtra("MSGtoService", message);
        localBroadcastManager.sendBroadcast(intent);
    }

}