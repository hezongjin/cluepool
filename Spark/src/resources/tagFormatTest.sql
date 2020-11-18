-- 通过SPARK-SQL执行,phoenix支持力度不足,以至于数据量大的SQL无法执行

-- =初始化数据=
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529
SELECT ent_id as subject_id,1 as idx,'2020-05-29 19:00:00' as modify_time,'' as create_time FROM EDS.T_EDS_ENT_BASE_INFO ;

-- 0 保留字段,内部判断使用
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t0 FROM EDS.T_EDS_ENT_BASE_INFO WHERE icr_no is not null or uscc is not null;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t0 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t0 is null;

-- 1 企业注册状态
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,cast(reg_status_code as integer) as t1 FROM EDS.T_EDS_ENT_BASE_INFO WHERE idx=1 and reg_status_code in ('1','2','3','4','5','6','7','8') ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,8 as t1 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t1 is null ;

-- 2 企业实际经营年份
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,0 as t2 FROM ( select ent_id,CASE WHEN estimate_date is null or instr(estimate_date,'-')=0 THEN -1 ELSE year(to_date(estimate_date)) * 10000 + month(to_date(estimate_date)) * 100 END as date_from1 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and reg_status_code='1' ) as t1 where date_from1=-1;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t2 FROM ( select ent_id,CASE WHEN estimate_date is null or instr(estimate_date,'-')=0 THEN -1 ELSE year(to_date(estimate_date)) * 10000 + month(to_date(estimate_date)) * 100 END as date_from1 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and reg_status_code='1' ) as t1 where date_from1>20181000;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,2 as t2 FROM ( select ent_id,CASE WHEN estimate_date is null or instr(estimate_date,'-')=0 THEN -1 ELSE year(to_date(estimate_date)) * 10000 + month(to_date(estimate_date)) * 100 END as date_from1 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and reg_status_code='1' ) as t1 where date_from1>20141000 and date_from1<=20181000;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,3 as t2 FROM ( select ent_id,CASE WHEN estimate_date is null or instr(estimate_date,'-')=0 THEN -1 ELSE year(to_date(estimate_date)) * 10000 + month(to_date(estimate_date)) * 100 END as date_from1 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and reg_status_code='1' ) as t1 where date_from1>20091000 and date_from1<=20141000;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,4 as t2 FROM ( select ent_id,CASE WHEN estimate_date is null or instr(estimate_date,'-')=0 THEN -1 ELSE year(to_date(estimate_date)) * 10000 + month(to_date(estimate_date)) * 100 END as date_from1 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and reg_status_code='1' ) as t1 where date_from1>19991000 and date_from1<=20091000;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,5 as t2 FROM ( select ent_id,CASE WHEN estimate_date is null or instr(estimate_date,'-')=0 THEN -1 ELSE year(to_date(estimate_date)) * 10000 + month(to_date(estimate_date)) * 100 END as date_from1 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and reg_status_code='1' ) as t1 where date_from1!=-1 and date_from1<=19991000;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,0 as t2 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t2 is null ;

-- 3 企业注册资本
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t3 FROM EDS_BAK.T_EDS_ENT_BASE_INFO_CAPITAL where cast(REG_CAPS2 as double)<100 ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,2 as t3 FROM EDS_BAK.T_EDS_ENT_BASE_INFO_CAPITAL where cast(REG_CAPS2 as double)>=100  and cast(REG_CAPS2 as double)<200;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,3 as t3 FROM EDS_BAK.T_EDS_ENT_BASE_INFO_CAPITAL where cast(REG_CAPS2 as double)>=200  and cast(REG_CAPS2 as double)<500;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,4 as t3 FROM EDS_BAK.T_EDS_ENT_BASE_INFO_CAPITAL where cast(REG_CAPS2 as double)>=500  and cast(REG_CAPS2 as double)<1000;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,5 as t3 FROM EDS_BAK.T_EDS_ENT_BASE_INFO_CAPITAL where cast(REG_CAPS2 as double)>=1000 and cast(REG_CAPS2 as double)<2000;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,6 as t3 FROM EDS_BAK.T_EDS_ENT_BASE_INFO_CAPITAL where cast(REG_CAPS2 as double)>=2000 ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,0 as t3 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t3 is null;

-- 4 是否外贸企业
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t4 FROM( select concat(ent_name,oper_scope) as con,ent_id from EDS.T_EDS_ENT_BASE_INFO where idx=1 ) as tb1 where con like '%外贸%' or con like '%国际贸易%' or con like '%进出口%' or con like '%货运代理%' or con like '%国际货运%' or con like '%对外贸易%' ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t4 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t4 is null;

