/* Copyright (c) 2008-2009 -- CommonsWare, LLC

	 Licensed under the Apache License, Version 2.0 (the "License");
	 you may not use this file except in compliance with the License.
	 You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

	 Unless required by applicable law or agreed to in writing, software
	 distributed under the License is distributed on an "AS IS" BASIS,
	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 See the License for the specific language governing permissions and
	 limitations under the License.
 */

package com.my_company.app_template;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class EditSortPreferences extends PreferenceActivity {
  String strPrefs_Key = "";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      Bundle extras = getIntent().getExtras();
      if (extras != null) {
        int intPrefs_File = extras.getInt("PREFERENCE_FILE");
        strPrefs_Key = extras.getString("PREFERENCE_KEY");

        if (intPrefs_File < 0 || intPrefs_File > 0) {
          addPreferencesFromResource(intPrefs_File);
          if (strPrefs_Key != null && !strPrefs_Key.equals("")
              && !strPrefs_Key.equals(" ")) {
            ListPreference lpSortOptList = (ListPreference) findPreference(strPrefs_Key);
            if (lpSortOptList != null) {
              setSortSummary(lpSortOptList.getEntry());
            }// end if(lpSortOptList != null)
          }// end if (strPrefs_Key != null &&...

          Preference customPref = (Preference) findPreference(strPrefs_Key);
          customPref
              .setOnPreferenceChangeListener(myOnPreferenceChangeListener);
        }// end if (intPrefs_File < 0 || intPrefs_File > 0)
      }// end if (extras != null)
      else {
        
        
        finish();
      }// end if/else
    }// end try
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this);
      errExcpError.addToLogFile(error, "EditSortPreferences.onCreate",
          "addPreferencesFromResource");
      errExcpError = null;
    }// end try/catch (Exception error)
  }// end onCreate

  private OnPreferenceChangeListener myOnPreferenceChangeListener = new OnPreferenceChangeListener() {
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
      setSortSummary(getSortSummary(newValue));
      
      /*
       *  finish();
       */
      return true;
    }// end onPreferenceChange
  };// end setOnPreferenceChangeListener

  /**
   * getSortSummary: set the summary message which displays the selected sorting
   * option
   * 
   * @return CharSequence
   * 
   */
  private CharSequence getSortSummary(Object objValue) {
    /* display the selected sorting option */
    CharSequence charSeqSelectedValue = "";
    ListPreference etSortOptions = (ListPreference) findPreference(strPrefs_Key);
    int intPrefKeyIndex = -1;

    if (etSortOptions != null) {
      if (objValue != null) {
        // the ListPreference list is 0-based.
        intPrefKeyIndex = Integer.parseInt((String) objValue);
      } else {
        intPrefKeyIndex = Integer.parseInt(etSortOptions.getValue());
      }
      charSeqSelectedValue = etSortOptions.getEntries()[intPrefKeyIndex];
      intPrefKeyIndex = -1;
      etSortOptions = null;
    }// end if etSortOptions != null)

    return charSeqSelectedValue;

  }// end getSortSummary

  /**
   * setSortSummary: set the summary message which displays the selected sorting
   * option
   * 
   * @return void
   * 
   */
  private void setSortSummary(CharSequence charSeqValue) {
    /* display the selected sorting option */
    ListPreference etSortOptions = (ListPreference) findPreference(strPrefs_Key);

    if (etSortOptions != null && !charSeqValue.equals("")) {
      refreshSummary(etSortOptions, "Selected Option: " + charSeqValue);
      etSortOptions = null;
      return;
    }// end if etSortOptions != null)
  }// end setSortSummary

  /**
   * refreshSummary: refreshes the summary for a preference
   * 
   * @return void
   * 
   */

  private void refreshSummary(Object objPref, String strSummary) {
    ((Preference) objPref).setSummary(strSummary);
    return;
  }// end refreshSummary

  /**
   * onResume: Cancel button Listener
   * 
   * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for
   * your activity to start interacting with the user.
   * 
   * This is a good place to begin animations, open exclusive-access devices
   * (such as the camera), etc.
   * 
   * Per the conditional check results, cleanup is performed and then the
   * program returns focus to the calling code.
   * 
   */
  @Override
  protected void onResume() {
    try {
      Bundle extras = getIntent().getExtras();
      if (extras != null) {
        String strQuitStatus = extras.getString("QUITTING");

        if (strQuitStatus != null && strQuitStatus.equals("TRUE")) {
          // exit application
          
          finish();
        }
      }// end if (extras != null)
    }// end try
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this);
      errExcpError.addToLogFile(error, "EditSortPreferences.onResume", " ");
      errExcpError = null;
    }// end try/catch (Exception error)
    super.onResume();
  }// end onResume

  /**
   * onPause method
   * 
   * Called as part of the activity lifecycle when an activity is going into the
   * background, but has not (yet) been killed. The counterpart to onResume().
   * 
   * This callback is mostly used for saving any persistent state the activity
   * is editing, to present a "edit in place" model to the user and making sure
   * nothing is lost if there are not enough resources to start the new activity
   * without first killing this one.
   * 
   * This is also a good place to do things like stop animations and other
   * things that consume a noticeable mount of CPU in order to make the switch
   * to the next activity as fast as possible, or to close resources that are
   * exclusive access such as the camera.
   * 
   * Checks the current state. If in "QUIT" state, then the cleanup and finish
   * code is executed.
   * 
   * "SAVEINSTANCE" is used for when the screen orientation is changed.
   * 
   * @return void
   * 
   */
  @Override
  protected void onPause() {
    // The user is going somewhere else, so make sure their current
    // changes are safely saved away in the provider. We don't need
    // to do this if only editing.
    super.onPause();
    
    try {
      if (APPGlobalVars.SCR_PAUSE_CTL != null
          && APPGlobalVars.SCR_PAUSE_CTL.equals("QUIT")) {
        APPGlobalVars.SCR_PAUSE_CTL = "";

        
        
        finish();
      }// end if (APPGlobalVars.SCR_PAUSE_CTL != null &&...
    }// end try
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this);
      errExcpError.addToLogFile(error, "EditSortPreferences.onPause", " ");
      errExcpError = null;
    }// end try/catch (Exception error)
  }// end onPause

  /**
   * onDestroy method
   * 
   * Perform any final cleanup before an activity is destroyed.
   * 
   * This can happen either because the activity is finishing (someone called
   * finish() on it, or because the system is temporarily destroying this
   * instance of the activity to save space.
   * 
   * You can distinguish between these two scenarios with the isFinishing()
   * method.
   * 
   * Note: do not count on this method being called as a place for saving data!
   * For example, if an activity is editing data in a content provider, those
   * edits should be committed in either onPause() or
   * onSaveInstanceState(Bundle), not here.
   * 
   * This method is usually implemented to free resources like threads that are
   * associated with an activity, so that a destroyed activity does not leave
   * such things around while the rest of its application is still running.
   * 
   * There are situations where the system will simply kill the activity's
   * hosting process without calling this method (or any others) in it, so it
   * should not be used to do things that are intended to remain around after
   * the process goes away.
   * 
   * Derived classes must call through to the super class's implementation of
   * this method. If they do not, an exception will be thrown.
   * 
   * @return void
   * 
   */
  @Override
  protected void onDestroy() {
    // The user is going somewhere else, so make sure their current
    // changes are safely saved away in the provider. We don't need
    // to do this if only editing.
    myAppAcctCleanup();

    super.onDestroy();
  }// end onDestroy

  /**
   * myAppAcctCleanup method
   * 
   * Sets object variables to null
   * 
   * @return void
   * 
   */
  protected static void myAppAcctCleanup() {
    /* Set object variables to null */
    
  }// end myAppAcctCleanup

  /**
   * onKeyDown method
   * 
   * Executes code depending on what keyCode is pressed.
   * 
   * @param int keyCode
   * @param KeyEvent
   *          event KeyEvent object
   * 
   * @return true if the code completes execution, false otherwise
   * 
   */
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
    case KeyEvent.KEYCODE_BACK:
      /* perform cleanup */
      
      
      finish();
      return true;
    default:
      return false;
    }
  }// end onKeyDown

  /**
   * onCreateOptionsMenu method
   * 
   * Initialize the contents of the Activity's standard options menu. You should
   * place your menu items in to menu.
   * 
   * This is only called once, the first time the options menu is displayed.
   * 
   * To update the menu every time it is displayed, see
   * onPrepareOptionsMenu(Menu).
   * 
   * @param menu
   *          The options menu in which you place your items.
   * 
   * @return Must return true for the menu to be displayed; if you return false
   *         it will not be shown.
   * 
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.preference_menu, menu);
    return true;
  }// end onCreateOptionsMenu

  /**
   * onMenuItemSelected method
   * 
   * onMenuItemSelected method Executes code per the menu item selected on the
   * menu options that appears when the menu button is pressed
   * 
   * @param int featureID: The panel that the menu is in.
   * @param MenuItem
   *          item: The menu item that was selected.
   * 
   * @return Return true to finish processing of selection, or false to perform
   *         the normal menu handling (calling its Runnable or sending a Message
   *         to its target Handler).
   * 
   */
  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    switch (item.getItemId()) {
    case R.id.back_menu_option:
      
      
      finish();
      break;
      
    case R.id.quit:
      APPGlobalVars.SCR_PAUSE_CTL = "QUIT";
      // perform cleanup
      Intent intentQuitActivity = new Intent(EditSortPreferences.this,
          Music_List.class);
      intentQuitActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intentQuitActivity.putExtra("QUITTING", "TRUE");
      startActivity(intentQuitActivity);
      break;
    }
    return super.onMenuItemSelected(featureId, item);
  }

}// end EditSortPreferences class

