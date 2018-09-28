package com.nickname.app

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyLauncher {
  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8081

    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
    context.setResourceBase("src/main/webapp")
//    context.setDescriptor("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<web-app xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n  xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee \n  http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd\"\n  version=\"3.1\">\n\n  <!--\n    This listener loads a class in the default package called ScalatraBootstrap.\n    That class should implement org.scalatra.LifeCycle.  Your app can be\n    configured in Scala code there.\n  -->\n  <listener>\n    <listener-class>org.scalatra.servlet.ScalatraListener</listener-class>\n  </listener>\n</web-app>")
    context.setInitParameter(ScalatraListener.LifeCycleKey, "ScalatraBootstrap")
    context.addEventListener(new ScalatraListener)

    server.setHandler(context)

    server.start
    server.join
  }
}
