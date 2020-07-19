package io.havwila.addonsLG;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.StealEvent;
import io.github.ph1lou.werewolfapi.events.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Witness extends RolesVillage implements AffectedPlayers, Power {

    public Witness(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    private final List<UUID> affectedPlayer = new ArrayList<>();
    private boolean power = true;


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.witness.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.witness.display";
    }

    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
    }

    @Override
    public void removeAffectedPlayer(UUID uuid) {
        this.affectedPlayer.remove(uuid);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<UUID> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @Override
    public void setPower(Boolean power) {
        this.power = power;
    }

    @Override
    public Boolean hasPower() {
        return this.power;
    }

    @EventHandler
    public void onWerewolfListEvent(WereWolfListEvent event){

        List<PlayerWW>  wolves = new ArrayList<>();
        for (PlayerWW p : game.getPlayersWW().values()) {

            if(p.isState(State.ALIVE) && p.getRole().isWereWolf() && !p.getRole().isNeutral()) {
                wolves.add(p);
            }
        }

        if (wolves.isEmpty()){
            return;
        }
        PlayerWW culprit = wolves.get((int) Math.floor(new Random(System.currentTimeMillis()).nextFloat()*wolves.size()));
        addAffectedPlayer(culprit.getRole().getPlayerUUID());

        Player player = Bukkit.getPlayer(getPlayerUUID());
        player.sendMessage(game.translate("werewolf.role.witness.reveal_culprit",culprit.getName()));

    }

    @EventHandler
    public void onFinalDeathEvent(FinalDeathEvent event){
        UUID uuid = event.getUuid();
        if(!getAffectedPlayers().contains(uuid)) return;
        if(game.getPlayersWW().get(getPlayerUUID()).isState(State.DEATH)) return;
        if(!power) return;

        removeAffectedPlayer(uuid);
        this.power = false;

        Player player = Bukkit.getPlayer(getPlayerUUID());
        player.setMaxHealth(Math.max(1, player.getMaxHealth() - 8));
        player.sendMessage(game.translate("werewolf.role.witness.culprit_death"));
    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event){
        UUID newUUID = event.getNewUUID();
        UUID oldUUID = event.getOldUUID();
        PlayerWW p = game.getPlayersWW().get(getPlayerUUID());

        if(!getAffectedPlayers().contains(oldUUID)) return;

        removeAffectedPlayer(oldUUID);
        addAffectedPlayer(newUUID);

        if(!p.isState(State.ALIVE)) return;

        if(Bukkit.getPlayer(getPlayerUUID())!=null) {
            Player player = Bukkit.getPlayer(getPlayerUUID());
            player.sendMessage(game.translate("werewolf.role.witness.change",game.getPlayersWW().get(newUUID).getName()));
        }
    }

    @Override
    public void stolen(UUID uuid){

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if(power) {
            player.sendMessage(game.translate("werewolf.role.witness.reveal_culprit",game.getPlayersWW().get(getAffectedPlayers().get(0)).getName()));
        }
        else {
            player.setMaxHealth(Math.max(1, player.getMaxHealth() - 8));
        }
    }
}
