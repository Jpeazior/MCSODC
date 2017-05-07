package com.atom.MultithreadedCocurrentSimulationOfDistributedComputing;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
			for (Iterator<Entry<String, ConcurrentHashMap<String, Integer>>> intermediateResultSetIterator
					= intermediateResultSet.entrySet().iterator(); intermediateResultSetIterator.hasNext();) {
				
				Entry<String, ConcurrentHashMap<String, Integer>> element = intermediateResultSetIterator.next(); 
				String threadName = element.getKey();
				ConcurrentHashMap<String, Integer> firstStepResultSet = element.getValue();

				//ȡ�ó���������ļ�����
				System.out.println("��ʼ�ϲ��̣߳���" + threadName + "�� �ĳ����������" );
				
				Collection<String> keys = firstStepResultSet.keySet();
				for (Iterator<String> keysIterator = keys.iterator(); keysIterator.hasNext();) {
					String word = keysIterator.next();
					
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
				  
				/*while (firstStepResultSet.keys().hasMoreElements()) {
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
				}*/
				System.out.println("�̣߳���" + threadName + "�� �ĳ���������ϲ���ɣ�" );
			}
			//ÿ��10���̵߳��м�����������ϣ������м������ͱ��λ���Ա���һ��ѭ��դ���Ĺ���
			intermediateResultSet.clear();
			readyToWork = false;

			
		}else {
			System.out.println("10̨�����ӻ�׼����ϣ�");
			readyToWork = true;
		}
				
	}
}
