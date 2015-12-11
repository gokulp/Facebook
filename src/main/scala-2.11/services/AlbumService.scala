package services

/**
 * Created by sahilpt on 11/29/15.
 */
import entities.{Album, AlbumUpdate}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class AlbumService(implicit val executionContext: ExecutionContext) {

  var albums = Vector.empty[Album] // TODO: Change logic to ArrayBuffer

  def initiateAlbums(noOfUsers:Int, profileService: ProfileService):Future[Option[String]] = Future{
    for (album_id <- 0 until noOfUsers){
      var userId = Random.nextInt(noOfUsers)
      var album = new Album(album_id, userId, 0, "uoirwekjsxf", false, "public", List())
      albums = albums :+ album
      profileService.addAlbum(userId, album_id)
    }
    Some(noOfUsers.toString)
  }

  def addPhoto(photoId:Int, albumID:Int): Future[Option[Album]] ={
    getAlbum(albumID).flatMap{ maybeAlbum =>
      maybeAlbum match {
        case None => Future {None}
        case Some(album) =>
          val newPhotos = photoId::(album.photos)
          //newPhotos = photoId :: newPhotos
          val newCount = album.count + 1
          var upAlbum:AlbumUpdate = new AlbumUpdate(count = Option(newCount),photos = Option(newPhotos))
          updateAlbum(album.id, upAlbum)
      }
    }
  }

  def createAlbum(album: Album): Future[Option[String]] = Future {
    albums.find(_.id == album.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        //println(album)
        albums = albums :+ album
        Some(album.id.toString)
    }
  }

  def getAlbum(id: Int): Future[Option[Album]] = Future {
    albums.find(_.id == id)
  }

  def updateAlbum(id: Int, update: AlbumUpdate): Future[Option[Album]] = {
    def updateEntity(album: Album): Album = {
      val from = update.from.getOrElse(album.from)
      val count = update.count.getOrElse(album.count)
      val description = update.description.getOrElse(album.description)
      val can_upload = update.can_upload.getOrElse(album.can_upload)
      val privacy = update.privacy.getOrElse(album.privacy)
      val photos = update.photos.getOrElse(album.photos)
      //val likes = update.likes.getOrElse(album.likes)
      Album(id, from, count, description, can_upload, privacy,photos)
    }

    getAlbum(id).flatMap { maybeAlbum =>
      maybeAlbum match {
        case None => Future { None } // No Album found, nothing to update
        case Some(album) =>
          val updatedAlbum = updateEntity(album)
          deleteAlbum(id).flatMap { _ =>
            createAlbum(updatedAlbum).map(_ => Some(updatedAlbum))
          }
      }
    }
  }

  def deleteAlbum(id: Int): Future[Unit] = Future {
    albums = albums.filterNot(_.id == id)
  }
}
