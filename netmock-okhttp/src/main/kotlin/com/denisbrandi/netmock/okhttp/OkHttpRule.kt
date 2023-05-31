package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.NetMock
import org.junit.rules.ExternalResource

class OkHttpRule(
    private val netMock: NetMock = OkHttpMock()
) : ExternalResource(), NetMock by netMock {

    override fun after() {
        netMock.shutDown()
        super.after()
    }
}