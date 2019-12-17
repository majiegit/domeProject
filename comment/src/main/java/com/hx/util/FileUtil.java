package com.hx.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件操作工具类
 * @author mao 2013-11-18 下午5:17:58
 */
public class FileUtil {
	// ------- 基础的文件操作方法
	/** 判断文件是否存在 */
	public static boolean existsFile(String file) {
		File f = new File(file);
		if (f.exists() && f.isFile()) {
			return true;
		}
		return false;
	}

	/** 确保文件存在, 如果不存在则创建。 */
	public static boolean insureFileExists(File f) {
		if (f.exists() && f.isFile()) {
			return true;
		}
		File p = new File(f.getParent());
		if (!p.exists() || !p.isDirectory()) {
			p.mkdirs();
		}
		try {
			return f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 确保文件存在, 如果不存在则创建。 */
	public static boolean insureFileExists(String file) {
		File f = new File(file);
		return insureFileExists(f);
	}

	/**
	 * 确保文件所在的目录存在, 如果不存在则创建
	 * @param file (String) 文件路径
	 */
	public static boolean insureDirExists(String file) {
		return insureDir(new File(file).getParentFile());
	}

	/**
	 * 确保文件所在的目录存在, 如果不存在则创建
	 * @param f (File) 文件
	 */
	public static boolean insureDirExists(File f) {
		return insureDir(f.getParentFile());
	}

	/**
	 * 确保目录存在
	 * @param dir (String) 目录路径
	 */
	public static boolean insureDir(String dir) {
		return insureDir(new File(dir));
	}

	/**
	 * 确保目录存在
	 * @param dir (File) 目录
	 */
	public static boolean insureDir(File dir) {
		if (!dir.isDirectory()) {
			return dir.mkdirs();
		}
		return false;
	}

	// -- 文件删除 操作
	/** 删除文件 */
	public static boolean delFile(String fileStr) {
		return delFile(new File(fileStr));
	}

	/** 删除文件 */
	public static boolean delFile(File file) {
		if (file != null && file.isFile()) {
			return file.delete();
		}
		return true;
	}

	/** 删除 文件夹及其子孙文件 */
	public static boolean delDir(File dir) {
		return delDir(dir, true);
	}

	/**
	 * 删除 文件夹及其子孙文件
	 * @param dir (File) 要删除的文件夹
	 * @param isDelSelf (boolean) 是否删除文件夹本身
	 */
	public static boolean delDir(File dir, boolean isDelSelf) {
		if (dir != null && dir.isDirectory()) {
			File[] fs = dir.listFiles();
			if (fs != null && fs.length > 0) {
				for (int i = 0; i < fs.length; i++) {
					if (fs[i].isFile()) {
						if (!fs[i].delete()) {
//							return false;
						}
					} else if (fs[i].isDirectory()) {
						if (!delDir(fs[i], true)) {
//							return false;
						}
					} else {
//						return false;
					}
				}
			}
			if (isDelSelf) {
				return dir.delete();
			}
		}
		return true;
	}

	// -- 文件功能 操作
	/** 在指定的路径下, 获取最后一次修改的子文件, 文件夹忽略 */
	public static File getLastModifyFile(File pDir) {
		File[] fs = pDir.listFiles();
		File lmf = null;
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isFile()) {
				if (lmf == null) {
					lmf = fs[i];
				} else if (fs[i].lastModified() > lmf.lastModified()) {
					lmf = fs[i];
				}
			}
		}
		return lmf;
	}

	/** 在指定的路径下, 获取最后一次修改的子文件夹 */
	public static File getLastModifyDir(File pDir) {
		File[] fs = pDir.listFiles();
		File lmf = null;
		long lmf_st = 0;
		File ft = null;
		for (int i = 0; i < fs.length; i++) {
			ft = fs[i];
			if (ft.isDirectory()) {
				long ft_st = ft.lastModified();
				if (ft_st > lmf_st) {
					lmf = ft;
					lmf_st = ft_st;
				}
			}
		}
		return lmf;
	}

