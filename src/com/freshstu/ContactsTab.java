package com.freshstu;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class ContactsTab extends TabActivity {
	TabHost tabHost = null;
	Intent intent = null;
	TabHost.TabSpec spec = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablist);
		Resources res = getResources();
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		intent = new Intent().setClass(this, ContactsBookActivity.class);
		spec = tabHost
				.newTabSpec("��ϵ��")
				.setIndicator("��ϵ��",
						res.getDrawable(R.drawable.tab_icon_state))
				.setContent(intent);
		tabHost.addTab(spec);
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		intent = new Intent().setClass(this, DailPlate.class);
		spec = tabHost
				.newTabSpec("������")
				.setIndicator("������",
						res.getDrawable(R.drawable.tab_icon_state))
				.setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
	}
	
}
