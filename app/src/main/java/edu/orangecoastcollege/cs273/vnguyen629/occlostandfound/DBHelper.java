package edu.orangecoastcollege.cs273.vnguyen629.occlostandfound;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Provides the necessary functions and tools to put information
 * regarding reported missing items inside an SQLite database while
 * providing various different functions such as adding, upgrading
 * and modifications.
 *
 * @author Vincent Nguyen
 * @version 1.0
 */
class DBHelper extends SQLiteOpenHelper {
    private Context mContext;
    static final String DATABASE_NAME = "LostAndFound";
    private static final int DATABASE_VERSION = 1;

    // Item Database Start
    private static final String ITEMS_TABLE = "LostItems";
    private static final String ITEM_KEY_FIELD_ID = "id";
    private static final String FIELD_ITEM_NAME = "name";
    private static final String FIELD_ITEM_DESCRIPTION = "description";
    private static final String FIELD_ITEM_DATE_LOST = "date_lost";
    private static final String FIELD_ITEM_LAST_LOCATION = "last_location";
    private static final String FIELD_ITEM_STATUS = "item_status";
    private static final String FIELD_ITEM_IMAGE_URI = "image_uri";
    // Item Database End

    // Account Table Start
    private static final String ACCOUNT_TABLE = "Accounts";
    private static final String KEY_FIELD_ACCOUNT_USERNAME = "name";
    private static final String FIELD_ACCOUNT_PASSWORD = "password";
    private static final String FIELD_ACCOUNT_PHONE_NUMBER = "phone_number";
    private static final String FIELD_ACCOUNT_EMAIL = "email";
    private static final String FIELD_ACCOUNT_STUDENT_ID = "student_id";
    private static final String FIELD_ACCOUNT_PROFILE_PICTURE = "profile_pic";
    private static final String FIELD_ACCOUNT_IS_ADMIN = "is_admin";
    private static final String FIELD_ACCOUNT_ALLOW_SHAKE = "allow_shake";
    private static final String FIELD_ACCOUNT_ALLOW_SMS = "allow_sms";
    private static final String FIELD_ACCOUNT_FEEDBACK = "feedback";
    // Account Table End

    // Report Table Start
    private static final String REPORT_TABLE  = "Reports";
    private static final String REPORT_KEY_FIELD_ID = "id";
    private static final String FIELD_REPORT_ACCOUNT = "account";
    private static final String FIELD_REPORT_ITEM_ID = "item_id";
    private static final String FIELD_REPORT_SMS_CHECK = "sms_notification";
    // Report Table End

    //Feedback Table Start
    private static final String FEEDBACK_TABLE = "Feedback";
    private static final String FIELD_FEEDBACK_CONTENT = "feedback";
    private static final String FIELD_FEEDBACK_USERNAME = "username";
    //Feedback Table End

