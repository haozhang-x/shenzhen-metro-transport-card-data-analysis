package util

import java.nio.charset.StandardCharsets
import java.{util => ju}

import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

/**
 * Http工具类
 * 2019/07/06
 *
 * @author zhanghao
 */
object HttpUtils {
  // 创建 client 实例
  private[util] val httpClient = HttpClients.createDefault()

  def postResponse(url: String, params: Map[String, Any] = null): String = {
    // 创建 post 实例
    val post = new HttpPost(url)
    if (params != null) {
      val formParams = new ju.ArrayList[NameValuePair]
      for (param <- params) {
        // 创建参数队列
        formParams.add(new BasicNameValuePair(param._1, param._2.toString))
      }
      val uefEntity = new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8)
      post.setEntity(uefEntity)
    }
    // 创建 client 实例
    val response = httpClient.execute(post)
    // 获取返回结果
    EntityUtils.toString(response.getEntity, StandardCharsets.UTF_8)
  }

}
