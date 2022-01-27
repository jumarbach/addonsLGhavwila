package io.havwila.addonsLG;


import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.registers.impl.AddonRegister;
import fr.ph1lou.werewolfapi.registers.impl.CommandRegister;
import fr.ph1lou.werewolfapi.registers.impl.ConfigRegister;
import fr.ph1lou.werewolfapi.registers.impl.RoleRegister;
import fr.ph1lou.werewolfapi.registers.interfaces.IRegisterManager;
import io.havwila.addonsLG.commands.*;
import io.havwila.addonsLG.roles.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.plaf.nimbus.State;
import java.util.UUID;

public class Main extends JavaPlugin {

    GetWereWolfAPI ww;

    @Override
    public void onEnable() {

        ww =  getServer().getServicesManager().load(GetWereWolfAPI.class);

        IRegisterManager registerManager = ww.getRegisterManager();

        String addonKey = "havwila.addon.havwila.name";

        registerManager.registerAddon(new AddonRegister(addonKey, "fr", this)
                .setItem(new ItemStack(UniversalMaterial.ARROW.getType()))
                .addLoreKey("havwila.addon.havwila.description")
                .addAuthors("havwila", UUID.fromString("792945f6-ce44-4039-8382-8652153fe884")));

        try {
            registerManager.registerRole(new RoleRegister(addonKey, "havwila.role.witness.display", Witness.class)
                    .addLoreKey("havwila.role.witness.item").addCategory(Category.ADDONS).addCategory(Category.VILLAGER));

            registerManager.registerRole(new RoleRegister(addonKey, "havwila.role.croupier.display", Croupier.class)
                    .addLoreKey("havwila.role.croupier.item").addCategory(Category.ADDONS).addCategory(Category.VILLAGER)
                    .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION).addConfig(Croupier::configOtherDay));

            registerManager.registerRole(new RoleRegister(addonKey, "havwila.role.hunter.display", Hunter.class)
                    .addLoreKey("havwila.role.hunter.item").addCategory(Category.ADDONS).addCategory(Category.VILLAGER)
                    .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION).addConfig(Hunter::configCanShoot));

            registerManager.registerRole(new RoleRegister(addonKey, "havwila.role.romulus_remus.display", RomulusRemus.class)
                    .addLoreKey("havwila.role.romulus_remus.item")
                    .addCategory(Category.ADDONS)
                    .addCategory(Category.NEUTRAL).setRequireDouble());

            registerManager.registerRole(new RoleRegister(addonKey, "havwila.role.inquisitor.display", Inquisitor.class)
                    .addLoreKey("havwila.role.inquisitor.item")
                    .addCategory(Category.ADDONS)
                    .addCategory(Category.VILLAGER));

            registerManager.registerRole(new RoleRegister(addonKey, "havwila.role.auramancer.display", Auramancer.class)
                    .addLoreKey("havwila.role.auramancer.item")
                    .addCategory(Category.ADDONS)
                    .addCategory(Category.VILLAGER));

            registerManager.registerRole(new RoleRegister(addonKey, "havwila.role.mastermind.display", Mastermind.class)
                    .addLoreKey("havwila.role.mastermind.item")
                    .addCategory(Category.NEUTRAL)
                    .addCategory(Category.ADDONS));

            registerManager.registerRole(new RoleRegister(addonKey, "havwila.role.silencer_werewolf.display", SilencerWerewolf.class)
                    .addLoreKey("havwila.role.silencer_werewolf.item").addCategory(Category.WEREWOLF).addCategory(Category.ADDONS));


            registerManager.registerCommands(new CommandRegister(addonKey, "havwila.role.croupier.command", new CommandCroupier())
                    .addRoleKey("havwila.role.croupier.display").addStateAccess(StatePlayer.ALIVE)
                    .addStateWW(StateGame.GAME).setRequiredPower().setRequiredAbilityEnabled().addArgNumbers(1));

            registerManager.registerCommands(new CommandRegister(addonKey, "havwila.role.inquisitor.command", new CommandInquisitor())
                    .addRoleKey("havwila.role.inquisitor.display").addStateAccess(StatePlayer.ALIVE)
                    .addStateWW(StateGame.GAME).setRequiredAbilityEnabled().setRequiredPower().addArgNumbers(1));

            /*registerManager.registerCommands(new CommandRegister(addonKey, "havwila.role.auramancer.command", new CommandAuramancer())
                    .addRoleKey("havwila.role.auramancer.display").addStateAccess(StatePlayer.ALIVE)
                    .addStateWW(StateGame.GAME).setRequiredPower().setRequiredAbilityEnabled().addArgNumbers(0));*/

            registerManager.registerCommands(new CommandRegister(addonKey, "havwila.role.hunter.command", new CommandHunter())
                    .addRoleKey("havwila.role.hunter.display").addStateAccess(StatePlayer.DEATH)
                    .addStateWW(StateGame.GAME).setRequiredPower().setRequiredAbilityEnabled().addArgNumbers(1));

            registerManager.registerCommands(new CommandRegister(addonKey, "havwila.guess.command", new CommandGuess())
                    .addStateWW(StateGame.GAME).addStateAccess(StatePlayer.ALIVE).addArgNumbers(1));

            registerManager.registerCommands(new CommandRegister(addonKey, "havwila.role.mastermind.command_disable", new CommandMastermindDisable())
                    .addRoleKey("havwila.role.mastermind").addStateAccess(StatePlayer.ALIVE)
                    .addStateWW(StateGame.GAME).addArgNumbers(1).setRequiredAbilityEnabled());

            registerManager.registerCommands(new CommandRegister(addonKey, "havwila.role.mastermind.command_swap", new CommandMastermindSwap())
                    .addRoleKey("havwila.role.mastermind").addStateAccess(StatePlayer.ALIVE)
                    .addStateWW(StateGame.GAME).setRequiredAbilityEnabled().addArgNumbers(2));

            registerManager.registerConfig(new ConfigRegister(addonKey, "havwila.role.croupier.croupier_every_other_day")
                    .unSetAppearInMenu());

            registerManager.registerConfig(new ConfigRegister(addonKey, "havwila.role.hunter.can_shoot")
                    .unSetAppearInMenu());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public GetWereWolfAPI getAPI() {
        return ww;
    }
}
