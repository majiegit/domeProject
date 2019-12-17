package com.hx.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @功能描述: 字符串操作帮助类
 * @开发人员: 黄浩
 * @创建日期: 2013-5-15 上午11:41:37
 */
public class MyStringUtil {

	/**
	 * 下划线 _
	 */
	public static final String SPAN = "_";
	/**
	 * 斜杠 \
	 */
	public static final String SLASH = "\\";
	/**
	 * 反斜杠 /
	 */
	public static final String BACKSLASH = "/";
	/**
	 * 点 .
	 */
	public static final String DOT = ".";
	/**
	 * 逗号 ,
	 */
	public static String COMMA = ",";
	/**
	 * 冒号 :
	 */
	public static String COLON = ":";
	/**
	 * @
	 */
	public static String ALT = "@";


	/**
	 * 空字符串 ""
	 */
	public static final String EMPTY = "";
	/**
	 * 空格 " "
	 */
	public static final String BLANK = " ";
	/**
	 * 双下划线 "__"
	 */
	public static final String DOUBLE_DASH_SEPERATOR = "__";


	/**
	 * 判断的指定的变量是否为空
	 * 
	 * @param t
	 *            需要判断的字符串
	 * @return 如为 null 范围 “”，如不为null，范围实际内容
	 */
	public static String checkNull(String t) {
		if (t == null) {
			return "";
		} else {
			return t;
		}
	}

	/**
	 * 
	 * @功能描述: (这里用一句话描述这个方法的作用)
	 * @输入参数: @param t
	 * @输入参数: @return
	 * @返回描述:
	 * @异常类型:
	 */
	public static String checkNullObj(Object t) {
		if (t == null) {
			return "";
		} else {
			return (String) t;
		}
	}


