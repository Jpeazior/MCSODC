package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.io.File;


/*
 * ���ڱ���ͻ�ȡ�ļ�IO����Ψһʵ��
 */
public class FileFactory {
	private FileFactory(){};
	private static final class FileContainer{
		private static File file;
		public static File getFile(){
			if(file == null) {
				file = new File("E:\\wordCountTest.txt");
				System.out.println("��ȡ�ļ���");
			}
			return file;
		}
	}
	
	public static File getFile(){
		return FileContainer.getFile();
	}
}
