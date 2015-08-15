package me.yangbajing.weixin.mp.message

sealed abstract class MessageType(val name: String) {
  override def toString: String = name

  def isEvent = this == MessageTypes.Event

  def isShortVideo = this == MessageTypes.ShortVideo

  def isVideo = this == MessageTypes.Video

  def isVoice = this == MessageTypes.Voice

  def isText = this == MessageTypes.Text

  def isImage = this == MessageTypes.Image

  def isMusic = this == MessageTypes.Music

  def isNews = this == MessageTypes.News
}

object MessageTypes {

  case object Event extends MessageType("event")

  case object ShortVideo extends MessageType("shortvideo")

  case object Video extends MessageType("video")

  case object Voice extends MessageType("voice")

  case object Text extends MessageType("text")

  case object Image extends MessageType("image")

  case object Music extends MessageType("music")

  case object News extends MessageType("news")

  def apply(v: String): MessageType = v match {
    case Event.name => Event
    case ShortVideo.name => ShortVideo
    case Video.name => Video
    case Voice.name => Voice
    case Text.name => Text
    case Image.name => Image
    case Music.name => Music
    case News.name => News
  }

}
