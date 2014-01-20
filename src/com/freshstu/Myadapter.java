package com.freshstu;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

public class Myadapter extends BaseAdapter {
	private Context context; 
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;
	//�ñ���������ڣ� ��ѡ��ʱʹ�ã���ʱ����
	private boolean[] hasChecked;

	//�ڲ��࣬��װ��ÿһ����Ҫ����UI�ؼ�
	private final class ListItemView{
		public ImageView avatorIMG;
		public TextView  contactName;
	}
	
	//	���캯�����������������
	public Myadapter(Context context,List<Map<String,Object>> listItems){
		this.context = context;
		this.listItems = listItems;
		this.listContainer = LayoutInflater.from(context);
	}
	
	/*How many items are in the data set represented by this Adapter.*/
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}
	/*Get the data item associated with the specified position in the data set.*/
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}
	/*Get the row id associated with the specified position in the list.*/
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	/*Get a View that displays the data at the specified position in the data set.*/
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int selectID = position; 
		ListItemView listItemView = null;
		if(convertView == null){
			//��һ�λ�����
			listItemView = new ListItemView();
			
			convertView = listContainer.inflate(R.layout.list_content,null);
			
			listItemView.avatorIMG = (ImageView)convertView.findViewById(R.id.avator);
			listItemView.contactName =(TextView)convertView.findViewById(R.id.person_name);
			
			//���ñ�ǩΪ֮����ƣ�Ѱ�ҷ���
			convertView.setTag(listItemView);			
		}else{
			//�Ż�֮��Ļ��ƹ���
			listItemView = (ListItemView)convertView.getTag();
		}
		
		listItemView.avatorIMG.setImageBitmap((Bitmap)listItems.get(position).get("avator"));
		listItemView.contactName.setText((String)listItems.get(position).get("name"));

		//ʣ�����Ϊ�ؼ����ü�����֮��Ķ���		
		return convertView;
	}
	


}
