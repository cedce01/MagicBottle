package vontus.magicbottle;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import vontus.magicbottle.config.Config;
import vontus.magicbottle.config.Messages;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Plugin extends JavaPlugin {
	public static Logger logger;
	public Map<String,Boolean> autoEnabled = new HashMap<>();
	Economy econ = null;

	@Override
	public void onEnable() {
		logger = getLogger();
		logger.log(Level.INFO,"Started Magic Bottle");
		autoEnabled = new HashMap<String,Boolean>();
		setupEconomy();
		loadConfig();
		loadRepAutoContinuously();
		new Recipes(this);
		this.getServer().getPluginManager().registerEvents(new Events(this), this);
		this.getCommand("magicbottle").setExecutor(new Commands(this));
	}

	public void loadRepAutoContinuously(){
		File file = new File(this.getDataFolder() + "/repairContinuously.txt");
		if(file.exists()){
            try (BufferedReader fr = new BufferedReader( new FileReader(file))){
                while (fr.ready()){
					String line = fr.readLine();
					autoEnabled.put(line.split(" ")[0],true);
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
