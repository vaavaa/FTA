package com.asiawaters.fta.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBController extends SQLiteOpenHelper {

    public static final int STATE_OPENED = 0;
    public static final int STATE_CLOSED = 1;
    public static final int STATE_NOTACTIVATED = -1;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "FieldTaskAppointer";

    private SQLiteDatabase db; //Update access
    private SQLiteDatabase dbr; //ReadA access
    private int state;
    private static final String _Credentials = "Credentials";
    private static final String CREATE_TABLE_Credentials = "CREATE TABLE Credentials (login VARCHAR(150) NOT NULL, password VARCHAR(150) NOT NULL)";

    private static final String _settings = "settings";
    private static final String CREATE_TABLE_settings = "CREATE TABLE Settings " +
            "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, settings_name VARCHAR(255) NOT NULL," +
            " settings_value VARCHAR(255) NOT NULL)";

    public DBController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        state = STATE_NOTACTIVATED;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_Credentials);
        db.execSQL(CREATE_TABLE_settings);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS" + _Credentials);
        db.execSQL("DROP TABLE IF EXISTS " + _settings);
        // create new tables
        onCreate(db);
    }

    public boolean openDB() {
        boolean bRet;
        try {
            db = this.getWritableDatabase();
            dbr = this.getReadableDatabase();
            state = STATE_OPENED;
            bRet = true;
        } catch (SQLException ex) {
            bRet = false;
        }
        return bRet;
    }

    public void close() {
        db.close();
        state = STATE_CLOSED;
    }

    public int getDBState() {
        return state;
    }

    /* Credentials  GET*/
    public String[] getCredentials() {
        String[] credentials = new String[]{"", ""};
        String selectQuery = "SELECT * FROM " + _Credentials;
        Cursor c = dbr.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            credentials[0] = c.getString(c.getColumnIndex("login"));
            credentials[1] = c.getString(c.getColumnIndex("password"));
            c.close();
        }
        return credentials;
    }

    /* Credentials SET*/
    public boolean setCredentials(String login, String password) {
        boolean bReturn;
        String selectQuery = "SELECT * FROM " + _Credentials;
        String selectQueryAction = "";
        Cursor c = dbr.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            selectQueryAction = "Update " + _Credentials + " SET login='" + login + "', password='" + password + "'";
            c.close();
        } else selectQueryAction = "insert into " + _Credentials + " ( login, password) " +
                "VALUES ('" + login + "','" + password + "')";

        try {
            db.execSQL(selectQueryAction);
            bReturn = true;
        } catch (Exception ex) {
            bReturn = false;
        }
        return bReturn;
    }

    // ------------------------ "settings" table methods ----------------//

    public boolean createNewSettings(m_settings dSettings) {
        int id_settings = 0;
        String insert_sql = "insert into settings ( settings_name, settings_value) " +
                "VALUES ('"+dSettings.getSettingsName()+"','"+dSettings.getSettingsValue()+"')";
        try {
            //Еcли там уже есть настрока, то обновим
            String Sql_Chk = "SELECT id FROM settings WHERE settings_name='"+dSettings.getSettingsName()+"'";
            Cursor c_ck = dbr.rawQuery(Sql_Chk, null);
            if (c_ck!=null && c_ck.moveToFirst()){
                id_settings = c_ck.getInt(c_ck.getColumnIndex("id"));
                dSettings.setId(id_settings);
                c_ck.close();
            }
            if (id_settings==0) {
                db.execSQL(insert_sql);
                String sql = "SELECT last_insert_rowid() as lastid";
                Cursor c = dbr.rawQuery(sql, null);
                if (c!=null && c.moveToFirst()){
                    dSettings.setId(c.getInt(c.getColumnIndex("lastid")));
                    c.close();
                }
                return true;
            }
            else {
                return this.updateSettings(dSettings);
            }

        }
        catch (Exception ex){
            return false;
        }
    }

    /**
     * get single settings by name
     * */
    public m_settings getSettings(String settings_name) {
        String selectQuery = "SELECT * FROM settings WHERE settings_name = '"+settings_name+"' limit 1";
        Cursor c = dbr.rawQuery(selectQuery, null);
        m_settings td = null;

        if( c != null && c.moveToFirst() ){
            td = new m_settings();
            td.setSettingsName(c.getString(c.getColumnIndex("settings_name")));
            td.setSettingsValue(c.getString(c.getColumnIndex("settings_value")));
            td.setId(c.getInt(c.getColumnIndex("id")));
            c.close();
        }
        return td;
    }

    /**
     * get single settings by id
     * */
    public m_settings getSettings(int id) {
        String selectQuery = "SELECT * FROM settings WHERE int = "+id+" limit 1";
        Cursor c = dbr.rawQuery(selectQuery, null);
        m_settings td = null;

        if( c != null && c.moveToFirst() ){
            td = new m_settings();
            td.setSettingsName(c.getString(c.getColumnIndex("settings_name")));
            td.setSettingsValue(c.getString(c.getColumnIndex("settings_value")));
            td.setId(c.getInt(c.getColumnIndex("id")));
            c.close();
        }
        return td;
    }

    /**
     * Updating a settings
     */
    public boolean updateSettings(m_settings dSettings) {
        boolean bReturn;
        String update_sql = "Update settings " +
                "SET settings_name ='"+dSettings.getSettingsName()+"', " +
                "settings_value = '"+dSettings.getSettingsValue() +"' "+
                "WHERE id = " +dSettings.getId();
        try {
            db.execSQL(update_sql);
            bReturn = true;
        }
        catch (Exception ex){
            bReturn = false;
        }
        return bReturn;
    }

    /**
     * Deleting a settings
     */
    public boolean deleteSettings(int id) {
        boolean bReturn;
        String delete_sql = "DELETE FROM settings \n"+
                "WHERE id=" + id;
        try {
            //SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex) {
            bReturn = false;
        }
        return bReturn;
    }
    /**
     * Deleting a settings by name
     */
    public boolean deleteSettings(String settings_name) {
        boolean bReturn;
        String delete_sql = "DELETE FROM settings \n"+
                "WHERE settings_name='" + settings_name+"'";
        try {
            db.execSQL(delete_sql);
            bReturn = true;
        }
        catch (Exception ex) {
            bReturn = false;
        }
        return bReturn;
    }

    // ------------------------ close "settings" table methods _---------//

}