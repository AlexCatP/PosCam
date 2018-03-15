package com.psy.util;

import java.io.File;
import java.text.DecimalFormat;


public class FileManager {
	// 递归方式 计算文件的大小
	public static long getTotalSizeOfFilesInDir(final File file) {
		if (file.isFile())
			return file.length();
		final File[] children = file.listFiles();
		long total = 0;
		if (children != null)
			for (final File child : children)
				total += getTotalSizeOfFilesInDir(child);
		return total;
	}

	public static String fileSizeCal(final File file1) {
		long total = getTotalSizeOfFilesInDir(file1);
		DecimalFormat df = new DecimalFormat("0.0");
		if (total == 0) {
			return "0KB";
		} else if (total > 0 && total < 1024) {
			return "<1KB";
		} else if (total >= 1024 && total < (1024 * 1024)) {
			return df.format((double)total / 1024) + "KB";
		} else if (total >= (1024 * 1024) && total < (1024 * 1024 * 1024)) {
			return df.format((double)total / (1024 * 1024)) + "M";
		} else {
			return df.format((double)total /  (1024 * 1024 * 1024))  + "G";
		}
	}

	// public static boolean isFilesDeleted(String filePath,Context context) {
	// File file = new File(filePath);
	// Common.display(context,
	// "file.exists()==="+file.exists()+"===file.delete()==="+file.delete());
	// if (file.exists()) {
	// return file.delete();
	// }
	// return false;
	// }

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
				flag = true;
			}
//			if (temp.isDirectory()) {
//				delAllFile(path + "/" + tempList[i]);// 删除文件夹里面的文件
//				flag = true;
//			}
		}
		return flag;
	}
}
