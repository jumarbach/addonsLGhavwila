package io.havwila.addonsLG.roles;

import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.*;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWithLimitedSelectionDuration;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Croupier  extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private boolean power;
    private int dayNumber = -8;

    public Croupier(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
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
                Formatter.format("&time&", Utils.conversion(game.getConfig().getTimerValue(TimerBase.POWER_DURATION.getKey()))));
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
