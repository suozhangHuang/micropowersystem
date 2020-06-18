package com.micropowersystem.management;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;

public class Manager extends Thread implements Management
{
	public Manager()
	{
		totalPower.put(GENERATOR, new TimeSeries("GENERATOR"));
		totalPower.put(STORAGE, new TimeSeries("STORAGE"));
		totalPower.put(USER, new TimeSeries("USER"));
		totalPower.put(POWERSYSTEM, new TimeSeries("POWERSYSTEM"));
	}
	
	public void setWeather(Weather weather)
	{
		this.weather = weather;
	}
	
	public void setPowerSystem(PowerSystem powerSystem)
	{
		String name = "PowerSystem";
		this.powerSystem = powerSystem;
		powerSystemPower.put(name, new TimeSeries(name));
		powerSystemVoltage.put(name, new TimeSeries(name));
		powerSystemInfo.put(name, powerSystem.getInfo());
		energyMeters.put(name, 0.0);
	}
	
	public void addGenerator(Generator generator)
	{
		String name;
		if(generator instanceof WindTurbine)
		{
			name = String.format("WindTurbine%d", generators.size());
		}
		else if(generator instanceof SolarPanel)
		{
			name = String.format("SolarPanel%d", generators.size());
		}
		else
		{
			name = String.format("Generator%d", generators.size());
		}
		generators.put(name, generator);
		generatorPower.put(name, new TimeSeries(name));
		generatorVoltage.put(name, new TimeSeries(name));
		generatorInfo.put(name, generator.getInfo());
		energyMeters.put(name, 0.0);
	}
	
	public void addUser(User user)
	{
		String name = null;
		switch(user.getType())
		{
		case User.OFFICE:
			name = String.format("Office%d", users.size());
			break;
		case User.FAMILY:
			name = String.format("Family%d", users.size());
			break;
		case User.FACTORY:
			name = String.format("Factory%d", users.size());
			break;
		default:
			name = String.format("User%d", users.size());
			break;
		}
		users.put(name, user);
		userPower.put(name, new TimeSeries(name));
		userVoltage.put(name, new TimeSeries(name));
		userInfo.put(name, user.getInfo());
		energyMeters.put(name, 0.0);
	}
	
	public void addStorage(Storage storage)
	{
		String name = String.format("Storage%d", storages.size());
		storages.put(name, storage);
		storagePower.put(name, new TimeSeries(name));
		storageVoltage.put(name, new TimeSeries(name));
		storageEnergy.put(name, new TimeSeries(name));
		storageInfo.put(name, storage.getInfo());
		energyMeters.put(name, 0.0);
		
		maxStorageInputPower += storage.getMaxInputPower();
		maxStorageOutputPower += storage.getMaxOutputPower();
	}
	
	public void setDataHandler(DataHandler dataHandler)
	{
		this.dataHandler = dataHandler;
	}
	
	@Override
	public boolean getManagerStatus()
	{
		return initialized;
	}

	@Override
	public Vector<String> getNames(int TYPE)
	{
		Vector<String> names = new Vector<String>();
		switch(TYPE)
		{
		case GENERATOR:
			for(String gName:this.generators.keySet())
			{
				names.add(gName);
			}
			return names;
		case STORAGE:
			for(String sName:this.storages.keySet())
			{
				names.add(sName);
			}
			return names;
		case USER:
			for(String uName:this.users.keySet())
			{
				names.add(uName);
			}
			return names;
		case POWERSYSTEM:
			names.add("PowerSystem");
			return names;
		}
		return null;
	}

	@Override
	public HashMap<String, TimeSeries> getPowerTimeSeries(int TYPE)
	{
		switch(TYPE)
		{
		case GENERATOR:
			return this.generatorPower;
		case STORAGE:
			return this.storagePower;
		case USER:
			return this.userPower;
		case POWERSYSTEM:
			return this.powerSystemPower;
		}
		return null;
	}

	@Override
	public HashMap<String, TimeSeries> getVoltageTimeSeries(int TYPE)
	{
		switch(TYPE)
		{
		case GENERATOR:
			return this.generatorVoltage;
		case STORAGE:
			return this.storageVoltage;
		case USER:
			return this.userVoltage;
		case POWERSYSTEM:
			return this.powerSystemVoltage;
		}
		return null;
	}

