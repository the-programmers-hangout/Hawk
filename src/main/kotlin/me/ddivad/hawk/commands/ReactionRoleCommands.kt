package me.ddivad.hawk.commands

import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.Permissions
import me.ddivad.hawk.dataclasses.ReactionRole
import me.ddivad.hawk.embeds.createReactionRoleMenu
import me.ddivad.hawk.services.LoggingService
import me.ddivad.hawk.services.buildGuildLogMessage
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.edit
import me.jakejmattson.discordkt.extensions.toSnowflake
import mu.KotlinLogging

val logger = KotlinLogging.logger { }

@Suppress("unused")
fun reactionRoleCommands(configuration: Configuration, loggingService: LoggingService) = commands("ReactionRole") {
    slash("createReactionRole", "Create a reaction role embed", Permissions.ADMINISTRATOR) {
        execute(
            EveryArg("Roles", "Role IDs to be added. If using multiple roles, separate IDs with a space"),
            EveryArg("EmbedDescription", "Text to be added to reaction role embed")
        ) {
            val (roleIds, descriptionText) = args
            try {
                val roles = roleIds.split(" ").map { guild.getRole(it.toSnowflake()) }
                val guildConfig = configuration[guild.id] ?: return@execute
                val reactionRole = ReactionRole(
                    guildConfig.reactionRoles.size + 1,
                    descriptionText,
                    roles.map { it.id }.toMutableList(),
                    null,
                    channel.id
                )
                reactionRole.messageId = respondMenu {
                    createReactionRoleMenu(discord, guild, reactionRole)
                }.id
                configuration.edit {
                    guildConfig.reactionRoles.add(reactionRole)
                }
                respond("Reaction role created")
            } catch (e: Exception) {
                respond("Error parsing roles. Make sure IDs $roleIds are valid roles and separated by ` ` if adding multiple roles.")
                logger.error(e) { buildGuildLogMessage(guild, "Failed to parse roles") }
            }
        }
    }
}