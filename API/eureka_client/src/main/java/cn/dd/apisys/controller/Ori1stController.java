package cn.dd.apisys.controller;

import cn.dd.apisys.entity.*;
import cn.dd.apisys.mapper.CallCenterRecordMapper;
import cn.dd.apisys.model.CallCenterRecord;
import cn.dd.apisys.service.IEntCommonService;
import cn.dd.apisys.service.impl.EntCommonServiceImpl;
import cn.dd.apisys.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FileName: Ori1stController.java
 * CreateTime: 2020/6/3 14:57.
 * Version: V1.0
 * Author: ChengTao
 * Description: 一号线索池API接口
 */
@Controller
@RequestMapping(value = "/cluesPoolRest")
public class Ori1stController {
    private static ResultUtil ru = new ResultUtil(EntCommonServiceImpl.class);
    @Autowired
    private IEntCommonService entCommonService;

    @Autowired
    CallCenterRecordMapper callCenterRecordMapper;

    /**
     * 一号线索池-接口16
     * 获取企业列表信息接口
     * 开发日期：2020-06-03
     * 更新日期：2020-06-03
     */
    @ResponseBody
    @PostMapping(value = "/getEntInfoList")
    public JSONObject getEntInfoList(@RequestBody EntBaseInfoEntity reqData) {
        //System.out.println("+---" + JSON.toJSONString(reqData));
        TimeUse timeUse = new TimeUse();
        Map<String, Object> respResErr = new HashMap<>();

        if (reqData == null) {
            //System.out.println("请求参数为空");
            respResErr.put("msg", "请求参数为空");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }


        if (reqData.getPageNum() <= 0 || reqData.getPageSize() <= 0) {
            //System.out.println("起始页/每页记录数必须大于0");
            respResErr.put("msg", "起始页/每页记录数必须大于0");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }

        if (StringUtils.isEmpty(reqData.getTableHeadList())) {
            //System.out.println("请求参数[tableHeadList]必须非空");
            respResErr.put("msg", "请求参数[tableHeadList]必须非空");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }
        if (reqData.getLabelList() == null)
            reqData.setLabelList(new ArrayList<Map<String, String>>());

        /**
         * tableHeadList 是要返回的字段值
         * labelList 里面是增加要查询的条件
         */
        //增加参数判断,增加默认的条件
        if (reqData.getLabelList() != null) {
            if ("1".equals(reqData.getContactInfoSign())) {
                Map<String, String> map = new HashMap<>();
                map.put("labelKey", "dtc_101.t1");
                map.put("labelValue", "1");
                reqData.getLabelList().add(map);
                Map<String, String> map2 = new HashMap<>();
                map2.put("labelKey", "dtc_101.t2");
                map2.put("labelValue", "1");
                reqData.getLabelList().add(map2);
            }
        }


        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(EsUtil.INDEX, "ent_base_info");
        dataMap.put(EsUtil.FETCH, reqData.getTableHeadList());
        //dataMap.put(EsUtil.FETCH, "ent_id,ent_name,cnei_code,cnei_sec_code,province_code,city_code,county_code");
        dataMap.put(EsUtil.SORT, EsUtil.SEP);
        dataMap.put("ent_id", reqData.getEnt_id());
        dataMap.put("cnei_code", reqData.getCneiCodeList());
        dataMap.put("cnei_sec_code", reqData.getCneiSecCodeList());
        dataMap.put("reg_status_code", "1");
        dataMap.put("ent_protect_status", "0");

        HashMap<String, String> gtCase = new HashMap<>();
        gtCase.put("key_lastModify", "last_modify_time");
        gtCase.put("value_lastModify", TimeUse.getDateStr());
        if (reqData.getLastModifyTime() != null) {
            gtCase.put("value_lastModify", reqData.getLastModifyTime().trim() + " 00:00:00");
        }
        //注册时间
        if (reqData.getEstimateDate() != null && reqData.getEstimateDate() != "") {
            gtCase.put("key_estimate_date", "estimate_date");
            gtCase.put("value_estimate_date", reqData.getEstimateDate().trim());
        }

        //是否过滤区域编码（2020-11-10天极爱）
        gtCase.put("filterArea", "ON");
        if (reqData.getFilterArea() != null) {
            if (!reqData.getFilterArea().equals("0")) {
                gtCase.put("filterArea", "OFF");
            }

        }

        // dataMap.put("ent_tel,ent_mobile", EsUtil.NOT_NULL);
        if (StringUtils.isNotEmpty(reqData.getEnt_name())) { // 精确or模糊
            if (reqData.getEnt_name().charAt(0) != '*') {
                dataMap.put("ent_name2", reqData.getEnt_name());
            } else {
                dataMap.put("ent_name2", reqData.getEnt_name() + "*");
            }
        }

        // BOOST | 511321 | 511322 | BOOST | 511000 | 511001
        // 专属、共享(只有一个不为空时就可以合并为一个条件进行查询)
        String ex = CommUtil.combine(reqData.getExclusiveAreaCodeList(), CommUtil.repalceAndAdd(reqData.getExclusiveCityCodeList(), EsUtil.CITYCODE_SUFFIX, EsUtil.SEP), EsUtil.SEP);
        String sh = CommUtil.combine(reqData.getShareAreaCodeList(), CommUtil.repalceAndAdd(reqData.getShareCityCodeList(), EsUtil.CITYCODE_SUFFIX, EsUtil.SEP), EsUtil.SEP);
        if (StringUtils.isNotEmpty(ex) && StringUtils.isNotEmpty(sh)) {
            dataMap.put("county_code,city_code", String.format("%s|%s|%s|%s", EsUtil.BOOST, ex, EsUtil.BOOST, sh));
        } else {
            if (ex.length() == 0)
                ex = sh;
            if (ex.length() > 0)
                dataMap.put("county_code,city_code", String.format("%s|%s|%s|%s", EsUtil.BOOST, ex, EsUtil.BOOST, ""));
        }

        //
        if (CommUtil.valid(reqData.getLabelList())) {
            // 且用,表示 ; 或用|表示
            // 针对联系方式的特殊处理,把整体or的关系放在最前面,专为联系方式用
            String k = null;
            String v = null;
            for (Map<String, String> labelMap : reqData.getLabelList()) {
                k = labelMap.get("labelKey");
                v = labelMap.getOrDefault("labelValue", "");
                if (EsUtil.specialList.contains(k))
                    k = "OR_" + k;

                // 实际上针对一个字段只有或的关系,默认或
                if (!labelMap.getOrDefault("labelRelation", "or").equals("and"))
                    dataMap.put(k, v.replace(",", EsUtil.SEP));
                else
                    dataMap.put(k, v);
            }
        }

        EntCommonEntity newDto = new EntCommonEntity();
        newDto.setPageNum(reqData.getPageNum());
        newDto.setPageSize(reqData.getPageSize());
        newDto.setData((JSONObject) JSON.toJSON(dataMap));
        System.out.println("1111" + JSON.toJSONString(newDto));
        System.out.println("2222" + JSON.toJSONString(gtCase));

        /*
        try {
            System.out.println("###########" + JSON.toJSONString(gtCase) + "#############" + JSON.toJSONString(entCommonService.searchGt(newDto, gtCase)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        //获取搜索结果
        ResultCommon result = entCommonService.searchGt(newDto, gtCase);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", result.getList());
        resMap.put("pageNum", newDto.getPageNum());
        resMap.put("pageSize", newDto.getPageSize());
        resMap.put("total", newDto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", result.getMsg());
        respRes.put("code", result.getResult_code());
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntInfoList]");
        return (JSONObject) JSON.toJSON(respRes);
        //return ru.common(result.getResult_code(), result.getMsg(), timeUse.end(), resMap);
    }

    /**
     * 一号线索池-接口17
     * 手机号精查接口
     * 开发日期：2020-06-03
     * 更新日期：2020-06-03
     */
    @ResponseBody
    @PostMapping(value = "/getEntInfoListByUserTel")
    public JSONObject getEntInfoListByUserTel(@RequestBody PhoneEntEntity reqData) {
        TimeUse timeUse = new TimeUse();
        Map<String, Object> respResErr = new HashMap<>();
        //接参条件判断
        if (reqData == null) {
            //System.out.println("请求参数为空");
            respResErr.put("msg", "请求参数为空");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }
        if (reqData.getPageNum() <= 0 || reqData.getPageSize() <= 0) {
            //System.out.println("起始页/每页记录数必须大于0");
            respResErr.put("msg", "起始页/每页记录数必须大于0");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }
        if (StringUtils.isEmpty(reqData.getPhoneCode())) {
            //System.out.println("请求参数[phoneCode]必须非空");
            respResErr.put("msg", "请求参数[phoneCode]必须非空");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }
        if (StringUtils.isEmpty(reqData.getTableHeadList())) {
            //System.out.println("请求参数[tableHeadList]必须非空");
            respResErr.put("msg", "请求参数[tableHeadList]必须非空");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }
        String phoneCode = reqData.getPhoneCode();
        //通过手机号检索ContactID
        //拼装检索条件
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(EsUtil.INDEX, "ent_contact_infomation");
        dataMap.put(EsUtil.FETCH, "contact_id");
        dataMap.put("contact_val", phoneCode);
        EntCommonEntity newDto = new EntCommonEntity();
        newDto.setData((JSONObject) JSON.toJSON(dataMap));
        //System.out.println(JSON.toJSONString(newDto));
        /*
        try {
            System.out.println("########################" + JSON.toJSONString(entCommonService.search(newDto)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        //获取搜索结果
        ResultCommon result = entCommonService.search(newDto);
        //System.out.println("[0]"+JSON.toJSONString(result));

        //获取ContactId列表
        ArrayList<String> contactIds = new ArrayList<>();
        String contactIdStr = "";
        for (int i = 0; i < result.getList().size(); i++) {
            contactIds.add(result.getList().get(i).get("contact_id").toString());
            contactIdStr += "," + result.getList().get(i).get("contact_id").toString();
        }
        //System.out.println("[contactIds] " + JSON.toJSONString(contactIds));
        //通过ContactId获取EntID
        HashMap<String, Object> realCaseC2E = new HashMap<>();
        realCaseC2E.put("real_ori", "contact_id");
        realCaseC2E.put("real_ord", "ent_id");
        realCaseC2E.put("real_ori_val", contactIds);
        Map<String, Object> dataMapContact = new HashMap<>();
        dataMapContact.put(EsUtil.INDEX, "ent_contact_info");
        dataMapContact.put(EsUtil.FETCH, "ent_id,contact_name");
        //dataMap.put(EsUtil.FETCH, "ent_id,ent_name,cnei_code,cnei_sec_code,province_code,city_code,county_code");
        dataMapContact.put(EsUtil.SORT, EsUtil.SEP);
        //dataMapContact.put("contact_id", contactIds);
        newDto = new EntCommonEntity();
        newDto.setPageNum(reqData.getPageNum());
        newDto.setPageSize(reqData.getPageSize());
        newDto.setData((JSONObject) JSON.toJSON(dataMapContact));
        //System.out.println(JSON.toJSONString(newDto));
        //获取搜索结果
        result = entCommonService.searchReal(newDto, realCaseC2E);
        HashMap<String, String> contactMap = new HashMap<>();
        for (int i = 0; i < result.getList().size(); i++) {
            contactMap.put(result.getList().get(i).get("ent_id").toString(), result.getList().get(i).get("contact_name").toString());
        }
        //System.out.println("[1]"+JSON.toJSONString(contactMap));

        //获取企业信息列表
        ArrayList<String> entIds = new ArrayList<>();
        for (int i = 0; i < result.getList().size(); i++) {
            entIds.add(result.getList().get(i).get("ent_id").toString());
        }
        HashMap<String, Object> realCaseE2I = new HashMap<>();
        realCaseE2I.put("real_ori", "ent_id");
        realCaseE2I.put("real_ori_val", entIds);
        Map<String, Object> dataMapEnt = new HashMap<>();
        dataMapEnt.put(EsUtil.INDEX, "ent_base_info");
        dataMapEnt.put(EsUtil.FETCH, reqData.getTableHeadList());
        //dataMap.put(EsUtil.FETCH, "ent_id,ent_name,cnei_code,cnei_sec_code,province_code,city_code,county_code");
        dataMapEnt.put(EsUtil.SORT, EsUtil.SEP);
        newDto = new EntCommonEntity();
        newDto.setPageNum(reqData.getPageNum());
        newDto.setPageSize(reqData.getPageSize());
        newDto.setData((JSONObject) JSON.toJSON(dataMapEnt));
        //System.out.println("[WWWWW]" + JSON.toJSONString(newDto));
        result = entCommonService.searchReal(newDto, realCaseE2I);
        for (int i = 0; i < result.getList().size(); i++) {
            result.getList().get(i).put("contact_name", contactMap.get(result.getList().get(i).get("ent_id")));
            result.getList().get(i).put("contact_val", phoneCode);
        }
        HashMap<String, Object> endRes = new HashMap<>();
        endRes.put("dataList", result.getList());
        endRes.put("pageSize", 100);
        endRes.put("total", result.getList().size());
        endRes.put("pageNum", 1);
        String backRes = phoneCode;
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", result.getMsg());
        respRes.put("code", result.getResult_code());
        respRes.put("data", endRes);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntInfoListByUserTel]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口18
     * 手机号查询通话记录接口
     * 开发日期：2020-06-03
     * 更新日期：2020-06-03
     */
    @ResponseBody
    @PostMapping(value = "/getCallInfoByUserTel")
    public JSONObject getCallInfoByUserTel(@RequestBody CallRecordEntity reqData) {
        TimeUse timeUse = new TimeUse();
        Map<String, Object> respResErr = new HashMap<>();
        //接参条件判断
        if (reqData == null) {
            //System.out.println("请求参数为空");
            respResErr.put("msg", "请求参数为空");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }
        if (StringUtils.isEmpty(reqData.getPhoneNum())) {
            //System.out.println("请求参数[phoneNum]必须非空");
            respResErr.put("msg", "请求参数[phoneNum]必须非空");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }
        HashMap<String, Object> backRes = new HashMap<>();
        backRes.put("msg", "success(请求失败)");
        backRes.put("code", CluesPoolConstant.RESULT_CODE.ERROR_);
        try {
            List<CallCenterRecord> res = callCenterRecordMapper.selectByDomain(reqData.getPhoneNum());
            backRes.put("msg", "success(请求成功)");
            backRes.put("code", CluesPoolConstant.RESULT_CODE.SUCCESS_);
            //System.out.println("WWWWW"+JSON.toJSONString(res));
            if (res.size() > 0) {
                HashMap<String, Object> endRes = new HashMap<>();
                endRes.put("callTimes", res.get(0).getCallTimes());
                endRes.put("phoneNum", res.get(0).getPhoneNum());
                endRes.put("connectedTimes", res.get(0).getConnectedTimes());
                List kkk = JSON.parseArray(res.get(0).getCallContext());
                endRes.put("callContext", kkk);
                backRes.put("data", endRes);
            } else {
                backRes.put("data", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("WWWWW"+JSON.toJSONString(backRes));

        backRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getCallInfoByUserTel]");
        return (JSONObject) JSON.toJSON(backRes);
    }

    /**
     * 一号线索池-接口2
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-企业基本信息(多表)
     * @author ZhangHao
     * @date 2019/6/17 13:04
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntInfo")
    public JSONObject getEntInfo(@RequestBody EntBaseInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        //dto转成json,json装成jsonObject
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        jsonObject.put("_index", "ent_base_info");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果，拿到分页后的企业
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }


        EntCommonEntity cdto2 = new EntCommonEntity();
        //查询全部缺省1
        cdto2.setPageNum(1);
        //查询全部缺省100w
        cdto2.setPageSize(10000);

        //遍历分页后的企业，根据企业ID拿到 企业行政许可信息集合、企业信息变更信息集合、企业开户许可证信息
        for (int i = 0; i < ja.size(); i++) {
            //获取当前循环的对象
            JSONObject wjo = (JSONObject) ja.get(i);
            jsonObject = new JSONObject();
            jsonObject.put("ent_id", wjo.get("ent_id"));

            //获取企业行政许可信息集合，加入JSON
            jsonObject.put("_index", "ent_admin_license");
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon entAdminLicenseList = entCommonService.search(cdto2);

            JSONArray ea = new JSONArray(entAdminLicenseList.getList().size());
            for (Map<String, Object> map : entAdminLicenseList.getList()) {
                ea.add(JSON.toJSON(map));
            }
            wjo.put("entAdminLicenseList", ea);

            //获取企业信息变更信息集合，加入JSON
            jsonObject.put("_index", "ent_alterations");
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon entAlterationsList = entCommonService.search(cdto2);

            JSONArray la = new JSONArray(entAlterationsList.getList().size());
            for (Map<String, Object> map : entAlterationsList.getList()) {
                la.add(JSON.toJSON(map));
            }
            wjo.put("entAlterationsList", la);

            //获取企业开户许可证信息集合,加入JSON
            jsonObject.put("_index", "ent_account_license");
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon entAccountLicenseList = entCommonService.search(cdto2);
            JSONArray na = new JSONArray(entAccountLicenseList.getList().size());
            for (Map<String, Object> map : entAccountLicenseList.getList()) {
                na.add(JSON.toJSON(map));
            }
            wjo.put("entAccountLicenseList", na);
        }

        // 组装数据
        /*
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
*/
        //返回数据
        //return ru.success("request successful", timeUse.end(), resMap);

        //获取搜索结果
        //ResultCommon result = entCommonService.searchGt(newDto, gtCase);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntInfo]");
        return (JSONObject) JSON.toJSON(respRes);


    }

    /**
     * 一号线索池-接口10
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-企业基本信息(多表)
     * @author ZhangHao
     * @date 2019/6/17 13:04
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getContactInfomations")

    public JSONObject getContactInfomations(@RequestBody EntContactInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        //System.out.println("[W]"+JSON.toJSONString(dto));
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_contact_info");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);
        //System.out.println("[Q]"+JSON.toJSONString(resultList));
        List<Map<String, Object>> resultList2 = new ArrayList<>();
        JSONArray ja = new JSONArray(resultList.getList().size());
        //这里返回的数据key中有空格，暂时根据代码进行过滤
        for (Map<String, Object> map : resultList.getList()) {
            Map<String, Object> map2 = new HashMap<>();
            for (String key : map.keySet()) {
                map2.put(key.trim(), map.get(key));
            }
            resultList2.add(map2);
        }
        for (Map<String, Object> map : resultList2) {
            ja.add(JSON.toJSON(map));
        }

        EntCommonEntity cdto2 = new EntCommonEntity();
        cdto2.setPageNum(1);
        cdto2.setPageSize(10000);

        //获取联系方式集合
        for (int i = 0; i < ja.size(); i++) {
            //获取当前循环的对象
            JSONObject wjo = (JSONObject) ja.get(i);
            jsonObject = new JSONObject();
            jsonObject.put("contact_id", wjo.get("contact_id"));
            //获取联系方式集合
            jsonObject.put("_index", "ent_contact_infomation");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            cdto2.setPageNum(dto.getPageNum());
            cdto2.setPageSize(dto.getPageSize());
            //获取搜索结果
            ResultCommon contactInfomationsList = entCommonService.search(cdto2);

            JSONArray ca = new JSONArray(contactInfomationsList.getList().size());
            for (Map<String, Object> map : contactInfomationsList.getList()) {
                ca.add(JSON.toJSON(map));
            }
            wjo.put("contactInfomationsList", ca);
            //获取证件信息集合
            jsonObject.put("_index", "license_info");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            cdto2.setPageNum(dto.getPageNum());
            cdto2.setPageSize(dto.getPageSize());
            //获取搜索结果
            ResultCommon licenseInfomationsList = entCommonService.search(cdto2);
            JSONArray la = new JSONArray(licenseInfomationsList.getList().size());
            for (Map<String, Object> map : licenseInfomationsList.getList()) {
                la.add(JSON.toJSON(map));
            }
            wjo.put("licenseInfomationsList", la);
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getContactInfomations]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口15
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-按名称精确查询企业基本信息
     * @author ZhangHao
     * @date 2019/6/17 13:04
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntInfoByEntName")
    public JSONObject getEntInfoByEntName(@RequestBody EntBaseInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        Map<String, Object> respResErr = new HashMap<>();
        //接参条件判断
        if (dto == null) {
            //System.out.println("请求参数为空");
            respResErr.put("msg", "请求参数为空");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }
        if (StringUtils.isEmpty(dto.getEnt_name())) {
            //System.out.println("请求参数[phoneNum]必须非空");
            respResErr.put("msg", "请求参数[ent_name]必须非空");
            respResErr.put("code", 502);
            respResErr.put("useTime", timeUse.end());
            return (JSONObject) JSON.toJSON(respResErr);
        }

        EntCommonEntity cdto = new EntCommonEntity();
        EntBaseInfoEntity dto2 = new EntBaseInfoEntity();
        dto2.setEnt_name2(dto.getEnt_name());
        //dto转成json
        String jsonString = JSONObject.toJSONString(dto2);
        //json装成jsonObject
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_base_info");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        //只查询一条
        cdto.setPageNum(1);
        cdto.setPageSize(1);
        //获取搜索结果，拿到分页后的企业
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        EntCommonEntity cdto2 = new EntCommonEntity();
        //查询全部缺省1
        cdto2.setPageNum(1);
        //查询全部缺省100w
        cdto2.setPageSize(10000);

        //遍历分页后的企业，根据企业ID拿到 企业行政许可信息集合、企业信息变更信息集合、企业开户许可证信息
        for (int i = 0; i < ja.size(); i++) {
            //获取当前循环的对象
            JSONObject wjo = (JSONObject) ja.get(i);
            jsonObject = new JSONObject();
            jsonObject.put("ent_id", wjo.get("ent_id"));

            //获取企业行政许可信息集合，加入JSON
            jsonObject.put("_index", "ent_admin_license");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon entAdminLicenseList = entCommonService.search(cdto2);

            JSONArray ea = new JSONArray(entAdminLicenseList.getList().size());
            for (Map<String, Object> map : entAdminLicenseList.getList()) {
                ea.add(JSON.toJSON(map));
            }
            wjo.put("entAdminLicenseList", ea);

            //获取企业信息变更信息集合，加入JSON
            jsonObject.put("_index", "ent_alterations");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon entAlterationsList = entCommonService.search(cdto2);

            JSONArray la = new JSONArray(entAlterationsList.getList().size());
            for (Map<String, Object> map : entAlterationsList.getList()) {
                la.add(JSON.toJSON(map));
            }
            wjo.put("entAlterationsList", la);

            //获取企业开户许可证信息集合,加入JSON
            jsonObject.put("_index", "ent_account_license");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon entAccountLicenseList = entCommonService.search(cdto2);
            JSONArray na = new JSONArray(entAccountLicenseList.getList().size());
            for (Map<String, Object> map : entAccountLicenseList.getList()) {
                na.add(JSON.toJSON(map));
            }
            wjo.put("entAccountLicenseList", na);
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataInfo", ja);

        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntInfoByEntName]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口3
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-网络推广列表(单表)
     * @author ZhangHao
     * @date 2019/6/17 13:10
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntNetPopularization")
    public JSONObject getEntNetPopularization(@RequestBody EntNetPopularizationEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null) {
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);
        }
        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0) {
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);
        }

        EntCommonEntity cdto = new EntCommonEntity();
        //dto转成json
        String jsonString = JSONObject.toJSONString(dto);
        //json装成jsonObject
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_net_popularization");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());

        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntInfoByEntName]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口4
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-招聘信息列表(单表)
     * @author ZhangHao
     * @date 2019/6/17 13:10
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntAdvertiseNotice")
    public JSONObject getEntAdvertiseNotice(@RequestBody EntAdvertiseNoticeEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null) {
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);
        }
        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0) {
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);
        }
        EntCommonEntity cdto = new EntCommonEntity();
        //dto转成json
        String jsonString = JSONObject.toJSONString(dto);
        //json装成jsonObject
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_advertise_notice");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntAdvertiseNotice]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口5
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-企业公众号信息列表（单表）
     * @author ZhangHao
     * @date 2019/6/17 13:10
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntSubscriptions")
    public JSONObject getEntSubscriptions(@RequestBody EntSubscriptionsEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置企业公众号信息列表_index
        jsonObject.put("_index", "ent_subscriptions");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntSubscriptions]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口6
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-新闻舆情-新闻动态信息列表
     * @author ZhangHao
     * @date 2019/6/17 13:11
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntPublicNews")
    public JSONObject getEntPublicNews(@RequestBody EntPublicNewsEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_public_news");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntPublicNews]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口7
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-知识产权-专利信息列表
     * @author ZhangHao
     * @date 2019/6/17 13:11
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntPatentInfo")
    public JSONObject getEntPatentInfo(@RequestBody EntPatentInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_patent_info");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntPatentInfo]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口8
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-知识产权-商标信息列表
     * @author ZhangHao
     * @date 2019/6/17 13:11
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntTrademarkInfo")
    public JSONObject getEntTrademarkInfo(@RequestBody EntTrademarkInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_trademark_info");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntTrademarkInfo]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口9
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-网站信息列表
     * @author ZhangHao
     * @date 2019/6/17 13:12
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntWebsiteInfo")
    public JSONObject getEntWebsiteInfo(@RequestBody EntWebsiteInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_website_info");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        EntCommonEntity cdto2 = new EntCommonEntity();
        //查询全部缺省1
        cdto2.setPageNum(1);
        //查询全部缺省100w
        cdto2.setPageSize(10000);

        //遍历所有的网站信息
        for (int i = 0; i < ja.size(); i++) {
            //获取当前循环的对象
            JSONObject wjo = (JSONObject) ja.get(i);
            jsonObject = new JSONObject();
            jsonObject.put("website_id", wjo.get("website_id"));

            //获取网站排名信息集合
            //设置取网站排名信息的_index
            jsonObject.put("_index", "website_rank");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon websiteList = entCommonService.search(cdto2);
            JSONArray wa = new JSONArray(websiteList.getList().size());
            for (Map<String, Object> map : websiteList.getList()) {
                wa.add(JSON.toJSON(map));
            }
            wjo.put("websiteRankList", wa);

            //获取网站性能评估信息集合
            //设置取网站性能评估信息的_index
            jsonObject.put("_index", "website_perform_evaluation");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon websiteRankList = entCommonService.search(cdto2);
            JSONArray ra = new JSONArray(websiteRankList.getList().size());
            for (Map<String, Object> map : websiteRankList.getList()) {
                ra.add(JSON.toJSON(map));
            }
            wjo.put("websitePerformEvaluationList", ra);
            //获取网站安全监测信息集合, 内含获取网站漏洞信息详情信息集合
            //设置网站安全监测信息的_index
            jsonObject.put("_index", "website_safety_monitor");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon websitePerformEvaluationList = entCommonService.search(cdto2);
            JSONArray ea = new JSONArray(websitePerformEvaluationList.getList().size());
            for (Map<String, Object> map : websitePerformEvaluationList.getList()) {
                ea.add(JSON.toJSON(map));
            }
            //遍历所有的网站安全监测信息
            for (int o = 0; o < ea.size(); o++) {
                //获取当前循环的对象
                JSONObject ejo = (JSONObject) ja.get(o);
                JSONObject sJsonObject = new JSONObject();
                sJsonObject.put("safety_id", wjo.get("safety_id"));
                sJsonObject.put("_index", "website_weakness");
                //封装到公共查询dto
                cdto2.setData(sJsonObject);
                //获取搜索结果
                ResultCommon websiteWeaknessList = entCommonService.search(cdto2);
                JSONArray ia = new JSONArray(websiteWeaknessList.getList().size());
                for (Map<String, Object> map : websiteWeaknessList.getList()) {
                    ia.add(JSON.toJSON(map));
                }
                ejo.put("websiteWeaknessList", ia);
            }
            wjo.put("websiteSafetyMonitorList", ea);
            //获取网站域名信息集合
            //设置网站域名信息的_index
            jsonObject.put("_index", "website_domain");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon websiteDomainList = entCommonService.search(cdto2);
            JSONArray sa = new JSONArray(websiteDomainList.getList().size());
            for (Map<String, Object> map : websiteDomainList.getList()) {
                sa.add(JSON.toJSON(map));
            }
            wjo.put("websiteDomainList", sa);
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntWebsiteInfo]");
        return (JSONObject) JSON.toJSON(respRes);

    }


    /**
     * 一号线索池-接口11
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-招标投标
     * @author ZhangHao
     * @date 2019/6/25 11:09
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntBidInfo")
    public JSONObject getEntBidInfo(@RequestBody EntBidInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_bid_info");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntBidInfo]");
        return (JSONObject) JSON.toJSON(respRes);

    }


    /**
     * 一号线索池-接口12
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-企业异常名录
     * @author ZhangHao
     * @date 2019/6/25 11:09
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntAbnormalOperInfo")
    public JSONObject getEntAbnormalOperInfo(@RequestBody EntBidInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_abnormal_oper_info");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntAbnormalOperInfo]");
        return (JSONObject) JSON.toJSON(respRes);
    }

    /**
     * 一号线索池-接口13
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-相关企业-对外投资列表
     * 2019-06-26调整业务逻辑
     * 1、参数由ent_id放到subject_main_id中进行参数查询
     * 2、遍历返回结果集
     * 3、拿到返回结果集中的subject_sub_id，调用企业基本信息查询接口进行查询
     * 4、拼接企业相关信息 企业名称、法人/负责人、企业所在地、成立日期
     * @description: 营销管理-企业详情-相关企业-对外投资列表
     * @author ZhangHao
     * @date 2019/6/25 11:09
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntInvestInfo")
    public JSONObject getEntInvestInfo(@RequestBody EntInvestInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);
        else
            dto.setSubject_main_id(dto.getEnt_id());

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_invest_info");
        //ent_id置空
        jsonObject.put("ent_id", "");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }
        EntCommonEntity cdto2 = new EntCommonEntity();
        cdto2.setPageNum(1);
        cdto2.setPageSize(100);
        //遍历返回结果集
        for (int i = 0; i < ja.size(); i++) {
            jsonObject = new JSONObject();
            //获取当前循环的对象
            JSONObject ejo = (JSONObject) ja.get(i);
            jsonObject.put("ent_id", ejo.get("subject_sub_id"));
            //获取企业行政许可信息集合，加入JSON
            jsonObject.put("_index", "ent_base_info");
            //封装到公共查询dto
            cdto2.setData(jsonObject);
            //获取搜索结果
            ResultCommon entList = entCommonService.search(cdto2);
            JSONArray ea = new JSONArray(entList.getList().size());
            for (Map<String, Object> map : entList.getList()) {
                ea.add(JSON.toJSON(map));
            }
            //被投资企业名称
            String ent_name = "";
            //法人/负责人
            String legal_represent = "";
            //企业所在地
            String mailing_addr = "";
            //成立日期
            String estimate_date = "";
            //遍历企业结果集
            for (int p = 0; p < ea.size(); p++) {
                //获取当前循环的对象
                JSONObject pea = (JSONObject) ea.get(p);
                ent_name = pea.getString("ent_name");
                legal_represent = pea.getString("legal_represent");
                mailing_addr = pea.getString("mailing_addr");
                estimate_date = pea.getString("estimate_date");
            }
            ejo.put("ent_name", ent_name);
            ejo.put("legal_represent", legal_represent);
            ejo.put("mailing_addr", mailing_addr);
            ejo.put("estimate_date", estimate_date);
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntInvestInfo]");
        return (JSONObject) JSON.toJSON(respRes);

    }

    /**
     * 一号线索池-接口14
     *
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @description: 营销管理-企业详情-相关企业-股东信息
     * @author ZhangHao
     * @date 2019/6/25 11:09
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/getEntShareholderInfo")
    public JSONObject getEntShareholderInfo(@RequestBody EntShareholderInfoEntity dto) {
        TimeUse timeUse = new TimeUse();
        if (dto == null)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "请求参数为空", timeUse.end(), null);

        if (dto.getPageNum() <= 0 || dto.getPageSize() <= 0)
            return ru.error(CluesPoolConstant.RESULT_CODE.PARERROR_, "起始页/每页记录数必须大于0", timeUse.end(), null);

        EntCommonEntity cdto = new EntCommonEntity();
        String jsonString = JSONObject.toJSONString(dto);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //设置网络推广的_index
        jsonObject.put("_index", "ent_be_invest");
        //封装到公共查询dto
        cdto.setData(jsonObject);
        cdto.setPageNum(dto.getPageNum());
        cdto.setPageSize(dto.getPageSize());
        //获取搜索结果
        ResultCommon resultList = entCommonService.search(cdto);

        JSONArray ja = new JSONArray(resultList.getList().size());
        for (Map<String, Object> map : resultList.getList()) {
            ja.add(JSON.toJSON(map));
        }

        // 组装数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("dataList", ja);
        resMap.put("pageNum", cdto.getPageNum());
        resMap.put("pageSize", cdto.getPageSize());
        resMap.put("total", cdto.getTotal());
        Map<String, Object> respRes = new HashMap<>();
        respRes.put("msg", "success(请求成功)");
        respRes.put("code", 200);
        respRes.put("data", resMap);
        respRes.put("useTime", timeUse.end());
        System.out.println("[" + TimeUse.getCurrtntTime() + "] [cluesPoolRest/getEntShareholderInfo]");
        return (JSONObject) JSON.toJSON(respRes);
    }


    /**
     * @param
     * @return void
     * @description: 每周/手动更新,读取mongo受保护的记录至ent_base_info.ent_protect_status
     * @author ZhangHao
     * @date 2019/10/15 13:30
     * @version 1.0.0.1
     */
    @ResponseBody
    @PostMapping(value = "/updateEntProtectStatusByMongo")
    public void updateEntProtectStatusByMongo() {
        //
        MongoClient mongoClient = MongoDBUtil.getConnect();
        String dbName = "MONCMA";//PropertyUtil.getValueByKey("conf/mongo.properties", "dbname");
        DB mongoDatabase = mongoClient.getDB(dbName);

        //第一个参数表示查询的表,第二个参数表示返回的具体key
        DBCursor cursor = mongoDatabase.getCollection("CM_CUST_ENTID_REL").find(null, null);
        //线程池，暂时先起6个
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(6);

        while (cursor.hasNext()) {
            final DBObject object = cursor.next();
            fixedThreadPool.execute(new Runnable() {
                public void run() {
                    String entId = CommUtil.toString(object.get("entId"), "");
                    String isUse = CommUtil.toString(object.get("isUse"), "");
                    //
                    if (StringUtils.isEmpty(entId) || !isUse.equals("1") || "null".equals(entId))
                        return;
                    //
                    EntCommonOptEntity dto = new EntCommonOptEntity();
                    dto.setIndex("ent_base_info");
                    dto.setIndexId(entId);
                    Map<String, Object> map = new HashMap<>(1);
                    map.put("ent_protect_status", "1");
                    dto.setMap(map);
                    EsUtil.updateEntStatus(dto);
                }
            });
        }

        //使用完close掉mongo链接
        MongoDBUtil.globalDestroy(mongoClient);

    }

}
