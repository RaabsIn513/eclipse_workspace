package seniorDesign.components;

import android.util.Log;

public class AppLog {
        public static String APP_TAG = "AudioRecorder";
        
        public static int logString(String message){
                return Log.i(APP_TAG,message);
        }
}