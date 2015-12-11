package services

/**
 * Created by gokul on 11/29/15.
 */
import entities.{AlbumUpdate, Album, Profile, ProfileUpdate}
import services.GenerateRandomStuff._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ProfileService(implicit val executionContext: ExecutionContext) {

  var profiles = Vector.empty[Profile] // TODO: Change logic to ArrayBuffer

  def initiateProfiles(noOfUsers:Int):Future[Option[String]] = Future{
    for (i <- 0 until noOfUsers){
      val dummyProfile:Profile = new Profile(i,getDOB,List(getEmail),"dummy",getGender(Random.nextInt(2)),"dummy","faoisyfowefhjkasdnfkasdhuoiusof", List(i), List())
      profiles = profiles :+ dummyProfile
    }
    Some(noOfUsers.toString)
  }

  def createProfile(profile: Profile): Future[Option[String]] = Future {
    profiles.find(_.id == profile.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        //println(profile)
        profiles = profiles :+ profile
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
          var upProfile:ProfileUpdate = new ProfileUpdate(albums = Option(profile.albums), photos = Option(newPhotos))
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
          val upProfile:ProfileUpdate = new ProfileUpdate(albums = Option(newAlbums))
          updateProfile(profile.id, upProfile)
      }
    }
  }

  def getProfile(id: Int): Future[Option[Profile]] = Future {
    profiles.find(_.id == id)
  }

  def updateProfile(id: Int, update: ProfileUpdate): Future[Option[Profile]] = {

    def updateEntity(profile: Profile): Profile = {
      val birthday = update.birthday.getOrElse(profile.birthday)
      val email = update.email.getOrElse(profile.email)
      val first_name = update.first_name.getOrElse(profile.first_name)
      val gender = update.gender.getOrElse(profile.gender)
      val last_name = update.last_name.getOrElse(profile.last_name)
      val public_key = update.public_key.getOrElse(profile.public_key)
      val albums = update.albums.getOrElse(profile.albums)
      val photos = update.photos.getOrElse(profile.photos)
      Profile(id, birthday, email, first_name, gender, last_name, public_key, albums, photos)
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
    profiles = profiles.filterNot(_.id == id)
  }
}
