package thegate.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gui.tools.guitools.GUIBase;
import com.gui.tools.guitools.InventoryManager;

import thegate.animaitons.IrisAnimation;
import thegate.gate.BlockedState;
import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;
import thegate.main.TheGateMain;

public class DHD_GUI extends GUIBase{

	public GateObject gate;
	
	private String currentAddress = "";
	private int index = 0;
	//da wut now!!!!
	private String charString = "0123456789abcdef";
	private String[] list = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	private char[] charlist = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	private List<String> assistAddresses = new ArrayList<>();
	private Set<GateObject> available;
	
	public DHD_GUI(Player p, String name, GateObject gate) {
		super(p, 9 * 6, name, "DHD_GUI");
		this.gate = gate;
		
		setup();
		setupFunctions();
		CondPerms();
	}
	
	public void CondPerms() {
		addUIAccessPermission(Perms.thegate_user_dialgate.value(), Perms.thegate_admin_dialgate.value());
		additemPerms(31, Perms.thegate_user_dialgate.value(), Perms.thegate_admin_dialgate.value(), Perms.thegate_user_canceldialing.value());
		
		
		setGUIAccessCondition(c -> {
			DHD_GUI g = (DHD_GUI) c;
			if (!g.getPlayer().hasPermission(Perms.thegate_admin_dialgate.value()) && (!gate.isOpen() && !gate.getOwnerUUID().equals(g.getPlayer().getUniqueId().toString()))) {
					g.getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message1"));
					return false;
			}else if(!gate.isOpen() && !gate.getOwnerUUID().equals(g.getPlayer().getUniqueId().toString())){
				return false;
			}else {
				return true;
			}
		});
	}
	