    /**
     * Creates a new SQL database.
     * @param context The context
     */
    public DBHelper(Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Creates a new database.
     * @param db The new database.
     */
    @Override
    public void onCreate (SQLiteDatabase db){
        String table = "CREATE TABLE " + ITEMS_TABLE + "("
                + ITEM_KEY_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_ITEM_NAME + " TEXT, "
                + FIELD_ITEM_DESCRIPTION + " TEXT, "
                + FIELD_ITEM_DATE_LOST + " TEXT, "
                + FIELD_ITEM_LAST_LOCATION + " TEXT, "
                + FIELD_ITEM_STATUS + " INTEGER, "
                + FIELD_ITEM_IMAGE_URI + " TEXT "
                + ")";
        db.execSQL(table);

        table =  "CREATE TABLE " + ACCOUNT_TABLE + "("
                + KEY_FIELD_ACCOUNT_USERNAME + " TEXT PRIMARY KEY, "
                + FIELD_ACCOUNT_PASSWORD + " TEXT, "
                + FIELD_ACCOUNT_PHONE_NUMBER + " TEXT, "
                + FIELD_ACCOUNT_EMAIL + " TEXT, "
                + FIELD_ACCOUNT_STUDENT_ID + " TEXT, "
                + FIELD_ACCOUNT_PROFILE_PICTURE + " TEXT, "
                + FIELD_ACCOUNT_IS_ADMIN + " INTEGER, "
                + FIELD_ACCOUNT_ALLOW_SHAKE + " INTEGER, "
                + FIELD_ACCOUNT_ALLOW_SMS + " INTEGER, "
                + FIELD_ACCOUNT_FEEDBACK + " TEXT"
                + ")";
        db.execSQL(table);

        table =  "CREATE TABLE " + REPORT_TABLE + "("
                + REPORT_KEY_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_REPORT_ACCOUNT + " TEXT, "
                + FIELD_REPORT_ITEM_ID + " TEXT, "
                + FIELD_REPORT_SMS_CHECK + " TEXT, "
                + "FOREIGN KEY(" + FIELD_REPORT_ACCOUNT + ") REFERENCES "
                + ACCOUNT_TABLE + "(" + KEY_FIELD_ACCOUNT_USERNAME + ")"
                + "FOREIGN KEY(" + FIELD_REPORT_ITEM_ID + ") REFERENCES "
                + ITEMS_TABLE + "(" + ITEM_KEY_FIELD_ID + "))";
        db.execSQL(table);

        table = "CREATE TABLE " + FEEDBACK_TABLE + "("
                + FIELD_FEEDBACK_USERNAME + " TEXT PRIMARY KEY, "
                + FIELD_FEEDBACK_CONTENT + " TEXT" + ")";
        db.execSQL(table);
    }

    /**
     * Upgrades the current <code>Item</code> database for newer versions.
     * @param db The <code>Item</code> database.
     * @param oldVersion The previous database version number.
     * @param newVersion The new database version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + REPORT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FEEDBACK_TABLE);
        onCreate(db);
    }

    /************* Item database functions *************/

    /**
     * Adds a new <code>Item</code> into the current database.
     * @param newItem New <code>Item</code> to be added too the database.
     */
    public void addItem(final Item newItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String name = newItem.getName();
        String description = newItem.getDescription();
        String dateLost = newItem.getDateLost();
        String lastLocation = newItem.getLastLocation();
        int status = ((newItem.getStatus())? 1 : 0);
        String imageURI = newItem.getImageUri().toString();

        values.put(FIELD_ITEM_NAME, name);
        values.put(FIELD_ITEM_DESCRIPTION, description);
        values.put(FIELD_ITEM_DATE_LOST, dateLost);
        values.put(FIELD_ITEM_LAST_LOCATION, lastLocation);
        values.put(FIELD_ITEM_STATUS, status);
        values.put(FIELD_ITEM_IMAGE_URI, imageURI);

        db.insert(ITEMS_TABLE, null, values);
        db.close();
    }

    /**
     * Applies changes/updates to a <code>Item</code>.
     * @param item <code>Item</code> to be updated in the database.
     */
    public void updateItem(final Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String name = item.getName();
        String description = item.getDescription();
        String dateLost = item.getDateLost();
        String lastLocation = item.getLastLocation();
        int status = ((item.getStatus())? 1 : 0);
        String imageURI = item.getImageUri().toString();

        values.put(FIELD_ITEM_NAME, name);
        values.put(FIELD_ITEM_DESCRIPTION, description);
        values.put(FIELD_ITEM_DATE_LOST, dateLost);
        values.put(FIELD_ITEM_LAST_LOCATION, lastLocation);
        values.put(FIELD_ITEM_STATUS, status);
        values.put(FIELD_ITEM_IMAGE_URI, imageURI);

        db.update(ITEMS_TABLE, values, ITEM_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(item.getID())});
        db.close();
    }

