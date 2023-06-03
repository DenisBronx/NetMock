package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.NetMock
import org.junit.rules.ExternalResource

class OkHttpRule(
    private val netMock: OkHttpMock = OkHttpMock()
) : ExternalResource(), NetMock by netMock {

    init {
        netMock.start()
    }

    override fun after() {
        netMock.shutDown()
        super.after()
    }
}