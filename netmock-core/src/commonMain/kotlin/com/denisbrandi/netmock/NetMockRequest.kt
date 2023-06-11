package com.denisbrandi.netmock

/**
 * @property requestUrl: full url with scheme, host, path and query params
 * Example:
 * ```
 * https://google.com/somePath?1=2&3=4
 * ```
 * @property method: method used for the request, see [Method]
 * @property mandatoryHeaders: headers that must be contained in the request
 * @property body: string value of the request body
 */
data class NetMockRequest(
    val requestUrl: String = "",
    val method: Method = Method.Custom(""),
    val mandatoryHeaders: Map<String, String> = emptyMap(),
    val body: String = ""
)

class NetMockRequestBuilder {
    lateinit var requestUrl: String
    lateinit var method: Method
    lateinit var mandatoryHeaders: Map<String, String>
    lateinit var body: String

    init {
        fromRequest(NetMockRequest())
    }

    fun fromRequest(request: NetMockRequest) {
        requestUrl = request.requestUrl
        method = request.method
        mandatoryHeaders = request.mandatoryHeaders
        body = request.body
    }

    fun fromBuilder(builder: NetMockRequestBuilder) {
        requestUrl = builder.requestUrl
        method = builder.method
        mandatoryHeaders = builder.mandatoryHeaders
        body = builder.body
    }

    fun build(): NetMockRequest {
        return NetMockRequest(
            requestUrl = requestUrl,
            method = method,
            mandatoryHeaders = mandatoryHeaders,
            body = body
        )
    }
}
