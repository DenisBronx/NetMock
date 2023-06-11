package com.denisbrandi.netmock.server

import okhttp3.Interceptor
import okhttp3.Response

internal object NetMockChainRequestInterceptor {
    fun intercept(chain: Interceptor.Chain, baseUrl: String): Response {
        val interceptedRequestUrl = chain.request().url
        val interceptedBaseUrl = "${interceptedRequestUrl.scheme}://${interceptedRequestUrl.host}/"
        val headers = chain.request().headers.newBuilder()
            .add(INTERCEPTED_REQUEST_URL_HEADER, interceptedRequestUrl.toString())
            .build()
        val redirectedRequest = chain.request().newBuilder()
            .headers(headers)
            .url(chain.request().url.toString().replace(interceptedBaseUrl, baseUrl))
            .build()
        return chain.proceed(redirectedRequest)
    }
}
