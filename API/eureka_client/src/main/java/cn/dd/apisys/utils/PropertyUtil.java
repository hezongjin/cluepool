package cn.dd.apisys.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * FileName: PropertyUtil.java
 * CreateTime: 2020/6/3 17:16.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class PropertyUtil {

    /**
     * 读取数据
     *
     * @param propertyName
     * @param key
     * @return
     */
    public static String getValueByKey(String propertyName, String key) {
        String result = "";
        try {
            Properties prop = new Properties();
            InputStream inputstream = null;
            ClassLoader cl = PropertyUtil.class.getClassLoader();
            if (cl != null)
                inputstream = cl.getResourceAsStream(propertyName);
            else
                inputstream = ClassLoader.getSystemResourceAsStream(propertyName);

            if (inputstream == null)
                throw new Exception("inputstream " + propertyName + " open null");

            prop.load(inputstream);
            inputstream.close();
            inputstream = null;
            result = prop.getProperty(key);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}