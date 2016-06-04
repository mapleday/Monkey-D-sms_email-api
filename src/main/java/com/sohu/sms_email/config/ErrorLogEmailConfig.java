package com.sohu.sms_email.config;

/**
 * Created by Gary Chan on 2016/6/3.
 */
public class ErrorLogEmailConfig {

  public static String EMAIL_HEAD = "<!DOCTYPE html>" +
                   "<html lang=\"en\">" +
                   "<head>" +
                     "<title></title>" +
                     "<script type=\"text/javascript\" src=\"http://apps.bdimg.com/libs/jquery/1.4.4/jquery.js\"></script>" +
                     "<script type=\"text/javascript\">" +
                        "$(function() {" +
                           "$(\"table tr:nth-child(odd)\").addClass(\"odd-row\");" +
                           "$(\"table td:first-child, table th:first-child\").addClass(\"first\");" +
                           "$(\"table td:last-child, table th:last-child\").addClass(\"last\");" +
                         "});" +
                     "</script>" +
                     "<style type=\"text/css\">" +
                           "html, body, div, span, object, iframe," +
                           "h1, h2, h3, h4, h5, h6, p, blockquote, pre," +
                           "abbr, address, cite, code," +
                           "del, dfn, em, img, ins, kbd, q, samp," +
                           "small, strong, sub, sup, var," +
                           "b, i," +
                           "dl, dt, dd, ol, ul, li," +
                           "fieldset, form, label, legend," +
                           "table, caption, tbody, tfoot, thead, tr, th, td {" +
                              "margin:0;" +
                              "padding:0;" +
                              "border:0;" +
                              "outline:0;" +
                              "font-size:100%;" +
                              "vertical-align:baseline;" +
                              "background:transparent;" +
                           "}" +
                           "body {" +
                              "margin:0;" +
                              "padding:0;" +
                              "font:15px/15px \"Helvetica Neue\",Arial, Helvetica, sans-serif;" +
                              "color: #555;" +
                           "}" +
                           "a {color:#666;}" +
                           "#content {width:65%; max-width:690px; margin:0 50px 0;}" +
                           "table {" +
                              "overflow:hidden;" +
                              "border:1px solid #483E3E;" +
                              "background:#fefefe;" +
                              "width:800px;" +
                              "margin:10px auto 0;" +
                              "-moz-border-radius:5px; /* FF1+ */" +
                              "-webkit-border-radius:5px; /* Saf3-4 */" +
                              "border-radius:5px;" +
                              "-moz-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);" +
                              "-webkit-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);" +
                           "}" +
                           "th, td {padding:10px 10px 10px; text-align:left; }" +
                           "th {padding-top:22px; text-shadow: 1px 1px 1px #fff; background:#e8eaeb;}" +
                           "td {border-top:1px solid #735757; border-right:1px solid #735757;}" +
                           "tr.odd-row td {background:#f6f6f6;}" +
                           "th.first {text-align:center}" +
                           "td.last {border-right:none;}" +
                           "td {" +
                               "background: -moz-linear-gradient(100% 25% 90deg, #fefefe, #f9f9f9);" +
                               "background: -webkit-gradient(linear, 0% 0%, 0% 25%, from(#f9f9f9), to(#fefefe));" +
                           "}" +
                           "tr.odd-row td {" +
                               "background: -moz-linear-gradient(100% 25% 90deg, #f6f6f6, #f1f1f1);" +
                               "background: -webkit-gradient(linear, 0% 0%, 0% 25%, from(#f1f1f1), to(#f6f6f6));" +
                           "}" +
                           "th {" +
                               "background: -moz-linear-gradient(100% 20% 90deg, #e8eaeb, #ededed);" +
                               "background: -webkit-gradient(linear, 0% 0%, 0% 20%, from(#ededed), to(#e8eaeb));" +
                           "}" +
                           "tr:first-child th.first {" +
                               "-moz-border-radius-topleft:5px;" +
                               "-webkit-border-top-left-radius:5px; /* Saf3-4 */" +
                           "}" +
                           "tr:first-child th.last {" +
                               "-moz-border-radius-topright:5px;" +
                               "-webkit-border-top-right-radius:5px; /* Saf3-4 */" +
                           "}" +
                           "tr:last-child td.first {" +
                               "-moz-border-radius-bottomleft:5px;" +
                               "-webkit-border-bottom-left-radius:5px; /* Saf3-4 */" +
                           "}" +
                           "tr:last-child td.last {" +
                               "-moz-border-radius-bottomright:5px;" +
                               "-webkit-border-bottom-right-radius:5px; /* Saf3-4 */" +
                           "}" +
                           ".div1 {" +
                               "margin:15px auto 0;" +
                               "color:#00F;" +
                               "font-weight:bold;" +
                           "}" +
                     "</style>" +
                     "</head>" +
                       "<body>" +
                          "<div id=\"content\">";
  public static String EMAIL_TAIL = "</div></body></html>";

}
