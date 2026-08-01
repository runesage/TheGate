package thegate.main;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	private Plugin mainGate;
	private static FileConfiguration TextConfig;
	public static FileConfiguration pluginConfig;
	private FileConfiguration sorceTextConfig;
	private FileConfiguration sorcepluginConfig;
	public File textconfig;
	public File pluginConfigFile;
	
	public static Map<String, String> updatedValuesConfig = new HashMap<>();
	public static Map<String, String> updatedValuesLang = new HashMap<>();

	private boolean newConfig = false;
	private boolean newLang = false;
	
	public ConfigManager(Plugin mainGate) {
		this.mainGate = mainGate;
	}

	public void CreateConfigFiles(){
		if (!this.mainGate.getDataFolder().exists()) {
			this.mainGate.getDataFolder().mkdir();
		}
		
		Reader r = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("config.yml"));
		this.sorcepluginConfig = YamlConfiguration.loadConfiguration(r);
		
		Reader r2 = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("lang.yml"));
		this.sorceTextConfig = YamlConfiguration.loadConfiguration(r2);
				
		this.textconfig = new File(this.mainGate.getDataFolder(), "lang.yml");
		this.pluginConfigFile = new File(this.mainGate.getDataFolder(), "config.yml");

		if (!this.textconfig.exists()) {
			try {
				mainGate.saveResource("lang.yml", false);
				this.textconfig = new File(this.mainGate.getDataFolder(), "lang.yml");
				newLang = true;
			} catch (Exception e) {
				TheGateMain.theGateMain.getLogger().log(Level.WARNING, e.toString());
			}
		}
		
		if (!this.pluginConfigFile.exists()) {
			try {
				mainGate.saveResource("config.yml", false);
				this.pluginConfigFile = new File(this.mainGate.getDataFolder(), "config.yml");
				newConfig = true;
			} catch (Exception e) {
				TheGateMain.theGateMain.getLogger().log(Level.WARNING, e.toString());
			}
		}
		
		TextConfig = YamlConfiguration.loadConfiguration(this.textconfig);
		pluginConfig = YamlConfiguration.loadConfiguration(this.pluginConfigFile);
	}
	
	public boolean hasUpdateConfig() {
		if(newConfig) return false;

		Set<String> s0 = sorcepluginConfig.getKeys(true);
		Set<String> s1 = pluginConfig.getKeys(true);
				
		s1.forEach(x -> s0.remove(x));
		s0.stream().filter(x -> !sorcepluginConfig.getString(x).contains("MemorySection")).forEach(x -> updatedValuesConfig.put(x, sorcepluginConfig.getString(x)));
		return !s0.isEmpty();
	}
	
	public boolean hasUpdateLang() {
		if(newLang) return false;
		
		Set<String> s0 = sorceTextConfig.getKeys(true);
		Set<String> s1 = TextConfig.getKeys(true);
		
		s1.forEach(x -> s0.remove(x));
		s0.stream().filter(x -> !sorceTextConfig.getString(x).contains("MemorySection")).forEach(x -> updatedValuesLang.put(x, sorceTextConfig.getString(x)));
		
		return !s0.isEmpty();
	}
	
	
	public static String getString(String Path, String... replace) {
		if(replace.length % 2 != 0) throw new IllegalArgumentException("Not enough arguments!");
		String out = TextConfig.getString(Path);
		if(out == null || out == "") return "[null]";
		out = out.replace("&", "§");
		for(int i = 0; i < replace.length; i += 2) {
			out = out.replace(replace[i], replace[i+1]);
		}
		return out;
	}
	
	public static List<String> getStringList(String Path, String... replace) {
		List<String> l = new ArrayList<>();
		l.add("[Null]");
		return getStringList(Path, l, replace);
	}
	
	public static List<String> getStringList(String Path, List<String> returnValue, String... replace) {
		if(replace.length % 2 != 0) throw new IllegalArgumentException("Not enough arguments!");
		List<String> out = TextConfig.getStringList(Path);
		if(out == null || out.isEmpty()) return returnValue;
		return out.stream().map(x -> {
			String b = x.replace("&", "§");
			for(int i = 0; i < replace.length; i += 2) {
				b = b.replace(replace[i], replace[i+1]);
			}
			return b;
			}).collect(Collectors.toList());
	}
	
	public static FileConfiguration getConfigFile() {
		return pluginConfig;
	}
	
}
