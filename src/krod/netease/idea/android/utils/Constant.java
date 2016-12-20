package krod.netease.idea.android.utils;

/**
 * Created by kswording on 2016/12/12.
 */
public class Constant {
    public static final String FINDID_TEMPLATE_NAME = "FindId Template";
    public static final String FINDID_TEMPLATE = "FindId_template";
    public static final String FINDID_TEMPLATE_NORMAL = "findViewById";

    public static final String CLOG_TEMPLATE_NAME = "CLog Template";
    public static final String CLOG_TEMPLATE = "CLog_template";
    public static final String CLOG_TEMPLATE_NORMAL = "Log.$method$($TAG$, $expr$);$END$";

    public static final String CTOAST_TEMPLATE_NAME = "CToast Template";
    public static final String CTOAST_TEMPLATE = "CToast_template";
    public static final String CTOAST_TEMPLATE_NORMAL = "Toast.makeText($context$, $expr$, $dur$).show();$END$";
}
