package io.havwila.addonsLG;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.*;
import io.github.ph1lou.werewolfapi.registers.*;
import io.havwila.addonsLG.commands.CommandAuramancer;
import io.havwila.addonsLG.commands.CommandCroupier;
import io.havwila.addonsLG.commands.CommandInquisitor;
import io.havwila.addonsLG.roles.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Main extends JavaPlugin {

    GetWereWolfAPI ww;

    @Override
    public void onEnable() {

        ww =  getServer().getServicesManager().load(GetWereWolfAPI.class);

        IRegisterManager registerManager = ww.getRegisterManager();

        String addonKey = "werewolf.addon.havwila.name";

        registerManager.registerAddon(new AddonRegister(addonKey, "fr", this)
                .setItem(new ItemStack(UniversalMaterial.ARROW.getType()))
                .addLoreKey("werewolf.addon.havwila.description")
                .addAuthors("havwila", UUID.fromString("792945f6-ce44-4039-8382-8652153fe884")));

        try {
            /*registerManager.registerRole(new RoleRegister(addonKey, "werewolf.role.medium.display", Medium.class)
                    .addLoreKey("werewolf.role.medium.item").addCategory(Category.ADDONS).addCategory(Category.VILLAGER));*/

            registerManager.registerRole(new RoleRegister(addonKey, "werewolf.role.witness.display", Witness.class)
                    .addLoreKey("werewolf.role.witness.item").addCategory(Category.ADDONS).addCategory(Category.VILLAGER));

            registerManager.registerRole(new RoleRegister(addonKey, "werewolf.role.croupier.display", Croupier.class)
                    .addLoreKey("werewolf.role.croupier.item").addCategory(Category.ADDONS).addCategory(Category.VILLAGER)
                    .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION));

            registerManager.registerRole(new RoleRegister(addonKey, "werewolf.role.hunter_havwila.display", Hunter.class)
                    .addLoreKey("werewolf.role.hunter_havwila.item").addCategory(Category.ADDONS).addCategory(Category.VILLAGER)
                    .setRandomCompositionAttribute(RandomCompositionAttribute.INFORMATION));

            registerManager.registerRole(new RoleRegister(addonKey, "werewolf.role.romulus_remus.display", RomulusRemus.class)
                    .addLoreKey("werewolf.role.romulus_remus.item")
                    .addCategory(Category.ADDONS)
                    .addCategory(Category.NEUTRAL).setRequireDouble());

            registerManager.registerRole(new RoleRegister(addonKey, "werewolf.role.inquisitor.display", Inquisitor.class)
                    .addLoreKey("werewolf.role.inquisitor.item")
                    .addCategory(Category.ADDONS)
                    .addCategory(Category.VILLAGER));

            registerManager.registerRole(new RoleRegister(addonKey, "werewolf.role.auramancer.display", Auramancer.class)
                    .addLoreKey("werewolf.role.auramancer.item")
                    .addCategory(Category.ADDONS)
                    .addCategory(Category.VILLAGER));

            registerManager.registerCommands(new CommandRegister(addonKey, "werewolf.role.croupier.command", new CommandCroupier())
                    .addRoleKey("werewolf.role.croupier.display").addStateAccess(StatePlayer.ALIVE)
                    .addStateWW(StateGame.GAME).setRequiredPower().setRequiredAbilityEnabled().addArgNumbers(1));

            registerManager.registerCommands(new CommandRegister(addonKey, "werewolf.role.inquisitor.command", new CommandInquisitor())
                    .addRoleKey("werewolf.role.inquisitor.display").addStateAccess(StatePlayer.ALIVE)
                    .addStateWW(StateGame.GAME).setRequiredAbilityEnabled().setRequiredPower().addArgNumbers(1));

            registerManager.registerCommands(new CommandRegister(addonKey, "werewolf.role.auramancer.command", new CommandAuramancer())
                    .addRoleKey("werewolf.role.auramancer.display").addStateAccess(StatePlayer.ALIVE)
                    .addStateWW(StateGame.GAME).setRequiredPower().setRequiredAbilityEnabled().addArgNumbers(0));

            registerManager.registerConfig(new ConfigRegister(addonKey, "werewolf.global.croupier_every_other_day"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public GetWereWolfAPI getAPI() {
        return ww;
    }
}
