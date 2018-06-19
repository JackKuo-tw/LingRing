/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jackkuo.lingring;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.*;
import android.util.Log;

/**
 * Listens for incoming SIP calls, intercepts and hands them off to Dial.
 */
public class IncomingCallReceiver extends BroadcastReceiver {

    /**
     * Processes the incoming call, answers it, and hands it over to the
     * Dial.
     * @param context The context under which the receiver is running.
     * @param intent The intent being received.
     */
    public SipAudioCall incomingCall = null;
    private Dial DialObj;
    @Override
    public void onReceive(Context context, Intent intent) {
        final MainActivity wtActivity = (MainActivity) context;
        DialObj = wtActivity.DialObj;
        try {
            Log.d("Receive", "Call incoming");
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    try {
                        call.answerCall(30);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                public void onCallEnded(SipAudioCall call) {
                    DialObj.endMessage();
                    DialObj.isSpeakerClicked = false;
                }
            };


            incomingCall = DialObj.manager.takeAudioCall(intent, listener);
            DialObj.updateStatus("call incoming");
            DialObj.call = incomingCall;
            DialObj.call.answerCall(30);
            DialObj.call.startAudio();
            DialObj.call.setSpeakerMode(DialObj.isSpeakerClicked);
            DialObj.SpeakerBtn.setBackgroundResource(DialObj.isSpeakerClicked ? R.drawable.sound_loud : R.drawable.sound);
            if(DialObj.call.isMuted()) {
                DialObj.call.toggleMute();
            }

//            wtActivity.updateStatus(incomingCall);
            DialObj.callVisibility(true); // 讓掛斷按鈕顯示
        } catch (Exception e) {
            if (incomingCall != null) {
                incomingCall.close();
            }
        }
    }

}
