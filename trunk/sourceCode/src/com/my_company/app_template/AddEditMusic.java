package com.my_company.app_template;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import com.my_company.app_template.CustAlrtMsgOptnListener.MessageCodes;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Class for entering/updating Entries.
 */
public class AddEditMusic extends Activity {
	/* Global variable declarations */
	private EditText txtAlbum;
	private EditText txtGenre;
	private EditText txtalbumdate;
	private EditText txtArtist;
	private EditText txtNotes;
	private ImageButton btnPickDate;

	private int intYear;
	private int intMonth;
	private int intDay;

	static final int DATE_DIALOG_ID = 1;

	private Long mRowId = null;
	private String strOrigAlbum = "";
	private String strOrigGenre = "";
	private String strOrigPubDate = "";
	private String strOrigArtist = "";

	private static MyDisplayAlertClass objDisplayAlertClass;

	private boolean blSaveNew;
	boolean blShowSaveNew = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit_music);
		try {
			this.loadViewObjects();

			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				String strAlbum = extras.getString(MyAppDbAdapter.KEY_ALBUM);
				String strPubDate = extras
						.getString(MyAppDbAdapter.KEY_ALBUMDATE);
				String strGenre = extras.getString(MyAppDbAdapter.KEY_GENRE);
				String strArtist = extras.getString(MyAppDbAdapter.KEY_ARTIST);
				String strNotes = extras.getString(MyAppDbAdapter.KEY_NOTES);
				mRowId = extras.getLong(MyAppDbAdapter.KEY_ROWID);

				blShowSaveNew = (extras.getBoolean("SHOW_SAVENEW") == true);

				if (strAlbum != null && !strAlbum.equals("")
						&& !strAlbum.equals(" ")) {
					txtAlbum.setText(strAlbum);
					strOrigAlbum = strAlbum;
				}
				if (strGenre != null && !strGenre.equals("")
						&& !strGenre.equals(" ")) {
					txtGenre.setText(strGenre);
					strOrigGenre = strGenre;
				}
				if (strArtist != null && !strArtist.equals("")
						&& !strArtist.equals(" ")) {
					txtArtist.setText(strArtist);
					strOrigArtist = strArtist;
				}
				if (strPubDate != null && !strPubDate.equals("")
						&& !strPubDate.equals(" ")) {
					// create the date formatter
					this.custDateFormatter(strPubDate);
				}// end if if (strPubDate != null &&..
				else {
					// no date has been set
					// get the current date
					final Calendar c = Calendar.getInstance();
					intYear = c.get(Calendar.YEAR);
					intMonth = c.get(Calendar.MONTH);
					intDay = c.get(Calendar.DAY_OF_MONTH);

					this.updateDisplay();
				}

				if (strNotes != null && !strNotes.equals("")
						&& !strNotes.equals(" ")) {
					txtNotes.setText(strNotes);
				}

			}// end if
			else {
				// new entry, need to set some initial values
				// set the current date
				final Calendar c = Calendar.getInstance();
				intYear = c.get(Calendar.YEAR);
				intMonth = c.get(Calendar.MONTH);
				intDay = c.get(Calendar.DAY_OF_MONTH);

				this.updateDisplay();
			}// end if (extras != null)

			btnPickDate = (ImageButton) findViewById(R.id.btnPickDate);
			btnPickDate.setOnClickListener(btnDatePickerListener);

			// call button events handler code
			this.setupButtonHandlers();

			// text focus change listeners
			this.setupFocusChangeListeners();
		}// end try statements
		catch (Exception error) {
			MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this);
			errExcpError.addToLogFile(error, "AddEditMusic.onCreate",
					"opening database");
			errExcpError = null;
		}
	}// end onCreate

	/**
	 * void loadViewObjects
	 * 
	 * Executes code to load the necessary view objects from the xml.
	 * 
	 */
	private void loadViewObjects() {
		txtGenre = (EditText) findViewById(R.id.txtGenre);
		txtAlbum = (EditText) findViewById(R.id.txtAlbum);
		txtalbumdate = (EditText) findViewById(R.id.txtalbumdate);
		txtArtist = (EditText) findViewById(R.id.txtArtist);
		txtNotes = (EditText) findViewById(R.id.txtNotes);
		btnPickDate = (ImageButton) findViewById(R.id.btnPickDate);
		return;
	}// end loadViewObjects

	/**
	 * void setupButtonHandlers
	 * 
	 * Executes code to setup the button Handlers that.
	 * 
	 */
	private void setupButtonHandlers() {
		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(btnSaveListener);

		Button btnSaveNew = (Button) findViewById(R.id.btnSaveNew);

		if (blShowSaveNew == false) {
			btnSaveNew.setEnabled(false);
			btnSaveNew.setVisibility(View.INVISIBLE);
		} else {
			btnSaveNew.setOnClickListener(btnSaveNewListener);
		}// end if (blShowSaveNew == false)

		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(btnCancelListener);
	}// end setupButtonHandlers

	/**
	 * void custDateFormatter
	 * 
	 * Executes code to format the date field.
	 * 
	 */
	private void custDateFormatter(String strDate) {
		try {
			// create the date formatter
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					getString(R.string.DATE_FORMAT_ISO8601));

			// put the formatted string into a Date format
			Date myDateFromString = dateFormatter.parse(strDate);
			dateFormatter = null;

			// format the date into a formatted date-string.
			dateFormatter = new SimpleDateFormat(
					getString(R.string.DATE_FORMAT_ENTRY_UPDATE));

			strDate = dateFormatter.format(myDateFromString);

			// cleanup the dateformatter object
			dateFormatter = null;

			if (strDate.length() == 10) {
				txtalbumdate.setText(strDate);
				strOrigPubDate = strDate;

				// date string: 08/15/2010
				String strYear = strDate.substring(6, 10);
				String strMonth = strDate.substring(0, 2);
				String strDay = strDate.substring(3, 5);

				intYear = Integer.valueOf(strYear);
				intMonth = (Integer.valueOf(strMonth) - 1);
				intDay = Integer.valueOf(strDay);
			} else {
				// there was an issue with the date format
				objDisplayAlertClass = new MyDisplayAlertClass(
						AddEditMusic.this, new CustAlrtMsgOptnListener(
								MessageCodes.ALERT_TYPE_MSG),
						"Date Format Error", "The date " + strDate
								+ " is not in the format MM/DD/YYYY");

				// create date instance for today
				Date today = new Date();

				// create the date formatter
				dateFormatter = new SimpleDateFormat(
						getString(R.string.DATE_FORMAT_ENTRY_UPDATE));

				// format the date into a formatted date-string.
				String strCustDate = dateFormatter.format(today);

				// cleanup the dateformatter object
				dateFormatter = null;

				txtalbumdate.setText(strCustDate);
				strOrigPubDate = strCustDate;

				// date string: 08/15/2010
				String strYear = strDate.substring(6, 10);
				String strMonth = strDate.substring(0, 2);
				String strDay = strDate.substring(3, 5);

				intYear = Integer.valueOf(strYear);
				intMonth = (Integer.valueOf(strMonth) - 1);
				intDay = Integer.valueOf(strDay);

			}// end if if (strDate.length() == 10)

		} catch (NullPointerException error) {
			MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this);
			errExcpError.addToLogFile(error,
					"SafeCheckRegEntry.custDateFormatter",
					"formatting a date string, the pattern is null");
			errExcpError = null;
		} catch (IllegalArgumentException error) {
			MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this);
			errExcpError
					.addToLogFile(
							error,
							"SafeCheckRegEntry.custDateFormatter",
							"formatting a date string, the pattern is not considered to be usable by this formatter");
			errExcpError = null;
		} catch (Exception error) {
			MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(this);
			errExcpError.addToLogFile(error,
					"SafeCheckRegEntry.custDateFormatter",
					"formatting a date string");
			errExcpError = null;
		}
	}// end custDateFormatter

	/**
	 * void setupFocusChangeListeners
	 * 
	 * Executes code to setup the field focus change listeners.
	 * 
	 */
	private void setupFocusChangeListeners() {
		// text focus change listeners
		txtAlbum.setOnFocusChangeListener(txtAlbumOnFocusChangeListener);
		txtGenre.setOnFocusChangeListener(txtGenreOnFocusChangeListener);
		txtArtist.setOnFocusChangeListener(txtArtistOnFocusChangeListener);
		txtNotes.setOnFocusChangeListener(txtNotesOnFocusChangeListener);
	}// end setupFocusChangeListeners

	/**
	 * txtAlbumOnFocusChangeListener: focus change listener
	 * 
	 * Executes code when the focus is changed.
	 * 
	 */
	private OnFocusChangeListener txtAlbumOnFocusChangeListener = new OnFocusChangeListener() {
		public void onFocusChange(View arg0, boolean arg1) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
		}
	};

	/**
	 * txtGenreOnFocusChangeListener: focus change listener
	 * 
	 * Executes code when the focus is changed.
	 * 
	 */
	private OnFocusChangeListener txtGenreOnFocusChangeListener = new OnFocusChangeListener() {
		public void onFocusChange(View arg0, boolean arg1) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
		}
	};

	/**
	 * txtArtistOnFocusChangeListener: focus change listener
	 * 
	 * Executes code when the focus is changed.
	 * 
	 */
	private OnFocusChangeListener txtArtistOnFocusChangeListener = new OnFocusChangeListener() {
		public void onFocusChange(View arg0, boolean arg1) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
		}
	};

	/**
	 * txtNotesOnFocusChangeListener: focus change listener
	 * 
	 * Executes code when the focus is changed.
	 * 
	 */
	private OnFocusChangeListener txtNotesOnFocusChangeListener = new OnFocusChangeListener() {
		public void onFocusChange(View arg0, boolean arg1) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
		}
	};

	/**
	 * mySaveDataMethod : Save method
	 * 
	 * Validates the data in the "Required" fields. (Description, date, and
	 * amount)
	 * 
	 * If the validation is successful, the bundles are populated, and then
	 * cleanup is performed, and then the program returns focus to the calling
	 * code.
	 * 
	 */
	private void mySaveDataMethod() {
		// if any of the fields are blank, don't let save
		String strGenre = "";
		String strAlbum = "";
		String strArtist = "";
		String strPubDate = "";
		String strNotes = "";
		boolean boolSaveOk;

		boolSaveOk = true;

		if (this.txtGenre == null) {
			this.txtGenre = (EditText) findViewById(R.id.txtGenre);
		}

		try {
			// do field validation for the required fields
			// genre
			strGenre = this.txtGenre.getText().toString();
			if (strGenre.contentEquals("")) {

				objDisplayAlertClass = new MyDisplayAlertClass(
						AddEditMusic.this, new CustAlrtMsgOptnListener(
								MessageCodes.ALERT_TYPE_MSG),
						"Missing Required Genre",
						"Please enter or select a GENRE for this entry.");

				boolSaveOk = false;
				txtGenre.requestFocus();
			}

			// album
			strAlbum = this.txtAlbum.getText().toString();
			if (strAlbum.contentEquals("")) {

				objDisplayAlertClass = new MyDisplayAlertClass(
						AddEditMusic.this, new CustAlrtMsgOptnListener(
								MessageCodes.ALERT_TYPE_MSG),
						"Missing Required Album",
						"Please enter or select a ALBUM for this entry.");

				boolSaveOk = false;
				txtAlbum.requestFocus();
			}

			// artist
			strArtist = this.txtArtist.getText().toString();
			if (strArtist.contentEquals("")) {

				objDisplayAlertClass = new MyDisplayAlertClass(
						AddEditMusic.this, new CustAlrtMsgOptnListener(
								MessageCodes.ALERT_TYPE_MSG),
						"Missing Required Artist",
						"Please enter or select a ARTIST for this entry.");

				boolSaveOk = false;
				txtArtist.requestFocus();
			}

			strPubDate = this.txtalbumdate.getText().toString();
			if (strPubDate.contentEquals("")) {

				objDisplayAlertClass = new MyDisplayAlertClass(
						AddEditMusic.this, new CustAlrtMsgOptnListener(
								MessageCodes.ALERT_TYPE_MSG),
						"Missing Required Date",
						"Please enter a date for this entry.");

				boolSaveOk = false;
				txtalbumdate.requestFocus();
			}

			strNotes = this.txtNotes.getText().toString();

			if (boolSaveOk == true) {
				APPGlobalVars.SCR_PAUSE_CTL = "SAVE";
				Bundle bundle = new Bundle();
				bundle.putString(MyAppDbAdapter.KEY_ALBUM, txtAlbum.getText()
						.toString());
				bundle.putString(MyAppDbAdapter.KEY_GENRE, strGenre);
				bundle.putString(MyAppDbAdapter.KEY_ARTIST, strArtist);
				bundle.putString(MyAppDbAdapter.KEY_ALBUMDATE, strPubDate);
				bundle.putBoolean("SHOW_FILTER", true);

				if (mRowId != null) {
					bundle.putLong(MyAppDbAdapter.KEY_ROWID, mRowId);
				}

				bundle.putString(MyAppDbAdapter.KEY_NOTES, strNotes);

				if (((!strOrigAlbum.equals(strAlbum)) && (!strOrigAlbum
						.equals("")))
						|| ((!strOrigGenre.equals(strGenre)) && (!strOrigGenre
								.equals("")))
						|| ((!strOrigArtist.equals(strArtist)) && (!strOrigArtist
								.equals("")))
						|| ((!strOrigPubDate.equals(strPubDate)) && (!strOrigPubDate
								.equals("")))) {
					bundle.putString("OrigGenre", strOrigGenre);
					bundle.putString("OrigArtist", strOrigArtist);
					bundle.putString("OrigPubDate", strOrigPubDate);
					bundle.putString("OrigAlbum", strOrigAlbum);
				} else {
					bundle.putString("OrigGenre", "");
					bundle.putString("OrigArtist", "");
					bundle.putString("OrigAlbum", "");
					bundle.putString("OrigPubDate", "");
				}// end if (((!strOrigAlbum.equals(strAlbum)) && ...

				bundle.putBoolean("IS_SAVE_NEW", AddEditMusic.this.blSaveNew);

				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);

				// cleanup objects
				bundle = null;
				mIntent = null;

				finish();
			}// end if boolSaveOk == true
		}// end try
		catch (Exception error) {
			MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
					AddEditMusic.this);
			errExcpError.addToLogFile(error, "AddEditMusic.mySaveDataMethod",
					"");
			errExcpError = null;
		}// end try/catch (Exception error)
	}// end mySaveDataMethod

	/**
	 * btnSaveListener: Save button Listener
	 * 
	 * Validates the data in the "Required" fields. (Description, date, and
	 * amount)
	 * 
	 * If the validation is successful, the bundles are populated, and then
	 * cleanup is performed, and then the program returns focus to the calling
	 * code.
	 * 
	 */
	private OnClickListener btnSaveListener = new OnClickListener() {
		public void onClick(View v) {
			// call save method code
			AddEditMusic.this.blSaveNew = false;
			AddEditMusic.this.mySaveDataMethod();
		}// end onClick
	};// end btnSaveListener

	/**
	 * btnSaveNewListener: Save button Listener
	 * 
	 * Validates the data in the "Required" fields. (Description, date, and
	 * amount)
	 * 
	 * If the validation is successful, the bundles are populated, and then
	 * cleanup is performed, and then the program returns focus to the calling
	 * code, setting a flag to have the calling code call this class again after
	 * the data is successfully committed to the database.
	 * 
	 */
	private OnClickListener btnSaveNewListener = new OnClickListener() {
		public void onClick(View v) {
			// call savenew method code
			AddEditMusic.this.blSaveNew = true;
			AddEditMusic.this.mySaveDataMethod();
		}// end onClick
	};// end btnSaveNewListener

	/**
	 * myCancelMethod: Cancel code
	 * 
	 * Discards changes and returns to the calling screen with status
	 * 'RESULT_CANCELED'
	 * 
	 */
	private void myCancelMethod() {
		APPGlobalVars.SCR_PAUSE_CTL = "CANCEL";

		Bundle bundle = new Bundle();
		bundle.putBoolean("SHOW_FILTER", true);
		bundle.putBoolean("IS_SAVE_NEW", AddEditMusic.this.blSaveNew);

		Intent mIntent = new Intent();
		mIntent.putExtras(bundle);
		setResult(RESULT_CANCELED, mIntent);

		// cleanup objects
		bundle = null;
		mIntent = null;

		System.gc();
		finish();
	};// end myCancelMethod

	/**
	 * btnCancelListener: Cancel button Listener
	 * 
	 * Discards changes and returns to the calling screen with status
	 * 'RESULT_CANCELED'
	 * 
	 */
	private OnClickListener btnCancelListener = new OnClickListener() {
		public void onClick(View v) {
			AddEditMusic.this.blSaveNew = false;
			AddEditMusic.this.myCancelMethod();
		}// end onClick
	};// end btnCancelListener

	/**
	 * onResume: onResume: code for when the Activity resumes
	 * 
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(),
	 * for your activity to start interacting with the user.
	 * 
	 * This is a good place to begin animations, open exclusive-access devices
	 * (such as the camera), etc.
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();

		AddEditMusic.this.blSaveNew = false;
	}// end onResume

	/**
	 * onPause method
	 * 
	 * Called as part of the activity lifecycle when an activity is going into
	 * the background, but has not (yet) been killed. The counterpart to
	 * onResume().
	 * 
	 * This callback is mostly used for saving any persistent state the activity
	 * is editing, to present a "edit in place" model to the user and making
	 * sure nothing is lost if there are not enough resources to start the new
	 * activity without first killing this one.
	 * 
	 * This is also a good place to do things like stop animations and other
	 * things that consume a noticeable mount of CPU in order to make the switch
	 * to the next activity as fast as possible, or to close resources that are
	 * exclusive access such as the camera.
	 * 
	 * Checks the current state. If in "SAVE", "CANCEL", or "SAVEINSTANCE"
	 * states, then the "pause" code is bypassed.
	 * 
	 * "SAVEINSTANCE" is used for when the screen orientation is changed.
	 * 
	 * @return void
	 * 
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// The user is going somewhere else, so make sure their current
		// changes are safely saved away in the provider. We don't need
		// to do this if only editing.
		if (APPGlobalVars.SCR_PAUSE_CTL != null
				&& ((APPGlobalVars.SCR_PAUSE_CTL.equals("SAVE")) || (APPGlobalVars.SCR_PAUSE_CTL
						.equals("CANCEL")))) {
			APPGlobalVars.SCR_PAUSE_CTL = "";

			if (this.isFinishing() == false)
				finish();
		} else {
			APPGlobalVars.SCR_PAUSE_CTL = "";
		}
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
	 * Note: do not count on this method being called as a place for saving
	 * data! For example, if an activity is editing data in a content provider,
	 * those edits should be committed in either onPause() or
	 * onSaveInstanceState(Bundle), not here.
	 * 
	 * This method is usually implemented to free resources like threads that
	 * are associated with an activity, so that a destroyed activity does not
	 * leave such things around while the rest of its application is still
	 * running.
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
		super.onDestroy();
		// The user is going somewhere else, so make sure their current
		// changes are safely saved away in the provider. We don't need
		// to do this if only editing.
		myAppCleanup();
	}// end onDestroy

	/**
	 * onCreateOptionsMenu method
	 * 
	 * Initialize the contents of the Activity's standard options menu. You
	 * should place your menu items in to menu.
	 * 
	 * This is only called once, the first time the options menu is displayed.
	 * 
	 * To update the menu every time it is displayed, see
	 * onPrepareOptionsMenu(Menu).
	 * 
	 * @param Menu
	 *            menu: The options menu in which you place your items.
	 * 
	 * @return Must return true for the menu to be displayed; if you return
	 *         false it will not be shown.
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.music_entry_menu, menu);
		return true;
	}

	/**
	 * onMenuItemSelected method
	 * 
	 * onMenuItemSelected method Executes code per the menu item selected on the
	 * menu options that appears when the menu button is pressed
	 * 
	 * @param int featureID: The panel that the menu is in.
	 * @param MenuItem
	 *            item: The menu item that was selected.
	 * 
	 * @return Return true to finish processing of selection, or false to
	 *         perform the normal menu handling (calling its Runnable or sending
	 *         a Message to its target Handler).
	 * 
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_menu:
			/* save entry */
			// call save method code
			mySaveDataMethod();
			break;

		case R.id.savenew_menu:
			// call savenew method code
			AddEditMusic.this.blSaveNew = true;
			AddEditMusic.this.mySaveDataMethod();
			break;

		case R.id.cancel_menu:
			// call save method code
			myCancelMethod();
			break;

		case R.id.quit:
			// application is exiting
			Intent intentQuitActivity = new Intent(AddEditMusic.this,
					Music_List.class);
			intentQuitActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentQuitActivity.putExtra("QUITTING", "TRUE");
			startActivity(intentQuitActivity);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}// end onMenuItemSelected

	/**
	 * onKeyDown method
	 * 
	 * Executes code depending on what keyCode is pressed.
	 * 
	 * @param int keyCode
	 * @param KeyEvent
	 *            event KeyEvent object
	 * 
	 * @return true if the code completes execution, false otherwise
	 * 
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:

			Bundle bundle = new Bundle();
			bundle.putBoolean("SHOW_FILTER", true);

			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_CANCELED, mIntent);

			// cleanup objects
			bundle = null;
			mIntent = null;

			System.gc();
			finish();
			return true;

		default:
			return false;
		}
	}// end onKeyDown

	/**
	 * onKeyUp method
	 * 
	 * Executes code depending on what keyCode is pressed.
	 * 
	 * @param int keyCode
	 * @param KeyEvent
	 *            event KeyEvent object
	 * 
	 * @return true if the code completes execution, false otherwise
	 * 
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		try {
			switch (keyCode) {
			case KeyEvent.KEYCODE_ENTER:
				if (txtGenre.hasFocus()) {
					// sends focus to txtGenre (user pressed "Next")
					txtAlbum.requestFocus();
					return true;
				} else if (txtAlbum.hasFocus()) {
					// sends focus to txtArtist (user pressed "Next")
					txtArtist.requestFocus();
					return true;
				}

			default:
				return false;
			}
		}// end try
		catch (Exception error) {
			MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
					AddEditMusic.this);
			errExcpError.addToLogFile(error, "AddEditMusic.onKeyUp", "");
			errExcpError = null;
			return false;
		}// end try/catch (Exception error)
	}// end onKeyUp

	// methods to handle date picking
	/**
	 * btnDatePickerListener method
	 * 
	 * Show a dialog managed by this activity. A call to onCreateDialog(int)
	 * will be made with the same id the first time this is called for a given
	 * id.
	 * 
	 * From thereafter, the dialog will be automatically saved and restored.
	 * 
	 * Each time a dialog is shown, onPrepareDialog(int, Dialog) will be made to
	 * provide an opportunity to do any timely preparation.
	 * 
	 * @param int id The id of the managed dialog.
	 * 
	 * @return void
	 * 
	 */
	private OnClickListener btnDatePickerListener = new OnClickListener() {
		public void onClick(View v) {
			showDialog(DATE_DIALOG_ID);
			txtGenre.requestFocus();
		}// end onClick
	};// end btnDatePickerListener

	/**
	 * onCreateDialog method
	 * 
	 * Callback for creating dialogs that are managed (saved and restored) for
	 * you by the activity. If you use showDialog(int), the activity will call
	 * through to this method the first time, and hang onto it thereafter.
	 * 
	 * Any dialog that is created by this method will automatically be saved and
	 * restored for you, including whether it is showing. If you would like the
	 * activity to manage the saving and restoring dialogs for you, you should
	 * override this method and handle any ids that are passed to
	 * showDialog(int).
	 * 
	 * If you would like an opportunity to prepare your dialog before it is
	 * shown, override onPrepareDialog(int, Dialog).
	 * 
	 * 
	 * @param int id The id of the dialog.
	 * 
	 * @return The dialog
	 * 
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, intYear,
					intMonth, intDay);
		}
		return null;
	}// end onCreateDialog

	/**
	 * onPrepareDialog method
	 * 
	 * Provides an opportunity to prepare a managed dialog before it is being
	 * shown.
	 * 
	 * Override this if you need to update a managed dialog based on the state
	 * of the application each time it is shown.
	 * 
	 * For example, a time picker dialog might want to be updated with the
	 * current time.
	 * 
	 * You should call through to the superclass's implementation.
	 * 
	 * The default implementation will set this Activity as the owner activity
	 * on the Dialog.
	 * 
	 * @param int id The id of the managed dialog.
	 * @param Dialog
	 *            dialog The dialog.
	 * 
	 * @return void
	 * 
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(intYear, intMonth, intDay);
			break;
		}
	}// end onPrepareDialog

	/**
	 * mDateSetListener method
	 * 
	 * The callback received when the user "sets" the date in the dialog, which
	 * is used to indicate the user is done filling in the date.
	 * 
	 * @param int id The id of the managed dialog.
	 * @param Dialog
	 *            dialog The dialog.
	 * 
	 * @return void
	 * 
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			intYear = year;
			intMonth = monthOfYear;
			intDay = dayOfMonth;

			AddEditMusic.this.updateDisplay();
		}// end onDateSet
	};// end mDateSetListener

	/**
	 * updateDisplay method
	 * 
	 * Updates the date we display in the TextView
	 * 
	 * @return void
	 * 
	 */
	private void updateDisplay() {
		// updates the date we display in the TextView
		String strPubDate = "";

		// format the month
		if (this.intMonth < 10) {
			if ((this.intMonth + 1) < 10) {
				strPubDate = strPubDate + "0"
						+ Integer.toString((this.intMonth + 1));
			} else {
				strPubDate = strPubDate + Integer.toString((this.intMonth + 1));
			}
		} else {
			strPubDate = strPubDate + Integer.toString(this.intMonth + 1);
		}

		strPubDate = strPubDate + "/";

		// format the day
		if (this.intDay < 10) {
			strPubDate = strPubDate + "0" + Integer.toString(this.intDay);
		} else {
			strPubDate = strPubDate + Integer.toString(this.intDay);
		}

		strPubDate = strPubDate + "/";

		// year validation
		if (Integer.toString(this.intYear).length() == 4) {
			// 2010 --> 10
			String strTempYear = Integer.toString(this.intYear);
			strPubDate = strPubDate + strTempYear;
		}

		this.txtalbumdate.setText(new StringBuilder()
		// Month is 0 based so add 1
				.append(strPubDate));
	}// end updateDisplay

	/**
	 * onSaveInstanceState method
	 * 
	 * This method is called before an activity may be killed so that when it
	 * comes back some time in the future it can restore its state.
	 * 
	 * If called, this method will occur before onStop(). There are no
	 * guarantees about whether it will occur before or after onPause().
	 * 
	 * @param Bundle
	 *            savedInstanceState: Bundle in which to place your saved state.
	 * 
	 * @return void
	 * 
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		try {
			APPGlobalVars.SCR_PAUSE_CTL = "SAVEINSTANCE";

			savedInstanceState.putString(MyAppDbAdapter.KEY_ALBUM, txtAlbum
					.getText().toString());
			savedInstanceState.putString(MyAppDbAdapter.KEY_GENRE, txtGenre
					.getText().toString());
			savedInstanceState.putString(MyAppDbAdapter.KEY_ARTIST, txtArtist
					.getText().toString());
			savedInstanceState.putString(MyAppDbAdapter.KEY_ALBUMDATE,
					txtalbumdate.getText().toString());
			savedInstanceState.putString(MyAppDbAdapter.KEY_NOTES, txtNotes
					.getText().toString());

			if (mRowId != null) {
				savedInstanceState.putLong(MyAppDbAdapter.KEY_ROWID, mRowId);
			}
		}// end try
		catch (Exception error) {
			MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
					AddEditMusic.this);
			errExcpError.addToLogFile(error,
					"AddEditMusic.onSaveInstanceState", "");
			errExcpError = null;
		}// end try/catch (Exception error)

		super.onSaveInstanceState(savedInstanceState);
	}// end onSaveInstanceState

	/**
	 * onRestoreInstanceState method
	 * 
	 * This method is called after onStart() when the activity is being
	 * re-initialized from a previously saved state, given here in state.
	 * 
	 * Most implementations will simply use onCreate(Bundle) to restore their
	 * state, but it is sometimes convenient to do it here after all of the
	 * initialization has been done or to allow subclasses to decide whether to
	 * use your default implementation.
	 * 
	 * The default implementation of this method performs a restore of any view
	 * state that had previously been frozen by onSaveInstanceState(Bundle).
	 * 
	 * @param Bundle
	 *            savedInstanceState: the data most recently supplied in
	 *            onSaveInstanceState(Bundle).
	 * 
	 * @return void
	 * 
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		try {
			APPGlobalVars.SCR_PAUSE_CTL = "";

			txtGenre = (EditText) findViewById(R.id.txtGenre);
			txtArtist = (EditText) findViewById(R.id.txtArtist);
			txtAlbum = (EditText) findViewById(R.id.txtAlbum);
			txtalbumdate = (EditText) findViewById(R.id.txtalbumdate);
			txtNotes = (EditText) findViewById(R.id.txtNotes);
			btnPickDate = (ImageButton) findViewById(R.id.btnPickDate);

			if (savedInstanceState != null) {
				String strAlbum = savedInstanceState
						.getString(MyAppDbAdapter.KEY_ALBUM);
				String strGenre = savedInstanceState
						.getString(MyAppDbAdapter.KEY_GENRE);
				String strArtist = savedInstanceState
						.getString(MyAppDbAdapter.KEY_ARTIST);
				String strPubDate = savedInstanceState
						.getString(MyAppDbAdapter.KEY_ALBUMDATE);
				String strNotes = savedInstanceState
						.getString(MyAppDbAdapter.KEY_NOTES);
				mRowId = savedInstanceState.getLong(MyAppDbAdapter.KEY_ROWID);

				if (strAlbum != null && !strAlbum.equals("")
						&& !strAlbum.equals(" ")) {
					txtAlbum.setText(strAlbum);
					strOrigAlbum = strAlbum;
				}
				if (strGenre != null && !strGenre.equals("")
						&& !strGenre.equals(" ")) {
					txtGenre.setText(strGenre);
					strOrigGenre = strGenre;
				}
				if (strArtist != null && !strArtist.equals("")
						&& !strArtist.equals(" ")) {
					txtArtist.setText(strArtist);
					strOrigArtist = strArtist;
				}

				if ((strPubDate != null) && !(strPubDate.equals(""))
						&& !(strPubDate.equals(" "))) {
					// format the date for proper formatting to be parsed into
					// the date datatype
					if (strPubDate.length() == 10) {
						txtalbumdate.setText(strPubDate);
						strOrigPubDate = strPubDate;

						// date string: 2010-08-15
						String strYear = strPubDate.substring(6, 10);
						String strMonth = strPubDate.substring(0, 2);
						String strDay = strPubDate.substring(3, 5);

						intYear = Integer.valueOf(strYear);
						intMonth = (Integer.valueOf(strMonth) - 1);
						intDay = Integer.valueOf(strDay);

					}// end if
					else {
						// there was an issue with the date format

						objDisplayAlertClass = new MyDisplayAlertClass(
								AddEditMusic.this,
								new CustAlrtMsgOptnListener(
										CustAlrtMsgOptnListener.MessageCodes.ALERT_TYPE_MSG),
								"Date Format Error", "The date " + strPubDate
										+ " is not in the format MM/DD/YYYY");

						// create date instance for today
						Date today = new Date();

						// create the date formatter
						SimpleDateFormat dateFormatter = new SimpleDateFormat(
								getString(R.string.DATE_FORMAT_ISO8601));

						// format the date into a formatted date-string.
						String strCustChkDate = dateFormatter.format(today);

						// cleanup the dateformatter object
						dateFormatter = null;

						txtalbumdate.setText(strCustChkDate);
						strOrigPubDate = strCustChkDate;

						String strYear = strCustChkDate.substring(6, 10);
						String strMonth = strCustChkDate.substring(0, 2);
						String strDay = strCustChkDate.substring(3, 5);
						
						// 2009-10-02
						intYear = Integer.valueOf(strYear);
						intMonth = (Integer.valueOf(strMonth) - 1);
						intDay = Integer.valueOf(strDay);
					}// end if (strPubDate.length() == 10)
				}// end if ((strPubDate != null) && ...
				else {
					// no date has been set
					// get the current date
					final Calendar c = Calendar.getInstance();
					intYear = c.get(Calendar.YEAR);
					intMonth = c.get(Calendar.MONTH);
					intDay = c.get(Calendar.DAY_OF_MONTH);
				}// end if else ((strPubDate != null) && ...

				if (strNotes != null && !strNotes.equals("")
						&& !strNotes.equals(" ")) {
					txtNotes.setText(strNotes);
				}// end if (strNotes != null && ...
			} else {
				// new entry, need to set some initial values
				// set the current date
				final Calendar c = Calendar.getInstance();
				intYear = c.get(Calendar.YEAR);
				intMonth = c.get(Calendar.MONTH);
				intDay = c.get(Calendar.DAY_OF_MONTH);
			}
		}// end try
		catch (Exception error) {
			MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
					AddEditMusic.this);
			errExcpError.addToLogFile(error,
					"AddEditMusic.onRestoreInstanceState", "");
			errExcpError = null;
		}// end try/catch (Exception error)
	}// end onRestoreInstanceState

	/**
	 * myAppCleanup method
	 * 
	 * Sets object variables to null
	 * 
	 * @return void
	 * 
	 */
	protected void myAppCleanup() {
		// Set object variables to null
		btnCancelListener = null;
		btnSaveListener = null;
		btnSaveNewListener = null;
		btnDatePickerListener = null;
		mDateSetListener = null;

		if (objDisplayAlertClass != null) {
			objDisplayAlertClass.cleanUpClassVars();
			objDisplayAlertClass = null;
		}
	}// end myAppCleanup

	/**
	 * onStop method
	 * 
	 * Called when you are no longer visible to the user. You will next receive
	 * either onRestart(), onDestroy(), or nothing, depending on later user
	 * activity. Note that this method may never be called, in low memory
	 * situations where the system does not have enough memory to keep your
	 * activity's process running after its onPause() method is called. Derived
	 * classes must call through to the super class's implementation of this
	 * method. If they do not, an exception will be thrown.
	 * 
	 * * @return void
	 * 
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}// end onStop

}// end AddEditMusic