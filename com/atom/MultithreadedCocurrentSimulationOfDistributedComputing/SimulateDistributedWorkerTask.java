package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

/*
 * ʹ���߳���ģ��ֲ�ʽ�����еĸ��������ӻ���������.
 * 
 * @param sourceFile 			�̹߳������֮Ψһ���ļ�ʵ�������û���ֵע��
 * @param cyclicBarrier 		�̹߳������֮Ψһ��ѭ��դ��ʵ��������ģ�⴦��ֲ�ʽ�����߼�
 * @param linNumber 			�̹߳������֮�Ѷ�ȡ�� ���ļ��У�ÿ���߳����¶�ʱ������һ�ļ��п�ʼ
 * @param firstTempResultSet 	�߳�˽�б���֮��һ����ʱ������������浱ǰ�߳�ͳ�Ƶĵ��ʼ���
 * 
 * ģ��ֲ�ʽ�����й����ӻ��Ĺ���
 * ��WordCount��һʾ���У������ֲ�ʽ�ӻ���Դ���ݻ���Ϊ����С�鲢�ֱ����
 * �ڴ˽�ÿ�δ��ļ��ж�ȡʮ�����ʣ���ʮ�У�����ÿʮ������ΪһС����ģ��ֲ�ʽ�����еķ�������
 * ʹ��ѭ��դ������Ϊ����ͬ������ʹ10�����̼߳���ִ�л�ȴ�
 * ʵ�ʺ��ļ�����㷨��WordCount�����ƣ�������Ϊ <word_1,count_1>���ļ�ֵ����ʽ
 */
	 
public class SimulateDistributedWorkerTask implements Runnable{
	private File sourceFile;
	private final CyclicBarrier cyclicBarrier;
	private Integer lineNumber;
	private ConcurrentHashMap<String, Integer> firstTempResultSet;
	
	private final static int ONE = 1;
	
	public SimulateDistributedWorkerTask(File sourceFile, CyclicBarrier cyclicBarrier) {
		this.sourceFile = sourceFile;
		this.cyclicBarrier = cyclicBarrier;
	}
	
	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public ConcurrentHashMap<String, Integer> getFirstTempResultSet() {
		return firstTempResultSet;
	}

	public void setFirstTempResultSet(ConcurrentHashMap<String, Integer> firstTempResultSet) {
		this.firstTempResultSet = firstTempResultSet;
	}

	public CyclicBarrier getCyclicBarrier() {
		return cyclicBarrier;
	}



	@Override
	public void run() {	
		//��ע����ļ�ʵ������LineNumberReader����Reader����ָ���кſ�ʼ��ȡ����
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(sourceFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			//�ȴ������̣߳�*10��׼�����
			cyclicBarrier.await();
			//���ö�ȡ����
			int runTime = 0;
			//ִ��ѭ������ȡ10������֮��դ�����ȴ������߳��������
			while (runTime < 10) {
				//��ǰ�Ѷ�ȡ��������
				int concurrentLineNumner;
				String word = null;
				boolean wordIsNull = "".equals(word) || word == null;
				boolean wordNotExisted = true;
				
				while (wordIsNull) {
					synchronized(lineNumber){
						concurrentLineNumner = lineNumber;
						//��ȡ����ǰ�Ѷ�ȡ�������Ժ����̸���
						lineNumber ++;			
					}
					reader.setLineNumber(concurrentLineNumner);
					try {
						word = reader.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				for (String keySet: firstTempResultSet.keySet()) {
					if (word.equals(keySet)) {
						int count = firstTempResultSet.get(keySet);
						count ++;
						firstTempResultSet.put(keySet, count);
						wordNotExisted = false;
					}
				}
				
				if (wordNotExisted) 
					firstTempResultSet.put(word, ONE);
				
				runTime ++;
			}
			//�ȴ������̣߳�*10���������
			cyclicBarrier.await();
		}catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
