package com.view.panel.smallPanel;

import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.dataModel.mbassadorObj.MBASymbolRealPrice;
import com.utils.TConst;
import com.utils.TMbassadorSingleton;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import static com.utils.SUtil.getDimension;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TFileUtil.getConfigValue;

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

    public SSymbolePanel(Component parentWin)
    {
        setBackground(Color.white);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        setPrice(257.8888, 3.2678);

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
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
                Symbol symbol = SDataManager.getInstance().getSymbol();
                if(symbol != null)
                {
                    symbol.cancelQuerySymbolRealPrice();
                    symbol.setSymbolVal(symbolText.getText().trim());
                    symbol.querySymbolRealPrice();
                }
            }
        });
        return btnQuery;
    }


    // 接收实时价格的消息过滤器
    static public class realPriceStatusFilter implements IMessageFilter<MBASymbolRealPrice>
    {
        @Override
        public boolean accepts(MBASymbolRealPrice msg, SubscriptionContext subscriptionContext)
        {
            return msg != null;
        }
    }

    // 连接消息处理器
    @Handler(filters = {@Filter(realPriceStatusFilter.class)})
    private void getRealPrice(MBASymbolRealPrice msg)
    {
        setPrice(msg.symbolRealPrice, 0.0);
    }

}
