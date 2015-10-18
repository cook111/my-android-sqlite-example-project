
```
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MyDisplayAlertClass extends AlertDialog.Builder{
  private Context ctxContext;
  private final String strMyTitle;
  private final String strMyMsg;
  private CustAlrtMsgOptnListener objOptnListener;
  private final int intPosButton_id;
  private final int intNegButton_id;
  private boolean blIsConfirmationAlert;
  private DialogInterface.OnClickListener myDialogClickListener;
  private AlertDialog alrtMsgBox;

  /**
   * Result Interface - implemented in calling class to handle class-specific
   * alert option selection
   * 
   * @param ctx
   *          the Context within which to work
   */
  public interface CustAlrtMsgOptnListener {
    public void custEventHandler(int iResultCode);
  }// end ResultListener

  /**
   * Constructor - takes the context to allow the database to be opened/created
   * 
   * @param ctx
   *          the Context within which to work
   */
  MyDisplayAlertClass(final Context ctx, CustAlrtMsgOptnListener optnListener,
      String prmMyTitle, String prmMyMsg) {
    super(ctx);
    // this.ctxContext = ctx;
    this.strMyTitle = prmMyTitle;
    this.strMyMsg = prmMyMsg;
    this.objOptnListener = optnListener;
    this.blIsConfirmationAlert = false;
    this.intPosButton_id = 0;
    this.intNegButton_id = 0;

    myDialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            // Yes/Ok button clicked
            objOptnListener.custEventHandler(which);
            dialog.dismiss();
            break;

          case DialogInterface.BUTTON_NEGATIVE:
            // No button clicked
            objOptnListener.custEventHandler(which);
            dialog.dismiss();
            break;
          }// end switch
        } catch (Exception error) {
          MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
              ctxContext);
          errExcpError.addToLogFile(error, "MyDisplayAlertClass.DialogInterface", "no prompt");
          errExcpError = null;
        }// end try/catch (Exception error)
      }// end onClick
    };// end DialogInterface.OnClickListener()

    alrtMsgBox = this.setAlertMsg(prmMyTitle, prmMyMsg);
  }// end constructor

  MyDisplayAlertClass(final Context baseContext,
      CustAlrtMsgOptnListener custAlrtMsgOptnListener, final int prmMyTitle,
      final int prmMyMsg, final int prmPosButton_id, final int prmNegButton_id) {
    super(baseContext);
    this.intPosButton_id = prmPosButton_id;
    this.intNegButton_id = prmNegButton_id;
    this.ctxContext = baseContext;
    this.strMyTitle = this.ctxContext.getString(prmMyTitle);
    this.strMyMsg = this.ctxContext.getString(prmMyMsg);
    this.blIsConfirmationAlert = true;
    this.objOptnListener = custAlrtMsgOptnListener;

    myDialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            // Yes/Ok button clicked
            objOptnListener.custEventHandler(which);
            dialog.dismiss();
            break;

          case DialogInterface.BUTTON_NEGATIVE:
            // No button clicked
            objOptnListener.custEventHandler(which);
            dialog.dismiss();
            break;
          }// end switch
        } catch (Exception error) {
          MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
              ctxContext);
          errExcpError.addToLogFile(error, "MyDisplayAlertClass.DialogInterface", "confirmation alert");
          errExcpError = null;
        }// end try/catch (Exception error)
      }// end onClick
    };// end DialogInterface.OnClickListener()

    alrtMsgBox = this.setAlertMsg(this.strMyTitle, this.strMyMsg);
  }// end constructor

  /**
   * displayAlert: Code to populate Alert Messages in an "Ok" dialog display.
   * 
   * @param String
   *          myTitle - title of the message
   * @param String
   *          myMsg - content of the alert message
   * 
   * @return void
   * 
   */
  private AlertDialog setAlertMsg(String prmMyTitle, String prmMyMsg) {
    try {
      setTitle(prmMyTitle);
      this.setMessage(prmMyMsg);
      this.setCancelable(true);
      
      if (this.blIsConfirmationAlert == false){
        this.setPositiveButton("Ok", myDialogClickListener);
      } else{
        this.setPositiveButton(this.intPosButton_id, myDialogClickListener);
        this.setNegativeButton(this.intNegButton_id, myDialogClickListener);
      }

      return this.show();
    }// end try
    catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          ctxContext);
      errExcpError.addToLogFile(error, "MyDisplayAlertClass.setAlertMsg", "no prompt");
      errExcpError = null;
      return null;
    }// end try/catch (Exception error)
  }// end setAlertMsg

  protected void cleanUpClassVars() {
    // ctxContext = null;
    // objOptnListener = null;
    // myDialogClickListener = null;
    try {
      if (this.alrtMsgBox != null) {
        this.alrtMsgBox.dismiss();
        this.alrtMsgBox = null;
      }
      if (this.objOptnListener != null) {
        this.objOptnListener = null;
      }
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          ctxContext);
      errExcpError.addToLogFile(error, "MyDisplayAlertClass.cleanUpClassVars", "");
      errExcpError = null;
    }// end try/catch (Exception error)
  }//end cleanUpClassVars
}// end MyDisplayAlertClass
```