package com.denisbrandi.netmock

data class NetMockResponse(
    val code: Int = 200,
    val containsHeaders: Map<String, String> = emptyMap(),
    val body: String = ""
)

class NetMockResponseBuilder {
    private val defaultResponse = NetMockResponse()
    var code: Int = defaultResponse.code
    var containsHeaders: Map<String, String> = defaultResponse.containsHeaders
    var body: String = defaultResponse.body
    fun build(): NetMockResponse {
        return NetMockResponse(
            code = code,
            containsHeaders = containsHeaders,
            body = body
        )
    }
}