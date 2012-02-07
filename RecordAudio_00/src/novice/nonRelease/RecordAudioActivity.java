package novice.nonRelease;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class RecordAudioActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void startStopRec(View view)
    {
    	// Create new Android ToggleButton object 
    	// and bind it to the toggleButton1 object in the display
    	final ToggleButton toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
    	// simple
    	if( toggleButton1.isChecked())
    	{
    		//alertBox( "Checked", "The button in ON" );
	      	
    		SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
	    	
	    	List<Sensor> sensorsList = sm.getSensorList(Sensor.TYPE_ALL);
	    	
	    	if( sensorsList.size() > 0 )
	    		alertBox("SensorList:", sensorsList.toArray());  		
    	
    	}
    	else if( !toggleButton1.isChecked() )
    	{
    		alertBox( "Unchecked", "The button is OFF");   
    		
    		recordAudio(500);
    	}
    	

    }
    
    
    protected boolean recordAudio( long miliseconds )
    {
    	android.media.MediaRecorder mr;
		try{
			mr = new android.media.MediaRecorder();
			mr.setAudioSource( android.media.MediaRecorder.AudioSource.MIC );
			mr.setOutputFormat( android.media.MediaRecorder.OutputFormat.MPEG_4);
			mr.setAudioEncoder( android.media.MediaRecorder.AudioEncoder.AAC);
			try 
			{
				// THROWS ERROR HERE. TODO/FIXME: Invalid storage file
				// Determine correct methods... etc. 
				mr.prepare();
				mr.start();
				
				java.util.Timer waitFor = new java.util.Timer();
				
				try {
					waitFor.wait(miliseconds);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mr.stop();
			}
			catch(Exception e){
				alertBox("Error", "Couldn't mr.prepare()");
				alertBox("Error:", e.toString());
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		mr.release();  
		return true;
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
    
    protected void alertBox(String title, Object[] myMessage)
    {
    	String convToStr = new String();
    	
    	for( int i = 0; i < myMessage.length; i++)
    		convToStr += myMessage[i].toString() + "\n";
    	
    	
	    new AlertDialog.Builder(this)
	       .setMessage(convToStr)
	       .setTitle(title)
	       .setCancelable(true)
	       .setNeutralButton(android.R.string.cancel,
	          new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int whichButton){}
	          })
	       .show();
    }
}