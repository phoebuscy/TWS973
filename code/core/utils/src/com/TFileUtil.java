package com;

import org.dom4j.io.SAXReader;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.util.*;

import static com.TPubUtil.*;
import static com.TStringUtil.notNullAndEmptyStr;
import static com.TStringUtil.nullOrEmptyStr;

/**
 * Created by caiyong on 2017/3/11.
 */
public class TFileUtil
{
    // 配置文件map，key为文件名，value为该文件内容key和value值
    private static Map<String, Map<String, String>> i18nMap = new HashMap<>();


    public static void main(String[] args)
    {

        String port = getConfigValue("port", TConst.CONFIG_I18N_FILE);
        String ip = getConfigValue("ip", TConst.CONFIG_I18N_FILE);

        List<String> logprop = getProjectFileByName("log4j.properties");


        int a = 1;

    }

    public static String getConfigValue(final String key, final String fileName)
    {
        Map<String, String> contentMap;
        if (i18nMap.get(fileName) == null)
        {
            contentMap = getConfigI18n(fileName);
            if (notNullAndEmptyMap(contentMap))
            {
                i18nMap.put(fileName, contentMap);
            }
        }
        contentMap = i18nMap.get(fileName);
        return notNullAndEmptyMap(contentMap) ? contentMap.get(key) : null;

    }


