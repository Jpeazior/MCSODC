package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

/*
 * ʹ��ѭ��դ����barrierAction����ϵͳ������ģ��ֲ�ʽ�����е��м�������׶�
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
