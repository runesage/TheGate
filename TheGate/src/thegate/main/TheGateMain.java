package thegate.main;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.gui.tools.guitools.InventoryManager;
import com.packageing.tools.packagetools.PackageManager;
import com.packageing.tools.packagetools.entitys.ArmorStand;

import bstats.Metrics;
import thegate.bungee.GateSync;
import thegate.bungee.PlayerServerData;
import thegate.bungee.Reconnect;
import thegate.commands.TheGateCommand;
import thegate.gate.CommandUseType;
import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.save.AutoSyncDatabase;
import thegate.main.save.DBGateAccess;
import thegate.main.save.DatabaseManager;
import thegate.math.GateMath;

public class TheGateMain extends JavaPlugin implements Listener, PluginMessageListener {

	public ConfigManager configManager;

	public static DBGateAccess SaveLoadInterface;
	public DatabaseManager dbManager;
	
	public static TheGateMain theGateMain;

	public Reconnect reconnect = new Reconnect();

	private int CooldownTimer = 100;
	
	// Longer than Velocity's default 30 second read timeout for joining a server.
	private static final int TransitRowTimeout = 20 * 60;
	
	public LocalDateTime AutoSyncTime = LocalDateTime.now();


	private static String[] supportedVersions = {"26.1"};
	
	@Override
	public void onLoad() {
		PackageManager.load(this);
	}

