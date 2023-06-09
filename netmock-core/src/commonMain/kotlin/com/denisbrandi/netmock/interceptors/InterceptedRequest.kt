package com.denisbrandi.netmock.interceptors

data class InterceptedRequest(
    val requestUrl: String?,
    val method: String?,
    val headers: Map<String, String>?,
    val body: String?
)
