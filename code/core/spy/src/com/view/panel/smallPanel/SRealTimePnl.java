package com.view.panel.smallPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Date;

public class SRealTimePnl extends JPanel
{
    private  TimeSeries timeSeries;
    private  Date begDate = new Date(2017, 10, 20);
    private  Date endDate = new Date(2017, 10, 21);
    private  long cruTime = begDate.getTime();


    public SRealTimePnl(String title, Dimension defaultDem)
    {
        initUI(title,"","");
        setPreferredSize(defaultDem);
    }

    private void initUI(String chartContent, String title, String yaxisName)
    {
        setLayout(new BorderLayout());
        add(createChartPnl(chartContent,title,yaxisName), new BorderLayout().CENTER);
    }

    private ChartPanel createChartPnl(String chartContent, String title, String yaxisName)
    {
        ChartPanel chartPanel = new ChartPanel(createChart(chartContent,title,yaxisName));
        chartPanel.setMouseZoomable(false);
        return chartPanel;
    }

    private JFreeChart createChart(String chartContent, String title, String yaxisName)
    {
        // TODO Auto-generated method stub
        // ����ʱ��ͼ����
        timeSeries = new TimeSeries(chartContent);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title,
                chartContent,
                yaxisName,
                timeseriescollection,
                false,
                true,
                false);
        XYPlot xyplot = jfreechart.getXYPlot();

        // X�����趨
        ValueAxis xAxis = xyplot.getDomainAxis();
        // �Զ��������������ݷ�Χ
        xAxis.setAutoRange(false);
        // ������̶����ݷ�Χ 300s
        // valueaxis.setFixedAutoRange(300000D);
        xAxis.setRange(begDate.getTime(), endDate.getTime());

        // ����Y�᷶Χ
        ValueAxis yAxis = xyplot.getRangeAxis();
        yAxis.setRange(255.50, 257.55);
        // rangeAxis.setAutoRange(true);
        xyplot.getRangeAxis().setUpperMargin(1.1);// ���ö���Y��������,��ֹ�����޷���ʾ
        xyplot.getRangeAxis().setLowerMargin(1.1);// ���õײ�Y��������

        /*
        //����������ʾ�����ݵ��ֵ
        XYItemRenderer xyitem = xyplot.getRenderer();
        xyitem.setBaseItemLabelsVisible(true);
        xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        //���������Ƕ���������ͼ���ݱ�ʾ�Ĺؼ�����
        xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 14));
        xyplot.setRenderer(xyitem);
        */

        return jfreechart;
    }


}
