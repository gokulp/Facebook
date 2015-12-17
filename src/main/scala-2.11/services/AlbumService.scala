package services

/**
 * Created by sahilpt on 11/29/15.
 */
import entities.{Album, AlbumUpdate}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class AlbumService(implicit val executionContext: ExecutionContext) {

  var albums = mutable.HashMap[Int, Album]()
    //Vector.empty[Album] // TODO: Change logic to ArrayBuffer

  def addPhoto(photoId:Int, albumID:Int): Future[Option[Album]] ={
    getAlbum(albumID).flatMap{ maybeAlbum =>
      maybeAlbum match {
        case None => Future {None}
        case Some(album) =>
          val newPhotos = photoId::(album.photos)
          //newPhotos = photoId :: newPhotos
          val newCount = album.count + 1
          var upAlbum:AlbumUpdate = new AlbumUpdate(count = Option(newCount),photos = Option(newPhotos),token = "")
          updateAlbum(album.id, upAlbum)
      }
    }
  }

  def createAlbum(album: Album): Future[Option[String]] = Future {
    albums.get(album.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        //println(album)
        albums(album.id) = album
        Some(album.id.toString)
    }
  }

  def getAlbum(id: Int): Future[Option[Album]] = Future {
    albums.get(id)
  }

  def updateAlbum(id: Int, update: AlbumUpdate): Future[Option[Album]] = {
    def updateEntity(album: Album): Album = {
      val from = album.from
      val byPage = album.byPage
      val count = update.count.getOrElse(album.count)
      val description = update.description.getOrElse(album.description)
      val can_upload = update.can_upload.getOrElse(album.can_upload)
      val privacy = update.privacy.getOrElse(album.privacy)
      val photos = update.photos.getOrElse(album.photos)
      //val likes = update.likes.getOrElse(album.likes)
      Album(id, byPage, from, count, description, can_upload, privacy,photos,token="")
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
    albums = albums -- Set(id)
  }
}
