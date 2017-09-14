package com.view.panel.smallPanel;

import com.dataModel.SDataManager;
import com.utils.TConst;
import com.utils.TMbassadorSingleton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.isIntOrDoubleNumber;
import static com.utils.TConst.AK_REAL_PRICE;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TPubUtil.STR_SEPARATOR;
import static com.utils.TPubUtil.getAKmsg;
import static com.utils.TStringUtil.notNullAndEmptyStr;

/**
 * Created by 123 on 2016/12/24.
 */
public class SSymbolePanel extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    private JLabel symbol = new JLabel("Symbol:");
    private JTextField symbolText = new JTextField("spy", 10);
    private JButton btnQuery = crtQueryBtn(); // 查询实时价格按钮
    private JLabel price = new JLabel("225.71    +0.33    +0.15%:");
    private String tikerID = "";  // 每次查询的tikerid

    public SSymbolePanel(Component parentWin)
    {
        setBackground(Color.white);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        setPrice(257.8888, 3.2678);

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension, 1.0, 0.1));
    }


    private void buildGUI()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        add(symbol);
        add(symbolText);
        add(btnQuery);
        add(price);
    }

    public void setPrice(double currentPrice, double add)
    {
        boolean addFlag = (add > 0.0) ? true : false;
        String str = String.format("%.2f   %.2f   %.2f%%", currentPrice, add, add / currentPrice * 100);
        price.setText(str);
        // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
        //price.setFont(new java.awt.Font("Dialog",   1,   15));
        price.setForeground(addFlag ? Color.RED : Color.blue);
    }

    // 开始查询symbolText中的实时价格
    private JButton crtQueryBtn()
    {
        JButton btnQuery = new JButton(getConfigValue("query", TConst.CONFIG_I18N_FILE));
        btnQuery.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // 1: 查询当前symbol的实时价格
                // 2: 查询期权链
                // 3：默认查询最近期权链数据
                cancelCurrentSybRealPrice();
                String currentSymbol = symbolText.getText().trim();
                tikerID = querySybRealPrice(currentSymbol);
                setCurrentQueryTickID(tikerID);
            }
        });
        return btnQuery;
    }

    private void cancelCurrentSybRealPrice()
    {
        if (notNullAndEmptyStr(tikerID))
        {
            SDataManager.getInstance().cancelQueryRealTimePrice(tikerID);
        }
    }

    // 查询实时价格，返回的时tickeid，用于在返回时接收过滤
    private String querySybRealPrice(String symbol)
    {
        if (notNullAndEmptyStr(symbol))
        {
            String tickID = SDataManager.getInstance().queryRealTimePrice(symbol);
            return tickID;
        }
        return "";
    }

    // 设置当前查询的symbol，用于后面取消查询时使用
    private void setCurrentQueryTickID(String currentQueryTickid)
    {
        this.tikerID = currentQueryTickid;
    }

    // 接收实时价格的消息过滤器
    static public class realPriceStatusFilter implements IMessageFilter<String>
    {
        @Override
        public boolean accepts(String msg, SubscriptionContext subscriptionContext)
        {
            return msg.startsWith(AK_REAL_PRICE);
        }
    }

    // 连接消息处理器
    @Handler(filters = {@Filter(realPriceStatusFilter.class)})
    private void getRealPrice(String msg)
    {
        String realPriceStr = getAKmsg(AK_REAL_PRICE, msg);
        String[] attrArry = realPriceStr.split(STR_SEPARATOR);
         /*
         public void tickPrice(int tickerId, int field, double price, TickAttr attrib)
        */
        // 根据返回的实时数据前3个进行设置
        if (attrArry.length == 3)
        {
            String tickerId = attrArry[0];
            String field = attrArry[1];
            String price = attrArry[2];
            if (tickerId.equals(tikerID) && isIntOrDoubleNumber(price))
            {
                setPrice(Double.valueOf(price), 0.0);
            }
        }

    }

}
