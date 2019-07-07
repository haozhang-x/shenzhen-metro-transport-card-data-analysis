package data

import java.io.PrintWriter
import java.{util => ju}

import com.alibaba.fastjson.JSON
import com.typesafe.config.ConfigFactory
import entity.TransData
import org.apache.commons.csv.{CSVFormat, CSVPrinter}
import util.HttpUtils


/**
 * 数据获取 2019/07/06
 * 将API中的数据获取保存成CSV文件到本地
 *
 * @author zhanghao
 */
object FetchData {
  def main(args: Array[String]): Unit = {
    val conf = ConfigFactory.load
    val api = conf.getString("api")
    val appKey = conf.getString("appKey")
    //保存的csv路径
    var csvPath: String = null
    //当前处理完成的page信息，只有当发生异常的时候，才会去记录
    var processPagePath: String = null
    if (args.length == 2) {
      csvPath = args(0)
      processPagePath = args(1)
    } else {
      csvPath = "card-data"
      processPagePath = getClass.getClassLoader.getResource(".page").getPath
    }

    val format = CSVFormat.DEFAULT.withHeader("card_no", "deal_date", "deal_type", "deal_money", "deal_value",
      "equ_no", "company_name", "station", "car_no", "conn_mark", "close_date")
    //数据总量
    val total: Float = 1337000F
    //每一页获取100000行数据
    val rows: Int = 100000
    //总的页数
    val totalPages: Int = Math.ceil(total / rows).toInt
    //用来保存当前页的信息，发生异常时恢复数据
    var currentPage = 0
    //数据量
    var dataSize = 0
    //遍历获取所有页数的数据
    for (page <- 1 to totalPages) {
      val responseEntity = HttpUtils.postResponse(api, Map("appKey" -> appKey, ".page" -> page, "rows" -> rows))
      //csv文件名称
      val csvName = s"${csvPath}/${page}_${totalPages}.csv"
      val out = new PrintWriter(csvName)
      val printer = new CSVPrinter(out, format)
      try {
        val data = JSON.parseObject(responseEntity).getJSONArray("data").toJavaList(classOf[TransData])
        dataSize += data.size()
        data.forEach(transData => {
          val records = new ju.ArrayList[String]()
          //卡号
          records.add(transData.card_no)
          //交易日期时间
          records.add(transData.deal_date)
          //交易类型
          records.add(transData.deal_type)
          //交易金额
          records.add(transData.deal_money)
          //交易值
          records.add(transData.deal_value)
          //设备编码
          records.add(transData.equ_no)
          //公司名称
          records.add(transData.company_name)
          //线路站点
          records.add(transData.station)
          //车牌号
          records.add(transData.car_no)
          //联程标记
          records.add(transData.conn_mark)
          //结算日期
          records.add(transData.close_date)
          printer.printRecord(records)
        })
        println(s"当前页${page},总页数${totalPages},已获取${dataSize}条数据")
        currentPage = page
        out.flush()
        out.close()
        //500毫秒之后再获取数据
        Thread.sleep(500)
      } catch {
        case e: Exception =>
          //保存当前已获取的page数据到文本文件中
          val page = new PrintWriter(processPagePath)
          page.write(currentPage)
          page.flush()
          page.close()
          println(e.getMessage)
      }

    }
  }
}