	/**
	 * 返回 最后一个被修改的孙文件夹
	 * @param baseDir (File) 基础路径
	 * @param subNames (String[]/String) 子文件夹名称，如果为null则返回 基础路径的最后一个被修改的子文件夹
	 * @return 返回 各子文件夹中最后一个被修改的孙文件夹。
	 */
	public static File getSubLastModifyDir(File baseDir, String... subNames) {
		File lmf = null;
		if (subNames != null && subNames.length > 1) {
			long time = 0;
			File tmpFile = null;
			long tmpTime = 0;
			String path = baseDir.getPath() + "\\";
			for (int i = 0; i < subNames.length; i++) {
				tmpFile = getLastModifyDir(new File(path + subNames[i]));
				if (tmpFile != null) {
					tmpTime = tmpFile.lastModified();
					if (tmpTime > time) {
						time = tmpTime;
						lmf = tmpFile;
					}
				}
			}
		} else {
			lmf = getLastModifyDir(baseDir);
		}
		return lmf;
	}

	/**
	 * 返回 最后一个被修改的孙文件, 孙文件夹忽略
	 * @param baseDir (File) 基础路径
	 * @param subNames (String[]/String) 子文件夹名称，如果为null则返回 基础路径的最后一个被修改的子文件
	 * @return 返回 各子文件夹中最后一个被修改的孙文件。
	 */
	public static File getSubLastModifyFile(File baseDir, String... subNames) {
		File lmf = null;
		if (subNames != null && subNames.length > 1) {
			long time = 0;
			File tmpFile = null;
			long tmpTime = 0;
			String path = baseDir.getPath() + "\\";
			for (int i = 0; i < subNames.length; i++) {
				tmpFile = getLastModifyFile(new File(path + subNames[i]));
				if (tmpFile != null) {
					tmpTime = tmpFile.lastModified();
					if (tmpTime > time) {
						time = tmpTime;
						lmf = tmpFile;
					}
				}
			}
		} else {
			lmf = getLastModifyFile(baseDir);
		}
		return lmf;
	}

	/**
	 * 将文件按最后修改日期排序
	 * @param files (File[]) 
	 * @param desc (boolean) true: 递减
	 */
	public static void sortByLastModify(File[] files, boolean desc){
		if(files == null || files.length < 2) return ;
		final int Len = files.length;
		long[] lms = new long[Len];
		int i, j;
		for(i = 0; i < Len; i++){
			lms[i] = files[i].lastModified();
		}

		//排序
		long m;
		File f;
		if(desc){
			for(i = 0; i < Len; i++){
				for(j = i+1; j < Len; j++){
					if(lms[j] > lms[i] ){
						m = lms[i];
						lms[i] = lms[j];
						lms[j] = m;

						f = files[i];
						files[i] = files[j];
						files[j] = f;
					}
				}
			}
		}else{
			for(i = 0; i < Len; i++){
				for(j = i+1; j < Len; j++){
					if(lms[j] < lms[i]){
						m = lms[i];
						lms[i] = lms[j];
						lms[j] = m;

						f = files[i];
						files[i] = files[j];
						files[j] = f;
					}
				}
			}
		}
	}

	/** 过滤大于指定日期的文件 */
	public static List<File> filterFloorDate(List<File> files, Date date){
		if(files == null || files.size() ==0) return null;
		final int Len = files.size();
		List<File> nfs = new ArrayList<File>();
		File f = null;
		long lm = 0;
		long cm = date.getTime();
		for(int i = 0; i < Len; i++){
			f = files.get(i);
			lm = f.lastModified();
			if(lm > cm){
				nfs.add(f);
			}
		}
		return nfs;
	}

