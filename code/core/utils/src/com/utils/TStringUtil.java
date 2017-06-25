package com.utils;

/**
 * Created by caiyong on 2017/3/11.
 */
public class TStringUtil
{

    public static void main(String[] args)
    {
        String str = "  g ";
        boolean rt = nullOrEmptyStr(str);

        int a = 1;

    }

    public static boolean nullOrEmptyStr(final String str)
    {
        return str == null || str.isEmpty() || trimStr(str) == null ||  trimStr(str).isEmpty();
    }

    public static  boolean notNullAndEmptyStr(final String str)
    {
        return str != null && !trimStr(str).isEmpty();
    }

    public static String trimStr(final String str)
    {
        if(str != null)
        {
            return str.replaceAll(" ","");
        }
        return null;
    }



}
