package org.example.project.shared.js.api

import com.standardedge.http.URL

trait APIUrlBuilder {
  protected def baseUrl
  def rootRelativePath(relativePath: String): URL = {
    URL(s"$baseUrl/$relativePath")
  }
}
