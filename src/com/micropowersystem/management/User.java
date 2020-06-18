package com.micropowersystem.management;

import java.sql.Date;
import java.util.Calendar;

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
	
	public String getInfo()
	{
		String info = "";
		switch(type)
		{

		case OFFICE:
			info = String.format("模拟办公场所的用电情况，工作时间均匀用电\n平均日用电量:%f", this.averagePower);
			break;
			
		case FAMILY:
			info = String.format("模拟家庭用电情况，晚上出现晚高峰\n夜晚只在夏季有额外消耗\n平均日用电量:%f", this.averagePower);
			break;
			
		case FACTORY:
			// 模拟生产型工厂的用电情况
			// 在工作时间有高负荷
			info = String.format("模拟生产型工厂的用电情况，在工作时间有高负荷\n平均日用电量:%f", this.averagePower);
			break;
			
		case DEFAULT:
			// 备用
			info = String.format("备用\n平均日用电量:0");
			break;
		}
		return info;
	}
	
	/* 设置用户平均功率，注意是在非夏季的平均功率 */
	public void setAveragePower(double averagePower)
	{
		synchronized(this)
		{
			this.averagePower = averagePower;
		}
	}
	
	public double getWattHour()
	{
		return currentEnergy;
	}
	
	public double getPower()
	{
		return currentPower;
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
				// 实现用户用电量信息更新
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date(timestamp));
				
				// 单位均为kW
				switch(this.type)
				{
				case OFFICE:
					// 模拟办公场所的用电情况，工作时间均匀用电
					// 取50W/m^2, 2000m^2
					// 夏季时80W/m^2 
					if(calendar.get(Calendar.HOUR) >= 8 && calendar.get(Calendar.HOUR) <= 18)
					{
						if(calendar.get(Calendar.MONTH) >= 5 && calendar.get(Calendar.MONTH) <= 10)
							currentPower = 160*averagePower/1140;
						else
							currentPower = 100*averagePower/1140;
					}
					else
					{
						currentPower = 10*averagePower/1140;
					}
					break;
					
				case FAMILY:
					// 模拟家庭用电情况，晚上出现晚高峰
					// 夜晚只在夏季有额外消耗
					if(calendar.get(Calendar.HOUR) >= 18 && calendar.get(Calendar.HOUR) <= 23)
					{
						currentPower = 10*averagePower/78;
					}
					else
					{
						currentPower = 1*averagePower/78;
					}
					if(calendar.get(Calendar.MONTH) >= 5 && calendar.get(Calendar.MONTH) <= 10)
					{
						if(calendar.get(Calendar.HOUR) >= 18 || calendar.get(Calendar.HOUR) <= 8)
							currentPower += 5*averagePower/78;
					}
					break;
					
				case FACTORY:
					// 模拟生产型工厂的用电情况
					// 在工作时间有高负荷
					if(calendar.get(Calendar.HOUR) >= 8 && calendar.get(Calendar.HOUR) <= 18)
					{
						if(calendar.get(Calendar.MONTH) >= 5 && calendar.get(Calendar.MONTH) <= 10)
							currentPower = 750*averagePower/7140;
						else
							currentPower = 700*averagePower/7140;
					}
					else
					{
						currentPower = 10*averagePower/7140;
					}
					break;
					
				case DEFAULT:
					// 备用
					currentPower = 0;
					break;
				}
				
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
	public final int OFFICE = 0;
	public final int FAMILY = 1;
	public final int FACTORY = 2;
	public final int DEFAULT = 3;
	private int type = FAMILY;
	
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
