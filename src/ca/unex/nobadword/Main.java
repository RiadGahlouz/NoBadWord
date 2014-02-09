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
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin implements Listener{
	public static Logger logger;
	public static Main plugin;
	public String UnexCraftMessage = ChatColor.RED+"["+ChatColor.DARK_AQUA+getCustomConfig().getString("ServerName")+ChatColor.RED+"] ";
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
		reloadCustomConfig();
		getCustomConfig().options().copyDefaults(true);
		saveCustomConfig();
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
		saveCustomConfig();
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
						List<String> confList = (List<String>)getCustomConfig().getList("Badwords");
						if(confList.contains(args[0])){
							player.sendMessage(AdminMessage + ChatColor.GREEN +"Word \""+ChatColor.YELLOW+args[0]+ChatColor.GREEN +"\" " +ChatColor.RED+"Already exists"+ChatColor.GREEN +" in BadWord List!");
						}else{
							confList.add(args[0].toLowerCase());
							getCustomConfig().set("Badwords", confList);
							saveCustomConfig();
							player.sendMessage(AdminMessage + ChatColor.GREEN +"Word \""+ChatColor.YELLOW+args[0]+ChatColor.GREEN +"\" has been added to BadWord List!");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Usage: /badword <word>");
					}
				}else{
					player.sendMessage(UnexCraftMessage + ChatColor.RED + "You don't have permission to perform this command!");
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
						player.sendMessage(getCustomConfig().getList("Badwords").toString());					}else{
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
                List<String> badwords  =(List<String>) (getCustomConfig().getList("Badwords"));
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
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "jail "+event.getPlayer().getName() + " jail 60");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute "+event.getPlayer().getName()+" 90");
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
	public void reloadCustomConfig() {
	    if (customConfigFile == null) {
	    customConfigFile = new File(getDataFolder(), "badwords.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource("badwords.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getCustomConfig() {
	    if (customConfig == null) {
	        reloadCustomConfig();
	    }
	    return customConfig;
	}
	
	public void saveCustomConfig() {
	    if (customConfig == null || customConfigFile == null) {
	        return;
	    }
	    try {
	        getCustomConfig().save(customConfigFile);
	    } catch (IOException ex) {
	        getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
	    }
	}
	
        @Override
	public void saveDefaultConfig() {
	    if (customConfigFile == null) {
	        customConfigFile = new File(getDataFolder(), "badwords.yml");
	    }
	    if (!customConfigFile.exists()) {            
	         plugin.saveResource("badwords.yml", false);
	     }
	}

}
