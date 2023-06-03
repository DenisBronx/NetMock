package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.interceptors.RequestInterceptor
import com.denisbrandi.netmock.okhttp.MockWebServerResponseMapper.mapResponse
import okhttp3.mockwebserver.*
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
            "Request not mocked:\n${request}\nWith headers:\n${request.headers}With body:\n${recordedRequestBody}"
        Logger.getLogger("NetMock").apply {
            severe(errorMessage)
            info("The following requests and responses were expected:\n${allowedMocks}")
            info("The following requests have been successfully mocked:\n${interceptedRequests}")
        }

        return mapResponse(NetMockResponse(code = 400, body = errorMessage))
    }
}