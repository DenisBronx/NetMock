package com.denisbrandi.netmock.matchers

object RequestUrlMatcher {
    fun isMatchingUrl(interceptedUrl: String?, netMockRequestUrl: String): Boolean {
        return interceptedUrl.orEmpty() == netMockRequestUrl
    }
}
