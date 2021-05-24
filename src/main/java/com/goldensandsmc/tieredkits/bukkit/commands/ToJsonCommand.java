package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.goldensandsmc.tieredkits.bukkit.adapters.BaseTypeAdapter;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToJsonCommand extends BaseCommand<Player>
{
    public ToJsonCommand(TieredKits plugin)
    {
        super(plugin);
    }

    public String getCommandName()
    {
        return "tojson";
    }

    public String[] getCommandAliases()
    {
        return new String[]{"itemtojson", "jsonfromitem", "fromitem"};
    }

    public CommandType getCommandType()
    {
        return CommandType.PLAYER;
    }

    public String[] getCommandDescription()
    {
        return new String[]{"Serialize a held item to JSON and print the result to the console"};
    }

    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName() + " [meta]"};
    }

    public String getCommandPermission()
    {
        return "sandcastle.foundation.command.tojson";
    }

    public String[] exec(Player sender, String command, ConcurrentArrayList<String> args) throws CommandException
    {
        Object target = sender.getInventory().getItemInMainHand();
        if (!args.isEmpty() && args.get(0).equalsIgnoreCase("meta"))
        {
            if (target == null)
            {
                throw new CommandException("You are not holding an item.");
            }

            target = ((ItemStack) target).getItemMeta();
        }

        String json = BaseTypeAdapter.gsonBuilderWithItemSerializers().create().toJson(target);
        this.getPlugin().getLogger().info(json);
        return new String[]{json};
    }
}
