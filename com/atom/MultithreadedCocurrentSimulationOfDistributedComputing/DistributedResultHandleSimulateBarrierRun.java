package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.util.concurrent.ConcurrentHashMap;

/*
 * ʹ��ѭ��դ����barrierAction����ϵͳ������ģ��ֲ�ʽ�����е��м�������׶�
 * 
 * @param readyToWork ���λ�������ж�׼�������Ƿ����
 * @param finalResultSet ���ս���������Ա������յĵ���ͳ�ƽ��
 * @param intermediateResultSet �м���������10�����̹߳�ͬ���죬������10�����̵߳ĳ��������
 * 
 * ģ��ֲ�ʽ�����У�reduce�׶ζ��м������Ĵ���
 * ��Ҫģ���˺ϲ�����������δ���
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
	 * ���Ĵ�������������������Ľ�����кϲ��������������ս����
	 */
	@Override
	public void run() {
		//�ж��Ƿ�׼�����
		//10���̵߳Ĺ����Ƿ�������߳����ж�
		if (readyToWork) {
			//�����м�������ֵ���ϣ������������
			for (ConcurrentHashMap<String, Integer> firstStepResultSet : intermediateResultSet.values()) {
				//ȡ�ó���������ļ�����
				System.out.println("��ʼ�ϲ��̣߳���" + intermediateResultSet.keys().nextElement() + "�� �ĳ����������" );
				while (firstStepResultSet.keys().hasMoreElements()) {
					String word = firstStepResultSet.keys().nextElement();
					//�ж����ս�������Ƿ��Ѿ�ͳ���˴˵���
					//���ͳ���ˣ��͸��´˵��ʵļ���
					//���û��ͳ�ƣ�������һ�Լ�ֵ������ͳ�ƴ˵���
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
			//ÿ��10���̵߳��м�����������ϣ������м������ͱ��λ���Ա���һ��ѭ��դ���Ĺ���
			intermediateResultSet.clear();
			readyToWork = false;
			System.out.println("�̣߳���" + intermediateResultSet.keys().nextElement() + "�� �ĳ���������ϲ���ɣ�" );
		}else {
			System.out.println("10̨�����ӻ�׼����ϣ�");
			readyToWork = true;
		}
				
	}
}
