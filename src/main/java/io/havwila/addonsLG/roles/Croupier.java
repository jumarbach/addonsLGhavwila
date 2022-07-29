package io.havwila.addonsLG.roles;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

    @Role(key = "havwila.role.croupier.display",
            category = Category.VILLAGER,
            attributes = {RoleAttribute.INFORMATION, RoleAttribute.VILLAGER},
            configurations = {@Configuration(config = @ConfigurationBasic(key = "havwila.role.croupier.croupier_every_other_day"))})
public class Croupier  extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private boolean power;
    private int dayNumber = -8;

    public Croupier(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this).setDescription(game.translate("havwila.role.croupier.description")).build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (game.getConfig().isConfigActive("havwila.role.croupier.croupier_every_other_day") && event.getNumber() == dayNumber + 1) {
            return;
        }
        dayNumber = event.getNumber();

        setPower(true);

        getPlayerWW().sendMessageWithKey("havwila.role.croupier.perform",
                Formatter.format("&time&", Utils.conversion(game.getConfig().getTimerValue(TimerBase.POWER_DURATION))));
    }

    public static ClickableItem configOtherDay(WereWolfAPI game) {
        IConfiguration config = game.getConfig();

        return ClickableItem.of(new ItemBuilder(Material.PAPER)
                .setLore(game.translate(
                        config.isConfigActive("havwila.role.croupier.croupier_every_other_day") ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                .setDisplayName(game.translate("havwila.role.croupier.croupier_every_other_day"))
                .build(), e -> {
            config.setConfig("havwila.role.croupier.croupier_every_other_day", !config.isConfigActive("havwila.role.croupier.croupier_every_other_day"));

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(game.translate(config.isConfigActive("havwila.role.croupier.croupier_every_other_day") ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                    .build());
        });
    }
}
