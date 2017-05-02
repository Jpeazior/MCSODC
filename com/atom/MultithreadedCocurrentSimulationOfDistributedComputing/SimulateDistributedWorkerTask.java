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
 * 使用线程来模拟分布式计算中的各个工作子机的子任务.
 * 
 * @param sourceFile 			线程共享变量之唯一的文件实例，由用户设值注入
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
		//以注入的文件实例创建LineNumberReader，该Reader将从指定行号开始读取数据
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(sourceFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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
			//等待所有线程（*10）完成任务
			cyclicBarrier.await();
		}catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
