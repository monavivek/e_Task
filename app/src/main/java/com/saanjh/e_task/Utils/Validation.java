package com.saanjh.e_task.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static boolean email_Validation(String email) {
        boolean status = false;

        String email_Pattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(email_Pattern);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            status = true;
        } else {
            status = false;
        }
        return status;
    }

    public static boolean passport_Validation(String passport_no) {
        boolean pass_no = false;
        //([a-zA-Z]{1})\\d{7})")^[A-Z]{1}-[0-9]{7}$
        String passport_Pattern = "^[A-Z]{1}+[0-9]{7}$";
        Pattern pattern = Pattern.compile(passport_Pattern);
        Matcher matcher = pattern.matcher(passport_no);
        if (matcher.matches()) {
            pass_no = true;
        } else {
            pass_no = false;
        }
        return pass_no;
    }

    public static boolean mobile_Validation(String mobile_no) {
        boolean mob_no = false;
        //([a-zA-Z]{1})\\d{7})")^[A-Z]{1}-[0-9]{7}$
        String mobno_Pattern = "^(((\\+?\\(91\\))|0|((00|\\+)?91))-?)?[7-9]\\d{9}$";
        Pattern pattern = Pattern.compile(mobno_Pattern);
        Matcher matcher = pattern.matcher(mobile_no);
        if (matcher.matches()) {
            mob_no = true;
        } else {
            mob_no = false;
        }
        return mob_no;
    }

    public static boolean alpha_special(String txtvalue) {
        boolean value = false;

        String alpha_pattern = "^[ A-Za-z_@./#&+-]*$";
        Pattern pattern = Pattern.compile(alpha_pattern);
        Matcher matcher = pattern.matcher(txtvalue);
        if (matcher.matches()) {
            value = true;
        } else {
            value = false;
        }
        return value;
    }

    public static boolean alpha_numeric(String txtvalue) {
        boolean value = false;

        String alpha_pattern = "^[a-zA-Z0-9]*$";
        Pattern pattern = Pattern.compile(alpha_pattern);
        Matcher matcher = pattern.matcher(txtvalue);
        if (matcher.matches()) {
            value = true;
        } else {
            value = false;
        }
        return value;
    }

    public static boolean alphabets(String txtvalue) {
        boolean value = false;

        String alpha_pattern = "^[a-zA-Z ]+$";
        Pattern pattern = Pattern.compile(alpha_pattern);
        Matcher matcher = pattern.matcher(txtvalue);
        if (matcher.matches()) {
            value = true;
        } else {
            value = false;
        }
        return value;
    }

    public static String replace_str(String val) {

        return val.replaceAll(" ", "%20");
    }
}
