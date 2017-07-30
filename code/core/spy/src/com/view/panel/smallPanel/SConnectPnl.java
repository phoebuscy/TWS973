package com.view.panel.smallPanel;

import com.dataModel.monitor.ConnectionMonitor;
import com.dataModel.monitor.ProcessMsgMonitor;
import com.utils.SUtil;
import com.utils.TMbassadorSingleton;
import com.dataModel.SDataManager;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.utils.TConst.AK_CONNECTED;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TIconUtil.getProjIcon;
import static com.utils.TPubUtil.getAKmsg;

/**
 * 连接按钮面板
 * Created by 123 on 2016/12/19.
 */
public class SConnectPnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;

    private JLabel ip = new JLabel("IP:");
    private JTextField ipText = new JTextField("127.0.0.1", 10);
    private JLabel port = new JLabel("Port:");
    private JTextField portText = new JTextField("4002", 5);
    private JButton connectBtn = new JButton("Connect");
    private JLabel connStatus = new JLabel(" Disconnected");

    private static int cltid = 888;


    public SConnectPnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        setButtonListener();

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
    }

    private void setDimension()
    {
        setSize(SUtil.getDimension(parentDimension, 1.0, 0.1));
    }


    private void buildGUI()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        add(ip);
        add(ipText);
        add(port);
        add(portText);
        add(connectBtn);
        add(connStatus);
        Icon icon = getProjIcon("disconnicon");
        connStatus.setIcon(icon);
    }

    private void setButtonListener()
    {
        if (connectBtn != null)
        {
            connectBtn.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    String ip = ipText.getText();
                    int port = Integer.valueOf(portText.getText());
                    SDataManager.getInstance().setClientConnectionParam(ip,port,cltid);
                    SDataManager.getInstance().connect();
                    ProcessMsgMonitor.startMonitor(SDataManager.getInstance());
                    ConnectionMonitor.startMonitor(SDataManager.getInstance());
                }
            });
        }
    }

    // 消息过滤器
    static public class connectStatusFilter implements IMessageFilter<String>
    {
        @Override
        public boolean accepts(String msg, SubscriptionContext subscriptionContext)
        {
            return msg.startsWith(AK_CONNECTED);
        }
    }

    // 连接消息处理器
    @Handler(filters = {@Filter(connectStatusFilter.class)})
    private void setConnStatus(String msg)
    {
        String isConnected = getAKmsg(AK_CONNECTED, msg);
        String iconName = "true".equalsIgnoreCase(isConnected) ? "connicon" : "disconnicon";
        connStatus.setIcon(getProjIcon(iconName));
        connStatus.setText("connicon".equals(iconName) ? " Connected" : " Disconnected");
    }


}
