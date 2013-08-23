package com.my_company.app_template;

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
	private Date _currDateTime = null;

	private Context _mCtx;
	private static final String _ERRORLOG_FILENAME = "ErrorLog.txt";
	private static final int _DEFAULT_BSTREAM_SIZE = 8000;

	private static final String _NO_DISPLAY_ALERT_FLAG = "no prompt";

	private MyDisplayAlertClass _objDisplayAlertClass;
	private boolean _USE_SDCARD;

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */

	MyErrorLog(final Context ctx) {
		this._mCtx = ctx;

		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())
				&& !(Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
						.getExternalStorageState()))) {
			// going to use the SDCARD
			this._USE_SDCARD = true;
		} else {
			// going to use default data directory
			this._USE_SDCARD = false;
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

		// create the date formatter
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				this._mCtx.getString(R.string.DATE_FORMAT_LOG_ENTRY));

		try {
			if (this._USE_SDCARD == true) {
				this._currDateTime = new Date();

				// final String ERRORLOG_FILEDIRECTORY =
				// this._mCtx.getExternalFilesDir(null).getPath() +
				// File.separator
				final String ERRORLOG_FILEDIRECTORY = Environment
						.getExternalStorageDirectory().getPath()
						+ File.separator + "myMusicList" + File.separator;

				File myErrorLogDirPath = new File(ERRORLOG_FILEDIRECTORY);

				if (myErrorLogDirPath.mkdirs()) {
					Log.i("MyErrorLog",
							"Application data directory created at "
									+ ERRORLOG_FILEDIRECTORY);
				}// end if (destination.mkdir())

				if (myErrorLogDirPath.exists()
						&& myErrorLogDirPath.isDirectory()) {
					myErrorLog = new File(ERRORLOG_FILEDIRECTORY
							+ _ERRORLOG_FILENAME);
					myErrorLogDirPath = null;
				} else {
					if (myErrorLogDirPath != null) {
						myErrorLogDirPath = null;
					}

					return;
				}// end if (DirPath.exists())

				// create outstream
				blFileCreated = myErrorLog.createNewFile();

				if (blFileCreated == false) {
					if (myErrorLog.exists() && myErrorLog.isFile()) {
						blFileCreated = myErrorLog.canWrite();
					}
				}// end if (blFileCreated == false)

				if (blFileCreated == true) {
					myErrorFileOut = new FileOutputStream(myErrorLog);
					myErrorFileBuffer = new BufferedOutputStream(
							myErrorFileOut, _DEFAULT_BSTREAM_SIZE);
					myErrorFileOutStreamWriter = new OutputStreamWriter(
							myErrorFileBuffer);
				}// end if (blFileCreated == true)
			}// end if sd card or external file storage exists
			else {
				// no sd card or external file storage
				// create outstream
				myErrorFileOut = this._mCtx.openFileOutput(_ERRORLOG_FILENAME,
						Context.MODE_PRIVATE);
				myErrorFileBuffer = new BufferedOutputStream(myErrorFileOut,
						_DEFAULT_BSTREAM_SIZE);
				myErrorFileOutStreamWriter = new OutputStreamWriter(
						myErrorFileBuffer);

				blFileCreated = true;
			}// end if/else sd card exists

			if (blFileCreated == true) {
				// format the date into a formatted date-string.
				String strCustChkDate = dateFormatter
						.format(this._currDateTime);
				strErrLocMsg = strCustChkDate + ": "
						+ "The following error occurred in class "
						+ strClassMethod;

				if ((!strAddtlInfo.equals(_NO_DISPLAY_ALERT_FLAG))
						&& (!strAddtlInfo.equals(""))) {
					strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
				}
				strErrLocMsg = strErrLocMsg + ": " + error.toString();

				myErrorFileOutStreamWriter.append(strErrLocMsg);
				myErrorFileOutStreamWriter.append("\n");
			}// end if (blFileCreated == true)
			else {
				// setup error message vars,
				// even if the error log file was not created,
				// but only if a error message was going to be displayed
				if (!strAddtlInfo.equals(_NO_DISPLAY_ALERT_FLAG)) {
					strErrLocMsg = "The following error occurred in class "
							+ strClassMethod;

					if ((!strAddtlInfo.equals(_NO_DISPLAY_ALERT_FLAG))
							&& (!strAddtlInfo.equals(""))) {
						strErrLocMsg = strErrLocMsg + ", " + strAddtlInfo;
					}

					strErrLocMsg = strErrLocMsg + ": " + error.toString();
				}
			}// end if/else if (blFileCreated == true)

			// display alert dialog depending on the display flag
			if (!strAddtlInfo.equals(_NO_DISPLAY_ALERT_FLAG)) {
				if (this._objDisplayAlertClass != null) {
					this._objDisplayAlertClass.cleanUpClassVars();
					this._objDisplayAlertClass = null;
				}// end if (_objDisplayAlertClass != null)

				this._objDisplayAlertClass = new MyDisplayAlertClass(
						this._mCtx,
						new CustAlrtMsgOptnListener(
								CustAlrtMsgOptnListener.MessageCodes.ALERT_TYPE_MSG),
						"Exception Error", strErrLocMsg);
			}// end if (!strAddtlInfo.equals(_NO_DISPLAY_ALERT_FLAG))

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
			this._currDateTime = null;
			this._mCtx = null;

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
		} catch (IllegalArgumentException errException) {
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
		}// end try/catch code

		return;
	}// end addToLogFile
}// end MyErrorLog
