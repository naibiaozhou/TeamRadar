package com.nut.teamradar;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.SoundPool;
import android.os.Bundle;

public class CallActivity extends BaseActivity {

    protected boolean isInComingCall;
    protected String username;
    protected String DisplayUsername;
    protected CallingState callingState = CallingState.CANCED;
    protected String callDruationText;
    protected String msgid;
    protected AudioManager audioManager;
    protected SoundPool soundPool;
    protected Ringtone ringtone;
    protected int outgoing;
    protected EMCallStateChangeListener callStateListener;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null)
            soundPool.release();
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);
        
        if(callStateListener != null)
            EMChatManager.getInstance().removeCallStateChangeListener(callStateListener);
            
    }
    
    /**
     * 鎾斁鎷ㄥ彿鍝嶉搩
     * 
     * @param sound
     * @param number
     */
    protected int playMakeCallSounds() {
        try {
            // 鏈�澶ч煶閲�
            float audioMaxVolumn = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            // 褰撳墠闊抽噺
            float audioCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            float volumnRatio = audioCurrentVolumn / audioMaxVolumn;

            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(false);

            // 鎾斁
            int id = soundPool.play(outgoing, // 澹伴煶璧勬簮
                    0.3f, // 宸﹀０閬�
                    0.3f, // 鍙冲０閬�
                    1, // 浼樺厛绾э紝0鏈�浣�
                    -1, // 寰幆娆℃暟锛�0鏄笉寰幆锛�-1鏄案杩滃惊鐜�
                    1); // 鍥炴斁閫熷害锛�0.5-2.0涔嬮棿銆�1涓烘甯搁�熷害
            return id;
        } catch (Exception e) {
            return -1;
        }
    }
    
    // 鎵撳紑鎵０鍣�
    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 鍏抽棴鎵０鍣�
    protected void closeSpeakerOn() {

        try {
            if (audioManager != null) {
                // int curVolume =
                // audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                // audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                // curVolume, AudioManager.STREAM_VOICE_CALL);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 淇濆瓨閫氳瘽娑堟伅璁板綍
     * @param type 0锛氶煶棰戯紝1锛氳棰�
     */
    protected void saveCallRecord(int type) {
        EMMessage message = null;
        TextMessageBody txtBody = null;
        if (!isInComingCall) { // 鎵撳嚭鍘荤殑閫氳瘽
            message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            message.setReceipt(DisplayUsername);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.setFrom(DisplayUsername);
        }

        String st1 = getResources().getString(R.string.call_duration);
        String st2 = getResources().getString(R.string.Refused);
        String st3 = getResources().getString(R.string.The_other_party_has_refused_to);
        String st4 = getResources().getString(R.string.The_other_is_not_online);
        String st5 = getResources().getString(R.string.The_other_is_on_the_phone);
        String st6 = getResources().getString(R.string.The_other_party_did_not_answer);
        String st7 = getResources().getString(R.string.did_not_answer);
        String st8 = getResources().getString(R.string.Has_been_cancelled);
        switch (callingState) {
        case NORMAL:
            txtBody = new TextMessageBody(st1 + callDruationText);
            break;
        case REFUESD:
            txtBody = new TextMessageBody(st2);
            break;
        case BEREFUESD:
            txtBody = new TextMessageBody(st3);
            break;
        case OFFLINE:
            txtBody = new TextMessageBody(st4);
            break;
        case BUSY:
            txtBody = new TextMessageBody(st5);
            break;
        case NORESPONSE:
            txtBody = new TextMessageBody(st6);
            break;
        case UNANSWERED:
            txtBody = new TextMessageBody(st7);
            break;
        default:
            txtBody = new TextMessageBody(st8);
            break;
        }
        // 璁剧疆鎵╁睍灞炴��
        if(type == 0)
            message.setAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, true);
        else
            message.setAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, true);

        // 璁剧疆娑堟伅body
        message.addBody(txtBody);
        message.setMsgId(msgid);

        // 淇濆瓨
        //EMChatManager.getInstance().saveMessage(message, false);
    }

    enum CallingState {
        CANCED, NORMAL, REFUESD, BEREFUESD, UNANSWERED, OFFLINE, NORESPONSE, BUSY
    }
}
