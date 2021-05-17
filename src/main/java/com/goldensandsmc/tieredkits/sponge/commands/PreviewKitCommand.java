package com.goldensandsmc.tieredkits.sponge.commands;

import com.goldensandsmc.tieredkits.sponge.TieredKits;
import com.goldensandsmc.tieredkits.sponge.kit.BonusItem;
import com.goldensandsmc.tieredkits.sponge.kit.Kit;
import com.goldensandsmc.tieredkits.sponge.kit.KitTier;
import com.goldensandsmc.tieredkits.sponge.kit.KitUsage;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent.Close;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class PreviewKitCommand implements CommandExecutor
{
    private static final AtomicBoolean registered = new AtomicBoolean(false);

    public PreviewKitCommand(Object plugin)
    {
        if (registered.compareAndSet(false, true))
        {
            CommandSpec spec = CommandSpec.builder().description(Text.of("Preview the contents of a kit"))
                                          .permission("tieredkits.command.preview".toLowerCase()).executor(this)
                                          .arguments(
                                                  new CommandElement[]{GenericArguments.choices(Text.of("name"), () ->
                                                          TieredKits.getInstance().getKits().keySet(), (s) ->
                                                                                                        s),
                                                                       GenericArguments.optional(GenericArguments
                                                                                                         .integer(
                                                                                                                 Text.of("uses")))})
                                          .build();
            Sponge.getCommandManager().register(plugin, spec, "previewkit", "viewkit");
        }

    }

    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException
    {
        if (!(src instanceof Player))
        {
            throw new CommandException(Text.of("This command is player-only"));
        }
        else
        {
            Player player = (Player) src;
            String name = ((String) args.getOne("name").get()).toLowerCase();
            HashMap<String, KitUsage> usages =
                    TieredKits.getInstance().getKitUsage().computeIfAbsent(player.getUniqueId(), (k) ->
                            new HashMap<>());
            Kit kit = null;

            for (Entry<String, Kit> stringKitEntry : Kit.getAvailableKits(src).entrySet())
            {
                if (stringKitEntry.getKey().equalsIgnoreCase(name))
                {
                    kit = stringKitEntry.getValue();
                    break;
                }
            }

            if (kit == null)
            {
                throw new CommandException(Text.of("There is no kit with that name"));
            }
            else if (!Kit.hasKitPermission(src, name))
            {
                throw new CommandException(Text.of("You do not have permission to use that kit"));
            }
            else
            {
                KitUsage usage = usages.computeIfAbsent(name, (n) ->
                        new KitUsage());
                if (args.hasAny("uses"))
                {
                    usage = new KitUsage();
                    usage.setTotalUses((Integer) args.getOne("uses").get());
                }

                List<KitTier> applicableTiers = KitTier.getApplicableTiers(kit, usage);
                int numRows = 0;
                int bonusRows = 0;
                for (KitTier tier : applicableTiers)
                {
                    numRows += (int) (Math.ceil(tier.getItems().size() / 9.0));
                    for (BonusItem bonus : tier.getBonuses())
                    {
                        bonusRows += bonus.getItems().size();
                    }
                    numRows += (int) (Math.ceil(bonusRows / 9.0));
                }

                if (applicableTiers.size() > 1)
                {
                    numRows += applicableTiers.size() - 1;
                }

                if (numRows == 0)
                {
                    throw new CommandException(Text.of("That kit is empty"));
                }
                else
                {
                    Inventory inventory = Inventory.builder().property("title", new InventoryTitle(
                            Text.of(TextColors.GOLD, name, " (", usage.getTotalUses(), " uses)")))
                                                   .property("inventorydimension", new InventoryDimension(9, numRows))
                                                   .listener(InteractInventoryEvent.class, (event) ->
                                                   {
                                                       if (!(event instanceof Close))
                                                       {
                                                           event.setCancelled(true);
                                                       }

                                                   }).build(TieredKits.getInstance());
                    ItemStack barrier = ItemStack.builder().itemType(ItemTypes.BARRIER).build();

                    for (int row = 0; row < applicableTiers.size(); ++row)
                    {
                        KitTier tier = applicableTiers.get(row);
                        for (int column = 0; column < tier.getItems().size(); ++column)
                        {
                            ItemStack item = tier.getItems().get(column);
                            if (column == 9)
                            {
                                ++row;
                                column = 0;
                            }

                            int index = row * 9 + column;
                            item = item.copy();
                            item.offer(Keys.ITEM_LORE, ImmutableList
                                    .of(Text.of("Available after " + tier.getAvailableAfter() + " uses"),
                                        Text.of("This item is always given"),
                                        Text.of("This tier does" + (tier.doesCascade() ? "" : " not") + " cascade")));
                            inventory.query(new InventoryProperty[]{new SlotIndex(index)}).set(item);
                        }
                        ++row;

                        for (BonusItem bonus : tier.getBonuses())
                        {
                            for (int column = 0; column < bonus.getItems().size(); ++column)
                            {
                                ItemStack item = bonus.getItems().get(column);
                                if (column == 9)
                                {
                                    ++row;
                                    column = 0;
                                }

                                int index = row * 9 + column;
                                ItemStack clone = item.copy();
                                clone.offer(Keys.ITEM_LORE, ImmutableList
                                        .of(Text.of("Available after " + tier.getAvailableAfter() + " uses"),
                                            Text.of(bonus.getProbability() * 100.0D + "% chance"),
                                            Text.of("This tier does" + (tier.doesCascade() ? "" : " not")
                                                    + " cascade")));
                                inventory.query(new InventoryProperty[]{new SlotIndex(index)}).set(clone);
                            }
                        }

                        ++row;
                        for (int column = 0; column < 9; ++column)
                        {
                            int index = row * 9 + column;
                            inventory.query(new InventoryProperty[]{new SlotIndex(index)}).set(barrier.copy());
                        }
                    }

                    if (player.isViewingInventory())
                    {
                        player.closeInventory();
                    }

                    System.out.println("inventory = " + inventory);
                    System.out.println(player.openInventory(inventory));
                    return CommandResult.success();
                }
            }
        }
    }
}
