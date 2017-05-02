package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.util.concurrent.ConcurrentHashMap;

/*
 * 使用循坏栅栏的barrierAction，即系统动作来模拟分布式计算中的中间结果处理阶段
 * 
 * @param readyToWork 标记位，用于判断准备工作是否完成
 * @param finalResultSet 最终结果集，用以保存最终的单词统计结果
 * @param intermediateResultSet 中间结果集，由10条子线程共同构造，保存了10条子线程的初步结果集
 * 
 * 模拟分布式计算中，reduce阶段对中间结果集的处理
 * 主要模拟了合并处理，排序尚未添加
 */
public class DistributedResultHandleSimulateBarrierRun implements Runnable{
	private boolean readyToWork;
	private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> intermediateResultSet;
	private final ConcurrentHashMap<String, Integer> finalResultSet;

	public DistributedResultHandleSimulateBarrierRun(boolean readyToWork,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> intermediateResultSet,
			ConcurrentHashMap<String, Integer> finalResultSet) {
		this.readyToWork = readyToWork;
		this.intermediateResultSet = intermediateResultSet;
		this.finalResultSet = finalResultSet;
	}

	/**
	 * 核心处理函数，将初步结果集的结果进行合并，并保存至最终结果集
	 */
	@Override
	public void run() {
		//判断是否准备完毕
		//10条线程的工作是否结束在线程中判断
		if (readyToWork) {
			//遍历中间结果集的值集合，即初步结果集
			for (ConcurrentHashMap<String, Integer> firstStepResultSet : intermediateResultSet.values()) {
				//取得初步结果集的键集合
				System.out.println("开始合并线程：【" + intermediateResultSet.keys().nextElement() + "】 的初步结果集。" );
				while (firstStepResultSet.keys().hasMoreElements()) {
					String word = firstStepResultSet.keys().nextElement();
					//判断最终结果集中是否已经统计了此单词
					//如果统计了，就更新此单词的计数
					//如果没有统计，就新增一对键值对用以统计此单词
					if (finalResultSet.containsKey(word)) {
						int wordCount = finalResultSet.get(word);
						wordCount += firstStepResultSet.get(word);
						finalResultSet.put(word, wordCount);					
					}
					else {
						finalResultSet.put(word, firstStepResultSet.get(word));
					}
				}
			}
			//每轮10条线程的中间结果集处理完毕，重置中间结果集和标记位，以便下一次循环栅栏的工作
			intermediateResultSet.clear();
			readyToWork = false;
			System.out.println("线程：【" + intermediateResultSet.keys().nextElement() + "】 的初步结果集合并完成！" );
		}else {
			System.out.println("10台工作子机准备完毕！");
			readyToWork = true;
		}
				
	}
}
