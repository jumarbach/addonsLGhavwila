package io.havwila.addonsLG.roles;

import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Inquisitor extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();

    public Inquisitor(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this).setDescription(game.translate("havwila.role.inquisitor.description")).build();
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
        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        setPower(true);

        this.getPlayerWW().sendMessageWithKey(
                "havwila.role.inquisitor.smite_message",
                Formatter.format("&time&", Utils.conversion(game.getConfig().getTimerValue(TimerBase.POWER_DURATION.getKey()))));
    }
}
