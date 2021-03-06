package services

/**
 * Created by sahilpt on 11/30/15.
 */

import entities.{Photo, PhotoUpdate}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random


class PhotoService(implicit val executionContext: ExecutionContext) {

  var photos = mutable.HashMap[Int, Photo]()

//  def initiatePhotos(noOfUsers:Int, albumService: AlbumService, profileService: ProfileService): Future[Option[String]] = Future {
//    var numPhotos = noOfUsers * 5
//    var album_id:Int = 0
//    for (photo_id <- 0 until numPhotos){
//      var userId = Random.nextInt(noOfUsers)
//      album_id = -1
//      if (Random.nextInt(2) == 0)
//        album_id = Random.nextInt(noOfUsers)
//      var photo = new Photo(photo_id, userId, "weblink", "uiouorhwhcknsnx", album_id,false)
//      profileService.addPhoto(photo_id, userId, album_id, albumService)
//      if (album_id >= 0) {
//        albumService.addPhoto(photo.id, album_id)
//      }
//      photos = photos :+ photo
//    }
//    Some(numPhotos.toString)
//  }
  def createPhoto(photo: Photo): Future[Option[String]] = Future {
    photos.get(photo.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        photos(photo.id) = photo
        Some(photo.id.toString)
    }
  }

  def getPhoto(id: Int): Future[Option[Photo]] = Future {
    photos.get(id)
  }

  def updatePhoto(id: Int, update: PhotoUpdate): Future[Option[Photo]] = {
    def updateEntity(photo: Photo): Photo = {
      val from = photo.from
      val byPage = photo.byPage
//      val link = update.link.getOrElse(photo.link)
//      val name = update.name.getOrElse(photo.name)
      val album = update.album.getOrElse(photo.album)
      val can_delete = update.can_delete.getOrElse(photo.can_delete)
      val hidden = update.hiddenValue.getOrElse(photo.hiddenValue)
      Photo(id, byPage, from, /*link, name, */album, can_delete, "", hidden)
    }

    getPhoto(id).flatMap { maybePhoto =>
      maybePhoto match {
        case None => Future { None } // No Photo found, nothing to update
        case Some(photo) =>
          val updatedPhoto = updateEntity(photo)
          deletePhoto(id).flatMap { _ =>
            createPhoto(updatedPhoto).map(_ => Some(updatedPhoto))
          }
      }
    }
  }

  def deletePhoto(id: Int): Future[Unit] = Future {
    photos = photos -- Set(id)
  }
}
