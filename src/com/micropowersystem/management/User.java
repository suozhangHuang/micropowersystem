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
	
	public int getType()
	{
		return this.type;
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
	
	public void setBlackedOut(boolean blackedOut)
	{
		this.blackedOut = blackedOut;
	}
	
	public double getWattHour()
	{
		return currentEnergy;
	}
	
	public double getPower()
	{
		return blackedOut ? 0 :currentPower;
	}
	
	public boolean getIfBlackedOut() {
		return blackedOut;
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
				if(!blackedOut)
				{
					// 实现用户用电量信息更新
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(timestamp));
					
					double hour = calendar.get(Calendar.HOUR_OF_DAY) +  calendar.get(Calendar.MINUTE)/60.0; 
					int month = calendar.get(Calendar.MONTH);
					
					// 单位均为kW
					switch(this.type)
					{
					case OFFICE:
						// 模拟办公场所的用电情况，工作时间均匀用电
						// 取50W/m^2, 2000m^2
						// 夏季时80W/m^2 
						if(hour >= 8 && hour <= 18)
						{
							if(month >= 5 && month <= 10)
								currentPower = 160*averagePower*24/1140;
							else
								currentPower = 100*averagePower*24/1140;
							currentPower += valleyCurve(50*averagePower*24/1140, (hour-8)/10);
						}
						else if( hour>=7 && hour<=8)
						{
							if(month >= 5 && month <= 10)
								currentPower = continuousInterp(10*averagePower*24/1140, 160*averagePower*24/1140, hour-7);
							else
								currentPower = continuousInterp(10*averagePower*24/1140, 100*averagePower*24/1140, hour-7);
						}
						else if( hour>=18 && hour<=20)
						{
							if(month >= 5 && month <= 10)
								currentPower = continuousInterp(160*averagePower*24/1140, 10*averagePower*24/1140, (hour-18)/2);
							else
								currentPower = continuousInterp(100*averagePower*24/1140, 10*averagePower*24/1140, (hour-18)/2);
						}
						else{
							currentPower = 10*averagePower*24/1140;
						}
						break;
						
					case FAMILY:
						// 模拟家庭用电情况，晚上出现晚高峰
						// 夜晚只在夏季有额外消耗
						if(hour >= 18 && hour <= 23)
						{
							currentPower = 10*averagePower*24/78;
						}
						else if( hour>=23)
						{
							currentPower = continuousInterp(10*averagePower*24/78, 1*averagePower*24/78, hour-23);
						}
						else if( hour<=18 && hour>=16)
						{
							currentPower = continuousInterp(1*averagePower*24/78, 10*averagePower*24/78, (hour-16)/2);
						}
						else
						{
							currentPower = 1*averagePower*24/78;
						}
						if(month >= 5 && month <= 10)
						{
							if(hour >= 18 || hour <= 8)
								currentPower += 5*averagePower*24/78;
						}
						break;
						
					case FACTORY:
						// 模拟生产型工厂的用电情况
						// 在工作时间有高负荷
						if(hour >= 8 && hour <= 21)
						{
							if(month >= 5 && month <= 10)
								currentPower = 750*averagePower*24/10000;
							else
								currentPower = 700*averagePower*24/10000;
							currentPower += valleyCurve(200*averagePower*24/10000, (hour-8)/10);
						}
						else if( hour>=7 && hour<=8)
						{
							if(month >= 5 && month <= 10)
								currentPower = continuousInterp(10*averagePower*24/10000, 750*averagePower*24/10000, hour-7);
							else
								currentPower = continuousInterp(10*averagePower*24/10000, 700*averagePower*24/10000, hour-7);
						}
						else if( hour>=21 && hour<=22)
						{
							if(month >= 5 && month <= 10)
								currentPower = continuousInterp(750*averagePower*24/10000, 10*averagePower*24/10000, hour-21);
							else
								currentPower = continuousInterp(700*averagePower*24/10000, 10*averagePower*24/10000, hour-21);
						}
						else
						{
							currentPower = 10*averagePower*24/10000;
						}
						break;
						
					case DEFAULT:
						// 备用
						currentPower = 0;
						break;
					}
					
					currentEnergy += currentPower * timeDelta/1000;
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
	
	private double continuousInterp(double begin, double end, double t)
	{
		return (0.5*Math.sin(Math.PI*(t-0.5))+0.5)*(end-begin)+begin;
	}
	
	private double valleyCurve(double depth, double t)
	{
		return -Math.exp(-108*(t-0.5)*(t-0.5))*depth;
	}

	// 仿真中的系统时间，从1970年1月1日 0:00 开始计算经过的ms数
	private long timestamp = 0;
	
	// 用户类型
	static public final int OFFICE = 0;
	static public final int FAMILY = 1;
	static public final int FACTORY = 2;
	static public final int DEFAULT = 3;
	private int type = FAMILY;
	
	// 用户平均用电量
	private double averagePower = 0;
	
	// 用户当前使用功率
	private double currentPower = 0;
	// 用户当前电表读数
	private double currentEnergy = 0;
	// 当前是否被停电
	private boolean blackedOut = false;
	
	// 标志自动更新是否开始
	private boolean started = false;

	// 仿真中的刷新实际间隔时间(ms)
	private final long REFRESH_INTERVAL = SimulationSetting.REFRESH_INTERVAL;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	private final long TIME_SCALE = SimulationSetting.TIME_SCALE;
	
}
