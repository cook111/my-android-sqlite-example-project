package com.my_company.app_template;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Environment;
import android.util.Log;

public class DBTableExport {
  private Context ctxLocalContext = null;
  private static final int DEFAULT_BSTREAM_SIZE = 8000;

  protected DBTableExport(Context ctxContext) {
    this.ctxLocalContext = ctxContext;
  }

  protected void exportAsCSVFile(Cursor tblDataCursor, String strTableName)
      throws IOException {
    try {
      String strColNames = "";
      String strEndOfLine = "\n";

      File myExportFile = null;
      FileOutputStream myExportFileOut = null;
      OutputStreamWriter myExportFileWriter = null;
      BufferedOutputStream myBuffOutStream = null;

      boolean blFileCreated = false;
      boolean blFileDeleted = false;
      boolean blNoSDCard = false;

      final String EXPORT_FILE_NAME = strTableName + ".csv";

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
      Date date = new Date();
      String dateStr = dateFormat.format(date);

      final String EXPORT_FILEDIRECTORY = Environment
          .getExternalStorageDirectory().toString()
          + "/myMusicList/exports/" + dateStr;
      String strExportPathToUse = EXPORT_FILEDIRECTORY;

      // create a file on the sdcard to export the
      // database contents to
      if (android.os.Environment.getExternalStorageState().equals(
          android.os.Environment.MEDIA_MOUNTED)
          && !(Environment.getExternalStorageState()
              .equals(Environment.MEDIA_MOUNTED_READ_ONLY))) {
        try {
          File myExportLogDirPath = new File(EXPORT_FILEDIRECTORY);

          if (myExportLogDirPath.mkdirs()) {
            Log.i("MyErrorLog", "Export directory created at "
                + EXPORT_FILEDIRECTORY);
          }// end if (destination.mkdir())

          if (myExportLogDirPath.exists()) {
            strExportPathToUse = EXPORT_FILEDIRECTORY + "/";
            myExportFile = new File(strExportPathToUse + EXPORT_FILE_NAME);
            myExportLogDirPath = null;
          } else {
            strExportPathToUse = Environment.getDataDirectory().toString()
                + "/";
            blNoSDCard = true;
          }// end if (destination.exists())
        }// end try
        catch (Exception error) {
          MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
              this.ctxLocalContext);
          errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
              "attempting to create an output directory");
          errExcpError = null;
          return;
        }// end try/catch
      }// end if (android.os.Environment.getExternalStorageState().equals(...
      else {
        strExportPathToUse = Environment.getDataDirectory().toString() + "/";
        blNoSDCard = true;
      }// end if sd card exists

