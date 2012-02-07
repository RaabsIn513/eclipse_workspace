package com.example.Layout_Play;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Layout_PlayActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button b; 
        b = (Button)findViewById(R.id.myButton);
        
        // Build alert dialog...
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setMessage("You clicked this button")
        	.setCancelable(false)
        	.setTitle("The Title!")
        	.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
        final AlertDialog alert = builder.create();
        
        // connect the button to the alert message...
         b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alert.show();
			}
		});
    }
}