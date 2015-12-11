package services

/**
 * Created by gokul on 11/29/15.
 */
import entities.{FriendList, FriendListUpdate}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class FriendListService(implicit val executionContext: ExecutionContext) {
 
   var friendLists = Vector.empty[FriendList] // TODO: Change logic to ArrayBuffer

   def initiateFriends (noOfUsers:Int) : Future[Option[String]] = Future {
     println("In Initiate Friends"+noOfUsers)
     for (i <- 0 until noOfUsers){
       var percent = Random.nextInt(10)
       if (percent == 0) percent = 1
       var numFriends = Random.nextInt((noOfUsers*percent*0.01).toInt)
       var list:List[Int] = List(1)
       var j = 0
       while (j < numFriends ){
         var userID = Random.nextInt(noOfUsers)
         if (!list.contains(userID)) {
           list = userID :: list
           j+=1;
         }
       }
       //println(list)
       var friendList = new FriendList(i,list)
       createFriendList(friendList)
     }
     Some(noOfUsers.toString)
   }
   def createFriendList(friendList: FriendList): Future[Option[String]] = Future {
     friendLists.find(_.id == friendList.id) match {
       case Some(q) => None // Conflict! id is already taken
       case None =>
         //println(friendList)
         friendLists = friendLists :+ friendList
         Some(friendList.id.toString)
     }
   }
   def getFriendList(id: Int): Future[Option[FriendList]] = Future {
     friendLists.find(_.id == id)
   }
 
   def updateFriendList(id: Int, update: FriendListUpdate): Future[Option[FriendList]] = {
     def updateEntity(friendList: FriendList): FriendList = {
       var list = friendList.list
       val ut = update.list.getOrElse(-1)
       if (update.list != None && !list.contains(ut))
         list = ut::list
       FriendList(id, list)
     }
 
     getFriendList(id).flatMap { maybeFriendList =>
       maybeFriendList match {
         case None => Future { None } // No friendList found, nothing to update
         case Some(friendList) =>
           val updatedFriendList = updateEntity(friendList)
           deleteFriendList(id).flatMap { _ =>
             createFriendList(updatedFriendList).map(_ => Some(updatedFriendList))
           }
       }
     }
   }

   def deleteFriendList(id: Int): Future[Unit] = Future {
     friendLists = friendLists.filterNot(_.id == id)
   }
}
