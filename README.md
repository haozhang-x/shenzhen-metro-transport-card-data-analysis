# sz metro transport card data analysis
# 深圳通刷卡数据分析 

<img src="https://github.com/haozhang-x/sz-metro-transport-card-data-analysis/blob/master/src/main/resources/images/204521%402x.png" width ="80%" height = "60%"  alt="image"/>

### 云图链接：https://v.yuntus.com/cloudv/5efa9def8e39e7410ede9d8ab98f141e
## 通过深圳市政府开放平台提供的深圳通刷卡数据，进行了简单分析，数据总量 1337000 条
## 功能说明
1. `FetchData.scala` 获取API上全量的数据，并保存成CSV文件到本地 
2. `TransData.scala` 交通卡数据实体类
3. `App.scala` Spark 应用程序，对数据进行了简单的统计，并保存结果到MySQL中
      - 统计指标：每个线路和站点，出站和入站的刷卡量
          * 按天统计
          * 按小时统计
4. `card-data` 目录下的csv文件是已经获取好的全量刷卡数据（`1337000`条）
5. `szt_card_data_2920000403601.csv` 是`10000`条的刷卡数据   
## 深圳市政府开放平台 深圳通刷卡数据开放API
### API说明
`https://opendata.sz.gov.cn/data/dataSet/toDataDetails/29200_00403601`
### API地址
`http://opendata.sz.gov.cn/api/29200_00403601/1/service`

