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
import android.preference.PreferenceActivity;

public class EditAppSettings extends PreferenceActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      Bundle extras = getIntent().getExtras();
      if (extras != null) {
        int intPrefs_File = extras.getInt("PREFERENCE_FILE");

        if (intPrefs_File < 0 || intPrefs_File > 0) {
          addPreferencesFromResource(intPrefs_File);
        }// end if (intPrefs_File < 0 || intPrefs_File > 0)
      }// end if (extras != null)
      else {
        
        
        finish();
      }// end if/else
    }// end try
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this);
      errExcpError.addToLogFile(error, "AppPreferences.onCreate",
          "addPreferencesFromResource");
      errExcpError = null;
    }// end try/catch (Exception error)
  }// end onCreate
}// end EditAppSettings class

