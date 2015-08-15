package me.yangbajing.weixin.mp.domain

/**
 * @param access_token 获取到的凭证
 * @param expires_in 凭证有效时间，单位：秒
 */
case class AccessToken(access_token: String, expires_in: Long) {
  val dueTimeMillis = System.currentTimeMillis() + expires_in * 1000

  def isValid = System.currentTimeMillis() < dueTimeMillis
}

case class JsapiTicket(/*errcode: Int, errmsg: String, */ ticket: String, expires_in: Long) {
  val dueTimeMillis = System.currentTimeMillis() + expires_in * 1000

  def isValid = System.currentTimeMillis() < dueTimeMillis
}

case class MaterialCount(voice_count: Int, video_count: Int, image_count: Int, news_count: Int)

case class DownUrlMsg(title: String, description: String, down_url: String)

case class NewsItem(title: String, thumb_media_id: String, show_cover_pic: String, author: String,
                    digest: String, content: String, url: String, content_source_url: String)

case class MaterialContent(news_item: Seq[NewsItem])

case class MaterialItem(media_id: String, content: MaterialContent, update_time: String)

case class BatchgetMaterial(total_count: Int, item_count: Int, item: Seq[MaterialItem])
