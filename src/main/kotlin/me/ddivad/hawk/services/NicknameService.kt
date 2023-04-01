package me.ddivad.hawk.services

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.TextChannel
import me.ddivad.hawk.dataclasses.Configuration
import me.jakejmattson.discordkt.annotations.Service

@Service
class NicknameService(private val configuration: Configuration, private val loggingService: LoggingService) {
    suspend fun setOrRemovePartyNickname(guild: Guild, member: Member, channel: TextChannel) {
        val partyConfiguration = configuration[guild.id]?.partyModeConfiguration ?: return
        if (member.isBot) {
            return
        }
        if (partyConfiguration.mode == "Furry" && (!furryNames.contains(member.displayName) || !furryNames.contains(member.nickname))) {
            val nickname = furryNames.random()
            changeMemberNickname(guild, member, nickname)
            return
        }

        if (partyConfiguration.symbol.isNullOrBlank()) {
            return
        }
        if (partyConfiguration.channelFilterEnabled && !partyConfiguration.channels.contains(channel.id)) {
            return
        }

        val hasPartyNicknameApplied =
            member.displayName.endsWith(partyConfiguration.symbol) || member.displayName.endsWith(partyConfiguration.symbolStrip)
        if (!hasPartyNicknameApplied && partyConfiguration.enabled) {
            val nickname = addNickSymbol(member.displayName, partyConfiguration.symbol, partyConfiguration.symbolStrip)
            changeMemberNickname(guild, member, nickname)
        } else if (hasPartyNicknameApplied && !partyConfiguration.enabled) {
            val nickname = removeNickSymbol(member.displayName, mutableListOf(partyConfiguration.symbolStrip))
            changeMemberNickname(guild, member, nickname)
        }
    }

    suspend fun checkNicknameForBlockedSymbols(guild: Guild, member: Member) {
        val guildConfiguration = configuration[guild.id] ?: return

        if (!guildConfiguration.disallowedNicknameSymbols.any { member.displayName.contains(it) }) {
            return
        }

        val nickname = removeNickSymbol(member.displayName, guildConfiguration.disallowedNicknameSymbols)
        loggingService.blocklistedSymbolRemoved(guild, member)
        changeMemberNickname(guild, member, nickname)
    }

    private fun addNickSymbol(nickname: String, symbol: String, stripString: String): String {
        val newName = nickname.replace(stripString, "").replace("\\s+|", "")
        val nickWithPrefix = "$newName $symbol"

        val sizedNick = if (nickWithPrefix.length > 32) {
            nickWithPrefix.substring(0, 31 - symbol.length) + symbol
        } else {
            nickWithPrefix
        }
        val discordNick = sizedNick.replace(" ", "").replace(stripString, "")

        return if (discordNick.isBlank()) {
            "Blanky blankerson"
        } else {
            sizedNick
        }
    }

    private fun removeNickSymbol(nickname: String, disallowedSymbols: MutableList<String>): String {
        var strippedName = nickname
        for (symbol in disallowedSymbols) {
            strippedName = strippedName.replace(symbol, "")
        }
        return if (strippedName.isBlank() || strippedName == "") {
            "Chunck Testa"
        } else {
            strippedName
        }
    }

    private suspend fun changeMemberNickname(guild: Guild, member: Member, nickname: String): Member {
        loggingService.nicknameApplied(guild, member, nickname)
        return member.edit { this.nickname = nickname }
    }

