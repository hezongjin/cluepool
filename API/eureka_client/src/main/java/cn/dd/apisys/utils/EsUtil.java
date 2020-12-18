package cn.dd.apisys.utils;

import cn.dd.apisys.entity.EntCommonOptEntity;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * FileName: EsUtil.java
 * CreateTime: 2020/6/3 16:56.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class EsUtil {
    public static final String BOOST = "BOOST";
    public static final String NOT_NULL = "_NOT_NULL_";
    public static final String INDEX = "_index";
    public static final String FETCH = "_fetch";
    public static final String EXCLUDE = "_exclude";
    public static final String HIGHTLIGHT = "_highlight";
    public static final String SORT = "_sort";
    public static final String CITYCODE_SUFFIX = "CCL";
    public static final String SEP = "|";
    public static final ScriptSortBuilder SORT_BUILDER = SortBuilders.scriptSort(new Script("Math.random()"), ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.ASC);
    private static final Logger logger = LoggerFactory.getLogger(EsUtil.class);
    public static List<String> specialList = Arrays.asList("dtc_101.t1", "dtc_101.t2", "dtc_101.t3", "dtc_101.t4", "dtc_101.t5");
    public static List<String> excludeList = Arrays.asList(INDEX, FETCH, EXCLUDE, HIGHTLIGHT, SORT, "maxRequest", "pageNum", "pageSize", "total");
    private static Map<String, HighlightBuilder> HBMap = new HashMap<String, HighlightBuilder>();

    /**
     * 根据 index.field获取高亮配置
     *
     * @param index_field
     * @return
     */
    public static HighlightBuilder getHighlightBuilder(String index_field) {
        HighlightBuilder hb = HBMap.get(index_field);
        if (hb == null) {
            hb = new HighlightBuilder().field(index_field).requireFieldMatch(false);
            hb.tagsSchema("default");

            HBMap.put(index_field, hb);
        }

        return hb;
    }


    public static QueryBuilder createQueryBuilders(JSONObject json, List<String> filterKey) {
        Set<String> keys = json.keySet();
        List<CommBuilder> list = new ArrayList<CommBuilder>();

        Map<String, List<String>> nestedMap = new HashMap<String, List<String>>();
        String[] ks = null;
        for (String k : keys) {
            if (filterKey.contains(k) || StringUtils.isEmpty(k))
                continue;
            //
            ks = k.split("\\.");
            // 内嵌
            if (ks.length == 2) {
                if (nestedMap.get(ks[0]) == null)
                    nestedMap.put(ks[0], new ArrayList<String>());
                nestedMap.get(ks[0]).add(k);
            } else {
                build(list, k, json.get(k));
            }
        }

        //
        if (!nestedMap.isEmpty()) {
            Set<String> tmpKeys = nestedMap.keySet();
            for (String k : tmpKeys)
                buildNested(list, k, nestedMap.get(k), json);
        }

        if (list.isEmpty())
            return null;

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (CommBuilder qb : list) {
            if (qb.getType() == CommBuilder.Type.FILTER)
                queryBuilder.filter(qb.getQueryBuilder());
            else
                queryBuilder.must(qb.getQueryBuilder());
        }

        return queryBuilder;
    }

    public static QueryBuilder createQueryBuildersGt(JSONObject json, List<String> filterKey, HashMap<String, String> gtCase) {
        //dto.getData(), EsUtil.excludeList,gtCase
        Set<String> keys = json.keySet(); //dataMap

        List<CommBuilder> list = new ArrayList<CommBuilder>();   //dataMap构造的所有queryBuilders

        Map<String, List<String>> nestedMap = new HashMap<String, List<String>>();

        String[] ks = null;

        for (String k : keys) {

            if (filterKey.contains(k) || StringUtils.isEmpty(k)) //filterKey 关键字 _fetch,_sort
                continue;

            ks = k.split("\\.");   //OR_dtc_101.t1,OR_dtc_101.t2

            if (ks.length == 2) {
                //3个内嵌字段 101 201 401
                if (nestedMap.get(ks[0]) == null)
                    nestedMap.put(ks[0], new ArrayList<String>());

                nestedMap.get(ks[0]).add(k);
            } else {

                //非嵌套结构
                build(list, k, json.get(k));
            }
        }


        //所有的可以遍历过后
        if (!nestedMap.isEmpty()) {     //嵌套字段
            Set<String> tmpKeys = nestedMap.keySet();
            for (String k : tmpKeys)
                //接入嵌套的处理
                buildNested(list, k, nestedMap.get(k), json);
        }

        if (list.isEmpty())
            return null;

        //所有dataMap处理的queryBuilders
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (CommBuilder qb : list) {
            if (qb.getType() == CommBuilder.Type.FILTER)
                queryBuilder.filter(qb.getQueryBuilder());
            else
                queryBuilder.must(qb.getQueryBuilder());
        }

        //gtCase 后来接入的条件 估计是原来的看不懂 又新加了一个map
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders
                .rangeQuery(gtCase.get("key_lastModify"))
                .from(gtCase.get("value_lastModify"));

        //增加一个注册时间
        if (gtCase.get("key_estimate_date") != null) {
            RangeQueryBuilder rangeQueryBuilder2 = QueryBuilders
                    .rangeQuery(gtCase.get("key_estimate_date"))
                    .from(gtCase.get("value_estimate_date"));
            queryBuilder.must(rangeQueryBuilder2);
        }

        //增加根据传参指令进行省市区为空检索(2020-11-10)
        /* 张嵩跟业务方再次确认后，此逻辑屏蔽
        if(gtCase.get("filterArea").trim().equals("ON")) {
            queryBuilder.must().add(QueryBuilders.boolQuery().must((QueryBuilders.existsQuery("province_code"))));
            queryBuilder.must().add(QueryBuilders.boolQuery().must((QueryBuilders.existsQuery("city_code"))));
            queryBuilder.must().add(QueryBuilders.boolQuery().must((QueryBuilders.existsQuery("county_code"))));
        }
        */
        return queryBuilder.must(rangeQueryBuilder);
    }


    public static QueryBuilder createQueryBuildersReal(JSONObject json, List<String> filterKey, HashMap<String, Object> realCase) {
        Set<String> keys = json.keySet();
        List<CommBuilder> list = new ArrayList<CommBuilder>();
        Map<String, List<String>> nestedMap = new HashMap<String, List<String>>();
        String[] ks = null;
        for (String k : keys) {
            if (filterKey.contains(k) || StringUtils.isEmpty(k))
                continue;
            //
            ks = k.split("\\.");
            // 内嵌
            if (ks.length == 2) {
                if (nestedMap.get(ks[0]) == null)
                    nestedMap.put(ks[0], new ArrayList<String>());
                nestedMap.get(ks[0]).add(k);
            } else {
                build(list, k, json.get(k));
            }
        }


        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (CommBuilder qb : list) {
            if (qb.getType() == CommBuilder.Type.FILTER)
                queryBuilder.filter(qb.getQueryBuilder());
            else
                queryBuilder.must(qb.getQueryBuilder());
        }
        //System.out.println("[KKK]"+realCase.get("real_ori").toString()+"WWW"+realCase.get("real_ori_val").toString());
        TermsQueryBuilder rangeQueryBuilder = QueryBuilders.termsQuery(realCase.get("real_ori").toString(), (List<String>) realCase.get("real_ori_val"));

        return queryBuilder.must(rangeQueryBuilder);
    }

    /**
     * 增加一个queryBuilders 非嵌套结构的
     * 例如:"reg_status_code": "1","ent_protect_status": "0"
     *
     * @param builderList 所有的queryBuilders的集合
     * @param key         dataMap的Key
     * @param objValue    这个key对应的value
     */
    private static void build(List<CommBuilder> builderList, String key, Object objValue) {

        if (objValue == null)
            return;

        String val = null;

        if (objValue instanceof String) {
            val = objValue.toString();

            if (StringUtils.isEmpty(val))
                return;
            //
            if (val.charAt(0) == '*') { //ent_name2
                // builderList.add(CommBuilder.build(CommBuilder.Type.DEF, QueryBuilders.matchQuery(key, val.substring(1))));
                //新增一个企业名称模糊查询的queryBuilders
                builderList.add(CommBuilder.build(CommBuilder.Type.DEF, QueryBuilders.wildcardQuery(key, val)));
            } else {
                //"dtc_201_t22": "2","reg_status_code": "1"
                builderList.add(assistTermsQuery(key, toList(val, key.startsWith("dtc_"))));
            }
        } else {
            // 这个基本用不到 不是字符串 那是什么?
            builderList.add(CommBuilder.build(CommBuilder.Type.DEF, QueryBuilders.termQuery(key, objValue)));
        }
    }


    /**
     * @param val
     * @return
     */
    private static List<Object> toList(String val, boolean isNumeral) {
        //exclusiveAreaCodeList 1234|2345|3456
        String[] vals = val.split("\\|");

        List<Object> list = new ArrayList<Object>();
        for (String v : vals) {
            if (StringUtils.isEmpty(v))
                continue;

            if (isNumeral)
                list.add(Integer.valueOf(v));
            else
                list.add(v);
        }

        return list;
    }


    /**
     * @param key  dataMap的某一个可以
     * @param vals 这个key对应的value 用|分割的list
     * @return
     */
    private static CommBuilder assistTermsQuery(String key, List<Object> vals) {

        CommBuilder cb = assistTermsQueryBase(key, vals);
        if (cb != null)
            return cb;

        // 权重判断,并判断多个字段的情况
        // BOOST|511321|511322|BOOST|511000|511001
        // BOOST|511321|511322|BOOST|511000|511001|BOOST| //vals list
        int first = vals.indexOf(BOOST);
        int last = vals.lastIndexOf(BOOST);

        String[] ks = key.split("\\,");

        List<Object> must1 = CommUtil.truncateList(vals, first + 1, last, null, true);
        List<Object> must2 = CommUtil.truncateList(vals, first + 1, last, CITYCODE_SUFFIX, true);
        List<Object> should1 = CommUtil.truncateList(vals, last + 1, vals.size(), null, true);
        List<Object> should2 = CommUtil.truncateList(vals, last + 1, vals.size(), CITYCODE_SUFFIX, true);

        QueryBuilder qb1 = null;
        if (ks.length == 2 && CommUtil.valid(must2)) {
            qb1 = QueryBuilders.boolQuery() //
                    .should(QueryBuilders.constantScoreQuery(new TermsQueryBuilder(ks[0], must1)).boost(2f)) //
                    .should(QueryBuilders.constantScoreQuery(new TermsQueryBuilder(ks[1], must2)).boost(2f));
        } else {
            qb1 = QueryBuilders.constantScoreQuery(new TermsQueryBuilder(ks[0], must1)).boost(2f);
        }

        //
        QueryBuilder builder = null;
        if (CommUtil.valid(should1)) {
            BoolQueryBuilder bool = QueryBuilders.boolQuery() //
                    .must(qb1) //
                    .should(QueryBuilders.constantScoreQuery(new TermsQueryBuilder(ks[0], should1)).boost(1f));
            //
            if (ks.length == 2 && CommUtil.valid(should2))
                bool.should(QueryBuilders.constantScoreQuery(new TermsQueryBuilder(ks[1], should2)).boost(1f));
            //
            builder = bool;
        } else {
            builder = qb1;
        }

        //
        return CommBuilder.build(CommBuilder.Type.DEF, builder);
    }

    /**
     * @param key
     * @param valList
     * @return
     */
    private static CommBuilder assistTermsQueryBase(String key, List<Object> valList) {
        if (valList.size() == 1) {
            // 添加 某组合字段或单个字段不为空限制
            if (valList.get(0).toString().equals(NOT_NULL)) {
                BoolQueryBuilder sq = QueryBuilders.boolQuery();
                String[] ks = key.split(",");
                for (String k : ks)
                    sq.should(QueryBuilders.existsQuery(k));
                return CommBuilder.build(CommBuilder.Type.FILTER, sq);
            } else {
                return CommBuilder.build(CommBuilder.Type.FILTER, QueryBuilders.termQuery(key, valList.get(0)));
            }
        }

        if (!valList.get(0).equals(BOOST) || Collections.frequency(valList, BOOST) != 2)
            return CommBuilder.build(CommBuilder.Type.FILTER, new TermsQueryBuilder(key, valList));

        return null;
    }

    /**
     * 不带封装的
     *
     * @param key
     * @param valList
     * @return
     */
    private static QueryBuilder assistTermsQueryBase4Simple(String key, List<Object> valList) {
        if (valList.size() == 1)
            return QueryBuilders.termQuery(key, valList.get(0));

        return new TermsQueryBuilder(key, valList);
    }


    /**
     * 构建嵌套
     *
     * @param builderList
     * @param nestedKey
     * @param keyList
     * @param json
     */
    private static void buildNested(List<CommBuilder> builderList, String nestedKey, List<String> keyList, JSONObject json) {
        if (keyList == null || keyList.isEmpty())
            return;
        //
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        if (nestedKey.startsWith("OR_")) {
            for (String k : keyList)
                bool.should(assistTermsQueryBase4Simple(k.substring(3), toList(json.get(k).toString(), true)));

            builderList.add(CommBuilder.build(CommBuilder.Type.DEF, QueryBuilders.nestedQuery(nestedKey.substring(3), bool, ScoreMode.Total)));
        } else {
            for (String k : keyList)
                bool.must(assistTermsQueryBase4Simple(k, toList(json.get(k).toString(), true)));

            builderList.add(CommBuilder.build(CommBuilder.Type.DEF, QueryBuilders.nestedQuery(nestedKey, bool, ScoreMode.Total)));
        }
    }

    public static Integer updateEntStatus(EntCommonOptEntity dto) {
        XContentBuilder builder = null;
        try {
            builder = jsonBuilder();
            //logger.info("ent_id:" + dto.getIndexId() + ",状态:" + dto.getMap().get("ent_protect_status"));
            builder.startObject().field("ent_protect_status", dto.getMap().get("ent_protect_status")).endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UpdateRequest updateRequest = new UpdateRequest(dto.getIndex(), dto.getIndexId()).doc(builder);
        Integer tag = 0;
        try {
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.add(updateRequest);
            bulkBatchUpdate(bulkRequest);
            tag = 1;
        } catch (Exception e) {
            tag = 0;
            logger.error("更新企业保护状态异常", e);
        }
        return tag;
    }

    private static void bulkBatchUpdate(BulkRequest bulkRequest) throws IOException {
        RestHighLevelClient restClient = ElasticSearchPoolUtil.getClient();
        BulkResponse responses = restClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        logger.info("bulkBatchUpdate," + responses.status().toString());


        ElasticSearchPoolUtil.returnClient(restClient);
    }
}
