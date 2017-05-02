package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * 使用自定义线程池来模拟分布式计算中的计算机资源池。
 */
public class DistributedEnvironmentSimulateThreadPool {
	
	public static ExecutorService newMyFixedThreadPool(int numberOfThreads) {
		return new TraceThreadPoolExecutor(numberOfThreads);
	}
	
	private static class TraceThreadPoolExecutor extends ThreadPoolExecutor {
		public TraceThreadPoolExecutor(int numberOfThreads){
			super(numberOfThreads, numberOfThreads, 
				0L, TimeUnit.MILLISECONDS, 
				new LinkedBlockingQueue<Runnable>(), 
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						System.out.println("创建子工作线程（模拟分布式计算的工作子机）：" + thread);
						return thread;
					}
				}
			);
		}

		@Override
		public void execute(Runnable command) {
			super.execute(warp(command, clientTrace(), Thread.currentThread().getName()));
		}

		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			System.out.println("准备执行：" + r);
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			System.out.println("执行完毕：" + r);
		}

		@Override
		protected void terminated() {
			System.out.println("线程池退出。");
		}
		
		private Exception clientTrace() {
			return new Exception("Client stack trace");
		}
		
		private Runnable warp(final Runnable task, final Exception clientStack, String clientThreadName) {
			return new Runnable() {
				@Override
				public void run() {
					try {
						task.run();
					} catch(Exception e) {
						clientStack.printStackTrace();
						throw e;
					}
				}
			};
		}
	}
}