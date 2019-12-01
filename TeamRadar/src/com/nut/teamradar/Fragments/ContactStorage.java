package com.nut.teamradar.Fragments;

import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.nut.teamradar.model.Person;

public class ContactStorage {
    public static void saveXML(List<Person> list,Writer write)throws Throwable  
    {  
        XmlSerializer serializer =Xml.newSerializer();//序列化  
        serializer.setOutput(write);//输出流  
        serializer.startDocument("UTF-8", true);//开始文档  
        serializer.startTag(null, "persons");  
        //循环去添加person  
        for (Person person : list) {  
            serializer.startTag(null, "person");  
            serializer.attribute(null, "id", person.getId().toString());//设置id属性及属性值  
            serializer.startTag(null, "name");  
            serializer.text(person.getName());//文本节点的文本值--name  
            serializer.endTag(null, "name");  
            serializer.startTag(null, "subscription");  
            serializer.text(person.getSubscription());//文本节点的文本值--age  
            serializer.endTag(null, "subscription");  
            serializer.endTag(null, "person");  
        }  
        serializer.endTag(null, "persons");  
        serializer.endDocument();  
        write.flush();  
        write.close();  
    }
    public List<Person> getPersons(InputStream stream) throws Throwable  
    {  
        List<Person> list =null;  
       Person person =null;  
       XmlPullParser parser =Xml.newPullParser();  
       parser.setInput(stream,"UTF-8");  
       int type =parser.getEventType();//产生第一个事件  
       //只要当前事件类型不是”结束文档“，就去循环  
       while(type!=XmlPullParser.END_DOCUMENT)  
       {  
       switch (type) {  
       case XmlPullParser.START_DOCUMENT:  
       list =  new ArrayList<Person>();  
           break;  
 
       case XmlPullParser.START_TAG:  
           String name=parser.getName();//获取解析器当前指向的元素名称  
           if("person".equals(name))  
           {  
               person =new Person();  
               person.setId(new Integer(parser.getAttributeValue(0)));  
           }  
           if(person!=null)  
           {  
               if("name".equals(name))  
               {  
                   person.setName(parser.nextText());//获取解析器当前指向的元素的下一个文本节点的文本值  
               }  
               if("subscription".equals(name))  
               {  
                   person.setSubscription(parser.nextText());  
               }  
           }  
           break;  
       case XmlPullParser.END_TAG:  
           if("person".equals(parser.getName()))  
           {  
               list.add(person);  
               person=null;  
           }  
           break;  
       }  
       type=parser.next();//这句千万别忘了哦  
       }  
        return list;  
    }  
}
