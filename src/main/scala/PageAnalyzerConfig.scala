import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor, Executors}

import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

trait PageAnalyzerConfig {
  self: PageAnalyzer =>

  val conf = ConfigFactory.load()
  override val numberOfSites = conf.getInt("page-analyzer.numberOfSites")
  override val apiUrl = conf.getString("page-analyzer.apiUrl")

  val executorService = new ThreadPoolExecutor(numberOfSites,
    numberOfSites, 1L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue[Runnable])
  executorService.allowCoreThreadTimeOut(true)
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutorService(executorService)

}
