package io.havwila.addonsLG.guess;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.Category;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.havwila.addonsLG.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GuessInventory implements InventoryProvider {

    private Category category = Category.VILLAGER;
    private Set<Category> categories;
    private final IPlayerWW targetWW;

    public GuessInventory(IPlayerWW targetWW, Set<Category> categories) {
        this.targetWW = targetWW;
        this.categories = categories;
        if (!categories.contains(Category.VILLAGER)) {
            if (categories.contains(Category.WEREWOLF)) {
                category = Category.WEREWOLF;
            } else if (categories.contains(Category.NEUTRAL)) {
                category = Category.NEUTRAL;
            } else {
                category = Category.ADDONS;
            }
        }
    }

    public static SmartInventory getInventory(IPlayerWW targetWW, Set<Category> categories) {
        return SmartInventory.builder()
                .id("guess")
                .manager(JavaPlugin.getPlugin(Main.class).getAPI().getInvManager())
                .provider(new GuessInventory(targetWW, categories))
                .size(6, 9)
                .title(JavaPlugin.getPlugin(Main.class).getAPI().getWereWolfAPI().translate("havwila.guess.title",
                        Formatter.format("&player&", targetWW.getName())))
                .closeable(true)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getAPI().getWereWolfAPI();
        Pagination pagination = contents.pagination();

        List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right")));

        if (categories.contains(Category.ADDONS)) {
            contents.set(5, 7, ClickableItem.of(
                    (new ItemBuilder(Category.ADDONS == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate("werewolf.categories.addons")).setAmount(1).build()),
                    e -> this.category = Category.ADDONS));
        }
        if (categories.contains(Category.NEUTRAL)) {
            contents.set(5, 5, ClickableItem.of(
                    (new ItemBuilder(Category.NEUTRAL == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate(Camp.NEUTRAL.getKey())).setAmount(1).build()),
                    e -> this.category = Category.NEUTRAL));
        }
        if (categories.contains(Category.WEREWOLF)) {
            contents.set(5, 1, ClickableItem.of(
                    (new ItemBuilder(Category.WEREWOLF == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate(Camp.WEREWOLF.getKey())).setAmount(1).build()),
                    e -> this.category = Category.WEREWOLF));
        }
        if (categories.contains(Category.VILLAGER)) {
            contents.set(5, 3, ClickableItem.of(
                    (new ItemBuilder(Category.VILLAGER == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate(Camp.VILLAGER.getKey())).setAmount(1).build()),
                    e -> this.category = Category.VILLAGER));
        }


        lore.add(game.translate("werewolf.menu.shift"));

        List<ClickableItem> items = new ArrayList<>();


        for (RoleRegister roleRegister : main.getAPI().getRegisterManager().getRolesRegister()) {

            if (roleRegister.getCategories().contains(this.category)) {

                String key = roleRegister.getKey();
                AtomicBoolean unRemovable = new AtomicBoolean(false);
                List<String> lore2 = new ArrayList<>(lore);
                roleRegister.getLoreKey().stream().map(game::translate).map(s -> Arrays.stream(s.split("\\n")).collect(Collectors.toList())).forEach(lore2::addAll);
                roleRegister.getRequireRole().ifPresent(roleKey -> lore2.add(game.translate("werewolf.menu.roles.need", game.translate(roleKey))));
                main.getAPI().getRegisterManager().getRolesRegister().stream()
                        .filter(roleRegister1 -> roleRegister1.getRequireRole().isPresent())
                        .filter(roleRegister1 -> game.getConfig().getRoleCount(roleRegister1.getKey()) > 0)
                        .filter(roleRegister1 -> roleRegister1.getRequireRole().get().equals(key))
                        .map(RoleRegister::getKey)
                        .findFirst().ifPresent(s -> {
                    lore2.add(game.translate("werewolf.menu.roles.dependant_load", game.translate(s)));
                    unRemovable.set(true);
                });

                items.add(ClickableItem.of((new ItemBuilder(roleRegister.getItem().isPresent() ?
                        roleRegister.getItem().get() : UniversalMaterial.RED_TERRACOTTA.getStack())
                        .setAmount(1)
                        .setLore(lore2)
                        .setDisplayName(game.translate(roleRegister.getKey()))
                        .build()), e -> {

                    UUID uuid = player.getUniqueId();
                    IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

                    if (playerWW == null) {
                        player.closeInventory();
                        return;
                    }

                    if (!(playerWW.getRole() instanceof IGuesser)) {
                        player.closeInventory();
                        return;
                    }

                    IGuesser role = (IGuesser) playerWW.getRole();

                    role.resolveGuess(roleRegister.getKey(), targetWW);
                    player.closeInventory();
                }));
            }
        }
        if (items.size() > 45) {
            pagination.setItems(items.toArray(new ClickableItem[0]));
            pagination.setItemsPerPage(36);
            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
            int page = pagination.getPage() + 1;
            contents.set(4, 0, null);
            contents.set(4, 1, null);
            contents.set(4, 3, null);
            contents.set(4, 5, null);
            contents.set(4, 7, null);
            contents.set(4, 8, null);
            contents.set(4, 2, ClickableItem.of(new ItemBuilder(Material.ARROW).
                            setDisplayName(game.translate("werewolf.menu.roles.previous", Formatter.format("&current&", page), Formatter.format("&previous&", pagination.isFirst() ? page : page - 1))).build(),
                    e -> getInventory(targetWW, categories).open(player, pagination.previous().getPage())));
            contents.set(4, 6, ClickableItem.of(new ItemBuilder(Material.ARROW)
                            .setDisplayName(game.translate("werewolf.menu.roles.next", Formatter.format("&current&", page), Formatter.format("&next&", pagination.isLast() ? page : page + 1))).build(),
                    e -> getInventory(targetWW, categories).open(player, pagination.next().getPage())));
            contents.set(4, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.SIGN.getType())
                    .setDisplayName(game.translate("werewolf.menu.roles.current", Formatter.format("&current&", page), Formatter.format("&sum&", items.size() / 36 + 1))).build()));
        } else {
            int i = 0;
            for (ClickableItem clickableItem : items) {
                contents.set(i / 9, i % 9, clickableItem);
                i++;
            }
            for (int k = i; k < 45; k++) {
                contents.set(k / 9, k % 9, null);
            }
        }

    }
}
