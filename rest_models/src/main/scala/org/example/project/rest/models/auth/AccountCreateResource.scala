package org.example.project.rest.models.auth

import org.example.project.rest.models.JsonParsers

case class AccountCreateResource(email: String, password: String, firstName: Option[String], lastName: Option[String], issuerClaim: String)
object AccountCreateResource {
  implicit val pkl = JsonParsers.accountCreateResourcePickler
}
