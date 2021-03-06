package io.havwila.addonsLG.roles;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Witness extends RoleVillage implements IAffectedPlayers, IPower {

    public Witness(WereWolfAPI main, IPlayerWW playerWW, String key) {
        super(main,playerWW, key);
    }

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    @Override
    public @NotNull String getDescription() {
        //return game.translate("werewolf.role.witness.description");
        return new DescriptionBuilder(game, this).setDescription(game.translate("werewolf.role.witness.description"))
                .addExtraLines(game.translate("werewolf.role.witness.culprit_name",
                        affectedPlayer.isEmpty() ? (power ?
                                game.translate("werewolf.role.witness.culprit_unknown", Utils.conversion(
                                        game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST.getKey())))
                                :
                                game.translate("werewolf.role.witness.culprit_dead"))
                                :
                                affectedPlayer.get(0).getName()))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.LIGHT;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayer.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayer.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @Override
    public void setPower(boolean b) {
        this.power = b;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @EventHandler
    public void onWerewolfListEvent(WereWolfListEvent event){

        List<IPlayerWW> wolves = new ArrayList<>();
        for (IPlayerWW p : game.getPlayersWW()) {

            if(p.isState(StatePlayer.ALIVE) && p.getRole().isWereWolf()) {
                wolves.add(p);
            }
        }

        if (wolves.isEmpty()){
            return;
        }
        IPlayerWW culprit = wolves.get((int) Math.floor(new Random(System.currentTimeMillis()).nextFloat()*wolves.size()));
        addAffectedPlayer(culprit);

        Player player = Bukkit.getPlayer(getPlayerUUID());
        player.sendMessage(game.translate("werewolf.role.witness.reveal_culprit",culprit.getName()));

    }

    @EventHandler
    public void onFinalDeathEvent(FinalDeathEvent event){
        IPlayerWW playerWW = event.getPlayerWW();
        if(!getAffectedPlayers().contains(playerWW)) return;
        if(getPlayerWW().isState(StatePlayer.DEATH))
        if(!power) return;

        removeAffectedPlayer(playerWW);
        this.power = false;

        getPlayerWW().removePlayerMaxHealth(8);
        getPlayerWW().sendMessageWithKey("werewolf.role.witness.culprit_death");
    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event){
        IPlayerWW player = event.getPlayerWW();
        IPlayerWW thief = event.getThiefWW();

        if(!getAffectedPlayers().contains(player)) return;

        removeAffectedPlayer(player);
        addAffectedPlayer(thief);

        if(getPlayerWW().isState(StatePlayer.DEATH)) return;

        getPlayerWW().sendMessageWithKey("werewolf.role.witness.change", thief.getName());
    }

    @EventHandler
    public void onStealEvent(StealEvent event){

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        if(power) {
            getPlayerWW().sendMessageWithKey("werewolf.role.witness.reveal_culprit", getAffectedPlayers().get(0).getName());
        }
        else {
            getPlayerWW().removePlayerMaxHealth(8);
        }
    }
}