    /**
     * 获取项目所在路径(包括jar)
     *
     * @return
     */
    public static String getProjectPath()
    {
        java.net.URL url = TFileUtil.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try
        {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar"))
        {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        java.io.File file = new java.io.File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    /**
     * 获取项目所在路径
     *
     * @return
     */
    public static String getRealPath()
    {
        String realPath = TFileUtil.class.getClassLoader().getResource("").getFile();
        java.io.File file = new java.io.File(realPath);
        realPath = file.getAbsolutePath();
        try
        {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return realPath;
    }

    public static String getAppPath(Class<?> cls)
    {
        // 检查用户传入的参数是否为空
        if (cls == null)
        {
            throw new java.lang.IllegalArgumentException("参数不能为空！");
        }

        ClassLoader loader = cls.getClassLoader();
        // 获得类的全名，包括包名
        String clsName = cls.getName();
        // 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
        if (clsName.startsWith("java.") || clsName.startsWith("javax."))
        {
            throw new java.lang.IllegalArgumentException("不要传送系统类！");
        }
        // 将类的class文件全名改为路径形式
        String clsPath = clsName.replace(".", "/") + ".class";

        // 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        java.net.URL url = loader.getResource(clsPath);
        // 从URL对象中获取路径信息
        String realPath = url.getPath();
        // 去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1)
        {
            realPath = realPath.substring(pos + 5);
        }
        // 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(clsPath);
        realPath = realPath.substring(0, pos - 1);
        // 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith("!"))
        {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }
        java.io.File file = new java.io.File(realPath);
        realPath = file.getAbsolutePath();

        try
        {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return realPath;
    }// getAppPath定义结束


    /**
     * # 对于getCanonicalPath()函数， (Canonical 是规范，标准的意思) “."就表示当前的文件夹，而”..“则表示当前文件夹的上一级文件夹
     * # 对于getAbsolutePath()函数，则不管”.”、“..”，返回当前的路径加上你在new File()时设定的路径
     * # 至于getPath()函数，得到的只是你在new File()时设定的路径
     * 比如当前的路径为 C:/test ：
     * File directory = new File("abc");
     * directory.getCanonicalPath(); //得到的是C:/test/abc
     * directory.getAbsolutePath();  //得到的是C:/test/abc
     * direcotry.getPath();          //得到的是abc
     * <p>
     * File directory = new File(".");
     * directory.getCanonicalPath(); //得到的是C:/test
     * directory.getAbsolutePath();  //得到的是C:/test/.
     * direcotry.getPath();          //得到的是.
     * <p>
     * File directory = new File("..");
     * directory.getCanonicalPath(); //得到的是C:/
     * directory.getAbsolutePath();  //得到的是C:/test/..
     * direcotry.getPath();          //得到的是..
     *
     * @return
     */
    public static String getCanPath()
    {
        String path = "";
        //  File directory = new File("");//设定为当前文件夹
        try
        {
            File directory = new File(".");
            path = directory.getCanonicalPath(); //得到的是C:/test
            path = directory.getAbsolutePath();  //得到的是C:/test/.
            path = directory.getPath();          //得到的是.

            directory = new File("..");
            path = directory.getCanonicalPath(); //得到的是C:/
            path = directory.getAbsolutePath();  //得到的是C:/test/..
            path = directory.getPath();          //得到的是..

            directory = new File("..\\..");
            path = directory.getCanonicalPath(); //得到的是C:/
            path = directory.getAbsolutePath();  //得到的是C:/test/..
            path = directory.getPath();          //得到的是..

            directory = new File("abc");
            path = directory.getCanonicalPath(); //得到的是C:/test/abc
            path = directory.getAbsolutePath();  //得到的是C:/test/abc
            path = directory.getPath();          //得到的是abc
        } catch (IOException e)
        {
        }
        return path;
    }

    /**
     * 比如当前的路径为 C:/test ：
     * File  directory = new File(".");
     * path = directory.getCanonicalPath(); //得到的是C:/test
     * directory = new File("..");
     * path = directory.getCanonicalPath(); //得到的是C:/
     * directory = new File("abc");
     * path = directory.getCanonicalPath(); //得到的是C:/test/abc
     *
     * @param canPrefix 路径标识 ，为 “." 或".."或 "abc"等路径字符
     * @return
     */
    public static String getCanonicalPath(final String canPrefix)
    {
        String pre = nullOrEmptyStr(canPrefix) ? "" : canPrefix;
        String path = "";
        File directory = new File(pre);//设定为当前文件夹
        try
        {
            path = directory.getCanonicalPath();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取指定路径下的文件或目录列表
     *
     * @param path   指定的路径
     * @param rescur 是否递归
     * @return 文件列表
     */
    public static List<String> getFileDirectory(final String path, TConst.TFileDirEnum fileDirEnum, boolean rescur)
    {
        List<String> fileList = new ArrayList<>();

        File file = new File(nullOrEmptyStr(path) ? "" : path);
        File[] tempList = file.listFiles();
        if (nullOrEmptyArray(tempList) || fileDirEnum == null)
        {
            return fileList;
        }

        for (int i = 0; i < tempList.length; i++)
        {
            File fileTmp = tempList[i];
            String fileOrDir = tempList[i].toString();
            if (fileDirEnum == TConst.TFileDirEnum.FILE_AND_DIR ||
                    (fileDirEnum == TConst.TFileDirEnum.FILE_FLAG && fileTmp.isFile()) ||
                    (fileDirEnum == TConst.TFileDirEnum.DIR_FLAG && fileTmp.isDirectory()))
            {
                fileList.add(fileOrDir);
            }

            if (fileTmp.isDirectory() && rescur)  // 递归调用
            {
                fileList.addAll(getFileDirectory(fileOrDir, fileDirEnum, rescur));
            }

        }
        return fileList;
    }

    /**
     * 获取到本项目的配置文件
     *
     * @return
     */
    public static String getSPYCONFIGFILE()
    {
        String spyconfigfile = "";
        String path = getRealPath();
        String pathSepa = java.io.File.separator;  // 文件分割符号
        do
        {
            List<String> fileLst = getFileDirectory(path, TConst.TFileDirEnum.FILE_FLAG, true);
            if (notNullAndEmptyCollection(fileLst))
            {
                for (String file : fileLst)
                {
                    if (notNullAndEmptyStr(file) && file.contains(TConst.SPY_CONFIG_FILE))
                    {
                        return file;
                    }
                }
            }
            path = path.substring(0, path.lastIndexOf(pathSepa));

        } while (notNullAndEmptyStr(path) && path.contains(pathSepa));
        return spyconfigfile;
    }

    public static List<String> getProjectFileByName(final String filename)
    {
        // 用系统路径分隔符号替换文件中的路径分隔符号
        String rp_fileName = replaceToSysFileSeparator(filename);
        List<String> searchfilelst = new ArrayList<>();
        if (nullOrEmptyStr(rp_fileName))
        {
            return searchfilelst;
        }
        String pth = getCanonicalPath(".");
        List<String> fileLst = getFileDirectory(pth, TConst.TFileDirEnum.FILE_FLAG, true);
        if (notNullAndEmptyCollection(fileLst))
        {
            for (String file : fileLst)
            {
                if (notNullAndEmptyStr(file))
                {
                    int fileIndex = file.lastIndexOf(rp_fileName);
                    if (fileIndex != -1 && fileIndex + rp_fileName.length() == file.length())
                    {
                        searchfilelst.add(file);
                    }
                }
            }
        }
        return searchfilelst;
    }

    /**
     * 根据后缀名获取文件
     *
     * @param doxName 后缀名
     * @return
     */
    public static List<String> getProjectFileByDoxName(final String doxName)
    {
        List<String> searchfilelst = new ArrayList<>();
        if (nullOrEmptyStr(doxName))
        {
            return searchfilelst;
        }
        String pth = getCanonicalPath(".");
        List<String> fileLst = getFileDirectory(pth, TConst.TFileDirEnum.FILE_FLAG, true);
        if (notNullAndEmptyCollection(fileLst))
        {
            for (String file : fileLst)
            {
                if (notNullAndEmptyStr(file))
                {

                    int fileIndex = file.lastIndexOf("." + doxName);
                    if (fileIndex != -1 && fileIndex + doxName.length() + 1 == file.length())
                    {
                        searchfilelst.add(file);
                    }
                }
            }
        }
        return searchfilelst;
    }


    public static Map<String, String> getConfigI18n(final String i18nfile)
    {
        Map<String, String> i18nMap = new HashMap<>();
        String filename = "conf/" + i18nfile;
        List<String> fileLst = getProjectFileByName(filename);
        if (notNullAndEmptyCollection(fileLst))
        {
            filename = fileLst.get(0);
        }
        else
        {
            return i18nMap;
        }
        try
        {
            //1.创建一个SAXBuilder的对象
            SAXBuilder saxBuilder = new SAXBuilder();
            //2.创建一个输入流，将xml文件加载到输入流中
            InputStream in = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            //3.通过saxBuilder的build方法，将输入流加载到saxBuilder中
            Document document = saxBuilder.build(isr);
            //4.通过document对象获取xml文件的根节点
            Element rootElement = document.getRootElement();
            //5.获取根节点下的子节点的List集合
            List<Element> elementList = rootElement.getChildren();
            for (Element element : elementList)
            {
                Attribute languageAttr = element.getAttribute("language-country");
                if(languageAttr != null && !"zh_CN".equals(languageAttr.getValue()))
                {
                    continue;
                }
                // 解析文件的属性集合
                List<Attribute> list = element.getAttributes();
                for (Attribute attr : list)
                {
                    // 获取属性名
                    String attrName = attr.getName();
                    // 获取属性值
                    String attrValue = attr.getValue();
                    System.out.println(attrName + "=" + attrValue);
                    // 对book节点的子节点的节点名以及节点值的遍历
                    List<Element> listChild = element.getChildren();
                    for (Element child : listChild)
                    {
                        Attribute keyAttr = child.getAttribute("label-key");
                        Attribute valAttr = child.getAttribute("label-value");
                        if(keyAttr != null && notNullAndEmptyStr(keyAttr.getValue()))
                        {
                            i18nMap.put(keyAttr.getValue(),valAttr != null? valAttr.getValue():null);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (JDOMException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return i18nMap;
    }


}
