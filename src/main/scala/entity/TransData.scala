package entity

/**
 * 深圳通交易数据
 * 2019/07/06
 *
 * @param card_no      卡号
 * @param deal_date    交易日期时间
 * @param deal_type    交易类型
 * @param deal_money   交易金额
 * @param deal_value   交易值
 * @param equ_no       设备编码
 * @param company_name 公司名称
 * @param station      线路站点
 * @param car_no       车牌号
 * @param conn_mark  联程标记
 * @param close_date 结算日期
 * @author zhanghao
 */
case class TransData(card_no: String, deal_date: String, deal_type: String, deal_money: String,
                     deal_value: String, equ_no: String, company_name: String, station: String,
                     car_no: String, conn_mark: String, close_date: String)
