package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.io.LineNumberReader;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;

public class Test {
	final static int THREAD_NUMBER = 10;
	private static String FILE_ADDRESS = "G:\\MCSODC\\wordCountTest.txt";

	public static void main(String[] args) {
		ReaderFactory.setFileAddress(FILE_ADDRESS);
		LineNumberReader lineNumberReader = ReaderFactory.getLineNumberReader();
		ExecutorService pool = DistributedEnvironmentSimulateThreadPool.newMyFixedThreadPool(10);
		boolean completeWork = false;
		CyclicBarrier cyclicBarrier = new CyclicBarrier(THREAD_NUMBER, new DistributedResultHandleSimulateBarrierRun(completeWork, THREAD_NUMBER));
		for (int i = 0; i < THREAD_NUMBER; i ++) {
			pool.execute(new SimulateDistributedWorkerTask(lineNumberReader, cyclicBarrier));
		}		
	}

}
