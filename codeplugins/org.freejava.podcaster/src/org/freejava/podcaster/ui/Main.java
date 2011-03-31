package org.freejava.podcaster.ui;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] ps = new String[] {
	            "EEE, dd MMM yy HH:mm:ss z",
	            "EEE, dd MMM yy HH:mm z",
	            "dd MMM yy HH:mm:ss z",
	            "dd MMM yy HH:mm z",
	            "'Tues, 'dd MMMM yyyy HH:mm:ss z"
		};
		String txt = "Fri, 22 May 2009 14:00:00 -0000";
		String txt2 = "Tues, 03 March 2009 15:00:00 -0000";
		System.out.println(txt);
		for (String mask : ps) {
			Date d = parseUsingMask(mask,txt);
			System.out.println(d);
		}
		System.out.println(txt2);
		for (String mask : ps) {
			Date d = parseUsingMask(mask,txt2);
			System.out.println(d);
		}

	}
    private static Date parseUsingMask(String mask,String sDate) {
        sDate = (sDate!=null) ? sDate.trim() : null;
        ParsePosition pp = null;
        Date d = null;
        DateFormat df = new SimpleDateFormat(mask,Locale.US);
        //df.setLenient(false);
        df.setLenient(true);
        try {
            pp = new ParsePosition(0);
            d = df.parse(sDate,pp);
            if (pp.getIndex()!=sDate.length()) {
                d = null;
            }
            //System.out.println("pp["+pp.getIndex()+"] s["+sDate+" m["+masks[i]+"] d["+d+"]");
        }
        catch (Exception ex1) {
            //System.out.println("s: "+sDate+" m: "+masks[i]+" d: "+null);
        }

        return d;
    }
}
