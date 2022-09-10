package me.ddivad.hawk

import dev.kord.common.annotation.KordPreview
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.Permissions
import me.ddivad.hawk.services.StartupService
import me.jakejmattson.discordkt.dsl.bot
import mu.KotlinLogging
import java.awt.Color

val logger = KotlinLogging.logger {  }

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
            defaultPermissions = Permissions.STAFF
            theme = Color.MAGENTA
            intents = Intents(
                Intent.Guilds,
                Intent.GuildMembers
            )
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
            logger.info { "Bot Ready" }
        }
    }
}