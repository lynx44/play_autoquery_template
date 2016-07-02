package org.example.project.schema

trait AccountProvider {
  def id: Int
  def account: Account
  def providerId: String
  def providerKey: String
}
