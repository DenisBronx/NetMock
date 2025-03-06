package com.denisbrandi.netmock

import kotlin.js.JsName
import kotlin.test.*

class MethodTest {
    @JsName("validMethodName")
    @Test
    fun `EXPECT valid name`() {
        assertEquals("GET", Method.Get.name)
        assertEquals("HEAD", Method.Head.name)
        assertEquals("POST", Method.Post.name)
        assertEquals("PUT", Method.Put.name)
        assertEquals("DELETE", Method.Delete.name)
        assertEquals("PATCH", Method.Patch.name)
        assertEquals("Custom Value", Method.Custom("Custom Value").name)
    }

    @JsName("validMethodFromName")
    @Test
    fun `EXPECT valid method from name`() {
        assertEquals(Method.Get, Method.from("GET"))
        assertEquals(Method.Head, Method.from("HEAD"))
        assertEquals(Method.Post, Method.from("POST"))
        assertEquals(Method.Put, Method.from("PUT"))
        assertEquals(Method.Delete, Method.from("DELETE"))
        assertEquals(Method.Patch, Method.from("PATCH"))
        assertEquals(Method.Custom("Custom Value"), Method.from("Custom Value"))
    }
}
