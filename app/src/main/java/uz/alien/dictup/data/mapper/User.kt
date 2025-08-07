package uz.alien.dictup.data.mapper

import uz.alien.dictup.data.local.room.user.UserEntity
import uz.alien.dictup.domain.model.User

fun UserEntity.toUser(): User {
    return User(
        id = this.id,
        name = this.name
    )
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name
    )
}
