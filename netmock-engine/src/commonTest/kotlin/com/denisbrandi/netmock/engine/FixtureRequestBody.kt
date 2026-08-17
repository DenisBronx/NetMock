package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.resources.readFromResources

val REQUEST_BODY = readFromResources("requests/request_body.json")

const val FORM_REQUEST_BODY = "form_key_1=form_value_1&form_key_2=form_value_2&form_key_2=form+value+3"
