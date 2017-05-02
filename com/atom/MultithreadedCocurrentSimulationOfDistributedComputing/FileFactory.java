package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.io.File;


/*
 * 用于保存和获取文件IO流的唯一实例
 */
public class FileFactory {
	private FileFactory(){};
	private static final class FileContainer{
		private static File file;
		public static File getFile(){
			if(file == null) {
				file = new File("E:\\wordCountTest.txt");
				System.out.println("获取文件！");
			}
			return file;
		}
	}
	
	public static File getFile(){
		return FileContainer.getFile();
	}
}
