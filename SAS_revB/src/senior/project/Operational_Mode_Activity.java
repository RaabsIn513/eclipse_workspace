package senior.project;

import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Operational_Mode_Activity extends Activity{

	private static Button exitBT;
	private LinkedList<Double> ampHist = new LinkedList<Double>();
	//private Double SumHigh = 0.0;
	//private Double avg = 0.0;
	//private int AMP_SAMP_HISTORY_SIZE;
	//private int THRESH_SAMPS;
	//private int THRESH_CNT;
	//private double avgDiff = 0;
	//private int THRESH_DIFF;
	private final int SAMP_HISTORY_SIZE = 64;
	private boolean trigger = false;
	public static audioGen audioData;
	public static Thread audioThread;
	public static TextView debug;
	private static String SAS_Settings = "SAS_SettingsFile";
	private static SharedPreferences SettingsFile;
	public  static Context thisContext;
	
	
    private class mUpdater implements Observer {
        //double[] data;
        boolean mTrigger = false;
    	private Double SumHigh = 0.0;
    	private Double avg = 0.0;
    	private int AMP_SAMP_HISTORY_SIZE;
    	private int THRESH_DIFF;
    	private int THRESH_SAMPS;
    	private int THRESH_CNT;
    	private double avgDiff = 0;
    	// Constructor sets up the series
        public mUpdater(boolean trigger ) {
        	this.mTrigger = trigger;
        	
    	    try{
    		    SettingsFile = getSharedPreferences(SAS_Settings, 0);
    		    AMP_SAMP_HISTORY_SIZE = Integer.valueOf(SettingsFile.getInt("AMP_SAMP_HISTORY_SIZE", 64));
    		    THRESH_DIFF = Integer.valueOf(SettingsFile.getInt("THRESH_DIFF", 20));
    		    THRESH_SAMPS = Integer.valueOf(SettingsFile.getInt("THRESH_SAMPS", 20));
    	    }
    	    catch( Exception ex )
    	    {
    	    	AppLog.logString(ex.getMessage());    	    	
    	    }
        	
        }

		@Override
        public void update(Observable o, Object arg) {

			// cast the incoming arg object as a audioGen object
			audioGen income = (audioGen)arg;	
			
			// raw mic data
			short[] raw = income.getShorts();		
			double[] data = convShort2Double(raw);
						
			double Max = max(data);
			//double Min = min(data);
			SumHigh = manageHighSum( Max );
			if( ampHist.size() < AMP_SAMP_HISTORY_SIZE )
				avg = SumHigh/ ampHist.size();
			else
				avg = SumHigh / AMP_SAMP_HISTORY_SIZE;
			
			//THRESH_DIFF  - the difference between the avg and the current max
			//THRESH_SAMPS - the number of samples in which ( currentMax - avg > THRESH_DIF ) 
			//				 that has to occur to set off an event trigger!
			//THRESH_CNT   - the current number of consecutive times that currentMax - avg > THRESH_DIFF )
			
			if( (Max - avg ) > THRESH_DIFF )
			{		
				THRESH_CNT += 1;
				avgDiff += (Max - avg);
				
				if( THRESH_CNT >= THRESH_SAMPS )			// We have an event!
				{	
					avgDiff = avgDiff / THRESH_CNT;
					mTrigger = true;
					THRESH_CNT = 0; 	// Reset the THRESH_CNT
		        	AppLog.APP_TAG = "TRIGGERin";
		        	AppLog.logString("AVG DIFF: " + avgDiff);
		        			        	
		        	String[] info = new String[1];
		        	info[0] = "avg of (Max - avg) for the event was: " + String.valueOf(avgDiff);
		        	Send_SMS_Activity.sendAlertSMS(info, getApplicationContext());
				}
			}
			else
				THRESH_CNT = 0;			// Reset the THRESH_CNT back to zero since we want consecutive samples			
				        
		}
		
		private Number[] fft_mag( double[] data )
		{
			int numUniquePts = (data.length + 1) / 2;	// assume our data is always a poewr of 2
			Complex[] x 	= new Complex[data.length]; // initialize a Complex[]
			Complex[] x2    = new Complex[numUniquePts];
			double[] Xd 	= new double[numUniquePts];
			Number[] result = new Number[numUniquePts];
			
			for( int i = 0; i < data.length; i++ )		// create Complex[] of the audio data
				x[i] = new Complex( data[i], 0); 		// Complex(real, imaginary);
			
			x = FFT.fft(x);
			// FFT is symetric take away half
			for( int i = 0; i < numUniquePts; i++ )
				x2[i] = x[i];
			// Magnitude
			Xd = FFT.abs(x2);
			// Scale the fft so that it is not a function of the length of x2
			//Xd = FFT.scale(Xd, 16/Xd.length);
			// Square the magnitude to get power.
			Xd = FFT.power(Xd, 2);
			// The DC component and Nyquist component, are unique 
			// and should not be multiplied by 2. Make a sub array of Xd
			for( int i = 1; i < Xd.length - 2; i++ )
				Xd[i] = Xd[i]*2;
			
			// chopping off the fist and last values?
			for( int i = 0; i < Xd.length; i++ )
				result[i] = (Number)(10*Math.log10(Xd[i]));
			
			return result;
		} 
		
		private double max( double[] list )
		{
			double result = 0;
			for( int i = 0; i < list.length; i++ )
			{
				if( list[i] > result )
					result = list[i] + 1;
			}
			return result;
		}
		
		private double min( double[] list )
		{
			double result = 0;
			for( int i = 0; i < list.length; i++ )
			{
				if( list[i] < result )
					result = list[i] + 1;
			}
			return result;
		}
		
		private double manageHighSum( double ValIn )
		{
			double result = SumHigh;
			ampHist.addLast(ValIn);
			if( ampHist.size() < AMP_SAMP_HISTORY_SIZE )
				result += ampHist.getLast();
			else
			{
				result = result - ampHist.getFirst() + ampHist.getLast();
				ampHist.removeFirst();
			}
			return result;
		}
				
		private double[] convShort2Double( short[] data )
		{
			double[] result = new double[data.length];
			for( int i = 0; i < data.length; i++ )
				result[i] = (double)data[i];
			
			return result;
		}
    }
			
	@Override
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main_operational);
	    
	    Context thisContext = getApplicationContext();
	    // Get our settings
	    /*
	    try{
		    SettingsFile = getSharedPreferences(SAS_Settings, 0);
		    AMP_SAMP_HISTORY_SIZE = Integer.valueOf(SettingsFile.getInt("AMP_SAMP_HISTORY_SIZE", 64));
		    THRESH_DIFF = Integer.valueOf(SettingsFile.getInt("THRESH_DIFF", 20));
		    THRESH_SAMPS = Integer.valueOf(SettingsFile.getInt("THRESH_SAMPS", 20));
	    }
	    catch( Exception ex )
	    {
	    	ex.getMessage();
	    }
	    */
	    exitBT = (Button) findViewById(R.id.btn_exitOp);
	    debug = (TextView) findViewById(R.id.tvDebug);
	    
	    mUpdater audioIn = new mUpdater(trigger);
	    
	    audioData = new audioGen(8000);
	    
	    audioData.addObserver(audioIn);
	    
        // kick off the data generating thread:
        audioThread = new Thread(audioData);
        audioThread.start();
        
        if( trigger )
        {
        	trigger = false;
        	AppLog.APP_TAG = "TRIGGERex";
        	
        	AppLog.logString("TRIGGER EVENT");
        }
	}
	
    @Override
    public void onBackPressed() {
    	// release our recorder object Android takes 
    	// care of this with finish() since this activity 
    	// created this thread, it kills it
    	audioData.release();	
    	//audioThread.stop();
    	//audioThread.destroy();
    	finish();
    return;
    }
}