	@Override
	public void onEnable() {
		theGateMain = this;
		PackageManager.enable();
//		this.getLogger().log(Level.INFO, "");
//		this.getLogger().log(Level.INFO, ChatColor.GREEN + "----------------------------------------------------------------------------");
//		this.getLogger().log(Level.INFO, ChatColor.GRAY +   "\t\t┏━━━━━┓ ┏━┓ ┏━┓ ┏━━━━┓    ┏━━━━━━┓ ┏━━━━━┓ ┏━━━━━┓ ┏━━━━┓");
//		this.getLogger().log(Level.INFO, ChatColor.GRAY +   "\t\t┗━┓ ┏━┛ ┃ ┃ ┃ ┃ ┃ ┏━━┛    ┃ ┏━━━━┛ ┃ ┏━┓ ┃ ┗━┓ ┏━┛ ┃ ┏━━┛");
//		this.getLogger().log(Level.INFO, ChatColor.RED +    "\t\t  ┃ ┃   ┃ ┗━┛ ┃ ┃ ┗━━┓    ┃ ┃ ┏━━┓ ┃ ┗━┛ ┃   ┃ ┃   ┃ ┗━━┓");
//		this.getLogger().log(Level.INFO, ChatColor.RED +    "\t\t  ┃ ┃   ┃ ┏━┓ ┃ ┃ ┏━━┛    ┃ ┃ ┗┓ ┃ ┃ ┏━┓ ┃   ┃ ┃   ┃ ┏━━┛");
//		this.getLogger().log(Level.INFO, ChatColor.YELLOW + "\t\t  ┃ ┃   ┃ ┃ ┃ ┃ ┃ ┗━━┓    ┃ ┗━━┛ ┃ ┃ ┃ ┃ ┃   ┃ ┃   ┃ ┗━━┓");
//		this.getLogger().log(Level.INFO, ChatColor.YELLOW + "\t\t  ┗━┛   ┗━┛ ┗━┛ ┗━━━━┛    ┗━━━━━━┛ ┗━┛ ┗━┛   ┗━┛   ┗━━━━┛");
//		this.getLogger().log(Level.INFO, ChatColor.GREEN + "----------------------------------------------------------------------------");
//		this.getLogger().log(Level.INFO, "");
		
		
		if(!isServerCompatible()) {
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "This version of the plugin is not supported on your server version!");
			this.getLogger().log(Level.WARNING, "You are using: " + ((Plugin)TheGateMain.theGateMain).getServer().getVersion());
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "Supported Versions by the Plugin:");
			for(String s : supportedVersions) {
				this.getLogger().log(Level.WARNING, "\t-" + s);
			}
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "For a compatible version got to the version history on the spigot page.");
			this.getLogger().log(Level.WARNING, "For more information, requests or other support you could join our discord:");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "https://discord.gg/RW4C7bn");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------");
		}
		
		Config.LoadConfig(this);

		this.configManager = new ConfigManager(this);
		try {
			this.configManager.CreateConfigFiles();
		} catch (Exception e1) {
			disable();
			return;
		}
		
		boolean missingConfig = false;
		if(configManager.hasUpdateConfig()) {	
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "Your are missing some config options!");
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "If you want to do the change manualy heare is a list of all the missing options:");
			this.getLogger().log(Level.WARNING, "");
			ConfigManager.updatedValuesConfig.keySet().stream().forEach(x -> {
				this.getLogger().log(Level.WARNING, x + " : " + ConfigManager.updatedValuesConfig.get(x));
			});
			this.getLogger().log(Level.WARNING, "");
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "You can see the newest version of the config file on our github page.");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "https://github.com/Badading/The_Gate/wiki/Config");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "For more information, requests or other support you could join our discord:");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "https://discord.gg/RW4C7bn");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------");
			missingConfig = true;
		}
		
		if(configManager.hasUpdateLang()) {
			this.getLogger().log(Level.WARNING, "-------------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "Your are missing some lines in the language file!");
			this.getLogger().log(Level.WARNING, "-------------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "If you want to do the change manualy heare is a list of all the missing options:");
			this.getLogger().log(Level.WARNING, "");
			ConfigManager.updatedValuesLang.keySet().stream().forEach(x -> {
				this.getLogger().log(Level.WARNING, x + " : " + ConfigManager.updatedValuesLang.get(x));
			});
			this.getLogger().log(Level.WARNING, "");
			this.getLogger().log(Level.WARNING, "-------------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "You can see the newest version of the language file on our github page.");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "https://github.com/Badading/The_Gate/wiki/TextConfig");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "For more information, requests or other support you could join our discord:");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "https://discord.gg/RW4C7bn");
			this.getLogger().log(Level.WARNING, " ");
			this.getLogger().log(Level.WARNING, "-------------------------------------------------------------------------------");							
			missingConfig = true;
		}
		
		if(missingConfig) {
			disable();
			return;
		}
		
		if(Globals.UseBungee && !Globals.SaveFromat.equalsIgnoreCase("mysql")) {
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------------------");
			this.getLogger().log(Level.WARNING, "You are using bungee but no mysql database please connect the plguin to a mysql database");
			this.getLogger().log(Level.WARNING, "----------------------------------------------------------------------------------------");
		}
		
		getServer().getPluginManager().registerEvents(this, this);

		registerCommands();
		if(Globals.SaveFromat.equalsIgnoreCase("mysql")) {
			try {
				this.dbManager = new DatabaseManager(DatabaseManager.getDatabaseInfo(Globals.MySQLPath, Globals.MySQLUserName, Globals.MySQLUserPassword, Globals.SaveFromat.toUpperCase()));
			} catch (Exception e) {
				e.printStackTrace();
				disable();
				return;
			}
		}else if(Globals.SaveFromat.equalsIgnoreCase("sqlite")) {
			this.dbManager = new DatabaseManager(DatabaseManager.getDatabaseInfo(this, "GateSaveFile"));
		}else {
			this.getLogger().log(Level.WARNING, "Could not identify save format: " + Globals.SaveFromat);
			this.getLogger().log(Level.WARNING, "Supported save formats: SQLITE | MYSQL");
			disable();
			return;
		}
		if(!dbManager.createDatabaseConnection(this)) {
			disable();
			return;
		}
		
		SaveLoadInterface = new DBGateAccess(dbManager);

		getServer().getPluginManager().registerEvents(new GateTools(), this);

		getServer().getPluginManager().registerEvents(new InventoryManager(), this);
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		
		if (SaveLoadInterface != null) {
			SaveLoadInterface.Load(this, null);
			SaveLoadInterface.LoadCoowners(this);
		}

		if (Globals.SaveFromat.equalsIgnoreCase("mysql") && Globals.UseBungee) {
			SaveLoadInterface.ClearPlayerTable();
		}

		if (Globals.SaveFromat.equalsIgnoreCase("mysql") && Globals.AutoSyncDatabase >= 0) {
			new AutoSyncDatabase(this).runTaskTimer(this, 0, Globals.AutoSyncDatabase);
		} else if (Globals.SaveFromat.equalsIgnoreCase("mysql") && Globals.AutoSyncDatabase < 0) {
//			GateManager.setGatesOnOtherServer(SaveLoadInterface.GateListOtherServers(this));
		}

		if (!Globals.SaveFromat.equalsIgnoreCase("mysql") && Globals.UseBungee) {
			this.getLogger().log(Level.WARNING, "If you want to use Bungee you have to have a MySql database connected!");
			disable();
		} else {
			this.getLogger().log(Level.INFO, "Have a save journey");
		}
		
		bStats();
	}
	
	public static void disable() {
		theGateMain.getServer().getPluginManager().disablePlugin(theGateMain);
		if(SaveLoadInterface != null) {
			SaveLoadInterface.stopAsyncWorker();
			SaveLoadInterface.closeConnection();
		}
	}

	public void bStats() {
		int PluginIDBStats = 7042;
		Metrics metrics = new Metrics(this, PluginIDBStats);
		
		metrics.addCustomChart(new Metrics.SimplePie("use_bungee", new Callable<String>() {
			
			@Override
			public String call() throws Exception {

				if(Globals.UseBungee) {
					return "True";
				}else {
					return "False";
				}
			}
		}));
		
		metrics.addCustomChart(new Metrics.SimplePie("save_format", new Callable<String>() {
			
			@Override
			public String call() throws Exception {
				return Globals.SaveFromat.toLowerCase();
			}
		}));
	}

	@Override
	public void onDisable() {
		if (SaveLoadInterface != null) {
			SaveLoadInterface.SaveDataFromSet(GateManager.getGatesAsSet());
		}
		
		for(GateObject gate : GateManager.getGatesAsSet()) {
			gate.Vanish();
		}

		if(SaveLoadInterface != null) {
			SaveLoadInterface.stopAsyncWorker();
			SaveLoadInterface.closeConnection();
		}

		PackageManager.disable();
	}
	
	@EventHandler
	public void OnPlayerQuitEvent(PlayerQuitEvent e) {
		String worldName = e.getPlayer().getLocation().getWorld().getName();
		if(worldName == null || worldName == "") return;
		
		Set<GateObject> gates = GateManager.getGatesInWorld(worldName);
		if(gates == null) return;
		
		gates.stream().forEach(x -> x.removePlayerInRangeDisconnect(e.getPlayer()));
	}

	boolean hasWorldRefreshed = false;
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(!hasWorldRefreshed) {
			hasWorldRefreshed = true;
			GateManager.getGatesAsSet().stream().forEach(x-> {
				Location l = new Location(this.getServer().getWorld(x.getWorldName()), x.getGate().getX(), x.getGate().getY(), x.getGate().getZ());
				x.setGate(l);
				if(x.hasDHD()) {
					Location l2 = new Location(this.getServer().getWorld(x.getWorldName()), x.getDHD().getX(), x.getDHD().getY(), x.getDHD().getZ());
					x.setDHD(l2);
				}
			});
		}
		
		Player player = e.getPlayer();

		// Gate change messages can't reach a server while it is empty, so catch up when it fills again.
		if(Globals.UseBungee && getServer().getOnlinePlayers().size() == 1) GateSync.reloadGatesOnOtherServers();

		ArmorStand stand = ArmorStand.CreateArmorStand(player.getLocation(), 0, 0, 0, 0);
		stand.setHeadMaterial(new ItemStack(Material.STONE));
		PackageManager.SendSpawnPackage(stand, player);
		PackageManager.SendDespawnPackage(stand.getEntityID(), player);
		
		
		OnCooldown.add(player.getName());
		GateManager.GateInRadius(player, Globals.VisibilityRadius);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				OnCooldown.remove(player.getName());
			}

		}.runTaskLater(this, CooldownTimer);

		if (Globals.GateBuilderUUID.contains(e.getPlayer().getUniqueId().hashCode())) {
			e.getPlayer().sendMessage(ChatColor.GOLD + "[The Gate] Welcome traveler " + ChatColor.RED + "["
					+ e.getPlayer().getName() + "]" + ChatColor.GOLD + " may your journey be a save one.");
		}

		if (Globals.UseBungee) {
			// teleporting for bungee
			PlayerServerData psd = SaveLoadInterface.getPlayerDataFromPlayerTable(player);
			
			if (psd != null) {
				GateObject destinationGate = GateManager.getGateWithAddress(psd.getAddressTo());
				GateObject GateOnOtherServer = GateManager.getGateOnOtherServerWithAddress(psd.getAddressFrom());
								
				if (destinationGate != null && GateOnOtherServer != null) {

					destinationGate.addJoinedPlayer(player);
					
					double playerx = psd.getPlayerX();
					double playery = psd.getPlayerY();
					double playerz = psd.getPlayerZ();

					double x = GateOnOtherServer.getGate().getX() + 0.5D;
					double y = GateOnOtherServer.getGate().getY() + 1.5D;
					double z = GateOnOtherServer.getGate().getZ() + 0.5D;

					double otherGatex = destinationGate.getGate().getX() + 0.5D;
					double otherGatey = destinationGate.getGate().getY() + 1.5D;
					double otherGatez = destinationGate.getGate().getZ() + 0.5D;

					double facing = GateOnOtherServer.getFacing();

					double facingothergate = destinationGate.getFacing();

					playerx -= x;
					playery -= y;
					playerz -= z;

					Vector vp = GateMath.RotateVectorY(new Vector(playerx, playery, playerz),
							Math.toRadians((-facing + 2) * 90));
					Vector vp2 = GateMath.RotateVectorY(vp, Math.toRadians((facingothergate) * 90));

					playerx = vp2.getX();
					playery = vp2.getY();
					playerz = vp2.getZ();

					playerx += otherGatex;
					playery += otherGatey;
					playerz += otherGatez;

					Location loc = new Location(this.getServer().getWorld(destinationGate.getWorldName()), playerx, playery + 0.25, playerz);

					double playeryaw = (psd.getPlayerYaw() + 360) % 360;

					loc.setYaw((float) (playeryaw + 180 + ((facingothergate - facing) * 90)));
					loc.setPitch((float) psd.getPlayerPitch());
					player.teleport(loc);
					destinationGate.dispach(CommandUseType.OnExit, player);
					player.setVelocity(
							GateMath.RotateVectorY(new Vector(0, 0, 0.3D), Math.toRadians((facingothergate) * 90)));
					if(destinationGate.woosh != null) destinationGate.woosh.quickShutdown = 100;
					if (!destinationGate.isActive()) destinationGate.Activate(GateOnOtherServer.getAddress(), false, player);
					this.getServer().getWorld(destinationGate.getWorldName()).playSound(destinationGate.getGate(), Globals.DefaultGateExitSound,
							Globals.DefaultGateExitVolume, Globals.DefaultGateExitPitch);
					SaveLoadInterface.DeleatePlayerFromTablePlayers(player);
					return;
				}

			} else {
				this.getLogger().log(Level.INFO, "[The Gate] No data for this player! (Main/onPlayerJoin)");
			}
		}

	}

	void registerCommands() {
		getCommand("Gatetools").setExecutor(new GateTools());

		getCommand("TheGate").setExecutor(new TheGateCommand());
	}

	public Set<String> OnCooldown = new HashSet<String>();
	public Set<String> InTransit = new HashSet<String>();

	@EventHandler
	public void OnPlayerMove(PlayerMoveEvent e) {

		Player player = e.getPlayer();
		
		GateManager.GateInRadius(player, Globals.VisibilityRadius);

		
		if (!player.hasPermission(Perms.thegate_user_allowteleport.value()))
			return;
		if (InTransit.contains(player.getName())) {
			e.setCancelled(true);
		}
		if (OnCooldown.contains(player.getName())) {
			return;
		}
		double playerx = player.getLocation().getX();
		double playery = player.getLocation().getY();
		double playerz = player.getLocation().getZ();
		GateObject myGate = GateManager.getClosestGateTo(player.getLocation());
		
		if (myGate == null) return;
				
		if (myGate.isActive()) {
			GateObject destinationGate = GateManager.getGateWithAddress(myGate.getDiled());
			if (destinationGate != null) {
				if (destinationGate.isActive()) {
					if (myGate.getGate().getWorld().equals(player.getLocation().getWorld())) {
						double x = myGate.getGate().getX() + 0.5D;
						double y = myGate.getGate().getY() + 1.5D;
						double z = myGate.getGate().getZ() + 0.5D;

						double otherGatex = destinationGate.getGate().getX() + 0.5D;
						double otherGatey = destinationGate.getGate().getY() + 1.5D;
						double otherGatez = destinationGate.getGate().getZ() + 0.5D;
						if ((GateMath.getDistance(playerx, playery, playerz, x, y, z) < 3.0D)
								&& (myGate.isAllowTeleport())) {
							Vector vn = myGate.getFacingVector();

							double facing = myGate.getFacing();

							double facingothergate = destinationGate.getFacing();

							if ((GateMath.DistancePointPlane(new Vector(playerx, playery, playerz), new Vector(x, y, z),
									vn) < 0.2D)
									&& (GateMath.DistancePointPlane(new Vector(playerx, playery, playerz),
											new Vector(x, y, z), vn) > -0.3D)) {
								if(myGate.isUseGatePerms()) {
									if(!player.hasPermission(Perms.thegate_user_teleport_.value() + myGate.getAddress()) &&
											!player.hasPermission(Perms.thegate_admin_dialgate.value())) {
										player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
										return;
									}
								}
								
								if(destinationGate.isUseGatePerms()) {
									if(!player.hasPermission(Perms.thegate_user_teleport_.value() + destinationGate.getAddress()) &&
											!player.hasPermission(Perms.thegate_admin_dialgate.value())) {
										player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
										return;
									}
								}
								
								if(!player.hasPermission(Perms.thegate_admin_ignoreiris.value())) {
									if(destinationGate.isIrisClosed() && Globals.DeadlyIris) {
										myGate.KillPlayer(player);
									}
									if(destinationGate.isIrisClosed() || myGate.isIrisClosed()) {
										return;
									}
								}
								
								playerx -= x;
								playery -= y;
								playerz -= z;

								Vector vp = GateMath.RotateVectorY(new Vector(playerx, playery, playerz),
										Math.toRadians((-facing + 2) * 90));
								Vector vp2 = GateMath.RotateVectorY(vp, Math.toRadians((facingothergate) * 90));

								playerx = vp2.getX();
								playery = vp2.getY();
								playerz = vp2.getZ();

								playerx += otherGatex;
								playery += otherGatey;
								playerz += otherGatez;

								Location loc = new Location(this.getServer().getWorld(destinationGate.getWorldName()), playerx, playery + 0.25, playerz);

								float playeryaw = (player.getLocation().getYaw() + 360) % 360;

								loc.setYaw((float) (playeryaw + 180 + ((facingothergate - facing) * 90)));
								loc.setPitch(player.getLocation().getPitch());

								this.getServer().getWorld(myGate.getWorldName()).playSound(myGate.getGate(), Globals.DefaultGateEnterSound,
										Globals.DefaultGateEnterVolume, Globals.DefaultGateEnterPitch);
								player.teleport(loc);

								myGate.dispach(CommandUseType.OnEnter, player);

								destinationGate.dispach(CommandUseType.OnExit, player);
								
								player.setVelocity(GateMath.RotateVectorY(new Vector(0, 0, 0.3D),
										Math.toRadians((facingothergate) * 90)));

								this.getServer().getWorld(destinationGate.getWorldName()).playSound(destinationGate.getGate(),
										Globals.DefaultGateExitSound, Globals.DefaultGateExitVolume,
										Globals.DefaultGateExitPitch);

								OnCooldown.add(player.getName());
								new BukkitRunnable() {

									@Override
									public void run() {
										OnCooldown.remove(player.getName());
									}

								}.runTaskLater(this, CooldownTimer);
								return;
							}
						}
					}
				}
			} else {
				if (GateManager.hasGateOnOtherServerWithAddress(myGate.getDiled())) {
					destinationGate = GateManager.getGateOnOtherServerWithAddress(myGate.getDiled());

					if (myGate.getGate().getWorld().equals(player.getLocation().getWorld())) {
						double x = myGate.getGate().getX() + 0.5D;
						double y = myGate.getGate().getY() + 1.5D;
						double z = myGate.getGate().getZ() + 0.5D;
						if ((GateMath.getDistance(playerx, playery, playerz, x, y, z) < 3.0D)
								&& (myGate.isAllowTeleport())) {
							
							if(myGate.isUseGatePerms()) {
								if(!player.hasPermission(Perms.thegate_user_teleport_.value() + myGate.getAddress()) &&
										!player.hasPermission(Perms.thegate_admin_dialgate.value())) {
									player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
									return;
								}
							}
							
							if(destinationGate.isUseGatePerms()) {
								if(!player.hasPermission(Perms.thegate_user_teleport_.value() + destinationGate.getAddress()) &&
										!player.hasPermission(Perms.thegate_admin_dialgate.value())) {
									player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
									return;
								}
							}
							
							Vector vn = new Vector(0, 0, 1);

							double facing = myGate.getFacing();

							vn = GateMath.RotateVectorY(vn, Math.toRadians(facing * 90));
							if ((GateMath.DistancePointPlane(new Vector(playerx, playery, playerz), new Vector(x, y, z),
									vn) < 0.2D)
									&& (GateMath.DistancePointPlane(new Vector(playerx, playery, playerz),
											new Vector(x, y, z), vn) > -0.3D)) {

								long transitId = SaveLoadInterface.AddPlayerToTablePlayers(player, destinationGate.getAddress(),
										myGate.getAddress());
								if(transitId >= 0) {
									myGate.dispach(CommandUseType.OnEnter, player);
									reconnect.ReconnectPlayer(destinationGate.getServer(), e.getPlayer(), this);
								}
								OnCooldown.add(player.getName());
								InTransit.add(player.getName());
								new BukkitRunnable() {

									@Override
									public void run() {
										OnCooldown.remove(player.getName());
										InTransit.remove(player.getName());
									}

								}.runTaskLater(this, CooldownTimer);
								if(transitId >= 0) {
									// The destination server deletes the row when the player arrives. The proxy keeps the
									// player here until then, so being online is no sign the switch failed. Only drop the row
									// once it is too old to belong to a switch still in progress.
									getServer().getScheduler().runTaskLater(this, () -> SaveLoadInterface.DeleteTransitRow(transitId), TransitRowTimeout);
								}

								this.getServer().getWorld(myGate.getWorldName()).playSound(myGate.getGate(), Globals.DefaultGateEnterSound,
										Globals.DefaultGateEnterVolume, Globals.DefaultGateEnterPitch);
								
								return;
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onTeleportEvent(PlayerTeleportEvent e) {
		Set<GateObject> gate = GateManager.getGatesInWorld(e.getPlayer().getLocation().getWorld().getName());
		if(gate != null && gate.size() > 0) {
			gate.stream().forEach(x -> x.removePlayerInRange(e.getPlayer()));
		}
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		if(!e.getBlock().getType().equals(Globals.DefaultDHDMaterial)) return;
		
		if(GateManager.hasDHDatLocation(e.getBlock().getLocation()) && !e.getPlayer().hasPermission(Perms.thegate_admin_breakdhd.value())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.DHDBreak"));
		}else {
			GateObject obj = GateManager.getGateByDHD(e.getBlock().getLocation());
			if(obj == null) return;
			obj.setDHD(null);
			e.getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditGateGUI.Message10", "{ADDRESS}", obj.getAddress()));
		}
		
	}
	
//	@EventHandler
//	public void onInteract(PlayerInteractEvent e) {
//		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
//			Location l = e.getClickedBlock().getLocation();
//			GateObject o = GateManager.getClosestGateTo(l);
//
//			if(o != null) {
//				Block b = o.getBarrierBlock(l);
//				if(b != null) {
//					e.setCancelled(true);
//					PackageManager.SendBlockChangePackage(b, Material.BARRIER, e.getPlayer());
//				}
//			}
//		}
//	}
	

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(channel.equals("BungeeCord")) GateSync.onMessage(message);
	}
	
	public static String getVersion() {
		return ((Plugin)TheGateMain.theGateMain).getDescription().getVersion();
	}
	
	public static boolean isServerCompatible() {
		String serverVersion = TheGateMain.theGateMain.getServer().getMinecraftVersion();
		boolean IsSupported = false;
		for(String s : supportedVersions) {
			if(serverVersion.equals(s) || serverVersion.startsWith(s + ".")) IsSupported = true;
		}
		return IsSupported;
	}
	
}
