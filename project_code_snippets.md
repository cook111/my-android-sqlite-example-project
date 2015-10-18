# List Activity onResume Method #

```
/**
   * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for
   * your activity to start interacting with the user.
   * 
   * This is a good place to begin animations, open exclusive-access devices
   * (such as the camera), etc.
   * 
   * the data is refreshed
   * 
   */
  @Override
  protected void onResume() {
    /* query sorting options */
    super.onResume();
    try {
      // set database query class object
      this.dbAppDbObj = new MyAppDbSQL(this);

      Music_List.getSortOption(Music_List.this);

      this.fillData();

      this.registerForContextMenu(this.getListView());
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          Music_List.this);
      errExcpError.addToLogFile(error, "Music_List.resumeData", "");
      errExcpError = null;
    }// end try/catch (Exception error)
  }// end onResume
```

# List Activity fillData and convText Methods #
```
/**
   * fillData: Get all of the rows from the database and create the item list
   * 
   * @return void
   * 
   */
  private void fillData() {
    // get a cursor of the entries
    // number
    try {
      boolean dbOpenResult = this.dbAppDbObj.openDbAdapter();

      if (dbOpenResult) {
        this.mEntryCursor = this.dbAppDbObj
            .fetchMusic(Music_List.intSortOption);

        this.startManagingCursor(this.mEntryCursor);

        boolean dbCloseResult = this.dbAppDbObj.closeDbAdapter();

        if (!dbCloseResult)
          throw new Exception("The database was not successfully closed.");

        // Create an array to specify the fields we want to display in the list
        // (only TITLE)
        String[] from = new String[] { MyAppDbAdapter.KEY_ALBUM,
            MyAppDbAdapter.KEY_ALBUMDATE, MyAppDbAdapter.KEY_ARTIST,
            MyAppDbAdapter.KEY_GENRE };

        // and an array of the fields we want to bind those fields to (in this
        // case just text1)
        int[] to = new int[] { R.id.txtalbum, R.id.txtalbumdate,
            R.id.txtartist, R.id.txtgenre };

        // Now create a simple cursor adapter and set it to display
        this.listRegister = new SimpleCursorAdapter(this,
            R.layout.musicentryrow, this.mEntryCursor, from, to) {
          @Override
          public void setViewText(TextView v, String text) {
            super.setViewText(v, convText(v, text));
          }
        };
        setListAdapter(this.listRegister);
        this.getListView().setTextFilterEnabled(false);
        this.getListView().setFastScrollEnabled(true);

        this.mEntryCursor.moveToPosition(this.intEntryListPosition);
        this.getListView().scrollTo(0, this.intEntryListPosition);
        this.getListView().setSelection(this.intEntryListPosition);
      }
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          Music_List.this);
      errExcpError.addToLogFile(error, "Music_List.fillData", "");
      errExcpError = null;
    }// end try/catch (Exception error)
  }// end fillData

  /**
   * convText: Code to run-time conversion for data returned by the cursor
   * before displaying data type
   * 
   * @param TextView
   *          v The textview being processed
   * @param String
   *          text used for passing the processed result
   * 
   * @return void
   * 
   */
  private String convText(TextView v, String text) {
    switch (v.getId()) {
    case R.id.txtalbumdate:
      try {
        String strCustDate = "";
        String strOrigDate = "";

        // query the date from the database table
        strOrigDate = this.mEntryCursor.getString(this.mEntryCursor
            .getColumnIndex(MyAppDbAdapter.KEY_ALBUMDATE));

        // create the date formatters
        SimpleDateFormat dateISOFormatter = new SimpleDateFormat(
            getString(R.string.DATE_FORMAT_ISO8601));

        SimpleDateFormat dateStringFormatter = new SimpleDateFormat(
            getString(R.string.DATE_FORMAT_ENTRY_UPDATE));

        // put the formatted string into a Date format
        Date myDateFromString = dateISOFormatter.parse(strOrigDate);

        // format the date into a formatted date-string.
        strCustDate = dateStringFormatter.format(myDateFromString);

        // cleanup the dateformatter object
        dateISOFormatter = null;

        return strCustDate;
      } catch (Exception error) {
        MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
            Music_List.this);
        errExcpError.addToLogFile(error, "Music_List.convText",
            "formatting the date");
        errExcpError = null;
      }// end try/catch (Exception error)
    }// end switch

    return text;
  }// end convText
```