package org.example.project.schema

trait Account {
  def id: Int
  def firstName: Option[String]
  def lastName: Option[String]
  def email: Option[String]
  def avatarURL: Option[String]
  def passwordHash: Option[String]
  def passwordSalt: Option[String]
}
