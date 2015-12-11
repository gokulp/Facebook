package services

/**
 * Created by gokul on 11/29/15.
 */
import entities.{Page, PageUpdate}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class StatisticService(implicit val executionContext: ExecutionContext) {
 
   var numRequestProcesed:Double = 0
   var prevtime = System.currentTimeMillis()

   def addOneMore(): Future[Option[Double]] = Future{
     numRequestProcesed += 1
     Some(numRequestProcesed)
   }

   def printStats(time:String): Future[Option[Double]] = Future {
     var curtime = System.currentTimeMillis();
     println("Num Request processed till now are : "+numRequestProcesed+" in "+(curtime - prevtime)+" Milliseconds")
     prevtime = curtime
     numRequestProcesed = 0
     Some(numRequestProcesed)
   }
 }
