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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.KeyEvent;

public class AppPreferences extends PreferenceActivity {
  private String strPrefs_Key = "";
  private static MyDisplayAlertClass objDisplayAlertClass;
  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
		  Bundle extras = getIntent().getExtras();
      if (extras != null) {
        int intPrefs_File = extras.getInt("PREFERENCE_FILE");
        int intPrefs_Layout_File = extras.getInt("PREFERENCE_LAYOUT_FILE");
        strPrefs_Key = extras.getString("PREFERENCE_KEY");
        
        //strPrefs_AcctListSort_Key = extras.getString("PREF_ACCTLISTSORT_KEY");
        
        if (intPrefs_Layout_File < 0 || intPrefs_Layout_File > 0){
          if (intPrefs_File < 0 || intPrefs_File > 0){
            getPreferenceManager().setSharedPreferencesName(getString(intPrefs_File));
          }
          addPreferencesFromResource(intPrefs_Layout_File);
          
          if (this.findPreference(strPrefs_Key) != null){
            this.findPreference(strPrefs_Key).setOnPreferenceChangeListener(myOnPreferenceChangeListener);
          }
        }//end if (intPrefs_File < 0 || intPrefs_File > 0)
      }//end if (extras != null)
      else {
        
        
        finish();
      }//end if/else
		}//end try
		catch (Exception error){
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this);
      errExcpError.addToLogFile(error, "AppPreferences.onCreate", "addPreferencesFromResource");
      errExcpError = null;
    }//end try/catch (Exception error)
	}//end onCreate
  
	private OnPreferenceChangeListener myOnPreferenceChangeListener = new OnPreferenceChangeListener() {
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {  
      /*
      if (preference.getKey().equals("AcctListSortOpt")){
        setSortSummary(getSortSummary(newValue));
      }//end if (preference.getKey().equals("AcctListSortOpt"))
      */
      
      return true;
    }//end onPreferenceChange
  };//end myOnPreferenceChangeListener
  
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
  protected void onResume(){
    super.onResume();   
  }//end onResume
  
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
      
      
      finish();
      return true;

    default:
      return false;
    }
  }// end onKeyDown

  /**
   * onStop method
   * 
   * Perform actions when the Activity is hidden from view
   * 
   * @return void
   * 
   */
  @Override
  protected void onStop() {
    super.onStop();
    /* Set object variables to null */
    if (AppPreferences.objDisplayAlertClass != null){
      AppPreferences.objDisplayAlertClass = null;
    }
  }//end onStop
  
}//end AppPreferences class

