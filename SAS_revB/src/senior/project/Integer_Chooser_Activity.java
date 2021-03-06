package senior.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


/*-----------------------------------------------------*\
 * Android Activity: Integer_Chooser_Activiy.java
 * 
 * REQUIRED EXTRAS (Passed in values):
 * NAME		  - The name of the value that is changing
 * ORIGINAL   - The original value which is used as the
 * 			    starting number when the activity launches
 * MAX_VALUE  - The max value that is allowed to be used
 * MIN_VALUE  - The min value that is allowed to be used
 * 
 * CREMENT_BY - increment/decrement by value. This is a
 * 				string so that you can specify between
 * 				addition and multiplication. It is 
 * 				parsed
 * NOT REQUIRED EXTRAS
 * INTLIST 	  - Comma separated string containing the
 * 				integers in ascending order. 
 * 
 \*----------------------------------------------------*/
public class Integer_Chooser_Activity extends Activity{
	
	private static TextView dispVal;
	private static String crementBy = null;
	private static String nameReq = null;
	private static String DESC_TEXT = null;
	private static int value = 0;
	private static int Max = 9999;
	private static int Min = -9999;
	private static String byList = null;
	private static int[] intList = null;
	private static String[] strList = null;
	private static int listIndex = 0;
	private static String operation = null;
	private static int crementVal = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.integer_chooser);
	    TextView desc = (TextView) findViewById(R.id.txtDesc);
	    // incoming values
	    try{
	    	nameReq	  = this.getIntent().getExtras().getString("NAME");
		    crementBy = this.getIntent().getExtras().getString("CREMENT_BY");
		    value     = this.getIntent().getExtras().getInt("ORIGINAL");
		    Max       = this.getIntent().getExtras().getInt("MAX_VALUE");
		    Min       = this.getIntent().getExtras().getInt("MIN_VALUE");
		    byList	  = this.getIntent().getExtras().getString("INTLIST");
		    DESC_TEXT= this.getIntent().getExtras().getString("DESC_TEXT");
		    
		    desc.setText(DESC_TEXT);
		    if( crementBy != null )	// parse crementBy to a usable int and operation
		    {
		    	crementBy = crementBy.toLowerCase();
		    	if( crementBy.contains("*") || crementBy.contains("x") )
		    	{
		    		crementBy = crementBy.replace("*", "x");
		    		operation = "multiply";
		    		String temp = crementBy.substring(crementBy.indexOf("x")+1);
		    		try
		    		{
		    			crementVal = Integer.valueOf(temp);
		    		}
		    		catch(Exception ex)
		    		{
		    			ex.getMessage();
		    		}
		    	}
		    	else
		    	{
		    		operation = "addition";
		    		try
		    		{
		    			crementVal = Integer.valueOf(crementBy);
		    		}
		    		catch(Exception ex)
		    		{
		    			ex.getMessage();
		    		}
		    	}
		    }
		    else if( byList != null )
		    {
		    	strList = byList.split(",");
		    	intList = new int[strList.length];
		    	for( int i = 0; i < strList.length; i++)
		    		intList[i] = Integer.valueOf(strList[i].trim());
		    	listIndex = indexOf( intList, value );
		    	
		    }
		    else
		    	crementVal = 1;
	    }
	    catch(Exception ex)
	    {}
	    
	    Button UP = (Button) findViewById(R.id.btn_numUp);
	    Button DN = (Button) findViewById(R.id.btn_numDn);
	    dispVal = (TextView) findViewById(R.id.intValue);
	    dispVal.setText(String.valueOf(value));
	    
	    UP.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v)
	    	{
	    		if( byList != null && (listIndex >= 0) && (listIndex < strList.length -1 ) )
	    		{
	    			listIndex++;
	    			value = intList[listIndex];
	    		}
	    		else
	    		{
	    		if( value < Max )
	    		{
    				if( operation == "multiply")
    					value = value * crementVal;
    				if( operation == "addition")
    					value = value + crementVal;
	    		}
	    		}
	    		dispVal.setText(String.valueOf(value));
	    	}
	    });

	    DN.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v)
	    	{	
	    		if( byList != null && (listIndex > 0) && (listIndex) <= (strList.length) )
	    		{
	    			listIndex--;
	    			value = intList[listIndex];
	    		}
	    		else
	    		{
	    			if( value > Min )
	    			{
	    				if( operation == "multiply")
	    					value = value / crementVal;
	    				if( operation == "addition")
	    					value = value - crementVal;
	    			}
	    		}
	    		dispVal.setText(String.valueOf(value));
	    	}
	    });
	}

    @Override
    public void onBackPressed() {
    	Intent retData = new Intent();
		
		retData.putExtra("VALUE", value);
		retData.putExtra("NAME", nameReq);
		
		setResult( Activity.RESULT_OK, retData );	// set the result/data
		finish();									// return to previous activity
    return;
    }
    
    public int indexOf( int[] list, int search )
    {
    	int result = -1;
    	
    	for( int i =0; i < list.length; i++ )
    	{
    		if( list[i] == search )
    			result = i;
    	}
    	return result;
    }
}
