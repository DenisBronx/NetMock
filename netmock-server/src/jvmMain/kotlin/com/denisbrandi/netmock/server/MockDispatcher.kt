package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.interceptors.RequestInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

internal class MockDispatcher(
    okHttpMockInterceptor: RequestInterceptor<RecordedRequest, MockResponse>
) : Dispatcher(), RequestInterceptor<RecordedRequest, MockResponse> by okHttpMockInterceptor {

    override fun dispatch(request: RecordedRequest): MockResponse {
        // Body can be read only once
        val recordedRequestBody = request.body.readUtf8()
        return intercept(request, request.headers, recordedRequestBody)
    }
}
