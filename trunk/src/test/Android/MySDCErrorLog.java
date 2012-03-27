package test.Android;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

public class MySDCErrorLog<T>  {
  private static Time currDateTime = new Time(); 
  
  private static Context mCtx;
  private static final String ERRORLOG_FILENAME = R.string.sd_card_folder_name + ".txt";
  private static final String ERRORLOG_FILEDIRECTORY = Environment.getExternalStorageDirectory().toString() + "/" + R.string.sd_card_folder_name;
  private String strErrorLogPathToUse = ERRORLOG_FILEDIRECTORY;
  
  private static MyDisplayAlertClass objDisplayAlertClass;
  
  /**
   * Constructor - takes the context to allow the database to be
   * opened/created
   * 
   * @param ctx the Context within which to work
   */
 
  MySDCErrorLog(final Context ctx) {
    MySDCErrorLog.mCtx = ctx;
    objDisplayAlertClass = new MyDisplayAlertClass(mCtx);
  }//end constructor
  
  protected void addToLogFile (T error, String strClassMethod, String strAddtlInfo){
    File myErrorLog = null;
    FileOutputStream myErrorFileOut = null;
    BufferedOutputStream myErrorFileBuffer = null;
    OutputStreamWriter myErrorFileOutStreamWriter = null;
    
    String strErrLocMsg = "";
    
    try{
      if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
        try{
          File myErrorLogDirPath = new File(ERRORLOG_FILEDIRECTORY);
          
          
          if (myErrorLogDirPath.mkdirs()) {     
            Log.d("MySDCErrorLog", "Application data directory created");
          }//end if (destination.mkdir())
          
          if (myErrorLogDirPath.exists()) {
            strErrorLogPathToUse = ERRORLOG_FILEDIRECTORY + "/";
            myErrorLog = new File(strErrorLogPathToUse, ERRORLOG_FILENAME);
            myErrorLogDirPath = null;
          } 
          else {
            strErrorLogPathToUse = "";
            myErrorLog = new File(Environment.getDataDirectory(), ERRORLOG_FILENAME);
          }//end if (destination.exists())
        }//end try
        catch (Exception excptnError)
        {
          String strErrMsg = strErrLocMsg + ": " + excptnError.toString();
          objDisplayAlertClass.displayAlert("MySDCErrorLog.addToLogFile Exception Error", 
                                         "While trying to create the error log directory, " +
                                         "the following error occurred: " +
                                         strErrMsg);
          
          //display the original error that was passed in, and then exit
          strErrLocMsg = "The following error occurred in class " + strClassMethod;
          if (!strAddtlInfo.equals("")){
            strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
          }
          
          
          strErrLocMsg = strErrLocMsg + ": " + error.toString();
          
          objDisplayAlertClass.displayAlert("Exception Error", strErrLocMsg);
          
          return;
        }//end try/catch
      }//end if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
      else {
        strErrorLogPathToUse = "";
        myErrorLog = new File(Environment.getDataDirectory  (), ERRORLOG_FILENAME);
      }//end if sd card exists
  
      if (myErrorLog.exists()){
        try {
          if (myErrorLog.exists()){
            myErrorFileOut = new FileOutputStream(myErrorLog, true);
          }
          else{
            myErrorFileOut = new FileOutputStream(myErrorLog, false);
          }
          
          myErrorFileBuffer = new BufferedOutputStream(myErrorFileOut);
          myErrorFileOutStreamWriter = new OutputStreamWriter(myErrorFileBuffer);
          
          if (myErrorLog.exists()){
            currDateTime.setToNow();
            strErrLocMsg = currDateTime.toString() + " " + "In class " + strClassMethod;
            if (!strAddtlInfo.equals("")){
              strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
            }
            strErrLocMsg = strErrLocMsg + ": " + error.toString();
            
            myErrorFileOutStreamWriter.append(strErrLocMsg);
            myErrorFileOutStreamWriter.append("\n");
            
          }//end if
          else{
            currDateTime.setToNow();
            strErrLocMsg = currDateTime.toString() + " " + "In class " + strClassMethod;
            if (!strAddtlInfo.equals("")){
              strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
            }
            strErrLocMsg = strErrLocMsg + ": " + error.toString();
            
            myErrorFileOutStreamWriter.append(strErrLocMsg);
            myErrorFileOutStreamWriter.write("\n");
          }//end if (myErrorLog.exists()
  
        }//end try 
        catch (FileNotFoundException myFileError) {
          // TODO Auto-generated catch block
          strErrLocMsg = "The following FileNotFoundException error occurred in MySDCErrorLog.addToLogFile";
          if (!strAddtlInfo.equals("")){
            strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
          }
          strErrLocMsg = strErrLocMsg + ": " + myFileError.toString();
          
          objDisplayAlertClass.displayAlert("File Not Found", strErrLocMsg);
        }//end catch (FileNotFoundException myFileError) 
        catch (IOException myIOError) {
          // TODO Auto-generated catch block
          strErrLocMsg = "The following IOException error occurred in MySDCErrorLog.addToLogFile";
          if (!strAddtlInfo.equals("")){
            strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
          }
          strErrLocMsg = strErrLocMsg + ": " + myIOError.toString();
          
          objDisplayAlertClass.displayAlert("I/O Exception", strErrLocMsg);
        }
        catch (Exception myError) {
          // TODO Auto-generated catch block
          strErrLocMsg = "The following Exception error occurred in class MySDCErrorLog.addToLogFile";
          if (!strAddtlInfo.equals("")){
            strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
          }
          strErrLocMsg = strErrLocMsg + ": " + myError.toString();
          
          objDisplayAlertClass.displayAlert("Exception", strErrLocMsg);
        }//end try/catch for recording error to a file
      
        strErrLocMsg = "The following error occurred in class " + strClassMethod;
        if (!strAddtlInfo.equals("")){
          strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
        }

        strErrLocMsg = strErrLocMsg + ": " + error.toString() 
                                    + " The error was written to the following file: " 
                                    + myErrorLog.toString();
        
        objDisplayAlertClass.displayAlert("Exception Error", strErrLocMsg);
        
      }//end if (myErrorLog.exists())
      else {
      	strErrLocMsg = "The following error occurred in class " + strClassMethod;
          if (!strAddtlInfo.equals("")){
            strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
          }
          
          
          strErrLocMsg = strErrLocMsg + ": " + error.toString();
          
          objDisplayAlertClass.displayAlert("Exception Error", strErrLocMsg);
          
      }//end else for if (myErrorLog.exists())
      
      //cleanup objects
      if (myErrorFileOutStreamWriter != null){
        myErrorFileOutStreamWriter.flush();
        myErrorFileOutStreamWriter.close();
        myErrorFileOutStreamWriter = null;
      }
      if (myErrorFileBuffer != null){
        myErrorFileBuffer.flush();
        myErrorFileBuffer.close();
        myErrorFileBuffer = null;
      }
      if (myErrorFileOut != null){
        myErrorFileOut.flush();
        myErrorFileOut.close();
        myErrorFileOut = null;
      }
      strErrLocMsg = null;
      currDateTime = null;
      mCtx = null;
      myErrorLog = null;
      
    }//end main try
    catch (Exception excError)
    {
      strErrLocMsg = "The following Exception error occurred in MySDCErrorLog.addToLogFile";
      if (!strAddtlInfo.equals("")){
        strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
      }
      strErrLocMsg = strErrLocMsg + ": " + excError.toString();
      
      objDisplayAlertClass.displayAlert("Exception", strErrLocMsg);
    }//end try/catch code

    return;
  }//end addToLogFile

}//end MySDCErrorLog
