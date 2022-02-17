package me.ddivad.hawk.embeds

import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.Guild
import dev.kord.rest.Image
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import me.ddivad.hawk.dataclasses.ReactionRole
import me.ddivad.hawk.services.LoggingService
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.dsl.MenuBuilder

suspend fun MenuBuilder.createReactionRoleMenu(discord: Discord, guild: Guild, reactionRole: ReactionRole) {
    val loggingService = discord.getInjectionObjects(LoggingService::class)
    page {
        color = discord.configuration.theme
        title = "Select a role"
        description = reactionRole.description
        footer {
            text = "Click a button to assign a role. Clicking it again will remove the role"
            icon = guild.getIconUrl(Image.Format.PNG) ?: ""
        }
    }
    buttons {
        runBlocking {
            reactionRole.roles.forEach {
                val liveRole = guild.getRole(it)

                actionButton(liveRole.name, null) {
                    val member = guild.getMemberOrNull(this.user.id) ?: return@actionButton
                    if (!member.roles.toList().contains(liveRole)) {
                        member.addRole(liveRole.id, "Reaction button clicked")
                        respondEphemeral { content = "Assigned role ${liveRole.name}" }
                        loggingService.reactionRoleAdded(guild, member, liveRole)
                    } else {
                        member.removeRole(liveRole.id, "Reaction button clicked")
                        respondEphemeral { content = "Removed role ${liveRole.name}" }
                        loggingService.reactionRoleRemoved(guild, member, liveRole)
                    }
                }
            }
        }
    }
}