      // create outstream
      try {
        if (blNoSDCard == false) {
          blFileCreated = myExportFile.createNewFile();

          if (blFileCreated == false) {
            try {
              blFileDeleted = myExportFile.delete();
            }// end try
            catch (SecurityException error) {
              MyErrorLog<SecurityException> errExcpError = new MyErrorLog<SecurityException>(
                  this.ctxLocalContext);
              errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
                  "deleting export file because it already exists");
              errExcpError = null;
              return;
            }// end try/catch

            if (blFileDeleted == true) {
              blFileCreated = myExportFile.createNewFile();
            }
          }// end if (blFileCreated == false)

          if (blFileCreated == true) {
            myExportFileOut = new FileOutputStream(myExportFile);
            myBuffOutStream = new BufferedOutputStream(myExportFileOut, DEFAULT_BSTREAM_SIZE);
            myExportFileWriter = new OutputStreamWriter(myBuffOutStream);
            
            if (myExportFileWriter != null) {
              blFileCreated = true;
            }// end if (myExportFileWriter != null)
          }// end if (blFileCreated == true)
        }// end if (blNoSDCard == false)
        else {
          myExportFileOut = this.ctxLocalContext.openFileOutput(
              EXPORT_FILE_NAME, Context.MODE_PRIVATE);
          myBuffOutStream = new BufferedOutputStream(myExportFileOut, DEFAULT_BSTREAM_SIZE);
          myExportFileWriter = new OutputStreamWriter(myBuffOutStream);

          if (myExportFileWriter != null) {
            blFileCreated = true;
          }// end if (myExportFileWriter != null)
        }// end if/else (blNoSDCard == false)
      }// end try
      catch (FileNotFoundException error) {
        MyErrorLog<FileNotFoundException> errExcpError = new MyErrorLog<FileNotFoundException>(
            this.ctxLocalContext);
        errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
            "creating BufferedWriter, file was not found");
        errExcpError = null;
        return;
      } catch (IOException error) {
        MyErrorLog<IOException> errExcpError = new MyErrorLog<IOException>(
            this.ctxLocalContext);
        errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
            "creating BufferedWriter, I/O error");
        errExcpError = null;
        return;
      } catch (Exception error) {
        MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
            this.ctxLocalContext);
        errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
            "creating BufferedWriter");
        errExcpError = null;
        return;
      }// end try/catch

      if (blFileCreated == true) {
        try {
          if (tblDataCursor != null) {
            tblDataCursor.moveToFirst();

            int numCols = tblDataCursor.getColumnCount();

            // store column names
            for (int idx = 0; idx < numCols; idx++) {
              String strSingleColName = tblDataCursor.getColumnName(idx);
              if (strSingleColName != null && !strSingleColName.equals("")
                  && !strSingleColName.equals(" ")
                  && strSingleColName.equals("reconciled")) {
                strSingleColName = strSingleColName + "(0=false/1=true)";
              } else if (strSingleColName != null
                  && !strSingleColName.equals("")
                  && !strSingleColName.equals(" ")
                  && strSingleColName.equals("_id")) {
                strSingleColName = "row_id";
              }

              strColNames = strColNames + strSingleColName;

              strColNames = strColNames + ",";

              strSingleColName = null;
            }// end for loop

            // write to file and then add end of line char
            myExportFileWriter.write(strColNames);
            myExportFileWriter.write(strEndOfLine);

            // move through the table, creating rows
            // and adding each column with name and value
            // to the row
            while (tblDataCursor.getPosition() < tblDataCursor.getCount()) {
              String strRowValues = "";

              // store column names
              for (int idx = 0; idx < numCols; idx++) {
                strRowValues = strRowValues + tblDataCursor.getString(idx);

                strRowValues = strRowValues + ",";
              }// end for

              // write to file and then add end of line char
              myExportFileWriter.write(strRowValues);
              myExportFileWriter.write(strEndOfLine);

              tblDataCursor.moveToNext();
            }// end while
          }// end if (tblDataCursor != null)

          tblDataCursor.close();
        }// end try
        catch (FileNotFoundException error) {
          MyErrorLog<FileNotFoundException> errExcpError = new MyErrorLog<FileNotFoundException>(
              this.ctxLocalContext);
          errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
              "ouput file was not found");
          errExcpError = null;
          return;
        } catch (IOException error) {
          MyErrorLog<IOException> errExcpError = new MyErrorLog<IOException>(
              this.ctxLocalContext);
          errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
              "error writing to the output file");
          errExcpError = null;
          return;
        } catch (SQLException error) {
          MyErrorLog<SQLException> errExcpError = new MyErrorLog<SQLException>(
              this.ctxLocalContext);
          errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
              "sql error with the export cursor");
          errExcpError = null;
          return;
        } catch (Exception error) {
          MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
              this.ctxLocalContext);
          errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
              "creating cursor and exporting data");
          errExcpError = null;
          return;
        }// end try/catch
      }// end if (blFileCreated == true)

      // perform object cleanup
      if (myExportFileWriter != null) {
        myExportFileWriter.flush();
        myExportFileWriter.close();
        myExportFileWriter = null;
      }
      if (myBuffOutStream != null) {
        myBuffOutStream.close();
        myBuffOutStream = null;
      }
      if (myExportFileOut != null) {
        myExportFileOut.close();
        myExportFileOut = null;
      }
      if (dateFormat != null) {
        dateFormat = null;
      }
      if (date != null) {
        date = null;
      }
      if (myExportFile != null) {
        myExportFile = null;
      }
      // end of object cleanup

    }// end try
    catch (FileNotFoundException error) {
      MyErrorLog<FileNotFoundException> errExcpError = new MyErrorLog<FileNotFoundException>(
          this.ctxLocalContext);
      errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
          "ouput file was not found");
      errExcpError = null;
    } catch (IOException error) {
      MyErrorLog<IOException> errExcpError = new MyErrorLog<IOException>(
          this.ctxLocalContext);
      errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
          "error writing to the output file");
      errExcpError = null;
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          this.ctxLocalContext);
      errExcpError.addToLogFile(error, "DBTableExport.exportAsCSVFile",
          "main try/catch, exception error thrown");
      errExcpError = null;
    }// end try/catch code

    return;
  }// exportAsCSVFile(Cursor tblDataCursor, String strTableName)
}// end DBTableExport
