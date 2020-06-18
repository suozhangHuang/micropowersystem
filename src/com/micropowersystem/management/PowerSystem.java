package com.micropowersystem.management;

import java.sql.Date;
import java.util.Calendar;

public class PowerSystem extends Thread
{
	public PowerSystem()
	{
		this.price = 0;
		this.providePower = true;
		this.start();
	}
	
	public String getInfo()
	{
		return "电网";
	}
	
	public double getBuyingPrice()
	{
		return price;
	}
	
	public double getSellingPrice()
	{
		return price * 0.95;
	}
	
	public void setCondition(boolean providePower)
	{
		synchronized(this)
		{
			this.providePower = providePower;
		}
	}
	
	public boolean getCondition()
	{
		return providePower;
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
			
			// TODO 根据当前的时间戳更新电价信息
			synchronized(this)
			{
				price = 1;
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date(timestamp));
				
				price *= 0.5*Math.abs(calendar.get(Calendar.MONTH) - 6.5)/(6.5);
				
				if(calendar.get(Calendar.HOUR) < 8 || calendar.get(Calendar.HOUR) > 18)
					price *= 1;
				else
					price *= 2;
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
	private double price;
	private boolean providePower;

	// 仿真中的刷新实际间隔时间(ms)
	private final long REFRESH_INTERVAL = 1000;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	private final long TIME_SCALE = 300;
	
	
}
