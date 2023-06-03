package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMock
import org.junit.rules.ExternalResource

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