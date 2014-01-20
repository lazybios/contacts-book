package com.freshstu;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;

public class DailPlate extends Activity {
	int[] numButtons = { R.id.number0,R.id.number1, R.id.number2, R.id.number3,
			R.id.number4, R.id.number5, R.id.number6, R.id.number7,
			R.id.number8, R.id.number9,R.id.numberstar,R.id.numberpound};
	
	ImageButton delButton = null;
	ImageButton callButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dial_plate);
		
		delButton = (ImageButton)findViewById(R.id.btndelete);
		delButton.setOnClickListener(new OnClickListener(){
			//É¾³ý¼àÌýÆ÷
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView tv = (TextView)findViewById(R.id.numberShow);
				String num =tv.getText().toString();
				num = (num.length()>1)?num.substring(0,num.length()-1):" ";
				tv.setText(num);
			}
			
		});
		
		callButton = (ImageButton)findViewById(R.id.btncall);
		callButton.setOnClickListener(new OnClickListener(){
			//²¦ºÅ¼àÌýÆ÷
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView tv = (TextView)findViewById(R.id.numberShow);
				String num =tv.getText().toString();
				
				Intent intent =new Intent();
                intent.setAction("android.intent.action.CALL");
                intent.setData(Uri.parse("tel://"+num));
				startActivity(intent);
			}});
				
		for(int i:numButtons){
			Button tempb = (Button)findViewById(i); 
			tempb.setOnClickListener(new numberButtonListener());
		}
	}
	
	class numberButtonListener implements OnClickListener{
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//0¡«9*#¼àÌýÆ÷
		TextView tv = (TextView)findViewById(R.id.numberShow);
		//´«ÈëµÄÊÓÍ¼×ª»»Îªbutton
		Button tempbt = (Button)v;
		tv.append(tempbt.getText());
	}
	}
}
