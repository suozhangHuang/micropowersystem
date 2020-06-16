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
			// 计算休眠时间，并更新当前时刻的电价
			timeDelta = (System.currentTimeMillis() - timestampStart)*TIME_SCALE - timestamp;
			timestamp = (System.currentTimeMillis() - timestampStart)*TIME_SCALE;

			
			// 根据用户类型和时间戳更新用户用电功率
			synchronized(this)
			{
				// TODO 实现用户用电量信息更新
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

	// 仿真中的系统时间，从1970年1月1日 0:00 开始计算经过的ms数
	private long timestamp = 0;
	
	// 用户类型
	public final int USERTYPE0 = 0;
	public final int USERTYPE1 = 1;
	public final int USERTYPE2 = 2;
	public final int USERTYPE3 = 3;
	private int type = USERTYPE0;
	
	// 用户平均用电量
	private double averagePower = 0;
	
	// 用户当前使用功率
	private double currentPower = 0;
	// 用户当前电表读数
	private double currentEnergy = 0;
	
	// 标志自动更新是否开始
	private boolean started = false;

	// 仿真中的刷新实际间隔时间(ms)
	private final long REFRESH_INTERVAL = 1000;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	private final long TIME_SCALE = 300;
	
}
