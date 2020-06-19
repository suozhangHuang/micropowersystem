package com.micropowersystem.management;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;



public class UIframe extends JFrame implements DataHandler{
	
	//UIcomponents
	private JTabbedPane jTabbedPane;
	private JPanel[] jPanels;
	private JMenuBar menuBar;
	private JMenu helpMenu;
	private JMenu moreMenu;
	private JMenu stopMenu;
	private JMenuItem helpMenuItem;
	private JMenuItem emailMenuItem;
	private JMenuItem connectMenuItem;
	private JMenuItem stopOrStartNow;
	private JMenuItem stopPlan;
	
	
	private JDialog jDialog;
	private JDialog jHelpDialog;
	private JDialog jEmailDialog;
	private JDialog stopPlanDialog;
	
	//jPanel[0]
	private JPanel infoPanelP0;
	private JLabel[] textLabelP0;
	private JLabel[] infoLabelP0;
	private JPanel mainPanelP0;
	private JScrollPane scrollPane0P0;
	private JScrollPane scrollPane1P0;
	private JScrollPane scrollPane2P0;
	private JScrollPane scrollPane3P0;
	private JScrollPane scrollPane4P0;
	private JScrollPane scrollPane5P0;
	private JList list0P0;
	private JList list1P0;
	private JList list2P0;
	private JTextArea text0P0;
	private JTextArea text1P0;
	private JTextArea text2P0;
	
	//jPanel[1]
	private JTextArea journalTextAreaP1;
	private JScrollPane scrollPane0P1; 
	private JFreeChart chartP1;
	private ChartPanel chartPanelP1;
	
	//jPanel[2]
	private JFreeChart chartP2;
	private ChartPanel chartPanelP2;
	private Vector<TimeSeries> tsVecP2 = new Vector<TimeSeries>();
	
	//jPanel[3]
	private JPanel upPanel; 
	private JScrollPane scrollPane0P3;
	private JScrollPane scrollPane1P3;
	
	private Box boxP3;
	private JList list0P3;
	private JList list1P3;
	
	private JPanel upMiddlePanel;
	private JButton addButP3;
	private JButton removeButP3;
	private JButton showButP3;
	
	
	private JPanel downPanel;
	
	private JFreeChart chartPowerP3;
	private ChartPanel chartPowerPanelP3;
	private JFreeChart chartVoltageP3;
	private ChartPanel chartVoltagePanelP3;
	
	private Vector<String> vecForList1P3 = new Vector<String>();
	private String selectedStr0P3 = null;
	private String selectedStr1P3 = null;
	private Vector<TimeSeries> tsVec0P3= new Vector<TimeSeries>();
	private Vector<TimeSeries> tsVec1P3= new Vector<TimeSeries>();
	
	//jPanel[4]
	private Box boxP4;
	private JList list0P4;
	private JList list1P4;
	private JScrollPane scrollPane0P4;
	private JScrollPane scrollPane1P4;
	private JButton addButP4;
	private JButton removeButP4;
	private JButton showButP4;
	
	private JFreeChart chart0P4;
	private ChartPanel chartPanel0P4;
	private JFreeChart chart1P4;
	private ChartPanel chartPanel1P4;
	
	private JLabel[] textLabelP4;
	private JLabel[] infoLabelP4;
	
	private Vector<String> vecForList1P4 = new Vector<String>();
	private String selectedStr0P4 = null;
	private String selectedStr1P4 = null;
	private Vector<TimeSeries> tsVec0P4 = new Vector<TimeSeries>();
	private Vector<TimeSeries> tsVec1P4 = new Vector<TimeSeries>();
	
	private JPanel upPanelP4;
	private JPanel centerPanelP4;
	private JPanel centerNorthPanelP4;
	private JPanel centerCenterPanelP4;
	private JPanel centerNorthMiddleP4;
	
	//jPanel[5]
	private JFreeChart chartP5;
	private ChartPanel chartPanelP5;
	private Vector<TimeSeries> tsVecP5 = new Vector<TimeSeries>();
	
