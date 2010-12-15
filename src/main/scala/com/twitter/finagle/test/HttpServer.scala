package com.twitter.finagle.test

import java.net.InetSocketAddress

import org.jboss.netty.buffer._
import org.jboss.netty.channel._
import org.jboss.netty.handler.codec.http._

import com.twitter.ostrich
import com.twitter.finagle.builder._
import com.twitter.finagle.service._

import com.twitter.util.Future
import com.twitter.ostrich.RuntimeEnvironment

object HttpServer extends ostrich.Service {
  def main(args: Array[String]) {
    val runtime = new RuntimeEnvironment(getClass)

    val config = new ostrich.Config {
      def telnetPort = 0
      def httpBacklog = 0
      def httpPort = 8889
      def jmxPackage = None
    }

    ostrich.ServiceTracker.register(this)
    ostrich.ServiceTracker.startAdmin(config, runtime)

    val server = new Service[HttpRequest, HttpResponse] {
      def apply(request: HttpRequest) = Future {
        val response = new DefaultHttpResponse(
          HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        response.setContent(ChannelBuffers.wrappedBuffer("yo".getBytes))
        response
      }
    }

    ServerBuilder()
     .codec(Http)
     .reportTo(Ostrich())
     .service(server)
     .bindTo(new InetSocketAddress(10000))
     .build
  }

  def quiesce() = ()
  def shutdown() = ()
}
