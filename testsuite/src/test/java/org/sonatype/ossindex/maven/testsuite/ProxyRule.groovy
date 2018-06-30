/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.ossindex.maven.testsuite

import java.lang.reflect.Method

import javax.annotation.Nullable

import com.google.common.net.HostAndPort
import groovy.util.logging.Slf4j
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import org.junit.rules.ExternalResource
import org.littleshoot.proxy.DefaultHostResolver
import org.littleshoot.proxy.HostResolver
import org.littleshoot.proxy.HttpFilters
import org.littleshoot.proxy.HttpFiltersAdapter
import org.littleshoot.proxy.HttpFiltersSourceAdapter
import org.littleshoot.proxy.HttpProxyServer
import org.littleshoot.proxy.HttpProxyServerBootstrap
import org.littleshoot.proxy.impl.DefaultHttpProxyServer

import static com.google.common.base.Preconditions.checkState

/**
 * HTTP-proxy rule.
 */
@Slf4j
class ProxyRule
  extends ExternalResource
{
  private final HttpProxyServerBootstrap bootstrap

  @Nullable
  private HttpProxyServer server

  ProxyRule(final HttpProxyServerBootstrap bootstrap) {
    this.bootstrap = bootstrap
  }

  ProxyRule() {
    this(DefaultHttpProxyServer.bootstrap())
  }

  private HttpProxyServerBootstrap customize(final HttpProxyServerBootstrap bootstrap) {
    return bootstrap.withFiltersSource(new HttpFiltersSourceAdapter() {
      @Override
      HttpFilters filterRequest(final HttpRequest originalRequest, final ChannelHandlerContext ctx) {
        return new HttpFiltersImpl(originalRequest)
      }
    })
  }

  @Override
  protected void before() throws Throwable {
    server = customize(bootstrap).start()
  }

  @Override
  protected void after() {
    server?.stop()
    server = null
  }

  int getPort() {
    checkState(server != null)
    return server.listenAddress.port
  }

  //
  // HttpFiltersImpl
  //

  private class HttpFiltersImpl
    extends HttpFiltersAdapter
  {
    HttpFiltersImpl(final HttpRequest originalRequest) {
      super(originalRequest)
    }

    private final HostResolver hostResolver = new DefaultHostResolver()

    @Override
    HttpResponse clientToProxyRequest(final HttpObject httpObject) {
      log.info("Client -> Proxy: \n----8<----\n$httpObject\n---->8----")
      return null
    }

    @Override
    HttpResponse proxyToServerRequest(final HttpObject httpObject) {
      log.info("Proxy -> Server: \n----8<----\n$httpObject\n---->8----")
      return null
    }

    /**
     * Workaround https://github.com/adamfisk/LittleProxy/issues/398 by overriding default parsing.
     */
    @Override
    InetSocketAddress proxyToServerResolutionStarted(final String resolvingServerHostAndPort) {
      try {
        HostAndPort hostAndPort = HostAndPort.fromString(resolvingServerHostAndPort)
        String host = getHost(hostAndPort)
        int port = hostAndPort.getPortOrDefault(80)
        return hostResolver.resolve(host, port)
      }
      catch (Exception e) {
        log.warn("Problem resolving: {}", resolvingServerHostAndPort, e)

        // we can not resolve this host; return null to fall back to normal DNS resolution
        return null
      }
    }

    private String getHost(final HostAndPort hostAndPort) {
      Method method
      try {
        // first try modern guava method
        method = HostAndPort.class.getMethod('getHost')
      }
      catch (NoSuchMethodException e) {
        // fallback to pre guava 20 method
        method = HostAndPort.class.getMethod('getHostText')
      }

      log.trace("Selected 'host' method: $method")
      return method.invoke(hostAndPort)
    }
  }
}
