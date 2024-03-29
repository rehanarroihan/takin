package com.multazamgsd.takin.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringHelper {

    public String cutString(String sentence, Integer maxLength) {
        String output = sentence;
        if (sentence.length() > 0) {
            if (sentence.length() >= maxLength) {
                output = sentence.substring(0, maxLength) + "...";
            }
        }
        return output;
    }

    public String priceOrFree(String price) {
        if (price.equals("0")) {
            return "Free";
        } else {
            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);
            return kursIndonesia.format(Double.parseDouble(price));
        }
    }

    public String timeNow() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
