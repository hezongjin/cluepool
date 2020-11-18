package cn.dd.apisys.service.impl;

import cn.dd.apisys.entity.CluesPoolConstant;
import cn.dd.apisys.entity.EntCommonEntity;
import cn.dd.apisys.entity.ResultCommon;
import cn.dd.apisys.service.IEntCommonService;
import cn.dd.apisys.utils.ElasticSearchPoolUtil;
import cn.dd.apisys.utils.EsUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * FileName: EntCommonServiceImpl.java
 * CreateTime: 2020/6/3 17:06.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
@Service("entCommonService")
public class EntCommonServiceImpl implements IEntCommonService {


    private static final Logger logger = LoggerFactory.getLogger(EntCommonServiceImpl.class);



    @Override
    public ResultCommon search(EntCommonEntity dto) {
        //获取ES客户端对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        setDefault(dto);

        setBase(sourceBuilder, dto);

        sourceBuilder.query(EsUtil.createQueryBuilders(dto.getData(), EsUtil.excludeList));
        //logger.debug(sourceBuilder.toString());

        RestHighLevelClient restHighLevelClient = ElasticSearchPoolUtil.getClient();
        SearchResponse searchResponse = null;
        SearchRequest searchRequest = null;
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        try {
            //System.out.println("++++"+dto.getData().getString(EsUtil.INDEX));
            //System.out.println("++++"+ JSON.toJSONString(sourceBuilder));
            searchRequest = new SearchRequest(dto.getData().getString(EsUtil.INDEX)).source(sourceBuilder);
            //System.out.println("++1++"+ JSON.toJSONString(sourceBuilder));
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //System.out.println("++2++"+ JSON.toJSONString(sourceBuilder));

        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("搜索数据异常", e);
            ElasticSearchPoolUtil.returnClient(restHighLevelClient);
            return new ResultCommon(CluesPoolConstant.RESULT_CODE.ERROR_, "error(请求失败，请检查参数)", resultList);
        }

        SearchHits shs = searchResponse.getHits();
        dto.setTotal(shs.getTotalHits().value);
        logger.debug("total:" + dto.getTotal());

        Iterator<SearchHit> its = shs.iterator();
        SearchHit sh = null;
        Map<String, Object> tmap = null;
        //String hlField = dto.getData().getString(EsUtil.HIGHTLIGHT);
        while (its.hasNext()) {
            sh = its.next();
            tmap = sh.getSourceAsMap();
            tmap.put("_score", sh.getScore());
            //handleHighlight(sh, hlField, tmap);
            resultList.add(tmap);
        }

        ElasticSearchPoolUtil.returnClient(restHighLevelClient);
        return new ResultCommon(CluesPoolConstant.RESULT_CODE.SUCCESS_, "success(请求成功)", resultList);
    }

    @Override
    public ResultCommon searchGt(EntCommonEntity dto, HashMap<String,String> gtCase) {
        //获取ES客户端对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        setDefault(dto);
        setBase(sourceBuilder, dto);
        sourceBuilder.query(EsUtil.createQueryBuildersGt(dto.getData(), EsUtil.excludeList,gtCase));
        //logger.debug(sourceBuilder.toString());
        RestHighLevelClient restHighLevelClient = ElasticSearchPoolUtil.getClient();
        SearchResponse searchResponse = null;
        SearchRequest searchRequest = null;
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        try {
            searchRequest = new SearchRequest(dto.getData().getString(EsUtil.INDEX)).source(sourceBuilder);
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.error("搜索数据异常", e);
            ElasticSearchPoolUtil.returnClient(restHighLevelClient);
            return new ResultCommon(CluesPoolConstant.RESULT_CODE.ERROR_, "error(请求失败，请检查参数)", resultList);
        }

        SearchHits shs = searchResponse.getHits();
        dto.setTotal(shs.getTotalHits().value);
        logger.debug("total:" + dto.getTotal());

        Iterator<SearchHit> its = shs.iterator();
        SearchHit sh = null;
        Map<String, Object> tmap = null;
        while (its.hasNext()) {
            sh = its.next();
            tmap = sh.getSourceAsMap();
            tmap.put("_score", sh.getScore());
            resultList.add(tmap);
        }

        ElasticSearchPoolUtil.returnClient(restHighLevelClient);
        return new ResultCommon(CluesPoolConstant.RESULT_CODE.SUCCESS_, "success(请求成功)", resultList);
    }

