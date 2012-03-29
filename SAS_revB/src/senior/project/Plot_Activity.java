package senior.project;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.SimpleXYSeries.ArrayFormat;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

public class Plot_Activity extends Activity {
	
    // redraws a plot whenever an update is received:
    private class mPlotUpdater implements Observer {
        XYPlot plot;
        // Constructor sets up the series
        public mPlotUpdater(XYPlot plot) {
            this.plot = plot;
            // only display whole numbers in domain labels
            plot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
            plot.getGraphWidget().setRangeValueFormat(new DecimalFormat("0"));
            
            // Line plot
            if( dispForm == "WAVE")
            {
                LineAndPointFormatter maxFormat = new LineAndPointFormatter(
                        Color.rgb(0, 25, 250),      // line color
                        Color.rgb(0, 25, 250),      // point color
                        null );              	    // fill color (optional)
                
                LineAndPointFormatter minFormat = new LineAndPointFormatter(
                        Color.rgb(250, 25, 0),      // line color
                        Color.rgb(250, 25, 0),      // point color
                        null );              	    // fill color (optional)
            	
	            LineAndPointFormatter audioFormat = new LineAndPointFormatter(
	                    Color.rgb(25, 250, 0),     // line color
	                    Color.rgb(25, 250, 0),      // point color
	                    null );              	   // fill color (optional)
	            
	            plot.addSeries(audioHistSeries, audioFormat);
	            plot.addSeries(maxSeries, maxFormat);
	            plot.addSeries(minSeries, minFormat);
	            plot.setDomainBoundaries(0, SAMP_HISTORY_SIZE, BoundaryMode.FIXED);
            }
            // Bar plot
            if( dispForm == "FFT")
            {
            	plot.addSeries(audioHistSeries, BarRenderer.class, new BarFormatter(Color.argb(100, 0, 200, 0), Color.rgb(0, 80, 0)));
            }
            
            // thin out domain/range tick labels so they don't overlap each other:
            plot.setPlotMarginLeft(0);
            plot.setGridPadding(2, 2, 2, 0);
            plot.setTicksPerDomainLabel(3);
            plot.setTicksPerRangeLabel(3);
            plot.disableAllMarkup();
     
            // freeze the range boundaries:            
            plot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
            plot.setDomainStepValue(1);
            plot.setTicksPerRangeLabel(3);
            if( dispForm == "WAVE")
            	plot.setDomainLabel("Time");
            if( dispForm == "FFT ")
            	plot.setDomainLabel("Frequency");
            plot.getDomainLabelWidget().pack();
            plot.setRangeLabel("Amplitude");
            plot.getRangeLabelWidget().pack();
            plot.disableAllMarkup();
        }

		@Override
        public void update(Observable o, Object arg) {

			// cast the incoming arg object as a audioGen object
			audioGen income = (audioGen)arg;	
			
			// raw mic data
			short[] raw = income.getShorts();		
			double[] data = convShort2Double(raw);
			dispForm = dispForm.toString();
			dispForm = dispForm.intern();
			if( dispForm == "FFT" )
				//sectionalFFT( data );
				FFT( data );
			if( dispForm == "WAVE" )
				waveform( data );
			
        }
		
