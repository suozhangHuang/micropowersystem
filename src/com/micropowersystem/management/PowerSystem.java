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
		return "����";
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
			// ��������ʱ�䣬�����µ�ǰʱ�̵ĵ��
			timeDelta = (System.currentTimeMillis() - timestampStart)*TIME_SCALE - timestamp;
			timestamp = (System.currentTimeMillis() - timestampStart)*TIME_SCALE;
			
			// TODO ���ݵ�ǰ��ʱ������µ����Ϣ
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

	// �����е�ϵͳʱ�䣬��1970��1��1�� 0:00 ��ʼ���㾭����ms��
	private long timestamp = 0;
	private double price;
	private boolean providePower;

	// �����е�ˢ��ʵ�ʼ��ʱ��(ms)
	private final long REFRESH_INTERVAL = 1000;
	// ����ʱ����ʵ��ʱ��ı�ֵ
	// ������ÿ����1000ms����Ӧϵͳ����5min
	private final long TIME_SCALE = 300;
	
	
}
