package com.view.panel.smallPanel;

import com.ib.client.Types;
import com.model.SOptionRealTimeInfoModel;
import com.utils.GBC;
import com.utils.ReturnObj;
import com.utils.TConst;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static com.utils.SUtil.getDiffDoubleNumber;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.getPercentValStr;
import static com.utils.SUtil.isIntOrDoubleNumber;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TStringUtil.notNullAndEmptyStr;

/**
 * Created by caiyong on 2016/12/25.
 */
public class SOptionRealTimeInfoPnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;
    private Types.Right right;

    private ObjPnl objPnl = new ObjPnl();
    private RealPricePnl realPricePnl = new RealPricePnl();
    private SellBuyPnl sellBuyPnl = new SellBuyPnl();
    private TodayOpenPnl todayOpenPnl = new TodayOpenPnl();



    public SOptionRealTimeInfoPnl(Component parentWin, Types.Right right)
    {
        setBackground(Color.gray);
        this.right = right;
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension, 1.0, 0.1));
    }

    private void buildGUI()
    {
        setLayout(new GridBagLayout());
        String callUp = getConfigValue("call.up", TConst.CONFIG_I18N_FILE);  // "CALL涨"
        String putDown = getConfigValue("put.down", TConst.CONFIG_I18N_FILE); // "PUT跌"
        setBorder(BorderFactory.createTitledBorder(Types.Right.Call.equals(right) ? callUp : putDown));

        add(objPnl, new GBC(0, 0, 2, 1).setWeight(100, 10).setFill(GBC.BOTH));
        add(realPricePnl, new GBC(0, 1).setWeight(100, 50).setFill(GBC.BOTH));
        add(sellBuyPnl, new GBC(1, 1).setWeight(100, 50).setFill(GBC.BOTH));
        add(todayOpenPnl, new GBC(0, 2, 2, 1).setWeight(100, 20).setFill(GBC.BOTH));
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
            obj_Label.setFont(new java.awt.Font("Dialog", style, size));
            expireDate_Label.setFont(new java.awt.Font("Dialog", style, size));
            operatePrice_Label.setFont(new java.awt.Font("Dialog", style, size));

            // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
            //price.setFont(new java.awt.Font("Dialog",   1,   15));

            setLayout(new GridBagLayout());

            // add(new JLabel("标的:"),new GBC(0,0).setIpad(2,0).setAnchor(GBC.WEST).setWeight(0,50).setFill(GBC.BOTH));
            add(obj_Label, new GBC(0, 0).setIpad(2, 0).setAnchor(GBC.WEST).setWeight(0, 50).setFill(GBC.BOTH)); // 标的
            //  add(new JLabel("到期:"),new GBC(2,0).setAnchor(GBC.WEST));
            add(expireDate_Label, new GBC(1, 0).setIpad(10, 0).setWeight(1, 50).setInsets(0, 15, 0, 0)
                                               .setAnchor(GBC.WEST).setFill(GBC.BOTH));
            //  add(new JLabel("行权价:"),new GBC(4,0).setAnchor(GBC.WEST));
            add(operatePrice_Label, new GBC(2, 0).setIpad(10, 0).setInsets(0, 5, 0, 0));

            setPreferredSize(new Dimension(100, 20));
        }

        public void setData(String obj, String expireDate, String operatePrice)
        {
            if (notNullAndEmptyStr(obj))
            {
                obj_Label.setText(obj);
            }
            if (notNullAndEmptyStr(expireDate))
            {
                expireDate_Label.setText(expireDate);
            }
            if (notNullAndEmptyStr(operatePrice))
            {
                operatePrice_Label.setText(operatePrice);
            }
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
        private String todayOpenPrice;
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
            realTimePrice_Label.setFont(new java.awt.Font("Dialog", 1, 20));
            realAdd_Label.setFont(new java.awt.Font("Dialog", style, size));
            addPercent_Label.setFont(new java.awt.Font("Dialog", style, size));

            setLayout(new GridBagLayout());
            add(realTimePrice_Label, new GBC(0, 0, 2, 2));
            add(realAdd_Label, new GBC(0, 2).setIpad(10, 0));
            add(addPercent_Label, new GBC(1, 2).setIpad(10, 0));
        }

        public void setData(String realPrice, String todayOpen)
        {
            if (isIntOrDoubleNumber(realPrice))
            {
                realTimePrice = realPrice;
            }
            if (isIntOrDoubleNumber(todayOpen))
            {
                todayOpenPrice = todayOpen;
            }

            if (isIntOrDoubleNumber(realTimePrice))
            {
                realTimePrice_Label.setText(realTimePrice);
            }

            if (isIntOrDoubleNumber(realTimePrice) && isIntOrDoubleNumber(todayOpenPrice))
            {
                String realAddStr = "--";
                String addPercentStr = "--";
                ReturnObj realAdd = getDiffDoubleNumber(todayOpenPrice, realTimePrice);
                realAddStr = realAdd.success ? String.format("%.3f", realAdd.returnObj) : realAddStr;
                ReturnObj realAddPercent = getPercentValStr(todayOpenPrice, realAddStr);
                addPercentStr = realAddPercent.success ? realAddPercent.returnObj.toString() : addPercentStr;
                realAdd_Label.setText(realAddStr);
                addPercent_Label.setText(addPercentStr);
                setPriceColor(realTimePrice, todayOpenPrice);
            }
        }

        private void setPriceColor(String realTimePrice, String todayOpenPrice)
        {
            int eaqual = 0;
            if (isIntOrDoubleNumber(realTimePrice) && isIntOrDoubleNumber(todayOpenPrice))
            {
                if (Double.valueOf(realTimePrice).compareTo(Double.valueOf(todayOpenPrice)) > 0)
                {
                    eaqual = 1;
                }
                else if (Double.valueOf(realTimePrice).compareTo(Double.valueOf(todayOpenPrice)) < 0)
                {
                    eaqual = -1;
                }
            }
            Color color = Color.black;
            if (eaqual == 1)
            {
                color = new Color(255, 80, 50);
            }
            else if (eaqual == -1)
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
            add(new JLabel(getConfigValue("sale.price", TConst.CONFIG_I18N_FILE)),
                new GBC(0, 0).setAnchor(GBC.WEST));  // 卖家
            add(curSellPrice_Label, new GBC(1, 0).setIpad(5, 0).setAnchor(GBC.WEST));
            add(curSellCount_Label, new GBC(2, 0).setAnchor(GBC.EAST));

            add(new JLabel(getConfigValue("buy.price", TConst.CONFIG_I18N_FILE)),
                new GBC(0, 1).setAnchor(GBC.WEST));   // 买价
            add(curBuyPrice_Label, new GBC(1, 1).setIpad(5, 0).setAnchor(GBC.WEST));
            add(curBuyCount_Label, new GBC(2, 1).setAnchor(GBC.EAST));

            add(new JLabel(getConfigValue("cjl", TConst.CONFIG_I18N_FILE)), new GBC(0, 2).setAnchor(GBC.WEST)); // 成交量
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
            if (notNullAndEmptyStr(curSellPrice))
            {
                curSellPrice_Label.setText(curSellPrice);
            }
            if (notNullAndEmptyStr(curSellCount))
            {
                curSellCount_Label.setText(curSellCount);
            }
            if (notNullAndEmptyStr(curBuyPrice))
            {
                curBuyPrice_Label.setText(curBuyPrice);
            }
            if (notNullAndEmptyStr(curBuyCount))
            {
                curBuyCount_Label.setText(curBuyCount);
            }
            if (notNullAndEmptyStr(tradingVol))
            {
                tradingVol_Label.setText(tradingVol);
            }
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
            add(new JLabel(getConfigValue("today.open", TConst.CONFIG_I18N_FILE)), new GBC(0, 0).setAnchor(GBC.WEST)
                                                                                                .setInsets(0,
                                                                                                           3,
                                                                                                           0,
                                                                                                           3)); //
            // "今开:"
            add(todayOpenPrice_Label, new GBC(1, 0).setAnchor(GBC.WEST).setInsets(0, 3, 0, 3));
            add(new JLabel(getConfigValue("yesterday.close", TConst.CONFIG_I18N_FILE)), new GBC(2, 0)
                    .setAnchor(GBC.WEST).setInsets(0, 3, 0, 3));  // 昨收
            add(yestadayClosePrice_Label, new GBC(3, 0).setAnchor(GBC.WEST).setInsets(0, 3, 0, 3));
            add(new JLabel(getConfigValue("max.high", TConst.CONFIG_I18N_FILE)), new GBC(4, 0).setAnchor(GBC.WEST)
                                                                                              .setInsets(0,
                                                                                                         3,
                                                                                                         0,
                                                                                                         3)); // 最高
            add(todayMaxPrice_Label, new GBC(5, 0).setAnchor(GBC.WEST).setInsets(0, 3, 0, 3));
            add(new JLabel(getConfigValue("min.low", TConst.CONFIG_I18N_FILE)), new GBC(6, 0).setAnchor(GBC.WEST)
                                                                                             .setInsets(0,
                                                                                                        3,
                                                                                                        0,
                                                                                                        3)); // 最低
            add(todayMinPrice_Label, new GBC(7, 0).setAnchor(GBC.WEST).setInsets(0, 3, 0, 3));
            add(new JLabel(getConfigValue("wpc", TConst.CONFIG_I18N_FILE)), new GBC(8, 0).setAnchor(GBC.WEST)
                                                                                         .setInsets(0, 3, 0, 3)); // 未平仓
            add(notCloseCount_Label, new GBC(9, 0).setAnchor(GBC.WEST).setInsets(0, 3, 0, 3));

            setPreferredSize(new Dimension(100, 20));

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
            if (notNullAndEmptyStr(todayOpenPrice))
            {
                todayOpenPrice_Label.setText(todayOpenPrice);
            }
            if (notNullAndEmptyStr(yestadayClosePrice))
            {
                yestadayClosePrice_Label.setText(yestadayClosePrice);
            }
            if (notNullAndEmptyStr(todayMaxPrice))
            {
                todayMaxPrice_Label.setText(todayMaxPrice);
            }
            if (notNullAndEmptyStr(todayMaxPrice))
            {
                todayMinPrice_Label.setText(todayMinPrice);
            }
            if (notNullAndEmptyStr(notCloseCount))
            {
                notCloseCount_Label.setText(notCloseCount);
            }
        }
    }





}
