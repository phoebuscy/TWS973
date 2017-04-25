package com;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.TStringUtil.notNullAndEmptyStr;

/**
 * Created by caiyong on 2017/3/11.
 */
public class TPubUtil
{

    public static void main(String[] args)
    {

        String str3 = null;
        String str2 = null;
        String str1 = getAKmsg("ABCDE", str2);


        int a = 1;

    }


    public static boolean nullOrEmptyCollection(final Collection collection)
    {
        return collection == null || collection.isEmpty();
    }

    public static boolean notNullAndEmptyCollection(final Collection collection)
    {
        return collection != null && !collection.isEmpty();
    }

    public static boolean nullOrEmptyArray(final Object[] objArry)
    {
        return objArry == null || objArry.length == 0;
    }

    public static boolean notNullAndEmptyArry(final Object[] objArry)
    {
        return objArry != null && objArry.length > 0;
    }

    public static boolean nullOrEmptyMap(final Map map)
    {
        return map == null || map.isEmpty();
    }

    public static boolean notNullAndEmptyMap(final Map map)
    {
        return map != null && !map.isEmpty();
    }

    /**
     * 把非系统路径分隔符号换成系统路径分隔符号
     *
     * @param path 路径
     * @return 换成系统路径分隔符号的路径
     */
    public static String replaceToSysFileSeparator(final String path)
    {
        if (notNullAndEmptyStr(path))
        {
            String rp_path = path;
            String sysPathSepa = java.io.File.separator;  // 系统分隔符
            sysPathSepa = "\\".equals(sysPathSepa) ? "\\\\" : sysPathSepa;
            String otherSepa = "\\\\".equals(sysPathSepa) ? "/" : "\\\\";
            return rp_path.replaceAll(otherSepa, sysPathSepa);
        }
        return path;
    }

    /**
     * 获取系统路径分隔符号
     *
     * @return
     */
    public static String getSysFileSeparator()
    {
        return java.io.File.separator;  // 系统分隔符

    }

    public static String makeAKmsg(final String akflag, final String attr)
    {
        if (akflag != null)
        {
            return akflag + ":" + attr;
        }
        return null;
    }

    public static String getAKmsg(final String akflag, final String attr)
    {
        if (notNullAndEmptyStr(attr) && attr.startsWith(akflag + ":"))
        {
            return attr.substring(akflag.length() + 1, attr.length());
        }
        return null;
    }


}
