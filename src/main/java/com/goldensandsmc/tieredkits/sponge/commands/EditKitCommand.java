package com.goldensandsmc.tieredkits.sponge.commands;

import com.goldensandsmc.tieredkits.sponge.TieredKits;
import com.goldensandsmc.tieredkits.sponge.kit.BonusItem;
import com.goldensandsmc.tieredkits.sponge.kit.Kit;
import com.goldensandsmc.tieredkits.sponge.kit.KitTier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.InventoryRow;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class EditKitCommand implements CommandExecutor
{
    private static final AtomicBoolean registered = new AtomicBoolean(false);

    public EditKitCommand(Object plugin)
    {
        if (registered.compareAndSet(false, true))
        {
            CommandSpec spec = CommandSpec.builder().description(Text.of("Create and manage kits"))
                                          .permission("tieredkits.command.ekit".toLowerCase()).executor(this).arguments(
                            GenericArguments.firstParsing(GenericArguments.literal(Text.of("load"), "load"),
                                                          GenericArguments.literal(Text.of("save"), "save"),
                                                          GenericArguments
                                                                  .seq(GenericArguments.literal(Text.of("create"),
                                                                                                "create"),
                                                                       GenericArguments.string(Text.of("name")),
                                                                       GenericArguments.optional(GenericArguments
                                                                                                         .longNum(
                                                                                                                 Text.of("seconds")))),
                                                          GenericArguments
                                                                  .seq(GenericArguments.literal(Text.of("remove"),
                                                                                                "remove"),
                                                                       GenericArguments.choices(Text.of("name"),
                                                                                                () -> TieredKits
                                                                                                        .getInstance()
                                                                                                        .getKits()
                                                                                                        .keySet(),
                                                                                                s -> s)),
                                                          GenericArguments.seq(
                                                                  GenericArguments.literal(Text.of("edit"), "edit"),
                                                                  GenericArguments.choices(Text.of("name"), () ->
                                                                                                   TieredKits.getInstance().getKits().keySet(),
                                                                                           (s) ->
                                                                                                   s),
                                                                  GenericArguments.integer(Text.of("uses")),
                                                                  GenericArguments.optional(
                                                                          GenericArguments.bool(Text.of("cascade"))))))
                                          .build();
            Sponge.getCommandManager().register(plugin, spec, "editkit", "ekit");
        }

    }

    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, CommandContext args) throws CommandException
    {
        if (args.hasAny("load"))
        {
            try
            {
                TieredKits.getInstance().loadConfig();
            }
            catch (ObjectMappingException | IOException e)
            {
                e.printStackTrace();
                throw new CommandException(Text.of("An error has occurred while loading kits"), e);
            }
        }
        else if (args.hasAny("save"))
        {
            try
            {
                TieredKits.getInstance().saveConfig();
            }
            catch (ObjectMappingException | IOException e)
            {
                e.printStackTrace();
                throw new CommandException(Text.of("An error has occurred while saving kits"), e);
            }
        }
        else
        {
            String name = ((String) args.getOne("name").get()).toLowerCase();
            if (args.hasAny("create"))
            {
                if (TieredKits.getInstance().getKits().containsKey(name))
                {
                    src.sendMessage(Text.of(TextColors.RED, "A kit with that name already exists"));
                }
                else
                {
                    long seconds = 0L;
                    if (args.hasAny("seconds"))
                    {
                        Optional<Long> opt = args.getOne("seconds");
                        if (opt.isPresent())
                        {
                            seconds = opt.get();
                        }
                    }

                    TieredKits.getInstance().getKits().put(name, new Kit(seconds, null));
                    src.sendMessages(Text.of(TextColors.GOLD, "Created an empty kit"),
                                     Text.of(TextColors.GOLD, "Use ", TextColors.RED,
                                             "/ekit edit ", name, TextColors.GOLD,
                                             " to edit this kit"));
                }

                try
                {
                    TieredKits.getInstance().saveConfig();
                }
                catch (ObjectMappingException | IOException var22)
                {
                    var22.printStackTrace();
                    throw new CommandException(Text.of("An error has occurred while saving kits"), var22);
                }
            }
            else if (args.hasAny("remove"))
            {
                if (TieredKits.getInstance().getKits().remove(name) == null)
                {
                    src.sendMessage(Text.of(TextColors.RED, "There is no kit with that name"));
                }
                else
                {
                    src.sendMessage(Text.of(TextColors.GOLD, "Removed kit"));
                }

                try
                {
                    TieredKits.getInstance().saveConfig();
                }
                catch (ObjectMappingException | IOException var23)
                {
                    var23.printStackTrace();
                    throw new CommandException(Text.of("An error has occurred while saving kits"), var23);
                }
            }
            else if (args.hasAny("edit"))
            {
                if (!(src instanceof Player))
                {
                    src.sendMessage(Text.of(TextColors.RED, "This command is player-only"));
                }
                else
                {
                    Player player = (Player) src;
                    boolean cascade = args.hasAny("cascade") && (Boolean) args.getOne("cascade").get();
                    int uses = (Integer) args.getOne("uses").get();
                    if (!TieredKits.getInstance().getKits().containsKey(name))
                    {
                        src.sendMessage(Text.of(TextColors.RED, "There is no kit with that name"));
                    }
                    else
                    {
                        Kit kit = TieredKits.getInstance().getKits().get(name);
                        List<ItemStack> items = new ArrayList<>();
                        List<BonusItem> bonuses = new ArrayList<>();
                        MainPlayerInventory inventory = player.getInventory().query(new Class[]{
                                MainPlayerInventory.class});
                        GridInventory grid = inventory.getGrid();
                        Hotbar hotbar = inventory.getHotbar();

                        for (int y = 0; y < grid.getRows(); ++y)
                        {
                            Optional<InventoryRow> opt_row = grid.getRow(y);
                            if (opt_row.isPresent())
                            {
                                for (Inventory slot : opt_row.get().slots())
                                {
                                    Optional<ItemStack> opt_item = slot.peek();
                                    opt_item.ifPresent(items::add);
                                }
                            }
                        }

                        for (Inventory slot : hotbar.slots())
                        {
                            Optional<ItemStack> opt_item = slot.peek();
                            if (opt_item.isPresent())
                            {
                                List<ItemStack> bonus_items = new ArrayList<>(1);
                                bonus_items.add(opt_item.get());
                                bonuses.add(new BonusItem(0.15D, bonus_items));
                            }
                        }

                        KitTier tier = new KitTier(uses, cascade, items, bonuses);
                        kit.getTiers().add(tier);
                        src.sendMessage(
                                Text.of(TextColors.GOLD, "Created ", TextColors.RED, cascade ? "" : "non-",
                                        "cascading", TextColors.GOLD, " tier available after ",
                                        TextColors.RED, uses, TextColors.GOLD, " uses with ",
                                        TextColors.RED, items.size(), TextColors.GOLD, " items and ",
                                        TextColors.RED, bonuses.size(), TextColors.GOLD,
                                        " bonus items (15% chance each)"));
                    }

                    try
                    {
                        TieredKits.getInstance().saveConfig();
                    }
                    catch (ObjectMappingException | IOException var24)
                    {
                        var24.printStackTrace();
                        throw new CommandException(Text.of("An error has occurred while saving kits"), var24);
                    }
                }
            }
        }
        return CommandResult.success();
    }
}
