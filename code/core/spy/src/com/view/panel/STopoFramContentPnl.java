package com.view.panel;

import javax.swing.*;
import java.awt.*;

import static com.utils.SUtil.getDimension;

/**
 * Created by 123 on 2016/12/18.
 */
public class STopoFramContentPnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    public STopoFramContentPnl(Component parentWin)
    {
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        buildContentPanel();
    }

    private void buildContentPanel()
    {
        setLayout(new BorderLayout());
        Point parentWinLocation = parentWin.getLocation();  // getLocation是获取相对于父窗口的位置

        // 构造左边显示数据面板
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setDividerSize(4);
        Dimension leftSplitPanDim = getDimension(parentWin.getSize(), 0.5, 1.0);
        leftSplitPane.setSize(leftSplitPanDim);
        JScrollPane operateScroPan = new JScrollPane(new SOptionInfoPnl(leftSplitPane));
        JScrollPane recordScroPan = new JScrollPane(new SOperatePnl(leftSplitPane));
        leftSplitPane.setTopComponent(operateScroPan);
        leftSplitPane.setBottomComponent(recordScroPan);
        leftSplitPane.setDividerLocation(parentWinLocation.y + leftSplitPanDim.height * 2 / 5);

        JPanel leftContentPan = new JPanel(new BorderLayout());
        leftContentPan.add(leftSplitPane, BorderLayout.CENTER);
      //  leftContentPan.setSize(getDimension(parentWin.getSize(), 0.5, 1.0));

        // 合并左右面板
        JSplitPane contentSplitPan = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        contentSplitPan.setSize(parentDimension);
        contentSplitPan.setDividerSize(4);
        contentSplitPan.setDividerLocation(0.5);
        JScrollPane leftScroPan = new JScrollPane(leftContentPan);
        JScrollPane rightScroPan = new JScrollPane(new SRealTimePicturePnl(parentWin));
        contentSplitPan.setLeftComponent(leftScroPan);
        contentSplitPan.setRightComponent(rightScroPan);

        add(contentSplitPan, BorderLayout.CENTER);

    }


}
