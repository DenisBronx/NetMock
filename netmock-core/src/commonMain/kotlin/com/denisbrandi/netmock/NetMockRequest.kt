package com.denisbrandi.netmock

data class NetMockRequest(
    val requestUrl: String = "",
    val method: Method = Method.Custom(""),
    val containsHeaders: Map<String, String> = emptyMap(),
    val body: String = ""
)

class NetMockRequestBuilder {
    lateinit var requestUrl: String
    lateinit var method: Method
    lateinit var containsHeaders: Map<String, String>
    lateinit var body: String

    init {
        fromRequest(NetMockRequest())
    }

    fun fromRequest(request: NetMockRequest) {
        requestUrl = request.requestUrl
        method = request.method
        containsHeaders = request.containsHeaders
        body = request.body
    }

    fun fromBuilder(builder: NetMockRequestBuilder) {
        requestUrl = builder.requestUrl
        method = builder.method
        containsHeaders = builder.containsHeaders
        body = builder.body
    }

    fun build(): NetMockRequest {
        return NetMockRequest(
            requestUrl = requestUrl,
            method = method,
            containsHeaders = containsHeaders,
            body = body
        )
    }
}
