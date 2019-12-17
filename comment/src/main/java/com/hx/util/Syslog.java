package com.hx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Syslog {
	// private static Logger logger = Logger.getLogger("SYSLOG");
	private static final Logger logger = LoggerFactory.getLogger("SYSLOG");
	// private static Log logger = LogFactory.getLog("SYSLOG");

	private Logger log1;
	

	public Syslog(Logger log1) {
		super();
		this.log1 = log1;
	}

	public void debug1(Object msg) {
		log1.debug(_FILE_LINE_() + msg);
	}

	public void info1(Object msg) {
		log1.info(_FILE_LINE_() + msg);
	}

	public void info1(Object msg, String... param) {
		if (param != null)
			log1.info(_FILE_LINE_() + msg + String.join(",", param));
		else
			log1.info(_FILE_LINE_() + msg);
	}

	public void warn1(Object msg) {
		log1.warn(_FILE_LINE_() + msg);
	}

	public void trace1(Object msg) {
		log1.trace(_FILE_LINE_() + msg);
	}

	public void error1(Object msg) {
		log1.error(_FILE_LINE_() + msg);
	}

	public void out1(Object msg) {
		out(msg);
	}
	public void printTrace1(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		error(buffer.toString());
	}
	//===================静态方法=====================================
	private static String _FILE_LINE_() {
		StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();
		StringBuffer strBuffer = new StringBuffer("[");
		// strBuffer.append(Constants.df.format(new Date())).append("][");
		strBuffer.append(stackTraces[2].getClassName()).append(".java:");
		strBuffer.append(stackTraces[2].getLineNumber());
		// strBuffer.append(stackTraces[0].getMethodName()).append("]");
		// strBuffer.append(stackTraces[0].getClassName()).append(".");
		strBuffer.append("行]");
		return strBuffer.toString();
	}

	public static boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public static boolean isWarnEnabled() {
		return logger.isTraceEnabled();
	}

	public static void debug(Object msg) {
		logger.debug(_FILE_LINE_() + msg);
	}

	public static void info(Object msg) {
		logger.info(_FILE_LINE_() + msg);
	}

	public static void info(Object msg, String... param) {
		if (param != null)
			logger.info(_FILE_LINE_() + msg + String.join(",", param));
		else
			logger.info(_FILE_LINE_() + msg);
	}

	public static void infoLog(Logger logger, Object msg, String... param) {
		if (param != null)
			logger.info(_FILE_LINE_() + msg + String.join(",", param));
		else
			logger.info(_FILE_LINE_() + msg);
	}

	public static void warn(Object msg) {
		logger.warn(_FILE_LINE_() + msg);
	}

	public static void trace(Object msg) {
		logger.trace(_FILE_LINE_() + msg);
	}

	public static void error(Object msg) {
		logger.error(_FILE_LINE_() + msg);
	}

	public static void out(Object msg) {
		System.out.println(msg);
	}

	public static void out(Object msg, String... param) {
		if (param != null)
			System.out.println(_FILE_LINE_() + msg + String.join(",", param));
		else
			System.out.println(_FILE_LINE_() + msg);
	}

	public static void printTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		error(buffer.toString());
	}

	public static void printTrace(Logger logger, Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		logger.error(buffer.toString());
	}

	// 将异常的printStackTrace输出转换为字符串
	public static String getTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}

}
