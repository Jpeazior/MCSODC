package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

/*
 * 使用线程来模拟分布式计算中的各个工作子机的子任务.
 * 
 * @param lineNumberReader 		线程共享变量之唯一的按行读取器，由用户注入
 * @param cyclicBarrier 		线程共享变量之唯一的循环栅栏实例，用于模拟处理分布式计算逻辑
 * @param linNumber 			线程共享变量之已读取到 的文件行，每次线程向下读时都从这一文件行开始
 * @param firstTempResultSet 	线程私有变量之第一步临时结果集，将保存当前线程统计的单词计数
 * 
 * 模拟分布式计算中工作子机的工作
 * 在WordCount这一示例中，各个分布式子机将源数据划分为数个小块并分别计算
 * 在此将每次从文件中读取十个单词（即十行），以每十个单词为一小块来模拟分布式计算中的分区函数
 * 使用循环栅栏来人为构成同步条，使10条子线程集体执行或等待
 * 实际核心计算的算法与WordCount中相似，输出结果为 <word_1,count_1>，的键值对形式
 */
	 
public class SimulateDistributedWorkerTask implements Runnable{
	private LineNumberReader lineNumberReader;
	private final CyclicBarrier cyclicBarrier;
	private Integer lineNumber;
	private ConcurrentHashMap<String, Integer> firstTempResultSet;
	
	
	private final static int ONE = 1;
	
	public SimulateDistributedWorkerTask(LineNumberReader lineNumberReader, CyclicBarrier cyclicBarrier) {
		this.lineNumberReader = lineNumberReader;
		this.cyclicBarrier = cyclicBarrier;
	}
	
	public LineNumberReader getLineNumberReader() {
		return lineNumberReader;
	}

	public void setLineNumberReader(LineNumberReader lineNumberReader) {
		this.lineNumberReader = lineNumberReader;
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
		try {
			//等待所有线程（*10）准备完毕
			cyclicBarrier.await();
			//设置读取次数
			int runTime = 0;
			//执行循环，读取10行数据之后被栅栏，等待其他线程完成任务
			while (runTime < 10) {
				//当前已读取到的行数
				int concurrentLineNumner;
				String word = null;
				boolean wordIsNull = "".equals(word) || word == null;
				boolean wordNotExisted = true;
				
				while (wordIsNull) {
					synchronized(lineNumber){
						concurrentLineNumner = lineNumber;
						//获取到当前已读取的行数以后立刻更新
						lineNumber ++;			
					}
					lineNumberReader.setLineNumber(concurrentLineNumner);
					try {
						word = lineNumberReader.readLine();
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
			//等待所有线程（*10）完成任务
			cyclicBarrier.await();
		}catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
