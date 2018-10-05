package com.view.panel.smallPanel.button;

import com.utils.TConst;
import javax.swing.Icon;
import javax.swing.JButton;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TIconUtil.getProjIcon;

/**
 * 中间的 平/反 按钮
 */
public class ChangeOperateButton extends JButton
{
    private Icon changeIco = getProjIcon("change");
    private OpenCloseButton callBtn;
    private OpenCloseButton putBtn;


    public ChangeOperateButton(OpenCloseButton callBtn, OpenCloseButton putBtn)
    {
        this.callBtn = callBtn;
        this.putBtn = putBtn;
        setIcon(changeIco);
        setText(getConfigValue("ping.and.fan", TConst.CONFIG_I18N_FILE)); // 平/反

    }

    public void doCallPutChange()
    {
        if (callBtn != null)
        {
            callBtn.placeOrder();
        }
        if (putBtn != null)
        {
            putBtn.placeOrder();
        }

    }
}