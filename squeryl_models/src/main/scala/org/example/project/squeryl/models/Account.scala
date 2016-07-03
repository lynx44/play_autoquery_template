
package org.example.project.squeryl.models

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

case class Account(
    @Column("id") id: scala.Int, 
    @Column("first_name") firstName: scala.Option[scala.Predef.String], 
    @Column("last_name") lastName: scala.Option[scala.Predef.String], 
    @Column("email") email: scala.Option[scala.Predef.String], 
    @Column("avatarurl") avatarURL: scala.Option[scala.Predef.String], 
    @Column("password_hash") passwordHash: scala.Option[scala.Predef.String], 
    @Column("password_salt") passwordSalt: scala.Option[scala.Predef.String])
    extends KeyedEntity[scala.Int]
    with org.example.project.schema.Account {
    
}


