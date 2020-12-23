package me.ddivad.hawk.core.services

import me.ddivad.hawk.core.dataclasses.Configuration
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.extensions.toTimeString
import java.util.*

@Service
class BotStatsService(private val configuration: Configuration, private val discord: Discord) {
    private var startTime: Date = Date()

    val uptime: String
        get() = ((Date().time - startTime.time) / 1000).toTimeString()

    val ping: String
        get() = "${discord.api.gateway.averagePing}"
}