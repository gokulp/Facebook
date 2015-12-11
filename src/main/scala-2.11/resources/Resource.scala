package resources

/**
 * Created by gokul on 11/29/15.
 */

import akka.util.Timeout
import entities._
import routing.MyHttpService
import services._
import spray.routing._
import scala.concurrent.duration._
import scala.concurrent.Await

trait Resource extends MyHttpService {

  val profileService: ProfileService
  val postService: UserPostService
  val pageService: PageService
  val friendListService:FriendListService
  val photoService : PhotoService
  val albumService : AlbumService
  val statService:StatisticService

  def profileRoutes: Route =
    pathPrefix("profiles") {
      pathEnd {
        post {
          statService.addOneMore()
          entity(as[Profile]) { profile =>
            completeWithLocationHeader(
              resourceId = profileService.createProfile(profile),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
        path(Segment) { id =>
          get {
            statService.addOneMore()
            complete(profileService.getProfile(id.toInt))
          } ~
            put {
              statService.addOneMore()
              entity(as[ProfileUpdate]) { update =>
                complete(profileService.updateProfile(id.toInt, update))
              }
            } ~
            delete {
              statService.addOneMore()
              complete(204, profileService.deleteProfile(id.toInt))
            }
        }
    } ~
    /*def postRoutes: Route = */
    pathPrefix("posts") {
      pathEnd {
        post {
          statService.addOneMore()
          entity(as[UserPost]) { posts =>
            completeWithLocationHeader(
              resourceId = postService.createPost(posts),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
        path(Segment) { id =>
          get {
            statService.addOneMore()
            complete(postService.getPost(id.toInt))
          } ~
            put {
              statService.addOneMore()
              entity(as[UserPostUpdate]) { update =>
                complete(postService.updatePost(id.toInt, update))
              }
            } ~
            delete {
              statService.addOneMore()
              complete(204, postService.deletePost(id.toInt))
            }
        }
    } ~
    pathPrefix("pages") {
      pathEnd {
        post {
          statService.addOneMore()
          entity(as[Page]) { page =>
            completeWithLocationHeader(
              resourceId = pageService.createPage(page),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
        path(Segment) { id =>
          get {
            statService.addOneMore()
            complete(pageService.getPage(id.toInt))
          } ~
            put {
              statService.addOneMore()
              entity(as[PageUpdate]) { update =>
                complete(pageService.updatePage(id.toInt, update))
              }
            } ~
            delete {
              statService.addOneMore()
              complete(204, pageService.deletePage(id.toInt))
            }
        }
    } ~
    pathPrefix("friendLists") {
      pathEnd {
        post {
          statService.addOneMore()
          entity(as[FriendList]) { friendList =>
            completeWithLocationHeader(
              resourceId = friendListService.createFriendList(friendList),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
        path(Segment) { id =>
          get {
            statService.addOneMore()
            statService.addOneMore()
            complete(friendListService.getFriendList(id.toInt))
          } ~
            put {
              statService.addOneMore()
              entity(as[FriendListUpdate]) { update =>
                complete(friendListService.updateFriendList(id.toInt, update))
              }
            } ~
            delete {
              statService.addOneMore()
              complete(204, friendListService.deleteFriendList(id.toInt))
            }
        }
    } ~
    pathPrefix("photos") {
      pathEnd {
        post {
          statService.addOneMore()
          entity(as[Photo]) { photo =>
            completeWithLocationHeader(
              resourceId = photoService.createPhoto(photo),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
        path(Segment) { id =>
          get {
            statService.addOneMore()
            complete(photoService.getPhoto(id.toInt))
          } ~
            put {
              statService.addOneMore()
              entity(as[PhotoUpdate]) { update =>
                complete(photoService.updatePhoto(id.toInt, update))
              }
            } ~
            delete {
              statService.addOneMore()
              complete(204, photoService.deletePhoto(id.toInt))
            }
        }
    } ~
    pathPrefix("albums") {
      pathEnd {
        post {
          statService.addOneMore()
          entity(as[Album]) { album =>
            completeWithLocationHeader(
              resourceId = albumService.createAlbum(album),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
        path(Segment) { id =>
          get {
            statService.addOneMore()
            complete(albumService.getAlbum(id.toInt))
          } ~
            put {
              statService.addOneMore()
              entity(as[AlbumUpdate]) { update =>
                complete(albumService.updateAlbum(id.toInt, update))
              }
            } ~
            delete {
              statService.addOneMore()
              complete(204, albumService.deleteAlbum(id.toInt))
            }
        }
    }~
      pathPrefix("initiateProfiles") {
        path(Segment) { id =>
          get {
            completeWithLocationHeader(resourceId = profileService.initiateProfiles(id.toInt),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
      pathPrefix("initiateFriends") {
        path(Segment) { id =>
          get {
            completeWithLocationHeader(resourceId = friendListService.initiateFriends(id.toInt),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
      pathPrefix("initiatePages") {
        path(Segment) { id =>
          get {
            completeWithLocationHeader(resourceId = pageService.initiatePages(id.toInt, id.toInt*2),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      }~
      pathPrefix("initiatePosts") {
        path(Segment) { id =>
          get {
            completeWithLocationHeader(resourceId = postService.initiatePosts(id.toInt),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
      pathPrefix("initiatePhotos") {
        path(Segment) { id =>
          get {
            completeWithLocationHeader(resourceId = photoService.initiatePhotos(id.toInt, albumService, profileService),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      } ~
      pathPrefix("initiateAlbums") {
        path(Segment) { id =>
          get {
            completeWithLocationHeader(resourceId = albumService.initiateAlbums(id.toInt, profileService),
              ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
      }~
      pathPrefix("Statistics") {
        path(Segment) { id =>
          get {
            complete(statService.printStats(id))
          }
        }
      }

}