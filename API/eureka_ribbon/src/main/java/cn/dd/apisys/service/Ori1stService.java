package cn.dd.apisys.service;

import cn.dd.apisys.comm.entity.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * FileName: QHService.java
 * CreateTime: 2020/6/29 14:00.
 * Version: V1.0
 * Author: ChengTao
 * Description: 基于ribbon的Service服务类-一号线索池项目
 */
@Service
public class Ori1stService {
    @Autowired
    RestTemplate restTemplate;
    /**
     * 一号线索池-接口16(复写原接口1)
     */
    public JSONObject getEntInfoList(EntBaseInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntInfoList",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口17
     * 手机号精查接口
     */
    public JSONObject getEntInfoListByUserTel(PhoneEntEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntInfoListByUserTel",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口18
     * 手机号查询通话记录接口
     */
    public JSONObject getCallInfoByUserTel(CallRecordEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getCallInfoByUserTel",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口2
     * 营销管理-企业详情-企业基本信息(多表)
     */
    public JSONObject getEntInfo(EntBaseInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntInfo",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口10
     * 营销管理-企业详情-联系人信息
     */
    public JSONObject getContactInfomations(EntContactInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getContactInfomations",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口15
     * 营销管理-企业详情-按名称精确查询企业基本信息
     */
    public JSONObject getEntInfoByEntName(EntBaseInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntInfoByEntName",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口3
     * 营销管理-企业详情-按名称精确查询企业基本信息
     */
    public JSONObject getEntNetPopularization(EntNetPopularizationEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntNetPopularization",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口4
     * 营销管理-企业详情-招聘信息列表(单表)
     */
    public JSONObject getEntAdvertiseNotice(EntAdvertiseNoticeEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntAdvertiseNotice",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口5
     * 营销管理-企业详情-企业公众号信息列表（单表）
     */
    public JSONObject getEntSubscriptions(EntSubscriptionsEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntSubscriptions",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口6
     * 营销管理-企业详情-新闻舆情-新闻动态信息列表
     */
    public JSONObject getEntPublicNews(EntPublicNewsEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntPublicNews",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口7
     * 营销管理-企业详情-知识产权-专利信息列表
     */
    public JSONObject getEntPatentInfo(EntPatentInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntPatentInfo",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口8
     * 营销管理-企业详情-知识产权-商标信息列表
     */
    public JSONObject getEntTrademarkInfo(EntTrademarkInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntTrademarkInfo",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口9
     * 营销管理-企业详情-网站信息列表
     */
    public JSONObject getEntWebsiteInfo(EntWebsiteInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntWebsiteInfo",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口11
     * 营销管理-营销管理-企业详情-招标投标
     */
    public JSONObject getEntBidInfo(EntBidInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntBidInfo",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口12
     * 营销管理-企业详情-企业异常名录
     */
    public JSONObject getEntAbnormalOperInfo(EntBidInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntAbnormalOperInfo",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口13
     * 营销管理-企业详情-相关企业-对外投资列表
     */
    public JSONObject getEntInvestInfo(EntInvestInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntInvestInfo",reqData,JSONObject.class);
    }
    /**
     * 一号线索池-接口14
     * 营销管理-企业详情-相关企业-股东信息
     */
    public JSONObject getEntShareholderInfo(EntShareholderInfoEntity reqData) {
        //System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/cluesPoolRest/getEntShareholderInfo",reqData,JSONObject.class);
    }

}
