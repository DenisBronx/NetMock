package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.*
import com.denisbrandi.netmock.okhttp.RequestMatcher.isMatchingTheRequest
import com.denisbrandi.netmock.okhttp.ResponseMapper.mapResponse
import okhttp3.mockwebserver.*
import java.util.logging.Logger

internal class MockDispatcher : Dispatcher() {

    val requestResponseList = mutableListOf<NetMockRequestResponse>()
    val interceptedRequests = mutableListOf<NetMockRequest>()

    var defaultResponse: NetMockResponse? = null

    override fun dispatch(request: RecordedRequest): MockResponse {
        return matchRequest(request) ?: getDefaultResponse(request)
    }

    private fun getDefaultResponse(request: RecordedRequest): MockResponse {
        return defaultResponse?.let { mapResponse(it) } ?: returnDefaultErrorResponseAndLogError(request)
    }

    private fun returnDefaultErrorResponseAndLogError(request: RecordedRequest): MockResponse {
        val errorMessage = "Request not mocked:\n${request}"
        Logger.getLogger("NetMock").apply {
            severe(errorMessage)
            info("The following requests and responses were expected:\n${requestResponseList}")
            info("The following requests have been successfully mocked:\n${interceptedRequests}")
        }

        return mapResponse(NetMockResponse(code = 400, body = errorMessage))
    }

    private fun matchRequest(recordedRequest: RecordedRequest): MockResponse? {
        return requestResponseList.filter { requestResponse ->
            isMatchingTheRequest(recordedRequest, requestResponse.request)
        }.firstNotNullOfOrNull { requestResponse ->
            interceptedRequests.add(requestResponse.request)
            requestResponseList.remove(requestResponse)
            mapResponse(requestResponse.response)
        }
    }
}