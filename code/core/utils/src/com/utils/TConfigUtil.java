package com.utils;


import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.xpath.DefaultXPath;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by caiyong on 2017/3/11.
 */
public class TConfigUtil
{

    public static void main(String[] args) throws Exception
    {
          readdom4j();
        //  parseMethod1();
      //  parseMethod2();
        // generate();
    }


    private static List<String> parseMethod1() throws DocumentException
    {
        List<String> result = new ArrayList<String>();

        SAXReader reader = new SAXReader();
        Document document = reader.read("C:\\GitHubProj\\TWS\\code\\core\\spy\\conf\\target.xml");
        XPath xPath = new DefaultXPath("/resources/product[@name='QQ']/account[@id='987654321']/password");
        List<Element> list = xPath.selectNodes(document.getRootElement());
        for (Element element : list)
        {
            System.out.println(element.getTextTrim());
            result.add(element.getTextTrim());
        }
        return result;
    }

    private static List<String> parseMethod2() throws DocumentException
    {
        List<String> result = new ArrayList<String>();

        SAXReader reader = new SAXReader();
        Document document = reader.read("C:\\GitHubProj\\TWS\\code\\core\\spy\\conf\\target.xml");
        List<Element> products = document.getRootElement().selectNodes("/resources/product");
        Iterator iterator = products.iterator();
        while (iterator.hasNext())
        {
            Element product = (Element) iterator.next();
            String name = product.attributeValue("name");
            System.out.println(name);
        }
        return result;
    }

    private static void generate()
    {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("resources");

        Element product = root.addElement("product");
        product.addAttribute("name", "QQ");

        Element account = product.addElement("account");
        account.addAttribute("id", "123456789");

        Element nickname = account.addElement("nickname");
        nickname.setText("QQ-account-1");

        Element password = account.addElement("password");
        password.setText("123asd21qda");

        Element level = account.addElement("level");
        level.setText("56");

        PrintWriter pWriter = null;
        XMLWriter xWriter = null;
        try
        {
            pWriter = new PrintWriter("C:\\GitHubProj\\TWS\\code\\core\\spy\\conf\\generate.xml");
            xWriter = new XMLWriter(pWriter);
            xWriter.write(doc);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                xWriter.flush();
                xWriter.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void readdom4j() throws DocumentException
    {
        //1.获取SAM接口：
        SAXReader saxReader = new SAXReader();
        //2.获取XML文件：
        Document doc = saxReader.read("C:\\GitHubProj\\TWS\\code\\core\\spy\\conf\\config-i18n.xml");
        //3.获取根节点：
        Element root = doc.getRootElement();
        System.out.println("根节点: " + root.getName());
        System.out.println("----------------");

        //获取子节点
        Iterator<?> it = root.elementIterator();
        while (it.hasNext())
        {
            Element elem = (Element) it.next();
            //获取属性名属性值
            List<Attribute> li = elem.attributes();
            for (Attribute att : li)
            {
                System.out.println(att.getName() + "  " + att.getValue());
            }

            //获取子节的子节点
            Iterator<?> ite = elem.elementIterator();
            while (ite.hasNext())
            {
                Element child = (Element) ite.next();
                System.out.println(child.getName() + "  " + child.getStringValue());
            }

            System.out.println("----------------");
        }
    }


}
