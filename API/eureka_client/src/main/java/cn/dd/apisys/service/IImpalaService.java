package cn.dd.apisys.service;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * FileName: IImpalaService.java
 * CreateTime: 2020/7/20 16:16.
 * Version: V1.0
 * Author: ChengTao
 * Description: Impala查询接口
 */
public interface IImpalaService {

    /**
     * 根据租户ID及检索日期范围及检索粒度进行地区分布数据查询
     * */
    HashMap<String,Object> getImpalaDataForAreaAnalysis(String tenantId,String startDate,String stopDate,String provinceName);


}
