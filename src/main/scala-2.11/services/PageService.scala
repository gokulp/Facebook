package services

/**
 * Created by gokul on 11/29/15.
 */
import entities.{Page, PageUpdate}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class PageService(implicit val executionContext: ExecutionContext) {
 
   var pages = Vector.empty[Page] // TODO: Change logic to ArrayBuffer

   def initiatePages(noOfPages:Int, noOfUsers:Int): Future[Option[String]] = Future{
     for (i <- 0 until noOfPages){
       var likes = Random.nextInt(noOfUsers+1)
       var dummyPage = new Page(i, "fdufweklasdnkfhasojf", true, true, List(GenerateRandomStuff.getEmail),"dummyPage",likes)
       createPage(dummyPage)
     }
     Some(noOfPages.toString)
   }
   def createPage(page: Page): Future[Option[String]] = Future {
     pages.find(_.id == page.id) match {
       case Some(q) => None // Conflict! id is already taken
       case None =>
         //println(page)
         pages = pages :+ page
         Some(page.id.toString)
     }
   }
 
   def getPage(id: Int): Future[Option[Page]] = Future {
     pages.find(_.id == id)
   }
 
   def updatePage(id: Int, update: PageUpdate): Future[Option[Page]] = {
     def updateEntity(page: Page): Page = {
       val access_token = update.access_token.getOrElse(page.access_token)
       val email = update.email.getOrElse(page.email)
       val can_checkin = update.can_checkin.getOrElse(page.can_checkin)
       val can_post = update.can_post.getOrElse(page.can_post)
       val username = update.username.getOrElse(page.username)
       val likes = update.likes.getOrElse(page.likes)
       Page(id, access_token, can_checkin, can_post, email, username, likes)
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
     pages = pages.filterNot(_.id == id)
   }
 }
