import org.scalatest.concurrent.AsyncAssertions
import org.scalatest.{FlatSpec, Matchers, ParallelTestExecution}

import scala.language.postfixOps

class PageAnalyzerTest extends FlatSpec with Matchers
with ParallelTestExecution with AsyncAssertions with PageAnalyzer with PageAnalyzerConfig {

  // TODO: cool unit and integration tests with mocks.

}
