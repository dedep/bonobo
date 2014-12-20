package modules

import scaldi.Module
import service.city_updater.{CityUpdater, CityUpdaterImpl}
import service.game.evaluator.{MatchEvaluator, NormalDistributionBasedMatchEvaluator}
import service.game.promoter.{PointsPromotionStrategy, PromotionsStrategy}

class ServiceModule extends Module {
  bind [CityUpdater] to new CityUpdaterImpl

  bind [MatchEvaluator] to new NormalDistributionBasedMatchEvaluator
  bind [PromotionsStrategy] to new PointsPromotionStrategy
}
