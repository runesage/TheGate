package thegate.main;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {

	public static void LoadConfig(Plugin mainGate) {
		mainGate.getConfig().options().copyDefaults(true);
		mainGate.saveDefaultConfig();

		FileConfiguration config = mainGate.getConfig();
		
		Material DRM = Material.getMaterial(config.getString("GateMaterial.Default-Ring"));
		Material DCFM = Material.getMaterial(config.getString("GateMaterial.Default-Chevrons-Frame-Material"));
		Material DCBM = Material.getMaterial(config.getString("GateMaterial.Default-Chevron-Bottom-Material"));
		Material DCLM = Material.getMaterial(config.getString("GateMaterial.Default-Chevron-Light-Material-Off"));
		Material DCLMO = Material.getMaterial(config.getString("GateMaterial.Default-Chevron-Light-Material-On"));
		Material DHM = Material.getMaterial(config.getString("GateMaterial.Default-Horizon-Material"));
		Material DDHDM = Material.getMaterial(config.getString("GateMaterial.Default-DHD-Material"));

		Globals.DefaultIrisMaterial = Material.getMaterial(config.getString("GateMaterial.Default-Iris-Material"));
				
		if(DRM != null) {
			Globals.DefaultringMaterial = DRM;
		}else {
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Default ring material created an error!");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Check the spelling in the config");
		}
		
		if(DCFM != null) {
			Globals.Defaultchevrons_frameMaterial = DCFM;
		}else {
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Default chevron frame material created an error!");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Check the spelling in the config");
		}
		
		if(DCBM != null) {
			Globals.Defaultchevron_botMaterial = DCBM;
		}else {
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Default chevron bottom material created an error!");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Check the spelling in the config");
		}
		
		if(DCLM != null) {
			Globals.Defaultchevron_lightMaterial = DCLM;
		}else {
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Default chevron light material created an error!");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Check the spelling in the config");
		}
		
		if(DCLMO != null) {
			Globals.Defaultchevron_lightMaterial_ON = DCLMO;
		}else {
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Default chevron light on material created an error!");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Check the spelling in the config");
		}
		
		if(DHM != null) {
			Globals.DefaulthorizonMaterial = DHM;
		}else {
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Default horizon material created an error!");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Check the spelling in the config");
		}
		
		if(DDHDM != null) {
			Globals.DefaultDHDMaterial = DDHDM;
		}else {
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Default DHD material created an error!");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "<The_Gate> Check the spelling in the config");
		}
		int t = config.getInt("GateSettings.AnimationSettings.ActiveTime");
		Globals.GateTime = (t > 45600  ? 45600 : t);
		Globals.CreateBarrier = config.getBoolean("GateSettings.PlaceBarrier");
		
		Globals.DefaultGateCreationTool = Material.getMaterial(config.getString("ToolCustomization.Default-GateCreationTool"));
		Globals.DefaultGateEditTool = Material.getMaterial(config.getString("ToolCustomization.Default-GateEditTool"));
		Globals.DefaultAbydosCartouche = Material.getMaterial(config.getString("ToolCustomization.Default-AbydosCartouche"));
		Globals.DefaultGateCrystal = Material.getMaterial(config.getString("ToolCustomization.Default-GateCrystal"));
		Globals.DefaultIDCTransmitter = Material.getMaterial(config.getString("ToolCustomization.Default-IDCTrancmitter"));

		
		Globals.AllowQuickDial = config.getBoolean("GateSettings.AllowQuickDial");
		Globals.PlayerGateAmmount = config.getInt("GateSettings.UserOwnedGates");
		Globals.GateExclusionRadius = config.getInt("GateSettings.GateExclusionRadius");
		
		Globals.DefaultGateActivateSound = parseSound(mainGate, config, "GateSoundSettings.Gate-Activate-Sound", Sound.ENTITY_EVOKER_CAST_SPELL);
		Globals.DefaultGateActivateVolume = (float) config.getDouble("GateSoundSettings.Gate-Activate-Volume");
		Globals.DefaultGateActivatePitch = (float) config.getDouble("GateSoundSettings.Gate-Activate-Pitch");
		  
		Globals.DefaultGateAmbientSound = parseSound(mainGate, config, "GateSoundSettings.Gate-Ambient-Sound", Sound.BLOCK_WATER_AMBIENT);
		Globals.DefaultGateAmbientVolume = (float) config.getDouble("GateSoundSettings.Gate-Ambient-Volume");
		Globals.DefaultGateAmbientePitch = (float) config.getDouble("GateSoundSettings.Gate-Ambient-Pitch");
		  
		Globals.DefaultChevronLockSound = parseSound(mainGate, config, "GateSoundSettings.Gate-ChevronLock-Sound", Sound.BLOCK_PISTON_CONTRACT);
		Globals.DefaultChevronLockVolume = (float) config.getDouble("GateSoundSettings.Gate-ChevronLock-Volume");
		Globals.DefaultChevronLockPitch = (float) config.getDouble("GateSoundSettings.Gate-ChevronLock-Pitch");
		  
		Globals.DefaultChevronOpenSound = parseSound(mainGate, config, "GateSoundSettings.Gate-ChevronOpen-Sound", Sound.BLOCK_PISTON_EXTEND);
		Globals.DefaultChevronOpenVolume = (float) config.getDouble("GateSoundSettings.Gate-ChevronOpen-Volume");
		Globals.DefaultChevronOpenPitch = (float) config.getDouble("GateSoundSettings.Gate-ChevronOpen-Pitch");
		  
		Globals.DefaultSpinningSound = parseSound(mainGate, config, "GateSoundSettings.Gate-DialingAmbient-Sound", Sound.ENTITY_MINECART_RIDING);
		Globals.DefaultSpinningVolume = (float) config.getDouble("GateSoundSettings.Gate-DialingAmbient-Volume");
		Globals.DefaultSpinningPitch = (float) config.getDouble("GateSoundSettings.Gate-DialingAmbient-Pitch");
		  
		Globals.DefaultGateEnterSound = parseSound(mainGate, config, "GateSoundSettings.Gate-Enter-Sound", Sound.AMBIENT_UNDERWATER_ENTER);
		Globals.DefaultGateEnterVolume = (float) config.getDouble("GateSoundSettings.Gate-Enter-Volume");
		Globals.DefaultGateEnterPitch = (float) config.getDouble("GateSoundSettings.Gate-Enter-Pitch");
		  
		Globals.DefaultGateExitSound = parseSound(mainGate, config, "GateSoundSettings.Gate-Exit-Sound", Sound.AMBIENT_UNDERWATER_EXIT);
		Globals.DefaultGateExitVolume = (float) config.getDouble("GateSoundSettings.Gate-Exit-Volume");
		Globals.DefaultGateExitPitch = (float) config.getDouble("GateSoundSettings.Gate-Exit-Pitch");

		Globals.DialingSpeed = (float) config.getDouble("GateSettings.AnimationSettings.DialingSpeed");
		
		List<String> SymbolMaterial = config.getStringList("GateMaterial.GateSymbolMaterial");
		
		for(int i = 0; i < 16; i++) {
			Globals.SymbolMaterial[i] = Material.valueOf(SymbolMaterial.get(i));
		}
		
		Globals.DisplayName = config.getBoolean("GateSettings.DisplayName");
		
		Globals.Networks.clear();
		Globals.Networks.add("Global");
		List<String> Networks = ((TheGateMain) mainGate).getConfig().getStringList("GateSettings.Networks");
		
		List<String> CustomWorldNames = ((TheGateMain) mainGate).getConfig().getStringList("GateSettings.WorldsAsNetworks");
		
		for(String s : CustomWorldNames) {
			if(mainGate.getServer().getWorld(s.split(":")[1]) != null) {
				Globals.WorldNames.put(s.split(":")[1], s.split(":")[0]);
				Globals.WorldDefaultSpawn.put(s.split(":")[1], Boolean.valueOf(s.split(":")[2]));
				Globals.Networks.add(s.split(":")[0]);
			}
		}
		
		for(int i = 0; i < Networks.size(); i++) {
			Globals.Networks.add(Networks.get(i));
		}
		
		Globals.SaveFromat = config.getString("PluginSettings.Savefile");
		if(Globals.SaveFromat.equals("MYSQL")) {
			Globals.MySQLPath = config.getString("PluginSettings.Path");
			Globals.MySQLUserName = config.getString("PluginSettings.Username");
			Globals.MySQLUserPassword = config.getString("PluginSettings.Password");
			Globals.AutoSyncDatabase = config.getInt("PluginSettings.AutoSyncDatabase");
			
			Globals.UseBungee = config.getBoolean("BungeeSettings.UseBungee");
			Globals.ServerName = config.getString("BungeeSettings.ServerName");
		}
		
		Globals.VisibilityRadius = Integer.valueOf(config.getString("GateSettings.GateVisibilityRadius"));
		Globals.DeadlyVortex = config.getBoolean("GateSettings.DeadlyVortex");
		Globals.DeadlyIris = config.getBoolean("GateSettings.DeadlyIris");
		Globals.GateCanBreakBlocks = config.getBoolean("GateSettings.GateCanBreakBlocks");
	
		List<String> mat = config.getStringList("GateSettings.DistrucitonSettings.ExcludedBlocks");
		Set<Material> excludeMat = new HashSet<>();
		for(String s : mat) {
			excludeMat.add(Material.valueOf(s));
		}
		Globals.excludeList = excludeMat;
		Globals.SelectionX = config.getInt("GateSettings.DestrucitonSettings.SelectionX");
		Globals.Selection_X = config.getInt("GateSettings.DestrucitonSettings.Selection-X");
		Globals.SelectionY = config.getInt("GateSettings.DestrucitonSettings.SelectionY");
		Globals.Selection_Y = config.getInt("GateSettings.DestrucitonSettings.Selection-Y");
		Globals.Radius = config.getDouble("GateSettings.DestrucitonSettings.Radius");
		Globals.DestructionDistance = config.getDouble("GateSettings.DestrucitonSettings.Distance");
		Globals.DestructionDistanceMult = config.getDouble("GateSettings.DestrucitonSettings.DistanceMult");
		
		Globals.MoreInfo = config.getBoolean("PluginSettings.ExtendedPlguinInfo");
		
		Globals.DialingAnimationTicks = config.getInt("GateSettings.AnimationSettings.DialingAnimationTicks");
		Globals.IrisAnimaitonTicks = config.getInt("GateSettings.AnimationSettings.IrisAnimaitonTicks");
		Globals.IrisSpeed = config.getInt("GateSettings.AnimationSettings.IrisSpeed");
		Globals.VortexSpeed = config.getInt("GateSettings.AnimationSettings.VortexSpeed");
		Globals.DoDialing = config.getBoolean("GateSettings.AnimationSettings.DoDialing");
		Globals.DoVortex = config.getBoolean("GateSettings.AnimationSettings.DoVortex");
		Globals.DoHorizonEffect = config.getBoolean("GateSettings.AnimationSettings.DoHorizonEffect");
		Globals.DoIrisAnimaiton = config.getBoolean("GateSettings.AnimationSettings.DoIrisAnimation");
		
		Globals.UseDummyOwner = config.getBoolean("GateSettings.UseDummyOwner");
		Globals.dummyOwnerName = config.getString("GateSettings.DummyOwnerName");
		
		Globals.playerTable = config.getString("PluginSettings.TablePlayer");
		Globals.coownerTable = config.getString("PluginSettings.TableCoOwner");
		Globals.gatesTable = config.getString("PluginSettings.TableGates");
		Globals.gatesCommandsTable = config.getString("PluginSettings.GateCommands");
		Globals.AllowDialSuggestions = config.getBoolean("GateSettings.AllowDialSuggestions");
	}
	

	/**
	 * Reads a sound by its old enum-style name (BLOCK_PISTON_EXTEND) or its
	 * namespaced key (minecraft:block.piston.extend).
	 */
	private static Sound parseSound(Plugin plugin, FileConfiguration config, String path, Sound fallback) {
		String value = config.getString(path);
		if(value != null) {
			if(value.contains(":") || value.contains(".")) {
				NamespacedKey key = NamespacedKey.fromString(value.toLowerCase(Locale.ROOT));
				Sound sound = key == null ? null : Registry.SOUND_EVENT.get(key);
				if(sound != null) return sound;
			} else {
				try {
					if(Sound.class.getField(value.toUpperCase(Locale.ROOT)).get(null) instanceof Sound sound) return sound;
				} catch (ReflectiveOperationException e) {
					// Not a known sound name, fall through to the warning.
				}
			}
		}
		plugin.getLogger().log(Level.WARNING, "Unknown sound '" + value + "' for " + path + ", using the default sound instead.");
		return fallback;
	}

}
