import com.typesafe.config.ConfigFactory

object Main extends App with PageAnalyzer {

  val conf = ConfigFactory.load()
  override val numberOfSites = conf.getInt("page-analyzer.numberOfSites")
  override val apiUrl = conf.getString("page-analyzer.apiUrl")

  analyzeWith()

}
