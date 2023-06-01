package com.denisbrandi.netmock

data class NetMockRequest(
    val path: String = "/",
    val method: Method = Method.Custom(""),
    val containsHeaders: Map<String, String> = emptyMap(),
    val params: Map<String, String> = emptyMap(),
    val body: String = "",
)

class NetMockRequestBuilder {
    private val defaultRequest = NetMockRequest()
    var path: String = defaultRequest.path
    var method: Method = defaultRequest.method
    var containsHeaders: Map<String, String> = defaultRequest.containsHeaders
    var params: Map<String, String> = defaultRequest.params
    var body: String = defaultRequest.body
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