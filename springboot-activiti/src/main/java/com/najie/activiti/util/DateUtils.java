package com.najie.activiti.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final String GENERAL_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
	
    /**
     * @desc 格式化日期
     * @param date
     * @return
     */
	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(GENERAL_DATE_FORMAT);
		return sdf.format(date);
	}
}
