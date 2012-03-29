package senior.project;

import java.util.Observable;
import java.util.Observer;

import android.media.AudioRecord;
import android.media.MediaRecorder;

public class audioGen implements Runnable {
	
    // encapsulates management of the observers watching this 
	// data source for update events:
    class MyObservable extends Observable {
    	
	    public void notifyObservers(audioGen b) {
	        super.notifyObservers(b);	        
	    }
	    
	    public void clearChanged()
	    {
	    	super.clearChanged();
	    }
	    
	    public void setChanged()
	    {
    		super.setChanged();
	    }
    }
      
	private int bufferSize = 2048;
	private int sizeInShorts = 1024;
	private boolean isRec = false;
    private AudioRecord mRecorder = null;
    //public byte[] audioData = new byte[bufferSize];
    public short[] audioData = new short[sizeInShorts];
    //private MediaPlayer   mPlayer = null;
    //private static final int offsetInBytes = 0;
    private static final short offsetInShorts = 0;
    private static int sampleRateInHz = 8000;
    public static final int channelConfig = android.media.AudioFormat.CHANNEL_IN_DEFAULT;
    public static final int audioFormat = android.media.AudioFormat.ENCODING_DEFAULT;
    
    
    private MyObservable notifier;
    {
        notifier = new MyObservable();
    }
    public audioGen( int SAMPLE_RATE )
    {
    	AppLog.APP_TAG = "audioGen";
    	AppLog.logString("SAMPLE_RATE: " + SAMPLE_RATE);
    	sampleRateInHz = SAMPLE_RATE;
    	
    	if( mRecorder != null )
    		mRecorder = null;
    }
    
    public void release()
    {
    	mRecorder.release();
    }
    
    public int getBufferSize()
    {
    	return bufferSize;
    }
        
    public short[] getShorts()
    {
    	return audioData;
    }
    
    public void run(){
    	
    	
    	mRecorder = getAudioRecorder(sampleRateInHz, audioFormat, 512);

    	if( mRecorder.getState() > 0 ) // Initialized
    	{
			try{
				mRecorder.startRecording();
				isRec = true;
				int result = 0;
			    audioData = new short[sizeInShorts];
				
		    	while( isRec )	// Continuous recording
		    	{
		    		// read into short[], no offset, size
		    		result = mRecorder.read(audioData, offsetInShorts, sizeInShorts);
			    	
			    	if ( result == AudioRecord.ERROR_INVALID_OPERATION )
			    	{
			    		System.out.println(".read() failed unable to perform AudioRecord.read()\n exiting");
			    		mRecorder.release();
			    		break;
			    	}
			    	else
			    	{
						notifier.setChanged();
						notifier.notifyObservers(this);
						notifier.clearChanged();
			    	}
		    	}
			}catch( IllegalStateException e ){
				e.printStackTrace();
			}
    	} 
    }
    
    /**
     * 
     * @param sampRate - Desired Sample Rate in Hertz
     * @param format - audioFormat, typically default
     * @param minBuffSize - The desired buffer size. This may increase
     * 						in order to meet the specified sampRate
     * @return AudioRecord object - a working AudioRecord object ready to be used (.read())
     */
    private AudioRecord getAudioRecorder(int sampRate, int format, int minBuffSize )
    {
    	while( true )
    	{
	    	AudioRecord result = new AudioRecord(MediaRecorder.AudioSource.MIC, 
	    			sampRate, channelConfig, format, minBuffSize);
	    	
	    	if( result.getState() <= 0 )
	    	{
	    		result = null;
	    		minBuffSize *=2;
	    	}
	    	else if(result.getState() == 1)
	    		return result;
    	}
    }
    
    public void addObserver(Observer observer) {
        notifier.addObserver(observer);
    }
 
    public void removeObserver(Observer observer) {
        notifier.deleteObserver(observer);
    }

}
