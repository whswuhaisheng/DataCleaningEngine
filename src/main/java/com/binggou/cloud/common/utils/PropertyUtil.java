package com.binggou.cloud.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class PropertyUtil {

    private static Properties pro;
    private static InputStream in;

    static {
        try {
            pro = new Properties();
            in = PropertyUtil.class.getResourceAsStream("/compar.properties");
            pro.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("reloadableConfig  properties file not find ");
        } catch (IOException e) {
            System.out.println("reloadableConfig initial faile");
        } catch (Exception e) {
            System.out.println("property file not exits");
        }

    }



    public static String getStringProperty(String key) {
        if (pro == null) {
            return null;
        }
        String value = pro.getProperty(key);
        return value;
    }
    public static Integer getIntProperty(String key){
        if (pro == null) {
            return null;
        }
        String value = pro.getProperty(key);
        if(StringUtils.isNumeric(value)){
            return Integer.parseInt(value);
        }
        return null;
    }

    public static List<String> getPropertyNames(){
        if (pro == null) {
            return null;
        }
        Iterator<String> iterator = pro.stringPropertyNames().iterator();
        List<String> list = new ArrayList<String>();
        while(iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }

}
