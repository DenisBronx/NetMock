package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMock
import org.junit.rules.ExternalResource

/**
 * JUnit rule that starts and shuts down a [NetMockServer] for you
 */
class NetMockServerRule(
    private val server: NetMockServer = NetMockServer()
) : ExternalResource(), NetMock by server {

    init {
        server.start()
    }

    val baseUrl = server.baseUrl
    val interceptor = server.interceptor

    override fun after() {
        server.shutDown()
        super.after()
    }
}
