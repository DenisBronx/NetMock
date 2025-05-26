package com.denisbrandi.netmock.engine.mappers

import com.denisbrandi.netmock.engine.*
import com.denisbrandi.netmock.interceptors.InterceptedRequest
import io.ktor.client.request.*
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlin.test.*

class KtorRequestMapperTest {

    private val sut = KtorRequestMapper

    @Test
    fun `EXPECT mapped request without body`() {
        val requestData = HttpRequestBuilder().apply {
            url(URL)
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            method = HttpMethod.Post
        }.build()

        val result = sut.mapRequest(requestData)

        assertEquals(EXPECTED_POST_NO_BODY_REQUEST, result)
    }

    @Test
    fun `EXPECT mapped POST json request`() {
        val requestData = HttpRequestBuilder().apply {
            url(URL)
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            method = HttpMethod.Post
            setBody(TextContent(REQUEST_BODY, ContentType.Application.Json))
        }.build()

        val result = sut.mapRequest(requestData)

        assertEquals(EXPECTED_POST_JSON_REQUEST, result)
    }

    @Test
    fun `EXPECT mapped POST form request`() {
        val requestData = HttpRequestBuilder().apply {
            url(URL)
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }
            method = HttpMethod.Post
            setBody(FORM_DATA)
        }.build()

        val result = sut.mapRequest(requestData)

        assertEquals(EXPECTED_POST_FORM_REQUEST, result)
    }

    private companion object {
        const val URL = "http://google.com/somePath?1=2&3=4"
        val EXPECTED_POST_NO_BODY_REQUEST = InterceptedRequest(
            requestUrl = URL,
            method = "POST",
            headers = mapOf(HttpHeaders.ContentType to ContentType.Application.Json.toString()),
            body = ""
        )
        val EXPECTED_POST_JSON_REQUEST = EXPECTED_POST_NO_BODY_REQUEST.copy(
            body = REQUEST_BODY
        )

        val FORM_DATA = FormDataContent(
            parameters {
                append("form_key_1", "form_value_1")
                appendAll("form_key_2", listOf("form_value_2", "form value 3"))
            }
        )
        val EXPECTED_POST_FORM_REQUEST = EXPECTED_POST_NO_BODY_REQUEST.copy(
            headers = mapOf(HttpHeaders.ContentType to ContentType.Application.FormUrlEncoded.toString()),
            body = FORM_REQUEST_BODY
        )
    }
}
