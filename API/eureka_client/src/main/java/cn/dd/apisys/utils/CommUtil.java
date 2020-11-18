package cn.dd.apisys.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * FileName: CommUtil.java
 * CreateTime: 2020/6/3 17:01.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class CommUtil {
    /**
     * 截取集合
     *
     * @param list      原集合
     * @param fromIndex 开始index
     * @param toIndex   结束index
     * @param suffix    值后缀,无需时传NULL
     * @return
     */
    public static List<Object> truncateList(List<Object> list, int fromIndex, int toIndex, String suffix, boolean deleteSuffix) {
        if (list == null)
            return null;

        int size = list.size();
        if (fromIndex >= size || toIndex > size)
            return null;

        //
        List<Object> newList = new ArrayList<Object>();
        String str = null;
        int len = StringUtils.isEmpty(suffix) ? 0 : suffix.length();
        for (int i = fromIndex; i < toIndex; i++) {
            if (len > 0) {
                str = list.get(i).toString();
                if (str.endsWith(suffix)) {
                    if (deleteSuffix)
                        newList.add(str.substring(0, str.length() - len));
                    else
                        newList.add(list.get(i));
                }
            } else {
                newList.add(list.get(i));
            }
        }

        return newList;
    }

    /**
     * 集合是否有效
     *
     * @param list
     * @return
     */
    public static <T> boolean valid(Collection<T> list) {
        if (list != null && list.size() > 0)
            return true;
        else
            return false;
    }

    /**
     * 按顺序合并字符串
     *
     * @param str1
     * @param str2
     * @param separator 分隔符
     * @return
     */
    public static String combine(String str1, String str2, String separator) {
        if (str1 == null)
            str1 = "";
        if (str2 == null)
            str2 = "";

        if (str1.length() > 0 && str2.length() > 0)
            return str1 + separator + str2;
        else if (str1.length() > 0 || str2.length() > 0)
            return str1 + str2;
        else
            return "";
    }

    /**
     * @param str
     * @param addNew   在原分隔符前添加新的字符串
     * @param toAndold 原分隔符
     * @return
     */
    public static String repalceAndAdd(String str, String addNew, String toAndold) {
        if (str == null || str.length() == 0)
            return "";
        if (str.indexOf(toAndold) > 0)
            return str.replace(toAndold, addNew + toAndold);
        else
            return str + addNew;
    }

    /**
     * 转str
     *
     * @param obj
     * @param defaultStr null时的值
     * @return
     */
    public static String toString(Object obj, String defaultStr) {
        if (obj == null)
            return defaultStr;

        return obj.toString();
    }

}
