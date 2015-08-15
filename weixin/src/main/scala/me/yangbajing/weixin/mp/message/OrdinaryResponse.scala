package me.yangbajing.weixin.mp.message

import me.yangbajing.weixin.mp.WeixinTools

import scala.xml.{Elem, Node, PCData}

abstract class OrdinaryResponse(val msgType: MessageType) {
  def toUserName: String

  def fromUserName: String

  def createTime: Long

  def toNode: Node = {
    <xml>
      {nodeSeq}
    </xml>
  }

  def stringify(): String = WeixinTools.stringify(toNode)

  protected def nodeSeq: Vector[Elem] = Vector(
    <ToUserName>
      {PCData(toUserName)}
    </ToUserName>,
    <FromUserName>
      {PCData(fromUserName)}
    </FromUserName>,
    <CreateTime>
      {createTime}
    </CreateTime>,
    <MsgType>
      {PCData(msgType.name)}
    </MsgType>
  )
}

final case class OrdinaryTextResponse(toUserName: String,
                                      fromUserName: String,
                                      createTime: Long,
                                      content: String) extends OrdinaryResponse(MessageTypes.Text) {
  override def nodeSeq = {
    val v = <Content>
      {PCData(content)}
    </Content>
    super.nodeSeq :+ v
  }
}

abstract class MediaResponse(override val msgType: MessageType) extends OrdinaryResponse(MessageTypes.Image) {
  def mediaId: String
}

final case class OrdinaryImageResponse(toUserName: String,
                                       fromUserName: String,
                                       createTime: Long,
                                       mediaId: String) extends MediaResponse(MessageTypes.Image) {
  override protected def nodeSeq = {
    val v = <Image>
      <MediaId>
        {PCData(mediaId)}
      </MediaId>
    </Image>
    super.nodeSeq :+ v
  }
}

final case class OrdinaryVoiceResponse(toUserName: String,
                                       fromUserName: String,
                                       createTime: Long,
                                       mediaId: String) extends MediaResponse(MessageTypes.Voice) {
  override protected def nodeSeq = {
    val v = <Voice>
      <MediaId>
        {PCData(mediaId)}
      </MediaId>
    </Voice>
    super.nodeSeq :+ v
  }
}

final case class OrdinaryVideoResponse(toUserName: String,
                                       fromUserName: String,
                                       createTime: Long,
                                       mediaId: String,
                                       title: String,
                                       description: String) extends MediaResponse(MessageTypes.Video) {
  override protected def nodeSeq = {
    val v = <Video>
      <MediaId>
        {PCData(mediaId)}
      </MediaId>
      <Title>
        {PCData(title)}
      </Title>
      <Description>
        {PCData(description)}
      </Description>
    </Video>
    super.nodeSeq :+ v
  }
}

final case class OrdinaryMusicResponse(toUserName: String,
                                       fromUserName: String,
                                       createTime: Long,
                                       title: String,
                                       description: String,
                                       musicUrl: String,
                                       hqmusicUrl: String,
                                       thumbMediaId: String) extends OrdinaryResponse(MessageTypes.Music) {
  override protected def nodeSeq = {
    val v = <Music>
      <Title>
        {PCData(title)}
      </Title>
      <Description>
        {PCData(description)}
      </Description>
      <MusicURL>
        {PCData(musicUrl)}
      </MusicURL>
      <HQMusicUrl>
        {PCData(hqmusicUrl)}
      </HQMusicUrl>
      <ThumbMediaId>
        {PCData(thumbMediaId)}
      </ThumbMediaId>
    </Music>
    super.nodeSeq :+ v
  }
}

final case class OrdinaryArticle(title: String, description: String, picUrl: String, url: String) {
  def node = {
    <item>
      <Title>
        {PCData(title)}
      </Title>
      <Description>
        {PCData(description)}
      </Description>
      <PicUrl>
        {PCData(picUrl)}
      </PicUrl>
      <Url>
        {PCData(url)}
      </Url>
    </item>
  }

  def nodeSeq: Seq[Elem] = Seq(
    <Title>
      {PCData(title)}
    </Title>,
    <Description>
      {PCData(description)}
    </Description>,
    <PicUrl>
      {PCData(picUrl)}
    </PicUrl>,
    <Url>
      {PCData(url)}
    </Url>
  )
}

final case class OrdinaryNewsResponse(toUserName: String,
                                      fromUserName: String,
                                      createTime: Long,
                                      articles: Seq[OrdinaryArticle]) extends OrdinaryResponse(MessageTypes.News) {
  override protected def nodeSeq = {
    super.nodeSeq ++ Seq(
      <ArticleCount>
        {articles.size}
      </ArticleCount>,
      <Articles>
        {articles.map(_.node)}
      </Articles>
    )
  }
}

object OrdinaryResponse {

  def respText(toUserName: String,
               fromUserName: String,
               timestamp: String,
               content: String) = {
    <xml>
      <ToUserName>
        {PCData(toUserName)}
      </ToUserName>
      <FromUserName>
        {PCData(fromUserName)}
      </FromUserName>
      <CreateTime>
        {timestamp}
      </CreateTime>
      <MsgType>
        {PCData("text")}
      </MsgType>
      <Content>
        {PCData(content)}
      </Content>
    </xml>
  }

}
