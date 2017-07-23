package com.utils;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caiyong on 2017/3/11.
 */
public class TIconUtil
{
    private static Map<String, Icon> iconMap;

    public static void main(String[] args)
    {
        Icon icon = getProjIcon("disconnic");
        Icon icon1 = getProjIcon("img0.png");
        int a = 1;
    }

    public static Icon getProjIcon(final String iconName)
    {
        if(TPubUtil.nullOrEmptyMap(iconMap))
        {
            iconMap = getIconSource();
        }

        return iconMap.get(getFileNameWithNoDox(iconName));
    }

    private static String getFileNameWithNoDox(final String name)
    {
        if(TStringUtil.notNullAndEmptyStr(name) && name.contains("."))
        {
            return name.substring(0,name.indexOf("."));
        }
        return name;
    }


    private static Map<String, Icon> getIconSource()
    {
        List<String> fileLst = TFileUtil.getProjectFileByDoxName("png");
        Map<String, Icon> iconMap = new HashMap<>();

        if (TPubUtil.notNullAndEmptyCollection(fileLst))
        {
            for (String file : fileLst)
            {
                String name = getNoPathFileName(file);
                String[] nameArr = name.split("\\.");
                if (TPubUtil.notNullAndEmptyArry(nameArr) && TStringUtil.notNullAndEmptyStr(nameArr[0]) && !iconMap.containsKey(nameArr[0]))
                {
                    Icon icon = new ImageIcon(file);
                    iconMap.put(nameArr[0], icon);
                }
            }
        }

        return iconMap;
    }

    private static String getNoPathFileName(final String fileName)
    {
        if (TStringUtil.notNullAndEmptyStr(fileName))
        {
            String sysSep = TPubUtil.getSysFileSeparator();
            String name = TPubUtil.replaceToSysFileSeparator(fileName);
            if (name.contains(sysSep))
            {
                return name.substring(name.lastIndexOf(sysSep) + 1, name.length());
            } else
            {
                return name;
            }
        }
        return fileName;
    }


}