	/**
	 * 判断字符串是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}
	
	
	public static boolean isEmptys(String ...args) {
		if(args == null){
			return true;
		}
		for(String s:args){
			if(isEmpty(s)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断字符是否为字母
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	/**
	 * 判断字符是否为数字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}
	public static boolean isNumber(String str) {
		return Pattern.matches("[0-9]*", str);
	}
	/**
	 * 计算字符串中给定字符出现的次数
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static int count(String str, char c) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == c) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 将字符串的首字母大写
	 * 
	 * @param s
	 * @return
	 */
	public static String upperFirstChar(String s) {
		if (s == null || s.length() <= 0) {
			return s;
		}
//		StringBuffer sb = new StringBuffer();
//		sb.append(s.substring(0, 1).toUpperCase());
//		sb.append(s);
//		sb.deleteCharAt(1);
//		return sb.toString();
		char[] cs=s.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);
	}

	/**
	 * 将小写字母转化为大写字母
	 * 
	 * @param c
	 * @return
	 */
	public static char toUpperCase(char c) {
		if (c >= 'a' && c <= 'z') {
			return (char) (c - 32);
		}
		return c;
	}

	/**
	 * 将大写字母转化为小写字母
	 * 
	 * @param c
	 * @return
	 */
	public static char toLowerCase(char c) {
		if (c >= 'A' && c <= 'Z') {
			return (char) (c + 32);
		}
		return c;
	}

	/**
	 * 判断字符是否为空字符
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isBlank(char c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
	}

	/**
	 * 将byte数组中指定的部分转化为字符串
	 * 
	 * @param buf
	 *            byte数组
	 * @param first
	 *            第一个byte的位置
	 * @param last
	 *            最后一个byte的位置
	 * @return
	 */
	public static String valueOf(byte[] buf, int first, int last) {
		int start = first;
		for (; start < last; start++) {
			if (buf[start] != 0) {
				break;
			}
		}

		int end = last - 1;
		for (; end >= start; end--) {
			if (buf[end] != 0) {
				break;
			}
		}

		if (end < start) {
			return null;
		}
		return new String(buf, start, end + 1);
	}

	/**
	 * 将byte数组转化为字符串
	 * 
	 * @param buf
	 *            byte数组
	 * @return
	 */
	public static String valueOf(byte[] buf) {
		return valueOf(buf, 0, buf.length);
	}


	/**
	 * 计算字符串的长度
	 * 
	 * @param s
	 * @return
	 */
	public static int size(String s) {
		int size = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 0x7F) {
				size += 2;
			} else {
				size += 1;
			}
		}
		return size;
	}

	/**
	 * 在字符串后追加空格，使其长度达到n
	 * 
	 * @param s
	 *            原始字符串
	 * @param n
	 *            结果字符串的长度
	 * @return
	 */
	public static String append(String s, int n) {
		return append(s, n, ' ');
	}

	/**
	 * 在字符串后追加字符c，使其长度达到n
	 * 
	 * @param s
	 *            原始字符串
	 * @param n
	 *            结果字符串的长度
	 * @return
	 */
	public static String append(String s, int n, char c) {
		StringBuilder sb = new StringBuilder(s);
		for (int i = 0; i < n; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 在字符串左边添加空格，使其长度达到n
	 * 
	 * @param s
	 *            原始字符串
	 * @param n
	 *            结果字符串的长度
	 * @return
	 */
	public static String lpad(String s, int n) {
		return lpad(s, n, ' ');
	}

	/**
	 * 在字符串右边添加空格，使其长度达到n
	 * 
	 * @param s
	 *            原始字符串
	 * @param n
	 *            结果字符串的长度
	 * @return
	 */
	public static String rpad(String s, int n) {
		return rpad(s, n, ' ');
	}

	/**
	 * 在字符串左边添加指定字符串，使其长度达到n
	 * 
	 * @param s
	 *            原始字符串
	 * @param n
	 *            结果字符串的长度
	 * @param padStr
	 *            需要添加的字符串
	 * @return
	 */
	public static String lpad(String s, int n, String padStr) {
		StringBuilder sb = new StringBuilder();
		int pad = n - s.length();

		if (padStr.length() == 0) {
			padStr = " ";
		}
		int times = pad / padStr.length();
		int remain = pad % padStr.length();
		for (int i = 0; i < times; i++) {
			sb.append(padStr);
		}

		if (remain != 0) {
			sb.append(padStr.substring(0, remain));
		}
		sb.append(s);

		return sb.toString();
	}

	/**
	 * 在字符串右边添加指定字符串，使其长度达到n
	 * 
	 * @param s
	 *            原始字符串
	 * @param n
	 *            结果字符串的长度
	 * @param padStr
	 *            需要添加的字符串
	 * @return
	 */
	public static String rpad(String s, int n, String padStr) {
		StringBuilder sb = new StringBuilder(s);

		if (padStr.length() == 0) {
			padStr = " ";
		}
		int pad = n - s.length();
		int times = pad / padStr.length();
		int remain = pad % padStr.length();
		for (int i = 0; i < times; i++) {
			sb.append(padStr);
		}

		if (remain != 0) {
			sb.append(padStr.substring(0, remain));
		}

		return sb.toString();
	}

	/**
	 * 在字符串左边添加指定字符，使其长度达到n
	 * 
	 * @param s
	 *            原始字符串
	 * @param n
	 *            结果字符串的长度
	 * @param c
	 *            需要添加的字符
	 * @return
	 */
	public static String lpad(String s, int n, char c) {
		StringBuilder sb = new StringBuilder();
		int size = size(s);
		int pad = n - size;
		for (int i = 0; i < pad; i++) {
			sb.append(c);
		}
		sb.append(s);
		return sb.toString();
	}

	/**
	 * 在字符串右边添加指定字符，使其长度达到n
	 * 
	 * @param s
	 *            原始字符串
	 * @param n
	 *            结果字符串的长度
	 * @param c
	 *            需要添加的字符
	 * @return
	 */
	public static String rpad(String s, int n, char c) {
		StringBuilder sb = new StringBuilder(s);
		int size = size(s);
		int pad = n - size;
		for (int i = 0; i < pad; i++) {
			sb.append(c);
		}
		return sb.toString();
	}



	/**
	 * 从字符串中删除指定的字符
	 * 
	 * @param s
	 *            原始字符串
	 * @param c
	 *            需要删除的字符
	 * @return
	 */
	public static String filter(String s, char c) {
		return filter(s, new char[] { c });
	}

	/**
	 * 从字符串中删除所有指定的字符
	 * 
	 * @param s
	 *            原始字符串
	 * @param filter
	 *            需要删除的所有字符
	 * @return
	 */
	public static String filter(String s, String filter) {
		char[] ca = new char[filter.length()];
		for (int i = 0; i < filter.length(); i++) {
			ca[i] = filter.charAt(i);
		}
		return filter(s, ca);
	}

	/**
	 * 从字符串中删除所有指定的字符
	 * 
	 * @param s
	 *            原始字符串
	 * @param ca
	 *            需要删除的所有字符数组
	 * @return
	 */
	public static String filter(String s, char[] ca) {
		StringBuilder sb = new StringBuilder();
		for (int start = 0; start < s.length(); start++) {
			char ch = s.charAt(start);
			if (!contains(ca, ch)) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * 将字符串首尾两端的字符c删除
	 * 
	 * @param s
	 *            原始字符串
	 * @param c
	 *            指定字符
	 * @return
	 */
	public static String trim(String s, char c) {
		return trim(s, new char[] { c });
	}

	/**
	 * 将字符串首尾两端的空字符删除
	 * 
	 * @param s
	 *            原始字符串
	 * @return
	 */
	public static String trim(String s) {
		return trim(s, new char[] { ' ', '\t', '\r', '\n' });
	}

	/**
	 * 将字符串首尾两端的所有字符删除
	 * 
	 * @param s
	 *            原始字符串
	 * @param ca
	 *            指定字符数组
	 * @return
	 */
	public static String trim(String s, char[] ca) {
		int start = 0;
		for (; start < s.length(); start++) {
			if (!contains(ca, s.charAt(start))) {
				break;
			}
		}

		int end = s.length() - 1;
		for (; end >= start; end--) {
			if (!contains(ca, s.charAt(end))) {
				break;
			}
		}

		if (end < start) {
			return "";
		}
		return s.substring(start, end + 1);
	}

	/*
	 * 分隔，去掉空字符串
	 */
	public static String[] split(String s, String regex) {
		String[] sa = s.split(regex);
		ArrayList<String> list = new ArrayList<String>(sa.length);

		for (int i = 0; i < sa.length; i++) {
			if (sa[i] == null || sa[i].length() == 0) {
				continue;
			}
			list.add(sa[i]);
		}
		return list.toArray(new String[0]);
	}

	/**
	 * 判断字符数组中是否包含给定字符
	 * 
	 * @param ca
	 *            字符数组
	 * @param c
	 *            给定字符
	 * @return
	 */
	private static boolean contains(char[] ca, char c) {
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] == c) {
				return true;
			}
		}
		return false;
	}



	/**
	* JAVA判断字符串数组中是否包含某字符串元素
	*
	* @param substring 某字符串
	* @param source 源字符串数组
	* @return 包含则返回true，否则返回false
	*/
	public static boolean isInList(String substring, String[] source) {
		if (source == null || source.length == 0 || substring == null) {
			return false;
		}
		for (int i = 0; i < source.length; i++) {
			String aSource = source[i];
			if (aSource.equals(substring)) {
				return true;
			}
		}
		return false;
	}
	
	//判断参数种是否有空字符传
	public static boolean haveEmpty(String... args){
		if (args == null || args.length <= 0) {
			return false;
		}
		for (int i = 0; i < args.length; i++) {
			String aSource = args[i];
			if (isEmpty(aSource)) {
				return true;
			}
		}
		return false;
	}
	
	
    /**匹配&或全角状态字符或标点*/  
    public static final String PATTERN="&|[\uFE30-\uFFA0]|‘’|“”";  
    /*
     * 替换全角字符
     */
    public static String replaceSpecialtyStr(String str){
    	return replaceSpecialtyStr(str,PATTERN,"");
    }
    public static String replaceSpecialtyStr(String str,String pattern,String replace){  
        if(isBlankOrNull(pattern))  
            pattern="\\s*|\t|\r|\n";//去除字符串中空格、换行、制表  
        if(isBlankOrNull(replace))  
            replace="";  
        return Pattern.compile(pattern).matcher(str).replaceAll(replace);  
          
    }  
    public static boolean isBlankOrNull(String str){  
        if(null==str)return true;  
        //return str.length()==0?true:false;  
                   return str.length()==0;  
    }  
      
    /**清除数字和空格*/  
    public static  String cleanBlankOrDigit(String str){  
        if(isBlankOrNull(str))return "null";  
        return Pattern.compile("\\d|\\s").matcher(str).replaceAll("");  
    }  
    
  	//首字母大写
  	public static String captureName(String name) {
  		char[] cs=name.toCharArray();
  		if (Character.isUpperCase(cs[0])){ 
  			return name;
  		}
  		cs[0] = Character.toUpperCase(cs[0]);
  		return String.valueOf(cs);
  	}
  	
  	/**
  	 * isNull:(判断一个Object的变量是否为空) 如果变量的引用值为 null/""/"null" 则返回true 否则返回false
  	 * 
  	 * @author ZhangLu 2017年9月22日13:03:08
  	 * @param str
  	 * @return
  	 */
  	public static boolean isNull(Object str) {
  		boolean isNull = false;
  		try {
  			if (null == str || "".equals(str) || "null" == str)
  				isNull = true;
  		} catch (Exception e) {
  		}
  		return isNull;
  	}
	/*
	 * 获取字符串的编码
	 */
	public static String getEncoding(String str) {      		
		try{
			String encode="GB2312";
			if(str.equalsIgnoreCase(new String(str.getBytes(encode),encode))){
				String s=encode;
				return s;
			}
			encode="ISO-8859-1";
			if(str.equalsIgnoreCase(new String(str.getBytes(encode),encode))){
				String s1=encode;
				return s1;
			}
			encode="UTF-8";
			if(str.equalsIgnoreCase(new String(str.getBytes(encode),encode))){
				String s2=encode;
				return s2;
			}
			encode="GBK";
			if(str.equalsIgnoreCase(new String(str.getBytes(encode),encode))){
				String s3=encode;
				return s3;
			}
		}catch(Exception e){
		}
		return "";
	}  
      
	@SuppressWarnings("deprecation")
	public static String decode(String s,String charset){
		try {
			return URLDecoder.decode(s, charset);
		} catch (Exception e) {
			return URLDecoder.decode(s);
		}
	}
	public static String decode(String s){
		return decode(s, "UTF-8");
	}
	
	public static String getStr(Object obj) {
		if (null == obj) {
			return "";
		}
		return obj.toString();
	}
    /** 
     * Unicode 编码并不只是为某个字符简单定义了一个编码，而且还将其进行了归类。 
    /pP 其中的小写 p 是 property 的意思，表示 Unicode 属性，用于 Unicode 正表达式的前缀。
          大写 P 表示 Unicode 字符集七个字符属性之一：标点字符。\\pP‘’“”]",如果在 JDK 5 或以下的环境中，全角单引号对、双引号对 
          其他六个是 
    L：字母； 
    M：标记符号（一般不会单独出现）； 
    Z：分隔符（比如空格、换行等）； 
    S：符号（比如数学符号、货币符号等）； 
    N：数字（比如阿拉伯数字、罗马数字等）； 
    C：其他字符 
     *  
     * */  
//    public static void main(String[] args){  
//          
//        System.out.println(replaceSpecialtyStr("中国电信2011年第一批IT设备集中采购-存储备份&（），)(UNIX服务器",PATTERN , ""));  
//    }  
}
