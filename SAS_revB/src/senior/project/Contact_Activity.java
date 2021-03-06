package senior.project;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Contact_Activity extends Activity {

	public static Intent retData = new Intent();
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_menu);    
        
        Button btnOKAY     = (Button) findViewById(R.id.btnOKAY);
        final EditText etName    = (EditText) findViewById(R.id.txtBoxName);
        final EditText etNumber  = (EditText) findViewById(R.id.txtBoxNumber);
        
        Bundle inData = this.getIntent().getExtras();
        
        // Always try to see if we're editing an entry, if we are the fields will be filled out
        try{
        	etName.setText(inData.getString("NAME"));
        	etNumber.setText(inData.getString("NUMBER"));
        }
        catch(Exception ex)
        {}
        
        // When user clicks ok, validate fields and package data into retData
        btnOKAY.setOnClickListener(new OnClickListener(){
        	public void onClick(View v )
        	{      		
        		if( etName.getText().length() > 0 && etNumber.getText().length() >= 7 )
        		{
        			String name = etName.getText().toString();
        			String number = etNumber.getText().toString();
        			// no format what so ever to the phone number
        			try{
        				number = number.replace("-", "");
        				number = number.replace("(", "");
        				number = number.replace(")", "");
        			}
        			catch(Exception ex)
        			{}
        			
        			retData = new Intent();
        			
        			retData.putExtra("NAME", name);
        			retData.putExtra("NUMBER", number);
        			
        			setResult(	Activity.RESULT_OK, retData);	// set the result/data
        			finish();									// return to previous activity
        		}
        		else
        		{
        			Toast.makeText(v.getContext(), "Unable to accept contact, there must" +
        					" be at least one letter in the name field and " +
        					" 7 digits in the phone number field", 1500).show();
        			setResult( Activity.RESULT_CANCELED, retData);
        		}
        	}
        });        
    }

}
