package io.havwila.addonsLG.roles;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.*;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.ITransformed;
import io.github.ph1lou.werewolfapi.rolesattributs.Role;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class RomulusRemus extends Role implements IAffectedPlayers, ITransformed {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private boolean isTransformed;
    private boolean isRomulus;
    private boolean isInitialized;
    private boolean staysNeutral;
    private boolean killedBrother;
    private boolean metMother;
    private int counter = 0;

    public RomulusRemus(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW, @NotNull String key) {
        super(game, playerWW, key);
        this.setDisplayRole(RolesBase.VILLAGER.getKey());
        this.setDisplayCamp(Camp.VILLAGER.getKey());
        isTransformed = false;
        isInitialized = false;
        staysNeutral = false;
        metMother = false;
    }

    @Override
    public @NotNull String getDescription() {

        if (!isTransformed && !staysNeutral ) {
            DescriptionBuilder descriptionBuilder = new DescriptionBuilder(game, this)
                    .setDescription(game.translate("werewolf.role.romulus_remus.description_naive"))
                    .setPower(game.translate("werewolf.role.romulus_remus.power_naive"));
            getBrother().ifPresent(brother -> {
                descriptionBuilder.addExtraLines(
                        game.translate("werewolf.role.romulus_remus.brother_name",
                                Formatter.format("&name&", brother.getName())));
            });
            getMother().ifPresent(mother -> {
                descriptionBuilder.addExtraLines(game.translate("werewolf.role.romulus_remus.mother_role",
                        Formatter.format("&role&", game.translate(mother.getRole().getKey()))));
            });
            return descriptionBuilder.build();
        }

        if (isTransformed) {
            if (isRomulus) {
                DescriptionBuilder descriptionBuilder = new DescriptionBuilder(game, this)
                        .setDescription(game.translate("werewolf.role.romulus_remus.description_romulus"));
                if (killedBrother) {
                    descriptionBuilder.addExtraLines(game.translate("werewolf.role.romulus_remus.strength"));
                }
                return descriptionBuilder.build();
            } else {
                DescriptionBuilder descriptionBuilder = new DescriptionBuilder(game, this)
                        .setDescription(game.translate("werewolf.role.romulus_remus.description_remus"));
                if (killedBrother) {
                    descriptionBuilder.addExtraLines(game.translate("werewolf.role.romulus_remus.strength"));
                }
                return descriptionBuilder.build();
            }
        }

        if (staysNeutral) {
            DescriptionBuilder descriptionBuilder = new DescriptionBuilder(game, this)
                    .setDescription(game.translate("werewolf.role.romulus_remus.description_neutral"));
            if (killedBrother) {
                descriptionBuilder.addExtraLines(game.translate("werewolf.role.romulus_remus.weakness"));
            }
            return descriptionBuilder.build();
        }
        return "This shouldn't be shown. Please inform havwila of the sequence of events that affected this role in this game";
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
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
    public boolean isNeutral() {
        return  (!isTransformed || staysNeutral) && !super.isWereWolf();
    }

    @Override
    public boolean isWereWolf() {
        return super.isWereWolf() || (this.isTransformed && !isRomulus);
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        initialize();
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW == null) {
            return;
        }

        if (!getAffectedPlayers().contains(playerWW)) {
            return;
        }

        if (getPlayerWW().isState(StatePlayer.DEATH)) {
            return;
        }

        if (playerWW.equals(getBrother().orElse(null))) {

            playerWW.getLastKiller().ifPresent(iPlayerWW -> {
                if (getPlayerWW().equals(iPlayerWW)) {
                    killedBrother = true;
                    if (isTransformed) {
                        if (isAbilityEnabled()) {
                            getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE, "romulus_remus_strength"));
                        }
                        getPlayerWW().sendMessageWithKey("werewolf.role.romulus_remus.killed_brother_strength");
                        if (!isRomulus) {
                            if (!super.isWereWolf()) {
                                Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(getPlayerWW()));
                            }
                        }
                    } else {
                        getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.WEAKNESS, "romulus_remus_weakness"));
                        getPlayerWW().sendMessageWithKey("werewolf.role.romulus_remus.killed_brother_weakness");
                    }
                }
            });
            if (!isTransformed) {
                staysNeutral = true;
                getPlayerWW().sendMessageWithKey("werewolf.role.romulus_remus.brother_died");
            }
        }

        if (staysNeutral) {
            return;
        }

        if (playerWW.equals(getMother().orElse(null))) {
            setTransformed(true);
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE, "romulus_remus_resistance"));
            getPlayerWW().sendMessageWithKey("werewolf.role.romulus_remus.mother_dead");
            if (!isRomulus) {
                playerWW.getLastKiller().ifPresent(iPlayerWW -> {
                    if (getPlayerWW().equals(iPlayerWW)) {
                        staysNeutral = true;
                        getPlayerWW().sendMessageWithKey("werewolf.role.romulus_remus.slayed_mother");
                    }
                });
            }
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    private void initialize() {

        if (!affectedPlayers.isEmpty() || isInitialized) {
            return;
        }

        List<IPlayerWW> brothers = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.getRole().isKey("werewolf.role.romulus_remus.display"))
                .filter(playerWW -> playerWW.getRole() instanceof RomulusRemus && !((RomulusRemus) playerWW.getRole()).isInitialized())
                .collect(Collectors.toList());

        if (brothers.isEmpty()) {
            staysNeutral = true;
            return;
        }

        IPlayerWW brother = brothers.get(game.getRandom().nextInt(brothers.size()));
        isRomulus = game.getRandom().nextBoolean();

        List<IPlayerWW> wolves = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().getKey() != this.getKey())
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (wolves.isEmpty()) {
            wolves = game.getPlayersWW().stream()
                    .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                    .filter(playerWW -> playerWW.getRole().getKey() != this.getKey())
                    .collect(Collectors.toList());
        }

        if (wolves.isEmpty()) {
            //All players in the game have the Romulus or Remus role
            staysNeutral = true;
            return;
        }

        IPlayerWW mother = wolves.get(game.getRandom().nextInt(wolves.size()));

        if (!(brother.getRole() instanceof RomulusRemus)) {
            staysNeutral = true;
            return;
        }
        ((RomulusRemus) brother.getRole()).initialize(getPlayerWW(), mother, !isRomulus);

        initialize(brother, mother, isRomulus);

    }

    public void initialize(IPlayerWW brother, IPlayerWW mother, boolean isRomulus) {

        this.isRomulus = isRomulus;
        addAffectedPlayer(brother);
        addAffectedPlayer(mother);

        isInitialized = true;
        getPlayerWW().sendMessageWithKey("werewolf.role.romulus_remus.family_init");
    }

    public Optional<IPlayerWW> getBrother() {
        return affectedPlayers.isEmpty() ? Optional.empty() : Optional.of(affectedPlayers.get(0));
    }

    public Optional<IPlayerWW> getMother() {
        return affectedPlayers.isEmpty() ? Optional.empty() : Optional.of(affectedPlayers.get(1));
    }

    @Override
    public boolean isTransformed() {
        return isTransformed;
    }

    @Override
    public void setTransformed(boolean b) {
        isTransformed = b;
    }

    @Override
    public void recoverPotionEffect() {

        if (isTransformed && killedBrother && isAbilityEnabled()) {
            getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE, "romulus_remus_strength"));
        }

        if (!isTransformed && staysNeutral && killedBrother) {
            getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.WEAKNESS, "romulus_remus_weakness"));
        }
    }

    @Override
    @EventHandler(priority = EventPriority.HIGH)
    public void onNightForWereWolf(NightEvent event) {

        if (super.isWereWolf() && !(this.isTransformed && killedBrother)) {
            super.onNightForWereWolf(event);
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if (!isAbilityEnabled()) return;

        if(!this.isWereWolf()) return;

        if(!this.game.getConfig().isConfigActive(ConfigBase.WEREWOLF_CHAT.getKey())) return;

        openWereWolfChat();

    }

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDayForWereWolf(DayEvent event) {

        if (super.isWereWolf() && !(this.isTransformed && killedBrother)) {
            super.onDayForWereWolf(event);
        }

    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) {
            return;
        }

        if (isTransformed || staysNeutral) {
            return;
        }

        if (!isAbilityEnabled()) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        IPlayerWW brother;
        if (getBrother().isPresent()) {
            brother = getBrother().get();
        } else {
            return;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!brother.isState(StatePlayer.ALIVE)) return;

        stringBuilder.append("§b ")
                .append(brother.getName())
                .append(" ")
                .append(Utils.updateArrow(player, brother.getLocation()));

        event.setActionBar(stringBuilder.toString());
    }

    @EventHandler
    public void onFamilyIsStolen(StealEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW thiefWW = event.getThiefWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        if (this.getPlayerWW().isState(StatePlayer.DEATH)) return;

        int i = affectedPlayers.indexOf(playerWW);
        affectedPlayers.set(i, thiefWW);

        getPlayerWW().sendMessageWithKey("werewolf.role.romulus_remus.family_change");
    }

    @EventHandler
    public void onDetectVictoryWithFamily(WinConditionsCheckEvent event) {

        if (event.isCancelled()) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (staysNeutral || isTransformed || affectedPlayers.isEmpty()) return;

        IPlayerWW brother = getBrother().orElse(null);

        if (brother == null || !brother.isState(StatePlayer.ALIVE)) return;

        IPlayerWW mother = getMother().orElse(null);

        if (mother == null || !mother.isState(StatePlayer.ALIVE)) return;

        if (game.getPlayerSize() == 3) {
            event.setCancelled(true);
            event.setVictoryTeam("werewolf.role.romulus_remus.display");
        }

    }


    @Override
    public void second() {

        counter++;
        if (counter % 6 != 0) return;
        counter = 0;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        IPlayerWW brother = getBrother().orElse(null);
        IPlayerWW mother = getMother().orElse(null);

        Location location = this.getPlayerWW().getLocation();

        if (brother != null && !isTransformed && !staysNeutral && brother.isState(StatePlayer.ALIVE) && isAbilityEnabled()) {
            boolean recoverResistance = brother.getLocation().distance(location) > 60;

            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE, "romulus_remus_resistance"));
            if (recoverResistance) {
                this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, "romulus_remus_resistance"));
            }
        }

        if (mother == null || metMother || !mother.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (mother.getLocation().distance(location) < 20) {
            BukkitUtils.scheduleSyncDelayedTask(() -> {
                mother.sendMessageWithKey("werewolf.role.romulus_remus.mother_message"); }, 20 * 60 * 2);
            metMother = true;
        }
    }

    @Override
    public void disableAbilities() {
        super.disableAbilities();

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,"romulus_remus_strength"));
    }
}
