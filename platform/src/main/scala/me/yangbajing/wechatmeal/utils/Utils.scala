package me.yangbajing.wechatmeal.utils

import java.security.SecureRandom
import java.time.{LocalDate, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

import akka.util.Timeout
import org.apache.commons.codec.digest.DigestUtils

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
 * 工具
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
object Utils {

  private final val LOW = 33
  private final val HIGH = 127
  private final val RANDOM_STRING = (0 to 9).mkString + ('a' to 'z').mkString + ('A' to 'Z').mkString + ",.?!~`@#$%^&*()_-+=|[]{};:<>/"
  private final val RANDOM_STRING_LENGTH = RANDOM_STRING.length

  val formatDate = DateTimeFormatter.ofPattern("YYYY-MM-dd")
  val formatTime = DateTimeFormatter.ofPattern("HH:mm:ss")
  val random = new SecureRandom()

  implicit val timeout = Timeout(10, TimeUnit.SECONDS)

  def now() = ZonedDateTime.now(ZoneId.of("+8"))

  def nowDate() = LocalDate.now(ZoneId.of("+8"))

  /**
   *
   * @param salt salt 
   * @param digestPassword digest password
   * @param password password
   * @return
   */
  def matchPassword(salt: Array[Byte], digestPassword: Array[Byte], password: String): Boolean = {
    java.util.Arrays.equals(DigestUtils.sha256(salt ++ password.getBytes("UTF-8")), digestPassword)
  }

  /**
   *
   * @param password old password
   * @return (salt, digest password
   */
  def generatePassword(password: String): (Array[Byte], Array[Byte]) = {
    val salt = Array.ofDim[Byte](12)
    random.nextBytes(salt)
    (salt, DigestUtils.sha256(salt ++ password.getBytes("UTF-8")))
  }

  def randomString(size: Int): String = {
    assert(size > 0, s"size: $size must be > 0")
    (0 until size).map(_ => RANDOM_STRING.charAt(random.nextInt(RANDOM_STRING_LENGTH))).mkString
  }

  def getFileExtension(filename: String): String = {
    val idx = filename.lastIndexOf('.')
    if (idx == -1) "" else filename.substring(idx + 1)
  }

  @inline
  def nextPrintableChar(): Char = (random.nextInt(HIGH - LOW) + LOW).toChar

  @inline
  def randomNextInt(begin: Int, bound: Int) = random.nextInt(bound - begin) + begin

  def durationFormNow(endAt: ZonedDateTime, seconds: Long = 0L, now: ZonedDateTime = Utils.now()): FiniteDuration = {
    require(now.isBefore(endAt))
    Duration(java.time.Duration.between(now, endAt).getSeconds + seconds, TimeUnit.SECONDS)
  }

  def currentTimeSeconds() = System.currentTimeMillis() / 1000
}
