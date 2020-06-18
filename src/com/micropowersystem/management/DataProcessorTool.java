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
		
		Font font = new Font("微软雅黑", Font.PLAIN, 12);    
        Font fontTitle = new Font("微软雅黑", Font.BOLD, 14);  

        chart.setTitle(new TextTitle(title, fontTitle));//设置标题字体样式 
       

        
        XYPlot plot = (XYPlot) chart.getPlot();             
        
        plot.setDataset(lineDataset);
       
        
        ValueAxis  valueAxisY = plot.getRangeAxis();  
        valueAxisY.setLabelFont(font);  //设置Y轴上标志符字体
        valueAxisY.setLabel(Yname);
        
          
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();  
        xylineandshaperenderer.setBaseShapesVisible(true); //设置曲线是否显示转接点   
         
         
        XYItemRenderer xyitem = plot.getRenderer();         //设置曲线显示各数据点的值  
        xyitem.setBaseItemLabelsVisible(false);    //改为true即可以显示每点的数值 
        xyitem.setBasePositiveItemLabelPosition(  
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));  
        xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());  
        xyitem.setBaseItemLabelFont(new Font("Arial", Font.PLAIN, 10));   
        xyitem.setSeriesPaint(0, new Color(0,128,128));     //设置折线颜色

        
       
        DateAxis axis = (DateAxis) plot.getDomainAxis();  
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss")); //设置X轴上日期样式           
          
        ValueAxis  valueAxisX = plot.getDomainAxis();   
        valueAxisX.setLabel("时间");
        valueAxisX.setLabelFont(font);    //设置X轴的标识符字体  
        valueAxisX.setTickLabelFont(font);//设置X轴上的字体  
        valueAxisX.setUpperMargin(0.05);  //设置右边距 
		
	}
	public static void clearChart(JFreeChart chart) {
		TimeSeriesCollection lineDataset = null;
		chart.setTitle(new TextTitle(""));//设置标题字体样式
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis  valueAxisY = plot.getRangeAxis();  
        valueAxisY.setLabel("");
        plot.setDataset(lineDataset);
		
	}
}