		private void FFT( double[] input )
		{
			AppLog.APP_TAG = "sectionalFFT";
			Number[] graphable = new Number[input.length/2];
			graphable = fft_mag(input);
			audioHist.clear();
			
			// space out the frequency info gained from the fft_mag and put it into audioHist
			int k = graphable.length / SAMP_HISTORY_SIZE;
			int j = 0;
			for( int i = 0; i < SAMP_HISTORY_SIZE; i++ )
			{
				audioHist.add(i, graphable[j]);
				j = j + k;
			}
			// adjust the Range (y axis) to be as long as it needs to be. 
			//plot.setRangeBoundaries(0, max(graphable), BoundaryMode.FIXED);
			plot.setRangeBoundaries(30, 300, BoundaryMode.FIXED);
			// update the graph. 
			audioHistSeries.setModel(audioHist, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
			plot.setDomainBoundaries(0, SAMP_HISTORY_SIZE, BoundaryMode.FIXED);
			try {
				dynamicPlot.postRedraw();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		private void waveform( double[] input )
		{
			if( audioHist.size() >= SAMP_HISTORY_SIZE)
				audioHist.clear();
		
			double Max = max(input);
			//double Min = min(input);
			SumHigh = manageHighSum( Max );
			double avg = SumHigh / AMP_HIST_SIZE;
					
			if( ( Max - avg ) > THRESH_DIFF )
			{		
				THRESH_CNT += 1;
				if( THRESH_CNT >= THRESH_SAMPS )
				{	
					//mTrigger = true;
					THRESH_CNT = 0; 	// Reset the THRESH_CNT
		        	AppLog.APP_TAG = "TRIGGERin";
		        	AppLog.logString("Actual Difference: " + String.valueOf(Max - avg));
				}
			}
			else
				THRESH_CNT = 0;			// Reset the THRESH_CNT back to zero since we want consecutive samples	
			
			// load the series. 
			for( int i = 0; i < SAMP_HISTORY_SIZE; i++)
				audioHist.addLast(input[i]);	
			
			graphLine(0, avg, SAMP_HISTORY_SIZE, avg, maxSeries);
			graphLine(0, Max, SAMP_HISTORY_SIZE, Max, minSeries);
			audioHistSeries.setModel(audioHist, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
			dynamicPlot.setRangeBoundaries(-30000, 30000, BoundaryMode.FIXED);
			try {
				dynamicPlot.postRedraw();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}

		private void graphLine( Number X1, Number Y1, Number X2, Number Y2, SimpleXYSeries ser )
		{
			LinkedList<Number> points = new LinkedList<Number>();
			points.add(X1);
			points.add(Y1);
			points.add(X2);
			points.add(Y2);

			ser.setModel(points, ArrayFormat.XY_VALS_INTERLEAVED);
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
			if( ampHist.size() < AMP_HIST_SIZE )
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
	
    private XYPlot dynamicPlot = null;
    private mPlotUpdater plotUpdater;
    private SimpleXYSeries audioHistSeries = new SimpleXYSeries("Audio");
    private SimpleXYSeries maxSeries       = new SimpleXYSeries("Avg Max");
    private SimpleXYSeries minSeries       = new SimpleXYSeries("Current Max");
    private LinkedList<Double> ampHist     = new LinkedList<Double>();
    private LinkedList<Number> audioHist = new LinkedList<Number>();
    private int SAMPLE_RATE;
    private int SAMP_HISTORY_SIZE = 64;
    public static int AMP_HIST_SIZE = 32;
	private int THRESH_SAMPS;
	private int THRESH_CNT;
	private int THRESH_DIFF;
    public static Double SumHigh = 0.0;
    public static Double SumLow  = 0.0;
    public static String dispForm = "WAVE"; // do wave plot by default if no bunlde is passed in
	public static audioGen audioData;
	public static Thread audioThread;
	private static String SAS_Settings = "SAS_SettingsFile";
	private static SharedPreferences SettingsFile;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_plot);
        SumHigh = 0.0;
	    // Get our settings
	    try{
		    SettingsFile = getSharedPreferences(SAS_Settings, 0);
		    AMP_HIST_SIZE = Integer.valueOf(SettingsFile.getInt("AMP_HIST_SIZE", 32));
		    THRESH_DIFF = Integer.valueOf(SettingsFile.getInt("THRESH_DIFF", 20));
		    THRESH_SAMPS = Integer.valueOf(SettingsFile.getInt("THRESH_SAMPS", 20));
		    SAMP_HISTORY_SIZE = Integer.valueOf(SettingsFile.getInt("SAMP_HISTORY_SIZE", 64));
		    SAMPLE_RATE = Integer.valueOf(SettingsFile.getInt("SAMPLE_RATE", 8000));
	    }
	    catch( Exception ex )
	    {
	    	ex.getMessage();
	    }
        
        // Determine if we shall display the FFT or the wave form plots..
        Bundle passedIn = this.getIntent().getExtras();
        dispForm = passedIn.getString("WaveOrFFT").intern(); // .intern makes it a true string
        
        // get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.dynamicPlot);
        
        // sets up the plot that will be updated
        plotUpdater = new mPlotUpdater(dynamicPlot);

        // start a new instance of a class that will generate data
        audioData = new audioGen(SAMPLE_RATE);
        
        // hook up the plotUpdater to the data model:
        audioData.addObserver( plotUpdater );
        
        // kick off the data generating thread:
        audioThread = new Thread(audioData);
        audioThread.start();
	}
		
    @Override
    public void onBackPressed() {
    	audioData.release();	// release our recorder object
    	// Thread operations aren't support, android takes care of this 
    	// with finish since this activity created this thread, it kills it
    	//audioThread.stop();
    	//audioThread.destroy();
    	finish();
    return;
    }
    
    protected void alertBox(String title, String myMessage)
    {
	    new AlertDialog.Builder(this)
	       .setMessage(myMessage)
	       .setTitle(title)
	       .setCancelable(true)
	       .setNeutralButton(android.R.string.cancel,
	          new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int whichButton){}
	          })
	       .show();
    }

}
