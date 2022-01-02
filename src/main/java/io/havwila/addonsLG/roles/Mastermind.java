package io.havwila.addonsLG.roles;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.Category;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import io.havwila.addonsLG.guess.IGuesser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Mastermind extends RoleNeutral implements IGuesser, IAffectedPlayers, IPower {

    private List<IPlayerWW> guessedPlayers = new ArrayList<>();
    private List<IPlayerWW> failedPlayers = new ArrayList<>();
    private boolean power = true;

    public Mastermind(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("havwila.role.mastermind.description")).build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @Override
    public boolean canGuess(IPlayerWW targetWW) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            getPlayerWW().sendMessageWithKey("werewolf.check.death");
            return false;
        }

        if (targetWW.equals(this.getPlayerWW())) {
            getPlayerWW().sendMessageWithKey("werewolf.check.not_yourself");
            return false;
        }

        if (guessedPlayers.contains(targetWW) || failedPlayers.contains(targetWW)) {
            getPlayerWW().sendMessageWithKey("havwila.role.mastermind.repeat_target");
            return false;
        }
        return true;
    }

    @Override
    public void resolveGuess(String key, IPlayerWW targetWW) {


        if (targetWW.getRole().getKey().equals(key)) {
            guessedPlayers.add(targetWW);
            getPlayerWW().sendMessageWithKey("havwila.role.mastermind.guess_success", Formatter.format("&player&", targetWW.getName()));
            getPlayerWW().addPlayerMaxHealth(2);
        } else {
            failedPlayers.add(targetWW);
            getPlayerWW().sendMessageWithKey("havwila.role.mastermind.guess_fail");
            getPlayerWW().removePlayerMaxHealth(2);
        }
    }

    @Override
    public Set<Category> getAvailableCategories() {
        return null;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        guessedPlayers.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        guessedPlayers.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        guessedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return guessedPlayers;
    }

    @Override
    public void setPower(boolean b) {
        power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