	//Data
	private Vector<HashMap<String,TimeSeries>> powerTS = new  Vector<HashMap<String,TimeSeries>>();
	private Vector<HashMap<String,TimeSeries>> voltageTS = new  Vector<HashMap<String,TimeSeries>>();
	private TimeSeries sellingPriceTS = null;
	private TimeSeries buyingPriceTS = null;
	private Vector<TimeSeries> TotalPowerTS = new Vector<TimeSeries>();
	private Vector<Vector<String>> namesVec = new Vector<Vector<String>>();
	private Vector<HashMap<String,String>> infoVec = new Vector<HashMap<String,String>>();
	private HashMap<String,TimeSeries> forecastPowerTS = null;
	private HashMap<String,TimeSeries> storageEnergyTS = null;
	private String planStopBeginTime = null;
	private String planStopEndTime = null;
	
	//flags
	private boolean managementStatus = false;
	private boolean dataConnected = false;
	
	//Management
	private Management management = null;
	
	
	public UIframe() {
		super("微电网管理系统");
	}
	
	public UIframe(String UIName) {
		super(UIName);
	}
	
	public void startFrame() {
		setupComponents();
		setupActions();
	}
	
	public void setManager(Management management) {
		this.management = management;
	}
	
	private void setupComponents() {
		
		//初始化Bar区域
		menuBar = new JMenuBar();
		
		helpMenu = new JMenu("帮助");
		moreMenu = new JMenu("更多");
		stopMenu = new JMenu("模拟孤岛运行");
		
		helpMenuItem = new JMenuItem("说明");
		emailMenuItem = new JMenuItem("邮件提醒");
		connectMenuItem = new JMenuItem("连接设备");
		stopOrStartNow = new JMenuItem("立即切断电网连接");
		stopPlan = new JMenuItem("设置切断计划");
		
		emailMenuItem.setEnabled(false);
		
		helpMenu.add(helpMenuItem);
		moreMenu.add(connectMenuItem);
		moreMenu.add(emailMenuItem);
		stopMenu.add(stopOrStartNow);
		stopMenu.add(stopPlan);
		
		menuBar.add(moreMenu);
		menuBar.add(helpMenu);
		menuBar.add(stopMenu);
		
		
		
		
		//初始化jTabbedPane
		jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		jPanels = new JPanel[6];
		jPanels[0] = new JPanel();
		jPanels[1] = new JPanel();
		jPanels[2] = new JPanel();
		jPanels[3] = new JPanel();
		jPanels[4] = new JPanel();
		jPanels[5] = new JPanel();
		jTabbedPane.add("设备信息",jPanels[0]);
		jTabbedPane.add("运行状况",jPanels[1]);
		jTabbedPane.add("发电预测",jPanels[2]);
		jTabbedPane.add("发电状况",jPanels[3]);
		jTabbedPane.add("用户状况",jPanels[4]);
		jTabbedPane.add("储能状况",jPanels[5]);
		
		
		//分别初始化各个TabbedPanel
		//Panels[0]
		infoPanelP0 = new JPanel();
		infoPanelP0.setLayout(new GridLayout(1,0));
		textLabelP0 = new JLabel[3];
		infoLabelP0 = new JLabel[3];
		for(int i = 0;i<3;i++) {
			textLabelP0[i] = new JLabel();
			infoLabelP0[i] = new JLabel();
		}
		textLabelP0[0].setText("发电设备数量：");
		textLabelP0[1].setText("储能设备数量：");
		textLabelP0[2].setText("用户数量：");
		for(int i = 0;i<3;i++) {
			infoPanelP0.add(textLabelP0[i]);
			infoPanelP0.add(infoLabelP0[i]);
		}
		
		mainPanelP0 = new JPanel();
		mainPanelP0.setLayout(new GridLayout(2,3));
		
		scrollPane0P0 = new JScrollPane();
		scrollPane1P0 = new JScrollPane();
		scrollPane2P0 = new JScrollPane();
		
		scrollPane0P0.setPreferredSize(new Dimension(200,100));
        scrollPane1P0.setPreferredSize(new Dimension(200,100));
        scrollPane2P0.setPreferredSize(new Dimension(200,100));
        
		list0P0 = new JList();
		list1P0 = new JList();
		list2P0 = new JList();
		
		scrollPane0P0.setViewportView(list0P0);
		scrollPane1P0.setViewportView(list1P0);
		scrollPane2P0.setViewportView(list2P0);
		
		text0P0 = new JTextArea(10,10);
		text1P0 = new JTextArea(10,10);
		text2P0 = new JTextArea(10,10);
		
		text0P0.setText("选择设备以获取详细信息");
		text1P0.setText("选择设备以获取详细信息");
		text2P0.setText("选择设备以获取详细信息");
		
		text0P0.setLineWrap(true);
		text1P0.setLineWrap(true);
		text2P0.setLineWrap(true);
		
		scrollPane3P0 = new JScrollPane(text0P0);
		scrollPane4P0 = new JScrollPane(text1P0);
		scrollPane5P0 = new JScrollPane(text2P0);
		
		mainPanelP0.add(scrollPane0P0);
		mainPanelP0.add(scrollPane1P0);
		mainPanelP0.add(scrollPane2P0);
		mainPanelP0.add(scrollPane3P0);
		mainPanelP0.add(scrollPane4P0);
		mainPanelP0.add(scrollPane5P0);
		
		
		jPanels[0].add(infoPanelP0,BorderLayout.NORTH);
		jPanels[0].add(mainPanelP0,BorderLayout.CENTER);
		//jPanels[0] end
		
		
		//jPanels[1]
		journalTextAreaP1 = new JTextArea(5,30);
		journalTextAreaP1.setLineWrap(true);
		scrollPane0P1 = new JScrollPane(journalTextAreaP1);
		chartP1 = ChartFactory.createTimeSeriesChart("Total Power of Different Sides", "Time","Power/KW", null, true, false, false);
		chartPanelP1 = new ChartPanel(chartP1);
		jPanels[1].setLayout(new BorderLayout());
		jPanels[1].add(scrollPane0P1,BorderLayout.NORTH);
		jPanels[1].add(chartPanelP1,BorderLayout.CENTER);
		//jPanels[1] end
		
		//jPanels[2]
		chartP2 = ChartFactory.createTimeSeriesChart( "Power Prediction Based on Weather Forecast", "Time","Power/KW", null, true, false, false);
		chartPanelP2 = new ChartPanel(chartP2);
		jPanels[2].setLayout(new BorderLayout());
		jPanels[2].add(chartPanelP2,BorderLayout.CENTER);
		//jPanels[2] end
		
		//jPanels[3]
	    jPanels[3].setLayout(new BorderLayout());
		
	    upPanel = new JPanel(); 
	    upPanel.setLayout(new GridLayout(1,3));
	    
	    upMiddlePanel = new JPanel();
	    
		scrollPane0P3 = new JScrollPane();
		scrollPane1P3 = new JScrollPane();
		
		scrollPane0P3.setPreferredSize(new Dimension(200,50));
		scrollPane1P3.setPreferredSize(new Dimension(200,50));
		
		list0P3 = new JList();
		list1P3 = new JList();
		
		scrollPane0P3.setViewportView(list0P3);
		scrollPane1P3.setViewportView(list1P3);
		
		addButP3 = new JButton("添加");
		removeButP3 = new JButton("删除");
		showButP3 = new JButton("展示");
		
		//不知道怎么实现按钮大小一致
		
		
		addButP3.setEnabled(false);
		removeButP3.setEnabled(false);
		
		boxP3 = Box.createVerticalBox();
		upMiddlePanel.add(boxP3);
		boxP3.add(Box.createVerticalStrut(10));    //添加高度为200的垂直框架
		
	   
	    boxP3.add(addButP3);
	    boxP3.add(removeButP3);
	    boxP3.add(showButP3);
		
	    upPanel.add(scrollPane0P3);
	    upPanel.add(upMiddlePanel);
	    upPanel.add(scrollPane1P3);
	    
	    
		downPanel = new JPanel();
		downPanel.setLayout(new GridLayout(1,2));
		chartPowerP3 = ChartFactory.createTimeSeriesChart( "Generators' Power", "Time","Power/KW", null, true, false, false);
		chartPowerPanelP3 = new ChartPanel(chartPowerP3);
		chartVoltageP3 = ChartFactory.createTimeSeriesChart( "Generators' Voltage", "Time","Voltage/V", null, true, false, false);
		chartVoltagePanelP3 = new ChartPanel(chartVoltageP3);
		downPanel.add(chartPowerPanelP3);
		downPanel.add(chartVoltagePanelP3);
		
		jPanels[3].add(upPanel,BorderLayout.NORTH);
		jPanels[3].add(downPanel,BorderLayout.CENTER);
		
		//jPanels[3] end
		
		//jPanels[4]
		
		jPanels[4].setLayout(new BorderLayout());
		upPanelP4 = new JPanel();
		centerPanelP4 = new JPanel();
		centerNorthPanelP4 = new JPanel();
		centerCenterPanelP4 = new JPanel();
		centerNorthMiddleP4 = new JPanel();
		scrollPane0P4 = new JScrollPane();
		scrollPane1P4 = new JScrollPane();
		
		upPanelP4.setLayout(new GridLayout(2,4));
		centerPanelP4.setLayout(new BorderLayout());
		centerNorthPanelP4.setLayout(new GridLayout(1,3));
		centerCenterPanelP4.setLayout(new GridLayout(1,2));
		
		list0P4 = new JList();
		list1P4 = new JList();
		
		scrollPane0P4.setViewportView(list0P4);
		scrollPane1P4.setViewportView(list1P4);
		
		addButP4 = new JButton("添加");
		removeButP4 = new JButton("删除");
		showButP4 = new JButton("展示");
		
		addButP4.setEnabled(false);
		removeButP4.setEnabled(false);
		
		boxP4 = Box.createVerticalBox();
		boxP4.add(Box.createVerticalStrut(10));
		boxP4.add(addButP4);
		boxP4.add(removeButP4);
		boxP4.add(showButP4);
		centerNorthMiddleP4.add(boxP4);
		
		centerNorthPanelP4.add(scrollPane0P4);
		centerNorthPanelP4.add(centerNorthMiddleP4);
		centerNorthPanelP4.add(scrollPane1P4);
		
		chart0P4 = ChartFactory.createTimeSeriesChart( "User Consuming Power", "Time","Power/KW", null, true, false, false);
		chartPanel0P4 = new ChartPanel(chart0P4);
		chart1P4 = ChartFactory.createTimeSeriesChart( "Electricity Prices", "Time","Money/Yuan", null, true, false, false);
		chartPanel1P4 = new ChartPanel(chart1P4);
		centerCenterPanelP4.add(chartPanel0P4);
		centerCenterPanelP4.add(chartPanel1P4);
		
		centerPanelP4.add(centerNorthPanelP4,BorderLayout.NORTH);
		centerPanelP4.add(centerCenterPanelP4,BorderLayout.CENTER);
		
		textLabelP4 = new JLabel[4];
		infoLabelP4 = new JLabel[4];
		for(int i = 0;i<4;i++) {
			textLabelP4[i] = new JLabel();
			infoLabelP4[i] = new JLabel();
		}
		textLabelP4[0].setText("已售电价：");
		textLabelP4[1].setText("已售电量：");
		textLabelP4[2].setText("已购电价：");
		textLabelP4[3].setText("已购电量：");
		for(int i=0;i<4;i++) {
			upPanelP4.add(textLabelP4[i]);
			upPanelP4.add(infoLabelP4[i]);
		}
		
		jPanels[4].add(upPanelP4,BorderLayout.NORTH);
		jPanels[4].add(centerPanelP4,BorderLayout.CENTER);
		
		
		
		//jPanels[4] end
		
		
		//jPanels[5]
		chartP5 = ChartFactory.createTimeSeriesChart( "Storage's Current Energy", "Time","Energy/KJ", null, true, false, false);
		chartPanelP5 = new ChartPanel(chartP5);
		jPanels[5].setLayout(new BorderLayout());
		jPanels[5].add(chartPanelP5,BorderLayout.CENTER);
		//jPanels[5] end
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(menuBar,BorderLayout.NORTH);
		container.add(jTabbedPane,BorderLayout.CENTER);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(620, 600);
		setVisible(true);
	}
	private void setupActions() {
		
		connectMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				managementStatus = management.getManagerStatus();
				String tempText = null;
				if(managementStatus == true) {
					tempText = "已经与设备连接成功！";
					//连接成功后，即可以开始进行数据的关联
					if(!dataConnected) {
						//进行数据连接
						for (int i = 0;i<4;i++) {
							powerTS.add(management.getPowerTimeSeries(i));
							voltageTS.add(management.getVoltageTimeSeries(i));
							TotalPowerTS.add(management.getTotalPowerTimeSeries(i));
						}
						emailMenuItem.setEnabled(true);
						namesVec.add(management.getNames(Management.GENERATOR));
						namesVec.add(management.getNames(Management.STORAGE));
						namesVec.add(management.getNames(Management.USER));
						
						infoVec.add(management.GetInfo(Management.GENERATOR));
						infoVec.add(management.GetInfo(Management.STORAGE));
						infoVec.add(management.GetInfo(Management.USER));
						
						storageEnergyTS = management.getStorageEnergy();
						
						sellingPriceTS = management.getPrices(Management.USER);
						buyingPriceTS = management.getPrices(Management.POWERSYSTEM);
						forecastPowerTS = management.getForecastPower();
						//设置UI有关数据
						list0P0.setListData(namesVec.elementAt(0));
						list1P0.setListData(namesVec.elementAt(1));
						list2P0.setListData(namesVec.elementAt(2));
						infoLabelP0[0].setText(String.valueOf(namesVec.elementAt(0).size()));
						infoLabelP0[1].setText(String.valueOf(namesVec.elementAt(1).size()));
						infoLabelP0[2].setText(String.valueOf(namesVec.elementAt(2).size()));
						//设置与图片有关的数据关联
						//P1
						DataProcessorTool.setChart(chartP1,TotalPowerTS , "Total Power of Different Sides", "Power/KW");
						//P2
						convertFromMapToVec(forecastPowerTS,tsVecP2);
						DataProcessorTool.setChart(chartP2,tsVecP2 , "Power Prediction Based on Weather Forecast", "Power/KW");
						//P3
						list0P3.setListData(namesVec.elementAt(Management.GENERATOR));
						//P4
						list0P4.setListData(namesVec.elementAt(Management.USER));
						tsVec1P4.add(sellingPriceTS);
						tsVec1P4.add(buyingPriceTS);
						DataProcessorTool.setChart(chart1P4,tsVec1P4 , "Electricity Prices", "Money/Yuan");
						//P5
						convertFromMapToVec(storageEnergyTS,tsVecP5);
						DataProcessorTool.setChart(chartP5,tsVecP5 , "Storage's Current Energy", "Energy/KJ");
						
						dataConnected = true;
					}
				}else {
					tempText = "未连接成功，请稍后再试！";
				}
				jDialog = new JDialog();
				jDialog.setSize(200,150);
				jDialog.setLocation(400, 300);
				jDialog.setLayout(new BorderLayout());
				JLabel tempLabel = new JLabel();
				tempLabel.setText(tempText);
				jDialog.add(tempLabel,BorderLayout.CENTER);
				
				jDialog.setVisible(true);
			}
		});
		
		emailMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				jEmailDialog = new JDialog();
				JPanel tempUpPanel = new JPanel();
				JPanel tempDownPanel = new JPanel();
				tempUpPanel.setLayout(new GridLayout(1,2));
				JLabel tempLabel0 = new JLabel();
				tempLabel0.setText("请输入您想要发送的邮箱地址：");
				JLabel tempLabel1 = new JLabel();
				JButton tempBut = new JButton("SEND");
				Box tempBox = Box.createVerticalBox();
				JTextField tempTF = new JTextField();
				tempTF.setEditable(true);
				tempBox.createVerticalStrut(50);
				tempBox.createHorizontalStrut(100);
				tempBox.add(tempBut);
				tempBox.add(tempLabel1);
				tempUpPanel.add(tempLabel0);
				tempUpPanel.add(tempTF);
				tempBut.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						String tempStr = tempTF.getText();
						boolean flag = false;
						if(tempStr!=null) {
							flag = management.sendEmail(tempStr);
							if(flag) {
								tempLabel1.setText("发送成功");
							}else {
								tempLabel1.setText("发送失败");
							}
						}else {
							tempLabel1.setText("请输入邮箱地址再发送?");
						}
					}
					
				});
				tempDownPanel.add(tempBox);
				jEmailDialog.setSize(400,100);
				jEmailDialog.setLocation(400, 300);
				jEmailDialog.setLayout(new BorderLayout());
				jEmailDialog.add(tempUpPanel,BorderLayout.NORTH);
				jEmailDialog.add(tempDownPanel,BorderLayout.CENTER);
				jEmailDialog.setVisible(true);
			}
			
		});
		
		stopOrStartNow.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(stopOrStartNow.getText().equals("立即切断电网连接")) {
					management.controlPowerSystem(false);
					stopOrStartNow.setText("立即接入电网");
				}else {
					management.controlPowerSystem(true);
					stopOrStartNow.setText("立即切断电网连接");
				}
			}
			
		});
		
		stopPlan.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stopPlanDialog = new JDialog();
				JLabel tempLabel0 = new JLabel();
				JLabel tempLabel1 = new JLabel();
				JLabel tempLabel2 = new JLabel();
				JTextField textFiled0 = new JTextField();
				JTextField textFiled1 = new JTextField();
				JButton applyBut = new JButton("Apply");
				textFiled0.addKeyListener(new KeyListener() {

					public void keyPressed(KeyEvent arg0) {
						tempLabel2.setText("时间输入格式为：yyyy.MM.dd HH:mm:ss");
					}
					public void keyReleased(KeyEvent arg0) {
					}
					public void keyTyped(KeyEvent arg0) {
					}
					
				});
				textFiled1.addKeyListener(new KeyListener() {

					public void keyPressed(KeyEvent arg0) {
						tempLabel2.setText("时间输入格式为：yyyy.MM.dd HH:mm:ss");
					}
					public void keyReleased(KeyEvent arg0) {
					}
					public void keyTyped(KeyEvent arg0) {
					}
					
				});
				applyBut.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						planStopBeginTime = textFiled0.getText();
						planStopEndTime = textFiled1.getText();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
						try {
							if(management.outageWarning(sdf.parse(planStopBeginTime), sdf.parse(planStopEndTime))) {
								tempLabel2.setText("设置成功，请退出");
							}
						} catch (ParseException e1) {
							e1.printStackTrace();
							tempLabel2.setText("输入的时间或者格式无效");
						}
					}
					
				});
				
				tempLabel0.setText("请输入你要想要切断电网的时间");
				tempLabel1.setText("请输入计划重新接入电网的时间");
				tempLabel2.setText("时间输入格式为：yyyy.MM.dd HH:mm:ss");
				stopPlanDialog.setLayout(new GridLayout(0,1));
				stopPlanDialog.add(tempLabel0);
				stopPlanDialog.add(textFiled0);
				stopPlanDialog.add(tempLabel1);
				stopPlanDialog.add(textFiled1);
				stopPlanDialog.add(tempLabel2);
				stopPlanDialog.add(applyBut);
				
				stopPlanDialog.setSize(400,150);
				stopPlanDialog.setLocation(400, 300);
				stopPlanDialog.setVisible(true);
			}
			
		});
		
		list0P0.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list0P0.getValueIsAdjusting()){    //设置只有释放鼠标时才触发
					String tempStr = (String)list0P0.getSelectedValue();
					String tempValue = infoVec.elementAt(0).get(tempStr);
					text0P0.setText(tempValue);
                }
			}
			
		});
		
		list1P0.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list1P0.getValueIsAdjusting()){    //设置只有释放鼠标时才触发
					String tempStr = (String)list1P0.getSelectedValue();
					String tempValue = infoVec.elementAt(1).get(tempStr);
					text1P0.setText(tempValue);
                }
			}
			
		});
		
		list2P0.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list2P0.getValueIsAdjusting()){    //设置只有释放鼠标时才触发
					String tempStr = (String)list2P0.getSelectedValue();
					String tempValue = infoVec.elementAt(2).get(tempStr);
					text2P0.setText(tempValue);
                }
			}
			
		});
		
		list0P3.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list0P3.getValueIsAdjusting()){    //设置只有释放鼠标时才触发
					addButP3.setEnabled(true);
					selectedStr0P3 = (String)list0P3.getSelectedValue();
                }
			}
			
		});
		
		list1P3.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list1P3.getValueIsAdjusting()){    //设置只有释放鼠标时才触发
					removeButP3.setEnabled(true);
					selectedStr1P3 = (String)list1P3.getSelectedValue();
                }
			}
			
		});
		
		addButP3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(!vecForList1P3.contains(selectedStr0P3)) {
					vecForList1P3.add(selectedStr0P3);
					list1P3.setListData(vecForList1P3);
				}
			}
			
		});
		
		removeButP3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				vecForList1P3.remove(selectedStr1P3);
				list1P3.setListData(vecForList1P3);
			}
			
		});
		
		showButP3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				tsVec0P3.removeAllElements();
				tsVec1P3.removeAllElements();
				for(int i = 0;i<vecForList1P3.size();i++) {
					tsVec0P3.add( powerTS.elementAt(Management.GENERATOR).get(vecForList1P3.elementAt(i) ) );
					tsVec1P3.add( voltageTS.elementAt(Management.GENERATOR).get(vecForList1P3.elementAt(i) ) );
				}
				DataProcessorTool.setChart(chartPowerP3,tsVec0P3 , "Generators' Power", "Power/KW");
				DataProcessorTool.setChart(chartVoltageP3,tsVec1P3 , "Generators' Voltage", "Voltage/V");
			}
			
		});
		
		list0P4.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list0P4.getValueIsAdjusting()){    //设置只有释放鼠标时才触发
					addButP4.setEnabled(true);
					selectedStr0P4 = (String)list0P4.getSelectedValue();
                }
			}
			
		});
		
		list1P4.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list1P4.getValueIsAdjusting()){    //设置只有释放鼠标时才触发
					removeButP4.setEnabled(true);
					selectedStr1P4 = (String)list1P4.getSelectedValue();
                }
			}
			
		});
		
		addButP4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(!vecForList1P4.contains(selectedStr0P4)) {
					vecForList1P4.add(selectedStr0P4);
					list1P4.setListData(vecForList1P4);
				}
			}
			
		});
		
		removeButP4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				vecForList1P4.remove(selectedStr1P4);
				list1P4.setListData(vecForList1P4);
			}
			
		});
		
		showButP4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				tsVec0P4.removeAllElements();
				for(int i = 0;i<vecForList1P4.size();i++) {
					tsVec0P4.add( powerTS.elementAt(Management.USER).get(vecForList1P4.elementAt(i) ) );
				}
				DataProcessorTool.setChart(chart0P4,tsVec0P4 , "Users' Power", "Power/KW");
			}
			
		});
		
		
		
		
		helpMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jHelpDialog = new JDialog();
				jHelpDialog.setSize(400,300);
				jHelpDialog.setLocation(400, 300);
				jHelpDialog.setLayout(new BorderLayout());
				JLabel tempLabel = new JLabel();
				tempLabel.setText("版权所有:许涵，黄俊力"+"      版本0.0");
				jHelpDialog.add(tempLabel,BorderLayout.CENTER);
				jHelpDialog.setVisible(true);
			}
			
		});
		
		
	}
	
	
	
	
	private void convertFromMapToVec(HashMap<String,TimeSeries> map , Vector<TimeSeries> vec) {
		for (Map.Entry<String, TimeSeries> entry : map.entrySet()) {
			vec.add( entry.getValue() );
        }
	}
	
	public void OnDataChanged(ArrayList<Double> Prices) {
		
		infoLabelP4[0].setText(String.format("%.1f",Prices.get(0))+"元");
		infoLabelP4[1].setText(String.format("%.1f",Prices.get(2))+"kWh");
		infoLabelP4[2].setText(String.format("%.1f",Prices.get(1))+"元");
		infoLabelP4[3].setText(String.format("%.1f",Prices.get(3))+"kWh");
	}

	@Override
	public void updateMessage(String message) {
		
		journalTextAreaP1.append(message);
	}
	
}