	public void setup() {
		this.setFilterUnaccassable(false);
		
		for (int i = 9; i < 54; i++) {
			setItem(i, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		}

		setItem(0, Material.RED_STAINED_GLASS_PANE, " ", null);
		setItem(8, Material.RED_STAINED_GLASS_PANE, " ", null);
		
		if(((gate.getOwnerUUID().equals(getPlayer().getUniqueId().toString()) ||
				gate.getCoOwner().keySet().contains(getPlayer().getUniqueId().toString())) && getPlayer().hasPermission(Perms.thegate_owner_useiris.value())) || getPlayer().hasPermission(Perms.thegate_admin_useiris.value())) {
			setItem(13, Material.FIRE_CHARGE, ConfigManager.getString("GUIS.DHD_GUI.Items.Iris"), null);
		}

		setItem(20, Globals.SymbolMaterial[0], "0", null);
		setItem(21, Globals.SymbolMaterial[1], "1", null);
		setItem(22, Globals.SymbolMaterial[2], "2", null);
		setItem(23, Globals.SymbolMaterial[3], "3", null);
		setItem(24, Globals.SymbolMaterial[4], "4", null);
		setItem(29, Globals.SymbolMaterial[5], "5", null);
		setItem(30, Globals.SymbolMaterial[6], "6", null);
		if (!(gate.getBlockedState().equals(BlockedState.block_outgoing) ||
				gate.getBlockedState().equals(BlockedState.locked)) &&
				(getPlayer().hasPermission(Perms.thegate_admin_dialgate.value()) ||
						gate.isOpen() || gate.hasOwningRelation(getPlayer()))) {
			ArrayList<String> Dile = new ArrayList<String>();
			Dile.add(ConfigManager.getString("GUIS.DHD_GUI.Items.Dial.Lore"));
			setItem(31, Material.REDSTONE_BLOCK, ConfigManager.getString("GUIS.DHD_GUI.Items.Dial.Name"), Dile);
			ArrayList<String> Clear = new ArrayList<String>();
			Clear.add(ConfigManager.getString("GUIS.DHD_GUI.Items.Clear_Dial.Lore"));
			if(!gate.isDialinginProssed() && !gate.isActive()) {
				setItem(53, Material.BARRIER, ConfigManager.getString("GUIS.DHD_GUI.Items.Clear_Dial.Name"), Clear);
			}
		}
		setItem(32, Globals.SymbolMaterial[7], "7", null);
		setItem(33, Globals.SymbolMaterial[8], "8", null);
		setItem(38, Globals.SymbolMaterial[9], "9", null);
		setItem(39, Globals.SymbolMaterial[10], "a", null);
		setItem(40, Globals.SymbolMaterial[11], "b", null);
		setItem(41, Globals.SymbolMaterial[12], "c", null);
		setItem(42, Globals.SymbolMaterial[13], "d", null);
		setItem(48, Globals.SymbolMaterial[14], "e", null);
		setItem(50, Globals.SymbolMaterial[15], "f", null);

		if(gate.isDialinginProssed() || gate.isActive()) {
			char[] address = gate.getDiled().toCharArray();
			for(int i = 1; i < 8; i++) {
				for(int j = 0; j < charlist.length; j++) {
					if(charlist[j] == address[i - 1]) {
						setItem(i, Globals.SymbolMaterial[j], "" + charlist[j], null);
					}
				}
			}
		}
		if (gate.getBlockedState().equals(BlockedState.block_outgoing) ||
				gate.getBlockedState().equals(BlockedState.locked) ||
				(!gate.isOpen() &&
						!getPlayer().hasPermission(Perms.thegate_admin_dialgate.value()) &&
						!gate.isOwner(this.getPlayer()) &&
						!gate.isCoowner(this.getPlayer()))) {
			for (int i = 1; i < 8; i++) {
				setItem(i, Material.SKELETON_SKULL, ConfigManager.getString("GUIS.DHD_GUI.Items.GateLocked"), null);
			}
		}

		if (Globals.AllowQuickDial &&
				getPlayer().hasPermission(Perms.thegate_user_quickdial.value()) &&
				(!(gate.getBlockedState().equals(BlockedState.block_outgoing) ||
						gate.getBlockedState().equals(BlockedState.locked))
						&& (getPlayer().hasPermission(Perms.thegate_admin_dialgate.value())
								|| gate.isOpen() || gate.hasOwningRelation(getPlayer())))) {
			setItem(35, Material.ELYTRA, ConfigManager.getString("GUIS.DHD_GUI.Items.QuickDial"), null);
		}
		
		if(Globals.AllowDialSuggestions && getPlayer().hasPermission(Perms.thegate_user_dialassist.value())) {
			setAssistPannel();
		}
		
		available = GateManager.getGatesAsSet();
		available.addAll(GateManager.getGatesOnOtherServer());
	}
	
	public boolean ListHasString(String s) {
		for (int i = 0; i < this.list.length; i++) {
			if (s.equals(this.list[i])) {
				return true;
			}
		}
		return false;
	}
	
	private void updateAssistList(List<String> addresses, int index) {
		if(addresses == null || addresses.isEmpty()) {
			setAssistPannel();
			return;
		} 
		if(addresses.size() > 5) {
			setItem(9, Material.ARROW, "Up", null);
			setItem(45, Material.ARROW, "Down", null);
			for(int i = 0; i < 3; i++) {
				if(i+(3*index) < addresses.size()) {
					setItem(18+(i*9), Globals.SymbolMaterial[charString.indexOf(addresses.get(i).charAt(0))], addresses.get(i+(3*index)), null);	
				}else {
					setItem(18+(i*9), Material.WHITE_STAINED_GLASS_PANE, " ", null);
				}
			}
		}else {
			setAssistPannel();
			for(int i = 0; i < addresses.size(); i++) {
				setItem(9+(i*9), Globals.SymbolMaterial[charString.indexOf(addresses.get(i).charAt(0))], addresses.get(i), null);
			}
		}
	}
	
	private void setAssistPannel() {
		setItem(9, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(18, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(27, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(36, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(45, Material.WHITE_STAINED_GLASS_PANE, " ", null);
	}
	
	public void setupFunctions() {
				
		setGeneralFunction(x ->{
			if (x.index > 9 && x.index < 54 && ListHasString(x.item.getItemMeta().getDisplayName())) {
				for (int i = 1; i < 8; i++) {
					if (getItem(i) == null) {
						setItem(i, x.item);
						currentAddress += x.item.getItemMeta().getDisplayName();
						break;
					}
				}
				
				if(Globals.AllowDialSuggestions && getPlayer().hasPermission(Perms.thegate_user_dialassist.value())) {
					assistAddresses = available.stream().map(gate -> gate.getAddress()).filter(address -> address.contains(currentAddress) && !address.equalsIgnoreCase(this.gate.getAddress())).sorted().collect(Collectors.toList());
					updateAssistList(assistAddresses, 0);
				}
				refreshItems();
			}
		});
		
		addGUIFunction(9, x ->{
			if(index > 0) {
				index--;
				updateAssistList(assistAddresses, index);
				refreshItems();
			}
		}, Material.ARROW);
		
		addGUIFunction(45, x ->{
			int listSize = assistAddresses.size();
			if(listSize <= 5) return;
			int maxRange = (listSize/3) + (listSize%3 == 0 ? -1 : 0);
			if(index < maxRange) {
				index++;
				updateAssistList(assistAddresses, index);
				refreshItems();
			}
		}, Material.ARROW);
		
		//TODO: DONT DO THIS RE DO AS SOON AS POSSIBLE
		for(int i = 0; i < 5; i++) {
			for(Material m : Globals.SymbolMaterial) {
				addGUIFunction(9+(i*9), x ->{
					char[] address = x.item.getItemMeta().getDisplayName().toCharArray();
					for(int a = 0; a < 7; a++) {
						setItem(1+a, Globals.SymbolMaterial[charString.indexOf(address[a])], address[a]+"", null);
					}
					refreshItems();
				}, m);
			}
		}
		
		addGUIFunction(35, x ->{
			x.player.closeInventory();
			QuickDialGUI quickDial = new QuickDialGUI(x.player, ConfigManager.getString("GUIS.DHD_GUI.GUIName", "{ADDRESS}", gate.getAddress()), gate, this);
			if(quickDial.OpenGUI()) InventoryManager.addGUI(quickDial);
		}, Material.ELYTRA);
		
		addGUIFunction(13, x ->{
			x.player.closeInventory();
			if(gate.irisAnimaiton == null) {
				gate.setIrisAnimaiton(new IrisAnimation(gate, gate.isIrisClosed()));
				if(!Globals.DoIrisAnimaiton) gate.setIrisAnimaiton(null);
			}
		}, Material.FIRE_CHARGE);
		
		addGUIFunction(53, x ->{
			for (int i = 1; i < 8; i++) {
				setItem(i, null);
			}
			if(Globals.AllowDialSuggestions && getPlayer().hasPermission(Perms.thegate_user_dialassist.value())) {
				currentAddress = "";
				assistAddresses = null;
				updateAssistList(null, 0);
				index = 0;
				refreshItems();
			}
		}, Material.BARRIER);
		
		addGUIFunction(31, x ->{
			Player player = getPlayer();
			String Address = "";
			for (int i = 1; i < 8; i++) {
				if (getItem(i) == null) {
					return;
				} else {
					Address = Address + getItem(i).getItemMeta().getDisplayName();
				}
			}
			dialGate(Address, player);
			
		}, Material.REDSTONE_BLOCK);
	}
	
	private void dialGate(String Address, Player player) {
		if (Address.equals(gate.getAddress())) {
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message6"));
			player.closeInventory();
			return;
		}

		GateObject destinationGate = GateManager.getGateWithAddress(Address);
		
		if(Globals.UseBungee && destinationGate == null && GateManager.hasGateOnOtherServerWithAddress(Address)) {
			bungeeDial(player, gate, destinationGate, Address);
			return;
		}
		
		if (destinationGate == null) {
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message7", "{ADDRESS}", Address));
			player.closeInventory();
			return;
		}
		
		if(player.hasPermission(Perms.thegate_user_canceldialing.value()) && (gate.isActive() || gate.isDialingout()) && gate.isDialingout()) {
			gate.Deactivate();
			destinationGate.Deactivate();
			player.closeInventory();
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message8"));
			return;
		}else if(player.hasPermission(Perms.thegate_user_canceldialing.value()) && (gate.isActive() || gate.isDialingout()) && !gate.isDialingout()) {
			player.closeInventory();
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message9"));
			return;
		}else if(!player.hasPermission(Perms.thegate_user_canceldialing.value()) && (gate.isActive() || gate.isDialingout()) && gate.isDialingout()) {
			player.closeInventory();
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message9"));
			return;
		}else if(!player.hasPermission(Perms.thegate_user_canceldialing.value()) && (gate.isActive() || gate.isDialingout()) && !gate.isDialingout()) {
			player.closeInventory();
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message9"));
			return;
		}
		
		if (!player.hasPermission(Perms.thegate_admin_dialgate.value()) && !destinationGate.isOpen() && !destinationGate.hasOwningRelation(player)) {
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message1"));
			player.closeInventory();
			return;
		}

		if (gate.hasSameNetwork(destinationGate)) {
			if (gate.canDial(destinationGate, player)) {
				if(Globals.DoDialing) {
					gate.dialGate(destinationGate, player);
					player.closeInventory();
					return;
				}else {
					destinationGate.Activate(gate.getAddress(), false, player);
					gate.Activate(destinationGate.getAddress(), true, player);
					player.closeInventory();
					return;
				}
			} else {
				player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message11"));
				player.closeInventory();
				return;
			}
		} else {
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message12"));
			player.closeInventory();
		}
	}
	
	private void bungeeDial(Player player, GateObject gate, GateObject destinationGate, String Address) {
		destinationGate = GateManager.getGateOnOtherServerWithAddress(Address);
		
		if (destinationGate == null) {
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message7", "{ADDRESS}", Address));
			player.closeInventory();
			return;
		}
		
		if(player.hasPermission(Perms.thegate_user_canceldialing.value()) && (gate.isDialinginProssed() || gate.isActive()) && gate.isDialingout()) {
			gate.Deactivate();
			player.closeInventory();
			return;
		}
		
		
		if(gate.canDial(destinationGate, player)) {
			gate.StartDialingOutSequenceSingleGate(TheGateMain.theGateMain, TheGateMain.theGateMain, Address, player);
			gate.setDialinginProssed(true);
			player.closeInventory();
		}else {
			player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message11"));
			player.closeInventory();
			return;
		}
		
	}

}
