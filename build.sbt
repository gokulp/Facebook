//name := "Facebook"
//
//version := "1.0"
//
//scalaVersion := "2.11.7"
//
//resolvers += "spray repo" at "http://repo.spray.io"
//
//val sprayVersion = "1.3.2"
//val Json4sVersion = "3.2.10"
//
//libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-actor" % "2.3.5",
//  "com.typesafe.akka" %% "akka-http-experimental" % "0.7",
//  "io.spray" %% "spray-can" % sprayVersion,
//  "io.spray" %% "spray-routing" % sprayVersion,
//  "io.spray" %% "spray-json" % "1.3.1",
//  "io.spray" %% "spray-client" % "1.3.2",
//  "io.spray" %% "spray-testkit" % "1.3.2" % "test",
//  "org.json4s" %% "json4s-native" % Json4sVersion,
//  "org.json4s" %% "json4s-ext" % Json4sVersion,
//  "com.typesafe.akka" %% "akka-remote" % "2.3.5",
//  "io.spray" %% "spray-testkit" % sprayVersion % "test",
//  "org.specs2" %% "specs2" % "2.3.13" % "test"
//)
enablePlugins(JavaServerAppPackaging)

name := "facebook"

version := "0.1"

//organization := "com.danielasfregola"

scalaVersion := "2.11.5"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray Repository"    at "http://repo.spray.io")

libraryDependencies ++= {
  val AkkaVersion       = "2.3.9"
  val SprayVersion      = "1.3.2"
  val Json4sVersion     = "3.2.11"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % AkkaVersion,
    "io.spray"          %% "spray-can"       % SprayVersion,
    "io.spray"          %% "spray-routing"   % SprayVersion,
    "io.spray"          %% "spray-client"   % SprayVersion,
    "io.spray"          %% "spray-json"      % "1.3.1",
    "com.typesafe.akka" %% "akka-slf4j"      % AkkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.1.2",
    "org.json4s"        %% "json4s-native"   % Json4sVersion,
    "org.json4s"        %% "json4s-ext"      % Json4sVersion,
    "com.typesafe.akka" %% "akka-testkit"    % AkkaVersion  % "test",
    "io.spray"          %% "spray-testkit"   % SprayVersion % "test",
    "org.specs2"        %% "specs2"          % "2.3.13"     % "test"
//    ,"org.apache.commons" % "commons-lang3" % "3.1",
//    "org.apache.commons" % "commons-codec" % "1.9"
  )
}

// Assembly settings
mainClass in Global := Some("facebook")

jarName in assembly := "facebook.jar"
