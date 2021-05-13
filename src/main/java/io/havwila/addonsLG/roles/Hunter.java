package io.havwila.addonsLG.roles;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Hunter extends RoleVillage implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private final List<HunterClue> clues = new ArrayList<>();
    private int secondsCount = 0;

    public Hunter(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
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
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.hunter_havwila.description"))
                .setItems(() -> game.translate("werewolf.role.hunter_havwila.items"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onFinalDeathEvent(FinalDeathEvent event) {
        if (getPlayerWW().isState(StatePlayer.DEATH)) {
            return;
        }

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW.equals(getPlayerWW())) {
            //TODO: shoot someone
            return;
        }

        Location deathLocation = playerWW.isState(StatePlayer.ALIVE) ? playerWW.getLocation() : playerWW.getSpawn();
        Set<IPlayerWW> nearbyPlayers = Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> {
                    try {
                        return deathLocation.distance(player.getLocation()) < 70;
                    } catch (Exception ignored) {
                        return false;
                    }
                })
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Objects::nonNull)
                .filter(player -> player.isState(StatePlayer.ALIVE))
                .filter(player -> !player.equals(playerWW))
                .collect(Collectors.toSet());

        clues.add(new HunterClue(playerWW, deathLocation, nearbyPlayers));
    }

    @Override
    public void second() {
        if (!(++secondsCount % 10 == 0)) {
            return;
        }
        secondsCount = 0;
        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        Location location = getPlayerWW().getLocation();
        double distanceOrigin = location.length();
        clues.forEach(clue -> {
            //double comparisons are faster than Location comparisons
            if (clue.getDistanceToOrigin() + 20 < distanceOrigin || clue.getDistanceToOrigin() - 20 > distanceOrigin) {
                return;
            }
            if (clue.getDeathLocation().distance(location) > 20) {
                return;
            }
            clue.incrementCount();
            switch (clue.getCount()) {
                case 6:
                    affectedPlayers.add(clue.getPlayerWW());
                    getPlayerWW().sendMessageWithKey("werewolf.role.hunter_havwila.clue_player", clue.getPlayerWW().getName(),
                            Integer.toString(clue.getNearbyPlayers().size()));
                    return;
                case 12:
                    getPlayerWW().sendMessageWithKey("werewolf.role.hunter_havwila.clue_role", clue.getPlayerWW().getName(),
                            game.translate(clue.getPlayerWW().getRole().getKey()),
                            buildNamesString(clue.getNamesList(), 1));
                    return;
                case 18:
                    getPlayerWW().sendMessageWithKey("werewolf.role.hunter_havwila.clue_nearby", buildNamesString(clue.getNamesList(), 2),
                            clue.getPlayerWW().getName());
                    return;
                case 24:
                    getPlayerWW().sendMessageWithKey("werewolf.role.hunter_havwila.clue_nearby", buildNamesString(clue.getNamesList(), 3),
                            clue.getPlayerWW().getName());
                    clues.remove(clue);
                    return;
                default:
                    return;
            }

        });
    }

    private String buildNamesString(List<String> names, int thirds) {
        int nReadable = names.size() * thirds / 3;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (i < nReadable) {
                list.add(names.get(i));
            } else {
                list.add(ChatColor.MAGIC + " Coucou");
            }
        }
        return list.toString();
    }

    private class HunterClue {

        private final IPlayerWW playerWW;
        private final Location deathLocation;
        private final double distanceToOrigin;
        private final Set<IPlayerWW> nearbyPlayers;
        private List<String> playerNames;
        private int count;

        private HunterClue(IPlayerWW playerWW, Location deathLocation, Set<IPlayerWW> nearbyPlayers) {
            this.playerWW = playerWW;
            this.deathLocation = deathLocation;
            this.distanceToOrigin = deathLocation.length();
            this.nearbyPlayers = nearbyPlayers;
            this.playerNames = nearbyPlayers.stream().map(IPlayerWW::getName).collect(Collectors.toList());
            Collections.shuffle(playerNames);
            this.count = 0;
        }

        public int getCount() {
            return count;
        }

        public void incrementCount() {
            count++;
        }

        public IPlayerWW getPlayerWW() {
            return playerWW;
        }

        public Location getDeathLocation() {
            return deathLocation;
        }

        public double getDistanceToOrigin() {
            return distanceToOrigin;
        }

        public Set<IPlayerWW> getNearbyPlayers() {
            return nearbyPlayers;
        }

        public List<String> getNamesList() {
            return playerNames;
        }
    }
}
