package com.nickname.app.helper

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import org.scalatra.ApiFormats

trait JsonFormat extends ApiFormats {

  implicit val jsonFormats = Serialization.formats(NoTypeHints)

  before() {
    contentType = formats("json")
  }

}
