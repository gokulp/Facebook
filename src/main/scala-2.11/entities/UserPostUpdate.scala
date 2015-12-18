package entities

/**
 * Created by gokul on 11/29/15.
 */
case class UserPostUpdate(
                           from:Int,
//                           caption:Option[String] = None,
//                           object_id: Option[String] = None,
//                           message:Option[String] = None,
                           privacy:Option[String] = None,
//                           sharedWith:Option[List[Int]] = None,
                           token:String,
                           hiddenValue:Option[String] = None)