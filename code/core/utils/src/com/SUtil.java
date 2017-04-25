package com;


import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 123 on 2016/12/18.
 */
public class SUtil
{

    public static void main(String[] args)
    {
        String src = "10";
        String des = "1";

        ReturnObj returnObj = getPercentValStr( src,  des);
        String ret = (String) returnObj.returnObj;

        double a = Double.parseDouble("2.2");
        double b = a;


    }


    /**
     * 获取到屏幕长宽相对比列的尺寸, 参数不能大于1
     *
     * @param rel_width  宽的相对比例
     * @param rel_height 高的相对比例
     * @return 计算后的dimension
     */
    public static Dimension getGUIDimension(double rel_width, double rel_height)
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDim = toolkit.getScreenSize();
        double width = rel_width >= 1.0 ? 1.0 : rel_width;
        double height = rel_height >= 1.0 ? 1.0 : rel_height;
        return new Dimension((int) (screenDim.width * width), (int) (screenDim.height * height));
    }

    public static Dimension getParentDimension(Dimension parentDim)
    {
        return (parentDim != null) ? parentDim : Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static void showInScreenCenter(Component frame, Dimension framDim)
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDim = toolkit.getScreenSize();

        int centerX = screenDim.width * 1 / 2;
        int centerY = screenDim.height * 1 / 2;

        int showX = centerX - framDim.width * 1 / 2;
        int showY = centerY - framDim.height * 1 / 2;

        frame.setLocation(showX, showY);
    }

    public static Dimension getScreenCenterLocation()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDim = toolkit.getScreenSize();

        int centerX = screenDim.width * 1 / 2;
        int centerY = screenDim.height * 1 / 2;
        return new Dimension(centerX, centerY);
    }


    public static Dimension getDimension(Dimension dim, double rel_width, double rel_height)
    {
        if (dim != null && rel_width >= 0.0 && rel_width <= 1.0)
        {
            return new Dimension((int) (dim.width * rel_width), (int) (dim.height * rel_height));
        }
        return null;
    }

    public static void setWindosStyle(JFrame topoframe, int style)
    {

        String sty = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        switch (style)
        {
            case 1:
                sty = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";//Nimbus风格，jdk6 update10版本以后的才会出现
                break;
            case 2:
                sty = UIManager.getSystemLookAndFeelClassName();//当前系统风格
                break;
            case 3:
                sty = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";//Motif风格，是蓝黑
                break;
            case 4:
                sty = UIManager.getCrossPlatformLookAndFeelClassName();//跨平台的Java风格
                break;
            case 5:
                sty = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";//windows风格
                break;
            case 6:
                sty = "javax.swing.plaf.windows.WindowsLookAndFeel";//windows风格
                break;
            case 7:
                sty = "javax.swing.plaf.metal.MetalLookAndFeel";//java风格
                break;
            case 8:
                sty = "com.apple.mrj.swing.MacLookAndFeel";//待考察，
                break;
        }
        try
        {
            UIManager.setLookAndFeel(sty);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(topoframe.getContentPane());
    }

    public static JButton crtButton(String buttTxt)
    {
        JButton btn = new JButton(buttTxt);
        return btn;
    }

    public static void setColumWidth(JTable table, int width, int height)
    {
        if (table != null)
        {
            TableColumnModel tableColumnModel = table.getColumnModel();
            int clumCount = tableColumnModel.getColumnCount();
            for (int i = 0; i < clumCount; i++)
            {
                tableColumnModel.getColumn(i).setPreferredWidth(width);
            }
            table.setRowHeight(height);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
    }

    public static boolean isStrNull(String str)
    {
        return str == null || str.isEmpty();
    }

    public static boolean isIntNumeric(Object obj)
    {
        return obj != null && isIntNumeric(obj.toString());
    }
    public static boolean isIntNumeric(String str)
    {
        if (!isStrNull(str))
        {
            Pattern pattern = Pattern.compile("[-+]?[0-9]*");
            Matcher isNum = pattern.matcher(str);
            return isNum.matches();
        }
        return false;
    }


    public static boolean isDoubleNumber(Object obj)
    {
        return obj != null && isDoubleNumber(obj.toString());
    }

    public static boolean isDoubleNumber(String str)
    {
        if (!isStrNull(str))
        {
            Pattern pattern = Pattern.compile("[-+]?[\\d]+\\.[\\d]+");
            Matcher isDoubleNum = pattern.matcher(str);
            return isDoubleNum.matches();
        }
        return false;
    }

    public static boolean isIntOrDoubleNumber(Object obj)
    {
        return obj != null && isIntOrDoubleNumber(obj.toString());
    }

    public static boolean isIntOrDoubleNumber(String str)
    {
        return isIntNumeric(str) || isDoubleNumber(str);
    }

    public static ReturnObj getDiffDoubleNumber(String src, String des)
    {
        ReturnObj returnObj = new ReturnObj();
        if (!isStrNull(src) && !isStrNull(des))
        {
            if (isIntOrDoubleNumber(src) && isIntOrDoubleNumber(des))
            {
                returnObj.setReturnObj(Double.valueOf(Double.parseDouble(des) - Double.parseDouble(src)));
                returnObj.setSuccess(true);
            }
        }
        return returnObj;
    }

    public static ReturnObj getDiffIntNumber(String src, String des)
    {
        ReturnObj returnObj = new ReturnObj();
        if (!isStrNull(src) && !isStrNull(des))
        {
            if (isIntNumeric(src) && isIntNumeric(des))
            {
                returnObj.setReturnObj(Integer.parseInt(des) - Integer.parseInt(src));
                returnObj.setSuccess(true);
            }
        }
        return returnObj;
    }

    public static ReturnObj getPercentVal(String src, String des)
    {
        ReturnObj returnObj = new ReturnObj();
        if (!isStrNull(src) && !isStrNull(des))
        {
            if (isIntOrDoubleNumber(src) && isIntOrDoubleNumber(des))
            {
                returnObj.setReturnObj(Double.parseDouble(des)/Double.parseDouble(src));
                returnObj.setSuccess(true);
            }
        }
        return returnObj;
    }

    public static ReturnObj getPercentValStr(String src, String des)
    {
        ReturnObj returnObj = new ReturnObj();
        if (!isStrNull(src) && !isStrNull(des))
        {
            if (isIntOrDoubleNumber(src) && isIntOrDoubleNumber(des))
            {
                Double percent = Double.parseDouble(des)/Double.parseDouble(src);
                String strPercent = String.format("%.2f%%",percent*100);
                returnObj.setReturnObj(strPercent);
                returnObj.setSuccess(true);
            }
        }
        return returnObj;
    }

    public static String getPercentValStr(Object obj)
    {
        String percnetStr = "--";
        if(obj != null && isIntOrDoubleNumber(obj))
        {
            Double db = Double.valueOf(obj.toString());
            percnetStr = String.format("%.2f%%",db*100);
        }
        return percnetStr;
    }

    // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
    //price.setFont(new java.awt.Font("Dialog",   1,   15));
    public static JLabel getShowNumberLabel(Object num,Boolean ifPer,BigInteger bold, BigInteger fontsize , Boolean ifColor)
    {
        JLabel label = new JLabel("--");
        label.setOpaque(true);
        Integer integer = Integer.valueOf(0);
        Double db = 0.0;
        if(isIntNumeric(num))
        {
            integer = Integer.valueOf(num.toString());
            if(ifPer)
            {
                label.setText(getPercentValStr(num));
            }
            else
            {
                label.setText(num.toString());
            }
        }
        else if(isDoubleNumber(num))
        {
            db = Double.valueOf(num.toString());
            if(ifPer)
            {
                label.setText(getPercentValStr(num));
            }
            else
            {
                label.setText(String.format("%.2f", db));
            }
        }
        Font font = label.getFont();
        font.getSize();
        int fontBold = (bold != null && bold.intValue() == 1)? 1: 0;  // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
        int ftSize = (fontsize != null) ? fontsize.intValue(): font.getSize();
        if(bold != null)
        {
            label.setFont(new Font("Dialog", fontBold, ftSize));
        }

        if(ifColor != null && ifColor)
        {
            if (integer > 0 || db > 0.0)
            {
                label.setForeground(Cst.ReadColor);
            }
            else if (integer < 0 || db < 0.0)
            {
                label.setForeground(Cst.GreenColor);
            }
            else
            {
                label.setForeground(Cst.BlackColor);
            }
        }
        return label;
    }


}
