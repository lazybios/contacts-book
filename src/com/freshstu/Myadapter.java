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
	//该变量用意何在？ 复选框时使用，暂时不用
	private boolean[] hasChecked;

	//内部类，封装了每一项需要操作UI控件
	private final class ListItemView{
		public ImageView avatorIMG;
		public TextView  contactName;
	}
	
	//	构造函数，传入待操作数据
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
			//第一次绘制行
			listItemView = new ListItemView();
			
			convertView = listContainer.inflate(R.layout.list_content,null);
			
			listItemView.avatorIMG = (ImageView)convertView.findViewById(R.id.avator);
			listItemView.contactName =(TextView)convertView.findViewById(R.id.person_name);
			
			//设置标签为之后绘制，寻找方便
			convertView.setTag(listItemView);			
		}else{
			//优化之后的绘制工作
			listItemView = (ListItemView)convertView.getTag();
		}
		
		listItemView.avatorIMG.setImageBitmap((Bitmap)listItems.get(position).get("avator"));
		listItemView.contactName.setText((String)listItems.get(position).get("name"));

		//剩余可以为控件设置监听器之类的东西		
		return convertView;
	}
	


}
