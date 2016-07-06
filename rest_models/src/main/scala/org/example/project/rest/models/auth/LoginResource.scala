package org.example.project.rest.models.auth

import org.example.project.rest.models.JsonParsers

case class LoginResource(email: String, password: String, issuerClaim: String)
object LoginResource {
  implicit val pkl = JsonParsers.loginResourcePickler
}
