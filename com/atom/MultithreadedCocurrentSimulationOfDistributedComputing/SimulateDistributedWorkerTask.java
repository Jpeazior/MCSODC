package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * 使用线程来模拟分布式计算中的各个工作子机的子任务.
 * 
 * @param intermediateResultSet 		线程共享变量之中间结果集，将保存10条线程处理后的初步结果集
 * @param lineNumberReader 				线程共享变量之唯一的按行读取器，由用户注入
 * @param cyclicBarrier 				线程共享变量之唯一的循环栅栏实例，用于模拟处理分布式计算逻辑
 * @param linNumber 					线程共享变量之已读取到 的文件行，每次线程向下读时都从这一文件行开始
 * @param firstTempResultSet 			线程私有变量之第一步临时结果集，将保存当前线程统计的单词计数
 * 
 * 模拟分布式计算中工作子机的工作
 * 在WordCount这一示例中，各个分布式子机将源数据划分为数个小块并分别计算
 * 在此将每次从文件中读取十个单词（即十行），以每十个单词为一小块来模拟分布式计算中的分区函数
 * 使用循环栅栏来人为构成同步条，使10条子线程集体执行或等待
 * 实际核心计算的算法与WordCount中相似，输出结果为 <word_1,count_1>，的键值对形式
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
	 * 执行子线程任务处理逻辑
	 */
	@Override
	public void run() {			
		try {
			//等待所有线程（*10）准备完毕
			cyclicBarrier.await();
			//设置读取次数
			int runTime = 0;
			firstTempResultSet = new ConcurrentHashMap<String, Integer>();
			//执行循环，读取10行数据（即10个单词）之后被栅栏，等待其他线程完成任务
			while (runTime < 10) {
				//当前已读取到的行数
				int concurrentLineNumner;
				String word = null;
				boolean wordIsNull = "".equals(word) || word == null;
				
				do{
					/*synchronized(lineNumber){
						lineNumber = lineNumberReader.getLineNumber();
						concurrentLineNumner = lineNumber;
						//获取到当前已读取的行数以后立刻更新
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
				
				//执行处理逻辑，每次读取一个单词，在初步结果集中查找是否已经存在
				//如果已经存在，则更新此单词计数
				//如果不存在，就新建一组键值对将单词计数设为1
				
				if(firstTempResultSet.containsKey(word)) {
					int count = firstTempResultSet.get(word);
					count ++;
					firstTempResultSet.put(word, count);
				} else {
					firstTempResultSet.put(word, ONE);
				}
				//完成当前单词的计数处理，执行数加1
				runTime ++;
			}
			//将此线程处理完成的初步结果集置入中间结果集中
			intermediateResultSet.put(Thread.currentThread().toString(), firstTempResultSet);
			//等待所有线程（*10）完成任务
			cyclicBarrier.await();
		}catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
