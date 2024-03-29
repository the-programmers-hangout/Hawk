package me.ddivad.hawk.services

import dev.kord.core.behavior.getChannelOfOrNull
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.GuildMessageChannel
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.ReactionRole
import me.ddivad.hawk.embeds.createReactionRoleMenu
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.createMenu

@Service
class StartupService(
    private val configuration: Configuration,
    private val discord: Discord,
    private val loggingService: LoggingService
) {
    suspend fun refreshReactionRoleInteractions() {
        configuration.guildConfigurations.forEach { (guildId, guildConfig) ->
            val guild = discord.kord.getGuild(guildId) ?: return@forEach
            if (guildConfig.reactionRoles.isEmpty()) return@forEach
            loggingService.guildLog(
                guild,
                "Refreshing ${guildConfig.reactionRoles.size} reaction roles for ${guild.name}"
            )
            val reactionRolesToDelete = guildConfig.reactionRoles.filterNot { it.refresh(guild) }.toMutableList()
            reactionRolesToDelete.forEach { guildConfig.reactionRoles.remove(it) }
            configuration.save()
        }
    }

    private suspend fun ReactionRole.refresh(guild: Guild): Boolean {
        loggingService.guildLog(guild, "Attempting to refresh reaction role $id - $messageId - ${roles.joinToString()}")
        val channel = guild.getChannelOfOrNull<GuildMessageChannel>(channel)
        val message = messageId?.let { channel?.getMessageOrNull(it) }
        val reactionRole = this
        if (channel != null && message != null) {
            message.delete()
            val newMessage = channel.createMenu { createReactionRoleMenu(discord, guild, reactionRole) }
            newMessage.pin()
            messageId = newMessage.id
            loggingService.guildLog(guild, "Reaction role $id refreshed. New message ID: ${newMessage.id}")
            return true
        } else {
            loggingService.guildLog(
                guild,
                "Channel or message for reaction role $id no longer exist, flagging for deletion"
            )
            return false
        }
    }
}