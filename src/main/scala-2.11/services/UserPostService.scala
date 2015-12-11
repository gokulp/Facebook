package services

/**
 * Created by gokul on 11/29/15.
 */
import entities.{UserPost, UserPostUpdate}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class UserPostService(implicit val executionContext: ExecutionContext) {
 
   var userPosts = Vector.empty[UserPost] // TODO: Change logic to ArrayBuffer
   def initiatePosts(noOfUsers:Int): Future[Option[String]] = Future {
      for (i <- 1 until noOfUsers * 5){
        var userID = Random.nextInt(noOfUsers)
        var posts = new UserPost(i, userID, "dfdsafsdaf","fdarewrer", "rweteryjojpjo")
        createPost(posts)
      }
      Some(noOfUsers.toString)
    }
   def createPost(userPost: UserPost): Future[Option[String]] = Future {
     userPosts.find(_.id == userPost.id) match {
       case Some(q) => None // Conflict! id is already taken
       case None =>
         //println(userPost)
         userPosts = userPosts :+ userPost
         Some(userPost.id.toString)
     }
   }
 
   def getPost(id: Int): Future[Option[UserPost]] = Future {
     userPosts.find(_.id == id)
   }
 
   def updatePost(id: Int, update: UserPostUpdate): Future[Option[UserPost]] = {
     def updateEntity(userPost: UserPost): UserPost = {
       val from = update.from.getOrElse(userPost.from)
       val caption = update.caption.getOrElse(userPost.caption)
       val object_id = update.object_id.getOrElse(userPost.object_id)
       val message = update.message.getOrElse(userPost.message)
       UserPost(id, from, caption, object_id, message)
     }
 
     getPost(id).flatMap { maybePost =>
       maybePost match {
         case None => Future { None } // No userPost found, nothing to update
         case Some(userPost) =>
           val updatedPost = updateEntity(userPost)
           deletePost(id).flatMap { _ =>
             createPost(updatedPost).map(_ => Some(updatedPost))
           }
       }
     }
   }
 
   def deletePost(id: Int): Future[Unit] = Future {
     userPosts = userPosts.filterNot(_.id == id)
   }
 }
