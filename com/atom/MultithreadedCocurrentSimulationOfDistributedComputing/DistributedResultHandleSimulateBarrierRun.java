package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

/*
 * 使用循坏栅栏的barrierAction，即系统动作来模拟分布式计算中的中间结果处理阶段
 */
public class DistributedResultHandleSimulateBarrierRun implements Runnable{
	private boolean completeWork;
	private int threadNumber;
	

	public DistributedResultHandleSimulateBarrierRun(boolean completeWork, int threadNumber) {
		this.completeWork = completeWork;
		this.threadNumber = threadNumber;
	}

	@Override
	public void run() {
		if (completeWork) {
			
		}else {
			
		}
				
	}
}
