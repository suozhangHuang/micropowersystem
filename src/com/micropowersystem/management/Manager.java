package com.micropowersystem.management;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import org.jfree.data.time.*;

public class Manager extends Thread implements Management
{
	public Manager()
	{
		totalPower.put(GENERATOR, new TimeSeries("GENERATOR"));
		totalPower.put(STORAGE, new TimeSeries("STORAGE"));
		totalPower.put(USER, new TimeSeries("USER"));
		totalPower.put(POWERSYSTEM, new TimeSeries("POWERSYSTEM"));
	}
	
	public void setWeatherForecast(WeatherForecast weatherForecast)
	{
		this.weatherForecast = weatherForecast;
		this.predictor.setWeatherForecast(weatherForecast);
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
			predictor.addWindPowerCapacity(generator.getPower(Generator.NOMIAL));
		}
		else if(generator instanceof SolarPanel)
		{
			name = String.format("SolarPanel%d", generators.size());
			predictor.addSolarPanelArea(((SolarPanel)generator).getPanelArea());
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
		maxStorageEnergy += storage.getCapacity();
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
		return prediction;
	}
	
	@Override
	public HashMap<String, TimeSeries> getStorageEnergy()
	{
		return this.storageEnergy;
	}

	@Override
	public boolean sendEmail(String receiveMailAccount)
	{
		try
		{
			this.email.setReceiverAccount(receiveMailAccount); 
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = null;
			synchronized(this)
			{
				dateString = formatter.format(timestamp);
			}
			String title = String.format("微电网系统信息 %s", dateString);
			
			String body = String.format("来自微电网系统的信息:\n    尊敬的用户您好，您已经卖电%.2f度，收益%.2f元。", 
					accumulatedOutputEnergy/3600 - accumulatedInputEnergy/3600,
					accumulatedIncome-accumulatedCost);
			
			this.email.sendEmail(title, body);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true; 
	}
	
	// 停电预警设置
	// 多次设置时，如果设置的时间是合法的，那么将会直接覆盖
	public void outageWarning(Date begin, Date end)
	{
		Date currentTime = new Date(timestamp);
		if(currentTime.before(begin) && end.after(begin))
		{
			outageExpected = true;
			this.begin = begin;
			this.end = end;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			System.out.printf("接收到停电预警，预计停电时间为\n%s - %s\n", sdf.format(begin), sdf.format(end));
		}
	}
	
	public void outageWarningCancel()
	{
		outageExpected = false;
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
			RegularTimePeriod regularTimePeriod = new FixedMillisecond(timestamp);

			/************************模拟停电*************************/
			
			// 提前12h进行预警
			if(!this.outageExpected)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
				try
				{
					if(timestamp > sdf.parse("1970.01.02 12:00:00").getTime() 
							&& timestamp < sdf.parse("1970.01.02 23:59:59").getTime())
					{
						this.outageWarning(sdf.parse("1970.01.03 00:00:00"), sdf.parse("1970.01.03 23:59:59"));
					}
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
			}
			
			if(calendar.get(Calendar.DAY_OF_YEAR)==3)
			{
				this.powerSystem.setCondition(false);
			}
			else
			{
				this.powerSystem.setCondition(true);
			}
			/************************模拟停电*************************/
			
			// 计算休眠时间内的电能变化
			// 发电机的电能用输出来表示
			double totalEnergyGenerator = 0;
			double totalPowerGenerator = 0;
			for(String name:generators.keySet())
			{
				double generated = generators.get(name).getWattHour() - energyMeters.get(name);
				energyMeters.put(name, generators.get(name).getWattHour());

				generatorVoltage.get(name).add(regularTimePeriod, generators.get(name).getVoltage(Generator.REALTIME));
				generatorPower.get(name).add(regularTimePeriod, generators.get(name).getPower(Generator.REALTIME));
				totalPowerGenerator += generators.get(name).getPower(Generator.REALTIME);
				totalEnergyGenerator += generated;
			}
			totalPower.get(GENERATOR).add(regularTimePeriod, totalPowerGenerator);
			
			// 用户的电能用输入来表示
			double totalEnergyUser = 0;
			double totalPowerUser = 0;
			for(String name:users.keySet())
			{
				double consumed = users.get(name).getWattHour() - energyMeters.get(name);
				energyMeters.put(name, users.get(name).getWattHour());

				userVoltage.get(name).add(regularTimePeriod, 220);
				userPower.get(name).add(regularTimePeriod, users.get(name).getPower());
				totalPowerUser += users.get(name).getPower();
				totalEnergyUser += consumed;
			}
			totalPower.get(USER).add(regularTimePeriod, totalPowerUser);
			
			// 储能装置的电能用输入来表示
			double totalEnergyStorage = 0;
			double totalPowerStorage = 0;
			currentStorageEnergy = 0;
			for(String name:storages.keySet())
			{
				double input = storages.get(name).getCurrentEnergy() - energyMeters.get(name);
				energyMeters.put(name, storages.get(name).getCurrentEnergy());
				
				storageEnergy.get(name).add(regularTimePeriod, storages.get(name).getCurrentEnergy());
				storageVoltage.get(name).add(regularTimePeriod, 220);
				storagePower.get(name).add(regularTimePeriod, storages.get(name).getInputPower());
				totalPowerStorage += storages.get(name).getInputPower();
				totalEnergyStorage += input;
				currentStorageEnergy += storages.get(name).getCurrentEnergy();
			}
			totalPower.get(STORAGE).add(regularTimePeriod, totalPowerStorage);
			
			// 计算从电网输入的能量，并计算对应的电费
			double totalEnergyPowerSystem;
			double totalPowerPowerSystem;
			if(powerSystem.getCondition())
			{
				totalEnergyPowerSystem = totalEnergyStorage + totalEnergyUser - totalEnergyGenerator;
				totalPowerPowerSystem = totalPowerStorage + totalPowerUser - totalPowerGenerator;
				powerSystemVoltage.get("PowerSystem").add(regularTimePeriod, 220);
				powerSystemPower.get("PowerSystem").add(regularTimePeriod, totalPowerPowerSystem);
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
				
				// 向dataHandler发送当前的资金信息
				if(this.dataHandler != null)
				{
					ArrayList<Double> prices = new ArrayList<Double>();
					prices.add(accumulatedIncome);
					prices.add(accumulatedCost);
					prices.add(accumulatedOutputEnergy/3600);
					prices.add(accumulatedInputEnergy/3600);
					dataHandler.OnDataChanged(prices);
				}
				totalPower.get(POWERSYSTEM).add(regularTimePeriod, totalPowerPowerSystem);
			}
			else
			{
				totalEnergyPowerSystem = 0;
				totalPowerPowerSystem = 0;
				powerSystemVoltage.get("PowerSystem").add(regularTimePeriod, 0);
				powerSystemPower.get("PowerSystem").add(regularTimePeriod, totalPowerPowerSystem);
				totalPower.get(POWERSYSTEM).add(regularTimePeriod, totalPowerPowerSystem);
			}
			
			// 统计电价信息
			buyingPrice.add(regularTimePeriod, powerSystem.getBuyingPrice());
			sellingPrice.add(regularTimePeriod, powerSystem.getSellingPrice());
			
			if(pointCount < maxPointCount)
			{
				avgBuyingPrice = (avgBuyingPrice * pointCount + powerSystem.getBuyingPrice())/(pointCount + 1);
				avgSellingPrice = (avgSellingPrice * pointCount + powerSystem.getSellingPrice())/(pointCount + 1);
				buyingPriceList.addFirst(powerSystem.getBuyingPrice());
				sellingPriceList.addFirst(powerSystem.getSellingPrice());
				pointCount++;
			}
			else
			{
				avgBuyingPrice += (powerSystem.getBuyingPrice() - buyingPriceList.removeLast())/maxPointCount;
				buyingPriceList.addFirst(powerSystem.getBuyingPrice());
				avgSellingPrice += (powerSystem.getSellingPrice() - sellingPriceList.removeLast())/maxPointCount;
				sellingPriceList.addFirst(powerSystem.getSellingPrice());
			}
			
			// 检查停电预警时间是否已经过去
			if(this.outageExpected)
			{
				if(this.end.before(new Date(timestamp)))
					this.outageExpected = false;
			}
			
			// 分配下一时间段的储能装置策略
			// 需要检查是否停电，以及是否有停电预警
			// 如果没有停电
			if(powerSystem.getCondition())
			{
				// 设置没有处于停电状态
				for(String name:users.keySet())
				{
					users.get(name).setBlackedOut(false);
				}
				
				// 如果没有停电预警，则正常进行储能
				if(!this.outageExpected)
				{
					// 价格低于平均
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
				}
				// 如果距离停电发生还有六小时以上，那么不允许对储能装置的电能进行销售
				else if(this.begin.getTime() - timestamp > 6*60*60*1000)
				{
					// 价格低于平均
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
						// 只允许使用储能装置供电，禁止卖电
						setStorageInputPower(-totalPowerUser + totalPowerGenerator);
					}
				}
				// 如果有停电预警，那么在停电开始前六小时内，需要对储能装置进行充能
				// 确保在停电开始之前，储能装置处于满电量状态
				else
				{
					setStorageInputPower(maxStorageInputPower);
				}
			}
			// 发生停电问题
			else
			{
				// 发生停电问题，则所有能量由储能装置提供
				setStorageInputPower(-totalPowerUser + totalPowerGenerator);
				
				// 如果储能值低于30%则暂停工厂的用电
				// 如果储能值低于30%则暂停办公室供电
				// 如果储能值低于5%则暂停家庭供电
				for(String name:users.keySet())
				{
					if(users.get(name).getType() == User.FACTORY)
					{
						if(maxStorageEnergy*0.3 > currentStorageEnergy)
							users.get(name).setBlackedOut(true);
						else if(maxStorageEnergy*0.95 < currentStorageEnergy)
							users.get(name).setBlackedOut(false);
					}
					else if(users.get(name).getType() == User.OFFICE)
					{
						if(maxStorageEnergy*0.3 > currentStorageEnergy)
							users.get(name).setBlackedOut(true);
						else if(maxStorageEnergy*0.95 < currentStorageEnergy)
							users.get(name).setBlackedOut(false);
					}
					else if(users.get(name).getType() == User.FAMILY)
					{
						if(maxStorageEnergy*0.05 > currentStorageEnergy)
							users.get(name).setBlackedOut(true);
						else  if(maxStorageEnergy*0.3 < currentStorageEnergy)
							users.get(name).setBlackedOut(false);
					}
				}
			}
			
			// 预测发电情况
			if(prediction.size() == 0)
			{
				prediction.put("WindTurbine", new TimeSeries("WindTurbine"));
				prediction.put("SolarPanel", new TimeSeries("SolarPanel"));
				long time0 = timestamp % (1000*60*60);
				for(int i=1; i<48; i++)
				{
					long time = time0 + i*1000*60*60;
					double wind = this.predictor.getWindPowerPrediction(time);
					double solar = this.predictor.getSolarPanelPrediction(time);
					prediction.get("WindTurbine").add(new FixedMillisecond(time), wind);
					prediction.get("SolarPanel").add(new FixedMillisecond(time), solar);
					
					System.out.printf("PREDICTION:%f %f\n", wind, solar);
				}
				nextPredictingTime = time0 + 1000*60*60;
			}
			else if(nextPredictingTime <= timestamp)
			{
				long time = nextPredictingTime + 48*1000*60*60;
				double wind = this.predictor.getWindPowerPrediction(time);
				double solar = this.predictor.getSolarPanelPrediction(time);
				prediction.get("WindTurbine").add(new FixedMillisecond(time), wind);
				prediction.get("SolarPanel").add(new FixedMillisecond(time), solar);
				
				System.out.printf("PREDICTION:%f %f\n", wind, solar);
				nextPredictingTime += 1000*60*60;
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
	
	// 天气预报与发电预测
	private WeatherForecast weatherForecast = null;
	private Predictor predictor = new Predictor();
	
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
	private final int maxPointCount = 1000;
	
	// 统计储能设备的最大功率
	private double maxStorageInputPower = 0;
	private double maxStorageOutputPower = 0;
	private double maxStorageEnergy = 0;
	private double currentStorageEnergy = 0;
	
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
	
	// 发电数据的预测值
	private HashMap<String, TimeSeries> prediction = new HashMap<String, TimeSeries>();
	private long nextPredictingTime = 0;
	
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
	
	// 停电预警使用的变量
	private boolean outageExpected = false;
	private Date begin = null;
	private Date end = null;

}
