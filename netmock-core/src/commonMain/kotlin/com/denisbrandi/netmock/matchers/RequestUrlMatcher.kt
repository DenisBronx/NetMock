package com.denisbrandi.netmock.matchers

import com.denisbrandi.netmock.NetMockRequest

object RequestUrlMatcher {
    fun isMatchingUrl(interceptedUrl: String?, netMockRequest: NetMockRequest): Boolean {
        var appendedParams = getParamsFromPath(netMockRequest.requestUrl)
        netMockRequest.params.forEach { (key, value) ->
            appendedParams += if (appendedParams.isEmpty()) {
                "?$key=$value"
            } else {
                "&$key=$value"
            }
        }
        val actualPath = "${getPathWithoutParams(netMockRequest.requestUrl)}$appendedParams"
        return interceptedUrl.orEmpty() == actualPath
    }

    private fun getPathWithoutParams(mockedRequestPath: String): String {
        return if (mockedRequestPath.contains("?")) {
            mockedRequestPath.split("?")[0]
        } else {
            mockedRequestPath
        }
    }

    private fun getParamsFromPath(mockedRequestPath: String): String {
        return if (mockedRequestPath.contains("?")) {
            val pathToRemove = getPathWithoutParams(mockedRequestPath)
            mockedRequestPath.replace(pathToRemove, "")
        } else {
            ""
        }
    }
}
