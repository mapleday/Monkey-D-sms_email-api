package com.sohu.sms_email.utils;

/**
 * Created by Gary Chan on 2016/4/1.
 */
public class EmailStringFormatUtils {

    private static final String EMAIL_BEGIN_CONTENT = "<br><div><b><font color=\"red\"> %s : </font></b></div><br>" +
            "<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"800\" style=\"border-collapse: collapse; table-layout:fixed;\">";

    private static final String EMAIL_END_CONTENT =
            "<tr><td align=\"center\" ><b>Params</b></td><td style=\"word-wrap:break-word;\">%s</td></tr>" +
            "<tr><td align=\"center\" ><b>StackTrace</b></td><td style=\"word-wrap:break-word;\"><a href=\"%s\">点击查看</a></td></tr>" +
            "<tr><td align=\"center\"><b>出现次数</b></td><td style=\"word-wrap:break-word;\">%d</td></tr>" +
            "<tr><td colspan=\"2\">&nbsp;</td></tr>";

    private static final String EMAIL_HEAD = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN" +
            "\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <html xmlns=\"http://www.w3.org/1999/xhtml\">" +
            " <head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> <title>Demystifying Email Design" +
            "</title> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/> " +
            "</head><body style=\"margin: 0; padding: 0;\"> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
            "<tr> <td><br><div><b>";

    public static String formatHead(String instance) {
        instance = (null==instance ? "" : instance);
        return String.format(EMAIL_BEGIN_CONTENT, instance);
    }

    public static String formatTail(String params, String stackTraceUrl, Integer times) {
        return String.format(EMAIL_END_CONTENT, params, stackTraceUrl, times);
    }

    public static String getEmailHead() {
        return EMAIL_HEAD;
    }

}
