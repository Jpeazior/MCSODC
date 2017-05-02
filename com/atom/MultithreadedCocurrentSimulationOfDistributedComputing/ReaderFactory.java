package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;


/*
 * 用于保存和获取文件IO流的唯一实例
 */
public class ReaderFactory {
	private static String FILE_ADDRESS = "";
	private ReaderFactory(){};
	private static final class ReaderContainer{
		private static LineNumberReader lineNumberReader;
		public static LineNumberReader getLineNumberReader(){
			if(lineNumberReader == null) {
				try {
					lineNumberReader = new LineNumberReader(new FileReader(new File(FILE_ADDRESS)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				System.out.println("获取文件！");
			}
			return lineNumberReader;
		}
	}
	
	public static void setFileAddress(String fileAddress) {
		FILE_ADDRESS = fileAddress;
	}
	
	public static LineNumberReader getLineNumberReader(){
		return ReaderContainer.getLineNumberReader();
	}
}
