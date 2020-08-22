package me.aberrantfox.hawk.commands

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.ChoiceArg
import me.aberrantfox.kjdautils.internal.arguments.MemberArg
import me.aberrantfox.kjdautils.internal.arguments.RoleArg
import me.aberrantfox.kjdautils.internal.arguments.AnyArg
import me.aberrantfox.kjdautils.internal.arguments.EveryArg
import me.aberrantfox.kjdautils.internal.services.PersistenceService

@CommandSet("Admin")
fun createAdministrationCommands(botConfiguration: BotConfiguration, persistenceService: PersistenceService) = commands {
    command("toggle") {
        description = "Toggles the bot functionality"
        execute {
            botConfiguration.enabled = !botConfiguration.enabled
            persistenceService.save(botConfiguration)
            it.respond("Bot has been turned **${if(botConfiguration.enabled) "On" else "Off"}**")
        }
    }

    command("enable") {
        description = "Enable the bot"
        execute {
            botConfiguration.enabled = true
            persistenceService.save(botConfiguration)
            it.respond("Enabled the bot. Listening.")
        }
    }

    command("disable") {
        description = "Disable the bot"
        execute {
            botConfiguration.enabled = false
            persistenceService.save(botConfiguration)
            it.respond("Disabled the bot. Events will be ignored as well as commands other than toggle/enable/disable.")
        }
    }

    command("setMode") {
        description = "Set the mode to prefix or suffix"
        execute(ChoiceArg("mode", "prefix", "suffix")) {
            botConfiguration.mode = it.args.first
            persistenceService.save(botConfiguration)
            it.respond("Set the bots nickname mode to **${it.args.first}**.")
        }
    }

    command("setSymbol") {
        description = "Set the token to appear in nicknames."
        execute(AnyArg("Symbol")) {
            val symbol = it.args.first
            botConfiguration.nickSymbol = "$symbol "
            botConfiguration.stripString = symbol
            persistenceService.save(botConfiguration)
            it.respond("Set the bots symbol to **${it.args.first}**")
        }
    }

    command("setRole") {
        description = "Set the role that will have symbols enforced."
        execute(RoleArg) {
            botConfiguration.staffRole = it.args.first.name
            persistenceService.save(botConfiguration)
            it.respond("Set the bots role to **${it.args.first.name}**")
        }
    }

    command("setOwner") {
        description = "Set the owner & admin of the bot."
        execute(MemberArg) {
            botConfiguration.owner = it.args.first.id
            persistenceService.save(botConfiguration)
            it.respond("Set the bots owner to **${it.args.first.user.asTag}**")
        }
    }

    command("setPrefix") {
        description = "Set the bot's invocation prefix"
        execute(AnyArg("Prefix")) {
            botConfiguration.botPrefix = it.args.first
            persistenceService.save(botConfiguration)
            it.respond("Set the bots owner prefix to **${it.args.first}**")
        }
    }
    
    command("toggleParty") {
        description = "Toggles party mode"
        execute {
            botConfiguration.partyMode = !botConfiguration.partyMode
            persistenceService.save(botConfiguration)
            it.respond("${if(botConfiguration.partyMode) "Let's get this party started!" else "We're done. That's all folks!"}")
        }
    }
    
    command("getPartySuffix") {
        description = "Display current party mode suffix"
        execute {
            it.respond("Suffix: ${botConfiguration.partySuffix}")
        }
    }
    
    command("setPartySuffix") {
        description = "Set new party mode suffix"
        execute (EveryArg("Suffix")){
            val symbol = it.args.first.replace("\uD83D\uDD28", "")
            botConfiguration.partySuffix = "$symbol "
            botConfiguration.partyStrip = symbol
            persistenceService.save(botConfiguration)
            it.respond("Set the party suffix to **${symbol}**")
        }
    }
}