-- 5 有无企业邮箱
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t5 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and instr(ent_mailbox,'@')>0  ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,2 as t5 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and (ent_mailbox is null or instr(ent_mailbox,'@')=0 ) ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,0 as t5 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t5 is null;

-- 6 有无联系人标签
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t6 FROM (select ent_id from EDS.T_EDS_ENT_CONTACT_INFO group by ent_id) as tb0;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t6 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t6 is null;

-- 7 是否有招聘标签（近1年）
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t7 FROM eds.t_eds_ent_advertise_notice where idx=1 and ent_id is not null and (job_release_date>= '2019-05-16' or update_time>= '2020-05-15') group by ent_id;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t7 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t7 is null;

-- 8 是否有手机
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT tb3.ent_id as subject_id,1 as t8 FROM EDS.T_EDS_ENT_CONTACT_INFO as tb3 inner join eds.t_eds_contact_infomations as tb4 on tb3.contact_id=tb4.contact_id where tb4.contact_type='0' group by tb3.ent_id ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t8 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t8 is null;

-- 9 是否上市企业
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT substr(ent_id,2) as subject_id,1 as t9 FROM EDS_BAK.T_EDS_ENT_BASE_INFO_LISTED where is_listed='1' ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT substr(ent_id,2) as subject_id,2 as t9 FROM EDS_BAK.T_EDS_ENT_BASE_INFO_LISTED where is_listed='0' ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,0 as t9 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t9 is null;

-- 10 是否集团企业
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t10 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and instr(ent_name,'集团')>0;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,2 as t10 FROM EDS.T_EDS_ENT_BASE_INFO where idx=1 and instr(ent_name,'集团')=0;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,0 as t10 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t10 is null;

-- 11 有无网站
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t11 FROM (select ent_id from EDS.T_EDS_ENT_WEBSITE_INFO group by ent_id) as tb0;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t11 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t11 is null;

-- 12 是否有公众号标签
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t12 FROM EDS.T_EDS_ENT_SUBSCRIPTIONS WHERE idx=1 ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t12 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t12 is null ;

-- 13 企业专利标签
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t13 FROM eds.t_eds_ent_patent_info WHERE idx=1 ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t13 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t13 is null;

-- 14 有无商标
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t14 FROM eds.t_eds_ent_trademark_info WHERE idx=1 ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t14 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t14 is null ;

-- 15 是否有新闻推广（近1年）
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t15 FROM eds.t_eds_ent_public_news WHERE idx=1 and release_time>='2019-05-15';
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t15 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t15 is null ;

-- 16 是否商贸企业
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t16 FROM EDS.T_EDS_ENT_BASE_INFO WHERE idx=1 and instr(ent_name,'商贸')>0 ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t16 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t16 is null ;

-- 17 是否中企老客 20190611添加
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 select tb2.ent_id as subject_id,1 as t17
                                  from !src.t_src_sm_cm_old_cust_qc2 as tb0
inner join EDS_BAK.T_EDS_ENT_ID_MAP as tb1 on tb0.custid=tb1.ent_id_old
inner join (select ent_id from EDS.T_EDS_ENT_BASE_INFO WHERE idx=1 ) as tb2 on tb1.ent_id=tb2.ent_id ;

UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t17 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t17 is null ;

-- 18 是否家电企业
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t18 FROM EDS.T_EDS_ENT_BASE_INFO WHERE idx=1 and instr(oper_scope,'家电')>0;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t18 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t18 is null;

-- 19 是否家居企业
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t19 FROM EDS.T_EDS_ENT_BASE_INFO WHERE idx=1 and instr(oper_scope,'家居')>0;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t19 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t19 is null;

-- 20 是否是竞争对手客户
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,1 as t20 FROM (select ent_id from EDS.T_EDS_ENT_WEBSITE_INFO where length(developer)>0 and developer!='无法获取' group by ent_id) as tb0;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t20 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t20 is null;

-- 21 是否有网站测速
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT tb1.ent_id as subject_id,1 as t21 FROM EDS.T_EDS_ENT_WEBSITE_INFO as tb1 inner join EDS.T_EDS_WEBSITE_PERFORM_EVALUATION as tb2 on tb1.website_id=tb2.website_id group by tb1.ent_id;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t21 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t21 is null;

-- 22服装行业/23食品行业/24机械行业/25生物科技行业
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529
select
  tb0.ent_id as subject_id,
