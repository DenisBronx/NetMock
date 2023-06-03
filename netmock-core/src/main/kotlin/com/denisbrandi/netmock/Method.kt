package com.denisbrandi.netmock

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

    data class Custom(override val name: String) : Method
}