	/** 过滤大于指定日期的子文件(夹) */
	public static List<File> filterSubFloorDate(File dir, Date date){
		if(dir == null || !dir.isDirectory()) return null;
		String[] fns = dir.list();
		if(fns == null || fns.length ==0) return null;


		List<File> nfs = new ArrayList<File>();
		final int Len = fns.length;
		File f = null;
		long lm = 0;
		long cm = date.getTime();
		for (int i = 0; i < Len; i++) {
			f = new File(dir, fns[i]);
			lm = f.lastModified();
			if(lm > cm){
				nfs.add(f);
			}
		}
		return nfs;
	}

	/** 最后一次修改的子文件, 文件夹忽略 */
	public static File lastModifyFile(List<File> fs) {
		if(fs == null || fs.size() == 0) return null;
		int Len = fs.size();
		File lmf = null;
		long lm = 0;
		File f = null;
		for (int i = 0; i < Len; i++) {
			f = fs.get(i);
			if (f.isFile()) {
				long cm = f.lastModified();
				if (lmf == null || cm > lm) {
					lmf = f;
					lm = cm;
				}
			}
		}
		return lmf;
	}

	/**
	 * 判断 path路径下是否存在指定文件(返回第一次)
	 * @param path
	 * @param fns
	 * @return [file, filepath, filename, index]
	 */
	public static Object[] inPathFileOne(String path, String[] fns){
		if(fns != null && fns.length > 0){
			for(int i = 0; i < fns.length; i++){
				String fp = path + File.separator + fns[i];
				File ff = new File(fp);
				if(ff.isFile()){
					Object[] os = new Object[]{ff, fp, fns[i], i};
					return os;
				}
			}
			return null;
		}
		return null;
	}

