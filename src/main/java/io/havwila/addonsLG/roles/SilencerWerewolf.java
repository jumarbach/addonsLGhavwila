package io.havwila.addonsLG.roles;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.Category;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWereWolf;
import io.havwila.addonsLG.guess.IGuesser;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SilencerWerewolf extends RoleWereWolf implements IPower, IGuesser, IAffectedPlayers {

    private List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private boolean power = false;
    private boolean failedGuess = false;

    public SilencerWerewolf(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription("werewolf.role.silencer_werewolf.description")
                .setCommand("werewolf.role.silencer_werewolf.desc_command")
                .setPower("werewolf.description.werewolf")
                .build();
    }

    @Override
    public void recoverPower() {

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

    @Override
    public void setPower(boolean b) {
        power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }

    @Override
    public boolean canGuess(IPlayerWW targetWW) {
        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            getPlayerWW().sendMessageWithKey("werewolf.check.state_player");
            return false;
        }

        if (targetWW.equals(this.getPlayerWW())) {
            getPlayerWW().sendMessageWithKey("werewolf.check.not_yourself");
            return false;
        }

        if (!power || !isAbilityEnabled() ) {
            getPlayerWW().sendMessageWithKey("werewolf.check.power");
            return false;
        }

        long nVillagers = game.getPlayersWW().stream()
                .filter(playerWW->playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW->playerWW.getRole().getCamp() == Camp.VILLAGER)
                .count();

        long nWW = game.getPlayersWW().stream()
                .filter(playerWW->playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW->playerWW.getRole().getCamp() == Camp.WEREWOLF)
                .count();
        if (nWW >= nVillagers) {
            getPlayerWW().sendMessageWithKey("werewolf.role.silencer_werewolf.more_wolves");
            return false;
        }
        return true;
    }

    @Override
    public void resolveGuess(String key, IPlayerWW targetWW) {
        power = false;
        if (targetWW.getRole().getKey().equals(key)) {
            affectedPlayers.add(targetWW);
            targetWW.getRole().disableAbilities();
            getPlayerWW().sendMessageWithKey("werewolf.role.silencer_werewolf.guess_success", Formatter.format("&player&", targetWW.getName()));
            targetWW.sendMessageWithKey("werewolf.role.silencer_werewolf.disable_target");
        } else {
            getPlayerWW().sendMessageWithKey("werewolf.role.silencer_werewolf.guess_fail");
            getPlayerWW().removePlayerMaxHealth(2);
        }
    }

    @Override
    public Set<Category> getAvailableCategories() {
        Set<Category> category = new HashSet<>();
        category.add(Category.VILLAGER);
        return category;
    }

    @EventHandler
    public void onNight(NightEvent event) {
        if (!failedGuess && isAbilityEnabled()) {
            power = true;
            getPlayerWW().sendMessageWithKey("werewolf.role.silencer_werewolf.power");
        }
    }
}
