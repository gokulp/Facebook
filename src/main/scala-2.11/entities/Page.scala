package entities

/**
 * Created by gokul on 11/29/15.
 */
case class Page(id:Int, /*page ID*/
                access_token:String, /* access_token for the page - only possesed by the administrators */
                can_checkin:Boolean, /* can user checkin with this page */
                can_post:Boolean, /* can user post on this page */
                email:List[String], /* List of the email addresses of the administrators or organization */
                username:String, /* name of the page */
                likes:Long) /* number of likes on the page */
