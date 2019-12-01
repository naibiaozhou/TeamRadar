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
        XmlSerializer serializer =Xml.newSerializer();//���л�  
        serializer.setOutput(write);//�����  
        serializer.startDocument("UTF-8", true);//��ʼ�ĵ�  
        serializer.startTag(null, "persons");  
        //ѭ��ȥ���person  
        for (Person person : list) {  
            serializer.startTag(null, "person");  
            serializer.attribute(null, "id", person.getId().toString());//����id���Լ�����ֵ  
            serializer.startTag(null, "name");  
            serializer.text(person.getName());//�ı��ڵ���ı�ֵ--name  
            serializer.endTag(null, "name");  
            serializer.startTag(null, "subscription");  
            serializer.text(person.getSubscription());//�ı��ڵ���ı�ֵ--age  
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
       int type =parser.getEventType();//������һ���¼�  
       //ֻҪ��ǰ�¼����Ͳ��ǡ������ĵ�������ȥѭ��  
       while(type!=XmlPullParser.END_DOCUMENT)  
       {  
       switch (type) {  
       case XmlPullParser.START_DOCUMENT:  
       list =  new ArrayList<Person>();  
           break;  
 
       case XmlPullParser.START_TAG:  
           String name=parser.getName();//��ȡ��������ǰָ���Ԫ������  
           if("person".equals(name))  
           {  
               person =new Person();  
               person.setId(new Integer(parser.getAttributeValue(0)));  
           }  
           if(person!=null)  
           {  
               if("name".equals(name))  
               {  
                   person.setName(parser.nextText());//��ȡ��������ǰָ���Ԫ�ص���һ���ı��ڵ���ı�ֵ  
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
       type=parser.next();//���ǧ�������Ŷ  
       }  
        return list;  
    }  
}
