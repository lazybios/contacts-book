package com.freshstu;

import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts;
import android.view.*;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MyCursorAdapter extends CursorAdapter{
	private LayoutInflater layoutInflater;
	private final class ListItemView{
		public ImageView avatorIMG;
		public TextView  contactName;
	}
	
	//查询常量
	private static final int ID_INDEX = 0;
	private static final int DISPLAY_NAME_INDEX = 1;
	private static final int PHOTO_ID_INDEX = 2;
	private static final int HAS_PHONE_NUMBER_INDEX = 3;
	
	public MyCursorAdapter(Context context, Cursor c) {
		super(context, c);
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public CharSequence convertToString (Cursor cursor){
		return cursor==null?"":cursor.getString(DISPLAY_NAME_INDEX);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		TextView contactName =(TextView)view.findViewById(R.id.person_name);
		contactName.setText(cursor.getString(DISPLAY_NAME_INDEX));
		
	}

	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView =new ListItemView();
		View view = layoutInflater.inflate(R.layout.list_content,null);
		listItemView.avatorIMG = (ImageView)view.findViewById(R.id.avator);
		listItemView.contactName =(TextView)view.findViewById(R.id.person_name);
		
		//设置
		
		//listItemView.avatorIMG.setImageBitmap(null);
		listItemView.contactName.setText(cursor.getString(DISPLAY_NAME_INDEX));
		
		return view;
	}

}
