package io.havwila.addonsLG.guess;

import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;

public interface IGuesser {

    boolean canGuess(IPlayerWW targetWW);

    void resolveGuess(String key, IPlayerWW targetWW);
}