    /**
     * Retrieves a specified <code>Item</code> from the database.
     * @param id The unique id of the <code>Item</code>.
     * @return The item with the matching unique ID.
     */
    public Item getItem(final int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                ITEMS_TABLE,
                new String[]{ITEM_KEY_FIELD_ID, FIELD_ITEM_NAME, FIELD_ITEM_DESCRIPTION,
                        FIELD_ITEM_DATE_LOST, FIELD_ITEM_LAST_LOCATION, FIELD_ITEM_STATUS,
                        FIELD_ITEM_IMAGE_URI},
                ITEM_KEY_FIELD_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        int itemID = cursor.getInt(0);
        String name = cursor.getString(1);
        String description = cursor.getString(2);
        String dateLost = cursor.getString(3);
        String lastLocation = cursor.getString(4);
        boolean status = (cursor.getInt(5) == 1);
        Uri imageUri = Uri.parse(cursor.getString(6));

        final Item ITEM = new Item(itemID, name, description, dateLost, lastLocation,
                status, imageUri);

        return ITEM;
    }

    /**
     * Returns a list of all the lost items inside the database.
     * @return The list of all the lost items.
     */
    public ArrayList<Item> getAllItems() {
        ArrayList<Item> itemArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                ITEMS_TABLE,
                new String[]{ITEM_KEY_FIELD_ID, FIELD_ITEM_NAME, FIELD_ITEM_DESCRIPTION,
                        FIELD_ITEM_DATE_LOST, FIELD_ITEM_LAST_LOCATION, FIELD_ITEM_STATUS,
                        FIELD_ITEM_IMAGE_URI},
                null, null, null, null, null, null );

        Log.i("getAllItems: ", cursor.toString());

