package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.mappers.ResponseMapper
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.date.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*

internal object KtorResponseMapper : ResponseMapper<HttpResponseData> {

    override fun mapResponse(netMockResponse: NetMockResponse): HttpResponseData {
        return HttpResponseData(
            statusCode = HttpStatusCode(netMockResponse.code, ""),
            requestTime = GMTDate(null),
            headers = HeadersBuilder().apply {
                netMockResponse.containsHeaders.forEach {
                    append(it.key, it.value)
                }
            }.build(),
            body = ByteReadChannel(netMockResponse.body),
            version = HttpProtocolVersion.HTTP_1_1,
            callContext = CoroutineScope(Dispatchers.Unconfined).coroutineContext
        )
    }
}
