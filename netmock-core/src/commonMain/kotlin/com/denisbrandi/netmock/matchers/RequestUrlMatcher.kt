package com.denisbrandi.netmock.matchers

internal object RequestUrlMatcher {
    fun isMatchingUrl(interceptedUrl: String?, netMockRequestUrl: String): Boolean {
        return interceptedUrl.orEmpty() == netMockRequestUrl
    }
}
