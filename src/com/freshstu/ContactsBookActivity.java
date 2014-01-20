package com.freshstu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.PropertyNode;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.SubMenu;

public class ContactsBookActivity extends Activity {
	/** Called when the activity is first created. */
	// UI控件
	private ImageView avatorIMG = null;
	private TextView personName = null;
	private ListView listView = null;
	private AutoCompleteTextView autoText = null;
	private Button add_contacts = null;
	private Builder builder = null;
	// 待显示数据容器
	private static List<Map<String, Object>> list = null;

	// 适配器
	private Myadapter mAdapter = null;
	private MyCursorAdapter mCursorAdapter = null;
	// 待查数据
	private static final String[] PHONES_PROJECTION = new String[] {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.PHOTO_ID,
			ContactsContract.Contacts.HAS_PHONE_NUMBER };

	private static final int ID_INDEX = 0;
	private static final int DISPLAY_NAME_INDEX = 1;
	private static final int PHOTO_ID_INDEX = 2;
	private static final int HAS_PHONE_NUMBER_INDEX = 3;

	// 存放所有联系人的列表
	private List<Map<String, Object>> allContactList = null;
	// vcf中属性值
	private String name = null;
	private String phoneHome = null;
	private String phoneWork = null;
	private String phoneMobile = null;
	private String phoneOther = null;
	private byte[] photoAvatorEnd = null;
	private String photoType = null;
	private List<VNode> allPIMInfo = null;

	// 定义菜单自定义项
	final int ADD_CONTACTS = 1;
	final int SEARCH_CONTACTS = 2;
	final int MULTIPLE_CONTROAL = 3;
	final int MANAGE_CONTACTS = 4;
	final int SYNCHRO_CONTACTS = 5;
	final int IMPORT_VCF = 6;
	final int EXPORT_VCF = 7;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 获取显示控件
		avatorIMG = (ImageView) findViewById(R.id.avator);
		personName = (TextView) findViewById(R.id.person_name);
		listView = (ListView) findViewById(R.id.contacts_list);
		autoText = (AutoCompleteTextView) findViewById(R.id.auto);
		list = new ArrayList<Map<String, Object>>();
		// 添加新联系人

