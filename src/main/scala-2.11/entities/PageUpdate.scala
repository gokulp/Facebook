package entities

/**
 * Created by gokul on 11/29/15.
 */
case class PageUpdate(id:Int, access_token:Option[String], can_checkin:Option[Boolean], can_post:Option[Boolean], email:Option[List[String]], username:Option[String], likes:Option[Long])