        if (cursor.moveToFirst()){
            do {
                int itemID = cursor.getInt(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                String dateLost = cursor.getString(3);
                String lastLocation = cursor.getString(4);
                boolean status = (cursor.getInt(5) == 1);
                Uri imageUri = Uri.parse(cursor.getString(6));

                itemArrayList.add(new Item(itemID, name, description, dateLost, lastLocation,
                        status, imageUri));

            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return itemArrayList;
    }

    /**
     * Deletes a specific <code>Item</code>.
     * @param id The unique ID of the <code>Item</code> object.
     */
    public void deleteItem(final int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ITEMS_TABLE, ITEM_KEY_FIELD_ID + " = ?", new String[] {String.valueOf(id)});
        db.close();
    }

    /**
     * Delete all the items in the current database.
     */
    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ITEMS_TABLE, null, null);
        db.close();
    }

    /**
     * Imports/reads lost item data from a CSV file to populate the database with.
     * @param csvFileName Name of the file to import data from.
     * @return Whether or not the file data was open and imported successfully.
     */
    public boolean importItemFromCSV(final String csvFileName) {
        AssetManager manager = mContext.getAssets();
        InputStream inStream;
        try {
            inStream = manager.open(csvFileName);
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4) {
                    Log.d("OCC Lost and Found", mContext.getString(R.string.skipping_bad_csv_row_text)
                            + Arrays.toString(fields));
                    continue;
                }

                int id = Integer.parseInt(fields[0].replaceAll("\\s+",""));
                String name = fields[1].trim();
                String description = fields[2].trim();
                String dateLost = fields[3].trim();
                String lastLocation = fields[4].trim();
                boolean status = (fields[5].replaceAll("\\s+","") == "Found");
                Uri itemImageURI = Uri.parse(fields[5].trim());

                addItem(new Item(id, name, description, dateLost, lastLocation,
                        status, itemImageURI));
            }
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }
        return true;
    }

    public Item getMostRecentItem() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                ITEMS_TABLE,
                new String[]{ITEM_KEY_FIELD_ID, FIELD_ITEM_NAME, FIELD_ITEM_DESCRIPTION,
                        FIELD_ITEM_DATE_LOST, FIELD_ITEM_LAST_LOCATION, FIELD_ITEM_STATUS,
                        FIELD_ITEM_IMAGE_URI},
                null,
                null,
                null, null, null, null );

        if (cursor != null)
            cursor.moveToLast();

        int itemID = cursor.getInt(0);
        String name = cursor.getString(1);
        String description = cursor.getString(2);
        String dateLost = cursor.getString(3);
        String lastLocation = cursor.getString(4);
        boolean status = (cursor.getInt(5) == 1);
        Uri imageUri = Uri.parse(cursor.getString(6));

        final Item ITEM = new Item(itemID, name, description, dateLost, lastLocation,
                status, imageUri);

        return ITEM;
    }

    /************* User Account database functions *************/

    /**
     * Create new Account
     * Adds a new account to the database
     * @param account The new account to be added
     */
    public void addAccount(UserAccount account)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_FIELD_ACCOUNT_USERNAME, account.getStudentUserName());
        values.put(FIELD_ACCOUNT_PASSWORD, account.getStudentPassword());
        values.put(FIELD_ACCOUNT_PHONE_NUMBER, account.getStudentPhoneNum());
        values.put(FIELD_ACCOUNT_EMAIL, account.getStudentEmail());
        values.put(FIELD_ACCOUNT_STUDENT_ID, account.getStudentID());
        values.put(FIELD_ACCOUNT_PROFILE_PICTURE, account.getStudentProfilePic().toString());
        values.put(FIELD_ACCOUNT_IS_ADMIN, (account.getIsAdmin()) ? 1 : 0);
        values.put(FIELD_ACCOUNT_ALLOW_SHAKE, (account.getAllowShake()) ? 1 : 0);
        values.put(FIELD_ACCOUNT_ALLOW_SMS, (account.getAllowSms()) ? 1 : 0);
        values.put(FIELD_ACCOUNT_FEEDBACK, account.getFeedBack());

        db.insert(ACCOUNT_TABLE, null, values);
        db.close();
    }

    /**
     *  Get UserAccount and place into a list
     * @return ArrayList<UserAccount></UserAccount>
     */
    public ArrayList<UserAccount> getAllUserAccount()
    {
        ArrayList<UserAccount> accountList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(
                ACCOUNT_TABLE,
                new String[]{KEY_FIELD_ACCOUNT_USERNAME, FIELD_ACCOUNT_PASSWORD, FIELD_ACCOUNT_PHONE_NUMBER,
                        FIELD_ACCOUNT_EMAIL, FIELD_ACCOUNT_STUDENT_ID, FIELD_ACCOUNT_PROFILE_PICTURE,
                        FIELD_ACCOUNT_IS_ADMIN, FIELD_ACCOUNT_ALLOW_SHAKE, FIELD_ACCOUNT_ALLOW_SMS, FIELD_ACCOUNT_FEEDBACK}, null, null, null, null, null, null);

        if (cursor.moveToFirst())
        {
            do {
                UserAccount account =
                        new UserAccount(cursor.getString(0), cursor.getString(1),
                                cursor.getString(2), cursor.getString(3),
                                cursor.getString(4), Uri.parse(cursor.getString(5)),
                                (cursor.getInt(6) == 0) ? false:true,
                                (cursor.getInt(7) == 0) ? false:true,
                                (cursor.getInt(8) == 0) ? false:true,
                                cursor.getString(9));
                accountList.add(account);

            }while (cursor.moveToNext());
        }

        return accountList;
    }

    /**
     * Deletes a desired account from the database
     * @param account The desired account to delete
     */
    public void deleteUserAccount(UserAccount account) {
        SQLiteDatabase db = this.getWritableDatabase();

        // DELETE THE TABLE ROW
        db.delete(ACCOUNT_TABLE, KEY_FIELD_ACCOUNT_USERNAME + " = ?",
                new String[]{account.getStudentUserName()});
    }

    /**
     * Deletes all the user accounts from the database
     */
    public void deleteAllUserAccounts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ACCOUNT_TABLE, null, null);
    }

    /**
     * Updates an account in the database, overwriting previous data
     * @param account The new account to take information from
     */
    public void updateAccount(UserAccount account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_FIELD_ACCOUNT_USERNAME, account.getStudentUserName());
        values.put(FIELD_ACCOUNT_PASSWORD, account.getStudentPassword());
        values.put(FIELD_ACCOUNT_PHONE_NUMBER, account.getStudentPhoneNum());
        values.put(FIELD_ACCOUNT_EMAIL, account.getStudentEmail());
        values.put(FIELD_ACCOUNT_STUDENT_ID, account.getStudentID());
        values.put(FIELD_ACCOUNT_PROFILE_PICTURE, account.getStudentProfilePic().toString());
        values.put(FIELD_ACCOUNT_IS_ADMIN, (account.getIsAdmin()) ? 1 : 0);
        values.put(FIELD_ACCOUNT_ALLOW_SHAKE, (account.getAllowShake()) ? 1 : 0);
        values.put(FIELD_ACCOUNT_ALLOW_SMS, (account.getAllowSms()) ? 1 : 0);
        values.put(FIELD_ACCOUNT_FEEDBACK, account.getFeedBack());

        db.update(ACCOUNT_TABLE, values, KEY_FIELD_ACCOUNT_USERNAME + " = ?",
                new String[]{account.getStudentUserName()});
    }

    /**
     * Retrieves a desired account from the database
     * @param username The username for the account to get
     * @return The account with the desired username
     */
    public UserAccount getUserAccount(String username) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                ACCOUNT_TABLE,
                new String[]{KEY_FIELD_ACCOUNT_USERNAME, FIELD_ACCOUNT_PASSWORD, FIELD_ACCOUNT_PHONE_NUMBER,
                        FIELD_ACCOUNT_EMAIL, FIELD_ACCOUNT_STUDENT_ID,
                        FIELD_ACCOUNT_PROFILE_PICTURE, FIELD_ACCOUNT_IS_ADMIN,
                        FIELD_ACCOUNT_ALLOW_SHAKE, FIELD_ACCOUNT_ALLOW_SMS, FIELD_ACCOUNT_FEEDBACK},
                KEY_FIELD_ACCOUNT_USERNAME + "=?",
                new String[]{username},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        UserAccount account =
                new UserAccount(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), Uri.parse(cursor.getString(5)),
                        (cursor.getInt(6) == 0) ? false:true, (cursor.getInt(7) == 0) ? false:true,
                        (cursor.getInt(8) == 0) ? false:true, cursor.getString(9));

        return account;
    }

    /************* Report database functions *******************/

    /**
     * @author: Benjamin Nguyen
     *
     * Adds a new <code>Report</code> into the current database
     * @param newReport New <code>Report</code> to be added too the database
     */
    public void addReport(final Report newReport) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        UserAccount account = newReport.getAccount();
        Item item = newReport.getItem();
        int smsCheck = newReport.getSmsCheck();

        values.put(FIELD_REPORT_ACCOUNT, account.getStudentUserName());
        values.put(FIELD_REPORT_ITEM_ID, item.getID());
        values.put(FIELD_REPORT_SMS_CHECK, smsCheck);

        db.insert(REPORT_TABLE, null, values);
    }

    /**
     * Returns a list of all the reports inside the database
     * @return The list of all the reports
     */
    public ArrayList<Report> getAllReports() {
        ArrayList<Report> reportArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                REPORT_TABLE,
                new String[]{REPORT_KEY_FIELD_ID, FIELD_REPORT_ACCOUNT, FIELD_REPORT_ITEM_ID,
                        FIELD_REPORT_SMS_CHECK},
                null, null, null, null, null, null );

        if (cursor.moveToFirst()){
            do {
                UserAccount account = getUserAccount(cursor.getString(1));
                Item item  = getItem(Integer.parseInt(cursor.getString(2)));
                int reportID = cursor.getInt(0);
                int smsCheck = cursor.getInt(3);

                reportArrayList.add(new Report(reportID, account, item, smsCheck));

            } while (cursor.moveToNext());
        }

        return reportArrayList;
    }

    /**
     * Get report from database and added into the an arrayList
     * @param account
     * @return ArrayList<Report></Report>
     */
    public ArrayList<Report> getAllReportsFromUser(UserAccount account) {
        ArrayList<Report> reportArrayList = new ArrayList<>();
        ArrayList<Item> itemArrayList = new ArrayList<>();

        Log.i("getAllReportsfromuser", account.toString());

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                REPORT_TABLE,
                new String[]{REPORT_KEY_FIELD_ID, FIELD_REPORT_ACCOUNT, FIELD_REPORT_ITEM_ID,
                        FIELD_REPORT_SMS_CHECK},
                    FIELD_REPORT_ACCOUNT + "=?",
                    new String[]{account.getStudentUserName()},
                    null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Log.i("useraccount", account.getStudentUserName());
                Log.i("itemid", cursor.getString(2));
                Item item = getItem(Integer.parseInt(cursor.getString(2)));
                Log.i("item", item.toString());
                int reportID = cursor.getInt(0);
                int smsCheck = cursor.getInt(3);

                reportArrayList.add(new Report(reportID, account, item, smsCheck));
            } while (cursor.moveToNext());
        }

        return reportArrayList;
    }

    /**
     * Retrieves a specified <code>Report</code> from the database
     * @param id The unique id of the <code>Report</code>
     * @return The report with the matching unique ID
     */
    public Report getReport(final int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                REPORT_TABLE,
                new String[]{REPORT_KEY_FIELD_ID, FIELD_REPORT_ACCOUNT, FIELD_REPORT_ITEM_ID,
                        FIELD_REPORT_SMS_CHECK},
                REPORT_KEY_FIELD_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        UserAccount account = getUserAccount(cursor.getString(1));
        Item item  = getItem(Integer.parseInt(cursor.getString(2)));
        int reportID = cursor.getInt(0);
        int smsCheck = cursor.getInt(3);

        final Report REPORT = new Report(reportID, account, item, smsCheck);

        return REPORT;
    }

    /**
     * Applies changes/updates to a <code>Report</code>
     * @param report <code>Report</code> to be updated in the database
     */
    public void updateReport(final Report report){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        UserAccount account = report.getAccount();
        Item item = report.getItem();
        int smsCheck = report.getSmsCheck();

        values.put(FIELD_REPORT_ACCOUNT, account.getStudentUserName());
        values.put(FIELD_REPORT_ITEM_ID, item.getID());
        values.put(FIELD_REPORT_SMS_CHECK, smsCheck);

        db.update(REPORT_TABLE, values, REPORT_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(report.getID())});
    }

    /**
     * Deletes a specific <code>Report</code> object
     * @param id The unique ID of the <code>Report</code> object
     */
    public void deleteReport(final int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(REPORT_TABLE, REPORT_KEY_FIELD_ID + " = ?", new String[] {String.valueOf(id)});
    }

    /**
     * Delete all the reports in the current database
     */
    public void deleteAllReports() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(REPORT_TABLE, null, null);
    }

    /**
     * Imports/reads reports data from a CSV file to populate the database with.
     * @param csvFileName Name of the file to import data from
     * @return Whether or not the file data was open and imported successfully
     */
    public boolean importReportFromCSV(final String csvFileName) {
        AssetManager manager = mContext.getAssets();
        InputStream inStream;
        try {
            inStream = manager.open(csvFileName);
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4) {
                    Log.d("OCC Lost and Found", mContext.getString(R.string.skipping_bad_csv_row_text)
                            + Arrays.toString(fields));
                    continue;
                }

                int id = Integer.parseInt(fields[0].replaceAll("\\s+",""));
                UserAccount account = getUserAccount(fields[1].trim());
                Item item = getItem(Integer.parseInt(fields[2].replaceAll("\\s+","")));
                int smsCheck = Integer.parseInt(fields[3].replaceAll("\\s+",""));

                addReport(new Report(id, account, item, smsCheck));
            }
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }
        return true;
    }

    /************* Report database functions end *************/

}