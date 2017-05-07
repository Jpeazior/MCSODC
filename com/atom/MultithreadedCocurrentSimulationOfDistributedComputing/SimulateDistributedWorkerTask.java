package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * ʹ���߳���ģ��ֲ�ʽ�����еĸ��������ӻ���������.
 * 
 * @param intermediateResultSet 		�̹߳������֮�м�������������10���̴߳����ĳ��������
 * @param lineNumberReader 				�̹߳������֮Ψһ�İ��ж�ȡ�������û�ע��
 * @param cyclicBarrier 				�̹߳������֮Ψһ��ѭ��դ��ʵ��������ģ�⴦��ֲ�ʽ�����߼�
 * @param linNumber 					�̹߳������֮�Ѷ�ȡ�� ���ļ��У�ÿ���߳����¶�ʱ������һ�ļ��п�ʼ
 * @param firstTempResultSet 			�߳�˽�б���֮��һ����ʱ������������浱ǰ�߳�ͳ�Ƶĵ��ʼ���
 * 
 * ģ��ֲ�ʽ�����й����ӻ��Ĺ���
 * ��WordCount��һʾ���У������ֲ�ʽ�ӻ���Դ���ݻ���Ϊ����С�鲢�ֱ����
 * �ڴ˽�ÿ�δ��ļ��ж�ȡʮ�����ʣ���ʮ�У�����ÿʮ������ΪһС����ģ��ֲ�ʽ�����еķ�������
 * ʹ��ѭ��դ������Ϊ����ͬ������ʹ10�����̼߳���ִ�л�ȴ�
 * ʵ�ʺ��ļ�����㷨��WordCount�����ƣ�������Ϊ <word_1,count_1>���ļ�ֵ����ʽ
 */
	 
public class SimulateDistributedWorkerTask implements Runnable{
	private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> intermediateResultSet;
	private final LineNumberReader lineNumberReader;
	private final CyclicBarrier cyclicBarrier;
	private volatile AtomicInteger lineNumber;
	private ConcurrentHashMap<String, Integer> firstTempResultSet;
	
	
	private final static int ONE = 1;
	
	public SimulateDistributedWorkerTask(LineNumberReader lineNumberReader,
			AtomicInteger lineNumber,
			CyclicBarrier cyclicBarrier,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> intermediateResultSet) {
		
		this.lineNumberReader = lineNumberReader;
		this.lineNumber = lineNumber;
		this.cyclicBarrier = cyclicBarrier;
		this.intermediateResultSet = intermediateResultSet;
	}
	
	public LineNumberReader getLineNumberReader() {
		return lineNumberReader;
	}

	public AtomicInteger getLineNumber() {
		return lineNumber;
	}

	public ConcurrentHashMap<String, Integer> getFirstTempResultSet() {
		return firstTempResultSet;
	}

	public CyclicBarrier getCyclicBarrier() {
		return cyclicBarrier;
	}


	/**
	 * ִ�����߳��������߼�
	 */
	@Override
	public void run() {			
		try {
			//�ȴ������̣߳�*10��׼�����
			cyclicBarrier.await();
			//���ö�ȡ����
			int runTime = 0;
			firstTempResultSet = new ConcurrentHashMap<String, Integer>();
			//ִ��ѭ������ȡ10�����ݣ���10�����ʣ�֮��դ�����ȴ������߳��������
			while (runTime < 10) {
				//��ǰ�Ѷ�ȡ��������
				int concurrentLineNumner;
				String word = null;
				boolean wordIsNull = "".equals(word) || word == null;
				
				do{
					/*synchronized(lineNumber){
						lineNumber = lineNumberReader.getLineNumber();
						concurrentLineNumner = lineNumber;
						//��ȡ����ǰ�Ѷ�ȡ�������Ժ����̸���
						lineNumber ++;			
					}*/
					
					concurrentLineNumner = lineNumber.getAndIncrement();					
				
					lineNumberReader.setLineNumber(concurrentLineNumner);
					try {
						word = lineNumberReader.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} while (!wordIsNull);
				
				//ִ�д����߼���ÿ�ζ�ȡһ�����ʣ��ڳ���������в����Ƿ��Ѿ�����
				//����Ѿ����ڣ�����´˵��ʼ���
				//��������ڣ����½�һ���ֵ�Խ����ʼ�����Ϊ1
				
				if(firstTempResultSet.containsKey(word)) {
					int count = firstTempResultSet.get(word);
					count ++;
					firstTempResultSet.put(word, count);
				} else {
					firstTempResultSet.put(word, ONE);
				}
				//��ɵ�ǰ���ʵļ�������ִ������1
				runTime ++;
			}
			//�����̴߳�����ɵĳ�������������м�������
			intermediateResultSet.put(Thread.currentThread().toString(), firstTempResultSet);
			//�ȴ������̣߳�*10���������
			cyclicBarrier.await();
		}catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