if( instr(oper_scope,'服装')>0 or instr(ent_name,'服装')>0 ,1,2) as t22,
if( instr(oper_scope,'食品')>0 or instr(ent_name,'食品')>0 ,1,2) as t23,
if( instr(oper_scope,'机械')>0 or instr(ent_name,'机械')>0 ,1,2) as t24,
if( instr(oper_scope,'生物')>0 or instr(ent_name,'生物')>0 ,1,2) as t25
from eds.t_eds_ent_base_info as tb0
inner join DTC.T_DTC_SUBJECT_201_20200529 as tb1 on tb0.ent_id=tb1.subject_id
inner join ( select tb3.ent_id as ent_id from EDS.T_EDS_ENT_CONTACT_INFO as tb3 inner join eds.t_eds_contact_infomations as tb4 on tb3.contact_id=tb4.contact_id where tb4.contact_type='0' group by tb3.ent_id ) as tb5 on tb0.ent_id=tb5.ent_id
inner join EDS_BAK.T_EDS_ENT_BASE_INFO_CAPITAL as tb6 on tb0.ent_id=tb6.ent_id
where tb1.t1=1 and tb1.t11=2 and cast(tb6.REG_CAPS2 as double)>200
;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t22 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t22 is null;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t23 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t23 is null;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t24 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t24 is null;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t25 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t25 is null;

-- 26企业类型
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529
select
  ent_id as subject_id,
  case
  when ent_type_code in ('1100','1110','1120','1121','1122','1123','1130','1140','1150','1151','1152','1153','1190','5100','5110','5120','5130','5140','5150','5160','5500','6100','6110','6120','6130','6140','6150','6160','6170','2100','2110','2120','2121','2122','2130','2140','2150','2151','2152','2153','2190','5600') then 1
  when ent_type_code in ('3300','3400','1200','1210','1211','1212','1213','1219','1220','1221','1222','1223','1229','5200','5210','5220','5230','5240','6200','6210','6220','6230','6240','6250','6260','2200','2210','2211','2212','2213','2219','2220','2221','2222','2223','2229') then 2
  when ent_type_code in ('3100','3200','4100','4110','4120','4200','4210','4220','4310','4320','4410','4420') then 3
  when ent_type_code in ('5000','5400','5800','5840','7000','7100','7110','7120','7130','7190','7200','7300','5410','5420','5430','5490','5810','7310','7390') then 4
  when ent_type_code in ('6000','6190','6290','6400','6410','6420','6430','6490','6800','6810','6840','6890') then 5
  when ent_type_code in ('4500','4530','4531','4532','4533','4550','4551','4552','4553','9100','9110','9200','9999','4540','4560') then 6
  when ent_type_code in ('5300','5310','5320','5820','5830','5890','6300','6310','6320','6390','6820','6830') then 7
  when ent_type_code in ('1000','5190','5290','5390','2000','3000','3500','4000','4300','4330','4340','4400','4600','4700','8000','9000','9900','0000') then 8
  else 0 end as t26
from eds.t_eds_ent_base_info where ent_type_code is not null;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,0 as t26 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t26 is null;

-- 27手机且实号
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT tb3.ent_id as subject_id,1 as t27 FROM EDS.T_EDS_ENT_CONTACT_INFO as tb3 inner join eds.t_eds_contact_infomations as tb4 on tb3.contact_id=tb4.contact_id inner join eds_bak.t_eds_contact_infomations_check2 as tb5 on tb4.contact_val=tb5.phone where tb4.contact_type='0' and tb5.status='实号' group by tb3.ent_id ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT subject_id,2 as t27 FROM DTC.T_DTC_SUBJECT_201_20200529 WHERE idx=1 and t27 is null;


-- 28新增行业识别标签（2020-05-29）
UPSERT INTO DTC.T_DTC_SUBJECT_201_20200529 SELECT ent_id as subject_id,(case when (instr(ent_name,'电子商务')>0 or instr(oper_scope,'酒')>0 or instr(oper_scope,'食品')>0 or instr(oper_scope,'米')>0 or instr(oper_scope,'面')>0 or instr(oper_scope,'油')>0 or instr(oper_scope,'粮')>0 or instr(oper_scope,'水果')>0 or instr(oper_scope,'蔬菜')>0 or instr(oper_scope,'饮用水')>0 or instr(oper_scope,'中成药')>0 or instr(oper_scope,'化学药制剂')>0 or instr(oper_scope,'抗生素制剂')>0 or instr(oper_scope,'生物制品')>0 or instr(oper_scope,'生化药品')>0 or instr(oper_scope,'中药饮片')>0 or instr(oper_scope,'医疗器械')>0) then 1 else 2 end) as t28 FROM EDS.T_EDS_ENT_BASE_INFO


