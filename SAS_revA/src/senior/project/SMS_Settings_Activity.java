package senior.project;

import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SMS_Settings_Activity extends Activity {

	LinkedList<String> PhoneNos = new LinkedList<String>();
	ArrayAdapter<Object> ad;
	ListView Numbers;
	public static String SAS_Contacts = "SAS_ContactsFile";
	private SharedPreferences ContactsFile;
	
	private int ADD_REQUEST = 0;
	private int EDIT_REQUEST = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_sms_settings);   
        
        // Form objects
        final EditText phoneNumber = (EditText) findViewById(R.id.txtBoxNumber);
        Numbers    				   = (ListView) findViewById(R.id.listNumbers);
        
        restoreContactsList();
        registerForContextMenu(findViewById(R.id.listNumbers));

    }
    
    // Show the context menu we created in context_menu.xml
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
      }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
		ContactsFile = getSharedPreferences(SAS_Contacts, 0);
		Map<String,?> rawCont;
		String[] keys = null;
		String[] vals = null;
		// build array to show in listView
		try{
			rawCont = ContactsFile.getAll();
			
			Object[] okeys = rawCont.keySet().toArray();
			Object[] ovals = rawCont.values().toArray();
			
			keys = Obj_to_Str(okeys);
			vals = Obj_to_Str(ovals);
		}
		catch(Exception ex)
		{}
    	
    	switch(item.getItemId()) {
    	case R.id.edit:
    		Intent editIntent = new Intent(Numbers.getContext(), Contact_Activity.class);
    		// Pass what we already 
    		editIntent.putExtra("NAME", keys[(int)info.id]);
    		editIntent.putExtra("NUMBER", vals[(int)info.id]);
    		startActivityForResult(editIntent, EDIT_REQUEST);
    		return true;
    	case R.id.addnew:	// Launches new ContactActivity
    		Intent addIntent = new Intent(Numbers.getContext(), Contact_Activity.class);
    		startActivityForResult(addIntent, ADD_REQUEST);
    		return true;
    	case R.id.delete:
    		Numbers.removeViews((int)info.id, 1);
    		deleteContact(keys[(int)info.id]);
    		return true;
    	case R.id.view:
    		Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.view) + 
    				" context menu option for " + keys[(int)info.id],
            		Toast.LENGTH_SHORT).show();
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
    
    public boolean restoreContactsList()
    {
		ContactsFile = getSharedPreferences(SAS_Contacts, 0);
		Map<String,?> rawCont;
		String[] keys;
		String[] vals;
		// build array to show in listView
		try{
			rawCont = ContactsFile.getAll();
			
			Object[] okeys = rawCont.keySet().toArray();
			Object[] ovals = rawCont.values().toArray();
			
			keys = Obj_to_Str(okeys);
			vals = Obj_to_Str(ovals);
			
			String[] dispVals = new String[vals.length];
			
			for( int i = 0; i < dispVals.length; i++ )
				dispVals[i] = keys[i] + " - " + vals[i];			
			
			if( vals.length == 0)
			{
				dispVals = new String[1];
				dispVals[0] = "No contacts have been added";
			}
						
			ad = new ArrayAdapter<Object>(this, 
					android.R.layout.simple_expandable_list_item_1, dispVals);
	        Numbers.setAdapter(ad);
		}
		catch(NullPointerException NP)
		{
			Toast.makeText(this, NP.getMessage(), 400).show();
		}
        return true;
    }
    
    public boolean editContact(String Name, String Number)
    {
    	deleteContact(Name);
    	addContact(Name, Number);
    	return true;
    }

    public boolean addContact(String Name, String Number)
    {
    	ContactsFile = getSharedPreferences(SAS_Contacts, 0);
    	SharedPreferences.Editor contEd = ContactsFile.edit();
    	contEd.putString(Name, Number);
    	contEd.commit();
    	
    	restoreContactsList();    	
    	
    	return true;
    }
    
    public boolean deleteContact( String Name )
    {
    	ContactsFile = getSharedPreferences(SAS_Contacts, 0);
    	SharedPreferences.Editor contEd = ContactsFile.edit();
    	contEd.remove(Name);
    	contEd.commit();
    	
    	restoreContactsList();    	
    	
    	return true;
    }
    
    @Override
    public void onBackPressed() {
    	finish();
    return;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	   if (resultCode == RESULT_OK) {
    	      if (requestCode == ADD_REQUEST) {
    	    	  addContact( data.getExtras().getString("NAME"), data.getExtras().getString("NUMBER"));
    	      }
    	      if (requestCode == EDIT_REQUEST) {
    	    	  editContact( data.getExtras().getString("NAME"), data.getExtras().getString("NUMBER"));
    	      }
    	   }
    	}
    
}


