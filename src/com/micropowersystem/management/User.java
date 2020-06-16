package com.micropowersystem.management;

public class User extends Thread
{
	public User()
	{
		
	}
	
	public void setType(int type)
	{
		synchronized(this)
		{
			this.type = type;
		}
	}
	
	public void setAveragePower(double averagePower)
	{
		synchronized(this)
		{
			this.averagePower = averagePower;
		}
	}
	
	public double getCurrentPower()
	{
		return currentEnergy;
	}
	
	public void userStart()
	{
		if(!started)
		{
			started = true;
			this.start();
		}
	}

	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		long timeDelta = 0;
		while(true)
		{
			// ��������ʱ�䣬�����µ�ǰʱ�̵ĵ��
			timeDelta = (System.currentTimeMillis() - timestampStart)*TIME_SCALE - timestamp;
			timestamp = (System.currentTimeMillis() - timestampStart)*TIME_SCALE;

			
			// �����û����ͺ�ʱ��������û��õ繦��
			synchronized(this)
			{
				// TODO ʵ���û��õ�����Ϣ����
				// currentPower = ...;
				currentEnergy += currentPower * timeDelta;
			}
			
			try
			{
				Thread.sleep(REFRESH_INTERVAL);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	// �����е�ϵͳʱ�䣬��1970��1��1�� 0:00 ��ʼ���㾭����ms��
	private long timestamp = 0;
	
	// �û�����
	public final int USERTYPE0 = 0;
	public final int USERTYPE1 = 1;
	public final int USERTYPE2 = 2;
	public final int USERTYPE3 = 3;
	private int type = USERTYPE0;
	
	// �û�ƽ���õ���
	private double averagePower = 0;
	
	// �û���ǰʹ�ù���
	private double currentPower = 0;
	// �û���ǰ������
	private double currentEnergy = 0;
	
	// ��־�Զ������Ƿ�ʼ
	private boolean started = false;

	// �����е�ˢ��ʵ�ʼ��ʱ��(ms)
	private final long REFRESH_INTERVAL = 1000;
	// ����ʱ����ʵ��ʱ��ı�ֵ
	// ������ÿ����1000ms����Ӧϵͳ����5min
	private final long TIME_SCALE = 300;
	
}
