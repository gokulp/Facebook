package resources

/**
 * Created by gokul on 11/29/15.
 */

import java.security.{PrivateKey, PublicKey}

import akka.util.Timeout
import entities._
import routing.MyHttpService
import services._
import spray.routing._
import scala.concurrent.duration._
import scala.concurrent.Await

trait Resource extends MyHttpService {

  val securityService: SecurityService
  val profileService: ProfileService
  val postService: UserPostService
  val pageService: PageService
  val friendListService:FriendListService
  val photoService : PhotoService
  val albumService : AlbumService
  val statService:StatisticService

  def profileRoutes: Route =
    pathPrefix("getPublicKey") {
      pathEnd{
        get{
          complete(securityService.getPublicKey())
        }
      }
    }~
      pathPrefix("getUserPublicKey") {
        path(Segment) { id =>
          get{
            complete(securityService.getUserPublicKey(id.toInt))
          }
        }
      }~
      pathPrefix("getSecureToken") {
        path(Segment) { id =>
          get{
            complete(securityService.getSecureToken(id.toInt))
          }
        }
      }~
      pathPrefix("authenticate") {
        pathEnd {
          post {
            entity(as[Authentication]) { token =>
              complete(securityService.authenticateUser(token.id, token.token))
            }
          }
        }
      }~
      pathPrefix("profiles") {
        pathEnd {
          post {
            statService.addOneMore()
            entity(as[Profile]) { profile =>
              // No authentication is needed as this step is like a new user registration
              //println(profile)
              var flag:Boolean = true
              var toCheck:Option[Profile] = None
              if (profileService.profiles.get(profile.id) == toCheck){
                //println("Creting frined List and security profile")
                val friends = FriendList(profile.id, List(), List(), "")
                friendListService.createFriendList(friends)// = friends
                if (!securityService.addPublicKeyForUser(profile.id, profile.public_key))
                  flag = false
              }
              if (flag) {
                completeWithLocationHeader(
                  resourceId = profileService.createProfile(profile),
                  ifDefinedStatus = 201, ifEmptyStatus = 409)
              } else {
                complete (501, "Error in processing publicKey")
              }
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
                  if (securityService.authenticateUser(id.toInt, update.token).equals("Yes")){
                    complete(profileService.updateProfile(id.toInt, update))
                  } else {
                    complete(500, "Authentication Error")
                  }
                }
              } ~
              delete {
                statService.addOneMore()
                complete(204, profileService.deleteProfile(id.toInt))
              }
          }
      } ~
      pathPrefix("posts") {
        pathEnd {
          post {
            statService.addOneMore()
            entity(as[UserPost]) { posts =>
              if (securityService.authenticateUser(posts.id, posts.token).equals("Yes")) {
                if (!postService.userPosts.contains(posts.id)) {
                  if (posts.byPage == 1) {
                    pageService.addPost(posts.from, posts.id)
                  } else {
                    profileService.addPost(posts.from, posts.id)
                  }
                }
                completeWithLocationHeader(
                  resourceId = postService.createPost(posts),
                  ifDefinedStatus = 201, ifEmptyStatus = 409)
              }else {
                complete(500, "Authentication Failure")
              }
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
                  if (securityService.authenticateUser(update.from, update.token).equals("Yes")) {
                    complete(postService.updatePost(id.toInt, update))
                  } else {
                    complete(500, "Authentication Failure")
                  }
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
            //This is like creating a new page, No token authentication needed here
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
                  if (securityService.authenticateUser(id.toInt, update.token).equals("Yes")) {
                    complete(pageService.updatePage(id.toInt, update))
                  } else {
                    complete(500, "Authentication Error")
                  }
                }
              } ~
              delete {
                statService.addOneMore()
                complete(204, pageService.deletePage(id.toInt))
              }
          }
      } ~
      pathPrefix("friendRequest") {
        pathEnd {
          put {
            entity(as[FriendRequest]) { friendRequest =>
              if (securityService.authenticateUser(friendRequest.idFrom, friendRequest.token).equals("Yes")) {
                complete(friendListService.followFriend(friendRequest.idFrom, friendRequest.idTo, friendRequest.fromKey))
              } else {
                complete(500, "Authentication Failure")
              }
            }
          }
        } ~
          path(Segment) { id =>
            get {
              entity(as[Authentication]) { token =>
                println(token)
                if (securityService.authenticateUser(token.id, token.token).equals("Yes")){
                  println("Sending friend list back!!!!!!!!!!!!!!!!!")
                  complete(friendListService.getPendingRequests(token.id))
                } else {
                  complete(500, "Authentication Error")
                }
              }
            }
          }
      }~
      pathPrefix("confirmfriendRequest") {
        pathEnd {
          put {
            entity(as[ConfirmFriendRequest]) { friendRequest =>
              if (securityService.authenticateUser(friendRequest.idFrom, friendRequest.token).equals("Yes")) {
                complete(friendListService.addNewFriend(friendRequest.idFrom, friendRequest.idTo, friendRequest.keyFrom, friendRequest.keyTo))
              } else {
                complete(500, "Authentication Failure")
              }
            }
          }
        }
      } ~
      pathPrefix("friendLists") {
        //      pathEnd {
        //        post {
        //          statService.addOneMore()
        //          entity(as[FriendList]) { friendList =>
        //            completeWithLocationHeader(
        //              resourceId = friendListService.createFriendList(friendList),
        //              ifDefinedStatus = 201, ifEmptyStatus = 409)
        //          }
        //        }
        //      } ~
        path(Segment) { id =>
          get {
            statService.addOneMore()
            statService.addOneMore()
            complete(friendListService.getFriendList(id.toInt))
          }
          /*
                      put {
                        statService.addOneMore()
                        entity(as[FriendListUpdate]) { update =>
                          complete(friendListService.updateFriendList(id.toInt, update))
                        }
                      } ~
          */
          //            delete {
          //              statService.addOneMore()
          //              complete(204, friendListService.deleteFriendList(id.toInt))
          //            }
        }
      } ~
      pathPrefix("photos") {
        pathEnd {
          post {
            statService.addOneMore()
            entity(as[Photo]) { photo =>
              if (securityService.authenticateUser(photo.id, photo.token).equals("Yes")) {
                if (!photoService.photos.contains(photo.id)) {
                  if (photo.byPage == 1) {
                    pageService.addPhoto(photo.id, photo.from, photo.album, albumService)
                  } else {
                    profileService.addPhoto(photo.id, photo.from, photo.album, albumService)
                  }
                }
                completeWithLocationHeader(
                  resourceId = photoService.createPhoto(photo),
                  ifDefinedStatus = 201, ifEmptyStatus = 409)
              } else {
                complete(500, "Authentication Failure")
              }
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
                  if (securityService.authenticateUser(id.toInt, update.token).equals("Yes")) {
                    complete(photoService.updatePhoto(id.toInt, update))
                  } else {
                    complete(500, "Authentication Failure")
                  }
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
              if (securityService.authenticateUser(album.id, album.token).equals("Yes")) {
                if (!albumService.albums.contains(album.id)) {
                  if (album.byPage == 1) {
                    pageService.addAlbum(album.from, album.id)
                  } else {
                    profileService.addAlbum(album.from, album.id)
                  }
                }
                completeWithLocationHeader(
                  resourceId = albumService.createAlbum(album),
                  ifDefinedStatus = 201, ifEmptyStatus = 409)
              } else {
                complete(500, "Authentication Failure")
              }
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
      pathPrefix("Statistics") {
        path(Segment) { id =>
          get {
            complete(statService.printStats(id))
          }
        }
      }

}