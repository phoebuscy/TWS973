package com.view.panel.smallPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class SRealTimePnl extends JPanel
{
    private TimeSeries timeSeries;
    private JFreeChart jfreechart;


    public SRealTimePnl(String title, Dimension defaultDem)
    {
        initUI(title, "", "");
        setPreferredSize(defaultDem);
    }

    private void initUI(String chartContent, String title, String yaxisName)
    {
        setLayout(new BorderLayout());
        add(createChartPnl(chartContent, title, yaxisName), new BorderLayout().CENTER);
    }

    private ChartPanel createChartPnl(String chartContent, String title, String yaxisName)
    {
        ChartPanel chartPanel = new ChartPanel(createChart(chartContent, title, yaxisName));
        chartPanel.setMouseZoomable(false);
        return chartPanel;
    }

    public void setXRange(Date begin, Date end)
    {
        if (jfreechart != null && begin != null && end != null)
        {
            // X�����趨
            XYPlot xyplot = jfreechart.getXYPlot();
            ValueAxis xAxis = xyplot.getDomainAxis();
            // �Զ��������������ݷ�Χ
            xAxis.setAutoRange(false);
            xAxis.setRange(begin.getTime(), end.getTime());

        }
    }

    public void setYRange(Double lower, Double upper)
    {
        if (jfreechart != null && lower != null && upper != null)
        {
            XYPlot xyplot = jfreechart.getXYPlot();
            // ����Y�᷶Χ
            ValueAxis yAxis = xyplot.getRangeAxis();
            yAxis.setRange(lower, upper);
            yAxis.setAutoRange(true);
            // xyplot.getRangeAxis().setUpperMargin(1.1);// ���ö���Y��������,��ֹ�����޷���ʾ
            //  xyplot.getRangeAxis().setLowerMargin(1.1);// ���õײ�Y��������
        }
    }

    public Range getYRange()
    {
        if (jfreechart != null)
        {
            XYPlot xyplot = jfreechart.getXYPlot();
            ValueAxis yAxis = xyplot.getRangeAxis();
            return yAxis.getRange();
        }
        return null;
    }

    private JFreeChart createChart(String chartContent, String title, String yaxisName)
    {
        // TODO Auto-generated method stub
        // ����ʱ��ͼ����
        timeSeries = new TimeSeries(chartContent);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);
        jfreechart = ChartFactory.createTimeSeriesChart(title,
                                                        chartContent,
                                                        yaxisName,
                                                        timeseriescollection,
                                                        false,
                                                        true,
                                                        false);
        /*
        XYPlot xyplot = jfreechart.getXYPlot();

        // X�����趨
        ValueAxis xAxis = xyplot.getDomainAxis();
        // �Զ��������������ݷ�Χ
        xAxis.setAutoRange(false);
        // ������̶����ݷ�Χ 300s
        // valueaxis.setFixedAutoRange(300000D);
        List<Date> dateList = getBeginEndDate();
        xAxis.setRange(dateList.get(0).getTime(), dateList.get(1).getTime());

        // ����Y�᷶Χ
        ValueAxis yAxis = xyplot.getRangeAxis();
        yAxis.setRange(255.50, 257.55);
        // rangeAxis.setAutoRange(true);
        xyplot.getRangeAxis().setUpperMargin(1.1);// ���ö���Y��������,��ֹ�����޷���ʾ
        xyplot.getRangeAxis().setLowerMargin(1.1);// ���õײ�Y��������
        */

        /*
        //����������ʾ�����ݵ��ֵ
        XYItemRenderer xyitem = xyplot.getRenderer();
        xyitem.setBaseItemLabelsVisible(true);
        xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor
        .BASELINE_LEFT));
        //���������Ƕ���������ͼ���ݱ�ʾ�Ĺؼ�����
        xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 14));
        xyplot.setRenderer(xyitem);
        */
        return jfreechart;
    }


    public void addValue(Date date, Double value)
    {
        if (date != null && value != null)
        {
            timeSeries.addOrUpdate(new Millisecond(date), value);
        }
    }


}