-- 网站系列---
-- =初始化数据=
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529
SELECT tb1.website_id as subject_id,tb1.ent_id as subject_id_top,
1 as idx,'2020-05-29 19:00:00' as modify_time,'' as create_time,1 as t0 FROM eds.t_eds_ent_website_info as tb1
inner join (select ent_id from eds.t_eds_ent_base_info ) as tb2 on tb1.ent_id=tb2.ent_id ;


-- 1是否有备案
-- 2网站是否可访问
-- 3是否适配移动端
-- 4是否使用SSL
-- 5是否官网
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT website_id as subject_id,if(length(icp_no)>0, 1,2) as t1,if(access_flag in ('true','Y'), 1,2) as t2,if(mobile_adapt_flag in ('true','Y'), 1,2) as t3,if(ssl_flag in ('true','Y'), 1,2) as t4,if(official_flag in ('true','Y'), 1,2) as t5 FROM eds.t_eds_ent_website_info WHERE idx=1 ;

UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT subject_id,2 as t1 FROM DTC.T_DTC_SUBJECT_401_20200529 WHERE idx=1 and t1 is null;
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT subject_id,2 as t2 FROM DTC.T_DTC_SUBJECT_401_20200529 WHERE idx=1 and t2 is null;
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT subject_id,2 as t3 FROM DTC.T_DTC_SUBJECT_401_20200529 WHERE idx=1 and t3 is null;
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT subject_id,2 as t4 FROM DTC.T_DTC_SUBJECT_401_20200529 WHERE idx=1 and t4 is null;
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT subject_id,2 as t5 FROM DTC.T_DTC_SUBJECT_401_20200529 WHERE idx=1 and t5 is null;

-- 6网站测速
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT website_id as subject_id,1 as t6 FROM (select website_id,repeat_open_duration,evaluate_date, row_number() over(partition by website_id order by evaluate_date desc) as tmp_num from EDS.T_EDS_WEBSITE_PERFORM_EVALUATION  ) as t1 where t1.tmp_num=1 and repeat_open_duration<='0.05';
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT website_id as subject_id,2 as t6 FROM (select website_id,repeat_open_duration,evaluate_date, row_number() over(partition by website_id order by evaluate_date desc) as tmp_num from EDS.T_EDS_WEBSITE_PERFORM_EVALUATION  ) as t1 where t1.tmp_num=1 and repeat_open_duration>'0.05' and repeat_open_duration<='0.2' ;
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT website_id as subject_id,3 as t6 FROM (select website_id,repeat_open_duration,evaluate_date, row_number() over(partition by website_id order by evaluate_date desc) as tmp_num from EDS.T_EDS_WEBSITE_PERFORM_EVALUATION  ) as t1 where t1.tmp_num=1 and repeat_open_duration>'0.2'  and repeat_open_duration<='0.35' ;
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT website_id as subject_id,4 as t6 FROM (select website_id,repeat_open_duration,evaluate_date, row_number() over(partition by website_id order by evaluate_date desc) as tmp_num from EDS.T_EDS_WEBSITE_PERFORM_EVALUATION  ) as t1 where t1.tmp_num=1 and repeat_open_duration>'0.35' and repeat_open_duration<='0.5' ;
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT website_id as subject_id,5 as t6 FROM (select website_id,repeat_open_duration,evaluate_date, row_number() over(partition by website_id order by evaluate_date desc) as tmp_num from EDS.T_EDS_WEBSITE_PERFORM_EVALUATION  ) as t1 where t1.tmp_num=1 and repeat_open_duration>'0.5' ;
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT website_id as subject_id,6 as t6 FROM (select website_id,repeat_open_duration,evaluate_date, row_number() over(partition by website_id order by evaluate_date desc) as tmp_num from EDS.T_EDS_WEBSITE_PERFORM_EVALUATION  ) as t1 where t1.tmp_num=1 and repeat_open_duration='timeout';
UPSERT INTO DTC.T_DTC_SUBJECT_401_20200529 SELECT subject_id,0 as t6 FROM DTC.T_DTC_SUBJECT_401_20200529 WHERE idx=1 and t6 is null;




