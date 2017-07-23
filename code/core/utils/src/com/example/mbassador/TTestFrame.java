package com.example.mbassador;

import com.utils.ReturnObj;
import com.utils.TBusFilter;
import com.utils.TMbassadorSingleton;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;

import javax.swing.*;
import java.awt.*;

import static com.utils.SUtil.*;

/**
 * Created by 123 on 2017/3/14.
 */
public class TTestFrame extends JFrame
{
    public static void main(String[] args)
    {
        TTestFrame sMainFram = new TTestFrame();
        sMainFram.setVisible(true);
    }

    public TTestFrame()
    {
        buildTopoFrame();
        TMbassadorSingleton.getInstance("myfirstBus").subscribe(this);
    }

    private void buildTopoFrame()
    {

        Dimension guiDim = getGUIDimension((double) 4 / 5, (double) 4 / 5);
        setSize(guiDim);
        showInScreenCenter(this, guiDim);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setWindosStyle(this, 1);

    }

    @Handler(filters = {@Filter(TBusFilter.StringFilter.class)})
    public void synchronousHandler(String message)
    {
        // do something
        if (message instanceof String)
        {
            int a = 1;
        }
        int b = 1;
    }


    @Handler(filters = {@Filter(TBusFilter.ReturnObjFilter.class)})
    public void bigInterMessage(ReturnObj message)
    {
        int a = 1;

    }


}
