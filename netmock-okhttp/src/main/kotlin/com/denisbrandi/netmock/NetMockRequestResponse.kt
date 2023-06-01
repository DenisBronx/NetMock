package com.denisbrandi.netmock

data class NetMockRequestResponse(val request: NetMockRequest, val response: NetMockResponse)

data class NetMockRequest(
    val path: String = "/",
    val method: Method,
    val containsHeaders: Map<String, String> = emptyMap(),
    val params: Map<String, String> = emptyMap(),
    val body: String = "",
)

sealed interface Method {
    val name: String

    object Get : Method {
        override val name = "GET"
    }

    object Head : Method {
        override val name = "HEAD"
    }

    object Post : Method {
        override val name = "POST"
    }

    object Put : Method {
        override val name = "PUT"
    }

    object Delete : Method {
        override val name = "DELETE"
    }

    object Patch : Method {
        override val name = "PATCH"
    }
}

data class NetMockResponse(
    val code: Int = 200,
    val containsHeaders: Map<String, String> = emptyMap(),
    val body: String = ""
)