/**
 * @author dawson dong
 */

package com.applikey.mattermost.utils.kissUtils.formatter;

import java.text.DecimalFormat;

public class DiskFormatter {

    public static final String B = "B";
    public static final String KB = "KB";
    public static final String MB = "MB";
    public static final String GB = "GB";
    public static final String TB = "TB";
    private static final int UNIT = 1024;
    private static final String FORMAT = "#.00";
    private double kbUnit;
    private double mbUnit;
    private double gbUnit;
    private double tbUnit;

    private String format = FORMAT;
    private int unit = UNIT;

    public DiskFormatter() {
        calUnits();
    }

    public void setUnit(int unit) {
        if (unit > 0) {
            this.unit = unit;
            calUnits();
        }
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String format(double size) {
        if (size < 0) {
            return null;
        } else if (size < kbUnit) {
            return size + "B";
        } else if (size < mbUnit) {
            return division(size, kbUnit) + "KB";
        } else if (size < gbUnit) {
            return division(size, mbUnit) + "MB";
        } else if (size < tbUnit) {
            return division(size, gbUnit) + "GB";
        } else {
            return division(size, tbUnit) + "TB";
        }
    }

    private void calUnits() {
        kbUnit = unit;
        mbUnit = unit * kbUnit;
        gbUnit = unit * mbUnit;
        tbUnit = unit * gbUnit;
    }

    private String division(double size, double unit) {
        final double result = size / unit;
        final DecimalFormat df = new DecimalFormat(format);
        //noinspection UnnecessaryLocalVariable
        final String formatted = df.format(result);
        return formatted;
    }
}
