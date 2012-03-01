package com.my_company.app_template;

import java.io.File;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import com.my_company.app_template.CustAlrtMsgOptnListener.MessageCodes;

/**
 * Application Database Access Helper class. Defines the basic CRUD operations
 * for this application.
 * 
 * Executes the database tables SQL create scripts.
 * 
 * @extends SQLiteOpenHelper
 * 
 * @constructor context
 * 
 */
public class MyAppDbAdapter extends SQLiteOpenHelper {
  // database info
  private static final String MY_DATABASE_NAME = "musicDB.db";
  private static final String DATABASE_PATH_EXTERNAL = Environment
      .getExternalStorageDirectory().toString()
      + File.separator + "myMusicList";
  private String dbPathToUse = DATABASE_PATH_EXTERNAL;

  // change this if the database structure gets changes and needs to be updated.
  private static final int DATABASE_VERSION = 1;

  // db table/field refs
  protected static final String KEY_ROWID = "_id";

  protected static final String MY_MUSIC_DB_TABLE = "music_table";
  protected static final String KEY_ALBUM = "album";
  protected static final String KEY_ALBUMDATE = "pubdate";
  protected static final String KEY_ARTIST = "artist";
  protected static final String KEY_GENRE = "genre";
  protected static final String KEY_SONG_TITLE = "song";
  protected static final String KEY_NOTES = "entrynotes";

  protected static final String MY_PREFS_DB_TABLE = "myappprefs";
  protected static final String KEY_PREFNAME = "prefname";
  protected static final String KEY_PREFVALUE = "prefvalue";
  protected static final String KEY_PREFDESCR = "prefdescr";

  protected static final int MY_MUSIC_TABLE = 1;
  protected static final int MY_PREFS_TABLE = 2;

  private static final String TAG = "MyAppDbAdapter";

  private Context mCtx;
  private SQLiteDatabase mDb;
  protected MyAppDbAdapter objMusicDbAdapterRef;

  private MyDisplayAlertClass objDisplayAlertClass;

  /*
   * DATABASE CREATE STATEMENTS
   */

  /**
   * Template Database table creation sql statement
   */

  private static final String MY_MUSIC_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
      + MY_MUSIC_DB_TABLE
      + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
      + KEY_ARTIST
      + " TEXT NOT NULL DEFAULT '', "
      + KEY_ALBUM
      + " TEXT NOT NULL DEFAULT '', "
      + KEY_SONG_TITLE
      + " TEXT NOT NULL DEFAULT '', "
      + KEY_GENRE
      + " TEXT NOT NULL DEFAULT '', "
      + KEY_ALBUMDATE
      + " TEXT NOT NULL DEFAULT '', " + KEY_NOTES + " TEXT);";

  /**
   * Application Preferences Database table creation sql statement
   */
  private static final String MY_PREFS_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
      + MY_PREFS_DB_TABLE
      + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
      + "prefname TEXT NOT NULL DEFAULT '', "
      + "prefvalue TEXT NOT NULL DEFAULT '', " + "prefdescr TEXT);";

