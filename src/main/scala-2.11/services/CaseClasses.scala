package services

import entities.{PageUpdate, Page}

/**
 * Created by gokul on 12/12/15.
 */
case class CreatePage(page: Page)
case class GetPage(id: Int)
case class UpdatePage(id:Int, update:PageUpdate)
case class DeletePage(id:Int)
case class InitiatePages(noOfPages:Int, noOfUsers:Int)
