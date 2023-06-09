package com.denisbrandi.netmock

data class NetMockResponse(
    val code: Int = 200,
    val containsHeaders: Map<String, String> = emptyMap(),
    val body: String = ""
)

class NetMockResponseBuilder {
    var code: Int = 0
    lateinit var containsHeaders: Map<String, String>
    lateinit var body: String

    init {
        fromResponse(NetMockResponse())
    }

    fun fromBuilder(builder: NetMockResponseBuilder) {
        code = builder.code
        containsHeaders = builder.containsHeaders
        body = builder.body
    }

    fun fromResponse(response: NetMockResponse) {
        code = response.code
        containsHeaders = response.containsHeaders
        body = response.body
    }

    fun build(): NetMockResponse {
        return NetMockResponse(
            code = code,
            containsHeaders = containsHeaders,
            body = body
        )
    }
}
