# Commands

## Key 
| Symbol      | Meaning                        |
|-------------|--------------------------------|
| [Argument]  | Argument is not required.      |
| /Category   | This is a subcommand group.    |

## /Configuration
| Commands   | Arguments              | Description                          |
|------------|------------------------|--------------------------------------|
| setChannel | ChannelChoice, Channel | Set the logging or alert channel     |
| setup      |                        | Setup a configuration for this guild |
| view       |                        | View the bot configuration           |

## /Party
| Commands           | Arguments                          | Description                                     |
|--------------------|------------------------------------|-------------------------------------------------|
| getPartySymbol     |                                    | View the current party mode symbol              |
| partyChannelFilter | PartyChannelOption, [PartyChannel] | Add, remove or view channels used in party mode |
| setPartySymbol     | Suffix                             | Set new party mode symbol                       |
| toggleParty        |                                    | Toggles party mode                              |

## Nickname
| Commands  | Arguments                  | Description                           |
|-----------|----------------------------|---------------------------------------|
| blocklist | BloclklistOption, [symbol] | Add a symbol to the symbol blocklist. |

## ReactionRole
| Commands           | Arguments               | Description                  |
|--------------------|-------------------------|------------------------------|
| createReactionRole | Roles, EmbedDescription | Create a reaction role embed |

## Utility
| Commands | Arguments | Description          |
|----------|-----------|----------------------|
| Help     | [Command] | Display a help menu. |
| info     |           | Bot info for Hawk    |

