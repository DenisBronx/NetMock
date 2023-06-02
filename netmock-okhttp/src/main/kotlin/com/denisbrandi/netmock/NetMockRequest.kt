package com.denisbrandi.netmock

data class NetMockRequest(
    val path: String = "/",
    val method: Method = Method.Custom(""),
    val containsHeaders: Map<String, String> = emptyMap(),
    val params: Map<String, String> = emptyMap(),
    val body: String = "",
)

class NetMockRequestBuilder {
    lateinit var path: String
    lateinit var method: Method
    lateinit var containsHeaders: Map<String, String>
    lateinit var params: Map<String, String>
    lateinit var body: String

    init {
        fromRequest(NetMockRequest())
    }

    fun fromRequest(request: NetMockRequest) {
        path = request.path
        method = request.method
        containsHeaders = request.containsHeaders
        params = request.params
        body = request.body
    }

    fun fromBuilder(builder: NetMockRequestBuilder) {
        path = builder.path
        method = builder.method
        containsHeaders = builder.containsHeaders
        params = builder.params
        body = builder.body
    }

    fun build(): NetMockRequest {
        return NetMockRequest(
            path = path,
            method = method,
            containsHeaders = containsHeaders,
            params = params,
            body = body
        )
    }
}