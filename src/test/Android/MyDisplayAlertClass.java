package test.Android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MyDisplayAlertClass {
  private static Context ctxContext;
  
  
  /**
   * Constructor - takes the context to allow the database to be
   * opened/created
   * 
   * @param ctx the Context within which to work
   */
 
  MyDisplayAlertClass(final Context ctx) {
    MyDisplayAlertClass.ctxContext = ctx;
  }//end constructor
  
  /**
   * displayAlert: Code to populate Alert Messages
   * in an "Ok" dialog display.
   * 
   * @param String myTitle - title of the message
   * @param String myMsg - content of the alert message
   * 
   * @return void
   * 
   */
  protected void displayAlert(String myTitle, String myMsg)
  {         
    try{ 
      new AlertDialog.Builder(ctxContext)
      .setTitle(myTitle)
      .setMessage(myMsg)
      .setPositiveButton("OK", new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialog, int whichButton){
          //objActivity.setResult(intResultCode);
        }})
      .show();
       
      //objActivity = null;
      
      return;
    }//end try 
    catch (Exception error){
      MySDCErrorLog<Exception> errExcpError = new MySDCErrorLog<Exception>(ctxContext);
      errExcpError.addToLogFile(error, "MyDisplayAlertClass.displayAlert", "");
      errExcpError = null;
    }//end try/catch (Exception error)
  }//end displayAlert 

}//end MyDisplayAlertClass
