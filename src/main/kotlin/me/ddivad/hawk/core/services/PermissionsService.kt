package me.ddivad.hawk.core.services

import com.gitlab.kordlib.core.any
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.User
import me.ddivad.hawk.core.dataclasses.Configuration
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.dsl.Command

enum class PermissionLevel {
    Everyone,
    Staff,
    Administrator,
    GuildOwner,
    BotOwner
}

val DEFAULT_REQUIRED_PERMISSION = PermissionLevel.Everyone
val commandPermissions: MutableMap<Command, PermissionLevel> = mutableMapOf()

@Service
class PermissionsService(private val configuration: Configuration) {
    suspend fun hasClearance(guild: Guild?, user: User, requiredPermissionLevel: PermissionLevel): Boolean {
        val permissionLevel = guild?.getMember(user.id)?.let { it.getPermissionLevel() }
        return if (permissionLevel == null) {
            requiredPermissionLevel == PermissionLevel.Everyone || user.id.value == configuration.ownerId
        } else {
            permissionLevel >= requiredPermissionLevel
        }
    }

    suspend fun hasPermission(member: Member, level: PermissionLevel) = member.getPermissionLevel() >= level

    suspend fun Member.getPermissionLevel() =
            when {
                isBotOwner() -> PermissionLevel.BotOwner
                isGuildOwner() -> PermissionLevel.GuildOwner
                isAdministrator() -> PermissionLevel.Administrator
                isStaff() -> PermissionLevel.Staff
                else -> PermissionLevel.Everyone
            }

    fun Member.isBotOwner() = id.value == configuration.ownerId
    private suspend fun Member.isGuildOwner() = isOwner()
    private suspend fun Member.isAdministrator() = roles.any { it.id.longValue == configuration[guild.id.longValue]?.adminRoleId }
    private suspend fun Member.isStaff() = roles.any { it.id.longValue == configuration[guild.id.longValue]?.staffRoleId }

    suspend fun Member.isHigherThan(other: Member): Boolean {
        if(this.id == other.id) return false
        if(isOwner()) return true
        return this.getPermissionLevel().ordinal > other.getPermissionLevel().ordinal
    }
}

var Command.requiredPermissionLevel: PermissionLevel
    get() = commandPermissions[this] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        commandPermissions[this] = value
    }
