package org.iris

import cats.data.Validated

def validateItem[F](value: String, userORRoom: F, name: String): Validated[String, F] = 
  Validated.cond(
    (value.length() >= 2 && value.length() <= 10),
    userORRoom,
    s"$name must be between 2 and 10 characters"
  )

case class User(name: String)
object User{
  def apply(name: String): Validated[String, User] = {
    validateItem(name, new User(name), "User name")
  }
}

case class Room(room: String)
object Room{
  def apply(room: String): Validated[String, Room] = 
    validateItem(room, new Room(room), "Room")
}

case class ChatState(userRooms: Map[User, Room], roomMembers: Map[Room, Set[User]])


case class AuthUser(id: Long, name: String)

case class TokenPayLoad(user: String, level: String)