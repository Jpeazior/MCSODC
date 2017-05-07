package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.io.LineNumberReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Jpeazior
 * 
 * @param THREAD_NUMBER 开启的子线程数，也即循环栅栏一次循环的计数值
 * @param FILE_ADDRESS 输入数据，一份包含80万个单词的txt文件
 * @param intermediateResultSet 中间结果集，用以保存每轮10条子线程所产生的初步结果集。在每轮栅栏执行完毕后清空
 * @param finalResultSet 最终结果集，用以保存最终统计后的单词计数
 * @param distributedEnvironmentSimulateThreadPool 自定义的线程池，用以模拟分布式环境中的计算机资源池
 * @param readyToWork 判断子线程是否准备完毕的标记位
 * @param cyclicBarrier 循环栅栏，相当于人为添加了同步条，10条子线程一起工作
 * 
 * 封装了业务逻辑，执行 Word Count 任务
 * 
 */
public class Test {
	private final static int THREAD_NUMBER = 10;
	private volatile static AtomicInteger lineNumber;
	private final static String FILE_ADDRESS = "G:\\MCSODC\\wordCountTest.txt";
	private static ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> intermediateResultSet = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
	private static ConcurrentHashMap<String, Integer> finalResultSet = new ConcurrentHashMap<String, Integer>();
	private static LineNumberReader lineNumberReader;
	private final static ExecutorService distributedEnvironmentSimulateThreadPool = DistributedEnvironmentSimulateThreadPool.newMyFixedThreadPool(10);
	private static boolean readyToWork;
	private static CyclicBarrier cyclicBarrier;

	

	public static void main(String[] args) {
		//设置文件地址
		ReaderFactory.setFileAddress(FILE_ADDRESS);
		//取得唯一的行文件读取器
		lineNumberReader = ReaderFactory.getLineNumberReader();
		lineNumber = new AtomicInteger(lineNumberReader.getLineNumber());
		readyToWork = false;
		//新建循环栅栏，传入数据集，包括中间数据集和最终数据集
		cyclicBarrier = new CyclicBarrier(THREAD_NUMBER,
				new DistributedResultHandleSimulateBarrierRun(readyToWork, intermediateResultSet, finalResultSet));
		//单词计数开始运行，添加10条子线程
		for (int i = 0; i < THREAD_NUMBER; i ++) {
			distributedEnvironmentSimulateThreadPool.execute(
					new SimulateDistributedWorkerTask(lineNumberReader, lineNumber, cyclicBarrier, intermediateResultSet)
					);
		}
		while (finalResultSet.keys().hasMoreElements()) {
			String word = finalResultSet.keys().nextElement();
			int wordCount = finalResultSet.get(word);
			System.out.println(word + ": " + wordCount);
		}
	}

}
