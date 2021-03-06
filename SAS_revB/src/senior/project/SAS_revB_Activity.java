package senior.project;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SAS_revB_Activity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Button instances
        Button SettingsBt  = (Button) findViewById(R.id.btnSettings);
        Button waveBt 	   = (Button) findViewById(R.id.wave_plot);
        Button FFTBt  	   = (Button) findViewById(R.id.FFT_plot);
        Button setSMSCont  = (Button) findViewById(R.id.setSMS);
        Button sendSMS     = (Button) findViewById(R.id.sendTestSMS);
        Button opModeBt	   = (Button) findViewById(R.id.btOpMode);
        
        // Listeners
        SettingsBt.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		Intent intent = new Intent(v.getContext(), Settings_Activity.class);
        		startActivity(intent);
        	}
        });
        waveBt.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		Intent intent = new Intent(v.getContext(), Plot_Activity.class);
        		// want the graph to do the wave
        		intent.putExtra("WaveOrFFT", "WAVE");  
        		startActivity(intent);
        	}
        });
        FFTBt.setOnClickListener(new OnClickListener(){
        	public void onClick(View v )
        	{
        		Intent intent = new Intent(v.getContext(), Plot_Activity.class);
        		// want the graph to do FFT
        		intent.putExtra("WaveOrFFT", "FFT");	
        		startActivity(intent);        		
        	}
        });
        setSMSCont.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		Intent intent = new Intent(v.getContext(), SMS_Settings_Activity.class);
        		startActivity(intent);
        	}
        });
        sendSMS.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		//alertBox("TODO!", "This component still needs to be implemented");
        		Intent intent = new Intent(v.getContext(), Test_SMS_Activity.class);
        		startActivity(intent);
        	}
        });
        opModeBt.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		Intent intent = new Intent(v.getContext(), Operational_Mode_Activity.class);
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