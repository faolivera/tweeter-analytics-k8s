import scala.sys.process._
import com.typesafe.sbt.packager.docker.Cmd

name := "id-service"

version := "0.1"

scalaVersion := "2.12.8"

lazy val akkaVersion = "2.5.21"
lazy val akkaGrpcVersion = "0.6.0"

enablePlugins(AkkaGrpcPlugin)

akkaGrpcGeneratedSources := Seq(AkkaGrpc.Server)

// ALPN agent
enablePlugins(JavaAgent)
javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.7" % "runtime;test"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

enablePlugins(DockerPlugin)
enablePlugins(JavaServerAppPackaging)

dockerBaseImage := "openjdk:8u191-jre-alpine3.9"

inConfig(Docker)(
  Seq(
    dockerCommands += Cmd("USER", (daemonUser in Docker).value),
    dockerCommands += Cmd("RUN", "apk add --update bash && rm -rf /var/cache/apk/*"),
    defaultLinuxInstallLocation := "/opt/docker",
    daemonUser := "root",
    maintainer := "Facundo Olivera <>",
    version := s"${version.value}-$getDockerVersion",
    packageName := "eu.gcr.io/hypnotic-epoch-235613/" + name.value,
    mainClass := Some("com.github.faolivera.idservice.IdServer"),
    dockerExposedPorts ++= Seq(8080)
  )
)

def getDockerVersion: String = "git rev-parse --short HEAD".!!.stripLineEnd