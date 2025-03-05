package com.denisbrandi.netmock.matchers

import kotlin.js.JsName
import kotlin.test.*

class RequestHeadersMatcherTest {
    private val sut = RequestHeadersMatcher

    @JsName("true_empty")
    @Test
    fun `EXPECT true WHEN empty and empty`() {
        assertTrue(sut.isMatchingTheHeaders(emptyMap(), emptyMap()))
    }

    @JsName("true_sameHeaders")
    @Test
    fun `EXPECT true WHEN headers are identical`() {
        assertTrue(
            sut.isMatchingTheHeaders(
                mapOf("a" to "b", "c" to "d"),
                mapOf("a" to "b", "c" to "d")
            )
        )
    }

    @JsName("true_containsMandatoryHeaders")
    @Test
    fun `EXPECT true WHEN mandatory headers are matching`() {
        assertTrue(
            sut.isMatchingTheHeaders(
                mapOf("a" to "b", "c" to "d", "e" to "f"),
                mapOf("a" to "b", "c" to "d")
            )
        )
    }

    @JsName("false_doesNotContainMandatoryHeaders")
    @Test
    fun `EXPECT false WHEN mandatory headers are not matching`() {
        assertFalse(sut.isMatchingTheHeaders(mapOf("a" to "b"), mapOf("a" to "b", "c" to "d")))
    }
}
