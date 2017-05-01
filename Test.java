package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class Test {

	public static void main(String[] args) {
		ExecutorService pool = DistributedEnvironmentSimulateThreadPool.newMyFixedThreadPool(10);
		pool.execute(null);
		
	}

}
