package com.hx.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public  static  String getNowDateTime(String formatLayout) {
        SimpleDateFormat sdf;
        if (formatLayout == null || formatLayout.length() <= 0) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        else {
            sdf = new SimpleDateFormat(formatLayout);
        }
        return (sdf.format(new Date()));
    }
}
