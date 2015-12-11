/**
 * Created by gokul on 11/29/15.
 */
import resources._
import services._
import spray.routing._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

class RestInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with Resources {

  val profileService = new ProfileService
  val postService = new UserPostService
  val pageService = new PageService
  val friendListService = new FriendListService
  val photoService = new PhotoService
  val albumService = new AlbumService
  val statService = new StatisticService

  val routes: Route = profileRoutes

  def receive = runRoute(routes)

}

trait Resources extends Resource //with PageResource with PostResource with FriendListResource
