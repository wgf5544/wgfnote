# Hive练习之MySQL

### 一、准备工作

* 在windows上的mysql数据库中创建两张表（account、money）

  * account表
  
        CREATE TABLE `acount` (
        `account_id` varchar(50) NOT NULL,
        `account_name` varchar(100) default NULL,
        `account_vip` int(11) default NULL,
        PRIMARY KEY  (`account_id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
        INSERT INTO `acount` VALUES ('1', 'zhangsan@163.com', '0');
        INSERT INTO `acount` VALUES ('2', 'lisi@163,com', '1');
        INSERT INTO `acount` VALUES ('3', 'wangwu@qq.com', '2');
        INSERT INTO `acount` VALUES ('4', 'zhaoliu', '0');
        INSERT INTO `acount` VALUES ('5', 'maqi', '1');
        
  * money表
 
        CREATE TABLE `money` (
         `m_id` varchar(50) NOT NULL,
         `m_incom` double default NULL,
         `m_paid` double default NULL,
         `account_name` varchar(100) default NULL,
         PRIMARY KEY  (`m_id`)
         ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
         INSERT INTO `money` VALUES ('1', '6000', '200', 'zhangsan@163.com');
         INSERT INTO `money` VALUES ('2', '9000', '0', 'lisi@163,com');
         INSERT INTO `money` VALUES ('3', '10000', '5000', 'wangwu@qq.com');
         INSERT INTO `money` VALUES ('4', '3000', '200', 'zhangsan@163.com');
         INSERT INTO `money` VALUES ('5', '80000', '90000', 'maqi');
         INSERT INTO `money` VALUES ('6', '4000', '0', 'wangwu@qq.com');
         INSERT INTO `money` VALUES ('7', '800', '0', 'lisi@163,com');
         INSERT INTO `money` VALUES ('8', '500000', '0', 'maqi');
 
### 二、在hive中创建两张表

* account表

       hive> create table account (account_id string,account_name string,account_vip int) 
                row format delimited fields terminated by '\t';      
       OK
       Time taken: 0.17 seconds

* money表

       hive> create table money (m_id string,m_income double,m_paid double,account_name string) 
                            row format delimited fields terminated by '\t';
       OK
       Time taken: 0.138 seconds

### 三、将Windows中mysq的数据直接导入到hive当中

* 将sqoop拷贝到hadoop03（运行着hive）上

       [root@hadoop01 softWare]# scp -r sqoop-1.4.4.bin__hadoop-2.0.4-alpha/ hadoop03:/softWare/

* 将mysq当中的数据直接导入到hive当中(切记关闭物理机的防火墙)

  * account表
  
        ./sqoop import \
        --connect jdbc:mysql://192.168.2.1:3306/yan \
        --username root \
        --password root \
        --table account \
        --fields-terminated-by '\t' \
        --delete-target-dir \
        --num-mappers 1 \
        --hive-import \
        --hive-database default \
        --hive-table account
  
  * money表

        ./sqoop import \
        --connect jdbc:mysql://192.168.2.1:3306/yan \
        --username root \
        --password root \
        --table money \
        --fields-terminated-by '\t' \
        --delete-target-dir \
        --num-mappers 1 \
        --hive-import \
        --hive-database default \
        --hive-table money

### 四、练习hive中的sql语句（HQL）

* 统计每一个账户的总收入、总支出、结余，再按照结余排序

     * select a.account_name name,t.sumIncom ,t.sumPaid,t.jieYu from account a join (select account_name,sum(m_income ) sumIncom,sum(m_paid ) sumPaid,(sum(m_income ) - sum(m_paid ) ) jieYu from money group by account_name) t on a.account_name = t.account_name order by t.jieYu desc;

           hive> select a.account_name name,t.sumIncom ,t.sumPaid,t.jieYu from account a join (select account_name,
                 sum(m_income ) sumIncom,sum(m_paid ) sumPaid,(sum(m_income ) - sum(m_paid ) ) jieYu from money 
                           group by account_name) t on a.account_name = t.account_name order by t.jieYu desc;
           Total jobs = 2
           Launching Job 1 out of 2
           Number of reduce tasks not specified. Estimated from input data size: 1
           In order to change the average load for a reducer (in bytes):
             set hive.exec.reducers.bytes.per.reducer=<number>
           In order to limit the maximum number of reducers:
             set hive.exec.reducers.max=<number>
           In order to set a constant number of reducers:
             set mapreduce.job.reduces=<number>
           Starting Job = job_1532966988105_0003, Tracking URL = http://hadoop03:8088/proxy/application_
                                                                                    1532966988105_0003/
           Kill Command = /softWare/hadoop-2.2.0/bin/hadoop job  -kill job_1532966988105_0003
           Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 1
           2018-07-30 11:27:36,196 Stage-1 map = 0%,  reduce = 0%
           2018-07-30 11:27:44,564 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 3.04 sec
           2018-07-30 11:28:00,014 Stage-1 map = 100%,  reduce = 100%, Cumulative CPU 5.19 sec
           MapReduce Total cumulative CPU time: 5 seconds 190 msec
           Ended Job = job_1532966988105_0003
           ..................
           Starting Job = job_1532966988105_0004, Tracking URL = http://hadoop03:8088/proxy/application_
                                                                                  1532966988105_0004/
           Kill Command = /softWare/hadoop-2.2.0/bin/hadoop job  -kill job_1532966988105_0004
           Hadoop job information for Stage-3: number of mappers: 1; number of reducers: 1
           2018-07-30 11:28:15,536 Stage-3 map = 0%,  reduce = 0%
           2018-07-30 11:28:22,876 Stage-3 map = 100%,  reduce = 0%, Cumulative CPU 2.59 sec
           2018-07-30 11:28:31,229 Stage-3 map = 100%,  reduce = 100%, Cumulative CPU 3.81 sec
           MapReduce Total cumulative CPU time: 3 seconds 810 msec
           Ended Job = job_1532966988105_0004
           MapReduce Jobs Launched: 
           Job 0: Map: 1  Reduce: 1   Cumulative CPU: 5.19 sec   HDFS Read: 419 HDFS Write: 309 SUCCESS
           Job 1: Map: 1  Reduce: 1   Cumulative CPU: 3.81 sec   HDFS Read: 661 HDFS Write: 135 SUCCESS
           Total MapReduce CPU Time Spent: 9 seconds 0 msec
           OK
           maqi	580000.0	90000.0	490000.0
           lisi@163,com	9800.0	0.0	9800.0
           wangwu@qq.com	14000.0	5000.0	9000.0
           zhangsan@163.com	9000.0	400.0	8600.0
           Time taken: 66.986 seconds, Fetched: 4 row(s)
           
* 创建一个result表保存前一个sql执行的结果

     * create table result row format delimited fields terminated by '\t' as select a.account_name name,t.sumIncom ,t.sumPaid,t.jieYu from account a join (select account_name,sum(m_income ) sumIncom,sum(m_paid ) sumPaid,(sum(m_income ) - sum(m_paid ) ) jieYu from money group by account_name) t on a.account_name = t.account_name order by t.jieYu desc;
     
     
           hive> create table result row format delimited fields terminated by '\t' as select a.account_name name,
                  t.sumIncom ,t.sumPaid,t.jieYu from account a join (select account_name,sum(m_income ) sumIncom,
                  sum(m_paid ) sumPaid,(sum(m_income ) - sum(m_paid ) ) jieYu from money group by account_name) t 
                  on a.account_name = t.account_name order by t.jieYu desc;
           Total jobs = 2
           Launching Job 1 out of 2
           Number of reduce tasks not specified. Estimated from input data size: 1
           In order to change the average load for a reducer (in bytes):
             set hive.exec.reducers.bytes.per.reducer=<number>
           In order to limit the maximum number of reducers:
             set hive.exec.reducers.max=<number>
           In order to set a constant number of reducers:
             set mapreduce.job.reduces=<number>
           Starting Job = job_1532966988105_0005, Tracking URL = http://hadoop03:8088/proxy/application_
                                                                                  1532966988105_0005/
           Kill Command = /softWare/hadoop-2.2.0/bin/hadoop job  -kill job_1532966988105_0005
           Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 1
           2018-07-30 11:38:16,643 Stage-1 map = 0%,  reduce = 0%
           2018-07-30 11:38:23,894 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 2.88 sec
           2018-07-30 11:38:31,208 Stage-1 map = 100%,  reduce = 100%, Cumulative CPU 4.43 sec
           MapReduce Total cumulative CPU time: 4 seconds 430 msec
           Ended Job = job_1532966988105_0005
           ....................................
           Starting Job = job_1532966988105_0006, Tracking URL = http://hadoop03:8088/proxy/application_
                                                                                1532966988105_0006/
           Kill Command = /softWare/hadoop-2.2.0/bin/hadoop job  -kill job_1532966988105_0006
           Hadoop job information for Stage-3: number of mappers: 1; number of reducers: 1
           2018-07-30 11:38:44,758 Stage-3 map = 0%,  reduce = 0%
           2018-07-30 11:38:52,155 Stage-3 map = 100%,  reduce = 0%, Cumulative CPU 1.64 sec
           2018-07-30 11:38:58,382 Stage-3 map = 100%,  reduce = 100%, Cumulative CPU 2.45 sec
           MapReduce Total cumulative CPU time: 2 seconds 450 msec
           Ended Job = job_1532966988105_0006
           Moving data to: hdfs://ns1/user/hive/warehouse/result
           Table default.result stats: [numFiles=1, numRows=4, totalSize=135, rawDataSize=131]
           MapReduce Jobs Launched: 
           Job 0: Map: 1  Reduce: 1   Cumulative CPU: 4.43 sec   HDFS Read: 419 HDFS Write: 309 SUCCESS
           Job 1: Map: 1  Reduce: 1   Cumulative CPU: 2.45 sec   HDFS Read: 661 HDFS Write: 206 SUCCESS
           Total MapReduce CPU Time Spent: 6 seconds 880 msec
           OK
           Time taken: 49.845 seconds 
 
