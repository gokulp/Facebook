package services

/**
 * Created by gokul on 11/29/15.
 */
import entities.{FollowerList, FriendList, FriendListUpdate}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class FriendListService(implicit val executionContext: ExecutionContext) {
 
   var friendLists = mutable.HashMap[Int, FriendList]()
   var followList = mutable.HashMap[Int, FollowerList]()

/*
   def initiateFriends (noOfUsers:Int) : Future[Option[String]] = Future {
     println("In Initiate Friends"+noOfUsers)
     for (i <- 0 until noOfUsers) {
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
*/

   def createFriendList(friendList: FriendList): Future[Option[String]] = Future {
     friendLists.get(friendList.id) match {
       case Some(q) => None // Conflict! id is already taken
       case None =>
         //println(friendList)
         friendLists(friendList.id) = friendList
         followList(friendList.id) = new FollowerList(friendList.id, friendList.list)
         Some(friendList.id.toString)
     }
   }

   def getFriendList(id: Int): Future[Option[FriendList]] = Future {
     friendLists.get(id)
   }

   def addNewFriend(idFrom:Int, idTo:Int, idFromKey:String, idToKey:String): Future[Option[String]] = Future {
     if (followList.contains(idTo) && followList(idTo).list.contains(idFrom)){
       //Confirmation received for friend request
       var fromList = idTo   :: friendLists(idFrom).list
       var toList   = idFrom :: friendLists(idTo).list
       var fromListKey = idToKey   :: friendLists(idFrom).keys
       var toListKey   = idFromKey :: friendLists(idTo).keys
       var friends = new FriendList(idTo, toList, fromListKey, token="")
       friendLists(idTo) = friends
       friends = new FriendList(idFrom, fromList, toListKey, token="")
       friendLists(idFrom) = friends
       var toFollowing = followList(idTo).list
       toFollowing = toFollowing.filterNot(Set(idFrom))
       var followers = new FollowerList(idTo, toFollowing)
       followList(idTo) = followers
     }
     Some("Added")
   }

  def followFriend(idFrom:Int, idTo:Int) : Future[Option[String]] = Future{
    if (followList.contains(idFrom)) {
      if (!followList(idFrom).list.contains(idTo)) {
        //request received just add to followList
        var list = idTo :: followList(idFrom).list
        var following = new FollowerList(idFrom, list)
        followList(idFrom) = following
      }
    }
    Some("Following")
  }

  def unfriend(idFrom:Int, idTo:Int): Future[Option[String]] = Future{
    if (friendLists.contains(idFrom) && friendLists.contains(idTo) &&  friendLists(idFrom).list.contains(idTo)){
      var keyToDrop = friendLists(idFrom).keys(friendLists(idFrom).list.indexOf(idTo))
      var ls = friendLists(idFrom).list.filterNot(Set(idTo))
      var keys = friendLists(idFrom).keys.filterNot(Set(keyToDrop))
      var friends = new FriendList(idFrom, ls, keys, token="")
      friendLists(idFrom) = friends

      keyToDrop = friendLists(idTo).keys(friendLists(idTo).list.indexOf(idFrom))
      ls = friendLists(idTo).list.filterNot(Set(idFrom))
      keys = friendLists(idTo).keys.filterNot(Set(keyToDrop))
      friends = new FriendList(idTo, ls, keys, token="")
      friendLists(idTo) = friends
    }
    Some("Unfriend Request Processed")
  }

   def deleteFriendList(id: Int): Future[Unit] = Future {
     friendLists = friendLists -- Set(id)
   }
}
