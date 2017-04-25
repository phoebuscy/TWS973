package com.view.panel.smallPanel;

import com.model.SOptionRealTimeInfoModel;
import com.ReturnObj;
import com.util.GBC;

import javax.swing.*;
import java.awt.*;

import static com.SUtil.*;

/**
 * Created by caiyong on 2016/12/25.
 */
public class SOptionRealTimeInfoPnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;
    private int putOrCall = -1;

    private ObjPnl objPnl = new ObjPnl();
    private RealPricePnl realPricePnl = new RealPricePnl();
    private SellBuyPnl sellBuyPnl = new SellBuyPnl();
    private TodayOpenPnl todayOpenPnl = new TodayOpenPnl();

    private SOptionRealTimeInfoModel infoModel = new SOptionRealTimeInfoModel();

    public SOptionRealTimeInfoPnl(Component parentWin, int putOrCall)
    {
        setBackground(Color.gray);
        this.putOrCall = putOrCall;
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        test_SetData();
    }

    private void setDimension()
    {
       setSize(getDimension(parentDimension, 1.0, 0.1));
    }

    private void buildGUI()
    {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(putOrCall == 1 ? "CALL涨" : "PUT跌"));
        /*
        objPnl.setBackground(Color.blue);
        realPricePnl.setBackground(Color.gray);
        sellBuyPnl.setBackground(Color.green);
        todayOpenPnl.setBackground(Color.blue);
       */
        add(objPnl, new GBC(0, 0, 2, 1).setWeight(100, 10).setFill(GBC.BOTH));
        add(realPricePnl, new GBC(0, 1).setWeight(100, 50).setFill(GBC.BOTH));
        add(sellBuyPnl, new GBC(1, 1).setWeight(100, 50).setFill(GBC.BOTH));
        add(todayOpenPnl, new GBC(0, 2, 2, 1).setWeight(100, 20).setFill(GBC.BOTH));
    }

    /**
     * 测试代码，添加实时数据
     */
    public void test_SetData()
    {
        SOptionRealTimeInfoModel infoModel = new SOptionRealTimeInfoModel();
        infoModel.setObj("SPY");
        infoModel.setExpireDate("2016-03-07");
        infoModel.setOperatePrice("223.5");
        infoModel.setRealTimePrice("1.57");
        infoModel.setCurSellPrice("25.3");
        infoModel.setCurSellCount("532");
        infoModel.setCurBuyPrice("25.7");
        infoModel.setCurBuyCount("23");
        infoModel.setTradingVol("32423");
        infoModel.setTodayOpenPrice("1.2");
        infoModel.setYestadayClosePrice("2.3");
        infoModel.setTodayMaxPrice("2.7");
        infoModel.setTodayMinPrice("1.2");
        infoModel.setNotCloseCount("234");

        setData(infoModel);
    }

    public void setData(SOptionRealTimeInfoModel infoModel)
    {
        objPnl.setData(infoModel.getObj(), infoModel.getExpireDate(), infoModel.getOperatePrice());
        realPricePnl.setData(infoModel.getRealTimePrice(), infoModel.getTodayOpenPrice());
        sellBuyPnl.setData(infoModel.getCurSellPrice(),
                           infoModel.getCurSellCount(),
                           infoModel.getCurBuyPrice(),
                           infoModel.getCurBuyCount(),
                           infoModel.getTradingVol());
        todayOpenPnl.setData(infoModel.getTodayOpenPrice(),
                             infoModel.getYestadayClosePrice(),
                             infoModel.getTodayMaxPrice(),
                             infoModel.getTodayMinPrice(),
                             infoModel.getNotCloseCount());
    }


    // OBJ 面板,在上面一行，包括 标的，行权日，行权价
    private class ObjPnl extends JPanel
    {
        private JLabel obj_Label; // 标的
        private JLabel expireDate_Label;  // 行权日
        private JLabel operatePrice_Label;  // 行权价

        public ObjPnl()
        {
            //  面板,在上面一行，包括 标的，行权日，行权价
            obj_Label = new JLabel("--"); // 标的
            expireDate_Label = new JLabel("--");  // 行权日
            operatePrice_Label = new JLabel("--");  // 行权价

            int style = 0;
            int size = 14;
            obj_Label.setFont(new java.awt.Font("Dialog",   style,   size));
            expireDate_Label.setFont(new java.awt.Font("Dialog",   style,   size));
            operatePrice_Label.setFont(new java.awt.Font("Dialog",   style,   size));

            // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
            //price.setFont(new java.awt.Font("Dialog",   1,   15));

            setLayout(new GridBagLayout());

           // add(new JLabel("标的:"),new GBC(0,0).setIpad(2,0).setAnchor(GBC.WEST).setWeight(0,50).setFill(GBC.BOTH));
            add(obj_Label, new GBC(0, 0).setIpad(2,0).setAnchor(GBC.WEST).setWeight(0,50).setFill(GBC.BOTH)); // 标的
          //  add(new JLabel("到期:"),new GBC(2,0).setAnchor(GBC.WEST));
            add(expireDate_Label, new GBC(1, 0).setIpad(10, 0).setWeight(1,50).setInsets(0,15,0,0).setAnchor(GBC.WEST).setFill(GBC.BOTH));
          //  add(new JLabel("行权价:"),new GBC(4,0).setAnchor(GBC.WEST));
            add(operatePrice_Label, new GBC(2, 0).setIpad(10, 0).setInsets(0, 5, 0, 0));

            setPreferredSize(new Dimension(100,20));
        }

        public void setData(String obj, String expireDate, String operatePrice)
        {
            obj_Label.setText(obj);
            expireDate_Label.setText(expireDate);
            operatePrice_Label.setText(operatePrice);
        }

        public void initData()
        {
            obj_Label.setText("--");
            expireDate_Label.setText("--");
            operatePrice_Label.setText("--");
        }
    }

    // 实时价格面板，位置在中部的左边
    private class RealPricePnl extends JPanel
    {
        private String todayOpenPrice; //
        private String realTimePrice;
        private JLabel realTimePrice_Label; // 当前实时价格
        private JLabel realAdd_Label; // 实际增减价格
        private JLabel addPercent_Label; // 增减百分比

        public RealPricePnl()
        {
            realTimePrice_Label = new JLabel();
            realAdd_Label = new JLabel();
            addPercent_Label = new JLabel();

            int style = 0;
            int size = 16;
            realTimePrice_Label.setFont(new java.awt.Font("Dialog",   1,   20));
            realAdd_Label.setFont(new java.awt.Font("Dialog",   style,   size));
            addPercent_Label.setFont(new java.awt.Font("Dialog",   style,   size));

            setLayout(new GridBagLayout());
            add(realTimePrice_Label, new GBC(0, 0, 2, 2));
            add(realAdd_Label, new GBC(0, 2).setIpad(10, 0));
            add(addPercent_Label, new GBC(1, 2).setIpad(10, 0));
        }

        public void setData(String realTimePrice, String todayOpenPrice)
        {
            String realAddStr = "--";
            String addPercentStr = "--";
            ReturnObj realAdd = getDiffDoubleNumber(todayOpenPrice, realTimePrice);
            realAddStr = realAdd.success ?  String.format("%.2f", realAdd.returnObj): realAddStr;
            ReturnObj realAddPercent = getPercentValStr(todayOpenPrice, realAddStr);
            addPercentStr = realAddPercent.success ? realAddPercent.returnObj.toString() : addPercentStr;

            realTimePrice_Label.setText(realTimePrice);
            realAdd_Label.setText(realAddStr);
            addPercent_Label.setText(addPercentStr);

            setPriceColor(realTimePrice,todayOpenPrice);
        }

        private void setPriceColor(String realTimePrice, String todayOpenPrice)
        {
            int eaqual = 0;
            if(isIntOrDoubleNumber(realTimePrice) && isIntOrDoubleNumber(todayOpenPrice))
            {
                if(Double.valueOf(realTimePrice).compareTo(Double.valueOf(todayOpenPrice)) >0)
                {
                    eaqual = 1;
                }
                else if(Double.valueOf(realTimePrice).compareTo(Double.valueOf(todayOpenPrice)) <0)
                {
                    eaqual = -1;
                }
            }
            Color color = Color.black;
            if(eaqual == 1)
            {
                color = new Color(255, 80, 50);
            }
            else if( eaqual == -1)
            {
                color = Color.BLUE;
            }
            realTimePrice_Label.setForeground(color);
            realAdd_Label.setForeground(color);
            addPercent_Label.setForeground(color);
        }

        public void initData()
        {
            realTimePrice_Label.setText("--");
            realAdd_Label.setText("--");
            addPercent_Label.setText("--");
        }
    }

    /**
     * 买卖价量面板 ，包括买卖价/量 成交量
     */
    private class SellBuyPnl extends JPanel
    {
        private JLabel curSellPrice_Label;   // 当前实时卖价
        private JLabel curSellCount_Label;  // 当前实时卖量
        private JLabel curBuyPrice_Label;   // 当前实时买价
        private JLabel curBuyCount_Label;    // 当前实时买量
        private JLabel tradingVol_Label; // 成交量

        public SellBuyPnl()
        {
            curSellPrice_Label = new JLabel();
            curSellCount_Label = new JLabel();
            curBuyPrice_Label = new JLabel();
            curBuyCount_Label = new JLabel();
            tradingVol_Label = new JLabel();

            setLayout(new GridBagLayout());
            add(new JLabel("卖价:"), new GBC(0, 0).setAnchor(GBC.WEST));
            add(curSellPrice_Label, new GBC(1, 0).setIpad(5, 0).setAnchor(GBC.WEST));
            add(curSellCount_Label, new GBC(2, 0).setAnchor(GBC.EAST));

            add(new JLabel("买价:"), new GBC(0, 1).setAnchor(GBC.WEST));
            add(curBuyPrice_Label, new GBC(1, 1).setIpad(5, 0).setAnchor(GBC.WEST));
            add(curBuyCount_Label, new GBC(2, 1).setAnchor(GBC.EAST));

            add(new JLabel("成交量:"), new GBC(0, 2).setAnchor(GBC.WEST));
            add(tradingVol_Label, new GBC(1, 2).setIpad(5, 0).setAnchor(GBC.WEST));
        }

        public void initData()
        {
            curSellPrice_Label.setText("--");
            curSellCount_Label.setText("--");
            curBuyPrice_Label.setText("--");
            curBuyCount_Label.setText("--");
            tradingVol_Label.setText("--");
        }

        public void setData(String curSellPrice,
                            String curSellCount,
                            String curBuyPrice,
                            String curBuyCount,
                            String tradingVol)
        {
            curSellPrice_Label.setText(curSellPrice);
            curSellCount_Label.setText(curSellCount);
            curBuyPrice_Label.setText(curBuyPrice);
            curBuyCount_Label.setText(curBuyCount);
            tradingVol_Label.setText(tradingVol);
        }
    }

    private class TodayOpenPnl extends JPanel
    {
        private JLabel todayOpenPrice_Label; // 今开价
        private JLabel yestadayClosePrice_Label; // 昨天收价
        private JLabel todayMaxPrice_Label;  // 最高价
        private JLabel todayMinPrice_Label;  // 最低价
        private JLabel notCloseCount_Label;   // 未平仓数量

        public TodayOpenPnl()
        {
            todayOpenPrice_Label = new JLabel();
            yestadayClosePrice_Label = new JLabel();
            todayMaxPrice_Label = new JLabel();
            todayMinPrice_Label = new JLabel();
            notCloseCount_Label = new JLabel();

            setLayout(new GridBagLayout());
            add(new JLabel("今开:"), new GBC(0, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));
            add(todayOpenPrice_Label, new GBC(1, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));
            add(new JLabel("昨收:"), new GBC(2, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));
            add(yestadayClosePrice_Label, new GBC(3, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));
            add(new JLabel("最高:"), new GBC(4, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));
            add(todayMaxPrice_Label, new GBC(5, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));
            add(new JLabel("最低:"), new GBC(6, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));
            add(todayMinPrice_Label, new GBC(7, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));
            add(new JLabel("未平仓:"), new GBC(8, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));
            add(notCloseCount_Label, new GBC(9, 0).setAnchor(GBC.WEST).setInsets(0,3,0,3));

            setPreferredSize(new Dimension(100,20));

        }

        public void initData()
        {
            todayOpenPrice_Label.setText("--");
            yestadayClosePrice_Label.setText("--");
            todayMaxPrice_Label.setText("--");
            todayMinPrice_Label.setText("--");
            notCloseCount_Label.setText("--");
        }

        public void setData(String todayOpenPrice,
                            String yestadayClosePrice,
                            String todayMaxPrice,
                            String todayMinPrice,
                            String notCloseCount)
        {
            todayOpenPrice_Label.setText(todayOpenPrice);
            yestadayClosePrice_Label.setText(yestadayClosePrice);
            todayMaxPrice_Label.setText(todayMaxPrice);
            todayMinPrice_Label.setText(todayMinPrice);
            notCloseCount_Label.setText(notCloseCount);
        }
    }


}
