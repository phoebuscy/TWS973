package com.view.panel.smallPanel;

import com.dataModel.SDataManager;
import com.ib.client.ContractDetails;
import com.answermodel.AnswerObj;
import com.utils.TConst;
import com.utils.TMbassadorSingleton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import static com.utils.TConst.AK_CONTRACT_DETAIL_END;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TPubUtil.getAKmsg;
import static com.utils.TPubUtil.notNullAndEmptyCollection;
import static com.utils.TPubUtil.notNullAndEmptyMap;

/**
 * Created by 123 on 2016/12/24.
 */
public class SExpireDatePnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    private JLabel expireDate = new JLabel("ExpireDate:");
    private JComboBox expireDataComb = new JComboBox();
    private JButton queryOptionbtn = new JButton(getConfigValue("query.option.chain", TConst.CONFIG_I18N_FILE)); // 查询期权链
    private static int reqid = -1;
    private List<ContractDetails> contractDetailsList = new ArrayList<>();


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
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
    }

    private void addActionListener()
    {
        queryOptionbtn.addActionListener(e ->
        {
            contractDetailsList.clear();
            expireDataComb.removeAllItems();
            reqid = queryOptionChain();
        });

        expireDataComb.addItemListener(e ->
        {
            onExpireDateChanged(e);
        });

    }

    private void onExpireDateChanged(ItemEvent e)
    {

    }

    /**
     * 查询期权链
     */
    private int queryOptionChain()
    {
        int tickID = SDataManager.getInstance().queryOptionChain();
        return tickID;
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
        expireDataComb.setPreferredSize(new Dimension(150,30));
        add(expireDataComb);
        add(queryOptionbtn);
    }


    // 接收期权链消息过滤器
    static public class optionChainFilter implements IMessageFilter<AnswerObj>
    {
        @Override
        public boolean accepts(AnswerObj msg, SubscriptionContext subscriptionContext)
        {
            return msg.getReqid() == reqid;
        }
    }

    // 连接消息处理器
    @Handler(filters = {@Filter(optionChainFilter.class)})
    private void getOptionChain(AnswerObj msg)
    {
         Object obj = msg.getAnswerObj();
        if(obj instanceof ContractDetails)
        {
            contractDetailsList.add((ContractDetails)obj);
        }
    }

    // 接收查询contractDetail完毕的过滤器
    static public class contractDetailEndFilter implements IMessageFilter<String>
    {
        @Override
        public boolean accepts(String msg, SubscriptionContext subscriptionContext)
        {
            if(msg.startsWith(AK_CONTRACT_DETAIL_END))
            {
              return  String.valueOf(reqid).equals(getAKmsg(AK_CONTRACT_DETAIL_END,msg));
            }
            return false;
        }
    }

    @Handler(filters = {@Filter(contractDetailEndFilter.class)})
    private void getContractDetailend(String msg)
    {
        Map<String, List<ContractDetails>> day2CtrdMap = new HashMap<>();
        if(notNullAndEmptyCollection(contractDetailsList))
        {
            for(ContractDetails ctrd: contractDetailsList)
            {
                String lastDay = ctrd.contract().lastTradeDateOrContractMonth();
                if(day2CtrdMap.containsKey(lastDay))
                {
                    day2CtrdMap.get(lastDay).add(ctrd);
                }
                else
                {
                    List<ContractDetails> ctrLst = new ArrayList<>();
                    ctrLst.add(ctrd);
                    day2CtrdMap.put(lastDay,ctrLst);
                }
            }
        }
        if(notNullAndEmptyMap(day2CtrdMap))
        {
            List<String> dateLst = new ArrayList<>(day2CtrdMap.keySet());
            Collections.sort(dateLst);
            expireDataComb.removeAllItems();
            for(String day: dateLst)
            {
                expireDataComb.addItem(day);
            }
        }

        int a = 1;
    }


}
