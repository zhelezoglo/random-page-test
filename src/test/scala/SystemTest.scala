import org.scalatest.concurrent.AsyncAssertions
import org.scalatest.{FlatSpec, Matchers, ParallelTestExecution}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class SystemTest extends FlatSpec with Matchers
with ParallelTestExecution with AsyncAssertions with PageAnalyzer with PageAnalyzerConfig {

  val allowedMinResponseFactor = 0.25 // Some requests fail due to connection problems/obsolete links
  val allowedMaxTimePerPage = 10 seconds

  it should "print some formatted lines in out" in {
    val waiter = new Waiter
    analyzeWith(_ => ()).onComplete {
      case Success(result) =>
        waiter(result.size should be > (numberOfSites * allowedMinResponseFactor).toInt)
        waiter.dismiss()
      case Failure(err) =>
        waiter(throw err)
        waiter.dismiss()
    }
    waiter.await(timeout(allowedMaxTimePerPage * numberOfSites))
  }


  it should "not return too many lines in out" in {
    val waiter = new Waiter
    analyzeWith(_ => ()).onComplete {
      case Success(result) =>
        waiter(result.size should be <= numberOfSites)
        waiter.dismiss()
      case Failure(err) =>
        waiter(throw err)
        waiter.dismiss()
    }
    waiter.await(timeout(allowedMaxTimePerPage * numberOfSites))
  }


  def isSorted(result: IndexedSeq[String]) = result.view.zip(result.tail).forall(x => x._1.head <= x._2.head)

  it should "return ordered result" in {
    val waiter = new Waiter
    analyzeWith {
      case (url, 200) => url
      case (url, code) => s"$url $code"
    }.onComplete {
      case Success(result) =>
        waiter(isSorted(result) should equal (true))
        waiter.dismiss()
      case Failure(err) =>
        waiter(throw err)
        waiter.dismiss()
    }
    waiter.await(timeout(allowedMaxTimePerPage * numberOfSites))
  }


}
