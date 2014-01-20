package com.freshstu;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.*;

public class DetailContactsInfo extends Activity {
	/** Called when the activity is first created. */
	// UI控件
	private TextView personName = null;
	private ImageView avatorImg = null;
	private Button deleteBut = null;
	private Button editorBut = null;
	private ListView phoneList = null;

	// 对话框
	private Builder builder = null;
	// 存放电话类型号码
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private static Long id;
	private PhoneTypeAdapter mAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_detail_info);
		// 获取UI控件
		personName = (TextView) findViewById(R.id.detail_name);
		// phoneType = (TextView)findViewById(R.id.detail_phone_type);
		avatorImg = (ImageView) findViewById(R.id.detail_avator);
		editorBut = (Button) findViewById(R.id.detail_editore);
		deleteBut = (Button) findViewById(R.id.detail_delete);
		phoneList = (ListView) findViewById(R.id.phone_info);

		// 构造builder构造器
		builder = new AlertDialog.Builder(this);

		id = getIntent().getLongExtra("id", 0);
		Long photo_id = getIntent().getLongExtra("photo_id", 0);
		Integer has_phone_number = getIntent().getIntExtra("has_phone_number",
				0);
		System.out.println("id值:"+id+"\n"+"photo_id值为:"+photo_id+"是否有照片"+has_phone_number);
		Cursor result = getAppointContactInfo2(id);
		System.out.println("返回Cursor个数：" + result.getCount());
		// 不再需要这个cursor了直接该村data里将姓名和电话一起传回来，放入到list中，然后通过自定义一个listView
		// 的adpter负责显示不同类型的电话号码 记住要给lsitView加一个 上下滑动 并且当满足类型为电话号码的时候，要激活显示发短信按钮
		// 断联系人是否有电话信息，有遍历显示，没有显示空
		while (result.moveToNext()) {
			// 设置联系人姓名
			personName.setText(result.getString(result
					.getColumnIndex(Phone.DISPLAY_NAME)));
		}

		System.out.println("指针当前位置在：" + result.getPosition() + "是否有电话："
				+ has_phone_number);

		// 设置头像图片
		if (photo_id > 0) {
			avatorImg.setImageBitmap(getAvator(id));
		} else {
			avatorImg.setBackgroundResource(R.drawable.default_avator);
		}
		if (has_phone_number != 0) {
			// 遍历所有电话号码
			// 读一次 第一while 第二次用for 将指针放到最前面 遍历电话
			for (result.moveToFirst(); !result.isAfterLast(); result
					.moveToNext()) {
				Map<String, Object> map = new HashMap<String, Object>();

				if (result.getInt(2) == Phone.TYPE_HOME) {
//					System.out.println("住宅电话");
					map.put("TYPE_HOME", result.getString(0));
					map.put("PHONE_TYPE", "1");
				} else if (result.getInt(2) == Phone.TYPE_MOBILE) {
//					System.out.println("手机号码");
					map.put("TYPE_MOBILE", result.getString(0));
					map.put("PHONE_TYPE", "2");
				} else if (result.getInt(2) == Phone.TYPE_WORK) {
//					System.out.println("单位号码");
					map.put("TYPE_WORK", result.getString(0));
					map.put("PHONE_TYPE", "3");
				} else if (result.getInt(2) == Phone.TYPE_OTHER) {
//					System.out.println("其它");
					map.put("TYPE_OTHER", result.getString(0));
					map.put("PHONE_TYPE", "7");
				}

				list.add(map);
			}
/*			System.out.println("list大小" + list.size());
			for (int x = 0; x < list.size(); x++) {
				System.out.println(list.get(x));
			}*/
			// 设置适配器 图标要在list里设置
		} else {
			// 显示为空，但设置
			// 设置图标
			// iconImg.setBackgroundResource(R.drawable.default_phone_icon);
			// 设置电话号码、电话类型
		}
		mAdapter = new PhoneTypeAdapter(this, list);
		phoneList.setAdapter(mAdapter);
		// 为编辑和删除设置监听器
		editorBut.setOnClickListener(new OnClickListener() {
			// 跳转到编辑联系人页面 更改以后要通知更新为界面上的数据
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 参数修改待完善
				// 点击编辑，直接跳转到系统自带编辑界面进行操作，之后返回
				Intent intent = new Intent(Intent.ACTION_EDIT, Uri
						.parse("content://com.android.contacts/contacts/" + id));
				startActivity(intent);
			}
		});
		// 不能使用内部匿名类 回来修改成外部类
		deleteBut.setOnClickListener(new deleteButListener());

	}

	// 删除按钮监听器

	class deleteButListener implements View.OnClickListener {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// builder.setIcon();
			builder.setTitle("删除");
			builder.setMessage("确认删除该联系人？");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 删除代码
							//再研究下 删除条记录的数据库
							deleteContacts(id);
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 取消代码
						}
					});

			builder.create().show();
		}

	}

	// 查找给定id的联系人信息
	public Cursor getAppointContactInfo(long id) {
		ContentResolver cr = this.getContentResolver();
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String where = "_ID = ?";
		Cursor result = cr.query(Data.CONTENT_URI, null, Data.CONTACT_ID + "=?"
				+ " AND " + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE
				+ "'", new String[] { String.valueOf(id) }, null);
		// Cursor result = cr.query(uri, null, where, new
		// String[]{String.valueOf(id)}, null);
		return result;
	}

	public Cursor getAppointContactInfo2(long id) {
		ContentResolver cr = this.getContentResolver();
//		Uri phone_type_uri = ContentUris.withAppendedId(
//				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, id);
		Cursor phone = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] { Phone.NUMBER, Phone.DISPLAY_NAME, Phone.DATA2 },
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
				null, null);
		return phone;

	}

	// 获取联系人头像图片
	public Bitmap getAvator(long id) {
		Bitmap avator = null;
		ContentResolver cr = this.getContentResolver();
		Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, id);
		InputStream input = Contacts.openContactPhotoInputStream(cr, uri);
		return avator = BitmapFactory.decodeStream(input);
	}

	// 删除指定联系人
	public Boolean deleteContacts(long id) {
		this.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
				"contact_id=?", new String[] { String.valueOf(id) });
		Toast.makeText(this,"删除成功", Toast.LENGTH_SHORT).show();
		return null;
	}
}