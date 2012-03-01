package senior.project;

import java.util.Map;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

public class Send_SMS_Activity extends Activity {
	
	private static String SAS_Settings = "SAS_SettingsFile";
	private static SharedPreferences SettingsFile;
	private static String SAS_Contacts = "SAS_ContactsFile";
	private static SharedPreferences ContactsFile;
	private static int AMP_HISTORY_SIZE;
	private static int THRESH_SAMPS;
	private static int THRESH_DIFF;
	private static int SAMP_HISTORY_SIZE;
	private static int SAMPLE_RATE;
	private static Context parentContext; 
	
	public static boolean sendAlertSMS( String[] info, Context con )
	{
		String[] message = new String[info.length + 5];
	    try{
	    	
	    	// Get some background info that will always be sent. 
		    SettingsFile = con.getSharedPreferences(SAS_Settings, 0);
		    AMP_HISTORY_SIZE = Integer.valueOf(SettingsFile.getInt("AMP_HISTORY_SIZE", 64));
		    THRESH_SAMPS = Integer.valueOf(SettingsFile.getInt("THRESH_SAMPS", 20));
		    THRESH_DIFF = Integer.valueOf(SettingsFile.getInt("THRESH_DIFF", 20));
		    SAMP_HISTORY_SIZE = Integer.valueOf(SettingsFile.getInt("SAMP_HISTORY_SIZE", 64));
		    SAMPLE_RATE = Integer.valueOf(SettingsFile.getInt("SAMP_HISTORY_SIZE", 8000));
		    String[] contacts = retrieveSASContacts(con);
		    
			message[0] = "AMP_HISTORY_SIZE: " + AMP_HISTORY_SIZE;
			message[1] = "THRESH_SAMPS: " + THRESH_SAMPS;
			message[2] = "THRESH_DIFF: " + THRESH_DIFF;
			message[3] = "SAMP_HISTORY_SIZE: " + SAMP_HISTORY_SIZE;
			message[4] = "SAMPLE_RATE: " + SAMPLE_RATE;
		   	// Build message
		    for( int i = 5; i < message.length; i++ )
		    	message[i] = info[i-5];
		    
		    if( sendSMS(message, contacts, con))
		    	return true;
		    else
		    	return false;
		    
	    }
	    catch( Exception ex )
	    {
	    	ex.getMessage();
	    	
	    	return false;
	    }
	}

    @SuppressWarnings("deprecation")
	public static boolean sendSMS( String phoneNumber, String message, Context con )
    {       
        PendingIntent pi = PendingIntent.getActivity(con, 0,
            new Intent(con, Send_SMS_Activity.class), 0);                
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);        
        
        return true;
    }  
    
	public static boolean sendSMS( String TXTmessage, String[] recipients, Context con)
	{
		for( int i = 0; i < recipients.length; i++ )
			sendSMS( recipients[i], TXTmessage, con );
		
		return true;
	}
	
	public static boolean sendSMS( String[] TXTmessage, String[] recipients, Context con )
	{
		String message = null;
		for( int i = 0; i < TXTmessage.length; i++ )
			message += TXTmessage[i] + "\n";
		for( int i = 0; i < recipients.length; i++ )
			sendSMS( recipients[i], message, con );
		
		return true;
	}
		
    private static String[] Obj_to_Str(Object[] ary)
    {
    	String[] result = new String[ary.length];
    	for( int i = 0; i < ary.length; i++)
    	{
    		try{
    			result[i] = ary[i].toString();
    		}
    		catch( Exception ex)
    		{
    			ex.getMessage();
    		}
    	}
    	return result;
    }
	
	private static String[] retrieveSASContacts(Context con)
	{
	    ContactsFile = con.getSharedPreferences(SAS_Contacts, 0);
		Map<String,?> rawCont;
		String[] keys = null;
		String[] result = null;
		// build array to show in listView
		try{
			rawCont = ContactsFile.getAll();
			
			Object[] okeys = rawCont.keySet().toArray();
			Object[] ovals = rawCont.values().toArray();
			
			keys = Obj_to_Str(okeys);
			result = Obj_to_Str(ovals);
		}
		catch(Exception ex)
		{}
	    return result;
	}

	
}