		// 获取整理显示数据
		// 设置适配器
		list = getContactsInfo();
		mAdapter = new Myadapter(this, list);
		listView.setAdapter(mAdapter);
		autoText.setHint("共有" + list.size() + "位联系人");
		autoText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// 联系调用查询
				Uri uri = ContactsContract.Contacts.CONTENT_URI;
				String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC";
				String selectionString = ContactsContract.Contacts.DISPLAY_NAME
						+ " LIKE ?";
				ContentResolver cr = ContactsBookActivity.this
						.getContentResolver();
				Cursor cur = cr.query(uri, PHONES_PROJECTION, selectionString,
						new String[] { s.toString() + "%" }, sortOrder);
				mCursorAdapter = new MyCursorAdapter(ContactsBookActivity.this,
						cur);
				autoText.setCompletionHint("找到" + cur.getCount() + "个联系人");
				autoText.setAdapter(mCursorAdapter);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}
		});

		autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				Cursor temp = mCursorAdapter.getCursor();
				temp.moveToPosition(position);
				// &&&&&&&&
				ContentResolver cr = ContactsBookActivity.this
						.getContentResolver();
				temp.moveToFirst();
				Bitmap avator = null;
				int photoIndex = temp
						.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
				// 获得联系人id
				Long contact_ID = temp.getLong(ID_INDEX);
				Long photot_id = temp.getLong(PHOTO_ID_INDEX);
				if (photot_id > 0) {
					Uri avator_uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contact_ID);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(cr, avator_uri);
					avator = BitmapFactory.decodeStream(input);
				} else {
					avator = BitmapFactory.decodeResource(getResources(),
							R.drawable.default_avator);
				}

				// &&&&&&&&
				Intent intent = new Intent(ContactsBookActivity.this,
						DetailContactsInfo.class);
				intent.putExtra("id", temp.getLong(ID_INDEX));
				intent.putExtra("photo_id", temp.getLong(PHOTO_ID_INDEX));
				intent.putExtra("has_phone_number",
						temp.getInt(HAS_PHONE_NUMBER_INDEX));
				startActivity(intent);
			}

		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				// System.out.println("position:" + position + "\n" + "id:" +
				// id);
				long contact_id = (Long) list.get(position).get("id");
				long photo_id = (Long) list.get(position).get("photo_id");
				int has_phone_number = (Integer) list.get(position).get(
						"has_phone_number");
				Intent intent = new Intent(ContactsBookActivity.this,
						DetailContactsInfo.class);
				intent.putExtra("id", contact_id);
				intent.putExtra("photo_id", photo_id);
				intent.putExtra("has_phone_number", has_phone_number);
				startActivity(intent);
			}
		});
	}

	// public void afterTextChanged (Editable s){
	//
	// }
	// 为搜索功能查询联系人
	/*
	 * private Cursor getContactsInfoForSearch() { Uri uri =
	 * ContactsContract.Contacts.CONTENT_URI; String sortOrder =
	 * ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"; String
	 * selectionString = "where "+ContactsContract.Contacts.DISPLAY_NAME
	 * +" like ?"; ContentResolver cr =
	 * ContactsBookActivity.this.getContentResolver(); Cursor cur =
	 * cr.query(uri, PHONES_PROJECTION, selectionString, new
	 * String[]{s.toString()+"%"}, sortOrder); return cur; }
	 */

	// 获取联系人信息函数
	private List<Map<String, Object>> getContactsInfo() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		// 使用ContentResolver客户端查询系统联系人信息
		// COLLATE LOCALIZED ASC 有点问题！代表怎样的排序
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";
		ContentResolver cr = ContactsBookActivity.this.getContentResolver();
		Cursor cur = cr.query(uri, PHONES_PROJECTION, null, null, sortOrder);
		while (cur.moveToNext()) {
			Bitmap avator = null;
			int photoIndex = cur
					.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
			// 获得联系人id
			Long contact_ID = cur.getLong(ID_INDEX);
			Long photot_id = cur.getLong(PHOTO_ID_INDEX);
			if (photot_id > 0) {
				Uri avator_uri = ContentUris.withAppendedId(uri, contact_ID);
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(cr, avator_uri);
				avator = BitmapFactory.decodeStream(input);
			} else {
				avator = BitmapFactory.decodeResource(getResources(),
						R.drawable.default_avator);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", contact_ID);
			map.put("name", cur.getString(DISPLAY_NAME_INDEX));
			map.put("avator", avator);
			map.put("photo_id", photot_id);
			map.put("has_phone_number", cur.getInt(HAS_PHONE_NUMBER_INDEX));
			result.add(map);
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// --------------添加新建联系人子菜单----------------
		menu.add(0, ADD_CONTACTS, 0, "新建联系人");
		// --------------添加搜索选项----------------
		menu.add(0, SEARCH_CONTACTS, 0, "搜索");
		// --------------添加多选选项----------------
		menu.add(0, MULTIPLE_CONTROAL, 0, "批量管理");
		// --------------添加备份恢复选项----------------
		SubMenu manageContacts = menu
				.addSubMenu(0, MANAGE_CONTACTS, 0, "管理联系人");
		manageContacts.add(0, EXPORT_VCF, 0, "备份到SD卡");
		manageContacts.add(0, IMPORT_VCF, 0, "从SD卡恢复");
		// --------------添加登录注册----------------
		menu.add(0, SYNCHRO_CONTACTS, 0, "同步联系人");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case ADD_CONTACTS:
			Intent intent = new Intent(Intent.ACTION_INSERT,
					ContactsContract.Contacts.CONTENT_URI);
			startActivity(intent);
			break;
		case SEARCH_CONTACTS:
			break;
		case EXPORT_VCF:
			final File saveFile = new File(
					Environment.getExternalStorageDirectory(),
					"backupContacts.vcf");
			builder = new AlertDialog.Builder(this);
			builder.setTitle("备份");
			builder.setMessage("备份当前联系人到" + saveFile + "文件夹？");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							System.out.println("开始备份...");
							exportVcf(saveFile);
							Toast.makeText(ContactsBookActivity.this,
									"导出" + saveFile + "文件成功",
									Toast.LENGTH_SHORT).show();
						}
					});
			builder.setNegativeButton("返回",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							System.out.println("返回");
						}
					});
			builder.create().show();
			break;

		case IMPORT_VCF:
			final File readFile = new File(
					Environment.getExternalStorageDirectory(),
					"backupContacts.vcf");

			System.out.println("我读取的路径是：" + readFile);
			builder = new AlertDialog.Builder(this);
			builder.setTitle("备份");
			builder.setMessage("从" + readFile + "文件,恢复联系人信息？");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							System.out.println("开始恢复...");
							// 解析后以list格式返回
							allPIMInfo = parseVCF(readFile);
							// 遍历恢复
							System.out.println("联系人共有" + allPIMInfo.size()
									+ "\n");

							for (VNode contact : allPIMInfo) {
								List<PropertyNode> props = contact.propList;
								String name = null;
								String phoneHome = null;
								String phoneWork = null;
								String phoneCell = null;
								String phoneOther = null;
								// 遍历属性队列
								for (PropertyNode prop : props) {

									System.out.println(prop.propName);
									if ("N".equals(prop.propName)) {
										name = prop.propValue;
										continue;
									} else if ("TEL".equals(prop.propName)) {
										// System.out.println("you fanyingmei"
										// + prop.propValue);
										if (prop.paramMap_TYPE.contains("HOME")) {
											phoneHome = prop.propValue;
										} else if (prop.paramMap_TYPE
												.contains("WORK")) {
											phoneWork = prop.propValue;
										} else if (prop.paramMap_TYPE
												.contains("X-OTHER")) {
											phoneOther = prop.propValue;
										} else if (prop.paramMap_TYPE
												.contains("CELL")) {
											phoneCell = prop.propValue;
										}
										continue;
									}
								}
								ContentProviderResult[] results;
								ArrayList<ContentProviderOperation> ops = batchAddContacts(
										name, phoneHome, phoneCell, phoneWork,
										phoneOther);

								try {
									results = ContactsBookActivity.this
											.getContentResolver().applyBatch(
													ContactsContract.AUTHORITY,
													ops);
									
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (OperationApplicationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Toast.makeText(ContactsBookActivity.this,
										"成功恢复" + allPIMInfo.size() + "位联系人^o^!",
										Toast.LENGTH_SHORT).show();
								// System.out.println("添加成功");
							}

						}
					});
			builder.setNegativeButton("返回",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							System.out.println("返回");
						}
					});
			builder.create().show();

			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void exportVcf(File file) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8");
			// 创建vcf解析对象
			VCardComposer composer = new VCardComposer();
			// 遍历输出相关项目
			allContactList = new ArrayList<Map<String, Object>>();
			// 查询找到所有联系人并返回list值
			List<Map<String, Object>> tempList = handleCursor();
			// System.out.println("########共有" + tempList.size() + "联系人");
			for (int i = 0; i < tempList.size(); i++) {
				ContactStruct contact = new ContactStruct();
				/*
				 * 暂时不对图片进行备份处理 photoAvatorEnd = new byte[1024]; photoAvatorEnd
				 * = null; photoAvatorEnd =
				 * (byte[])tempList.get(i).get("avator");
				 */
				name = (String) tempList.get(i).get("name");
				int has_number_phone_end = (Integer) tempList.get(i).get(
						"has_number");

				List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
				// 用i去除该位置的list 之后要用另一个循环
				temp = (List<Map<String, Object>>) tempList.get(i).get(
						"phone_info");
				contact.name = name;
				System.out.println("##########" + name + "我引起的问题 哈哈");
				if (has_number_phone_end != 0) {
					for (int j = 0; j < temp.size(); j++) {
						int phone_type;
						if ((String) temp.get(j).get("PHONE_TYPE") == null) {
							phone_type = 999;
						} else {
							phone_type = Integer.parseInt((String) temp.get(j)
									.get("PHONE_TYPE"));
						}
						System.out.println("android类型" + phone_type);
						switch (phone_type) {
						case 1:
							contact.addPhone(Contacts.Phones.TYPE_HOME,
									(String) temp.get(j).get("TYPE_HOME"),
									null, false);
							break;
						case 2:
							contact.addPhone(Contacts.Phones.TYPE_MOBILE,
									(String) temp.get(j).get("TYPE_MOBILE"),
									null, true);
							break;
						case 3:
							contact.addPhone(Contacts.Phones.TYPE_WORK,
									(String) temp.get(j).get("TYPE_WORK"),
									null, false);
							break;
						case 7:
							contact.addPhone(Contacts.Phones.TYPE_OTHER,
									(String) temp.get(j).get("TYPE_OTHER"),
									null, false);
							break;
						}
					}
				}

				/*
				 * 头像图片处理部分 contact.photoBytes = photoAvatorEnd;
				 * contact.photoType = ;
				 */
				String vcardString = composer.createVCard(contact,
						VCardComposer.VERSION_VCARD30_INT);
				writer.write(vcardString);
				writer.flush();
			}
			writer.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VCardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<Map<String, Object>> handleCursor() throws IOException {
		// 查询出所有联系人
		// 姓名、电话类型、电话号码、是否有电话、是否有头像、头像图片
		Cursor result = queryAllContactInfos();
		// 处理头像
		ContentResolver cr = ContactsBookActivity.this.getContentResolver();
		while (result.moveToNext()) {
			byte[] phoneAvator = new byte[1024];
			List<Map<String, Object>> phone_type_list = null;
			Map<String, Object> map = new HashMap<String, Object>();
			// 获取联系人id
			Long contact_ID = result.getLong(ID_INDEX);
			// 获取联系人姓名
			String tempName = result.getString(DISPLAY_NAME_INDEX);
			// 放入联系人姓名
			map.put("name", tempName);
			// 获取联系人头像序号
			Long photot_id = result.getLong(PHOTO_ID_INDEX);
			// 获取头像字节流
			if (photot_id > 0) {
				Uri avator_uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI, contact_ID);
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(cr, avator_uri);
				input.read(phoneAvator);
			}
			// 放入联系人头像
			map.put("avator", phoneAvator);
			// 获取联系人是否有电话项的值
			Integer has_phone_number = result.getInt(HAS_PHONE_NUMBER_INDEX);
			map.put("has_number", has_phone_number);
			if (has_phone_number != 0) {
				Cursor phone = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[] { Phone.NUMBER, Phone.DISPLAY_NAME,
								Phone.DATA2 },
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contact_ID, null, null);
				// 调用处理电话信息的函数

				phone_type_list = handlePhoneType(phone);
			}
			map.put("phone_info", phone_type_list);
			// 放入到list中
			allContactList.add(map);
		}
		return allContactList;
	}

	public Cursor queryAllContactInfos() {
		// 查询返回所有条目到Cursor
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";
		ContentResolver cr = ContactsBookActivity.this.getContentResolver();
		Cursor cur = cr.query(uri, PHONES_PROJECTION, null, null, sortOrder);
		return cur;
	}

	public List<Map<String, Object>> handlePhoneType(Cursor cursor) {
		// 处理电话信息
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();

			if (cursor.getInt(2) == Phone.TYPE_HOME) {
				// System.out.println("住宅电话");
				map.put("TYPE_HOME", cursor.getString(0));
				map.put("PHONE_TYPE", "1");
			} else if (cursor.getInt(2) == Phone.TYPE_MOBILE) {
				// System.out.println("手机号码");
				map.put("TYPE_MOBILE", cursor.getString(0));
				map.put("PHONE_TYPE", "2");
			} else if (cursor.getInt(2) == Phone.TYPE_WORK) {
				// System.out.println("单位号码");
				map.put("TYPE_WORK", cursor.getString(0));
				map.put("PHONE_TYPE", "3");
			} else if (cursor.getInt(2) == Phone.TYPE_OTHER) {
				// System.out.println("其它");
				map.put("TYPE_OTHER", cursor.getString(0));
				map.put("PHONE_TYPE", "7");
			}

			list.add(map);
		}
		return list;
	}

	/* #########以下是解析相关函数########## */
	public List<VNode> parseVCF(File file) {
		// 解析vcf函数
		// 创建解析器
		VCardParser parser = new VCardParser();
		// 存放解析结果容器
		VDataBuilder builder = new VDataBuilder();
		// 返回vcf字符串
		String vcfContentString = getVcfString(file);

		try {
			boolean isParsed = parser.parse(vcfContentString, "UTF-8", builder);
			if (!isParsed) {
				// 解析不成功报错
				System.out.println("解析此文件失败");
			}

		} catch (VCardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 处理解析结果
		List<VNode> pimContactsList = builder.vNodeList;

		return pimContactsList;
	}

	public String getVcfString(File file) {
		// 根据传入文件转换内容为String格式，返回给parseVCF函数处理
		BufferedReader bufReader = null;
		String vcardString = "";
		String line;
		try {
			bufReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));

			while ((line = bufReader.readLine()) != null) {
				vcardString += line + "\n";
			}

			bufReader.close();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vcardString;

	}

	public ArrayList<ContentProviderOperation> batchAddContacts(String name,
			String home, String phone, String work, String other) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();
		// 向原始raw_contacts表中插入一条记录，返回该条记录的id值
		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());
		// 插入姓名
		if (name != null) {
			ops.add(ContentProviderOperation
					.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.DISPLAY_NAME, name).build());
		}

		// 插入手机号码 家庭
		if (home != null) {
			ops.add(ContentProviderOperation
					.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.TYPE, Phone.TYPE_HOME)
					.withValue(Phone.NUMBER, home).build());
		}
		// 手机
		if (phone != null) {
			ops.add(ContentProviderOperation
					.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.TYPE, Phone.TYPE_MOBILE)
					.withValue(Phone.NUMBER, phone).build());
		}
		// 工作
		if (work != null) {
			ops.add(ContentProviderOperation
					.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.TYPE, Phone.TYPE_WORK)
					.withValue(Phone.NUMBER, work).build());
		}
		// 其它号码
		if (other != null) {
			ops.add(ContentProviderOperation
					.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.TYPE, Phone.TYPE_OTHER)
					.withValue(Phone.NUMBER, other).build());
		}
		return ops;
	}
}