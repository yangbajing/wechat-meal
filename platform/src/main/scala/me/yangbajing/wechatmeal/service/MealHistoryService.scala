package me.yangbajing.wechatmeal.service

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.data.repo.MealHistoryRepo

/**
 * MealHistoryService
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-04.
 */
@Singleton
class MealHistoryService @Inject()(mealHistoryRepo: MealHistoryRepo) {
  def findByDate(date: LocalDate) = mealHistoryRepo.findByDate(date)

}
