package senior.project;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Operational_Mode_Activity extends Activity{

    private class mUpdater implements Observer {
        //double[] data;
        //boolean mTrigger = false;
    	TextView aTV;
    	// Constructor sets up the series
        public mUpdater(TextView aTV) {
            //this.data = data;
        	this.aTV = aTV;
        }

		@Override
        public void update(Observable o, Object arg) {

			// cast the incoming arg object as a audioGen object
			audioGen income = (audioGen)arg;	
			
			// raw mic data
			short[] raw = income.getShorts();		
			double[] data = convShort2Double(raw);
			
			//aTV.setText(String.valueOf(data[0]));
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
		
		private void manageHighSum( double ValIn )
		{
			ampHist.addLast(ValIn);
			if( ampHist.size() < AMP_HISTORY_SIZE )
				SumHigh += ampHist.getLast();
			else
			{
				SumHigh = SumHigh - ampHist.getFirst() + ampHist.getLast();
				ampHist.removeFirst();
			}
		}
				
		private double[] convShort2Double( short[] data )
		{
			double[] result = new double[data.length];
			for( int i = 0; i < data.length; i++ )
				result[i] = (double)data[i];
			
			return result;
		}
    }
	
	private static Button exitBT;
	private LinkedList<Double> ampHist;
	private Double SumHigh = 0.0;
	private int AMP_HISTORY_SIZE;
	private final int HISTORY_SIZE = 64;
	private boolean trigger = false;
	public static audioGen audioData;
	public static Thread audioThread;
	public static TextView debug;
		
	@Override
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main_operational);
	    
	    exitBT = (Button) findViewById(R.id.btn_exitOp);
	    debug = (TextView) findViewById(R.id.tvDebug);
	    
	    mUpdater audioIn = new mUpdater(debug);
	    
	    audioData = new audioGen();
	    
	    audioData.addObserver(audioIn);
	    
	    //debug.setText("hello");
	    
        // kick off the data generating thread:
        audioThread = new Thread(audioData);
        audioThread.start();
        
	}
}
