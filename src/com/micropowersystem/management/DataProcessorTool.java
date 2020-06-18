package com.micropowersystem.management;

import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;  
import org.jfree.chart.axis.DateAxis;  
import org.jfree.chart.axis.ValueAxis;  
import org.jfree.chart.labels.ItemLabelAnchor;  
import org.jfree.chart.labels.ItemLabelPosition;  
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;  
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;  
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Month;  
import org.jfree.data.time.TimeSeries;  
import org.jfree.data.time.TimeSeriesCollection;  
import org.jfree.ui.ApplicationFrame;  
import org.jfree.ui.RefineryUtilities;  
import org.jfree.ui.TextAnchor;  

public class DataProcessorTool {
	
	public static void setChart(JFreeChart chart,Vector<TimeSeries> ts,String title,String Yname) {
		
		TimeSeriesCollection lineDataset = new TimeSeriesCollection();
		
		for (TimeSeries tempTs : ts) {
			lineDataset.addSeries(tempTs);
		}
		
		Font font = new Font("΢���ź�", Font.PLAIN, 12);    
        Font fontTitle = new Font("΢���ź�", Font.BOLD, 14);  

        chart.setTitle(new TextTitle(title, fontTitle));//���ñ���������ʽ 
       

        
        XYPlot plot = (XYPlot) chart.getPlot();             
        
        plot.setDataset(lineDataset);
       
        
        ValueAxis  valueAxisY = plot.getRangeAxis();  
        valueAxisY.setLabelFont(font);  //����Y���ϱ�־������
        valueAxisY.setLabel(Yname);
        
          
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();  
        xylineandshaperenderer.setBaseShapesVisible(true); //���������Ƿ���ʾת�ӵ�   
         
         
        XYItemRenderer xyitem = plot.getRenderer();         //����������ʾ�����ݵ��ֵ  
        xyitem.setBaseItemLabelsVisible(false);    //��Ϊtrue��������ʾÿ�����ֵ 
        xyitem.setBasePositiveItemLabelPosition(  
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));  
        xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());  
        xyitem.setBaseItemLabelFont(new Font("Arial", Font.PLAIN, 10));   
        xyitem.setSeriesPaint(0, new Color(0,128,128));     //����������ɫ

        
       
        DateAxis axis = (DateAxis) plot.getDomainAxis();  
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss")); //����X����������ʽ           
          
        ValueAxis  valueAxisX = plot.getDomainAxis();   
        valueAxisX.setLabel("ʱ��");
        valueAxisX.setLabelFont(font);    //����X��ı�ʶ������  
        valueAxisX.setTickLabelFont(font);//����X���ϵ�����  
        valueAxisX.setUpperMargin(0.05);  //�����ұ߾� 
		
	}
	public static void clearChart(JFreeChart chart) {
		TimeSeriesCollection lineDataset = null;
		chart.setTitle(new TextTitle(""));//���ñ���������ʽ
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis  valueAxisY = plot.getRangeAxis();  
        valueAxisY.setLabel("");
        plot.setDataset(lineDataset);
		
	}
}