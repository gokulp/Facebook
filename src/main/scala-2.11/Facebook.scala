import akka.actor.{Props, ActorSystem}
import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import services.{PageService, ProfileService}
import spray.can.Http

import scala.concurrent.duration.Duration

/**
 * Created by gokul on 11/29/15.
 */
object Facebook extends App {
    //check arguments here
    val config = ConfigFactory.load()
    val host = config.getString("http.host")
    val bindport = config.getInt("http.port")

    implicit val system = ActorSystem("facebook");
    implicit val executionContext = system.dispatcher
    implicit val timeout = new Timeout(Duration.create(10, "seconds"))
    //var pageServer = system.actorOf(Props(classOf[PageService], executionContext), name="pageServer")

    val api = system.actorOf(Props(new RestInterface))
    IO(Http).ask(Http.Bind(listener = api, interface = host, port = bindport))
      .mapTo[Http.Event]
      .map {
      case Http.Bound(address) =>
        println(s"REST interface bound to $address")
      case Http.CommandFailed(cmd) =>
        println("REST interface could not bind to " +
          s"$host:$bindport, ${cmd.failureMessage}")
        system.shutdown()
    }
}
