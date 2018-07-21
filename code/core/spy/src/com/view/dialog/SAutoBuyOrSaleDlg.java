package com.view.dialog;

import com.auto.BuyOrSale;
import com.auto.Caculate;
import com.commdata.mbassadorObj.MBACaculateTxtInfo;
import com.database.DbManager;
import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TConst;
import com.utils.TMbassadorSingleton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.utils.TConst.CALCULATE_BUS;
import static com.utils.TFileUtil.getConfigValue;

public class SAutoBuyOrSaleDlg extends JFrame
{
    private static Logger LogApp = LogManager.getLogger("applog");
    private Caculate caculate = Caculate.getInstance();
    private BuyOrSale buyOrSale = BuyOrSale.getInstance();

    private boolean isAutoProcessing = false;

    JButton autoProcessBtn = new JButton(getConfigValue("begin.auto.buy.sale", TConst.CONFIG_I18N_FILE));

    JTextArea infoTextArea = new JTextArea();
    JComboBox barSizeCombox = new JComboBox();

    public SAutoBuyOrSaleDlg()
    {
        initFrame();
        // 订阅消息总线名称为 CALCULATE_BUS 的 消息
        TMbassadorSingleton.getInstance(CALCULATE_BUS).subscribe(this);
    }

    private void initFrame()
    {
        Dimension guiDim = SUtil.getGUIDimension((double) 2 / 5, (double) 1 / 3);
        setTitle(getConfigValue("auto.buy.sale", TConst.CONFIG_I18N_FILE));
        setSize(guiDim);
        SUtil.showInScreenCenter(this, guiDim);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(crtContentPnl());
        SUtil.setWindosStyle(this, 1);
    }

    private Container crtContentPnl()
    {
        JPanel contentPnl = new JPanel(new GridBagLayout());
        JPanel paramPanel = getQuryParamPanel();
        JPanel showInfoPanel = getShowInfoPanel();
        contentPnl.add(paramPanel, new GBC(0, 0).setWeight(1, 1).setFill(GBC.BOTH).setInsets(6));
        contentPnl.add(showInfoPanel, new GBC(0, 1).setWeight(1, 10).setFill(GBC.BOTH).setInsets(6));
        return contentPnl;
    }

    private JPanel getShowInfoPanel()
    {
        JPanel showInfoPnl = new JPanel(new BorderLayout());
        JScrollPane js = new JScrollPane(infoTextArea);
        js.setBounds(13, 10, 350, 340);
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        showInfoPnl.add(js, BorderLayout.CENTER);
        return showInfoPnl;
    }

    private JPanel getQuryParamPanel()
    {
        JPanel paramPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        autoProcessBtn.addActionListener(e -> processAutoBuyOrSale());
        paramPnl.add(autoProcessBtn);
        return paramPnl;
    }

    private void processAutoBuyOrSale()
    {
        isAutoProcessing = !isAutoProcessing;

        Color btnBackColor = isAutoProcessing ? Color.blue : Color.red;
        String btnTxt = !isAutoProcessing ? "begin.auto.buy.sale" : "stop.auto.buy.sale";
        autoProcessBtn.setBackground(btnBackColor);
        autoProcessBtn.setText(getConfigValue(btnTxt, TConst.CONFIG_I18N_FILE));

        if(isAutoProcessing)
        {
          caculate.beginCaculate();

        }
        else
        {
            caculate.stop();
        }
    }


    // Caculate 日志信息过滤器
    public static class rcvCaculateInfoFilter implements IMessageFilter<MBACaculateTxtInfo>
    {
        @Override
        public boolean accepts(MBACaculateTxtInfo msg, SubscriptionContext subscriptionContext)
        {
            return msg != null ;
        }
    }

    // 显示caculate 传来的信息
    @Handler(filters = {@Filter(rcvCaculateInfoFilter.class)})
    private void processContractRealTimePrice(MBACaculateTxtInfo msg)
    {
        if (msg != null)
        {
            infoTextArea.append(msg.info + '\n');
        }
    }



}