	@Override
	public TimeSeries getPrices(int TYPE)
	{
		if(TYPE == USER)
			return this.sellingPrice;
		if(TYPE == POWERSYSTEM)
			return this.buyingPrice;
		return null;
	}

	@Override
	public TimeSeries getTotalPowerTimeSeries(int TYPE)
	{
		return totalPower.get(TYPE);
	}

	@Override
	public HashMap<String, String> GetInfo(int TYPE)
	{
		switch(TYPE)
		{
		case GENERATOR:
			return this.generatorInfo;
		case STORAGE:
			return this.storageInfo;
		case USER:
			return this.userInfo;
		case POWERSYSTEM:
			return this.powerSystemInfo;
		}
		return null;
	}

	@Override
	public HashMap<String, TimeSeries> getForecastPower()
	{
		return new HashMap<String, TimeSeries>();
	}
	
	@Override
	public HashMap<String, TimeSeries> getStorageEnergy()
	{
		return this.storageEnergy;
	}

	@Override
	public void sendEmail(String receiveMailAccount)
	{
		try
		{
			this.email.setReceiverAccount(receiveMailAccount);
			this.email.sendEmail("电网提示信息TITLE", "电网提示信息BODY");
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("发送了提醒信息");
	}
	
	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		long timeDelta = 0;
		while(true)
		{
			// 计算休眠时间
			timeDelta = (System.currentTimeMillis() - timestampStart)*TIME_SCALE - timestamp;
			timestamp = (System.currentTimeMillis() - timestampStart)*TIME_SCALE;
			
			// 获得当前的时间
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(timestamp));
			
			// 计算休眠时间内的电能变化
			
			// 发电机的电能用输出来表示
			double totalEnergyGenerator = 0;
			double totalPowerGenerator = 0;
			for(String name:generators.keySet())
			{
				double generated = generators.get(name).getWattHour() - energyMeters.get(name);
				energyMeters.put(name, generators.get(name).getWattHour());

				generatorVoltage.get(name).add(new FixedMillisecond(timestamp), generators.get(name).getVoltage(Generator.REALTIME));
				generatorPower.get(name).add(new FixedMillisecond(timestamp), generators.get(name).getPower(Generator.REALTIME));
				totalPowerGenerator += generators.get(name).getPower(Generator.REALTIME);
				totalEnergyGenerator += generated;
			}
			totalPower.get(GENERATOR).add(new FixedMillisecond(timestamp), totalPowerGenerator);
			
			// 用户的电能用输入来表示
			double totalEnergyUser = 0;
			double totalPowerUser = 0;
			for(String name:users.keySet())
			{
				double consumed = users.get(name).getWattHour() - energyMeters.get(name);
				energyMeters.put(name, users.get(name).getWattHour());

				userVoltage.get(name).add(new FixedMillisecond(timestamp), 220);
				userPower.get(name).add(new FixedMillisecond(timestamp), users.get(name).getPower());
				totalPowerUser += users.get(name).getPower();
				totalEnergyUser += consumed;
			}
			totalPower.get(USER).add(new FixedMillisecond(timestamp), totalPowerUser);
			
			// 储能装置的电能用输入来表示
			double totalEnergyStorage = 0;
			double totalPowerStorage = 0;
			for(String name:storages.keySet())
			{
				double input = storages.get(name).getCurrentEnergy() - energyMeters.get(name);
				energyMeters.put(name, storages.get(name).getCurrentEnergy());
				
				storageEnergy.get(name).add(new FixedMillisecond(timestamp), storages.get(name).getCurrentEnergy());
				storageVoltage.get(name).add(new FixedMillisecond(timestamp), 220);
				storagePower.get(name).add(new FixedMillisecond(timestamp), storages.get(name).getInputPower());
				totalPowerStorage += storages.get(name).getInputPower();
				totalEnergyStorage += input;
			}
			totalPower.get(STORAGE).add(new FixedMillisecond(timestamp), totalPowerStorage);
			
			// 计算从电网输入的能量，并计算对应的电费
			double totalEnergyPowerSystem = totalEnergyStorage + totalEnergyUser - totalEnergyGenerator;
			double totalPowerPowerSystem = totalPowerStorage + totalPowerUser - totalPowerGenerator;
			powerSystemVoltage.get("PowerSystem").add(new FixedMillisecond(timestamp), 220);
			powerSystemPower.get("PowerSystem").add(new FixedMillisecond(timestamp), totalPowerPowerSystem);
			if(totalEnergyPowerSystem>0)
			{
				accumulatedInputEnergy += totalEnergyPowerSystem;
				accumulatedCost += totalEnergyPowerSystem * powerSystem.getBuyingPrice();
			}
			else
			{
				accumulatedOutputEnergy += -totalEnergyPowerSystem;
				accumulatedIncome += -totalEnergyPowerSystem * powerSystem.getSellingPrice();
			}
			if(this.dataHandler != null)
			{
				ArrayList<Double> prices = new ArrayList<Double>();
				prices.add(accumulatedIncome);
				prices.add(accumulatedCost);
				prices.add(accumulatedOutputEnergy);
				prices.add(accumulatedInputEnergy);
				dataHandler.OnDataChanged(prices);
			}
			
			// 统计电价信息
			buyingPrice.add(new FixedMillisecond(timestamp), powerSystem.getBuyingPrice());
			sellingPrice.add(new FixedMillisecond(timestamp), powerSystem.getSellingPrice());
			
			if(pointCount < maxPointCount)
			{
				avgBuyingPrice = (avgBuyingPrice * pointCount + powerSystem.getBuyingPrice())/(pointCount + 1);
				avgSellingPrice = (avgSellingPrice * pointCount + powerSystem.getSellingPrice())/(pointCount + 1);
				buyingPriceList.addFirst(powerSystem.getBuyingPrice());
				sellingPriceList.addFirst(powerSystem.getSellingPrice());
			}
			else
			{
				avgBuyingPrice += (powerSystem.getBuyingPrice() - buyingPriceList.removeLast())/maxPointCount;
				buyingPriceList.addFirst(powerSystem.getBuyingPrice());
				avgSellingPrice += (powerSystem.getSellingPrice() - sellingPriceList.removeLast())/maxPointCount;
				sellingPriceList.addFirst(powerSystem.getSellingPrice());
			}
			
			// 分配下一时间段的储能装置策略
			// 价格低于平均
			System.out.printf("avgSP:%f avgBP:%f SP:%f BP:%f\n", 
					avgSellingPrice, 
					avgBuyingPrice, 
					powerSystem.getSellingPrice(), 
					powerSystem.getBuyingPrice());
			if(avgSellingPrice > powerSystem.getBuyingPrice()*STOP_CONSUMING_RATIO)
			{
				// 如果价格远低于平均，那么就买电
				if(avgSellingPrice > powerSystem.getBuyingPrice()*BUY_RATIO)
				{
					setStorageInputPower(maxStorageInputPower);
				}
				// 否则停止储能装置的供能
				else
				{
					setStorageInputPower(0);
				}
			}
			// 价格高于平均
			if(avgBuyingPrice < powerSystem.getSellingPrice()*START_CONSUMING_RATIO)
			{
				// 如果价格远高于平均，那么就卖电
				if(avgBuyingPrice < powerSystem.getSellingPrice()*SELL_RATIO)
				{
					setStorageInputPower(-maxStorageOutputPower);
				}
				// 否则使用储能装置供电
				else
				{
					setStorageInputPower(-totalPowerUser + totalPowerGenerator);
				}
			}
			
			// 标记初始化完成
			initialized = true;
			
			// 休眠并等待下一次的处理
			try
			{
				Thread.sleep(REFRESH_INTERVAL);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// 对储能装置功率进行设定，使得其输入功率为指定值
	private void setStorageInputPower(double inputPower)
	{
		for(String name:storages.keySet())
		{
			Storage storage = storages.get(name);
			// 设定的输入功率超过最大值，则全部储能设备均设定成最大值
			if(inputPower>maxStorageInputPower)
			{
				storage.setInputPower(storage.getMaxInputPower());
			}
			// 设定的输出功率超过最大值，则全部储能设备均设定成最大值
			else if(inputPower < -maxStorageOutputPower)
			{
				storage.setInputPower(-storage.getMaxOutputPower());
			}
			// 设定的功率合理，则根据输入功率限制值分配各个储能设备的功率
			else
			{
				if(inputPower>0)
				{
					storage.setInputPower(inputPower * storage.getMaxInputPower()/maxStorageInputPower);
				}
				else
				{
					storage.setInputPower(inputPower * storage.getMaxOutputPower()/maxStorageOutputPower);
				}
			}
		}
	}
	
	// 天气信息
	private Weather weather;
	
	// 记录管理的设备
	private PowerSystem powerSystem;
	private HashMap<String, Generator> generators = new HashMap<String, Generator>();
	private HashMap<String, User> users = new HashMap<String, User>();
	private HashMap<String, Storage> storages = new HashMap<String, Storage>();
	
	// 记录上一次设备的电表读数
	private HashMap<String, Double> energyMeters = new HashMap<String, Double>();

	// 设备工作状况信息
	private HashMap<String, TimeSeries> powerSystemVoltage = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> generatorVoltage = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> userVoltage = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> storageVoltage = new HashMap<String, TimeSeries>();
	
	private HashMap<String, TimeSeries> powerSystemPower = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> generatorPower = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> userPower = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> storagePower = new HashMap<String, TimeSeries>();
	
	private HashMap<String, TimeSeries> storageEnergy = new HashMap<String, TimeSeries>();
	private HashMap<Integer, TimeSeries> totalPower = new HashMap<Integer, TimeSeries>();
	
	// 电价信息
	private TimeSeries sellingPrice = new TimeSeries("sellingPrice");
	private TimeSeries buyingPrice = new TimeSeries("buyingPrice");
	
	// 制定策略时使用60个采样点的滚动平均值
	private double avgBuyingPrice = 0;
	private double avgSellingPrice = 0;
	private LinkedList<Double> buyingPriceList = new LinkedList<Double>();
	private LinkedList<Double> sellingPriceList = new LinkedList<Double>();
	private int pointCount = 0;
	private final int maxPointCount = 60;
	
	// 统计储能设备的最大功率
	private double maxStorageInputPower = 0;
	private double maxStorageOutputPower = 0;
	
	// 指定策略时使用的参数 
	// 当avgSellingPrice > buyingPrice * STOP_CONSUMING_RATIO 时停止使用储能装置的储能
	private double STOP_CONSUMING_RATIO = 1.0;
	// 当avgBuyingPrice < sellingPrice * START_CONSUMING_RATIO 时开始使用储能装置的储能
	private double START_CONSUMING_RATIO = 1.0;
	// 当avgSellingPrice > buyingPrice * BUY_RATIO 时开始从电网买电
	private double BUY_RATIO = 0.9;
	// 当avgBuyingPrice < sellingPrice * SELL_RATIO 时向电网卖电
	private double SELL_RATIO = 0.8;
	
	// 设备的info
	private HashMap<String, String> powerSystemInfo = new HashMap<String, String>();
	private HashMap<String, String> generatorInfo = new HashMap<String, String>();
	private HashMap<String, String> userInfo = new HashMap<String, String>();
	private HashMap<String, String> storageInfo = new HashMap<String, String>();
	
	// 用电数据的预测值
	
	
	// 初始化完成标记
	private boolean initialized = false;

	// 仿真中的系统时间，从1970年1月1日 0:00 开始计算经过的ms数
	private long timestamp = 0;
	// 仿真中的刷新实际间隔时间(ms)
	private final long REFRESH_INTERVAL = SimulationSetting.REFRESH_INTERVAL*3;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	private final long TIME_SCALE = SimulationSetting.TIME_SCALE;
	
	// 与累计收入相关的信息
	private DataHandler dataHandler = null;
	private double accumulatedIncome = 0;
	private double accumulatedCost = 0;
	private double accumulatedInputEnergy = 0;
	private double accumulatedOutputEnergy = 0;
	
	// 发送邮件的类
	private Email email = new Email();

}
