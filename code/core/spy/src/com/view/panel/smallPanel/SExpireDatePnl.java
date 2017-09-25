package com.view.panel.smallPanel;

import com.dataModel.SDataManager;
import com.dataModel.mbassadorObj.MBAOptionChainMap;
import com.dataModel.mbassadorObj.MBAOptionExpireDayList;
import com.ib.client.ContractDetails;
import com.utils.TConst;
import com.utils.TMbassadorSingleton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
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
                                                                TConst.CONFIG_I18N_FILE)); // ��ѯ��Ȩ��


    public SExpireDatePnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        setExpireDataComb();
        buildGUI();
        addActionListener();

        // ������Ϣ��������Ϊ DATAMAAGER_BUS �� ��Ϣ
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
    }

    private void addActionListener()
    {
        queryOptionbtn.addActionListener(e -> {
            expireDataComb.removeAllItems();
            SDataManager.getInstance().getSymbol().queryOptionChain();  // ��ѯ��Ȩ��
        });

        expireDataComb.addItemListener(e -> {
            onExpireDateChanged(e);
        });

    }

    private void onExpireDateChanged(ItemEvent e)
    {
        // ����ѡ������ڲ�ѯ����ǰ�۸�����5����֮�����Ȩ��
        if (e.getStateChange() == SELECTED)
        {
            Object selecteddate = expireDataComb.getSelectedItem();
            if (selecteddate instanceof String)
            {
                String expireDay = (String) selecteddate;
                Map<Double, List<ContractDetails>> strike2ContractDtalsLst = SDataManager.getInstance().getSymbol()
                                                                                         .getStrike2ContractDtalsLst(
                                                                                                 expireDay);
                // ���͹���õĵ�ǰ��Ȩ������Ϣ
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
    // ������Ȩ���������ڵĹ�����
    static public class optionExpireDayFilter implements IMessageFilter<MBAOptionExpireDayList>
    {
        @Override
        public boolean accepts(MBAOptionExpireDayList msg, SubscriptionContext subscriptionContext)
        {
            return msg != null;
        }
    }

    // ������Ȩ�����յĴ�����
    @Handler(filters = {@Filter(optionExpireDayFilter.class)})
    private void proccessOptionExpireDays(MBAOptionExpireDayList msg)
    {
        expireDataComb.removeAllItems();
        if (msg != null && msg.optionExpireDayList != null)
        {
            for (String day : msg.optionExpireDayList)
            {
                expireDataComb.addItem(day);
            }
        }
    }

}