	/**
	 * 判断 path路径下是否存在指定文件
	 * @param path
	 * @param fns
	 * @return boolean
	 */
	public static boolean isTypeExist(String path,String[] fns){
		if(fns != null && fns.length > 0){
			for(int i = 0; i < fns.length; i++){
				if(path.endsWith(fns[i])){
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * 判断 path路径下是否存在指定后缀名的文件(返回第一次)
	 * @param path
	 * @param fns
	 * @return [file, filepath, filename, index, 后缀]
	 */
	public static Object[] inPathFileExtOne(String path, String[] fnExts){
		if(fnExts != null && fnExts.length > 0){
			final int E_Len = fnExts.length;
			String[] fnes = new String[E_Len];
			for(int i = 0; i < E_Len; i++){
				fnes[i] = fnExts[i].toLowerCase();
			}

			File dir = new File(path);
			File[] list = dir.listFiles();
			if(list != null && list.length > 0){
				for(int i = 0; i < list.length; i++){
					File file = list[i];
					if(file.isFile()){
						String fn = file.getName().toLowerCase();
						for(int j = 0; j < E_Len; j++){
							if(fn.endsWith(fnes[j])){
								Object[] os = new Object[]{file, file.getAbsolutePath(), file.getName(), i, fnes[j]};
								return os;
							}
						}

					}
				}
			}
		}
		return null;
	}

	/** 列出指定目录下的所有文件 */
	public static List<File> listAllFile(File dir, List<File> fs){
		if(fs == null) fs = new ArrayList<File>();
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			if(files != null && files.length > 0){
				for(int i = 0; i < files.length; i++){
					File file = files[i];
					if(file.isDirectory()){
						listAllFile(file, fs);
					}else{
						fs.add(file);
					}
				}
			}
		}else{
			fs.add(dir);
		}
		return fs;
	}


	/**
	 * @param filepath (String) 文件路径
	 * @return BufferedReader:读取文件
	 */
	public static BufferedReader getbr(String filepath) {
		return getbr(filepath, "UTF-8");
	}

	/**
	 * @param filepath (String) 文件路径
	 * @param charset (String) 字符集
	 * @return BufferedReader:读取文件
	 */
	public static BufferedReader getbr(String filepath, String charset) {
		File file = new File(filepath);
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(file), charset);
			return new BufferedReader(read);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 写文件  
	 * @param to (File) 保存到的文件
	 * @param str (String) 字符串
	 * @param charset (String) 字符集编辑
	 * @param outNull (boolean) false: null值作为""处理
	 * @return
	 */
	public static boolean writeFile(File to, String str, String charset, boolean outNull){
		if(to == null) return false;
		if(charset == null || charset.length() == 0) charset = "UTF-8";
		BufferedWriter bw = null;
		try {
			insureDir(to.getParentFile());
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(to), charset));
			bw.append(str);
			bw.flush();
			bw.close();
			bw = null;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw != null){
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 写文件  
	 * @param to (File) 保存到的文件
	 * @param cs (char[]) 字符串
	 * @param start (int) 
	 * @param len (int) 
	 * @param charset (String) 字符集编辑
	 * @return
	 */
	public static boolean writeFile(File to, char[] cs, int start, int len, String charset){
		if(to == null) return false;
		if(charset == null || charset.length() == 0) charset = "UTF-8";
		BufferedWriter bw = null;
		try {
			insureDir(to.getParentFile());
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(to), charset));
			bw.write(cs, start, len);
			bw.flush();
			bw.close();
			bw = null;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw != null){
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * 读文件，返回字符串
	 *
	 * @param path
	 * @return
	 */
	public static String readFile(String path) {
		File file = new File(path);
		BufferedReader reader = null;
		StringBuffer laststr = new StringBuffer();
		try {
			// System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				laststr.append(tempString);
				laststr.append("\n");
				line++;
				System.out.println(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return laststr.substring(0, laststr.lastIndexOf("\n"));
	}

	/**
	 *
	 * @param f1
	 * @param f2
	 * @throws Exception
	 */
	public static void copyforChannel(File f1,File f2) throws Exception{
		int length=2097152;
		FileInputStream in=new FileInputStream(f1);
		FileOutputStream out=new FileOutputStream(f2);
		FileChannel inC=in.getChannel();
		FileChannel outC=out.getChannel();
		ByteBuffer b=null;
		try {
			while(true){
				if(inC.position()==inC.size()){
					inC.close();
					outC.close();
					in.close();
					out.close();
					break;
				}
				if((inC.size()-inC.position())<length){
					length=(int)(inC.size()-inC.position());
				}else
					length=2097152;
				b=ByteBuffer.allocateDirect(length);
				inC.read(b);
				b.flip();
				outC.write(b);
				outC.force(false);
			}
		} catch(Exception e ){
			e.printStackTrace();

		}finally {
			// TODO: handle finally clause
			inC.close();
			outC.close();
			in.close();
			out.close();
		}
	}

//	// -- 测试
//	public static void main(String[] args) {
////		 boolean b = delDir(new File("E:\\工作笔记\\Test - 副本"), true);
////		 System.out.println(b);
////		String fileStr1= "E:\\华信文件\\信息中心监控项目\\方案\\CTS数据对接方案\\MCP-CTS\\ctsdi\\pump_USR_MCP_TAB_OMIN_CTS_DI_1_2017-07-25_00-52-47_462399_data.dsv";
////		String fileStr2= "E:\\华信文件\\信息中心监控项目\\方案\\CTS数据对接方案\\MCP-CTS\\ctsdi2\\pump_USR_MCP_TAB_OMIN_CTS_DI_1_2017-07-25_00-52-47_462399_data.dsv";
////		
////		try {
////			copyforChannel(new File(fileStr1),new File(fileStr2));
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
//				e.printStackTrace();
////		}
//		
//		
//		
////		String laststr = "0"+"\n";
////		//Long lastTimeSuffix = Long.valueOf(laststr);
////		laststr = laststr.substring(0, laststr.lastIndexOf("\n"));
////		Long lastTimeSuffix = Long.valueOf(laststr);
////		System.out.println(lastTimeSuffix);
//		Pattern  ignorePattern = Pattern.compile("^pump_USR_MCP_TAB_OMIN_CTS_DI_1.*.dsv$");
//		boolean re =ignorePattern.matcher("pump_USR_MCP_TAB_OMIN_CTS_DI_1_2017-08-09_14-06-16_523303_data.dsv").matches();
//		System.out.println(re);
//	}
}
