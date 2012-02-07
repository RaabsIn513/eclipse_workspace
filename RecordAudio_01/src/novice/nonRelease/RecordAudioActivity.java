package novice.nonRelease;

//import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class RecordAudioActivity extends Activity {
	
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = "test_rob.3gps";

    private RecordButton mRecordButton = null;
    private AudioRecord mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        setContentView(ll);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
  
    // ------------ BUTTON DEFINITIONS --------------
    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }
    
    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
    
    // ------------ FUNCTIONAL  ACTIONS -------------
    //@SuppressWarnings("null")
	private void startRecording() {
    	
    	//String dataDir = Environment.getDataDirectory().toString();
        //String exDataDir = Environment.getExternalStorageDirectory().toString();
        
    	int result = 0;
        byte[] audioData = new byte[4096];
        int offsetInBytes = 0;
        int sizeInBytes = 4096;
        int sampleRateInHz = 8000;
        int channelConfig = android.media.AudioFormat.CHANNEL_IN_DEFAULT;
        int audioFormat = android.media.AudioFormat.ENCODING_DEFAULT;
        int minBufSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    	try{
    		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, sizeInBytes);
    	}
    	catch(IllegalArgumentException e){
    		alertBox("new AudioRecord failed", e.toString());
    	}
    	alertBox("state", mRecorder.getState());
    	
    	if( mRecorder.getState() == AudioRecord.STATE_INITIALIZED)
    	{
	    	try{
	    		mRecorder.startRecording();
	    	}catch( IllegalStateException e)
	    	{
	    		alertBox(".startRecording() fail:", e.toString());
	    	}
	    	result = mRecorder.read(audioData, offsetInBytes, sizeInBytes);
			if( result == AudioRecord.ERROR_BAD_VALUE || result == AudioRecord.ERROR_INVALID_OPERATION)
				alertBox(".read() failed", result);
			
	        alertBox("mRecorder.read:", audioData.toString());
	        
	        mRecorder.stop();
	        mRecorder.release();
    	}
    	else if ( mRecorder.getState() == AudioRecord.STATE_UNINITIALIZED )
    		alertBox("Uninitialized", "Uninitialized state for AudioRecord object");
    		
    }
    


	private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    // ----------- FUNCTIONAL EVENTS ----------------
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    
    // ------------ Debugging purposes -------------- 
    protected void alertBox(String title, String myMessage)
    {
	    new AlertDialog.Builder(this)
	       .setMessage(myMessage)
	       .setTitle(title)
	       .setCancelable(false)
	       .setNeutralButton(android.R.string.cancel,
	          new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int whichButton){}
	          })
	       .show();
    }
    
    protected void alertBox(String title, Object[] myMessage)
    {
    	String convToStr = new String();
    	
    	for( int i = 0; i < myMessage.length; i++)
    		convToStr += myMessage[i].toString() + "\n";
    	
    	
	    new AlertDialog.Builder(this)
	       .setMessage(convToStr)
	       .setTitle(title)
	       .setCancelable(false)
	       .setNeutralButton(android.R.string.cancel,
	          new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int whichButton){}
	          })
	       .show();
    }
    
    private void alertBox(String title, int number) {
		// TODO Auto-generated method stub
    	String convToStr = new String();  	
    	convToStr = String.valueOf(number);
    	
	    new AlertDialog.Builder(this)
	       .setMessage(convToStr)
	       .setTitle(title)
	       .setCancelable(false)
	       .setNeutralButton(android.R.string.cancel,
	          new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int whichButton){}
	          })
	       .show();	
	}
}