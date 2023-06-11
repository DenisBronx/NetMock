package com.denisbrandi.netmock.engine.mappers

import com.denisbrandi.netmock.NetMockResponse
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.date.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal object KtorResponseMapper {

    fun mapResponse(netMockResponse: NetMockResponse): HttpResponseData {
        return HttpResponseData(
            statusCode = HttpStatusCode(netMockResponse.code, ""),
            requestTime = GMTDate(null),
            headers = HeadersBuilder().apply {
                netMockResponse.mandatoryHeaders.forEach {
                    append(it.key, it.value)
                }
            }.build(),
            body = ByteReadChannel(netMockResponse.body),
            version = HttpProtocolVersion.HTTP_1_1,
            callContext = CoroutineScope(Dispatchers.Unconfined).coroutineContext
        )
    }
}