  /**
   * Constructor - takes the context to allow the database to be opened/created
   * 
   * @param ctx
   *          the Context within which to work
   */
  MyAppDbAdapter(Context context) {
    super(context, MyAppDbAdapter.MY_DATABASE_NAME, null,
        MyAppDbAdapter.DATABASE_VERSION);
    try {
      this.mCtx = context;
      this.objMusicDbAdapterRef = this;

      // check for existence of the SD card
      if (android.os.Environment.getExternalStorageState().equals(
          android.os.Environment.MEDIA_MOUNTED)
          && !(Environment.getExternalStorageState()
              .equals(Environment.MEDIA_MOUNTED_READ_ONLY))) {
        this.dbPathToUse = DATABASE_PATH_EXTERNAL + File.separator;
      }// end if
      else {
        this.dbPathToUse = context.getDatabasePath(
            MyAppDbAdapter.MY_DATABASE_NAME).getPath();
      }// end if sd card exists

      // file might not be created yet, use built path instead
      File myAppDBDir = new File(this.dbPathToUse);
      File myAppDB = null;

      // if the directory does not yet exist, create it.
      if (!myAppDBDir.exists() && !myAppDBDir.isDirectory()) {
        boolean blMkDirRslt = myAppDBDir.mkdirs();

        if (blMkDirRslt != true) {
          throw new Exception(
              "Application data directory could not be created at "
                  + this.dbPathToUse);
        } else {
          myAppDB = new File(this.dbPathToUse + MyAppDbAdapter.MY_DATABASE_NAME);
        }
      } else {
        if (myAppDBDir.exists() && myAppDBDir.isDirectory()) {
          myAppDB = new File(this.dbPathToUse + MyAppDbAdapter.MY_DATABASE_NAME);
        }// end if (!myAppDBDir.exists() &&...
      }// end if (!myAppDBDir.exists() &&...

      if (myAppDB != null) {
        this.mDb = SQLiteDatabase.openDatabase(this.dbPathToUse
            + File.separator + MyAppDbAdapter.MY_DATABASE_NAME, null,
            SQLiteDatabase.OPEN_READWRITE);
      }

    } catch (SQLiteException error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this.mCtx);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.MyAppDbAdapter",
          "SQLiteException - Class constructor");
      errExcpError = null;
    }// end try/catch (Exception error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this.mCtx);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.MyAppDbAdapter",
          "Class constructor");
      errExcpError = null;
    } finally {
      DBUtil.safeCloseDataBase(this.mDb);
    }// end try/catch (Exception error)
  }// end constructor

  /**
   * public void onCreate
   * 
   * Executes SQLite commands to create tables in the database.
   * 
   * @param: SQLiteDatabase object
   * 
   * @return: void
   * 
   * @throws SQLException
   *           if the SQL scripts encounter issues
   */
  @Override
  public void onCreate(SQLiteDatabase db) throws SQLException {
    try {
      db.execSQL(MY_MUSIC_DATABASE_CREATE);
      db.execSQL(MY_PREFS_DATABASE_CREATE);

    } catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.mCtx);
      errExcpError.addToLogFile(error, "DatabaseHelper.onCreate",
          "database Table creates");
      errExcpError = null;
    }// end try/catch (SQLException error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this.mCtx);
      errExcpError.addToLogFile(error, "DatabaseHelper.onCreate",
          "creating Table creates");
      errExcpError = null;
    }// end try/catch (Exception error)
  }// end onCreate

  /**
   * Handles the logging and SQL scripts for upgrade actions
   * 
   * When creating an upgrade, make sure the code here reflects what needs to be
   * done.
   * 
   * For example, if the table structure has been changed
   * 
   * @return void
   * 
   * @param database
   * @param oldVersion
   * @param newVersion
   * 
   * @throws SQLException
   *           if the SQL scripts encounter issues
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
      throws SQLException {
    try {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
          + newVersion + ".");

      if (this.objMusicDbAdapterRef.objDisplayAlertClass != null) {
        this.objMusicDbAdapterRef.objDisplayAlertClass.cleanUpClassVars();
        this.objMusicDbAdapterRef.objDisplayAlertClass = null;
      }// end if (objDisplayAlertClass != null)
      this.objMusicDbAdapterRef.objDisplayAlertClass = new MyDisplayAlertClass(
          this.mCtx, new CustAlrtMsgOptnListener(MessageCodes.ALERT_TYPE_MSG),
          "Upgrading Database", "Upgrading database from version " + oldVersion
              + " to " + newVersion + ".  Existing Data will be preserved.");

      if (oldVersion == 1 && newVersion > 1) {
        // execute code to handle database changes
      }// end initial create
    } catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.mCtx);
      errExcpError.addToLogFile(error, "DatabaseHelper.onUpgrade",
          "upgrading the database");
      errExcpError = null;
    }// end try/catch (SQLException error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this.mCtx);
      errExcpError.addToLogFile(error, "DatabaseHelper.onUpgrade",
          "upgrading the database");
      errExcpError = null;
    }// end try/catch (Exception error)
  }// end onUpgrade

  /**
   * Create and/or open a database that will be used for reading and writing.
   * Once opened successfully, the database is cached, so you can call this
   * method every time you need to write to the database. Make sure to call
   * close() when you no longer need it.
   * 
   * Errors such as bad permissions or a full disk may cause this operation to
   * fail, but future attempts may succeed if the problem is fixed.
   * 
   * Returns a read/write database object valid until close() is called
   * 
   * Throws SQLiteException if the database cannot be opened for writing
   */

  @Override
  public SQLiteDatabase getWritableDatabase() throws SQLiteException {
    try {
      this.mDb = SQLiteDatabase.openDatabase(this.dbPathToUse + File.separator
          + MyAppDbAdapter.MY_DATABASE_NAME, null,
          SQLiteDatabase.OPEN_READWRITE);

      return this.mDb;
    } catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.mCtx);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.getWritableDatabase",
          "SQLException - main try/catch");
      errExcpError = null;

      return null;
    }// end try/catch (SQLException error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this.mCtx);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.getWritableDatabase",
          "main try/catch");
      errExcpError = null;

      return null;
    }// end try/catch (Exception error)
  }// end getWriteableDatabase

  /**
   * Create and/or open a database.
   * 
   * This will be the same object returned by getWritableDatabase() unless some
   * problem, such as a full disk, requires the database to be opened read-only.
   * 
   * In that case, a read-only database object will be returned.
   * 
   * If the problem is fixed, a future call to getWritableDatabase() may
   * succeed, in which case the read-only database object will be closed and the
   * read/write object will be returned in the future.
   * 
   * Returns a database object valid until getWritableDatabase() or close() is
   * called.
   * 
   * Throws SQLiteException if the database cannot be opened
   * 
   */
  @Override
  public SQLiteDatabase getReadableDatabase() throws SQLiteException {
    try {
      this.mDb = SQLiteDatabase.openDatabase(this.dbPathToUse + File.separator
          + MyAppDbAdapter.MY_DATABASE_NAME, null,
          SQLiteDatabase.OPEN_READONLY);

      return this.mDb;
    }// end try
    catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.mCtx);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.getReadableDatabase",
          "main try/catch");
      errExcpError = null;

      return null;
    }// end try/catch (SQLException error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this.mCtx);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.getReadableDatabase",
          "main try/catch");
      errExcpError = null;

      return null;
    }// end try/catch (Exception error)
  }// end getReadableDatabase

  /**
   * Close the database.
   * 
   * @return void
   * 
   * @param none
   * 
   */
  public void close() {
    if (this.mDb != null && this.mDb.isOpen()) {
      DBUtil.safeCloseDataBase(this.mDb);
      this.mDb = null;
    }
  }// end close()

  /**
   * Open the database. If it cannot be opened, try to create a new instance of
   * the database. If it cannot be created, throw an exception to signal the
   * failure
   * 
   * @return this (self reference, allowing this to be chained in an
   *         initialization call)
   * 
   * @param mCtx
   *          the Context within which to work
   * 
   * @throws SQLException
   *           if the database could be neither opened or created
   */
  protected SQLiteDatabase open() throws SQLException {
    this.mDb = super.getWritableDatabase();
    return this.mDb;
  }// end open()

  /**
   * boolean dbIsOpen
   * 
   * @return true if the DB is currently open (has not been closed)
   */
  protected boolean dbIsOpen() {
    // check if the database object is open
    if (this.mDb != null && this.mDb.isOpen())
      return true;
    else
      return false;
  }// end dbIsOpen
}// end MyAppDbAdapter class
