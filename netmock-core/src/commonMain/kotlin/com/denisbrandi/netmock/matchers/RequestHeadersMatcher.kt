package com.denisbrandi.netmock.matchers

internal object RequestHeadersMatcher {
    fun isMatchingTheHeaders(interceptedHeaders: Map<String, String>?, mandatoryHeaders: Map<String, String>): Boolean {
        for (header in mandatoryHeaders) {
            if (interceptedHeaders?.get(header.key) != header.value) {
                return false
            }
        }
        return true
    }
}
