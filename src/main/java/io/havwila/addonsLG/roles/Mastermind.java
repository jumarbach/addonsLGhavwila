package io.havwila.addonsLG.roles;

import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
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
