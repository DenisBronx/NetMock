package com.denisbrandi.netmock.mappers

import com.denisbrandi.netmock.NetMockResponse

interface ResponseMapper<Response : Any> {
    fun mapResponse(netMockResponse: NetMockResponse): Response
}
