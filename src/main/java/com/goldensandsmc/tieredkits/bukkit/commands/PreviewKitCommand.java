package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.KitPreviewListener;
import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.goldensandsmc.tieredkits.bukkit.Utils;
import com.goldensandsmc.tieredkits.bukkit.kit.Kit;
import com.goldensandsmc.tieredkits.bukkit.kit.KitTier;
import com.goldensandsmc.tieredkits.bukkit.kit.KitUsage;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.bukkit.command.exceptions.InvalidArgumentException;
import com.shortcircuit.utils.bukkit.command.exceptions.TooFewArgumentsException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;

public class PreviewKitCommand extends BaseCommand<Player>
{
    public PreviewKitCommand(TieredKits plugin)
    {
        super(plugin);
    }

    public String getCommandName()
    {
        return "previewkit";
    }

    public String[] getCommandAliases()
    {
        return null;
    }

    public CommandType getCommandType()
    {
        return CommandType.PLAYER;
    }

    public String[] getCommandDescription()
    {
        return new String[]{"Preview the contents of a kit"};
    }

    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName() + " <kit> [uses]"};
    }

    public String getCommandPermission()
    {
        return "essentials.kit.preview";
    }

    public String[] exec(Player sender, String command, ConcurrentArrayList<String> args) throws CommandException
    {
        if (args.isEmpty())
        {
            throw new TooFewArgumentsException(getCommandUsage()[0]);
        }
        else
        {
            String name = args.remove(0).toLowerCase();
            Kit kit = this.getPlugin().getKits().get(name);
            if (kit == null)
            {
                throw new InvalidArgumentException("There is no kit with that name.");
            }
            else
            {
                List<KitTier> tiers = new LinkedList<>();
                if (args.isEmpty())
                {
                    tiers.addAll(kit.getTiers());
                }
                else
                {
                    try
                    {
                        int uses = Integer.parseInt(args.get(0));
                        KitUsage usage = new KitUsage();
                        usage.setTotalUses(uses);
                        tiers.addAll(Utils.getApplicableTiers(kit, usage));
                    }
                    catch (NumberFormatException e)
                    {
                        throw new InvalidArgumentException("Invalid number: " + args.get(0));
                    }
                }

                KitPreviewListener.openKitPreview(sender, name, tiers);
                return new String[0];
            }
        }
    }
}
