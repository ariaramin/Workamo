package com.ariaramin.workamo.Utils;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public String convertPersianNumber(String num) {
        HashMap<String, String> numbers = new HashMap<>();
        numbers.put("0", "۰");
        numbers.put("1", "۱");
        numbers.put("2", "۲");
        numbers.put("3", "۳");
        numbers.put("4", "۴");
        numbers.put("5", "۵");
        numbers.put("6", "۶");
        numbers.put("7", "۷");
        numbers.put("8", "۸");
        numbers.put("9", "۹");
        for (Map.Entry<String, String> entry :
                numbers.entrySet()) {
            num = num.replace(entry.getKey(), entry.getValue());
        }
        return num;
    }

    public String convertLongDate(String date) {
        HashMap<String, String> months = new HashMap<>();
        months.put("01", "فروردین");
        months.put("02", "اردیبهشت");
        months.put("03", "خرداد");
        months.put("04", "تیر");
        months.put("05", "مرداد");
        months.put("06", "شهریور");
        months.put("07", "مهر");
        months.put("08", "آبان");
        months.put("09", "آذر");
        months.put("10", "دی");
        months.put("11", "بهمن");
        months.put("12", "اسفند");
        String[] dateList = date.split("-");
        String year = convertPersianNumber(dateList[0]);
        String month = dateList[1];
        for (Map.Entry<String, String> entry :
                months.entrySet()) {
            month = month.replace(entry.getKey(), entry.getValue());
        }
        String day = convertPersianNumber(dateList[2]);
        return String.format("%s %s %s", day, month, year);
    }
}
