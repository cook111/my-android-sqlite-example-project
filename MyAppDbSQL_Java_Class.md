
```
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Application Database Actions class.
 * 
 * Executes the database actions such as insert, update, delete, etc.
 * 
 * 
 * @constructor context
 * 
 */

public class MyAppDbSQL {
  private MyAppDbAdapter dbAdapterObj;
  private SQLiteDatabase sqliteDBObj;
  private Context ctxContext;

  /**
   * MyAppDbSQL Class Constructor
   */
  protected MyAppDbSQL(Context ctxContext) {
    try {
      this.ctxContext = ctxContext;
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbSQL.MyAppDbSQL",
          "exception thrown in the class constructor");
      errExcpError = null;
    }// end try/catch (Exception error)
  }// end constructor

  /**
   * Database Query methods
   */

  /**
   * listOrderByOpts
   * 
   * @param int intOrderByOpt - the OrderBy Option to return
   * @return String
   * 
   */
  protected String listOrderByOpts(int intOrderByOpt) {
    switch (intOrderByOpt) {
    case 0:
      return MyAppDbAdapter.KEY_ALBUM + " ASC";

    case 1:
      return MyAppDbAdapter.KEY_ALBUM + " DESC";

    case 2:
      return MyAppDbAdapter.KEY_ARTIST + " ASC";

    case 3:
      return MyAppDbAdapter.KEY_ARTIST + " DESC";

    case 4:
      return MyAppDbAdapter.KEY_ALBUMDATE + "  ASC";

    case 5:
      return MyAppDbAdapter.KEY_ALBUMDATE + " DESC";

    case 6:
      return MyAppDbAdapter.KEY_GENRE + "  ASC";

    case 7:
      return MyAppDbAdapter.KEY_GENRE + " DESC";

    default:
      return MyAppDbAdapter.KEY_ARTIST + " ASC";
    }// end switch
  }// end listOrderByOpts

  protected boolean openDbAdapter() throws Exception {
    boolean isOpen = false;

    try {
      if (this.dbAdapterObj == null || !(this.dbAdapterObj.dbIsOpen())) {
        this.dbAdapterObj = new MyAppDbAdapter(ctxContext);

        if (this.dbAdapterObj != null)
          this.sqliteDBObj = this.dbAdapterObj.getReadableDatabase();
      }// end if (this.dbMusic == null || ...

      if (this.dbAdapterObj.dbIsOpen() && this.sqliteDBObj != null) {
        isOpen = true;
      }
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbSQL.openDbAdapter",
          "exception thrown opening the db adapter");
      errExcpError = null;

      isOpen = false;
    }// end try/catch (Exception error)

    return isOpen;
  }

  protected boolean closeDbAdapter() throws Exception {
    boolean isClosed = false;

    try {
      // clean up DB objects
      if (this.sqliteDBObj != null) {
        this.sqliteDBObj.close();
        this.sqliteDBObj = null;
      }

      if (this.dbAdapterObj != null) {
        this.dbAdapterObj.close();
        this.dbAdapterObj = null;
      }

      isClosed = true;
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbSQL.closeDbAdapter",
          "exception thrown closing the db adapter");
      errExcpError = null;

      isClosed = false;
    }// end try/catch (Exception error)

    return isClosed;
  }

  /**
   * fetchMusic: Return a Cursor over the list of entries in the database
   * 
   * @param intSortOpt
   *          the selected sorting option
   * @return Cursor containing filtered query results
   * @throws SQLException
   *           if entry could not be found/retrieved
   */
  protected Cursor fetchMusic(int intSortOpt) throws SQLException {
    String strOrderBy = "";
    Cursor mMusicCursor = null;

    try {
      strOrderBy = listOrderByOpts(intSortOpt);

      mMusicCursor = this.sqliteDBObj.query(MyAppDbAdapter.MY_MUSIC_DB_TABLE,
          new String[] { MyAppDbAdapter.KEY_ROWID, MyAppDbAdapter.KEY_ALBUM,
              MyAppDbAdapter.KEY_ALBUMDATE, MyAppDbAdapter.KEY_ARTIST,
              MyAppDbAdapter.KEY_GENRE, MyAppDbAdapter.KEY_SONG_TITLE,
              MyAppDbAdapter.KEY_NOTES }, null, null, null, null, strOrderBy);

      if (mMusicCursor != null) {
        mMusicCursor.moveToFirst();

        return mMusicCursor;
      } else {
        return null;
      }// end if (mMusicCursor != null)
    } catch (SQLiteException error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbSQL.fetchMusic",
          "SQLiteException - exception thrown fetching music");
      errExcpError = null;

      return null;
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbSQL.fetchMusic",
          "exception - exception thrown fetching music");
      errExcpError = null;

      return null;
    }// end try/catch (Exception error)
  }// end fetchMusic(int intSortOpt)

  /**
   * Return a Cursor positioned at the entry that matches the given rowId
   * 
   * @param rowId
   *          id of entry to retrieve
   * @return Cursor positioned to matching entry, if found
   * @throws SQLException
   *           if entry could not be found/retrieved
   */
  protected Cursor fetchListEntry(long rowId) throws SQLException {

    Cursor mCursor = null;

    try {
      mCursor = this.sqliteDBObj.query(true, MyAppDbAdapter.MY_MUSIC_DB_TABLE,
          new String[] { MyAppDbAdapter.KEY_ROWID, MyAppDbAdapter.KEY_ALBUM,
              MyAppDbAdapter.KEY_ALBUMDATE, MyAppDbAdapter.KEY_ARTIST,
              MyAppDbAdapter.KEY_GENRE, MyAppDbAdapter.KEY_SONG_TITLE,
              MyAppDbAdapter.KEY_NOTES }, MyAppDbAdapter.KEY_ROWID + "="
              + rowId, null, null, null, null, null);

      if (mCursor != null) {
        mCursor.moveToFirst();

        return mCursor;
      } else {
        return null;
      }// end if (mCursor != null)
    } catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.fetchListEntry",
          "ListEntry query");
      errExcpError = null;

      return null;
    }// end try/catch (SQLException error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.fetchListEntry",
          "ListEntry query");
      errExcpError = null;

      return null;
    }// end try/catch (Exception error)
  }// end fetchListEntry(long rowId)

  /**
   * exportTableQuery: Return a Cursor for a specific table in the database
   * 
   * @param strTableName
   *          the specified table
   * 
   * @param strOrderBy
   *          the selected ordering option
   * 
   * @return Cursor containing filtered query results
   * @throws SQLException
   *           if entry could not be found/retrieved
   */
  protected Cursor exportTableQuery(String strTableName, String strOrderBy) {
    Cursor mMusicCursor = null;

    try {
      // get everything from the table
      String strSQL = "SELECT * FROM " + strTableName + " ORDER BY "
          + strOrderBy;

      mMusicCursor = this.sqliteDBObj.rawQuery(strSQL, null);

      if (mMusicCursor != null) {
        mMusicCursor.moveToFirst();

        return mMusicCursor;
      } else {
        return null;
      }// end if (mMusicCursor != null
    } catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.exportTableQuery",
          "creating export temporary Cursor");
      errExcpError = null;

      return null;
    }// end try/catch (SQLException error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.exportTableQuery",
          "creating export temporary Cursor");
      errExcpError = null;

      return null;
    }// end try/catch (Exception error)
  }// end exportTableQuery

  /**
   * createMusicEntry: Creates a new entry in the database table
   * 
   * @return True if the database insert succeeds
   * @throws SQLException
   *           if entry could not be found/retrieved
   */
  protected Boolean createMusicEntry(String txtAlbum, String albumdate,
      String txtartist, String txtgenre, String txtsongname, String txtnotes) {
    boolean blIsSuccessful = false;

    this.sqliteDBObj.beginTransaction();
    try {
      ContentValues initialValues = new ContentValues();

      initialValues.put(MyAppDbAdapter.KEY_ALBUM, txtAlbum);
      initialValues.put(MyAppDbAdapter.KEY_ALBUMDATE, albumdate);
      initialValues.put(MyAppDbAdapter.KEY_ARTIST, txtartist);
      initialValues.put(MyAppDbAdapter.KEY_GENRE, txtgenre);
      // initialValues.put(KEY_SONG_TITLE, txtsongname);
      initialValues.put(MyAppDbAdapter.KEY_NOTES, txtnotes);

      blIsSuccessful = (this.sqliteDBObj.insert(
          MyAppDbAdapter.MY_MUSIC_DB_TABLE, null, initialValues) != -1);

      if (blIsSuccessful == true) {
        this.sqliteDBObj.setTransactionSuccessful();
      }
    }// end try
    catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.createMusicEntry",
          "creating music entry");
      errExcpError = null;
      blIsSuccessful = false;
    }// end try/catch (Exception error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.createMusicEntry",
          "creating music entry");
      errExcpError = null;
      blIsSuccessful = false;
    }// end try/catch (Exception error)
    finally {
      this.sqliteDBObj.endTransaction();
    }

    return blIsSuccessful;
  }// end createMusicEntry

  /**
   * updateMusicEntry: Updates an existing entry in the database table
   * 
   * @return True if the database update succeeds
   * @throws SQLException
   *           if entry could not be found/retrieved
   */
  protected boolean updateMusicEntry(Long rowId, String strAlbum,
      String strPubDate, String strArtist, String strGenre,
      String strSongTitle, String strNotes, String strOrigAlbum,
      String strOrigPubDate, String strOrigArtist, String strOrigGenre) {
    boolean blIsSuccessful = false;

    this.sqliteDBObj.beginTransaction();
    try {
      ContentValues args = new ContentValues();

      args.put(MyAppDbAdapter.KEY_ALBUM, strAlbum);
      args.put(MyAppDbAdapter.KEY_ALBUMDATE, strPubDate);
      args.put(MyAppDbAdapter.KEY_ARTIST, strArtist);
      args.put(MyAppDbAdapter.KEY_GENRE, strGenre);
      args.put(MyAppDbAdapter.KEY_NOTES, strNotes);

      blIsSuccessful = (this.sqliteDBObj.update(
          MyAppDbAdapter.MY_MUSIC_DB_TABLE, args, MyAppDbAdapter.KEY_ROWID
              + "=" + rowId, null) != -1);

      if (blIsSuccessful == true) {
        this.sqliteDBObj.setTransactionSuccessful();
      }
    }// end try
    catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.updateMusicEntry",
          "updating music entry information");
      errExcpError = null;
      blIsSuccessful = false;
    }// end try/catch (Exception error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.updateMusicEntry",
          "updating music entry information");
      errExcpError = null;
      blIsSuccessful = false;
    }// end try/catch (Exception error)
    finally {
      this.sqliteDBObj.endTransaction();
    }

    return blIsSuccessful;
  }// end updateMusicEntry

  /**
   * Delete the entries
   * 
   * 
   * @return true if deleted, false otherwise
   * 
   * @throws SQLException
   *           if the SQL scripts encounter issues
   */
  protected boolean deleteMusicEntries() throws SQLException {
    // delete entries
    boolean blIsSuccessful = false;

    this.sqliteDBObj.beginTransaction();
    try {
      blIsSuccessful = this.sqliteDBObj.delete(
          MyAppDbAdapter.MY_MUSIC_DB_TABLE, "1", null) > 0;
      if (blIsSuccessful == true) {
        this.sqliteDBObj.setTransactionSuccessful();
      }
    }// end try
    catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.deleteMusicEntries",
          "deleting music entries");
      errExcpError = null;
      blIsSuccessful = false;
    }// end try/catch (Exception error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.deleteMusicEntries",
          "deleting music entries");
      errExcpError = null;
      blIsSuccessful = false;
    }// end try/catch (Exception error)
    finally {
      this.sqliteDBObj.endTransaction();
    }

    return blIsSuccessful;
  }// end deleteMusicEntries(String strAcctName)

  /**
   * Delete the Music Entry per the given rowID
   * 
   * @param rowId
   *          rowId of the Music Entry in the Music table to be deleted
   * 
   * @return true if the delete is successful, false otherwise.
   * 
   * @throws SQLException
   *           if the SQL scripts encounter issues
   */
  protected boolean deleteMusicEntry(long rowId) throws SQLException {
    // delete a single music entry in the music table
    boolean blIsSuccessful = false;

    this.sqliteDBObj.beginTransaction();
    try {
      Cursor crsrListRowData;

      crsrListRowData = fetchListEntry(rowId);

      if (crsrListRowData.getCount() >= 0) {
        blIsSuccessful = this.sqliteDBObj.delete(
            MyAppDbAdapter.MY_MUSIC_DB_TABLE, MyAppDbAdapter.KEY_ROWID + "="
                + rowId, null) > 0;
      } else {
        blIsSuccessful = true;
      }

      crsrListRowData.close();
      crsrListRowData = null;

      if (blIsSuccessful == true) {
        this.sqliteDBObj.setTransactionSuccessful();
      }
    }// end try
    catch (SQLException error) {
      MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.deleteMusicEntry",
          "deleting music entry");
      errExcpError = null;
      blIsSuccessful = false;
    }// end try/catch (Exception error)
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          this.ctxContext);
      errExcpError.addToLogFile(error, "MyAppDbAdapter.deleteMusicEntry",
          "deleting music entry");
      errExcpError = null;
      blIsSuccessful = false;
    }// end try/catch (Exception error)
    finally {
      this.sqliteDBObj.endTransaction();
    }

    return blIsSuccessful;
  }// end deleteMusicEntry(long rowId)
}// end MyAppDbSQL class
```