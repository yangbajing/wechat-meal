package me.yangbajing.wechatmeal.data.model

import java.time.LocalDateTime

/**
 * 商户
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-18.
 */
case class Merchant(id: Int,
                    name: String,
                    remark: Option[String],
                    createdAt: LocalDateTime)
