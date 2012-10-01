package com.my_company.app_template;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SplashScreen extends Activity {
  private String android_id;
  protected static SplashScreen objSScreen;
  private static MyDisplayAlertClass objDisplayAlertClass;

  // private static ServerManagedPolicy serverPolicy;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    try {
      setContentView(R.layout.my_splash_screen);
      objSScreen = this;
      APPGlobalVars.SCR_PAUSE_CTL = "";
      
          Intent i = new Intent(SplashScreen.this, Music_List.class);
          startActivity(i);
  
          finish();
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          SplashScreen.this);
      errExcpError.addToLogFile(error, "SplashScreen.onCreate", "");
      errExcpError = null;
      return;
    }// end try/catch (Exception error)
  }// end onCreate

  public void toast(String string) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
  }// end toast

  @Override
  protected void onDestroy() {
    super.onDestroy();
    try {
      if (objDisplayAlertClass != null) {
        objDisplayAlertClass = null;
      }

    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          SplashScreen.this);
      errExcpError.addToLogFile(error, "SplashScreen..onDestroy", "no prompt");
      errExcpError = null;
      return;
    }// end try/catch (Exception error)

    
  }// end onDestroy

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
    try {
      if (APPGlobalVars.SCR_PAUSE_CTL != null
          && APPGlobalVars.SCR_PAUSE_CTL.equals("QUIT")) {
        APPGlobalVars.SCR_PAUSE_CTL = "";

        
        
        finish();
      }// end (APPGlobalVars.SCR_PAUSE_CTL != null) &&...
    }// end try
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          SplashScreen.this);
      errExcpError.addToLogFile(error, "SplashScreen.onPause", "no prompt");
      errExcpError = null;
    }// end try/catch (Exception error)

    super.onPause();
  }// end onPause

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
    try {
      if (objDisplayAlertClass != null) {
        objDisplayAlertClass.cleanUpClassVars();
      }

      if (android_id != null) {
        android_id = null;
      }
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          SplashScreen.this);
      errExcpError.addToLogFile(error, "SplashScreen.onStop", "no prompt");
      errExcpError = null;
    }// end try/catch (Exception error)
  }// end onStop
  
  /**
   * onResume
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

        if (strQuitStatus.equals("TRUE")) {
          // exit application
          
          SplashScreen.this.finish();
        }// end if (strQuitStatus.equals("TRUE"))
      }// end if (extras != null)
    }// end try
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          SplashScreen.this);
      errExcpError.addToLogFile(error, "SplashScreen.onResume", "");
      errExcpError = null;
    }// end try/catch (Exception error)

    super.onResume();
  }// end onResume
}// end SplashScreen