package me.ddivad.hawk

import dev.kord.common.annotation.KordPreview
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.Permissions
import me.ddivad.hawk.services.BotStatsService
import me.ddivad.hawk.services.StartupService
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.extensions.addInlineField
import me.jakejmattson.discordkt.extensions.pfpUrl
import java.awt.Color

@KordPreview
@PrivilegedIntent
suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null
    val prefix = System.getenv("DEFAULT_PREFIX") ?: "<none>"

    require(token != null) { "Expected the bot token as an environment variable" }

    bot(token) {
        val configuration = data("data/config.json") { Configuration() }

        prefix {
            guild?.let { configuration[it.id]?.prefix } ?: prefix
        }

        configure {
            allowMentionPrefix = true
            commandReaction = null
            theme = Color.MAGENTA
            entitySupplyStrategy = EntitySupplyStrategy.cacheWithRestFallback
            permissions = Permissions
            intents = Intents(
                Intent.GuildMembers
            )
        }

        mentionEmbed {
            val botStats = it.discord.getInjectionObjects(BotStatsService::class)
            val channel = it.channel
            val self = channel.kord.getSelf()

            color = it.discord.configuration.theme

            thumbnail {
                url = self.pfpUrl
            }

            field {
                name = self.tag
                value = "A bot to add and maintain a symbol as a prefix or suffix in staff names."
            }

            addInlineField("Prefix", it.prefix())
            addInlineField("Contributors", "ddivad#0001")

            val kotlinVersion = KotlinVersion.CURRENT
            val versions = it.discord.versions

            field {
                name = "Build Info"
                value = "```" +
                        "Version:   2.0.0\n" +
                        "DiscordKt: ${versions.library}\n" +
                        "Kotlin:    $kotlinVersion" +
                        "```"
            }

            field {
                name = "Uptime"
                value = botStats.uptime
            }

            field {
                name = "Ping"
                value = botStats.ping
            }
        }
        onStart {
            val startupService = this.getInjectionObjects(
                StartupService::class
            )
            try {
                startupService.refreshReactionRoleInteractions()
            } catch (ex: Exception) {
                println(ex.message)
            }
        }
    }
}