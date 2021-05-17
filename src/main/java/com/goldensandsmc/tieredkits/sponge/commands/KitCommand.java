package com.goldensandsmc.tieredkits.sponge.commands;

import com.goldensandsmc.tieredkits.sponge.TieredKits;
import com.goldensandsmc.tieredkits.sponge.kit.BonusItem;
import com.goldensandsmc.tieredkits.sponge.kit.Kit;
import com.goldensandsmc.tieredkits.sponge.kit.KitTier;
import com.goldensandsmc.tieredkits.sponge.kit.KitUsage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;

public class KitCommand implements CommandExecutor
{
    private static final AtomicBoolean registered = new AtomicBoolean(false);

    public KitCommand(Object plugin)
    {
        if (registered.compareAndSet(false, true))
        {
            CommandSpec spec = CommandSpec.builder().description(Text.of("Spawn a predefined kit"))
                                          .permission("tieredkits.command.kit".toLowerCase()).executor(this).arguments(
                            GenericArguments.optional(GenericArguments.choices(Text.of("name"), () ->
                                    TieredKits.getInstance().getKits().keySet(), (s) ->
                                                                                       s))).build();
            Sponge.getCommandManager().register(plugin, spec, "kit", "kits");
        }

    }

    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, CommandContext args) throws CommandException
    {
        if (!args.hasAny("name"))
        {
            Builder builder = Text.builder("Available kits: ").color(TextColors.GOLD);
            Set<Entry<String, Kit>> entries = Kit.getAvailableKits(src).entrySet();
            List<Text> texts = new ArrayList<>(entries.size());

            for(Entry<String, Kit> entry : entries)
            {
                HashMap<String, KitUsage> usage = src instanceof Player ? TieredKits.getInstance().getKitUsage()
                                                .computeIfAbsent(((Player) src).getUniqueId(), (k) ->
                                                        new HashMap<>()) : new HashMap<>();

                KitUsage kit_usage = usage.computeIfAbsent(entry.getKey(), (n) ->
                        new KitUsage());
                LiteralText tiny_text;
                if (entry.getValue().canUseNow(src, kit_usage))
                {
                    tiny_text = Text.builder(entry.getKey()).color(TextColors.RED)
                                    .onClick(TextActions.runCommand("/kit " + entry.getKey())).onHover(
                                    TextActions.showText(Text.of(TextColors.GOLD, "Click to use kit")))
                                    .build();
                }
                else
                {
                    tiny_text = Text.builder(entry.getKey()).color(TextColors.RED)
                                    .style(TextStyles.STRIKETHROUGH).onHover(TextActions.showText(
                                    Text.of(TextColors.RED, "You must wait to use this kit again")))
                                    .build();
                }
                texts.add(tiny_text);
            }

            Text joined = Text.joinWith(Text.of(TextColors.GOLD, ", "), texts);
            src.sendMessage(builder.append(new Text[]{joined}).build());
        }
        else
        {
            long now = System.currentTimeMillis();
            if (!(src instanceof Player))
            {
                throw new CommandException(Text.of("This command is player-only"));
            }
            else
            {
                String name = ((String) args.getOne("name").get()).toLowerCase();
                HashMap<String, KitUsage> usages = TieredKits.getInstance().getKitUsage()
                                                                       .computeIfAbsent(((Player) src).getUniqueId(),
                                                                                        (k) ->
                                                                                                new HashMap<>());
                Kit kit = null;
                KitUsage usage = null;

                for (Entry<String, Kit> stringKitEntry : Kit.getAvailableKits(src).entrySet())
                {
                    if (stringKitEntry.getKey().equalsIgnoreCase(name))
                    {
                        usage = usages.computeIfAbsent(stringKitEntry.getKey(), (n) ->
                                new KitUsage());
                        if (!stringKitEntry.getValue().canUseNow(src, usage))
                        {
                            throw new CommandException(Text.of("You must wait to use this kit again"));
                        }

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
                    List<KitTier> applicableTiers = KitTier.getApplicableTiers(kit, usage);
                    LinkedList<ItemStack> allItems = new LinkedList<>();

                    for (KitTier tier : applicableTiers)
                    {
                        for (ItemStack item : tier.getItems())
                        {
                            allItems.add(item.copy());
                        }

                        Random random = new Random();
                        for (BonusItem bonus : tier.getBonuses())
                        {
                            if (random.nextDouble() < bonus.getProbability())
                            {
                                for (ItemStack item : bonus.getItems())
                                {
                                    allItems.add(item.copy());
                                }
                            }
                        }
                    }

                    MainPlayerInventory inventory = ((Player) src).getInventory()
                                                                  .query(new Class[]{MainPlayerInventory.class});

                    for (ItemStack item : allItems)
                    {
                        InventoryTransactionResult result = inventory.offer(item);

                        for (ItemStackSnapshot rejected : result.getRejectedItems())
                        {
                            Item dropped_item = (Item) ((Player) src).getWorld().createEntity(EntityTypes.ITEM,
                                                                                              ((Player) src)
                                                                                                      .getLocation()
                                                                                                      .getPosition());
                            dropped_item.offer(Keys.REPRESENTED_ITEM, rejected);
                            ((Player) src).getWorld().spawnEntity(dropped_item);
                        }
                    }

                    int prev_tiers = applicableTiers.size();
                    usage.setTotalUses(usage.getTotalUses() + 1);
                    usage.setLastUsed(now);
                    int new_tiers = KitTier.getApplicableTiers(kit, usage).size();
                    if (new_tiers > prev_tiers)
                    {
                        src.sendMessage(
                                Text.of(TextColors.GOLD, "Congratulations! Your ", TextColors.RED, name,
                                        TextColors.GOLD, " kit has upgraded!"));
                    }
                }
            }
        }
        return CommandResult.success();
    }
}
