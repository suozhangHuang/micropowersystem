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
	
	// 模拟真实物理模型的工作，没有实际实现
	public void storage()
	{
		
	}
	
	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		long timeDelta = 0;
		while(true)
		{
			// 计算休眠时间，并更新当前时刻的电价
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
	
	// 标记仿真是否开始
	private boolean started = false;
	
	// 仿真中的刷新实际间隔时间(ms)
	private final long REFRESH_INTERVAL = 1000;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	private final long TIME_SCALE = 300;
}