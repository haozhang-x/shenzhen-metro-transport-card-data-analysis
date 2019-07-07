import java.util.Properties

import com.typesafe.config.ConfigFactory
import entity.TransData
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
 * Spark 应用程序
 * 2019/07/06
 *
 * @author zhanghao
 */
object App {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("sz-metro-transport-card-data-app")
      .master("local[*]").getOrCreate()

    var dataPath: String = null
    if (args.length == 1) {
      dataPath = args(0)
    } else {
      //默认为card-data文件夹
      dataPath = this.getClass.getClassLoader.getResource("card-data").getPath
      println(dataPath)
    }
    //设置日志级别为WARN
    spark.sparkContext.setLogLevel("WARN")
    //导入隐式依赖
    import spark.implicits._
    //加载数据并转成Dataset
    val data = spark.read.option("header", value = true).csv(s"$dataPath/*.csv").as[TransData]
    //缓存数据
    data.cache()

    //数据库连接信息
    val conf = ConfigFactory.load
    val jdbcURL = conf.getString("jdbcUrl")
    val connectionProperties = new Properties()
    connectionProperties.setProperty("user", conf.getString("username"))
    connectionProperties.setProperty("password", conf.getString("password"))

    //展示前5条数据
    data.show(5)
    //数据总量
    println(s"total_data_amount: ${data.count()}")
    import org.apache.spark.sql.functions._
    //数据期间与数据总量
    data.agg(min("deal_date").as("min"),
      max("deal_date").as("max"), count("*").as("total_amount")).show()
    //过滤出站点不为空的数据
    val filterData = data.filter("station is not null")
    filterData.cache()
    //按天进行分组的数据
    val stationDayData = filterData.groupBy('company_name, $"station", 'deal_type,
      date_format($"deal_date", "YYYY-MM-dd").as("deal_date"))
      .count()
      .orderBy(desc("count"))
    //保存数据到数据库中
    stationDayData.write.mode(SaveMode.Append).jdbc(jdbcURL, "day_card_data", connectionProperties)
    //按天和小时进行分组的数据
    val stationDayHourData = filterData.groupBy('company_name, $"station", 'deal_type,
      date_format($"deal_date", "YYYY-MM-dd").as("deal_date"),
      date_format($"deal_date", "HH").as("deal_hour"))
      .count()
      .orderBy(desc("count"))
    //保存数据到数据库中
    stationDayHourData.write.mode(SaveMode.Append).jdbc(jdbcURL, "day_hour_card_data", connectionProperties)
    //关闭SparkSession
    spark.close()
  }


}