-- 联系人系列---
-- =初始化数据=
UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT tb0.contact_id as subject_id,tb0.ent_id as subject_id_top,'2020-05-29 19:00:00' as modify_time,'' as create_time,1 as t0 FROM eds.t_eds_ent_contact_info as tb0 INNER JOIN (select ent_id from eds.t_eds_ent_base_info ) as tb1 on tb0.ent_id=tb1.ent_id;

--
UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT contact_id as subject_id,1 as t1 from eds.t_eds_contact_infomations where contact_type in ('0','2');
UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT subject_id,2 as t1 FROM DTC.T_DTC_SUBJECT_101_20200529 WHERE idx=1 and t1 is null;

UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT contact_id as subject_id,1 as t2 from eds.t_eds_contact_infomations where contact_type='1';
UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT subject_id,2 as t2 FROM DTC.T_DTC_SUBJECT_101_20200529 WHERE idx=1 and t2 is null;

UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT contact_id as subject_id,1 as t3 from eds.t_eds_contact_infomations where contact_type='3';
UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT subject_id,2 as t3 FROM DTC.T_DTC_SUBJECT_101_20200529 WHERE idx=1 and t3 is null;

UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT contact_id as subject_id,1 as t4 from eds.t_eds_contact_infomations where contact_type='4';
UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT subject_id,2 as t4 FROM DTC.T_DTC_SUBJECT_101_20200529 WHERE idx=1 and t4 is null;

UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT contact_id as subject_id,1 as t5 from eds.t_eds_contact_infomations where contact_type='5';
UPSERT INTO DTC.T_DTC_SUBJECT_101_20200529 SELECT subject_id,2 as t5 FROM DTC.T_DTC_SUBJECT_101_20200529 WHERE idx=1 and t5 is null;


-- 招聘信息标签
-- 初始化数据
UPSERT INTO DTC.T_DTC_SUBJECT_201_ADVERTISE_NOTICE_20200529 SELECT tb0.id as subject_id,tb0.ent_id as subject_id_top,'2020-05-29 19:00:00' as modify_time,'' as create_time,1 as t0 FROM eds.t_eds_ent_advertise_notice as tb0 INNER JOIN (select ent_id from eds.t_eds_ent_base_info ) as tb1 on tb0.ent_id=tb1.ent_id;

-- 外贸岗位
UPSERT INTO DTC.T_DTC_SUBJECT_201_ADVERTISE_NOTICE_20200529 SELECT id as subject_id,1 as t1 from eds.t_eds_ent_advertise_notice where instr(job_name,'外贸')>0 ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_ADVERTISE_NOTICE_20200529 SELECT subject_id,2 as t1 FROM DTC.T_DTC_SUBJECT_201_ADVERTISE_NOTICE_20200529 WHERE idx=1 and t1 is null;
-- 网站岗位
UPSERT INTO DTC.T_DTC_SUBJECT_201_ADVERTISE_NOTICE_20200529 SELECT id as subject_id,1 as t2 from eds.t_eds_ent_advertise_notice where instr(job_name,'网站')>0 ;
UPSERT INTO DTC.T_DTC_SUBJECT_201_ADVERTISE_NOTICE_20200529 SELECT subject_id,2 as t2 FROM DTC.T_DTC_SUBJECT_201_ADVERTISE_NOTICE_20200529 WHERE idx=1 and t2 is null;



-- 最新更新时间标签源数据
UPSERT INTO DTC.T_DTC_LASTMODIFYTIME select ent_id,modify_time as lmt01 from EDS.T_EDS_ENT_BASE_INFO;
UPSERT INTO DTC.T_DTC_LASTMODIFYTIME select t1.ent_id,t1.modify_time as lmt02,t2.modify_time as lmt03 from EDS.T_EDS_ENT_CONTACT_INFO as t1 left join EDS.T_EDS_CONTACT_INFOMATIONS as t2 on t1.contact_id=t2.info_id  where t1.ent_id is not null;
UPSERT INTO DTC.T_DTC_LASTMODIFYTIME select ent_id,modify_time as lmt04 from EDS.T_EDS_ENT_WEBSITE_INFO where ent_id is not null;
UPSERT INTO DTC.T_DTC_LASTMODIFYTIME select ent_id,modify_time as lmt05 from EDS.T_EDS_ENT_ADVERTISE_NOTICE where ent_id is not null;
UPSERT INTO DTC.T_DTC_LASTMODIFYTIME select ent_id,modify_time as lmt06 from EDS.T_EDS_ENT_NET_POPULARIZATION where ent_id is not null;
