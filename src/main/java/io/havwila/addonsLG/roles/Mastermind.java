package io.havwila.addonsLG.roles;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import io.havwila.addonsLG.guess.IGuesser;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Mastermind extends RoleNeutral implements IGuesser, IAffectedPlayers, IPower {

    private List<IPlayerWW> guessedPlayers;
    private List<IPlayerWW> failedPlayers;
    private boolean power = true;

    public Mastermind(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this).setDescription("werewolf.role.mastermind.description").build();
    }

    @Override
    public void recoverPower() {

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

        if (guessedPlayers.contains(targetWW) || failedPlayers.contains(targetWW)) {
            getPlayerWW().sendMessageWithKey("werewolf.role.mastermind.repeat_target");
            return false;
        }
        return true;
    }

    @Override
    public void resolveGuess(String key, IPlayerWW targetWW) {


        if (targetWW.getRole().getKey().equals(key)) {
            guessedPlayers.add(targetWW);
            getPlayerWW().sendMessageWithKey("werewolf.role.mastermind.guess_success", Formatter.format("&player&", targetWW.getName()));
            getPlayerWW().addPlayerMaxHealth(2);
        } else {
            failedPlayers.add(targetWW);
            getPlayerWW().sendMessageWithKey("werewolf.role.mastermind.guess_fail");
            getPlayerWW().removePlayerMaxHealth(2);
        }
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
