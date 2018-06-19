package com.example.jackkuo.lingring;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;


/**
 * A simple {@link Fragment} subclass.
 */
public class Dial extends Fragment {

    View V;
    private EditText phoneNum;
    public String domain;
    public String sipAddress = null;
    public boolean isSpeakerClicked = false;
    public boolean endingCall = false;
    public boolean ringBack = false;
    public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
    private String whom = "";
    private String username;
    private String password;
    Button ConnectionBtn;
    Button DisconnectionBtn;
    Button CallBtn;
    Button HangupBtn;
    Button SpeakerBtn;
    private static final int CALL_ADDRESS = 1;
    private static final int SET_AUTH_INFO = 2;
    private static final int UPDATE_SETTINGS_DIALOG = 3;
    private static final int HANG_UP = 4;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;



    public Dial() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        V = inflater.inflate(R.layout.dial, container, false);
        // "Push to talk" can be a serious pain when the screen keeps turning off.
        // Let's prevent that.
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findViews();
        initializeManager();
        display();
        // Inflate the layout for this fragment
        return V;
    }

    public void BtnConnect(View v){
        initializeManager();
    }

    public void BtnUnregister(View v){
        Log.d("Close", "OnHangUpCall");
        hangUpCall(); // 如果通話進行中則掛斷
        Log.d("Close", "OnDestroy");
        closeLocalProfile();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hangUpCall();
        Log.d("Close", "OnDestroy");
//        closeLocalProfile();
        // 照理來說應該要 closeLocalProfile(); 可是在 frame 切換會 crash

    }

    public SipRegistrationListener mRegistrationListener  = new SipRegistrationListener() {
        public void onRegistering(String localProfileUri) {
            updateStatus("Registering with SIP Server...");
            Log.d("account", "Registering with SIP Server...");
        }

        public void onRegistrationDone(String localProfileUri, long expiryTime) {
            updateStatus("Ready");
        }

        public void onRegistrationFailed(String localProfileUri, int errorCode,
                                         String errorMessage) {
            updateStatus("Registration failed.  Please check settings.");
        }
    };

    public void initializeManager() {
        if(manager == null) {
            manager = SipManager.newInstance(getActivity());
        }

        initializeLocalProfile();
        /* 在 profile initialize 結束後即可撥號*/
        if(!"".equals(whom)){
            sipAddress = "sip:" + whom + '@' + domain;
            whom = "";
            initiateCall();
        }

    }

    /**
     * Logs you into your SIP provider, registering this device as the location to
     * send SIP calls to for your SIP address.
     */
    public void initializeLocalProfile() {
        if (manager == null) {
            return;
        }

        if (me != null) {
            Log.d("Close", "null");
            closeLocalProfile();
        }
        // 從上一個呼叫頁面拿取資料
        Intent intent = getActivity().getIntent();
        Bundle bag = intent.getExtras();
        if (intent.getStringExtra("whom") != null){
            whom = bag.getString("whom", "");
        }
        username = getActivity().getSharedPreferences("account", 0)
                .getString("ext", "");
        password = getActivity().getSharedPreferences("account", 0)
                .getString("pass", "");

        domain = getActivity().getSharedPreferences("account", 0)
                .getString("server", "");

        if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
            getActivity().showDialog(UPDATE_SETTINGS_DIALOG);
            return;
        }

        try {
            Log.d("account", username+ " " +password+ " " +domain);
            SipProfile.Builder builder = new SipProfile.Builder(username, domain);
            builder.setPassword(password);
            me = builder.build();

            Intent i = new Intent();
            i.setAction("com.example.jackkuo.lingring.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, i, Intent.FILL_IN_DATA);
            manager.open(me, pi, null);


            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.

            manager.setRegistrationListener(me.getUriString(), mRegistrationListener);
        } catch (ParseException pe) {
            updateStatus("Connection Error.");
        } catch (SipException se) {
            updateStatus("Connection error.");
        }
    }

    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    public void closeLocalProfile() {
        Log.d("Close", "closingProfile");
        if (manager == null) {
            Log.d("close", "manager = null");
            return;
        }
        try {
            if (me != null) {
                Log.d("Close", "unregister");
                manager.unregister(me, mRegistrationListener);
                Thread.sleep(300);
                Log.d("Close", "getUristring");
                manager.close(me.getUriString());
                Log.d("Close", "unregister the device from the server");
            }
        } catch (Exception ee) {
            Log.d("onDestroy", "Failed to close local profile.", ee);
        }
    }

    /**
     * Make an outgoing call.
     */
    public void initiateCall() {

        updateStatus(sipAddress);

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    Log.d("Listener", "Established");
                    if(ringBack) { // 如果對方真的在線
                        call.startAudio(); // 在通話接通後進行語音處理
                        call.setSpeakerMode(false);
                        if(call.isMuted()) { // 取消靜音
                            call.toggleMute();
                        }
                        updateStatus(call);
                    }
                    else{ // 對方不在線上
                        hangUpCall();
                    }

                }

                @Override
                public void onCallEnded(SipAudioCall call) { // 掛斷
                    Log.d("Listener", "CallEnded");
                    hangUpCall();
                    if(!ringBack) {
                        call("928");
                    }
                }

                /* Called when an event occurs and the corresponding callback is not overridden. */
                @Override
                public void onChanged(SipAudioCall call){
                    Log.d("Listener", "onChanged");
                    ringBack = false; // set default
                }

                /* function: onRingingBack
                可以用來判斷對方是否在線
                如果沒有 onRingingBack 就 Established 即是對方不在線
                onChanged → Established → CallEnded
                正常是
                onChanged → onRingingBack → Established → CallEnded
                 */

                @Override
                public void onRingingBack(SipAudioCall call){
                    Log.d("Listener", "onRingingBack");
                    ringBack = true;
                }

            };
            callVisibility(true);
            call = manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);

        }
        catch (Exception e) {
            Log.i("InitiateCall", "Error when trying to close manager.", e);
            if (me != null) {
                try {
                    manager.close(me.getUriString());
                } catch (Exception ee) {
                    Log.i("InitiateCall",
                            "Error when trying to close manager.", ee);
                    ee.printStackTrace();
                }
            }
            if (call != null) {
                call.close();
            }
        }
    }

    /**
     * Updates the status box at the top of the UI with a messege of your choice.
     * @param status The String to display in the status box.
     */
    public void updateStatus(final String status) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                TextView labelView = (TextView) getActivity().findViewById(R.id.sipLabel);
                labelView.setText(status);
            }
        });
    }

    /**
     * Updates the status box with the SIP address of the current call.
     * @param call The current, active call.
     */
    public void updateStatus(SipAudioCall call) {
        String useName = call.getPeerProfile().getDisplayName();
        if(useName == null) {
            useName = call.getPeerProfile().getUserName();
        }
        updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CALL_ADDRESS, 0, "Call someone");
        menu.add(0, SET_AUTH_INFO, 0, "Edit your SIP Info.");
        menu.add(0, HANG_UP, 0, "End Current Call.");

        return true;
    }


    public void SipCalling(View v){
        phoneNum = (EditText) getActivity().findViewById(R.id.phoneNum);
        if (username.length() != 0 && domain.length() != 0 && password.length() != 0) {
            if (!phoneNum.getText().toString().matches("")) { //不能爲空
                sipAddress = "sip:" + phoneNum.getText().toString() + '@' + domain;
                Log.d("num", sipAddress);
                initiateCall();
            }
            else{
                Toast.makeText(getActivity(), "號碼有誤：" + phoneNum.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getActivity(), "帳號設定錯誤", Toast.LENGTH_LONG).show();
        }
    }

    public void ContactCalling(String ContactPhoneNum){
        if (username.length() != 0 && domain.length() != 0 && password.length() != 0) {
            if (!ContactPhoneNum.matches("") || ContactPhoneNum.indexOf(',') >= 0) { //不能爲空
                sipAddress = "sip:" + ContactPhoneNum + '@' + domain;
                Log.d("num", sipAddress);
                initiateCall();
            }
            else{
                Toast.makeText(getActivity(), "號碼有誤：" + ContactPhoneNum, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getActivity(), "帳號設定錯誤", Toast.LENGTH_LONG).show();
        }
    }

    private void findViews() {
        phoneNum = (EditText) getActivity().findViewById(R.id.phoneNum);
        HangupBtn = (Button) getActivity().findViewById(R.id.hangup);
        SpeakerBtn = (Button) getActivity().findViewById(R.id.Speaker);
    }

    public void hangUpCall(){

        Log.d("close", "ending");
        /*
        ** getState() is a method in SipSession.State **
        READY_TO_CALL: 0, 待機
        OUTGOING_CALL_CANCELING: 7, When a CANCEL request is sent for the INVITE request sent.
        IN_CALL: 8, 通話中
        NOT_DEFINED: 101
         */
        if(!endingCall && call!=null) { // 避免重複掛斷導致 crash
            Log.d("hangUpCall", "Start");
            try {
                Log.d("hangUpCall", "Starting");
                endingCall = true;
                Log.d("hangUpCall", "Starting");
                call.endCall();
                Log.d("hangUpCall", "endCall finish");
                endingCall = false;
                Log.d("hangUpCall", "End");
                endMessage();
            } catch (SipException se) {
                Log.d("endCall", "Error ending call.");
                Log.d("onOptionsItemSelected",
                        "Error ending call.", se);
            }
            call.close();
        }
    }

    public void BtnHangUpCall(View v){
        hangUpCall();
    }

    public void BtnSpeakerClick(View v){

        isSpeakerClicked = !isSpeakerClicked;
        SpeakerBtn.setBackgroundResource(isSpeakerClicked ? R.drawable.sound_loud : R.drawable.sound);
        call.setSpeakerMode(isSpeakerClicked);

    }

    public void callVisibility(boolean calling){

        if(calling){
            HangupBtn.setVisibility(View.VISIBLE);
            SpeakerBtn.setVisibility(View.VISIBLE);
            isSpeakerClicked = false;
            SpeakerBtn.setBackgroundResource(R.drawable.sound);
        }
        else{
            INVISIBLE();
        }

    }

    public void INVISIBLE(){
        SpeakerBtn.setVisibility(View.INVISIBLE);
        HangupBtn.setVisibility(View.INVISIBLE);
    }

    public void endMessage(){

        Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),
                "通話結束",
                Snackbar.LENGTH_LONG)
                .show();
        updateStatus("Ready");
        callVisibility(false);
        Log.d("endMessage", "endMessage Finish");
    }

    private void call(String phone) {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        }else {
            // Permission has already been granted
        }
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getActivity().getApplicationContext() , "該操作需要撥號電話權限", Toast.LENGTH_LONG).show();
        }
        else{
            Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
            startActivity(intent);
        }

    }
    private void display(){

        ConnectionBtn = (Button) V.findViewById(R.id.Connect);
        DisconnectionBtn = (Button) V.findViewById(R.id.unregister);
        CallBtn = (Button) V.findViewById(R.id.call);
        HangupBtn = (Button) V.findViewById(R.id.hangup);
        SpeakerBtn = (Button) V.findViewById(R.id.Speaker);

        ConnectionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                BtnConnect(view);
            }

        });
        DisconnectionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                BtnUnregister(view);
            }

        });
        CallBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SipCalling(view);
            }

        });
        HangupBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                BtnHangUpCall(view);
            }

        });
        SpeakerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                BtnSpeakerClick(view);
            }

        });
    }

}
