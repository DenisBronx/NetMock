package com.denisbrandi.netmock

import org.junit.Assert.assertEquals
import org.junit.Test

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