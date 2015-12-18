package entities

import scala.collection.mutable.ArrayBuffer

/**
 * Created by gokul on 11/29/15.
 */
case class PendingRequests(id:Int, list: ArrayBuffer[Int], keys: ArrayBuffer[String]);