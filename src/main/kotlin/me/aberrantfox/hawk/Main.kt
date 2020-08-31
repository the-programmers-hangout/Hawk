package me.aberrantfox.hawk

import com.google.gson.Gson
import me.aberrantfox.hawk.configuration.BotConfiguration
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.jda.fullName
import java.awt.Color
import java.util.*
import kotlin.system.exitProcess

data class Properties(val author: String, val version: String, val discordKt: String, val repository: String)
private val propFile = Properties::class.java.getResource("/properties.json").readText()
val project: Properties = Gson().fromJson(propFile, Properties::class.java)
val startTime = Date()

fun main(args: Array<String>) {
    val token = args.firstOrNull()

    if(token == null || token == "UNSET") {
        println("Please specify bot_Token ")
        exitProcess(-1)
    }

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
                with(project) {
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
                            "Version: $version\n" +
                            "DiscordKt: $discordKt\n" +
                            "Kotlin: $kotlinVersion" +
                            "```")

                    addField("Uptime", "$days day(s), " +
                            "$hours hour(s), " +
                            "$minutes minute(s) " +
                            "and $seconds second(s)")

                    addInlineField("Source", repository)
                }
            }

        }
    }
}
