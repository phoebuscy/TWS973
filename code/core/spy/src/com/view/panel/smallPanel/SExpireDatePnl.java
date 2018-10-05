package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBAOptionChainMap;
import com.commdata.mbassadorObj.MBAOptionExpireDayList;
import com.dataModel.SDataManager;
import com.ib.client.ContractDetails;
import com.utils.TConst;
import com.utils.TMbassadorSingleton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.getCurrentAmericalLocalDate;
import static com.utils.SUtil.getDimension;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TFileUtil.getConfigValue;
import static java.awt.event.ItemEvent.SELECTED;

/**
 * Created by 123 on 2016/12/24.
 */
public class SExpireDatePnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    private JLabel expireDate = new JLabel("ExpireDate:");
    private JComboBox expireDataComb = new JComboBox();
    private JButton queryOptionbtn = new JButton(getConfigValue("query.option.chain",
                                                                TConst.CONFIG_I18N_FILE)); // 查询期权链


    public SExpireDatePnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        setExpireDataComb();
        buildGUI();
        addActionListener();

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
    }

    private void addActionListener()
    {
        queryOptionbtn.addActionListener(e -> {
            expireDataComb.removeAllItems();
            SDataManager.getInstance().getSymbol().queryOptionChain();  // 查询期权链
        });

        expireDataComb.addItemListener(e -> {
            onExpireDateChanged(e);
        });

    }

    private void onExpireDateChanged(ItemEvent e)
    {
        // 根据选择的日期查询出当前价格上下5个点之间的期权链
        if (e.getStateChange() == SELECTED)
        {
            Object selecteddate = expireDataComb.getSelectedItem();
            if (selecteddate instanceof String)
            {
                String expireDay = (String) selecteddate;

                // 获取指定数量期权
                Map<Double, List<ContractDetails>> strike2ContractDtalsLst = SDataManager.getInstance().getSymbol()
                                                                                         .getStrike2ContractDtalsLst(
                                                                                                 expireDay,
                                                                                                 8);
                // 发送构造好的当前期权链的消息
                TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(new MBAOptionChainMap(strike2ContractDtalsLst));
            }
        }
    }


    private void setExpireDataComb()
    {
        expireDataComb.addItem("2016/12/25");
        expireDataComb.addItem("2016/12/27");
        expireDataComb.addItem("2016/12/30");
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension, 1.0, 0.1));
    }

    private void buildGUI()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        add(expireDate);
        expireDataComb.setPreferredSize(new Dimension(150, 30));
        add(expireDataComb);
        add(queryOptionbtn);
    }

    /////////////////////////////
    // 接收期权链到期日期的过滤器
    static public class optionExpireDayFilter implements IMessageFilter<MBAOptionExpireDayList>
    {
        @Override
        public boolean accepts(MBAOptionExpireDayList msg, SubscriptionContext subscriptionContext)
        {
            return msg != null;
        }
    }

    // 处理期权到期日的处理器
    @Handler(filters = {@Filter(optionExpireDayFilter.class)})
    private void proccessOptionExpireDays(MBAOptionExpireDayList msg)
    {
        LocalDate curUsaOpenDate = getCurrentAmericalLocalDate();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyMMdd");
        expireDataComb.removeAllItems();
        if (msg != null && msg.optionExpireDayList != null)
        {
            for (String day : msg.optionExpireDayList)
            {
                LocalDate tmpDate = LocalDate.parse(day, dateTimeFormatter);
                if (!tmpDate.isBefore(curUsaOpenDate))
                {
                    expireDataComb.addItem(day);
                }
            }
        }

    }

}


