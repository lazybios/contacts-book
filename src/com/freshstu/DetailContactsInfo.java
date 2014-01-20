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
	// UI�ؼ�
	private TextView personName = null;
	private ImageView avatorImg = null;
	private Button deleteBut = null;
	private Button editorBut = null;
	private ListView phoneList = null;

	// �Ի���
	private Builder builder = null;
	// ��ŵ绰���ͺ���
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private static Long id;
	private PhoneTypeAdapter mAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_detail_info);
		// ��ȡUI�ؼ�
		personName = (TextView) findViewById(R.id.detail_name);
		// phoneType = (TextView)findViewById(R.id.detail_phone_type);
		avatorImg = (ImageView) findViewById(R.id.detail_avator);
		editorBut = (Button) findViewById(R.id.detail_editore);
		deleteBut = (Button) findViewById(R.id.detail_delete);
		phoneList = (ListView) findViewById(R.id.phone_info);

		// ����builder������
		builder = new AlertDialog.Builder(this);

		id = getIntent().getLongExtra("id", 0);
		Long photo_id = getIntent().getLongExtra("photo_id", 0);
		Integer has_phone_number = getIntent().getIntExtra("has_phone_number",
				0);
		System.out.println("idֵ:"+id+"\n"+"photo_idֵΪ:"+photo_id+"�Ƿ�����Ƭ"+has_phone_number);
		Cursor result = getAppointContactInfo2(id);
		System.out.println("����Cursor������" + result.getCount());
		// ������Ҫ���cursor��ֱ�Ӹô�data�ｫ�����͵绰һ�𴫻��������뵽list�У�Ȼ��ͨ���Զ���һ��listView
		// ��adpter������ʾ��ͬ���͵ĵ绰���� ��סҪ��lsitView��һ�� ���»��� ���ҵ���������Ϊ�绰�����ʱ��Ҫ������ʾ�����Ű�ť
		// ����ϵ���Ƿ��е绰��Ϣ���б�����ʾ��û����ʾ��
		while (result.moveToNext()) {
			// ������ϵ������
			personName.setText(result.getString(result
					.getColumnIndex(Phone.DISPLAY_NAME)));
		}

		System.out.println("ָ�뵱ǰλ���ڣ�" + result.getPosition() + "�Ƿ��е绰��"
				+ has_phone_number);

		// ����ͷ��ͼƬ
		if (photo_id > 0) {
			avatorImg.setImageBitmap(getAvator(id));
		} else {
			avatorImg.setBackgroundResource(R.drawable.default_avator);
		}
		if (has_phone_number != 0) {
			// �������е绰����
			// ��һ�� ��һwhile �ڶ�����for ��ָ��ŵ���ǰ�� �����绰
			for (result.moveToFirst(); !result.isAfterLast(); result
					.moveToNext()) {
				Map<String, Object> map = new HashMap<String, Object>();

				if (result.getInt(2) == Phone.TYPE_HOME) {
//					System.out.println("סլ�绰");
					map.put("TYPE_HOME", result.getString(0));
					map.put("PHONE_TYPE", "1");
				} else if (result.getInt(2) == Phone.TYPE_MOBILE) {
//					System.out.println("�ֻ�����");
					map.put("TYPE_MOBILE", result.getString(0));
					map.put("PHONE_TYPE", "2");
				} else if (result.getInt(2) == Phone.TYPE_WORK) {
//					System.out.println("��λ����");
					map.put("TYPE_WORK", result.getString(0));
					map.put("PHONE_TYPE", "3");
				} else if (result.getInt(2) == Phone.TYPE_OTHER) {
//					System.out.println("����");
					map.put("TYPE_OTHER", result.getString(0));
					map.put("PHONE_TYPE", "7");
				}

				list.add(map);
			}
/*			System.out.println("list��С" + list.size());
			for (int x = 0; x < list.size(); x++) {
				System.out.println(list.get(x));
			}*/
			// ���������� ͼ��Ҫ��list������
		} else {
			// ��ʾΪ�գ�������
			// ����ͼ��
			// iconImg.setBackgroundResource(R.drawable.default_phone_icon);
			// ���õ绰���롢�绰����
		}
		mAdapter = new PhoneTypeAdapter(this, list);
		phoneList.setAdapter(mAdapter);
		// Ϊ�༭��ɾ�����ü�����
		editorBut.setOnClickListener(new OnClickListener() {
			// ��ת���༭��ϵ��ҳ�� �����Ժ�Ҫ֪ͨ����Ϊ�����ϵ�����
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// �����޸Ĵ�����
				// ����༭��ֱ����ת��ϵͳ�Դ��༭������в�����֮�󷵻�
				Intent intent = new Intent(Intent.ACTION_EDIT, Uri
						.parse("content://com.android.contacts/contacts/" + id));
				startActivity(intent);
			}
		});
		// ����ʹ���ڲ������� �����޸ĳ��ⲿ��
		deleteBut.setOnClickListener(new deleteButListener());

	}

	// ɾ����ť������

	class deleteButListener implements View.OnClickListener {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// builder.setIcon();
			builder.setTitle("ɾ��");
			builder.setMessage("ȷ��ɾ������ϵ�ˣ�");
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// ɾ������
							//���о��� ɾ������¼�����ݿ�
							deleteContacts(id);
						}
					});
			builder.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// ȡ������
						}
					});

			builder.create().show();
		}

	}

	// ���Ҹ���id����ϵ����Ϣ
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

	// ��ȡ��ϵ��ͷ��ͼƬ
	public Bitmap getAvator(long id) {
		Bitmap avator = null;
		ContentResolver cr = this.getContentResolver();
		Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, id);
		InputStream input = Contacts.openContactPhotoInputStream(cr, uri);
		return avator = BitmapFactory.decodeStream(input);
	}

	// ɾ��ָ����ϵ��
	public Boolean deleteContacts(long id) {
		this.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
				"contact_id=?", new String[] { String.valueOf(id) });
		Toast.makeText(this,"ɾ���ɹ�", Toast.LENGTH_SHORT).show();
		return null;
	}
}