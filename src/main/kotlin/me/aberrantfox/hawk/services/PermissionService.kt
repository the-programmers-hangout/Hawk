package me.aberrantfox.hawk.services

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.jakejmattson.discordkt.api.annotations.Service
import net.dv8tion.jda.api.entities.Member

enum class Permission {
    BotOwner,
    GuildOwner,
    Administrator,
    Everyone
}

val DEFAULT_REQUIRED_PERMISSION = Permission.Administrator

@Service
class PermissionsService(private val configuration: BotConfiguration) {
    fun hasClearance(member: Member, requiredPermissionLevel: Permission): Boolean {
        return member.getPermissionLevel().ordinal <= requiredPermissionLevel.ordinal
    }
    fun getPermissionLevel(member: Member) = member.getPermissionLevel().ordinal

    private fun Member.getPermissionLevel() =
            when {
                isBotOwner() -> Permission.BotOwner
                isGuildOwner() -> Permission.GuildOwner
                isAdministrator() -> Permission.Administrator
                else -> Permission.Everyone
            }

    private fun Member.isBotOwner() = user.id == configuration.owner
    private fun Member.isGuildOwner() = isOwner
    private fun Member.isAdministrator() : Boolean {

        val requiredRole = configuration.adminRole?.let {
            guild.getRolesByName(it, true).firstOrNull()
        } ?: return false

        return requiredRole in roles
    }
}