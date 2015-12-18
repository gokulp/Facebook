package services

/**
 * Created by gokul on 11/29/15.
 */
import entities.{UserPost, UserPostUpdate}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class UserPostService(implicit val executionContext: ExecutionContext) {
 
   var userPosts = mutable.HashMap[Int, UserPost]()

   def createPost(userPost: UserPost): Future[Option[String]] = Future {
     userPosts.get(userPost.id) match {
       case Some(q) => None // Conflict! id is already taken
       case None =>
         //println(userPost)
         userPosts(userPost.id) = userPost
         Some(userPost.id.toString)
     }
   }
 
   def getPost(id: Int): Future[Option[UserPost]] = Future {
     userPosts.get(id)
   }
 
   def updatePost(id: Int, update: UserPostUpdate): Future[Option[UserPost]] = {
     def updateEntity(userPost: UserPost): UserPost = {
       val from = userPost.from
       val byPage = userPost.byPage
//       val caption = update.caption.getOrElse(userPost.caption)
//       val object_id = update.object_id.getOrElse(userPost.object_id)
//       val message = update.message.getOrElse(userPost.message)
       val privacy = update.privacy.getOrElse(userPost.privacy)
//       val sharedWith = update.sharedWith.getOrElse(userPost.sharedWith)
       val hidden = update.hiddenValue.getOrElse(userPost.hiddenValue)
       UserPost(id, byPage, from, /*caption, object_id, message,*/ privacy, /*sharedWith,*/ "", hidden)
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
     userPosts = userPosts -- Set(id)
   }
 }
