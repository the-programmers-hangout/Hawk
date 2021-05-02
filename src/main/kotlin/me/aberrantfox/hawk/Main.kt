package me.aberrantfox.hawk

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.jda.fullName
import java.awt.Color
import java.util.*

val startTime = Date()

fun main(args: Array<String>) {
    val token = System.getenv("BOT_TOKEN") ?: null
    val prefix = System.getenv("DEFAULT_PREFIX") ?: "<none>"
    require(token != null) { "Expected the bot token as an environment variable" }

    bot(token) {
        configure {
            val configuration = it.getInjectionObjects(BotConfiguration::class)
            prefix { configuration.botPrefix }
            allowMentionPrefix = true

            colors {
                infoColor = Color.CYAN
                failureColor = Color.RED
                successColor = Color.GREEN
            }

            mentionEmbed {
                val self = it.guild!!.jda.selfUser
                val kotlinVersion = KotlinVersion.CURRENT
                val milliseconds = Date().time - startTime.time
                val seconds = (milliseconds / 1000) % 60
                val minutes = (milliseconds / (1000 * 60)) % 60
                val hours = (milliseconds / (1000 * 60 * 60)) % 24
                val days = (milliseconds / (1000 * 60 * 60 * 24))

                color = infoColor
                thumbnail = self.effectiveAvatarUrl
                addField(self.fullName(), "A bot to add and maintain a symbol as a prefix or suffix in staff names.")
                addInlineField("Prefix", configuration.botPrefix)

                addField("Config Info", "```" +
                        "Enabled: ${configuration.enabled}\n" +
                        "Mode: ${configuration.mode}\n" +
                        "Role: ${configuration.staffRole}\n" +
                        "Symbol: ${configuration.nickSymbol}\n" +
                        "PartySuffix: ${configuration.partySuffix}" +
                        "```")

                addField("Build Info", "```" +
                        "Version: 1.5.0\n" +
                        "DiscordKt: 0.19.1\n" +
                        "Kotlin: $kotlinVersion" +
                        "```")

                addField("Uptime", "$days day(s), " +
                        "$hours hour(s), " +
                        "$minutes minute(s) " +
                        "and $seconds second(s)")

                addInlineField("Source", "https://github.com/the-programmers-hangout/Hawk")
            }

        }
    }
}
