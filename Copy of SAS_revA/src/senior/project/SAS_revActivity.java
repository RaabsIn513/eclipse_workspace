package senior.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SAS_revActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Button instances
        Button testButton  = (Button) findViewById(R.id.dog);
        Button waveBt 	   = (Button) findViewById(R.id.wave_plot);
        Button FFTBt  	   = (Button) findViewById(R.id.FFT_plot);
        Button setThreshBt = (Button) findViewById(R.id.setThresh);
        Button setSMSCont  = (Button) findViewById(R.id.setSMS);
        Button sendSMS     = (Button) findViewById(R.id.sendTestSMS);
        
        // Listeners
        testButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		alertBox("Yes hello?", "This is dog");
        	}
        });
        waveBt.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		Intent intent = new Intent(v.getContext(), PlotActivity.class);
        		// want the graph to do the wave
        		intent.putExtra("WaveOrFFT", "WAVE");  
        		startActivity(intent);
        	}
        });
        FFTBt.setOnClickListener(new OnClickListener(){
        	public void onClick(View v )
        	{
        		Intent intent = new Intent(v.getContext(), PlotActivity.class);
        		// want the graph to do FFT
        		intent.putExtra("WaveOrFFT", "FFT");	
        		startActivity(intent);        		
        	}
        });
        setThreshBt.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		alertBox("TODO!", "This component still needs to be implemented");
        	}
        });
        setSMSCont.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		//alertBox("TODO!", "This component still needs to be implemented");
        		Intent intent = new Intent(v.getContext(), SMS_Settings.class);
        		startActivity(intent);
        	}
        });
        sendSMS.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		//alertBox("TODO!", "This component still needs to be implemented");
        		Intent intent = new Intent(v.getContext(), Test_SMS.class);
        		startActivity(intent);
        	}
        });
        
    }
    
    // Good alertBox function
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