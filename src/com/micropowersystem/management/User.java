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
			info = String.format("ģ��칫�������õ����������ʱ������õ�\nƽ�����õ���:%f", this.averagePower);
			break;
			
		case FAMILY:
			info = String.format("ģ���ͥ�õ���������ϳ�����߷�\nҹ��ֻ���ļ��ж�������\nƽ�����õ���:%f", this.averagePower);
			break;
			
		case FACTORY:
			// ģ�������͹������õ����
			// �ڹ���ʱ���и߸���
			info = String.format("ģ�������͹������õ�������ڹ���ʱ���и߸���\nƽ�����õ���:%f", this.averagePower);
			break;
			
		case DEFAULT:
			// ����
			info = String.format("����\nƽ�����õ���:0");
			break;
		}
		return info;
	}
	
	/* �����û�ƽ�����ʣ�ע�����ڷ��ļ���ƽ������ */
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
			// ��������ʱ�䣬�����µ�ǰʱ�̵ĵ��
			timeDelta = (System.currentTimeMillis() - timestampStart)*TIME_SCALE - timestamp;
			timestamp = (System.currentTimeMillis() - timestampStart)*TIME_SCALE;

			// �����û����ͺ�ʱ��������û��õ繦��
			synchronized(this)
			{
				if(!blackedOut)
				{
					// ʵ���û��õ�����Ϣ����
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(timestamp));
					
					double hour = calendar.get(Calendar.HOUR_OF_DAY) +  calendar.get(Calendar.MINUTE)/60.0; 
					int month = calendar.get(Calendar.MONTH);
					
					// ��λ��ΪkW
					switch(this.type)
					{
					case OFFICE:
						// ģ��칫�������õ����������ʱ������õ�
						// ȡ50W/m^2, 2000m^2
						// �ļ�ʱ80W/m^2 
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
						// ģ���ͥ�õ���������ϳ�����߷�
						// ҹ��ֻ���ļ��ж�������
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
						// ģ�������͹������õ����
						// �ڹ���ʱ���и߸���
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
						// ����
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

	// �����е�ϵͳʱ�䣬��1970��1��1�� 0:00 ��ʼ���㾭����ms��
	private long timestamp = 0;
	
	// �û�����
	static public final int OFFICE = 0;
	static public final int FAMILY = 1;
	static public final int FACTORY = 2;
	static public final int DEFAULT = 3;
	private int type = FAMILY;
	
	// �û�ƽ���õ���
	private double averagePower = 0;
	
	// �û���ǰʹ�ù���
	private double currentPower = 0;
	// �û���ǰ������
	private double currentEnergy = 0;
	// ��ǰ�Ƿ�ͣ��
	private boolean blackedOut = false;
	
	// ��־�Զ������Ƿ�ʼ
	private boolean started = false;

	// �����е�ˢ��ʵ�ʼ��ʱ��(ms)
	private final long REFRESH_INTERVAL = SimulationSetting.REFRESH_INTERVAL;
	// ����ʱ����ʵ��ʱ��ı�ֵ
	// ������ÿ����1000ms����Ӧϵͳ����5min
	private final long TIME_SCALE = SimulationSetting.TIME_SCALE;
	
}
