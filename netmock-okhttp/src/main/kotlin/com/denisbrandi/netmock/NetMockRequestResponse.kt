package com.denisbrandi.netmock

data class NetMockRequestResponse(val request: NetMockRequest, val response: NetMockResponse)

data class NetMockRequest(
    val path: String = "/",
    val method: Method,
    val containsHeaders: Map<String, String> = emptyMap(),
    val params: Map<String, String> = emptyMap(),
    val body: String = "",
)

enum class Method {
    GET, HEAD, POST, PUT, DELETE, PATCH
}

data class NetMockResponse(
    val code: Int = 200,
    val containsHeaders: Map<String, String> = emptyMap(),
    val body: String = ""
)