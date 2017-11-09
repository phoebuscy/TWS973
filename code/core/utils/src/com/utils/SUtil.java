package com.utils;


import com.commdata.mbassadorObj.MBAHistoricalData;

import com.ib.client.Types;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.Pair;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableColumnModel;

import static com.utils.TPubUtil.notNullAndEmptyCollection;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

/**
 * Created by 123 on 2016/12/18.
 */
public class SUtil
{

    public static void main(String[] args)
    {
        String src = "10";
        String des = "1";

        String dateStr = "1509629405";


        Instant instant = Instant.ofEpochSecond(1509629405);

        LocalDateTime lt = LocalDateTime.ofInstant(instant, ZoneId.of("America/New_York"));

        //  ZoneOffset zoneOffset =  ZoneOffset.of("Asia/Shanghai"); //  ZoneOffset.SHORT_IDS.get()
        // LocalDateTime lt = LocalDateTime.ofEpochSecond(Long.parseLong(dateStr),0,zoneOffset);
        //  LocalDateTime dateFromBase = LocalDateTime.ofEpochSecond(Long.parseLong(dateStr), 0, );

        int lastDay = 1;
        Pair lastOPencloseLocalTime = getUSAOpenDateTimeByLastDay(lastDay);


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

    // 判断两个数是否相等，包括整数和double数，此处差小于0.0001即表示相等
    public static boolean isEqualDoubleNumber(String oneNum, String otherNum, double betweendiff)
    {
        if (!isStrNull(oneNum) && !isStrNull(otherNum))
        {
            if (isIntOrDoubleNumber(oneNum) && isIntOrDoubleNumber(otherNum))
            {
                double abs = Math.abs(Double.parseDouble(otherNum) - Double.parseDouble(oneNum));
                return Double.compare(abs, betweendiff) == -1;
            }
        }
        return false;
    }

    // 判断两个数是否相等，包括整数和double数，此处差小于diff即表示相等
    public static boolean isEqualdoubleNumber(double oneNum, double otherNum, double betweendiff)
    {
        double abs = Math.abs(oneNum - otherNum);
        return Double.compare(abs, betweendiff) == -1;
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
                returnObj.setReturnObj(Double.parseDouble(des) / Double.parseDouble(src));
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
                Double percent = Double.parseDouble(des) / Double.parseDouble(src);
                String strPercent = String.format("%.2f%%", percent * 100);
                returnObj.setReturnObj(strPercent);
                returnObj.setSuccess(true);
            }
        }
        return returnObj;
    }

    public static String getPercentValStr(Object obj)
    {
        String percnetStr = "--";
        if (obj != null && isIntOrDoubleNumber(obj))
        {
            Double db = Double.valueOf(obj.toString());
            percnetStr = String.format("%.2f%%", db * 100);
        }
        return percnetStr;
    }

    // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
    //price.setFont(new java.awt.Font("Dialog",   1,   15));
    public static JLabel getShowNumberLabel(Object num,
                                            Boolean ifPer,
                                            BigInteger bold,
                                            BigInteger fontsize,
                                            Boolean ifColor)
    {
        JLabel label = new JLabel("--");
        label.setOpaque(true);
        Integer integer = Integer.valueOf(0);
        Double db = 0.0;
        if (isIntNumeric(num))
        {
            integer = Integer.valueOf(num.toString());
            if (ifPer)
            {
                label.setText(getPercentValStr(num));
            }
            else
            {
                label.setText(num.toString());
            }
        }
        else if (isDoubleNumber(num))
        {
            db = Double.valueOf(num.toString());
            if (ifPer)
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
        int fontBold = (bold != null && bold.intValue() == 1) ? 1 : 0;  // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
        int ftSize = (fontsize != null) ? fontsize.intValue() : font.getSize();
        if (bold != null)
        {
            label.setFont(new Font("Dialog", fontBold, ftSize));
        }

        if (ifColor != null && ifColor)
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

    public static String getDate(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //前面的lSysTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
        if (isIntNumeric(date))
        {
            long l_date = Long.valueOf(date);
            Date dt = new Date(l_date * 1000);
            String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
            return sDateTime;
        }
        return "1900-01-01 00:00:00";
    }

    public static String getSysYear()
    {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        return year;
    }

    // 获取当前美国时间
    public static LocalDateTime getCurrentAmericaLocalDateTime()
    {
        Instant timestamp = new Date().toInstant();
        LocalDateTime usaLocalDateTime = LocalDateTime.ofInstant(timestamp, ZoneId.of("America/New_York"));
        return usaLocalDateTime;
    }

    // 获取当前美国日期
    public static LocalDate getCurrentAmericalLocalDate()
    {
        LocalDate usaLocalDate = LocalDate.now(ZoneId.of("America/New_York"));
        return usaLocalDate;
    }


    // 判断指定美国日期是否是交易日期
    // 美国股市放假时间
    /*
    2017年1月2日 ： 新年元旦次日，休市一天。
　　2017年1月16日 ： 马丁-路德-金纪念日，休市一天。
　　2017年2月20日 ： 华盛顿诞辰日（总统日），休市一天。
　　2017年4月14日 ： 耶稣受难日，休市一天。
　　2017年5月29日 ： 美国亡兵纪念日，休市一天。
　　2017年7月4日 ： 美国独立日，休市一天。
　　2017年9月4日 ： 美国的劳工日，休市一天。
　　2017年11月23日 ： 感恩节，休市一天。
　　2017年11月24日 ： 感恩节次日，休市三小时（即提前三小时收盘）。
　　2017年12月25日 ： 圣诞节，休市一天。
     */
    public static boolean isOpenDayOfUSADateTime(LocalDateTime usaDateTime)
    {
        if (usaDateTime != null)
        {
            int m = usaDateTime.getMonthValue();
            int d = usaDateTime.getDayOfMonth();
            DayOfWeek dayOfWeek = usaDateTime.getDayOfWeek();
            if ((m == 1 && d == 2) || (m == 1 && d == 16) || (m == 2 && d == 20) || (m == 4 && d == 14) ||
                (m == 5 && d == 29) || (m == 7 && d == 4) || (m == 9 && d == 4) || (m == 11 && d == 23) ||
                (m == 12 && d == 25) || SATURDAY.equals(dayOfWeek) || SUNDAY.equals(dayOfWeek))
            {
                return false;
            }
            return true;
        }
        return false;
    }


    // 是否是感恩节:注意 2017年11月24日 ： 感恩节次日，休市三小时（即提前三小时收盘）。
    public static boolean isGanEnJie()
    {
        LocalDateTime nowDateTime = getCurrentAmericaLocalDateTime();
        int m = nowDateTime.getMonthValue();
        int d = nowDateTime.getDayOfMonth();
        return m == 11 && d == 24;

    }

    // 判断当前是否是开盘时间: 注意 2017年11月24日 ： 感恩节次日，休市三小时（即提前三小时收盘）。
    public static boolean ifNowIsOpenTime()
    {
        LocalDateTime curUSADateTime = getCurrentAmericaLocalDateTime();
        if (isOpenDayOfUSADateTime(curUSADateTime))
        {
            int year = curUSADateTime.getYear();
            int month = curUSADateTime.getMonthValue();
            int day = curUSADateTime.getDayOfMonth();
            boolean isGanEnJie = isGanEnJie();

            int beginHour = 9;
            int endHour = 16;
            endHour = isGanEnJie ? 13 : endHour;
            return curUSADateTime.isAfter(LocalDateTime
                                                  .of(LocalDate.of(year, month, day), LocalTime.of(beginHour, 30))) &&
                   curUSADateTime.isBefore(LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.of(endHour, 0)));
        }
        return false;
    }


    // 获取当天美国开盘时间
    public static LocalDateTime getCurrentDayUSAOpenDateTime()
    {
        LocalDateTime curUsaDateTime = getCurrentAmericaLocalDateTime();
        if (isOpenDayOfUSADateTime(curUsaDateTime))
        {
            int year = curUsaDateTime.getYear();
            int month = curUsaDateTime.getMonthValue();
            int day = curUsaDateTime.getDayOfMonth();
            return LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.of(9, 30));
        }
        return null;
    }

    // 获取当天美国收盘时间
    public static LocalDateTime getCurrentDayUSACloseDateTime()
    {
        LocalDateTime currentAmericaLocalDateTime = getCurrentAmericaLocalDateTime();
        if (isOpenDayOfUSADateTime(currentAmericaLocalDateTime))
        {
            int year = currentAmericaLocalDateTime.getYear();
            int month = currentAmericaLocalDateTime.getMonthValue();
            int day = currentAmericaLocalDateTime.getDayOfMonth();
            boolean isGanEnJie = isGanEnJie();
            int endHour = isGanEnJie ? 13 : 16;
            return LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.of(endHour, 0));
        }
        return null;
    }


    // 判断美国是否是夏令时间：美国的夏令时从三月的第二个周日开始到十一月的第一个周日结束
    public static boolean isAmericanDaylightSavingTime()
    {
        LocalDateTime nowDateTime = getCurrentAmericaLocalDateTime();
        return isAmericanDaylightSavingTime(nowDateTime);
    }

    public static boolean isAmericanDaylightSavingTime(LocalDateTime localDateTime)
    {
        int nowYear = localDateTime.getYear();
        LocalDateTime dltBeginDate = LocalDateTime.of(nowYear, 3, 1, 2, 0);
        int sunday = 0;
        do
        {
            dltBeginDate = dltBeginDate.plusDays(1);
            if (SUNDAY.equals(dltBeginDate.getDayOfWeek()))
            {
                sunday++;
            }
        } while (sunday < 2);

        LocalDateTime dltEndDate = LocalDateTime.of(nowYear, 11, 1, 2, 0);
        sunday = 0;
        do
        {
            dltEndDate = dltEndDate.plusDays(1);
            if (SUNDAY.equals(dltEndDate.getDayOfWeek()))
            {
                sunday++;
            }
        } while (sunday < 1);
        return localDateTime.isAfter(dltBeginDate) && localDateTime.isBefore(dltEndDate);
    }

    public static boolean isAmericanDaylightSavingTime(int year, int month, int day)
    {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, 0, 0);
        return isAmericanDaylightSavingTime(localDateTime);
    }


    public static List<Date> getBeginEndDate()
    {
        LocalDateTime openDateTime = LocalDateTime.of(getCurrentAmericalLocalDate(), LocalTime.of(9, 30));
        LocalDateTime closeDateTime = LocalDateTime.of(getCurrentAmericalLocalDate(), LocalTime.of(16, 0));
        Date begDate = changeToDate(openDateTime);
        Date endDate = changeToDate(closeDateTime);
        List<Date> dateZonelst = new ArrayList<>();
        dateZonelst.add(begDate);
        dateZonelst.add(endDate);

        return dateZonelst;
    }

    public static Pair<Date, Date> getOpenCloseDate(LocalDateTime openDateTime, LocalDateTime closeDateTime)
    {
        if (openDateTime != null && closeDateTime != null)
        {
            Date begDate = changeToDate(openDateTime);
            Date endDate = changeToDate(closeDateTime);
            return new Pair<>(begDate, endDate);
        }
        return null;
    }


    public static Date changeToDate(LocalDateTime localDateTime)
    {
        if (localDateTime != null)
        {
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDateTime.atZone(zone).toInstant();
            Date date = Date.from(instant);
            return date;
        }
        return null;
    }

    public static LocalDateTime localChangeToUSADateTime(LocalDateTime localDateTime)
    {
        if (localDateTime != null)
        {
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDateTime.atZone(zone).toInstant();
            return LocalDateTime.ofInstant(instant, ZoneId.of("America/New_York"));
        }
        return null;
    }


    public static LocalDateTime usaChangeToLocalDateTime(LocalDateTime usaDateTime)
    {
        if (usaDateTime != null)
        {
            ZoneId zone = ZoneId.of("America/New_York");
            Instant instant = usaDateTime.atZone(zone).toInstant();
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
        return null;
    }

    // 根据 秒 获取美国时间
    public static LocalDateTime getUSADateTimeByEpochSecond(String epochSecond)
    {
        if (isIntNumeric(epochSecond))
        {
            long epocSecs = Long.parseLong(epochSecond);
            Instant instant = Instant.ofEpochSecond(epocSecs);
            LocalDateTime lt = LocalDateTime.ofInstant(instant, ZoneId.of("America/New_York"));
            return lt;
        }
        return null;
    }

    // 根据 秒 获取本地时间
    public static LocalDateTime getLocalDateTimeByEpochSecond(String epochSecond)
    {
        if (isIntNumeric(epochSecond))
        {
            long epocSecs = Long.parseLong(epochSecond);
            Instant instant = Instant.ofEpochSecond(epocSecs);
            LocalDateTime lt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return lt;
        }
        return null;
    }

    // 获取历史数据中最低和最高值
    public static Pair getLowHighPair(List<MBAHistoricalData> historicalDataList)
    {
        if (notNullAndEmptyCollection(historicalDataList))
        {
            Double low = Double.MAX_VALUE;
            Double high = Double.MIN_VALUE;
            for (MBAHistoricalData historydata : historicalDataList)
            {
                low = historydata.low < low ? historydata.low : low;
                high = historydata.high > high ? historydata.high : high;
            }
            return new Pair<>(low, high);
        }
        return null;
    }

    // 获取指定天数之前的开盘的本地时间, 参数 lastDay 是表示之前多少天
    public static Pair<LocalDateTime, LocalDateTime> getUSAOpenDateTimeByLastDay(int lastDay)
    {
        if (lastDay > 0)
        {

            // 获取当前美国日期
            LocalDateTime curUSADateTime = getCurrentAmericaLocalDateTime();
            int tmpDay = 0;
            do
            {
                curUSADateTime = curUSADateTime.plusDays(-1);
                if (isOpenDayOfUSADateTime(curUSADateTime))
                {
                    tmpDay++;
                }
            } while (tmpDay < lastDay);


            // 获取开盘时间
            boolean isAmericanDaylightSavingTime = isAmericanDaylightSavingTime(curUSADateTime); // 是否是夏令时
            int beginHour = isAmericanDaylightSavingTime ? 9 : 10;

            // 获取收盘时间
            boolean isGanEnJie = isGanEnJie();
            int endHour = isAmericanDaylightSavingTime ? 16 : 17;
            endHour = isGanEnJie ? 13 : endHour;

            LocalDate usaLocalDate = LocalDate.of(curUSADateTime.getYear(),
                                                  curUSADateTime.getMonthValue(),
                                                  curUSADateTime.getDayOfMonth());

            LocalDateTime openUsaDateTime = LocalDateTime.of(usaLocalDate, LocalTime.of(beginHour, 30));
            LocalDateTime closeUsaDateTime = LocalDateTime.of(usaLocalDate, LocalTime.of(endHour, 0));

            //  LocalDateTime localOpenDateTime = usaChangeToLocalDateTime(openUsaDateTime);
            // LocalDateTime localCloseDateTime = usaChangeToLocalDateTime(closeUsaDateTime);
            return new Pair<>(openUsaDateTime, closeUsaDateTime);
        }
        return null;
    }


    // 计算当前时间到开盘时间的时间间隔, 单位秒
    public static long getLastOpenTimeSeconds()
    {
        if (ifNowIsOpenTime())
        {
            LocalDateTime usaOpentTime = getCurrentDayUSAOpenDateTime();
            LocalDateTime curUsaTime = getCurrentAmericaLocalDateTime();
            Duration duration = Duration.between(usaOpentTime, curUsaTime);
            return duration.getSeconds();
        }
        return -1;
    }

    // 根据时间间隔 获取到 BarSize
    public static Types.BarSize getBarSizebyDurationSeconds(long seconds)
    {
        if (seconds <= 10000)
        {
            return Types.BarSize._5_secs;
        }
        else if (seconds <= 20000)
        {
            return Types.BarSize._10_secs;
        }
        else if (seconds <= 30000)
        {
            return Types.BarSize._15_secs;
        }
        else
        {
            return Types.BarSize._30_secs;
        }
    }


}
