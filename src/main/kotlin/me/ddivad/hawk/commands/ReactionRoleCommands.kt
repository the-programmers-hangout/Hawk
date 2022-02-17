package me.ddivad.hawk.commands

import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.Permissions
import me.ddivad.hawk.dataclasses.ReactionRole
import me.ddivad.hawk.embeds.createReactionRoleMenu
import me.ddivad.hawk.services.LoggingService
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.arguments.MultipleArg
import me.jakejmattson.discordkt.arguments.RoleArg
import me.jakejmattson.discordkt.commands.commands

@Suppress("unused")
fun reactionRoleCommands(configuration: Configuration, loggingService: LoggingService) = commands("ReactionRole") {
    slash("createReactionRole") {
        description = "Create a reaction role embed"
        requiredPermission = Permissions.ADMINISTRATOR
        execute(MultipleArg(RoleArg, "Roles"), EveryArg("EmbedDescription")) {
            val (roles, descriptionText) = args
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
            }?.id

            guildConfig.reactionRoles.add(reactionRole)
            configuration.save()
        }
    }
}