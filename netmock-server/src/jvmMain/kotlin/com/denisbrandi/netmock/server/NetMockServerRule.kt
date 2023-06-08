package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMock
import org.junit.rules.ExternalResource

/**
 * JUnit rule that starts and shuts down a [NetMockServer] for you
 */
class NetMockServerRule(
    private val netMock: NetMockServer = NetMockServer()
) : ExternalResource(), NetMock by netMock {

    init {
        netMock.start()
    }

    override fun after() {
        netMock.shutDown()
        super.after()
    }
}