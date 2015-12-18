package services

/**
 * Created by gokul on 11/29/15.
 */

import akka.actor.Actor
import akka.actor.Actor.Receive
import entities._
import org.apache.commons.codec.binary.Base64
import security.RSAEncryptor
import services.GenerateRandomStuff._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ProfileService(implicit val executionContext: ExecutionContext) {

  var profiles = mutable.HashMap[Int, Profile]()

  def createProfile(profile: Profile): Future[Option[String]] = Future {
    profiles.get(profile.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        //println(profile)
        /*var key = RSAEncryptor.generateKey()
        var obj = RSAEncryptor.encrypt(profile, key.getPublic)
        println("Encrpted "+obj)
        println("Decrypt "+RSAEncryptor.decrypt(Base64.decodeBase64(obj), key.getPrivate))*/
        profiles(profile.id) = profile
        Some(profile.id.toString)
    }
  }

  def addPhoto(photoId:Int, userID:Int, albumID:Int, albumService: AlbumService): Future[Option[Profile]] = {
    getProfile(userID).flatMap{ maybeProfile =>
      maybeProfile match {
        case None => Future {None}
        case Some(profile) =>
          //var newAlbums = profile.albums
          if (albumID != -1 && profile.albums.contains(albumID))
            albumService.addPhoto(photoId, albumID)
          val newPhotos = photoId::(profile.photos)
          //println(profile.albums)
          var upProfile:ProfileUpdate = new ProfileUpdate(albums = Option(profile.albums), photos = Option(newPhotos), token= "")
          updateProfile(userID, upProfile)
      }
    }
  }

  def addAlbum(userID:Int, album_ID:Int): Future[Option[Profile]] ={
    getProfile(userID).flatMap{ maybeProfile =>
      maybeProfile match {
        case None => Future {None}
        case Some(profile) =>
          val newAlbums:List[Int] = album_ID::profile.albums
          val upProfile:ProfileUpdate = new ProfileUpdate(albums = Option(newAlbums), token = "")
          updateProfile(profile.id, upProfile)
      }
    }
  }

  def likePage(userId:Int, pageId:Int): String = {
    getProfile(userId).flatMap{maybeProfile =>
      maybeProfile match{
        case None => Future{None}
        case Some(profile) =>
          val newPageList:List[Int] = pageId :: profile.likedpages
          val upProfile:ProfileUpdate = new ProfileUpdate(likedpages = Option(newPageList), token = "")
          updateProfile(profile.id, upProfile)
      }
    }
    "Liked Page"
  }

  def addPost(userId:Int, postId:Int): String = {
    getProfile(userId).flatMap{maybeProfile =>
      maybeProfile match{
        case None => Future{None}
        case Some(profile) =>
          val newpostList:List[Int] = postId :: profile.userposts
          val upProfile:ProfileUpdate = new ProfileUpdate(userposts = Option(newpostList), token = "")
          updateProfile(profile.id, upProfile)
      }
    }
    "Posted new content"
  }

  def getProfile(id: Int): Future[Option[Profile]] = Future {
    profiles.get(id)
  }

  def updateProfile(id: Int, update: ProfileUpdate): Future[Option[Profile]] = {

    def updateEntity(profile: Profile): Profile = {
//      val birthday = update.birthday.getOrElse(profile.birthday)
//      val email = update.email.getOrElse(profile.email)
      val first_name = update.first_name.getOrElse(profile.first_name)
      val gender = update.gender.getOrElse(profile.gender)
      val last_name = update.last_name.getOrElse(profile.last_name)
      val public_key = update.public_key.getOrElse(profile.public_key)
      val albums = update.albums.getOrElse(profile.albums)
      val photos = update.photos.getOrElse(profile.photos)
      val likedpages = update.likedpages.getOrElse(profile.likedpages)
      val userposts = update.userposts.getOrElse(profile.userposts)
      val hidden = update.hiddenValue.getOrElse(profile.hiddenValue)
      Profile(id, /*birthday, email, */first_name, gender, last_name, public_key, albums, photos, likedpages, userposts, token="", hidden)
    }

    getProfile(id).flatMap { maybeProfile =>
      maybeProfile match {
        case None => Future { None } // No profile found, nothing to update
        case Some(profile) =>
          val updatedProfile = updateEntity(profile)
          deleteProfile(id).flatMap { _ =>
            createProfile(updatedProfile).map(_ => Some(updatedProfile))
          }
      }
    }
  }

  def deleteProfile(id: Int): Future[Unit] = Future {
    profiles = profiles -- Set(id)
  }
}
