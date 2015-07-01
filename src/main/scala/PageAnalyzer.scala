import com.ning.http.client.AsyncHttpClient
import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scala.xml.XML._

trait RandomApiClient {

  val apiUrl: String

  val h: Http = new Http(new AsyncHttpClient)

  def executeRequest(link: String): Future[Res] = h(url(link).GET)

  def getRandRes: Future[Res] = executeRequest(apiUrl)

  def getSite(url: String): Future[Res] = executeRequest(url)

  def shutdown(): Unit = h.shutdown()

}


trait PageAnalyzer extends RandomApiClient {

  val numberOfSites: Int
  val apiUrl: String


  private def defaultProcessor(pageResponse: (String, Int)) = pageResponse match {
    case (url, 200) =>
      println(url)
    case (url, code) =>
      println(s"$url $code")
  }

  def analyzeWith[A](analyzer: ((String,  Int)) => A = defaultProcessor _): Future[IndexedSeq[A]] = {
    val randomUrls: IndexedSeq[Future[String]] = for {
      _ <- 1 to numberOfSites
    } yield for (result <- getRandRes) yield extractUrl(result)


    val unsortedUrlsToCodes = for {
      url <- randomUrls
    } yield for {
        urlValue <- url
        urlToCode <- trim(urlValue).zip(getSite(urlValue).map(_.getStatusCode))
      } yield urlToCode


    val sortedUrlsToCodes: Future[IndexedSeq[(String, Int)]] =
      Future.sequence(unsortedUrlsToCodes.map(futureToFutureTry)).map {
        _.collect {
          case Success(v) => v
        }.sortWith {
          case ((link1, _), (link2, _)) => link1.head < link2.head
        }
      }

    sortedUrlsToCodes.map { list =>
      list.map(analyzer)
    }.andThen {
      case _ => shutdown()
    }
  }

  private def trim(link: String) = {
    val http = "http://"
    val httpWww = "http://www."
    (link match {
      case l if l.startsWith(httpWww) => Future(l.drop(httpWww.length))
      case l if l.startsWith(http) => Future(l.drop(http.length))
      case l => Future(l)
    }).map {
      case l if l.endsWith("/") => l.dropRight(1)
      case l => l
    }
  }

  private def extractUrl(res: Res) = {
    val withoutSchema = res.getResponseBody.lines.drop(1).mkString("\n")
    val xmlRes = loadString(withoutSchema)
    (xmlRes \\ "a" \ "@href" head).toString()
  }

  private def futureToFutureTry[T](f: Future[T]): Future[Try[T]] =
    f.map(Success(_)).recover { case x => Failure(x) }


}