    /**
     * 根据映射关系查询
     * */
    @Override
    public ResultCommon searchReal(EntCommonEntity dto, HashMap<String,Object> realCase) {
        //获取ES客户端对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        setDefault(dto);

        setBase(sourceBuilder, dto);

        sourceBuilder.query(EsUtil.createQueryBuildersReal(dto.getData(), EsUtil.excludeList,realCase));
        //logger.debug(sourceBuilder.toString());

        RestHighLevelClient restHighLevelClient = ElasticSearchPoolUtil.getClient();
        SearchResponse searchResponse = null;
        SearchRequest searchRequest = null;
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        try {
            searchRequest = new SearchRequest(dto.getData().getString(EsUtil.INDEX)).source(sourceBuilder);
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("搜索数据异常", e);
            ElasticSearchPoolUtil.returnClient(restHighLevelClient);
            return new ResultCommon(CluesPoolConstant.RESULT_CODE.ERROR_, "error(请求失败，请检查参数)", resultList);
        }

        SearchHits shs = searchResponse.getHits();
        dto.setTotal(shs.getTotalHits().value);
        logger.debug("total:" + dto.getTotal());

        Iterator<SearchHit> its = shs.iterator();
        SearchHit sh = null;
        Map<String, Object> tmap = null;
        //String hlField = dto.getData().getString(EsUtil.HIGHTLIGHT);
        while (its.hasNext()) {
            sh = its.next();
            tmap = sh.getSourceAsMap();
            if(dto.getData().get(EsUtil.INDEX).equals("ent_contact_info")){
                tmap.put("contact_name", tmap.get("contact_name"));
                //System.out.println("[a]"+JSON.toJSONString(sh));
            }
            //tmap.put("_score", sh.getScore());
            //handleHighlight(sh, hlField, tmap);
            resultList.add(tmap);
        }

        ElasticSearchPoolUtil.returnClient(restHighLevelClient);
        return new ResultCommon(CluesPoolConstant.RESULT_CODE.SUCCESS_, "success(请求成功)", resultList);
    }
    /**
     * 基础赋值
     *
     * @param ssb
     * @param dto
     */
    private void setBase(SearchSourceBuilder ssb, EntCommonEntity dto) {
        ssb.size(dto.getPageSize());
        ssb.from((dto.getPageNum() - 1) * dto.getPageSize());

        // 设置获取字段与排除字段
        String _fetch = dto.getData().getOrDefault(EsUtil.FETCH, "").toString();
        String _exclude = dto.getData().getOrDefault(EsUtil.EXCLUDE, "").toString();
        String[] fs = null;
        String[] es = null;
        if (StringUtils.isNotEmpty(_fetch) && !_fetch.equals("*"))
            fs = _fetch.split(",");
        if (StringUtils.isNotEmpty(_exclude))
            es = _exclude.split(",");

        if (fs != null || es != null)
            ssb.fetchSource(fs, es);

        // 设置随机排序(不为空即可)
        String _tmp = dto.getData().getOrDefault(EsUtil.SORT, "").toString();
        if (StringUtils.isNotEmpty(_tmp))
            ssb.sort(EsUtil.SORT_BUILDER);

        // 设置高亮
        _tmp = dto.getData().getOrDefault(EsUtil.HIGHTLIGHT, "").toString();
        if (StringUtils.isNotEmpty(_tmp))
            ssb.highlighter(EsUtil.getHighlightBuilder(_tmp));

    }

    private void setDefault(EntCommonEntity dto) {
        dto.getData().put(EsUtil.EXCLUDE, "dtc_201_advertise_notice,dtc_401,dtc_101");
    }

    private void handleHighlight(SearchHit sh, String field, Map<String, Object> nowData) {
        if (StringUtils.isEmpty(field))
            return;

        HighlightField hf = sh.getHighlightFields().get(field);
        if (hf != null) {
            Text[] fragments = hf.fragments();
            String str = "";
            for (Text text : fragments)
                str += text.string();

            nowData.put(field + "2", str);
        }
    }

}
