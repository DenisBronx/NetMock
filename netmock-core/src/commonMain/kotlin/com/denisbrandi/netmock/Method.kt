package com.denisbrandi.netmock

/**
 * Method: the HTTP method used for a [NetMockRequest]
 */
sealed interface Method {
    val name: String

    data object Get : Method {
        override val name = "GET"
    }

    data object Head : Method {
        override val name = "HEAD"
    }

    data object Post : Method {
        override val name = "POST"
    }

    data object Put : Method {
        override val name = "PUT"
    }

    data object Delete : Method {
        override val name = "DELETE"
    }

    data object Patch : Method {
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

    companion object {
        fun from(method: String): Method {
            return when (method) {
                "GET" -> Get
                "HEAD" -> Head
                "POST" -> Post
                "PUT" -> Put
                "DELETE" -> Delete
                "PATCH" -> Patch
                else -> Custom(method)
            }
        }
    }
}
