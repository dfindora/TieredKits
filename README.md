# TieredKits
TieredKits plugin initially developed by ShortCircuit908.
## Commands
|Command|Parameters|Aliases|Description
|----|----|----|----
|/addkittier|\<name\> \<index\> \<uses\> \<cascade\>|/addtier, /atier|adds a tier to the target kit, named with the index, with the specified minimum required uses and cascade. If cascade is true, this tier will be given on every usage above and including the number of uses at which this is unlocked. Otherwise, it will only be given until the next tier is unlocked. Basic items should be placed in the bottom 9 slots of your inventory and the hotbar, while bonus items should be in the top 2 rows.
|/createkit|\<name\> [\<cooldown\>]|/ckit|creates an empty kit with the specified name with the cooldown in seconds if provided. If no cooldown is provided, it will default to 0 seconds.
|/fromjson|\<player\> \<json\>|/itemfromjson, /jsontoitem, /toitem|attempts to convert json into an item and gives it to the player.
|/kit|<name> [\<other\>]|/kits|uses the specified kit, if able. If a user is provided, it will use the kit for the specified user based on whether the command sender can use the kit.
|/previewkit|\<name\> [\<uses\>]| |previews the specified kit. If no uses argument is provided, it will show all tiers. Otherwise, it will show only the available tiers at the specified number of uses.
|/reloadkits| | |reloads all kits from file. If the usage.json config was not loaded at startup, it will attempt to reload that as well.
|/removekit|\<name\>|/deletekit|deletes the kit with the specified name.
|/removekittier|\<name\> \<index\>|/removetier, /rtier|deletes the tier from the specified kit with the specified index.
|/resetkit reset|\<user\> \<kit\>|/rkit|resets all usage stats for a specific user's kit.
|/resetkit usage|\<user\> \<kit\> \<value\>|/rkit|sets the specified user's kit to the specified number of uses.
|/resetkit lastused|\<user\> \<kit\> \<value\>|/rkit|sets the time the specified user last used the specified kit. If you want to just reset the cooldown, just set it to 0.
|/tojson|[\<meta\>]|/itemtojson, /jsonfromitem, /fromitem|converts the item in the player's main hand into json. If `meta` is passed, it will return only the metadata of the item.

## Permissions
|Command|Permission|Description
|----|----|----
|/addkittier|essentials.kit.tier.add|Grants permission to /addkittier.
|/createkit|essentials.kit.create|Grants permission to /createkit.
|/fromjson|sandcastle.foundation.command.fromjson|Grants permission to /fromjson.
|/kit|essentials.kits|Grants permission to /kit.
|/kit|essentials.kits.\<kitname\>|Grants access to a specific kit.
|/kit|essentials.kits.\*|Grants access to all kits.
|/kit|essentials.kits.exemptdelay|Allows user to bypass cooldowns on kits.
|/previewkit|essentials.kit.preview|Grants permission to /previewkit.
|/reloadkits|essentials.kit.reload|Grants permission to /reloadkits.
|/removekit|essentials.kit.remove|Grants permission to /removekit.
|/removekittier|essentials.kit.tier.remove|Grants permission to /removekittier.
|/resetkit|essentials.kit.reset|Grants permission to /resetkit.
|/tojson|sandcastle.foundation.command.tojson|Grants permission to /tojson.
