package com.micropowersystem.management;

import java.io.IOException;
import java.util.Vector;

public class MainSimulation {
	public static void main(String[] args) throws InterruptedException, IOException
	{
	
		Weather w0 = new Weather();
		WeatherForecast wf0 = new WeatherForecast();
		wf0.setWeather(w0);
		
		Generator g0 = new SolarPanel("solar_panel_1.properties");
		Generator g1 = new WindTurbine("wind_turbine_1.properties");
		Generator g2 = new WindTurbine("wind_turbine_2.properties");
		
		g0.setWeatherCondition(w0);
		g1.setWeatherCondition(w0);
		g2.setWeatherCondition(w0);
		
		Vector<User> uVec = new Vector<User>();
		for (int i=0;i<25;i++) {
			User u = new User();
			u.setAveragePower(5);
			u.setType(User.FAMILY);
			uVec.add(u);
		}
		User u = new User();
		u.setType(User.FACTORY);
		u.setAveragePower(500);
		uVec.add(u);
		
		u = new User();
		u.setType(User.OFFICE);
		u.setAveragePower(200);
		uVec.add(u);
		
		u = new User();
		u.setType(User.OFFICE);
		u.setAveragePower(300);
		uVec.add(u);
		
		u = new User();
		u.setType(User.OFFICE);
		u.setAveragePower(400);
		uVec.add(u);
		
		PowerSystem ps = new PowerSystem();
		ps.setCondition(true);
		
		Storage s0 = new Storage("storage_1.properties");
		Storage s1 = new Storage("storage_2.properties");
		
		Manager m = new Manager();
		m.setWeather(w0);
		
		m.addGenerator(g0);
		m.addGenerator(g1);
		m.addGenerator(g2);
		
		m.addStorage(s0);
		m.addStorage(s1);
		
		for(User tempUser : uVec) {
			m.addUser(tempUser);
		}
		
		m.setPowerSystem(ps);
		
		UIframe frame = new UIframe("微网管理系统");
		
		m.setDataHandler(frame);
		
		frame.setManager(m);
		
		w0.start();
		ps.start();
		((Thread)g0).start();
		((Thread)g1).start();
		((Thread)g2).start();
		for(User user:uVec)
		{
			user.start();
		}
		s0.start();
		s1.start();

		frame.startFrame();
		m.start();
		
		
		
	}
}
