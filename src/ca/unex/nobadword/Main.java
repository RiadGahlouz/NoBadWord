package ca.unex.nobadword;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	public static Logger logger;
	public static Main plugin;
	public String UnexCraftMessage = ChatColor.RED+"["+ChatColor.DARK_AQUA+getCustomConfig("language.yml").getString("ServerName")+ChatColor.RED+"] ";
	public static String AdminMessage = ChatColor.RED+"["+ChatColor.DARK_AQUA+"Admin"+ChatColor.RED+"] ";
	//Config Files
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	
	@Override
	public void onEnable(){
		logger = getLogger();
		PluginDescriptionFile pdffile = this.getDescription();
		PluginManager pm = this.getServer().getPluginManager();
                pm.registerEvents(this, this);
		pm.addPermission(new Permissions().UnexcanAddBadWord);
		pm.addPermission(new Permissions().UnexcanListBadWord);
		pm.addPermission(new Permissions().UnexcanSwear);
                //pm.addPermission(new Permissions().UnexcanReloadNBW);
		reloadCustomConfig("badwords.yml");
		getCustomConfig("badwords.yml").options().copyDefaults(true);
		saveCustomConfig("badwords.yml");
                reloadCustomConfig("language.yml");
                getCustomConfig("language.yml").options().copyDefaults(true);
                saveCustomConfig("language.yml");
		//saveDefaultConfig();
		logger.log(Level.INFO, "{0} Version {1} is ON!", new Object[]{pdffile.getName(), pdffile.getVersion()});
	}
	@Override
	public void onDisable(){
		PluginDescriptionFile pdffile = this.getDescription();
		getServer().getPluginManager().removePermission(new Permissions().UnexcanAddBadWord);
		getServer().getPluginManager().removePermission(new Permissions().UnexcanListBadWord);
		getServer().getPluginManager().removePermission(new Permissions().UnexcanSwear);
                //getServer().getPluginManager().removePermission(new Permissions().UnexcanReloadNBW);
		saveCustomConfig("badwords.yml");
                saveCustomConfig("language.yml");
		logger.log(Level.INFO, "{0} is OFF", pdffile.getName());
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
            /*if(commandLabel.equalsIgnoreCase("nbw")){
              if(args.length == 1){
                  if(args[0].equals("reload") || args[0].equals("rl")){
                    if(sender instanceof Player){
                        Player player = (Player) sender;
                  if(player.hasPermission(new Permissions().UnexcanReloadNBW)){
                      reloadCustomConfig();
                      player.sendMessage(MessageFormat.format("{0}{1}Successfully Reloaded badwords.yml File!",AdminMessage,ChatColor.GREEN));
                  }else{
                      player.sendMessage(UnexCraftMessage + ChatColor.RED + "You don't have permission to perform this command!");
                  }
              }  
            }
              }
            }*/
		/*
		 * Command: /badword <badword>
		 */
		if(commandLabel.equalsIgnoreCase("badword")){
			if(sender instanceof Player){
				Player player = (Player) sender;
				if(sender.hasPermission(new Permissions().UnexcanAddBadWord)){
					if(args.length == 0){
						player.sendMessage(ChatColor.RED + "Usage: /badword <word>");
					}else if(args.length == 1){
						@SuppressWarnings("unchecked")
						List<String> confList = (List<String>)getCustomConfig("badwords.yml").getList("Badwords");
						if(confList.contains(args[0])){
							player.sendMessage(AdminMessage + ChatColor.GREEN +"Word \""+ChatColor.YELLOW+args[0]+ChatColor.GREEN +"\" " +ChatColor.RED+"Already exists"+ChatColor.GREEN +" in BadWord List!");
						}else{
							confList.add(args[0].toLowerCase());
							getCustomConfig("badwords.yml").set("Badwords", confList);
							saveCustomConfig("badwords.yml");
							player.sendMessage(AdminMessage + ChatColor.GREEN +"Word \""+ChatColor.YELLOW+args[0]+ChatColor.GREEN +"\" has been added to BadWord List!");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Usage: /badword <word>");
					}
				}else{
					player.sendMessage(UnexCraftMessage + getCustomConfig("language.yml").getString("NoPermission"));
				}
			}else{
                            Bukkit.getConsoleSender().sendMessage(MessageFormat.format("{0}You must execute this commande {1}In-Game!",ChatColor.GREEN,ChatColor.RED));
                        }
		}
		/*
		 * Comand: /badwords
		 */
		if(commandLabel.equalsIgnoreCase("badwords")){
			if(sender instanceof Player){
				Player player = (Player) sender;
				if(sender.hasPermission(new Permissions().UnexcanListBadWord)){
					if(args.length == 0){
						player.sendMessage(getCustomConfig("badwords.yml").getList("Badwords").toString());					}else{
						player.sendMessage(ChatColor.RED + "No args needed! Usage: /badwords");
					}
				}else{
					player.sendMessage(UnexCraftMessage + ChatColor.RED + "You don't have permission to perform this command!");
				}
			}else{
                            Bukkit.getConsoleSender().sendMessage(MessageFormat.format("{0}You must execute this commande {1}In-Game!",ChatColor.GREEN,ChatColor.RED));
                        }
		}
		return false;
	}
	/*
         * HANDLERS
         */

    @EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(final AsyncPlayerChatEvent event){
            if(!event.getPlayer().hasPermission(new Permissions().UnexcanSwear)){
                List<String> badwords  =(List<String>) (getCustomConfig("badwords.yml").getList("Badwords"));
                for (String badword : badwords) {
                    String playerMessage = event.getMessage().toLowerCase();
                    if(playerMessage.matches("(.* )?"+badword+"( .*)?")){
                        Player player = event.getPlayer();
                        //player.performCommand(badword);
                        event.setCancelled(true);
                        //event.setMessage("*Censored*");
                        player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES,10,10);
                        player.getWorld().playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST2, 100,100);
                        player.sendMessage(ChatColor.RED + "You're in " + ChatColor.YELLOW + "jail" + ChatColor.RED+". Stop Swearing!");
                        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
                        @Override
                        public void run(){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "jail "+event.getPlayer().getName() + " jail "+getCustomConfig("badwords.yml").getInt("JailTime"));
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute "+event.getPlayer().getName()+" "+getCustomConfig("badwords.yml").getInt("MuteTime"));
                        }
                        },10L);                        
                        break;
                    }
                }
            }
	}
        
        /*
	 * Config Methods
	 */
	public void reloadCustomConfig(String confName) {
	    if (customConfigFile == null) {
	    customConfigFile = new File(getDataFolder(), confName);
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource(confName);
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getCustomConfig(String confName) {
	    if (customConfig == null) {
	        reloadCustomConfig(confName);
	    }
	    return customConfig;
	}
	
	public void saveCustomConfig(String confName) {
	    if (customConfig == null || customConfigFile == null) {
	        return;
	    }
	    try {
	        getCustomConfig(confName).save(customConfigFile);
	    } catch (IOException ex) {
	        getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
	    }
	}
	
        	public void saveDefaultConfig(String confName) {
	    if (customConfigFile == null) {
	        customConfigFile = new File(getDataFolder(), confName);
	    }
	    if (!customConfigFile.exists()) {            
	         plugin.saveResource(confName, false);
	     }
	}

}
