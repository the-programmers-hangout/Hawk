package me.ddivad.hawk.dataclasses

import me.jakejmattson.discordkt.dsl.Permission
import me.jakejmattson.discordkt.dsl.PermissionSet
import me.jakejmattson.discordkt.dsl.permission

@Suppress("unused")
object Permissions : PermissionSet {
    val BOT_OWNER = permission("Bot Owner") { discord.getInjectionObjects<Configuration>().ownerId?.let { users(it) } }
    val GUILD_OWNER = permission("Guild Owner") { guild?.ownerId?.let { users(it) } }
    val ADMINISTRATOR = permission("Administrator") {
        discord.getInjectionObjects<Configuration>()[guild!!.id]?.adminRoleId?.let {
            roles(it)
        }
    }
    val STAFF = permission("Staff") {
        discord.getInjectionObjects<Configuration>()[guild!!.id]?.staffRoleId?.let {
            roles(it)
        }
    }
    val NONE = permission("None") { guild?.everyoneRole?.let { roles(it.id) } }

    override val hierarchy: List<Permission> = listOf(NONE, STAFF, ADMINISTRATOR, GUILD_OWNER, BOT_OWNER)
    override val commandDefault: Permission = STAFF
}