package senior.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SMS_Settings extends Activity {

	LinkedList<String> PhoneNos = new LinkedList<String>();
	ArrayAdapter<Object> ad;
	ListView Numbers;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_sms_settings);   
        
        // Form objects
        Button addToList     = (Button) findViewById(R.id.btnAddToList);
        final EditText phoneNumber = (EditText) findViewById(R.id.txtBoxNumber);
        Numbers    				   = (ListView) findViewById(R.id.listNumbers);
        
        PhoneNos.add("5551234567");
        PhoneNos.add("5551001000");
        
		//ArrayAdapter<Object> ad = new ArrayAdapter<Object>(this, 
		//		android.R.layout.simple_expandable_list_item_1, PhoneNos.toArray());
		ad = new ArrayAdapter<Object>(this, 
				android.R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.names));
        Numbers.setAdapter(ad);
        registerForContextMenu(findViewById(R.id.listNumbers));
        	
        // Listeners 
        
        addToList.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		if( phoneNumber.getText() != null )
        		{
    				
        		}
        	}
        });
       
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
    	String[] names = getResources().getStringArray(R.array.names);
    	switch(item.getItemId()) {
    	case R.id.edit:
    		Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.edit) + 
    				" context menu option for " + names[(int)info.id],
            		Toast.LENGTH_SHORT).show();
    		return true;
    	case R.id.save:
    		Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.save) + 
    				" context menu option for " + names[(int)info.id],
            		Toast.LENGTH_SHORT).show();
    		return true;
    	case R.id.delete:
    		File file = new File(, R.array.names);
    		Numbers.removeViews(indexOf(names, names[(int)info.id]), 1);
    		InputStream is = getResources().openRawResource(R.array.names);
    		OutputStream os = new FileOutputStream(getResources().);
    		return true;
    	case R.id.view:
    		Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.view) + 
    				" context menu option for " + names[(int)info.id],
            		Toast.LENGTH_SHORT).show();
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
    
    private int indexOf(String[] strAry, String toFind )
    {
    	for( int i = 0; i < strAry.length; i++)
    		if( strAry[i] == toFind)
    			return i;
    	
    	return -1;
    }
}


