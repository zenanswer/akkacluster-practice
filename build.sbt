
name := "akkacluster-practice"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= {
  val akkaVersion = "2.5.22"
  Seq(
    "com.typesafe.akka"       %% "akka-actor"                        % akkaVersion,
    "com.typesafe.akka"       %% "akka-slf4j"                        % akkaVersion,
    "com.typesafe.akka"       %% "akka-remote"                       % akkaVersion,
    "com.typesafe.akka"       %% "akka-cluster"                      % akkaVersion,
    "com.typesafe.akka"       %% "akka-discovery"                    % akkaVersion,
    "com.typesafe.akka"       %% "akka-actor-typed"                  % akkaVersion,
    "com.typesafe.akka"       %% "akka-cluster-typed"                % akkaVersion,
    "com.typesafe.akka"       %% "akka-cluster-tools"                % akkaVersion,
    "com.typesafe.akka"       %% "akka-multi-node-testkit"           % akkaVersion   % "test",
    "com.typesafe.akka"       %% "akka-testkit"                      % akkaVersion   % "test",
    "org.scalatest"           %% "scalatest"                         % "3.0.0"       % "test",
    "com.typesafe.akka"       %% "akka-slf4j"                        % akkaVersion,
    "ch.qos.logback"          %  "logback-classic"                   % "1.0.10"
  )
}

resolvers += "aliyun" at "http://maven.aliyun.com/nexus/content/groups/public/"
resolvers += "163" at "http://mirrors.163.com/maven/repository/maven-central"
