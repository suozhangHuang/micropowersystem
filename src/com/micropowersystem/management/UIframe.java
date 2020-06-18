package com.micropowersystem.management;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.Box;
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
	private JMenuItem helpMenuItem;
	private JMenuItem emailMenuItem;
	private JMenuItem connectMenuItem;
	
	private JDialog jDialog;
	private JDialog jHelpDialog;
	
	
	//jPanel[0]
	private JPanel infoPanelP0;
	private JLabel[] textLabelP0;
	private JLabel[] infoLabelP0;
	private JPanel mainPanelP0;
	private JScrollPane scrollPane0P0;
	private JScrollPane scrollPane1P0;
	private JScrollPane scrollPane2P0;
	private JList list0P0;
	private JList list1P0;
	private JList list2P0;
	private JTextField text0P0;
	private JTextField text1P0;
	private JTextField text2P0;
	
	//jPanel[1]
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
	private JFreeChart chart0P4;
	private ChartPanel chartPanel0P4;
	private JFreeChart chart1P4;
	private ChartPanel chartPanel1P4;
	private JLabel[] textLabelP4;
	private JLabel[] infoLabelP4;
	private Vector<TimeSeries> tsVec0P4 = new Vector<TimeSeries>();
	private Vector<TimeSeries> tsVec1P4 = new Vector<TimeSeries>();
	private JPanel upPanelP4;
	private JPanel centerPanelP4;
	
	//jPanel[5]
	private JFreeChart chartP5;
	private ChartPanel chartPanelP5;
	private Vector<TimeSeries> tsVecP5 = new Vector<TimeSeries>();
	
	//Data
	private Vector<HashMap<String,TimeSeries>> powerTS = null;
	private Vector<HashMap<String,TimeSeries>> voltageTS = null;
	private TimeSeries sellingPriceTS = null;
	private TimeSeries buyingPriceTS = null;
	private Vector<TimeSeries> TotalPowerTS = null;
	private Vector<Vector<String>> namesVec = null;
	private Vector<HashMap<String,String>> infoVec = null;
	private HashMap<String,TimeSeries> forecastPowerTS = null;
	private HashMap<String,TimeSeries> storageEnergyTS = null;
	
	
	//flags
	private boolean managementStatus = false;
	private boolean dataConnected = false;
	
	//Management
	private Management management = null;
	
	public static void main(String[] args) {
        new UIframe("΢��������ϵͳ");
        
    }
		public UIframe(String UIName) {
		super(UIName);
		setupComponents();
		setupActions();
	}
	
	private void setManager(Management management) {
		this.management = management;
	}
	
	private void setupComponents() {
		
		//��ʼ��Bar����
		menuBar = new JMenuBar();
		
		helpMenu = new JMenu("����");
		moreMenu = new JMenu("����");
		helpMenuItem = new JMenuItem("˵��");
		emailMenuItem = new JMenuItem("�ʼ�����");
		connectMenuItem = new JMenuItem("�����豸");
		
		helpMenu.add(helpMenuItem);
		moreMenu.add(connectMenuItem);
		moreMenu.add(emailMenuItem);
		
		menuBar.add(moreMenu);
		menuBar.add(helpMenu);
		
		//��ʼ��jTabbedPane
		jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		jPanels = new JPanel[6];
		jPanels[0] = new JPanel();
		jPanels[1] = new JPanel();
		jPanels[2] = new JPanel();
		jPanels[3] = new JPanel();
		jPanels[4] = new JPanel();
		jPanels[5] = new JPanel();
		jTabbedPane.add("�豸��Ϣ",jPanels[0]);
		jTabbedPane.add("����״��",jPanels[1]);
		jTabbedPane.add("����Ԥ��",jPanels[2]);
		jTabbedPane.add("����״��",jPanels[3]);
		jTabbedPane.add("�û�״��",jPanels[4]);
		jTabbedPane.add("����״��",jPanels[5]);
		
		
		//�ֱ��ʼ������TabbedPanel
		//Panels[0]
		infoPanelP0 = new JPanel();
		infoPanelP0.setLayout(new GridLayout(1,0));
		textLabelP0 = new JLabel[3];
		infoLabelP0 = new JLabel[3];
		for(int i = 0;i<3;i++) {
			textLabelP0[i] = new JLabel();
			infoLabelP0[i] = new JLabel();
		}
		textLabelP0[0].setText("�����豸������");
		textLabelP0[1].setText("�����豸������");
		textLabelP0[2].setText("�û�������");
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
		
		text0P0 = new JTextField();
		text1P0 = new JTextField();
		text2P0 = new JTextField();
		
		text0P0.setText("ѡ���豸�Ի�ȡ��ϸ��Ϣ");
		text1P0.setText("ѡ���豸�Ի�ȡ��ϸ��Ϣ");
		text2P0.setText("ѡ���豸�Ի�ȡ��ϸ��Ϣ");
		
		mainPanelP0.add(scrollPane0P0);
		mainPanelP0.add(scrollPane1P0);
		mainPanelP0.add(scrollPane2P0);
		mainPanelP0.add(text0P0);
		mainPanelP0.add(text1P0);
		mainPanelP0.add(text2P0);
		
		
		jPanels[0].add(infoPanelP0,BorderLayout.NORTH);
		jPanels[0].add(mainPanelP0,BorderLayout.CENTER);
		//jPanels[0] end
		
		
		//jPanels[1]
		chartP1 = ChartFactory.createTimeSeriesChart("Total Power of Different Sides", "Time","Power/KW", null, true, false, false);
		chartPanelP1 = new ChartPanel(chartP1);
		jPanels[1].setLayout(new BorderLayout());
		jPanels[1].add(chartPanelP1,BorderLayout.CENTER);
		//jPanels[1] end
		
		//jPanels[2]
		chartP2 = ChartFactory.createTimeSeriesChart( "Power Prediction Based on Weather Forecast", "Time","Power/KW", null, true, false, false);
		chartPanelP2 = new ChartPanel(chartP2);
		jPanels[2].setLayout(new BorderLayout());
		jPanels[2].add(chartPanelP2,BorderLayout.CENTER);
		//jPanels[2] end
		
		//jPanels[3]
	    jPanels[3].setLayout(new GridLayout(2,1));
		
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
		
		addButP3 = new JButton("Add");
		removeButP3 = new JButton("Remove");
		showButP3 = new JButton("Show");
		
		//��֪����ôʵ�ְ�ť��Сһ��
		
		
		addButP3.setEnabled(false);
		removeButP3.setEnabled(false);
		
		boxP3 = Box.createVerticalBox();
		upMiddlePanel.add(boxP3);
		boxP3.add(Box.createVerticalStrut(50));    //���Ӹ߶�Ϊ200�Ĵ�ֱ���
		
	   
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
		
		jPanels[3].add(upPanel);
		jPanels[3].add(downPanel);
		
		//jPanels[3] end
		
		//jPanels[4]
		
		jPanels[4].setLayout(new BorderLayout());
		upPanelP4 = new JPanel();
		centerPanelP4 = new JPanel();
		
		upPanelP4.setLayout(new GridLayout(2,4));
		centerPanelP4.setLayout(new GridLayout(2,1));
		
		chart0P4 = ChartFactory.createTimeSeriesChart( "User Consuming Power", "Time","Power/KW", null, true, false, false);
		chartPanel0P4 = new ChartPanel(chart0P4);
		chart1P4 = ChartFactory.createTimeSeriesChart( "Electricity Prices", "Time","Money/Yuan", null, true, false, false);
		chartPanel1P4 = new ChartPanel(chart1P4);
		centerPanelP4.add(chartPanel0P4);
		centerPanelP4.add(chartPanel1P4);
		textLabelP4 = new JLabel[4];
		infoLabelP4 = new JLabel[4];
		for(int i = 0;i<4;i++) {
			textLabelP4[i] = new JLabel();
			infoLabelP4[i] = new JLabel();
		}
		textLabelP4[0].setText("���۵�ۣ�");
		textLabelP4[1].setText("���۵�����");
		textLabelP4[2].setText("�ѹ���ۣ�");
		textLabelP4[3].setText("�ѹ�������");
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
					tempText = "�Ѿ����豸���ӳɹ���";
					//���ӳɹ��󣬼����Կ�ʼ�������ݵĹ���
					if(!dataConnected) {
						//������������
						for (int i = 0;i<4;i++) {
							powerTS.add(management.getPowerTimeSeries(i));
							voltageTS.add(management.getVoltageTimeSeries(i));
							TotalPowerTS.add(management.getTotalPowerTimeSeries(i));
						}
						
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
						//����UI�й�����
						list0P0.setListData(namesVec.elementAt(0));
						list1P0.setListData(namesVec.elementAt(1));
						list2P0.setListData(namesVec.elementAt(2));
						infoLabelP0[0].setText(String.valueOf(namesVec.elementAt(0).size()));
						infoLabelP0[1].setText(String.valueOf(namesVec.elementAt(1).size()));
						infoLabelP0[2].setText(String.valueOf(namesVec.elementAt(2).size()));
						//������ͼƬ�йص����ݹ���
						//P1
						DataProcessorTool.setChart(chartP1,TotalPowerTS , "Total Power of Different Sides", "Power/KW");
						//P2
						convertFromMapToVec(forecastPowerTS,tsVecP2);
						DataProcessorTool.setChart(chartP2,tsVecP2 , "Power Prediction Based on Weather Forecast", "Power/KW");
						//P3
						list0P3.setListData(namesVec.elementAt(Management.GENERATOR));
						//P4
						convertFromMapToVec(powerTS.elementAt(Management.USER),tsVec0P4);
						tsVec1P4.add(sellingPriceTS);
						tsVec1P4.add(buyingPriceTS);
						DataProcessorTool.setChart(chart0P4,tsVec0P4 , "User Consuming Power", "Power/KW");
						DataProcessorTool.setChart(chart1P4,tsVec1P4 , "Electricity Prices", "Money/Yuan");
						//P5
						convertFromMapToVec(storageEnergyTS,tsVecP5);
						DataProcessorTool.setChart(chartP5,tsVecP5 , "Storage's Current Energy", "Energy/KJ");
						
						dataConnected = true;
					}
				}else {
					tempText = "δ���ӳɹ������Ժ����ԣ�";
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
				management.sendEmail();
			}
			
		});
		
		
		list0P0.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list0P0.getValueIsAdjusting()){    //����ֻ���ͷ����ʱ�Ŵ���
					String tempStr = (String)list0P0.getSelectedValue();
					String tempValue = infoVec.elementAt(0).get(tempStr);
					text0P0.setText(tempValue);
                }
			}
			
		});
		
		list1P0.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list1P0.getValueIsAdjusting()){    //����ֻ���ͷ����ʱ�Ŵ���
					String tempStr = (String)list1P0.getSelectedValue();
					String tempValue = infoVec.elementAt(1).get(tempStr);
					text0P0.setText(tempValue);
                }
			}
			
		});
		
		list2P0.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list2P0.getValueIsAdjusting()){    //����ֻ���ͷ����ʱ�Ŵ���
					String tempStr = (String)list2P0.getSelectedValue();
					String tempValue = infoVec.elementAt(2).get(tempStr);
					text0P0.setText(tempValue);
                }
			}
			
		});
		
		list0P3.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list0P3.getValueIsAdjusting()){    //����ֻ���ͷ����ʱ�Ŵ���
					addButP3.setEnabled(true);
					selectedStr0P3 = (String)list0P3.getSelectedValue();
                }
			}
			
		});
		
		list1P3.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!list1P3.getValueIsAdjusting()){    //����ֻ���ͷ����ʱ�Ŵ���
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
		
		helpMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jHelpDialog = new JDialog();
				jHelpDialog.setSize(400,300);
				jHelpDialog.setLocation(400, 300);
				jHelpDialog.setLayout(new BorderLayout());
				JLabel tempLabel = new JLabel();
				tempLabel.setText("��Ȩ����:�������ƿ���"+"      �汾0.0");
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
		infoLabelP4[0].setText(String.valueOf(Prices.get(0)) );
		infoLabelP4[1].setText(String.valueOf(Prices.get(2)) );
		infoLabelP4[2].setText(String.valueOf(Prices.get(1)) );
		infoLabelP4[3].setText(String.valueOf(Prices.get(3)) );
	}
	
}