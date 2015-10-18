
```
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.my_company.app_template.CustAlrtMsgOptnListener.MessageCodes;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class MyErrorLog<T> {
  private Date currDateTime = null;

  private Context mCtx;
  private static final String ERRORLOG_FILENAME = "ErrorLog.txt";
  private static final String ERRORLOG_FILEDIRECTORY = Environment
      .getExternalStorageDirectory().toString()
      + "/myMusicList";
  private String strErrorLogPathToUse = ERRORLOG_FILEDIRECTORY + File.separator;
  private static final int DEFAULT_BSTREAM_SIZE = 8000;

  private static final String NO_DISPLAY_ALERT_FLAG = "no prompt";

  private MyDisplayAlertClass objDisplayAlertClass;
  private boolean USE_SDCARD;

  /**
   * Constructor - takes the context to allow the database to be opened/created
   * 
   * @param ctx
   *          the Context within which to work
   */

  MyErrorLog(final Context ctx) {
    this.mCtx = ctx;
    this.currDateTime = new Date();

    if (android.os.Environment.getExternalStorageState().equals(
        android.os.Environment.MEDIA_MOUNTED)
        && !(Environment.getExternalStorageState()
            .equals(Environment.MEDIA_MOUNTED_READ_ONLY))) {
      // going to use the SDCARD
      this.USE_SDCARD = true;
    } else {
      // going to use default data directory
      this.USE_SDCARD = false;
      this.strErrorLogPathToUse = ctx.getFilesDir().getPath().toString()
      + File.separator;
    }
  }// end constructor

  protected void addToLogFile(T error, String strClassMethod,
      String strAddtlInfo) {
    File myErrorLog = null;
    FileOutputStream myErrorFileOut = null;
    OutputStreamWriter myErrorFileOutStreamWriter = null;
    BufferedOutputStream myErrorFileBuffer = null;

    String strErrLocMsg = "";

    boolean blFileCreated = false;
    boolean blNoSDCard = false;

    try {
      // create the date formatter
      SimpleDateFormat dateFormatter = new SimpleDateFormat(this.mCtx
          .getString(R.string.DATE_FORMAT_LOG_ENTRY));

      if (this.USE_SDCARD == true) {
        try {
          File myErrorLogDirPath = new File(this.strErrorLogPathToUse);

          if (myErrorLogDirPath.mkdirs()) {
            Log.i("MyErrorLog", "Application data directory created at "
                + this.strErrorLogPathToUse);
          }// end if (destination.mkdir())

          if (myErrorLogDirPath.exists()) {
            myErrorLog = new File(strErrorLogPathToUse + ERRORLOG_FILENAME);
            myErrorLogDirPath = null;
          } else {
            if (myErrorLogDirPath != null) {
              myErrorLogDirPath = null;
            }
            
            return;
          }// end if (destination.exists())
        }// end try
        catch (Exception excptnError) {
          String strErrMsg = strErrLocMsg + ": " + excptnError.toString();
          Log.i("MyErrorLog.addToLogFile Exception Error",
              "While trying to create the error log directory, "
                  + "the following error occurred: " + strErrMsg);

          // display the original error that was passed in, and then exit
          strErrLocMsg = "The following error occurred in class "
              + strClassMethod;
          if ((!strAddtlInfo.equals(NO_DISPLAY_ALERT_FLAG)) &&
              (!strAddtlInfo.equals(""))) {
            strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
          }

          strErrLocMsg = strErrLocMsg + ": " + error.toString();

          Log.i("Exception Error", strErrLocMsg);

          return;
        }// end try/catch
      }// end if
      else {
        // no SD Card found
        myErrorLog = new File(strErrorLogPathToUse + ERRORLOG_FILENAME);
        blNoSDCard = true;
      }// end if sd card exists

      // create outstream
      try {
        if (blNoSDCard == false) {
          blFileCreated = myErrorLog.createNewFile();

          if (blFileCreated == false) {
            if (myErrorLog.exists() && myErrorLog.isFile()) {
              blFileCreated = myErrorLog.canWrite();
            } else {
              blFileCreated = false;
            }// end if(myErrorLog.exists() && ...
          }// end if (blFileCreated == false)

          if (blFileCreated == true) {
            myErrorFileOut = new FileOutputStream(myErrorLog);
            myErrorFileBuffer = new BufferedOutputStream(myErrorFileOut,
                DEFAULT_BSTREAM_SIZE);
            myErrorFileOutStreamWriter = new OutputStreamWriter(
                myErrorFileBuffer);

            if (myErrorFileOutStreamWriter != null) {
              blFileCreated = true;
            } else {
              blFileCreated = false;
            }// end if (myErrorFileOutStreamWriter != null)
          }// end if (blFileCreated == true)
        } else {
          myErrorFileOut = this.mCtx.openFileOutput(ERRORLOG_FILENAME,
              Context.MODE_PRIVATE);
          myErrorFileBuffer = new BufferedOutputStream(myErrorFileOut,
              DEFAULT_BSTREAM_SIZE);
          myErrorFileOutStreamWriter = new OutputStreamWriter(myErrorFileBuffer);

          if (myErrorFileOutStreamWriter != null) {
            blFileCreated = true;
          } else {
            blFileCreated = false;
          }// end if (myErrorFileOutStreamWriter != null)
        }// end if (blNoSDCard == false)
      }// end try
      catch (FileNotFoundException errException) {
        Log.i("MyErrorLog.addToLogFile",
            " While creating BufferedWriter, the following exception occurred: "
                + errException.toString());
        errException = null;
        return;
      } catch (IOException errException) {
        Log.i("MyErrorLog.addToLogFile",
            " While creating BufferedWriter, the following exception occurred: "
                + errException.toString());
        errException = null;
        return;
      } catch (Exception errException) {
        Log.i("MyErrorLog.addToLogFile",
            " While creating BufferedWriter, the following exception occurred: "
                + errException.toString());
        errException = null;
        return;
      }// end try/catch

      if (blFileCreated == true) {
        try {
          this.currDateTime = new Date();

          // format the date into a formatted date-string.
          String strCustChkDate = dateFormatter.format(this.currDateTime);

          strErrLocMsg = strCustChkDate + ": " + "In class " + strClassMethod;
          
          if ((!strAddtlInfo.equals(NO_DISPLAY_ALERT_FLAG)) &&
              (!strAddtlInfo.equals(""))) {
            strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
          }
          strErrLocMsg = strErrLocMsg + ": " + error.toString();

          myErrorFileOutStreamWriter.append(strErrLocMsg);
          myErrorFileOutStreamWriter.write("\n");
        }// end try
        catch (FileNotFoundException errException) {
          Log.i("MyErrorLog.addToLogFile",
              " While trying to write to the error log, the following exception occurred: "
                  + errException.toString());
          errException = null;
          return;
        } catch (IOException errException) {
          Log.i("MyErrorLog.addToLogFile",
              " While trying to write to the error log, the following exception occurred: "
                  + errException.toString());
          errException = null;
          return;
        } catch (Exception errException) {
          Log.i("MyErrorLog.addToLogFile",
              " While trying to write to the error log, the following exception occurred: "
                  + errException.toString());
          errException = null;
          return;
        }// end try/catch
      }// end if (blFileCreated == true)
      else {
        strErrLocMsg = "The following error occurred in class "
            + strClassMethod;
        
        if ((!strAddtlInfo.equals(NO_DISPLAY_ALERT_FLAG)) &&
            (!strAddtlInfo.equals(""))) {
          strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
        }

        strErrLocMsg = strErrLocMsg + ": " + error.toString();
      }// end if/else if (blFileCreated == true)

      // display alert dialog depending on the display flag
      if ((!strAddtlInfo.equals(NO_DISPLAY_ALERT_FLAG)) &&
          (!strAddtlInfo.equals(""))) {
        if (this.objDisplayAlertClass != null) {
          this.objDisplayAlertClass.cleanUpClassVars();
          this.objDisplayAlertClass = null;
        }// end if (objDisplayAlertClass != null)
        this.objDisplayAlertClass = new MyDisplayAlertClass(this.mCtx,
            new CustAlrtMsgOptnListener(MessageCodes.ALERT_TYPE_MSG),
            "Exception Error", strErrLocMsg);
      }// end if (!strAddtlInfo.equals(NO_DISPLAY_ALERT_FLAG))

      // perform object cleanup
      if (dateFormatter != null) {
        // cleanup the dateformatter object
        dateFormatter = null;
      }

      if (myErrorLog != null) {
        myErrorLog = null;
      }

      if (myErrorFileOutStreamWriter != null) {
        myErrorFileOutStreamWriter.flush();
        myErrorFileOutStreamWriter.close();
        myErrorFileOutStreamWriter = null;
      }

      if (myErrorFileBuffer != null) {
        myErrorFileBuffer.close();
        myErrorFileBuffer = null;
      }

      if (myErrorFileOut != null) {
        myErrorFileOut.close();
        myErrorFileOut = null;
      }

      strErrLocMsg = null;
      this.currDateTime = null;
      this.mCtx = null;

    }// end main try
    catch (Exception errException) {
      Log.i("MyErrorLog.addToLogFile",
          " While trying to write to the error log, the following exception occurred: "
              + errException.toString());
      errException = null;
    }// end try/catch code

    return;
  }// end addToLogFile
}// end MyErrorLog
```