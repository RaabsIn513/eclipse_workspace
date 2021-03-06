package senior.project;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Settings_Activity extends Activity{
	
	private static String SAS_Settings = "SAS_SettingsFile";
	private static SharedPreferences SettingsFile;
	private static ArrayAdapter ad;
	private static ListView settingList;
	
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main_settings);
				
	    settingList = (ListView) findViewById(R.id.listSAS_settings);
	    loadSettingsFile();
	    
	    // When user selects list item
	    settingList.setOnItemClickListener(new OnItemClickListener(){
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	    	{
	    		String selected = ((TextView) view).getText().toString();
	    		String strVal = selected.intern();
	    		int currentVal = 0;
	    		strVal = strVal.substring(strVal.indexOf("- ") + 2);
	    		selected = selected.substring(0, selected.indexOf(" "));
	    		selected = selected.intern();
	    		
	    		try{
	    			currentVal = Integer.valueOf(strVal);
	    		}
	    		catch(Exception ex)
	    		{
	    			ex.getMessage();
	    		}
	    		
	    		
	    		// Start integer chooser activity
	    	    Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
	    	              Toast.LENGTH_SHORT).show();    
	    	    
	    	    if( selected == "SAMPLE_RATE" )
	    	    {
	    	    	// SAMPLE_RATE will use a list of sample rate options
	    	    	Intent intent = new Intent(view.getContext(), Integer_Chooser_Activity.class);
	    	    	intent.putExtra("NAME", 	 "SAMPLE_RATE");
	    	    	intent.putExtra("INTLIST",   "8000, 11025, 22050, 44100");
	    	    	intent.putExtra("ORIGINAL",  currentVal);
	    	    	intent.putExtra("MIN_VALUE", 8000);
	    	    	intent.putExtra("MAX_VALUE", 44100);
	    	    	
	    	    	startActivityForResult(intent, position);
	    	    }
	    	    if( selected == "SAMP_HISTORY_SIZE" )
	    	    {
	    	    	Intent intent = new Intent(view.getContext(), Integer_Chooser_Activity.class);
	    	    	intent.putExtra("NAME", "SAMP_HISTORY_SIZE");
	    	    	intent.putExtra("CREMENT_BY", "x2" );
	    	    	intent.putExtra("ORIGINAL",  currentVal);
	    	    	intent.putExtra("MIN_VALUE", 16);
	    	    	intent.putExtra("MAX_VALUE", 128);
	    	    	
	    	    	startActivityForResult(intent, position);
	    	    }
	    	    if( selected == "AMP_HIST_SIZE" )
	    	    {
	    	    	Intent intent = new Intent(view.getContext(), Integer_Chooser_Activity.class);
	    	    	intent.putExtra("NAME", "AMP_HIST_SIZE");
	    	    	intent.putExtra("CREMENT_BY", "1");
	    	    	intent.putExtra("ORIGINAL",  currentVal);
	    	    	intent.putExtra("MIN_VALUE", 8);
	    	    	intent.putExtra("MAX_VALUE", 128);
	    	    	
	    	    	startActivityForResult(intent, position);	    	    	
	    	    }
	    	    if( selected == "THRESH_DIFF" )
	    	    {
	    	    	Intent intent = new Intent(view.getContext(), Integer_Chooser_Activity.class);
	    	    	intent.putExtra("NAME", "THRESH_DIFF");
	    	    	intent.putExtra("CREMENT_BY", "1");
	    	    	intent.putExtra("ORIGINAL",  currentVal);
	    	    	intent.putExtra("MIN_VALUE", 8);
	    	    	intent.putExtra("MAX_VALUE", 128);
	    	    	
	    	    	startActivityForResult(intent, position);	    	    	
	    	    }
	    	    if( selected == "THRESH_SAMPS" )
	    	    {
	    	    	Intent intent = new Intent(view.getContext(), Integer_Chooser_Activity.class);
	    	    	intent.putExtra("NAME", "THRESH_SAMPS");
	    	    	intent.putExtra("CREMENT_BY", "1");
	    	    	intent.putExtra("ORIGINAL",  currentVal);
	    	    	intent.putExtra("MIN_VALUE", 8);
	    	    	intent.putExtra("MAX_VALUE", 128);
	    	    	
	    	    	startActivityForResult(intent, position);	    	    	
	    	    }
	    	}
	    });
	}
	
	public boolean refreshList()
	{
		SettingsFile = getSharedPreferences(SAS_Settings, 0);
		// cast table with key & value as strings
		try{
			Map<String,?> settingTable =  SettingsFile.getAll();
			Object[] okeys = settingTable.keySet().toArray();
			Object[] ovals = settingTable.values().toArray();
			String[] keys = new String[okeys.length];
			String[] vals = new String[ovals.length];
			
			keys = Obj_to_Str(okeys);
			vals = Obj_to_Str(ovals);
			
			String[] dispSets = new String[vals.length];
			
			for( int i = 0; i < dispSets.length; i++ )
				dispSets[i] = keys[i] + " - " + vals[i];	
			
			ad = new ArrayAdapter<Object>(this, 
					android.R.layout.simple_expandable_list_item_1, dispSets);
			settingList.setAdapter(ad);
			
			return true;
		}
		catch(Exception ex)
		{return false;}
	}
	
	public boolean loadSettingsFile()
	{
		SettingsFile = getSharedPreferences(SAS_Settings, 0);
		// cast table with key & value as strings
		try{
			Map<String,?> settingTable =  SettingsFile.getAll();
			Object[] okeys = settingTable.keySet().toArray();
			Object[] ovals = settingTable.values().toArray();
			String[] keys = new String[okeys.length];
			String[] vals = new String[ovals.length];
			
			keys = Obj_to_Str(okeys);
			vals = Obj_to_Str(ovals);
			
			String[] dispSets = new String[vals.length];
			
			for( int i = 0; i < dispSets.length; i++ )
				dispSets[i] = keys[i] + " - " + vals[i];			
	
			// If there are no setting values (ie first time ran) provide the 
			// default settings and values.
			if( vals.length == 0)
			{
				int numSettings = 3;
				dispSets = new String[numSettings];
				vals = new String[numSettings];
				keys = new String[numSettings];
				SharedPreferences.Editor edSet = SettingsFile.edit();
				
				edSet.putString("SAMPLE_RATE", "8000");
				edSet.putString("SAMP_HISTORY_SIZE", "64");
				edSet.putString("AMP_HIST_SIZE", "32");
				edSet.putString("THRESH_DIFF", "50");		// (current_max_amp) - (avg_max_amp)
				edSet.putString("THRESH_SAMPS", "16");		// The number of consecutive samples where 
															// (current_max_amp) - (avg_max_amp) > THRESH_DIFF 
															// before causing an event trigger
				edSet.commit();
			}
						
			ad = new ArrayAdapter<Object>(this, 
					android.R.layout.simple_expandable_list_item_1, dispSets);
			settingList.setAdapter(ad);
			return true;
		}catch(Exception ex)
		{
			Toast.makeText(getBaseContext(), ex.getMessage(), 1500).show();
			return false;
		}
	
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	   if (resultCode == RESULT_OK) 
    	   {
    		   SettingsFile = getSharedPreferences(SAS_Settings, 0);
    		   SharedPreferences.Editor edSet = SettingsFile.edit();
    		   
    		   // Get the key and value back
    		   String key = data.getExtras().getString("NAME");
    		   int value = data.getExtras().getInt("VALUE");
    		   edSet.putInt(key, value); // put it in our settings file
    		   edSet.commit();			 // save it
    		   refreshList();			 // refresh our display
    	   }
    	}
	
    private String[] Obj_to_Str(Object[] ary)
    {
    	String[] result = new String[ary.length];
    	for( int i = 0; i < ary.length; i++)
    	{
    		try{
    			result[i] = ary[i].toString();
    		}
    		catch( Exception ex)
    		{
    			Toast.makeText(this, ex.getMessage(), 300);
    		}
    	}
    	return result;
    }
}
