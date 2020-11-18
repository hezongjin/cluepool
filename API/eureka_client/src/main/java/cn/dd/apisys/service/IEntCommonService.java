package cn.dd.apisys.service;

import cn.dd.apisys.entity.EntCommonEntity;
import cn.dd.apisys.entity.ResultCommon;

import java.util.HashMap;

/**
 * FileName: IEntCommonService.java
 * CreateTime: 2020/6/3 17:06.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public interface IEntCommonService {
    //普通查询
    ResultCommon search(EntCommonEntity dto);
    //含带关系比较的查询(字符串范围)
    ResultCommon searchGt(EntCommonEntity dto, HashMap<String,String> gtCase);
    //根据ID集合检索数据
    ResultCommon searchReal(EntCommonEntity dto, HashMap<String,Object> realCase);

}
