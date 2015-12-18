package services

import java.security.PublicKey

import entities.{Authentication, Profile}
import org.apache.commons.codec.binary.Base64
import security.RSAEncryptor

import scala.collection.mutable
import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by gokul on 12/16/15.
 */
class SecurityService (implicit val executionContext: ExecutionContext){
  val key = RSAEncryptor.generateKey()
  val publicKey = key.getPublic
  val privateKey = key.getPrivate
  var userPublicKeys = mutable.HashMap[Int, PublicKey]()
  var secureNumberSent = mutable.HashMap[Int, Long]()

  def getPublicKey(): Future[Authentication] = Future {
    var toReturn = new Authentication(-1, publicKey.toString)
    toReturn
  }
  def getUserPublicKey(id:Int): Authentication = {
    var toReturn = new Authentication(-1, "")
    if (userPublicKeys.contains(id)){
      toReturn = new Authentication(-1, userPublicKeys(id).toString)
      return (toReturn)
    }
    toReturn
  }

  def addPublicKeyForUser(id:Int, key:String): Boolean ={
    try {
      userPublicKeys(id) = RSAEncryptor.getPublicKeyFromString(key)
      return(true)
    } catch {
      case e:Exception => println("Error occured")
    }
    false
  }

  def getSecureToken(id:Int): Future[Authentication] = Future {
    var encrypted:String = ""
    var token = new Authentication(id, encrypted)
    if (userPublicKeys.contains(id)) {
      val num = RSAEncryptor.getSecureRandomNumber()
      secureNumberSent(id) = num
      encrypted = RSAEncryptor.encrypt(num, userPublicKeys(id))
      println("Sending "+ num+" token as "+ encrypted)
      //encrypted = RSAEncryptor.signContent(encrypted, privateKey)
      token = new Authentication(id, encrypted)
    }
    token
  }

  def authenticateUser(id:Int, token:String): String = {
    try {
      if (userPublicKeys.contains(id)) {
        val num = RSAEncryptor.authenticateSign(Base64.decodeBase64(token), userPublicKeys(id))
        println("Decrypted Content" + num)
        if (num == secureNumberSent(id)) {
          secureNumberSent(id) = RSAEncryptor.getSecureRandomNumber
          return "Yes"
        }
      }
      "No"
    } catch {
      case e: Exception =>
        println("Exception occured")
        return "No"
    }
  }
}
