package com.hx.util;

import java.math.BigDecimal;

public class MyNumberUtil {

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入
	 *
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */

	public static double div(double v1, double v2) {
		return div(v1, v2, 10);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	public static int divide(double v3, double v4, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v3));
		BigDecimal b2 = new BigDecimal(Double.toString(v4));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).intValue();
	}
	
	public static double Str2Double(String str,int digit) {
		double d = Double.parseDouble(str);
		BigDecimal b = new BigDecimal(d);
		d=b.setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	public static Double double2_(Double input,int digit) {
		BigDecimal b = new BigDecimal(input);
		input = b.setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue();
		return input;
	}

}
