
```
import android.content.Context;
import android.content.DialogInterface;
import android.view.MenuItem;

//Alert Dialog Result Event Handler
public class CustAlrtMsgOptnListener implements
    MyDisplayAlertClass.CustAlrtMsgOptnListener {
  private final MessageCodes iWhichMessage;
  private final Context ctxPassedContext;

  CustAlrtMsgOptnListener(final MessageCodes iParmEventType) {
    this.iWhichMessage = (MessageCodes) iParmEventType;
    this.ctxPassedContext = null;
  }// end constructor

  CustAlrtMsgOptnListener(final Context ctx, final MessageCodes iParmEventType) {
    this.iWhichMessage = (MessageCodes) iParmEventType;
    this.ctxPassedContext = ctx;
  }// end constructor

  CustAlrtMsgOptnListener(final Context ctx, final MessageCodes iParmEventType,
      final MenuItem menuItem) {
    this.iWhichMessage = (MessageCodes) iParmEventType;
    this.ctxPassedContext = ctx;
  }// end constructor

  /** Message codes. */
  public enum MessageCodes {
    /** reserved for AlertMessage --> does nothing */
    ALERT_TYPE_MSG,
    /** Clear the password info. */
    CLEAR_PASSWORDINFO_CONFIRMATION_MESSAGE,
  }//

  @Override
  public void custEventHandler(int iButtonType) {
    try {
      switch (iButtonType) {
      case DialogInterface.BUTTON_POSITIVE:
        switch (iWhichMessage) {
        case ALERT_TYPE_MSG:
          // nothing to do for alert messages
          break;

        default:
          break;
        }// end switch(iEventType)
        break;

      case DialogInterface.BUTTON_NEGATIVE:
        // nothing to do
        break;
      default:
        break;
      }// end switch(iResultCd)
    } catch (Exception error) {
      MyErrorLog<Exception> errExcpError = new MyErrorLog<Exception>(
          CustAlrtMsgOptnListener.this.ctxPassedContext);
      errExcpError.addToLogFile(error,
          "CustAlrtMsgOptnListener.custEventHandler", "");
      errExcpError = null;
    }// end try/catch (Exception error)
    return;
  }// end public void custEventHandler(int iButtonType)
}// end CustAlrtMsgOptnListener
```