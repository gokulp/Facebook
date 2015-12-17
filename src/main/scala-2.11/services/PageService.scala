package services

/**
 * Created by gokul on 11/29/15.
 */
import entities.{PageUpdate, Page}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class PageService(implicit val executionContext: ExecutionContext) {

  var pages = mutable.HashMap[Int, Page]()

  /*
     def initiatePages(noOfPages:Int, noOfUsers:Int): Future[Option[String]] = Future{
       for (i <- 0 until noOfPages){
         var likes = Random.nextInt(noOfUsers+1)
         var dummyPage = new Page(i, "fdufweklasdnkfhasojf", 0, 1, List(GenerateRandomStuff.getEmail),"dummyPage",likes, "")
         createPage(dummyPage)
       }
       Some(noOfPages.toString)
     }
  */
  def createPage(page: Page): Future[Option[String]] = Future {
    pages.get(page.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        //println(page)
        pages(page.id) = page
        Some(page.id.toString)
    }
  }

  def getPage(id: Int): Future[Option[Page]] = Future {
    pages.get(id)
  }

  def addPhoto(photoId:Int, pageID:Int, albumID:Int, albumService: AlbumService): String = {
    getPage(pageID).flatMap{ maybePage =>
      maybePage match {
        case None => Future {None}
        case Some(page) =>
          //var newAlbums = page.albums
          if (albumID != -1 && page.albums.contains(albumID))
            albumService.addPhoto(photoId, albumID)
          val newPhotos = photoId::(page.photos)
          //println(page.albums)
          var upPage:PageUpdate = new PageUpdate(albums = Option(page.albums), photos = Option(newPhotos), token= "")
          updatePage(pageID, upPage)
      }
    }
    "Posted Content"
  }

  def addAlbum(pageID:Int, album_ID:Int): String = {
    getPage(pageID).flatMap{ maybePage =>
      maybePage match {
        case None => Future {None}
        case Some(page) =>
          val newAlbums:List[Int] = album_ID::page.albums
          val upPage:PageUpdate = new PageUpdate(albums = Option(newAlbums), token = "")
          updatePage(page.id, upPage)
      }
    }
    "Posted Content"
  }

  def addPost(userId:Int, postId:Int): String = {
    getPage(userId).flatMap{maybePage =>
      maybePage match{
        case None => Future{None}
        case Some(page) =>
          val newpostList:List[Int] = postId :: page.userposts
          val upPage:PageUpdate = new PageUpdate(userposts = Option(newpostList), token = "")
          updatePage(page.id, upPage)
      }
    }
    "Posted new content"
  }

  def updatePage(id: Int, update: PageUpdate): Future[Option[Page]] = {
    def updateEntity(page: Page): Page = {
      val access_token = update.access_token.getOrElse(page.access_token)
      val email = update.email.getOrElse(page.email)
      val can_checkin = update.can_checkin.getOrElse(page.can_checkin)
      val can_post = update.can_post.getOrElse(page.can_post)
      val username = update.username.getOrElse(page.username)
      val likes = update.likes.getOrElse(page.likes)
      val userposts = update.userposts.getOrElse(page.userposts)
      val photos = update.photos.getOrElse(page.photos)
      val albums = update.albums.getOrElse(page.albums)
      Page(id, access_token, can_checkin, can_post, email, username, likes, albums, photos, userposts, "")
    }

    getPage(id).flatMap { maybePage =>
      maybePage match {
        case None => Future { None } // No page found, nothing to update
        case Some(page) =>
          val updatedPage = updateEntity(page)
          deletePage(id).flatMap { _ =>
            createPage(updatedPage).map(_ => Some(updatedPage))
          }
      }
    }
  }

  def deletePage(id: Int): Future[Unit] = Future {
    pages = pages -- Set(id)
  }
}
