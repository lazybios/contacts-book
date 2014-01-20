package com.freshstu;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class PhoneTypeAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;

	// 内部类
	private final class listUiWidget {
		public ImageView iconImg = null;
		public TextView phoneType = null;
		public TextView phoneNumber = null;
	}

	// 构造函数 初始化
	public PhoneTypeAdapter(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		this.listItems = listItems;
		this.listContainer = LayoutInflater.from(context);
	}

	public int getCount() {
		// TODO Auto-generated method stub

		return listItems.size();

	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int selectID = position;
		listUiWidget listItemView = null;
		if (convertView == null) {
			listItemView = new listUiWidget();

			convertView = listContainer.inflate(R.layout.phone_type_info, null);

			listItemView.iconImg = (ImageView) convertView
					.findViewById(R.id.detail_phone_icon);
			listItemView.phoneType = (TextView) convertView
					.findViewById(R.id.detail_phone_type);
			listItemView.phoneNumber = (TextView) convertView
					.findViewById(R.id.detail_phone_number);

			convertView.setTag(listItemView);
		} else {
			listItemView = (listUiWidget) convertView.getTag();
		}
		listItemView.iconImg
				.setBackgroundResource(R.drawable.default_phone_icon);
		// 尝试性修改
		int phone_type = Integer.parseInt((String) listItems.get(position).get(
				"PHONE_TYPE"));

		System.out.println("电话类型是：" + phone_type);
		switch (phone_type) {
		case 1:
			listItemView.phoneType.setText("住宅电话");
			listItemView.phoneNumber.setText((String) listItems.get(position)
					.get("TYPE_HOME"));
			break;
		case 2:
			listItemView.phoneType.setText("手机号码");
			listItemView.phoneNumber.setText((String) listItems.get(position)
					.get("TYPE_MOBILE"));
			break;
		case 3:
			listItemView.phoneType.setText("单位电话");
			listItemView.phoneNumber.setText((String) listItems.get(position)
					.get("TYPE_WORK"));
			break;
		case 7:
			listItemView.phoneType.setText("其它号码");
			listItemView.phoneNumber.setText((String) listItems.get(position)
					.get("TYPE_OTHER"));
			break;
		default:
			break;
		}
		return convertView;
	}

}
