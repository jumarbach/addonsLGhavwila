package io.havwila.addonsLG.roles;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import org.bukkit.event.EventHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class Medium extends RoleVillage{

    public Medium(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this).setDescription(game.translate("werewolf.role.medium.description")).build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @EventHandler
    public void onFirstDeathEvent(FirstDeathEvent event){

        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        if (!isAbilityEnabled()) return;

        IPlayerWW p = event.getPlayerWW();
        if(Bukkit.getPlayer(getPlayerUUID()) != null){
            TextComponent medium_msg = new TextComponent(game.translate("werewolf.role.medium.death_message", p.getName()));
            Bukkit.getPlayer(getPlayerUUID()).spigot().sendMessage(medium_msg);
        }
    }
}
