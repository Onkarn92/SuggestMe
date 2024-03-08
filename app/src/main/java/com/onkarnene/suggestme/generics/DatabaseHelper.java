package com.onkarnene.suggestme.generics;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.onkarnene.suggestme.models.AppModel;
import com.onkarnene.suggestme.models.Customer;
import com.onkarnene.suggestme.models.Pair;

import java.io.File;
import java.util.ArrayList;

/*
 * Created by Onkar Nene on 24-01-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "SuggestMe.db";
	private static final int DATABASE_VERSION = 1;
	private static DatabaseHelper m_instance = null;
	private final String KEY_LAST_LOGIN = "LastLogin";
	private final String VALUE_LOGOUT = "CustomerLogout";
	private final String TABLE_CUSTOMER = "customer";
	private final String TABLE_PAIR = "pair";
	private final String TABLE_PAIR_HISTORY = "pair_history";
	private final String TABLE_APP_SETTING = "app_settings";
	
	private final Context m_context;
	
	/**
	 * Using Singleton Pattern Create Instance if null
	 *
	 * @param context Calling Activity context
	 * @return instance of DataBaseHelper
	 */
	public static DatabaseHelper getInstance(Context context) {
		try {
			if (m_instance == null) {
				m_instance = new DatabaseHelper(context.getApplicationContext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m_instance;
	}
	
	/**
	 * Constructor Function
	 *
	 * @param context Application Context
	 */
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		m_context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE_CUSTOMER = "CREATE TABLE IF NOT EXISTS " + this.TABLE_CUSTOMER + " (cid TEXT, " + "first_name TEXT, " + "surname TEXT, "
		                               + "email TEXT, " + "password TEXT, " + "dob TEXT, " + "contact_no TEXT, " + "PRIMARY KEY(cid))";
		
		String CREATE_TABLE_APP_SETTING = "CREATE TABLE IF NOT EXISTS " + this.TABLE_APP_SETTING + " (id TEXT," + "value TEXT," + "PRIMARY KEY(id))";
		
		String CREATE_TABLE_PAIR = "CREATE TABLE IF NOT EXISTS " + this.TABLE_PAIR + " (_id INTEGER PRIMARY KEY, " + "customer_id TEXT, "
		                           + "shirt_path TEXT, " + "pant_path TEXT)";
		
		String CREATE_TABLE_PAIR_HISTORY = "CREATE TABLE IF NOT EXISTS " + this.TABLE_PAIR_HISTORY + " (_id INTEGER PRIMARY KEY, "
		                                   + "customer_id TEXT, " + "shirt_path TEXT, " + "pant_path TEXT)";
		
		db.execSQL(CREATE_TABLE_CUSTOMER);
		db.execSQL(CREATE_TABLE_APP_SETTING);
		db.execSQL(CREATE_TABLE_PAIR);
		db.execSQL(CREATE_TABLE_PAIR_HISTORY);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	/**
	 * Save customer
	 *
	 * @param customer to be saved
	 */
	public void saveCustomer(Customer customer) {
		try {
			if (customer != null) {
				SQLiteDatabase db = this.getWritableDatabase();
				this.m_openDatabase(db);
				
				ContentValues customerValues = new ContentValues();
				customerValues.put("cid", customer.getCustomerID());
				customerValues.put("first_name", customer.getFirstName());
				customerValues.put("surname", customer.getSurname());
				customerValues.put("email", customer.getEmail());
				customerValues.put("password", customer.getPassword());
				customerValues.put("dob", customer.getDob());
				customerValues.put("contact_no", customer.getMobileNumber());
				db.replace(this.TABLE_CUSTOMER, null, customerValues);
				db.close();
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Authenticate customer from db
	 *
	 * @param email    of customer
	 * @param password of customer
	 */
	@SuppressLint("Range")
	public Customer authenticateCustomer(String email, String password) {
		Customer customer = null;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			this.m_openDatabase(db);
			String sql =
					"SELECT * FROM " + this.TABLE_CUSTOMER + " WHERE email='" + email.toLowerCase() + "' AND password='" + password.toLowerCase()
			             + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				do {
					customer = new Customer();
					customer.setCustomerID(cursor.getString(cursor.getColumnIndex("cid"))
					                             .toLowerCase());
					customer.setFirstName(cursor.getString(cursor.getColumnIndex("first_name"))
					                            .toLowerCase());
					customer.setSurname(cursor.getString(cursor.getColumnIndex("surname"))
					                          .toLowerCase());
					customer.setEmail(cursor.getString(cursor.getColumnIndex("email"))
					                        .toLowerCase());
					customer.setPassword(cursor.getString(cursor.getColumnIndex("password"))
					                           .toLowerCase());
					customer.setDob(cursor.getString(cursor.getColumnIndex("dob"))
					                      .toLowerCase());
					customer.setMobileNumber(cursor.getString(cursor.getColumnIndex("contact_no"))
					                               .toLowerCase());
				} while (cursor.moveToNext());
			}
			if (customer != null) {
				ContentValues appSettingValues = new ContentValues();
				appSettingValues.put("id", this.KEY_LAST_LOGIN);
				appSettingValues.put("value", customer.getCustomerID());
				db.replace(this.TABLE_APP_SETTING, null, appSettingValues);
			}
			cursor.close();
			return customer;
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return customer;
	}
	
	/**
	 * @return last logged In Customer
	 */
	public Customer getLastLoggedInCustomer() {
		SQLiteDatabase db = this.getReadableDatabase();
		this.m_openDatabase(db);
		String sql = "SELECT * FROM " + this.TABLE_APP_SETTING + " WHERE id='" + this.KEY_LAST_LOGIN + "'";
		Cursor cursor = db.rawQuery(sql, null);
		String lastLogin;
		if (cursor.moveToFirst()) {
			do {
				lastLogin = cursor.getString(1);
			} while (cursor.moveToNext());
			cursor.close();
			if (lastLogin.equalsIgnoreCase(this.VALUE_LOGOUT)) {
				return null;
			} else {
				return this.m_getCustomer(lastLogin);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Save pair of shirt and pant
	 */
	public void savePair(Pair pair) {
		try {
			if (pair != null) {
				SQLiteDatabase db = this.getWritableDatabase();
				this.m_openDatabase(db);
				
				ContentValues pairValues = new ContentValues();
				pairValues.put("customer_id", pair.getCustomerID()
				                                  .toLowerCase());
				pairValues.put("shirt_path", pair.getShirtPath());
				pairValues.put("pant_path", pair.getPantPath());
				db.replace(this.TABLE_PAIR, null, pairValues);
				db.close();
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save pair history of shirt and pant
	 */
	public void savePairHistory(Pair pair) {
		try {
			if (pair != null) {
				SQLiteDatabase db = this.getWritableDatabase();
				this.m_openDatabase(db);
				
				ContentValues pairHistoryValues = new ContentValues();
				pairHistoryValues.put("customer_id", pair.getCustomerID()
				                                         .toLowerCase());
				pairHistoryValues.put("shirt_path", pair.getShirtPath());
				pairHistoryValues.put("pant_path", pair.getPantPath());
				db.replace(this.TABLE_PAIR_HISTORY, null, pairHistoryValues);
				db.close();
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return pair list
	 */
	@SuppressLint("Range")
	public ArrayList<Pair> getPairs(String source) {
		ArrayList<Pair> pairs = new ArrayList<>();
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			this.m_openDatabase(db);
			String tableName = this.TABLE_PAIR;
			if (source.equalsIgnoreCase(AppModel.GET_PAIR_HISTORY)) {
				tableName = this.TABLE_PAIR_HISTORY;
			}
			String sql = "SELECT * FROM " + tableName + " WHERE customer_id='" + AppModel.customer.getCustomerID()
			                                                                                      .toLowerCase() + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				do {
					Pair pair = new Pair();
					pair.setCustomerID(cursor.getString(cursor.getColumnIndex("customer_id"))
					                         .toLowerCase());
					pair.setShirtPath(cursor.getString(cursor.getColumnIndex("shirt_path")));
					pair.setPantPath(cursor.getString(cursor.getColumnIndex("pant_path")));
					pairs.add(pair);
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return pairs;
	}
	
	/**
	 * Handles Customer logout event
	 */
	public void logoutCustomer() {
		try {
			SQLiteDatabase db = getWritableDatabase();
			this.m_openDatabase(db);
			ContentValues appSettingValues = new ContentValues();
			appSettingValues.put("id", this.KEY_LAST_LOGIN);
			appSettingValues.put("value", this.VALUE_LOGOUT);
			db.replace(this.TABLE_APP_SETTING, null, appSettingValues);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Open database
	 *
	 * @param db to be open
	 */
	private void m_openDatabase(SQLiteDatabase db) {
		try {
			if (!db.isOpen()) {
				File file = m_context.getDatabasePath(DATABASE_NAME);
				SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return customers
	 */
	@SuppressLint("Range")
	private Customer m_getCustomer(String customerID) {
		Customer customer = null;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			this.m_openDatabase(db);
			String sql = "SELECT * FROM " + this.TABLE_CUSTOMER + " WHERE cid='" + customerID.toLowerCase() + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				do {
					customer = new Customer();
					customer.setCustomerID(cursor.getString(cursor.getColumnIndex("cid"))
					                             .toLowerCase());
					customer.setFirstName(cursor.getString(cursor.getColumnIndex("first_name"))
					                            .toLowerCase());
					customer.setSurname(cursor.getString(cursor.getColumnIndex("surname"))
					                          .toLowerCase());
					customer.setEmail(cursor.getString(cursor.getColumnIndex("email"))
					                        .toLowerCase());
					customer.setPassword(cursor.getString(cursor.getColumnIndex("password"))
					                           .toLowerCase());
					customer.setDob(cursor.getString(cursor.getColumnIndex("dob"))
					                      .toLowerCase());
					customer.setMobileNumber(cursor.getString(cursor.getColumnIndex("contact_no"))
					                               .toLowerCase());
				} while (cursor.moveToNext());
			}
			cursor.close();
			return customer;
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return customer;
	}
}