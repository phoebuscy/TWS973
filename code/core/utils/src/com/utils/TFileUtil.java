package com.utils;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javafx.util.Pair;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.io.XMLWriter;

import static com.utils.TPubUtil.getSysFileSeparator;
import static com.utils.TPubUtil.notNullAndEmptyCollection;
import static com.utils.TStringUtil.notNullAndEmptyStr;
import static com.utils.TStringUtil.nullOrEmptyStr;

/**
 * Created by caiyong on 2017/3/11.
 */
public class TFileUtil
{
    // 配置文件map，key为文件名，value为该文件内容key和value值
    private static Map<String, Map<String, String>> i18nMap = new HashMap<>();


    public static void main(String[] args)
    {

        //   String port = getConfigValue("port", TConst.CONFIG_I18N_FILE);
        //    String ip = getConfigValue("ip", TConst.CONFIG_I18N_FILE);

        String module = "symbol";
        String id = "oncemoney";
        String value = "2000";
        setSpyConfigFile(module, id, value);



        Map<String, String> cfgmap = getSPYCONFIGContents();
        List<String> logprop = getProjectFileByName("log4j.properties");


        int a = 1;

    }

    public static String getConfigValue(final String key, final String fileName)
    {
        Map<String, String> contentMap;
        if (i18nMap.get(fileName) == null)
        {
            contentMap = getConfigI18n(fileName);
            if (TPubUtil.notNullAndEmptyMap(contentMap))
            {
                i18nMap.put(fileName, contentMap);
            }
        }
        contentMap = i18nMap.get(fileName);
        String retVal = TPubUtil.notNullAndEmptyMap(contentMap) ? contentMap.get(key) : key;
        return notNullAndEmptyStr(retVal) ? retVal : key;

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
        }
        catch (Exception e)
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
        }
        catch (Exception e)
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
        }
        catch (Exception e)
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
        }
        catch (IOException e)
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
        String pre = TStringUtil.nullOrEmptyStr(canPrefix) ? "" : canPrefix;
        String path = "";
        File directory = new File(pre);//设定为当前文件夹
        try
        {
            path = directory.getCanonicalPath();
        }
        catch (IOException e)
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

        File file = new File(TStringUtil.nullOrEmptyStr(path) ? "" : path);
        File[] tempList = file.listFiles();
        if (TPubUtil.nullOrEmptyArray(tempList) || fileDirEnum == null)
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
            if (TPubUtil.notNullAndEmptyCollection(fileLst))
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
        String rp_fileName = TPubUtil.replaceToSysFileSeparator(filename);
        List<String> searchfilelst = new ArrayList<>();
        if (TStringUtil.nullOrEmptyStr(rp_fileName))
        {
            return searchfilelst;
        }
        String pth = getCanonicalPath(".");
        List<String> fileLst = getFileDirectory(pth, TConst.TFileDirEnum.FILE_FLAG, true);
        if (TPubUtil.notNullAndEmptyCollection(fileLst))
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
        if (TStringUtil.nullOrEmptyStr(doxName))
        {
            return searchfilelst;
        }
        String pth = getCanonicalPath(".");
        List<String> fileLst = getFileDirectory(pth, TConst.TFileDirEnum.FILE_FLAG, true);
        if (TPubUtil.notNullAndEmptyCollection(fileLst))
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

    public static boolean setSpyConfigFile(String module, String id, String value)
    {
        if (nullOrEmptyStr(module) || nullOrEmptyStr(id) || nullOrEmptyStr(value))
        {
            return false;
        }

        boolean writen = true;
        OutputStream outputStream = null;
        XMLWriter xmlWriter = null;
        Document document;

        final String fileNameInput = "spy.par\\conf\\" + TConst.SPY_CONFIG_FILE;

        // 用系统路径分隔符号替换文件中的路径分隔符号
        Pair<String, Document> file2Doc = getXMLDocument(fileNameInput);
        if (file2Doc == null)
        {
            return false;
        }

        try
        {
            document = file2Doc.getValue();
            if (document != null)
            {
                Element root = document.getRootElement();
                List<Element> elementList = root.elements();

                boolean findmodel = false;
                for (Element ele : elementList)
                {
                    if (module.equals(ele.getName()))
                    {
                        findmodel = true;
                        boolean findid = false;
                        List<Element> elements = ele.elements();
                        if (notNullAndEmptyCollection(elements))
                        {
                            for (Element item : elements)
                            {
                                String itemName = item.getName();
                                if (id.equals(itemName))
                                {
                                    findid = true;
                                    item.setText(value);
                                    break;
                                }
                            }
                        }
                        if (!findid)
                        {
                            Element newElement = DocumentHelper.createElement(id);
                            newElement.setText(value);
                            ele.add(newElement);
                        }
                    }
                }
                if(!findmodel)
                {
                    Element newModel = DocumentHelper.createElement(module);
                    Element newElement = DocumentHelper.createElement(id);
                    newElement.setText(value);
                    newModel.add(newElement);
                    root.add(newModel);
                }
            }

            if (writen)
            {
                OutputFormat outputFormat = new OutputFormat();
                outputFormat.setEncoding("UTF-8");

                //outputStream = new FileOutputStream(rootPath);
                outputStream = new FileOutputStream(file2Doc.getKey());
                xmlWriter = new XMLWriter(outputStream, outputFormat);
                xmlWriter.write(document);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            xmlDocumentClose(xmlWriter, outputStream, null);
        }

        return writen;
    }

    private static void xmlDocumentClose(XMLWriter xmlWriter, OutputStream outputStream, InputStream inputStream)
    {
        if (xmlWriter != null)
        {
            try
            {
                xmlWriter.close();
            }
            catch (IOException e)
            {
            }
            xmlWriter = null;
        }

        if (outputStream != null)
        {
            try
            {
                outputStream.close();
            }
            catch (IOException e)
            {
            }
            outputStream = null;
        }

        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
            }
            inputStream = null;
        }
    }


    private static Pair<String, Document> getXMLDocument(final String fileNameInput)
    {
        String rp_fileName = TPubUtil.replaceToSysFileSeparator(fileNameInput);
        String sysSep = getSysFileSeparator();
        String filename = rp_fileName.contains(sysSep) ? rp_fileName : "conf" + sysSep + rp_fileName;

        List<String> fileLst = getProjectFileByName(filename);
        if (TPubUtil.notNullAndEmptyCollection(fileLst))
        {
            filename = fileLst.get(0);
        }
        else
        {
            return null;
        }

        //1.获取SAM接口：
        SAXReader saxReader = new SAXReader();
        //2.获取XML文件：
        Document doc = null;
        try
        {
            doc = saxReader.read(new File(filename));
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        return (doc != null) ? new Pair<>(filename, doc) : null;
    }

    public static Map<String, String> getSPYCONFIGContents()
    {
        final String fileNameInput = "spy.par\\conf\\" + TConst.SPY_CONFIG_FILE;
        Map<String, String> configMap = new HashMap<>();
        // 用系统路径分隔符号替换文件中的路径分隔符号
        Pair<String, Document> file2Doc = getXMLDocument(fileNameInput);
        if (file2Doc == null)
        {
            return configMap;
        }

        //3.获取根节点：
        Element root = file2Doc.getValue().getRootElement();

        //获取子节点
        Iterator<?> it = root.elementIterator();
        while (it.hasNext())
        {
            Element elem = (Element) it.next();
            //获取子节的子节点
            Iterator<?> ite = elem.elementIterator();
            while (ite.hasNext())
            {
                Element child = (Element) ite.next();
                String name = child.getName();
                String value = child.getStringValue();
                if (notNullAndEmptyStr(name) && notNullAndEmptyStr(value))
                {
                    configMap.put(name, value);
                }
            }
        }
        return configMap;
    }


    public static Map<String, String> getConfigI18n(final String i18nfile)
    {
        Map<String, String> i18nMap = new HashMap<>();
        String filename = "conf/" + i18nfile;
        List<String> fileLst = getProjectFileByName(filename);
        if (TPubUtil.notNullAndEmptyCollection(fileLst))
        {
            filename = fileLst.get(0);
        }
        else
        {
            return i18nMap;
        }

        //1.获取SAM接口：
        SAXReader saxReader = new SAXReader();
        //2.获取XML文件：
        Document doc = null;
        try
        {
            doc = saxReader.read(new File(filename));
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }

        //3.获取根节点：
        Element root = doc.getRootElement();

        //获取子节点
        Iterator<?> it = root.elementIterator();
        while (it.hasNext())
        {
            Element elem = (Element) it.next();
            //获取属性名属性值
            Attribute attr = elem.attribute(0);
            if ("language-country".equals(attr.getName()) && "zh_CN".equals(attr.getValue()))
            {
                //获取子节的子节点
                Iterator<?> ite = elem.elementIterator();
                while (ite.hasNext())
                {
                    Element child = (Element) ite.next();
                    String labelKey = child.attribute("label-key").getValue();
                    String labelVal = child.attribute("label-value").getValue();
                    if (notNullAndEmptyStr(labelKey))
                    {
                        i18nMap.put(labelKey, labelVal);
                    }
                }
            }
        }

        return i18nMap;
    }


}
