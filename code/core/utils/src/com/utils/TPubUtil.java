package com.utils;

import com.answermodel.AnswerObj;
import com.ib.client.Contract;
import com.ib.client.Types;
import java.util.Collection;
import java.util.Map;
import static com.utils.TStringUtil.notNullAndEmptyStr;

/**
 * Created by caiyong on 2017/3/11.
 */
public class TPubUtil
{

    public final static String STR_SEPARATOR = "/";   // 字符串分割符号

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

    public static AnswerObj makeAKmsg(final int reqid, Object answerObj)
    {
        AnswerObj answerObj1 = new AnswerObj(reqid, answerObj);
        return answerObj1;
    }

    public static String makeAKmsg(final String akflag, final String... attrs)
    {
        if (notNullAndEmptyStr(akflag) && notNullAndEmptyArry(attrs))
        {
            StringBuilder builder = new StringBuilder();
            builder.append(akflag);
            for (String attr : attrs)
            {
                builder.append(STR_SEPARATOR);
                builder.append(attr);
            }
            return builder.toString();
        }
        return null;
    }

    public static String getAKmsg(final String akflag, final String attr)
    {
        if (notNullAndEmptyStr(attr) && attr.startsWith(akflag + STR_SEPARATOR))
        {
            return attr.substring(akflag.length() + 1, attr.length());
        }
        return null;
    }

    public static Contract crtContract(String symbolName)
    {
        if (notNullAndEmptyStr(symbolName))
        {
            Contract contract = new Contract();
            contract.conid(0);
            contract.symbol(symbolName);
            contract.secType("STK");
            contract.exchange("SMART");
            contract.primaryExch("ISLAND");
            contract.currency("USD");
            return contract;
        }
        return null;
    }

    public static boolean isCall(Contract contract)
    {
        if(contract != null)
        {
            return (Types.Right.Call.equals(contract.right()));
        }
        return false;
    }

    public static boolean isPut(Contract contract)
    {
        if(contract != null)
        {
            return (Types.Right.Put.equals(contract.right()));
        }
        return false;
    }

    public static boolean isSameRight(Contract one, Contract other)
    {
        if(one != null && !Types.Right.None.equals(one.right()) && other != null)
        {
            return one.right().equals(other.right());
        }
        return false;
    }

    public static boolean isSameContractID(Contract one, Contract other)
    {
        if(one != null && other != null)
        {
            return one.conid() == other.conid();
        }
        return false;
    }


}
