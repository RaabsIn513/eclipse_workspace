package com.example;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

public class TestPlotActivity extends Activity {
	
    // redraws a plot whenever an update is received:
    private class mPlotUpdater implements Observer {
        XYPlot plot;
        // Constructor sets up the series
        public mPlotUpdater(XYPlot plot) {
            this.plot = plot;
            // only display whole numbers in domain labels
            plot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
            
            //plot.setRangeBoundaries(0, 1000, BoundaryMode.AUTO);
            //plot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
            
            /*
            LineAndPointFormatter series1Format = new LineAndPointFormatter(
                    Color.rgb(150, 50, 100),                   // line color
                    Color.rgb(255, 0, 0),                   // point color
                    Color.rgb(150, 50, 100));              // fill color (optional)
            */
            // Bar plot
            plot.addSeries(audioHistSeries, BarRenderer.class, new BarFormatter(Color.argb(100, 0, 200, 0), Color.rgb(0, 80, 0)));
            
            //plot.addSeries(audioHistSeries, series1Format);
            
            // thin out domain/range tick labels so they don't overlap each other:
            plot.setGridPadding(5, 0, 5, 0);
            plot.setTicksPerDomainLabel(5);
            plot.setTicksPerRangeLabel(3);
            plot.disableAllMarkup();
     
            
            // freeze the range boundaries:            
            plot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
            plot.setDomainStepValue(1);
            plot.setTicksPerRangeLabel(6);
            plot.setDomainLabel("Time");
            plot.getDomainLabelWidget().pack();
            plot.setRangeLabel("Amplitude");
            plot.getRangeLabelWidget().pack();
            plot.disableAllMarkup();
        }

		@Override
        public void update(Observable o, Object arg) {
        	//o.notifyObservers();
			audioGen income = (audioGen)arg;	// cast the incoming arg object as a audioGen object
			
			short[] raw = income.getShorts();		// raw mic data
			double[] data = convShort2Double(raw);
			
			//sectionalFFT( data );
			waveform( data );

        }
		
		/**
		 * sectionalFFT takes an array with size greater than HISTORY_SIZE
		 * and creates FFT frames to graph. The graph's domain is equal
		 * to HISTORY_SIZE
		 * @param input
		 */
		private void sectionalFFT( double[] input )
		{
			AppLog.APP_TAG = "sectionalFFT";
			// Split this data into pieces that are HISTORY_SIZE elements in size
			int nFrames = input.length / HISTORY_SIZE;			// Number of frames we will create from a mic read
			Number[] graphable = new Number[HISTORY_SIZE];
			double[] section = new double[HISTORY_SIZE];
			
			AppLog.logString("input.length: " + input.length +
					" nFrames: " + nFrames);
			
			// for the number of sections
			for ( int j = 1; j < nFrames + 1; j++ )
			{
				// Get the section
				for( int i = 0; i < HISTORY_SIZE; i++ )
					section[i] = input[i*j];				// get the fft magnitude of 64 value frame
				graphable = fft_mag( section );
				audioHist.clear();
				for( int i = 0; i < graphable.length; i++)
					audioHist.add(i, graphable[i]);
					
				// adjust the Range (y axis) to be as long as it needs to be. 
				//plot.setRangeBoundaries(0, max(graphable), BoundaryMode.FIXED);
				plot.setRangeBoundaries(30, 100, BoundaryMode.FIXED);
				// update the graph. 
				audioHistSeries.setModel(audioHist, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
				plot.setDomainBoundaries(0, graphable.length, BoundaryMode.FIXED);
				try {
					dynamicPlot.postRedraw();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void waveform( double[] input )
		{
			if( audioHist.size() >= HISTORY_SIZE)
				audioHist.clear();
			
			for( int i = 0; i < HISTORY_SIZE; i++)
				audioHist.addLast(input[i]);
			
			audioHistSeries.setModel(audioHist, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
			
			try {
				dynamicPlot.postRedraw();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Expected to be in double[]
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
		
		private int max( double[] list )
		{
			int result = 0;
			for( int i = 0; i < list.length; i++ )
			{
				if( list[i] > (double)result )
					result = (int)list[i] + 1;
			}
			return result;
		}
		
		private int max( Number[] list )
		{
			int result = 0;
			for( int i = 0; i < list.length; i++ )
			{
				if( list[i].intValue() >  result )
					result = list[i].intValue() + 1;
			}
			return result;
		}
		
		private int min( Number[] list )
		{
			int result = 0;
			for( int i = 0; i < list.length; i++ )
			{
				if( list[i].intValue() <  result )
					result = list[i].intValue() + 1;
			}
			return result;
		}
		
		private double[] conv16BitData( byte[] data )
		{
			double[] result;
			if( data.length % 2 == 0) // only if even
			{
				int n = data.length/2;
				result = new double[n];
				
				for( int i = 0; i < n; i++ )
					result[i] = twoBytes2Double(data[i*2], data[i*2 + 1]);
				return result;
			}
			else
				return new double[-1];
		}
		
		private double[] convShort2Double( short[] data )
		{
			double[] result = new double[data.length];
			for( int i = 0; i < data.length; i++ )
				result[i] = (double)data[i];
			
			return result;
		}
		
		private double twoBytes2Double(byte a, byte b )
		{
			short A = (short) (a & 0x00FF); // unsign them...
			short B = (short) (b & 0x00FF);
			return (double)A + B; 
		}
    }
	
    private XYPlot dynamicPlot = null;
    private mPlotUpdater plotUpdater;
    private SimpleXYSeries audioHistSeries = new SimpleXYSeries("Audio");
    private LinkedList<Number> audioHist = new LinkedList<Number>();
    private final int HISTORY_SIZE = 64;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.dynamicPlot);
 
        // sets up the plot that will be updated
        plotUpdater = new mPlotUpdater(dynamicPlot);

        // start a new instance of a class that will generate data
        //dataGen data = new dataGen();
        audioGen audioData = new audioGen();
        
        // hook up the plotUpdater to the data model:
        //data.addObserver(plotUpdater);
        audioData.addObserver( plotUpdater );
        
        // kick off the data generating thread:
        //new Thread(data).start();
        new Thread(audioData).start();
        
    }
    
    protected void alertBox(String title, byte[] myMessage)
    {
    	String convToStr = new String();
    	int maxLen = 15; // max of 15 lines...
    	if ( myMessage.length < maxLen )
    		maxLen = myMessage.length;
    	
    	for( int i = 0; i < maxLen; i++)
    		convToStr += String.valueOf( myMessage[i] ) + "\n";
    	
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
}