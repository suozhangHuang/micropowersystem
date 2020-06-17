package com.micropowersystem.management;

public class Storage extends Thread
{
	public Storage()
	{
		this.capacity = 0;
		this.currentEnergy = 0;
		this.inputPower = 0;
	}
	
	public void setCapacity(double capacity)
	{
		synchronized(this)
		{
			this.capacity = capacity;
		}
	}
	
	public double getCurrentEnergy()
	{
		synchronized(this)
		{
			return currentEnergy;
		}
	}
	
	public void setInputPower(double inputPower)
	{
		synchronized(this)
		{
			this.inputPower = inputPower;
		}
		if(!started)
		{
			started = true;
			this.start();
		}
	}
	
	// ģ����ʵ����ģ�͵Ĺ�����û��ʵ��ʵ��
	public void storage()
	{
		
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

			synchronized(this)
			{
				currentEnergy += inputPower * timeDelta/1000;
				if(currentEnergy > capacity)
				{
					currentEnergy = capacity;
				}
				if(currentEnergy < 0)
				{
					currentEnergy = 0;
				}
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
	
	private double capacity;
	private double currentEnergy;
	private double inputPower;
	private long timestamp;
	
	// ��Ƿ����Ƿ�ʼ
	private boolean started = false;
	
	// �����е�ˢ��ʵ�ʼ��ʱ��(ms)
	private final long REFRESH_INTERVAL = 1000;
	// ����ʱ����ʵ��ʱ��ı�ֵ
	// ������ÿ����1000ms����Ӧϵͳ����5min
	private final long TIME_SCALE = 300;
}