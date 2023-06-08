package com.denisbrandi.netmock

/**
 * Method: the HTTP method used for a [NetMockRequest]
 */
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

    /**
     * Custom: for non-ordinary HTTP methods.
     *
     * Example:
     *
     * val method = Custom("OPTIONS")
     *
     * When intercepting the requests, NetMock will match the [Method.name] with the real request's method.
     */
    data class Custom(override val name: String) : Method
}
