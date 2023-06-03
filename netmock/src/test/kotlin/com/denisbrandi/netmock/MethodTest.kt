package com.denisbrandi.netmock

import kotlin.test.*


class MethodTest {

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
}