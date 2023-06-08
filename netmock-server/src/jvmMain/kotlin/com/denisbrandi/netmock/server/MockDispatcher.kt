package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.interceptors.RequestInterceptor
import com.denisbrandi.netmock.server.MockWebServerResponseMapper.mapResponse
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.util.logging.Logger

internal class MockDispatcher(
    okHttpMockInterceptor: RequestInterceptor<RecordedRequest, MockResponse>
) : Dispatcher(), RequestInterceptor<RecordedRequest, MockResponse> by okHttpMockInterceptor {

    override fun dispatch(request: RecordedRequest): MockResponse {
        // Body can be read only once
        val recordedRequestBody = request.body.readUtf8()
        return intercept(request, recordedRequestBody) ?: returnDefaultErrorResponseAndLogError(
            request,
            recordedRequestBody
        )
    }

    private fun returnDefaultErrorResponseAndLogError(
        request: RecordedRequest,
        recordedRequestBody: String
    ): MockResponse {
        val errorMessage =
            "\n----\nRequest not mocked:\n${request}\nWith headers:\n${request.headers}With body:\n${recordedRequestBody}" +
                    "\n\nThe following requests and responses were expected:\n${allowedMocks}" +
                    "\n\nThe following requests have been successfully mocked:\n${interceptedRequests}" +
                    "\n----"
        logError(errorMessage)
        return mapResponse(NetMockResponse(code = 400, body = errorMessage))
    }

    private fun logError(errorMessage: String) {
        Logger.getLogger("NetMock").severe(errorMessage)
    }
}