package com.denisbrandi.netmock

/**
 * @property code: response status code
 * @property mandatoryHeaders: headers that must be contained in the response
 * @property body: string value of the response body
 */
data class NetMockResponse(
    val code: Int = 200,
    val mandatoryHeaders: Map<String, String> = emptyMap(),
    val body: String = ""
)

class NetMockResponseBuilder {
    var code: Int = 0
    lateinit var mandatoryHeaders: Map<String, String>
    lateinit var body: String

    init {
        fromResponse(NetMockResponse())
    }

    fun fromBuilder(builder: NetMockResponseBuilder) {
        code = builder.code
        mandatoryHeaders = builder.mandatoryHeaders
        body = builder.body
    }

    fun fromResponse(response: NetMockResponse) {
        code = response.code
        mandatoryHeaders = response.mandatoryHeaders
        body = response.body
    }

    fun build(): NetMockResponse {
        return NetMockResponse(
            code = code,
            mandatoryHeaders = mandatoryHeaders,
            body = body
        )
    }
}
