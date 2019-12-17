package com.hx.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class DllLoad {
	// LIBFILENAME dll文件的路径

	public static void add2JavaLibPath(String dir) {
		try {
			//System.out.println(pwd);
			// 获取到java.library.path 及系统变量中Path中的内容
			String libpath = System.getProperty("java.library.path");
			if (libpath == null || libpath.length() == 0) {
				libpath = "";
			}
			String[] allpath = libpath.split(System.getProperty("path.separator"));
			dir = URLDecoder.decode(dir, "utf-8");
			boolean havepath = false;
			for (String each : allpath) {
				if (each.equals(dir)) {
					havepath = true;
					break;
				}
			}
			if (!havepath) {
				//libpath = jnotifyLibPath + System.getProperty("path.separator") + libpath;
				//System.setProperty("java.library.path", libpath);
				addDir(dir);
			}
			System.out.println(System.getProperty("java.library.path"));
		} catch (Throwable e) {
			throw new RuntimeException("load Convert.dll error!", e);
		}

	}

	public static void addDir(String libdir) throws IOException {
		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);
			for (int i = 0; i < paths.length; i++) {
				if (libdir.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 0, paths.length);
			tmp[paths.length] = libdir;
			field.set(null, tmp);
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path");
		}
	}

	/**
	 * 判断当前操作系统是不是window
	 * 
	 * @return boolean www.2cto.com
	 */
	public static boolean isWindows() {
		boolean flag = false;
		if (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
			flag = true;
		}
		return flag;
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("sun.arch.data.model"));
//		DllLoad.add2JavaLibPath();
//		System.loadLibrary("jnotify_64bit");
		String hex = Integer.toHexString(0x0111);
		hex = String.format("%04d", Integer.parseInt(hex));
		System.out.println(hex);
		System.out.println(URLEncoder.encode("C:\\Users\\zhw\\Desktop\\test\\机房接口失败信息.txt"));
	}
}
