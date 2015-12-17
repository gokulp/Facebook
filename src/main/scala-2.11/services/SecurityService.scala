package services

import java.security.PublicKey

import entities.Profile
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

  def getPublicKey(): Future[String] = Future {
    publicKey.toString
  }
  def getUserPublicKey(id:Int): String={
    if (userPublicKeys.contains(id)){
      return (userPublicKeys(id).toString)
    }
    "None"
  }

  def addPublicKeyForUser(id:Int, key:String): Unit ={
    try {
      userPublicKeys(id) = RSAEncryptor.getPublicKeyFromString(key)
    } catch {
      case e:Exception => println("Error occured")
    }
  }

  def getSecureToken(id:Int): Future[String] = Future {
    var encrypted:String = ""
    if (userPublicKeys.contains(id)) {
      val num = RSAEncryptor.getSecureRandomNumber()
      secureNumberSent(id) = num
      encrypted = RSAEncryptor.encrypt(num, userPublicKeys(id))
      //encrypted = RSAEncryptor.signContent(encrypted, privateKey)
    }
    encrypted
  }

  def authenticateUser(id:Int, token:String): String = {
    try {
      if (userPublicKeys.contains(id)) {
        val num = RSAEncryptor.authenticateSign(Base64.decodeBase64(token), userPublicKeys(id))
        //println("Decrypted Content" + num)
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
