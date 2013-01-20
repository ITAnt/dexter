package uk.ac.cam.db538.dexter.merge;

public class TaintConstants {

  public static final int TAINT_SOURCE_SMS = 1 << 0;
  public static final int TAINT_SOURCE_CONTACTS = 1 << 1;

  public static final int TAINT_SINK_OUT = 1 << 31;

  public static final void init() {
    ObjectTaintStorage.set(System.out, TAINT_SINK_OUT);
  }

  public static final int queryTaint(String query) {
    if (query.startsWith("content://sms"))
      return TAINT_SOURCE_SMS;
    else if (query.startsWith("content://com.android.contacts"))
      return TAINT_SOURCE_CONTACTS;
    return 0;
  }
}
