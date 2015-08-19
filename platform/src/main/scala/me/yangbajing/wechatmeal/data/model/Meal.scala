package me.yangbajing.wechatmeal.data.model

import java.time.LocalDateTime

/**
 * 餐
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-18.
 */
case class Meal(id: Long,
                merchantId: Int,
                name: String,
                price: BigDecimal,
                images: List[String],
                remark: Option[String],
                createdAt: LocalDateTime)