    val furryNames = listOf(
        "Shadowfang Thunderstrike",
        "Sunnywhisker Briskclaw",
        "Moonclaw Silvershadow",
        "Dusksnout Wittytail",
        "Frostyclaw Luckyeyes",
        "Copperpelt Softfur",
        "Silentwhisker Darkmug",
        "Rainwhisker Cleverheart",
        "Amberpaw Boldshadow",
        "Mistycoat Rapidfeet",
        "Sneakyclaw Darksnout",
        "Sparklingfur Sharpclaw",
        "Ravencrown Luckytail",
        "Thunderwhisker Softyelp",
        "Snowyclaw Swiftfeet",
        "Crystalpelt Fierceheart",
        "Flamepaw Brightclaw",
        "Leafcoat Jollytooth",
        "Stormyclaw Wildyelp",
        "Emberwhisker Humblepelt",
        "Smokyfur Richmug",
        "Goldenclaw Silenttail",
        "Mistyclaw Calmheart",
        "Midnightfur Snowytooth",
        "Honeywhisker Lazytail",
        "Sablepelt Darkclaw",
        "Braveclaw Swiftmug",
        "Fuzzywhisker Braveheart",
        "Dustyclaw Rustytail",
        "Thundercoat Calmfeet",
        "Snowpaw Coldyelp",
        "Fluffywhisker Fierceclaw",
        "Dawnfur Quickheart",
        "Nightclaw Wiseeyes",
        "Rapidclaw Sillytail",
        "Mistywhisker Richshadow",
        "Gentlecoat Swiftmug",
        "Brightclaw Windyheart",
        "Sneakypelt Strongclaw",
        "Twilightfur Wiseyelp",
        "Majesticpaw Darkfeather",
        "Mooncoat Fierceclaw",
        "Wildwhisker Cozytail",
        "Windyfur Softyelp",
        "Stormyclaw Humbleheart",
        "Tundrapelt Speedytooth",
        "Breezywhisker Rustymug",
        "Nightfur Swiftshadow",
        "Frostyclaw Darksnout",
        "Goldenwhisker Jollyclaw",
        "Sandycoat Boldyelp",
        "Lunarclaw Richpelt",
        "Sablewhisker Quicktail",
        "Brightcoat Braveheart",
        "Crimsonpaw Wittyheart",
        "Thunderpelt Fierceclaw",
        "Cloudyclaw Silentmug",
        "Snowyclaw Cozytail",
        "Sneakyclaw Calmheart",
        "Swiftcoat Windytooth",
        "Midnightwhisker Sillyclaw",
        "Smokyfur Humblepelt",
        "Silverclaw Darkfeather",
        "Mistywhisker Swifttail",
        "Duskycoat Richyelp",
        "Crystalpelt Speedyclaw",
        "Fluffywhisker Darkheart",
        "Ravencrown Softclaw",
        "Thunderpaw Snowytail",
        "Dawnclaw Rustyheart",
        "Mistyclaw Wildclaw",
        "Gentlewhisker Boldtooth",
        "Sneakypelt Swiftmug",
        "Brightclaw Fierceheart",
        "Sablepaw Softtail",
        "Majesticfur Rustyeyes",
        "Frostclaw Coolpaws",
        "Moonwhisker Twinkletoes",
        "Sablefur Wildheart",
        "Spirithawk Gloomyswoop",
        "Thunderfox Mightyfang",
        "Mistysnout Dreamyhowl",
        "Goldenpelt Richmane",
        "Fireflyer Quickwings",
        "Swiftbunny Furryfeet",
        "Wildwhisker Roamingeyes",
        "Mudpaw Grittyclaw",
        "Stonefang Hardhide",
        "Cloudchaser Fluffytail",
        "Leafylynx Sleekears",
        "Blazingtiger Fiercegrowl",
        "Junglejaguar Sneakypaws",
        "Silverswift Swiftclaw",
        "Thornystud Proudmane",
        "Smokysnout Greyhowl",
        "Riverscale Riverheart",
        "Stormysky Thunderpaws",
        "Nightclaw Darkfur",
        "Honeybunny Sweetnose",
        "Grimwhisker Darktooth",
        "Lightningleopard Electricclaw",
        "Shadowlynx Sharpshadow",
        "Moonbeam Brighteyes",
        "Goldenkitten Softpaws",
        "Stormystud Wildmane",
        "Blackbird Wittytoes",
        "Lionheart Braveclaw",
        "Flowerpelt Floralhide",
        "Frostymuzzle Coldnose",
        "Wildwhirlwind Furryheart",
        "Rockyclaw Rockyhide",
        "Cloudystud Mistyclaws",
        "Duskyfur Mysteriouspelt",
        "Ironpaws Steelclaw",
        "Winterfrost Frostyeyes",
        "Silentsnout Quietgrowl",
        "Majesticmoon Royalmane",
        "Rainyday Rainyeyes",
        "Thickfur Bigclaws",
        "Lazysnout Sleepyhowl",
        "Sunnybunny Warmheart",
        "Thunderclaw Thunderfur",
        "Autumnleaf Fallingheart",
        "Rockywhisker Hardnose",
        "Darkscale Darkheart",
        "Goldenwhisker Shinytail"
    )
}