name := "Ascetic"
version := "0.1"
scalaVersion := "3.6.1"

enablePlugins(AssemblyPlugin)

assembly / mainClass := Some("Main")

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

libraryDependencies ++= Seq(
    "com.google.code.gson" % "gson" % "2.10.1"
)