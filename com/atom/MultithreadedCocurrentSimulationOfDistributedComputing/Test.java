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
 * @param THREAD_NUMBER ���������߳�����Ҳ��ѭ��դ��һ��ѭ���ļ���ֵ
 * @param FILE_ADDRESS �������ݣ�һ�ݰ���80������ʵ�txt�ļ�
 * @param intermediateResultSet �м����������Ա���ÿ��10�����߳��������ĳ������������ÿ��դ��ִ����Ϻ����
 * @param finalResultSet ���ս���������Ա�������ͳ�ƺ�ĵ��ʼ���
 * @param distributedEnvironmentSimulateThreadPool �Զ�����̳߳أ�����ģ��ֲ�ʽ�����еļ������Դ��
 * @param readyToWork �ж����߳��Ƿ�׼����ϵı��λ
 * @param cyclicBarrier ѭ��դ�����൱����Ϊ�����ͬ������10�����߳�һ����
 * 
 * ��װ��ҵ���߼���ִ�� Word Count ����
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
		//�����ļ���ַ
		ReaderFactory.setFileAddress(FILE_ADDRESS);
		//ȡ��Ψһ�����ļ���ȡ��
		lineNumberReader = ReaderFactory.getLineNumberReader();
		lineNumber = new AtomicInteger(lineNumberReader.getLineNumber());
		readyToWork = false;
		//�½�ѭ��դ�����������ݼ��������м����ݼ����������ݼ�
		cyclicBarrier = new CyclicBarrier(THREAD_NUMBER,
				new DistributedResultHandleSimulateBarrierRun(readyToWork, intermediateResultSet, finalResultSet));
		//���ʼ�����ʼ���У����10�����߳�
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
