import com.typesafe.sbt.packager.docker.Cmd

name := "tweet-collect"
 
version := "1.0" 
      
lazy val `twitcollectapi` = (project in file(".")).enablePlugins(PlayScala).enablePlugins(DockerPlugin)

dockerBaseImage := "openjdk:8u191-jre-alpine3.9"

inConfig(Docker)(
  Seq(
    dockerCommands += Cmd("RUN", "apk add --update bash && rm -rf /var/cache/apk/*"),
    defaultLinuxInstallLocation := "/opt/docker",
    daemonUser := "root",
    maintainer := "Facundo Olivera <>",
    version := s"${version.value}-$getDockerVersion",
    packageName := "eu.gcr.io/hypnotic-epoch-235613/" + name.value
  )
)


def getDockerVersion: String = "git rev-parse --short HEAD".!!.stripLineEnd

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  ws,
  guice,
  "org.apache.kafka" % "kafka-clients" % "2.1.1",
  specs2 % Test
)