package com.utils;

/**
 * Created by caiyong on 2017/3/12.
 */
public class TConst
{
    public enum TFileDirEnum
    {
        FILE_FLAG,
        DIR_FLAG,
        FILE_AND_DIR;
    };

    public static final String SPY_CONFIG_FILE = "spy-config.xml";
    public static final String CONFIG_I18N_FILE = "config-i18n.xml"; // 国际化文件

    public static final String DATAMAAGER_BUS = "SDataManager_bus";  // 用于DataManger 发布数据
    public static final String SYMBOL_BUS = "Symbol_bus";            // 用于Symbol对象发布数据
    public static final String REALTIMEPRICEMGR_BUS = "RealTimePriceMgr_bus"; // 用于RealTimePriceMgr发布数据


    public static final String AK_CONNECTED = "ak_connected";  // 连接返回报文头
    public static final String AK_CONTRACT_DETAIL_END = "ak_contractDetailsEnd";  // 查询contractDetail返回报文头





}
