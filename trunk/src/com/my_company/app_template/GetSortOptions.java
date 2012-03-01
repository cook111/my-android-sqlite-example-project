package com.my_company.app_template;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GetSortOptions {
  private Context ctxContext;
  private String strCallingClass = "";
  private String strSortKey = "";
  private final static String strDefaultSortOption = "-1";
  private final static int intDefaultSortOption = -1;

  /**
   * Constructor - takes the context to allow the database to be opened/created
   * 
   * @param ctx
   *          the Context within which to work
   */

  GetSortOptions(final Context ctx, final String strParamCallingClass,
      final String strParamSortKey) {
    this.ctxContext = ctx;
    this.strCallingClass = strParamCallingClass;
    this.strSortKey = strParamSortKey;
  }// end constructor

  protected int getIntSortOption(String strPrefs_File) {
    int intSortOption = GetSortOptions.intDefaultSortOption;
    int intPrefKeyIndex = intDefaultSortOption;
    SharedPreferences spSortOpt = null;

    try {
      if (strPrefs_File != null && !strPrefs_File.equals("")
          && !strPrefs_File.equals(" ")) {
        spSortOpt = this.ctxContext.getSharedPreferences(strPrefs_File,
            Context.MODE_PRIVATE);
      } else {
        spSortOpt = PreferenceManager
            .getDefaultSharedPreferences(this.ctxContext);
      }

      if (spSortOpt != null) {
        String strSortOptVal = spSortOpt.getString(this.strSortKey,
            GetSortOptions.strDefaultSortOption);

        if (strSortOptVal != null && !strSortOptVal.equals("")
            && !strSortOptVal.equals(" ")) {
          intPrefKeyIndex = Integer.parseInt(strSortOptVal);
          if (intPrefKeyIndex >= 0) {
            intSortOption = intPrefKeyIndex;
          }// end if (intPrefKeyIndex >= 0)
        }// end if (strSortOptVal != null &&...
      }// end if (spSortOpt != null)
    }// end try
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          this.ctxContext);
      errExcpError.addToLogFile(error, this.strCallingClass + ".getSortOption",
          "An Error Occured, Sort Option set to default, sorted by rowID");

      errExcpError = null;
      intSortOption = -1;
    }// end try/catch (Exception error)

    return intSortOption;
  }// end getSortOption

}// end GetSortOptions
