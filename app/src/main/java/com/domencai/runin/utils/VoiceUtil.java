package com.domencai.runin.utils;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by lenovo on 2017/1/5.
 * 语音工具
 * 调用方式如下
 * VoiceUtil voiceUtil = new VoiceUtil(context,CHINESE);
 * voiceUtil.speaking("开始挑战");
 */

public class VoiceUtil {

    private Context context;
    private SpeechSynthesizer mTts;
    public static final int CANTONESE = 1;
    public static final int CHINESE = 2;

    public VoiceUtil(Context context, int speakingId){
        this.context = context;
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=586c7745");
        init();
        setParams(speakingId);
    }

    //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener  
    private void init() {
        mTts = SpeechSynthesizer.createSynthesizer(context, null);
    }

    private void setParams(int speakingId){
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类  
        switch (speakingId){
            case CANTONESE:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"xiaomei");//设置发音人 
                break;
            case CHINESE:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"vixq");//设置发音人 
                break;
            case 3:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"vimary");//设置发音人 
                break;
            case 4:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"vixl");//设置发音人 
                break;
            case 5:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"vixying");//设置发音人 
                break;
            case 6:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"xiaorong");//设置发音人 
                break;
            case 7:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"vixyun");//设置发音人 
                break;
            case 8:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"vixk");//设置发音人 
                break;
            case 9:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"vixqa");//设置发音人 
                break;
            case 10:
                mTts.setParameter(SpeechConstant.VOICE_NAME,"vinn");//设置发音人 
                break;
            default:
                break;
        }
        mTts.setParameter(SpeechConstant.SPEED,"50");//设置语速  
        mTts.setParameter(SpeechConstant.VOLUME,"80");//设置音量，范围0~100  
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);//设置云端  
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”  
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限  
        //如果不需要保存合成音频，注释该行代码  
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,"./sdcard/iflytek.pcm");
    }

    public void speaking(String speakingString){
        //3.开始合成  
        mTts.startSpeaking(speakingString,mSynListener);
    }
    //合成监听器  
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //开始播放  
        @Override
        public void onSpeakBegin() {

        }
        //缓冲进度回调  
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。  
        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }
        //暂停播放  
        @Override
        public void onSpeakPaused() {

        }
        //恢复播放回调接口 
        @Override
        public void onSpeakResumed() {

        }
        //播放进度回调  
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.  
        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }
        //会话结束回调接口，没有错误时，error为null  
        @Override
        public void onCompleted(SpeechError speechError) {

        }
        //会话事件回调接口 
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

}
