package io.havwila.addonsLG.guess;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import org.jetbrains.annotations.NotNull;

public class TestGuesser extends RoleVillage implements IGuesser {

    public TestGuesser(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return "nobody cares about a test subject";
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public boolean canGuess(IPlayerWW targetWW) {
        return true;
    }

    @Override
    public void resolveGuess(String key, IPlayerWW targetWW) {
        if (targetWW.getRole().getKey().equals(key)) {
            getPlayerWW().sendMessageWithKey("werewolf.guess.success",
                    Formatter.format("&player&", targetWW.getName()),
                    Formatter.format("&role&", game.translate(key)));
        } else {
            getPlayerWW().sendMessageWithKey("werewolf.guess.failure",
                    Formatter.format("&player&", targetWW.getName()),
                    Formatter.format("&guess&", game.translate(key)),
                    Formatter.format("&real&", game.translate(targetWW.getRole().getKey())));
        }
    }
}
