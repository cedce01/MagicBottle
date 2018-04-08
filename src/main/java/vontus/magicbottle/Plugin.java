package vontus.magicbottle;

import java.util.HashSet;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import vontus.magicbottle.Commands;
import vontus.magicbottle.Events;
import vontus.magicbottle.config.Config;
import vontus.magicbottle.config.Messages;

public class Plugin extends JavaPlugin {
	public static Logger logger;
	public HashSet<Player> autoEnabled = new HashSet<>();
	Economy econ = null;

	@Override
	public void onEnable() {
		logger = getLogger();
		setupEconomy();
		loadConfig();
		new Recipes(this);
		this.getServer().getPluginManager().registerEvents(new Events(this), this);
		this.getCommand("magicbottle").setExecutor(new Commands(this));
		new Metrics(this);
	}

	public void loadConfig() {
		this.reloadConfig();

		this.saveDefaultConfig();
		Config.load(this);
		Messages.load(this);
		
		if (Config.costMoneyCraftNewBottle != 0 && econ == null) {
			logger.warning("Vault is required to set economy costs. Add Vault or set the recipe cost to 0 to disable this warning.");
			Config.costMoneyCraftNewBottle = 0;
		}
	}

	private void setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp != null) {
				econ = rsp.getProvider();
			}
		}
	}
}
