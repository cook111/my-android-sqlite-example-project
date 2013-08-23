package com.my_company.app_template;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class DBTableExport
{
  private Context _mCtx = null;
  private static final int _DEFAULT_BSTREAM_SIZE = 8000;
  private boolean _USE_SDCARD;
  private Date _currDate;

  protected DBTableExport(Context ctxContext)
	{
    this._mCtx = ctxContext;

    if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())
        && !(Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()))
        )
		{
      // going to use the SDCARD
      this._USE_SDCARD = true;
    }
		else
		{
      // going to use default data directory
      this._USE_SDCARD = false;
    }
  }

  protected void exportAsCSVFile(Cursor tblDataCursor, String strTableName)
	{
		String strColNames = "";
		String strEndOfLine = "\n";

		File myExportFile = null;
		FileOutputStream myExportFileOut = null;
		OutputStreamWriter myExportFileWriter = null;
		BufferedOutputStream myBuffOutStream = null;

		boolean blFileCreated = false;
		boolean blFileDeleted = false;

		final String EXPORT_FILE_NAME = strTableName + ".csv";

		SimpleDateFormat dateFormat = new SimpleDateFormat(
			this._mCtx.getString(R.string.DATE_FORMAT_TableExport));

		try
		{
      if (this._USE_SDCARD == true)
			{
        //TODO: !!create correct export directory path object
				// TODO: get project root storage dir from fileUtil class
				this._currDate = new Date();
        String dateStr = dateFormat.format(this._currDate);

				//final String EXPORT_FILEDIRECTORY = this._mCtx.getExternalFilesDir(null).getPath() + File.separator 
				final String EXPORT_FILEDIRECTORY = 
				  Environment.getExternalStorageDirectory().getPath() 
					+ File.separator
						+ "myMusicList" 
					+ File.separator 
					+ "exports" 
					+ File.separator 
					+ dateStr 
					+ File.separator;

        File myExportLogDirPath = new File(EXPORT_FILEDIRECTORY);

        if (myExportLogDirPath.mkdirs())
				{
          Log.i("MySDCErrorLog", "Export directory created at "
								+ EXPORT_FILEDIRECTORY);
        }// end if (destination.mkdir())

				if (myExportLogDirPath.exists() && myExportLogDirPath.isDirectory())
				{
					myExportFile = new File(EXPORT_FILEDIRECTORY + EXPORT_FILE_NAME);
          myExportLogDirPath = null;

					//TODO: Now do filestream, outstream, etc.
					// use and/or create fileUtil class code AMAP
        }
				else
				{
					if (myExportLogDirPath != null)
					{
						myExportLogDirPath = null;
					}

					return;
        }// end if (destination.exists())

        // create outstream
        blFileCreated = myExportFile.createNewFile();

        if (blFileCreated == false)
				{
          blFileDeleted = myExportFile.delete();

          if (blFileDeleted == true)
					{
            blFileCreated = myExportFile.createNewFile();
          }
        }// end if (blFileCreated == false)

        if (blFileCreated == true)
				{
          myExportFileOut = 
						new FileOutputStream(myExportFile);
          myBuffOutStream = 
						new BufferedOutputStream(myExportFileOut,
																		 _DEFAULT_BSTREAM_SIZE);
          myExportFileWriter = 
						new OutputStreamWriter(myBuffOutStream);
        }// end if (blFileCreated == true)
      }
			else
			{
        // this._USE_SDCARD == false
        myExportFileOut = 
					this._mCtx.openFileOutput(EXPORT_FILE_NAME, 
																		Context.MODE_PRIVATE);
        myBuffOutStream = 
					new BufferedOutputStream(myExportFileOut,
																	 _DEFAULT_BSTREAM_SIZE);
        myExportFileWriter = 
					new OutputStreamWriter(myBuffOutStream);

        blFileCreated = true;
      }//end if/else (this._USE_SDCARD == true)

      if (blFileCreated == true)
			{
        if (tblDataCursor != null)
				{
          tblDataCursor.moveToFirst();

          int numCols = tblDataCursor.getColumnCount();

          // store column names
          for (int idx = 0; idx < numCols; idx++)
					{
            String strSingleColName = tblDataCursor.getColumnName(idx);
            if(strSingleColName != null
			   && !strSingleColName.equals("")
			   && !strSingleColName.equals(" ")
			   && strSingleColName.equals("_id"))
						{
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
          while (tblDataCursor.getPosition() < tblDataCursor.getCount())
					{
            String strRowValues = "";

            // store column names
            for (int idx = 0; idx < numCols; idx++)
						{
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
      }// end if (blFileCreated == true)

      // perform object cleanup
      if (myExportFileWriter != null)
			{
        myExportFileWriter.flush();
        myExportFileWriter.close();
        myExportFileWriter = null;
      }
      if (myBuffOutStream != null)
			{
        myBuffOutStream.close();
        myBuffOutStream = null;
      }
      if (myExportFileOut != null)
			{
        myExportFileOut.close();
        myExportFileOut = null;
      }
      if (dateFormat != null)
			{
        dateFormat = null;
      }
      if (this._currDate != null)
			{
        this._currDate = null;
      }
      if (myExportFile != null)
			{
        myExportFile = null;
      }
      // end of object cleanup

    }// end try
    catch (FileNotFoundException errException)
		{
      Log.i("MyErrorLog.addToLogFile",
						" While trying to write the export data, the following exception occurred: "
						+ errException.toString());
      errException = null;
      return;
    }
		catch (IOException errException)
		{
      Log.i("MyErrorLog.addToLogFile",
						" While trying to write the export data, the following exception occurred: "
						+ errException.toString());
      errException = null;
      return;
    }
		catch (IllegalArgumentException errException)
		{
      Log.i("MyErrorLog.addToLogFile",
						" While trying to write the export data, the following exception occurred: "
						+ errException.toString());
      errException = null;
      return;
    }
    catch (Exception errException)
		{
      Log.i("MyErrorLog.addToLogFile",
						" While trying to write the export data, the following exception occurred: "
						+ errException.toString());
      errException = null;
    }// end try/catch code

    return;
  }// exportAsCSVFile(Cursor tblDataCursor, String strTableName)
}// end DBTableExport
