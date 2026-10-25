package thegate.gate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.packageing.tools.packagetools.PackageManager;

import thegate.animaitons.AnimationDialing;
import thegate.animaitons.AnimationDialingSingleGate;
import thegate.animaitons.IrisAnimation;
import thegate.animaitons.WooshAnimation;
import thegate.gate.packages.GatePackages;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;
import thegate.main.TheGateMain;
import thegate.main.save.SqlStatement;
import thegate.math.GateMath;

public class GateObject{

	private boolean updated = false;
	private boolean shutdown = false;
	private boolean dialingout = false;
	private boolean Active = false;
	private boolean AllowTeleport = false;
	private BlockedState Locked = BlockedState.unlocked;
	private boolean Open = true;
	private boolean WormholeOutgoing = false;
	private boolean IrisClosed = false;
	private boolean commandOnDeactivate = false;
	private boolean useGatePerms = false;
	private int acitveTime = Globals.GateTime;
	private String IrisCode = "0000000";
	
	
	private boolean dialinginProssed = false;

	private String OwnerUUID = null;
	private String OwnerName = "";

	private Location Gate, DHD;
	private int ChunkX = 0, ChunkZ = 0;

	private String address = "";
	private String Server = "none";
	private String Network = Globals.Networks.get(0);
	private String SecundaryNetwork = "null";
	private String WorldName = "";
	
	private String Diled = "";
	private Player lastActivatedBy = null;
	
	private String description = "";
	private String gateName = "";
	private ArrayList<String> DisplayText = new ArrayList<String>();
	
	private Material ringMaterial = Globals.DefaultringMaterial,
			chevrons_frameMaterial = Globals.Defaultchevrons_frameMaterial,
			chevron_botMaterial = Globals.Defaultchevron_botMaterial,
			chevron_lightMaterial = Globals.Defaultchevron_lightMaterial,
			chevron_lightMaterial_on = Globals.Defaultchevron_lightMaterial_ON,
			horizonMaterial = Globals.DefaulthorizonMaterial,
			irisMaterial = Globals.DefaultIrisMaterial;

	private float facing = 0;
	private Vector facingVector;

	private Map<String, String> coOwner = new HashMap<>();
	private Set<Player> PlayerInRange = new HashSet<Player>();
	private Set<Player> JoinedPlayers = new HashSet<Player>();
	private Map<Location, Block> barrierBlocks = new HashMap<>();
	private Map<CommandUseType, List<GateCommandInfo>> gateCommands = new HashMap<>();
	
	public AnimationDialing dialing = null;
	public AnimationDialingSingleGate dialingSingle = null;
	public WooshAnimation woosh = null;
	public IrisAnimation irisAnimaiton = null;
	private GatePackages packages;
	
	public GateObject() {
		this.Server = Globals.ServerName;
		packages = new GatePackages(this);
	}

	public GateObject(Location Gate, String WorldName, Location DHD, float facing, String address, String OwnerID, String OwnerName) {
		this.Gate = Gate;
		this.ChunkX = Gate.getChunk().getX();
		this.ChunkZ = Gate.getChunk().getZ();
		this.WorldName = WorldName;
		this.DHD = DHD;
		this.address = address;
		this.facing = facing;
		this.OwnerUUID = OwnerID;
		this.OwnerName = OwnerName;
		if(Globals.WorldDefaultSpawn.containsKey(Gate.getWorld().getName())) {
			if(!Globals.WorldDefaultSpawn.get(Gate.getWorld().getName())) {	
				this.Network = Globals.Networks.get(0);
			}else {
				this.Network = Globals.WorldNames.get(Gate.getWorld().getName());
			}
		}else {
			this.Network = Globals.Networks.get(0);
		}
		
		this.Server = Globals.ServerName;

		packages = new GatePackages(this);
	}

	public GateObject(Location Gate, String WorldName, float facing, String Address, String OwnerID, String OwnerName) {
		this.Gate = Gate;
		this.ChunkX = Gate.getChunk().getX();
		this.ChunkZ = Gate.getChunk().getZ();
		this.WorldName = WorldName;
		this.facing = facing;
		this.address = Address;
		this.OwnerUUID = OwnerID;
		this.OwnerName = OwnerName;
		if(Globals.WorldDefaultSpawn.containsKey(Gate.getWorld().getName())) {
			if(!Globals.WorldDefaultSpawn.get(Gate.getWorld().getName())) {	
				this.Network = Globals.Networks.get(0);
			}else {
				this.Network = Globals.WorldNames.get(Gate.getWorld().getName());
			}
		}else {
			this.Network = Globals.Networks.get(0);
		}
		
		this.Server = Globals.ServerName;
		packages = new GatePackages(this);
	}

	
	public void Repair() {
		Deactivate();
		
		Vanish();

		CreateGate.CreateGateRing(this);
		CreateGate.CreateSymbols(this);
		CreateGate.CreateEventHorizon(this);
		if(Globals.DisplayName) CreateGate.CreateDisplayTextStands(this);
		for(Player p : this.PlayerInRange) {
			Display(p);
		}
	}
	
	public void Refresh() {
		Repair();
		
		packages.Refresh();
	}
	
	private void Display(Player p) {
		if(packages.Symbol == null || packages.Chevrons == null || packages.Ring == null) {
			Repair();
		}
		packages.Display(p);
	}
	
	public void StartDialingOutSequenceSingleGate(Plugin p, TheGateMain mainGate, String Address, Player player) {
		this.setDialingout(true);
		setDiled(Address);

		GateObject otherGate = GateManager.getGateOnOtherServerWithAddress(Address);
		if (otherGate != null) {
			dialingSingle = new AnimationDialingSingleGate(this, p, Address, player);
		} else {
			TheGateMain.theGateMain.getLogger().log(Level.WARNING, "[The Gate] nullpointer avoided (gateobject == null in dialing sequence)");
			TheGateMain.theGateMain.getLogger().log(Level.WARNING, "[The Gate] player diald from: " + this.address);
			TheGateMain.theGateMain.getLogger().log(Level.WARNING, "[The Gate] address of null gate: " + Address);
		}
	}
	
	public void StartDialingOutSequence(String Address, Player player) {
		this.setDialingout(true);
		setDiled(Address);

		GateObject otherGate = GateManager.getGateWithAddress(Address);
		if (otherGate != null) {
			if(packages.Symbol == null || packages.Chevrons == null || packages.Ring == null) {
				Repair();
				this.setDialingout(true);
				this.setDiled(Address);
				this.setDialinginProssed(true);
			}
			if(otherGate.packages.Symbol == null || otherGate.packages.Chevrons == null || otherGate.packages.Ring == null) {
				otherGate.Repair();
				otherGate.setDialingout(false);
				otherGate.setDiled(this.address);
				otherGate.setDialinginProssed(true);
			}
			dialing = new AnimationDialing(this, otherGate, TheGateMain.theGateMain, Address, player);
		} else {
			TheGateMain.theGateMain.getLogger().log(Level.WARNING, "[The Gate] nullpointer avoided (gateobject == null in dialing sequence)");
			TheGateMain.theGateMain.getLogger().log(Level.WARNING, "[The Gate] player diald from: " + this.address);
			TheGateMain.theGateMain.getLogger().log(Level.WARNING, "[The Gate] address of null gate: " + Address);
		}
	}
	
	public void Vanish(Player... p) {
		if(p.length == 0 || p == null) {
			packages.getEntityIDs().stream().forEach(x -> {
				for(Player player : this.PlayerInRange) {
					PackageManager.SendDespawnPackage(x, player);
				}
			});
			return;
		}
		
		packages.getEntityIDs().stream().forEach(x -> {
			for(Player player : p) {
				PackageManager.SendDespawnPackage(x, player);
			}
		});
	}
	
	public void KillPlayer(Player p) {
		this.removePlayerInRange(p);
		p.setHealth(0);
	}
	
	public boolean isActive() {
		return Active;
	}
	
	public void Activate(String Address, boolean AllowTeleport, Player player) {
		this.lastActivatedBy = player;
		if(packages.Symbol == null || packages.Chevrons == null || packages.Ring == null) {
			Repair();
		}
		
		if (!Active && woosh == null) {
			this.setActive(false);
			this.setAllowTeleport(AllowTeleport);
			this.setDiled(Address);
			this.setDialinginProssed(true);
			this.setDialingout(AllowTeleport);
			
			woosh = new WooshAnimation(this, TheGateMain.theGateMain);
			if(!packages.Chevrons.get(0).On) {
				for(GateChevron c : this.packages.Chevrons) {
					c.On = true;
					c.UpdateLight();
				}
			}
			if(this.dialingout) TheGateMain.theGateMain.getServer().getScheduler().runTask(TheGateMain.theGateMain, () -> dispach(CommandUseType.OnActivate, lastActivatedBy));
			this.commandOnDeactivate = true;
		}
	}
	
	public void Deactivate() {
		if(commandOnDeactivate && this.dialingout) TheGateMain.theGateMain.getServer().getScheduler().runTask(TheGateMain.theGateMain, () -> dispach(CommandUseType.OnDeactivate, lastActivatedBy));
		
		commandOnDeactivate = false;
		this.lastActivatedBy = null;
		
		this.setActive(false);
		this.setDiled("");
		this.setDialingout(false);
		this.setDialinginProssed(false);
		this.setDialingout(false);
		
		this.JoinedPlayers.clear();
		
		if(woosh != null) {
			woosh.cancel();
			woosh.Remove();
			woosh = null;
		} 
		if(dialing != null) {
			dialing.Stop();
			dialing = null;
		}
		if(dialingSingle != null) {
			dialingSingle.Stop();
			dialingSingle = null;
		}
	}
	
	public void dialGate(GateObject other, Player player) {
		this.StartDialingOutSequence(other.getAddress(), player);
		this.setDiled(other.getAddress());
		other.setDiled(this.getAddress());
		this.setDialinginProssed(true);
		other.setDialinginProssed(true);
	}
	
	public void addPlayerInRange(Player p) {
		if(!this.PlayerInRange.contains(p)) {
			this.PlayerInRange.add(p);
			Display(p);
		}
	}
	
	public void removePlayerInRange(Player p) {
		if(this.PlayerInRange.contains(p)) {
			this.PlayerInRange.remove(p);
			Vanish(p);
		}
	}
	
	public void removePlayerInRangeDisconnect(Player p) {
		if(this.PlayerInRange.contains(p)) {
			this.PlayerInRange.remove(p);
		}
	}

	public SqlStatement getInsertStatement() {
		String querry = "INSERT INTO "+ Globals.gatesTable +"(address, world, server_name, locx, locy, locz, chunkx, chunkz, has_dhd, dhdx, dhdy, dhdz, facing, dialingdisabled, open, ring, chevron_bot_material, chevron_light_material_off, chevron_light_material_on, chevron_frame_material, horizon_material, uuid, player_name, gate_name, description, primary_network, secundary_network, iriscode, irismaterial)"
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		return new SqlStatement(querry,
				getAddress(),
				getWorldName(),
				getServerNameForDatabase(),
				getGate().getX(),
				getGate().getY(),
				getGate().getZ(),
				getChunkX(),
				getChunkZ(),
				hasDHD(),
				(hasDHD() ? getDHD().getX() : 0),
				(hasDHD() ? getDHD().getY() : 0),
				(hasDHD() ? getDHD().getZ() : 0),
				getFacing(),
				getBlockedState().ordinal(),
				isOpen(),
				String.valueOf(getRingMaterial()),
				String.valueOf(getChevron_botMaterial()),
				String.valueOf(getChevron_lightMaterial()),
				String.valueOf(getChevron_lightMaterial_on()),
				String.valueOf(getChevrons_frameMaterial()),
				String.valueOf(getHorizonMaterial()),
				getOwnerUUID(),
				getOwnerName().toString(),
				getGateName(),
				getDescription(),
				getNetwork(),
				getSecondaryNetwork(),
				getIrisCode(),
				String.valueOf(getIrisMaterial()));
	}
	
	public SqlStatement getUpdateStatement() {
		String querry = "UPDATE " + Globals.gatesTable + " SET world=?, server_name=?, locx=?, locy=?, locz=?, chunkx=?, chunkz=?"
				+ ", has_dhd=?, dhdx=?, dhdy=?, dhdz=?, facing=?, dialingdisabled=?, open=?"
				+ ", ring=?, chevron_bot_material=?, chevron_light_material_off=?, chevron_light_material_on=?, chevron_frame_material=?, horizon_material=?"
				+ ", uuid=?, player_name=?, gate_name=?, description=?, primary_network=?, secundary_network=?, iriscode=?, irismaterial=?, usegateperm=?"
				+ " WHERE address=?;";
		return new SqlStatement(querry,
				getWorldName(),
				getServerNameForDatabase(),
				getGate().getX(),
				getGate().getY(),
				getGate().getZ(),
				getChunkX(),
				getChunkZ(),
				hasDHD(),
				(hasDHD() ? getDHD().getX() : 0),
				(hasDHD() ? getDHD().getY() : 0),
				(hasDHD() ? getDHD().getZ() : 0),
				getFacing(),
				getBlockedState().ordinal(),
				isOpen(),
				String.valueOf(getRingMaterial()),
				String.valueOf(getChevron_botMaterial()),
				String.valueOf(getChevron_lightMaterial()),
				String.valueOf(getChevron_lightMaterial_on()),
				String.valueOf(getChevrons_frameMaterial()),
				String.valueOf(getHorizonMaterial()),
				getOwnerUUID(),
				getOwnerName().toString(),
				getGateName(),
				getDescription(),
				getNetwork(),
				getSecondaryNetwork(),
				getIrisCode(),
				String.valueOf(getIrisMaterial()),
				isUseGatePerms(),
				getAddress());
	}
	
	private String getServerNameForDatabase() {
		return getServer() == null || getServer().isEmpty() ? "none" : getServer();
	}
	
	public void addCoOwner(String uuid, String name) {
		this.coOwner.put(uuid, name);
	}
	
	public boolean addCommand(CommandUseType type, String command, CommandUser user) {
		//TODO: do checks if command can be added the way it is
		List<GateCommandInfo> list = this.gateCommands.get(type);
		if(list == null) 
			list = new ArrayList<>();
		list.add(new GateCommandInfo(command, user, this));
		this.gateCommands.put(type, list);
		return true;
	}
	
	public GateCommandInfo removeCommand(CommandUseType type, int index) {
		List<GateCommandInfo> list = this.gateCommands.get(type);
		if(list == null || list.isEmpty()) return null;
		if(index > list.size()-1 || index < 0) return null;
		GateCommandInfo obj = list.get(index);
		if(obj == null) return null;
		list.remove(obj);
		gateCommands.put(type, list);
		return obj;
	}
	
	public void dispach(CommandUseType type, CommandSender sender) {
		List<GateCommandInfo> list = this.gateCommands.get(type);
		if(list == null) return;
		list.forEach(x -> x.dispach(sender));
	}
	
	public boolean canDial(GateObject other, Player whoDialed) {
		
		/*
		 * -player has permissions
		 * 		-user.dial | admin.dial
		 * 		-if gate uses gate perms (from/to) <- admin.dial
		 * 
		 * - from allready active
		 * - to allready active
		 * - dialing in progress from and to
		 * - networks
		 * - blocked
		 * - private
		 * 
		 * */
		
		if(other.isUseGatePerms()) {
			if(!whoDialed.hasPermission(Perms.thegate_user_dial_.value() + other.getAddress()) &&
					!whoDialed.hasPermission(Perms.thegate_admin_dialgate.value())) {
				whoDialed.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
				return false;
			}
		}
		
		if(this.isUseGatePerms()) {
			if(!whoDialed.hasPermission(Perms.thegate_user_dial_.value() + this.getAddress()) &&
					!whoDialed.hasPermission(Perms.thegate_admin_dialgate.value())) {
				whoDialed.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
				return false;
			}
		}
		
		if(!other.isOpen() && !other.hasOwningRelation(whoDialed) && !whoDialed.hasPermission(Perms.thegate_admin_dialgate.value())) {
			return false;
		}
		
		return this.hasSameNetwork(other) && this.checkActivity(other) && this.checkBlockedState(other);
	}
	
	public boolean hasSameNetwork(GateObject other) {
		return this.getNetwork().equals(other.getNetwork())
				|| (this.getNetwork().equals(other.getSecondaryNetwork())
						|| this.getSecondaryNetwork().equals(other.getNetwork())); 
	}
	
	public boolean checkBlockedState(GateObject other) {
		return !(other.getBlockedState().equals(BlockedState.block_incoming) ||
				other.getBlockedState().equals(BlockedState.locked))
				&& !(this.getBlockedState().equals(BlockedState.block_outgoing) ||
						this.getBlockedState().equals(BlockedState.locked));
	}
	
	public boolean checkActivity(GateObject other) {
		return (!other.isActive() && !this.isActive()) && (!other.isDialinginProssed()
				&& !this.isDialinginProssed());
	}

	public boolean isOwner(Player p) {
		return p.getUniqueId().toString().equals(this.OwnerUUID);
	}
	
	public boolean isCoowner(Player p) {
		return this.getCoOwner().get(p.getUniqueId().toString()) != null;
	}
	
	public boolean hasOwningRelation(Player p) {
		return isOwner(p) || isCoowner(p);
	}
	
	public void setActive(boolean acitve) {
		Active = acitve;
	}

	public Location getGate() {
		return Gate;
	}

	public void setGate(Location gate) {
		Gate = gate;
	}

	public Location getDHD() {
		return DHD;
	}

	public void setDHD(Location dHD) {
		DHD = dHD;
	}

	public boolean hasDHD() {
		if (this.DHD == null) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String toString() {
		return "{Gate: " + address + "; Name: [" + this.gateName + "]; OwnerID: " + this.OwnerUUID + " OwnerName: " + this.OwnerName + "; Network: " + getNetwork() + "; SecondaryNetwork: " + getSecondaryNetwork() + "}";
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDiled() {
		return Diled;
	}

	public void setDiled(String diled) {
		Diled = diled;
	}

	public float getFacing() {
		return facing;
	}

	public void setFacing(float facing) {
		this.facing = facing;
	}

	public boolean isAllowTeleport() {
		return AllowTeleport;
	}

	public void setAllowTeleport(boolean allowTeleport) {
		AllowTeleport = allowTeleport;
	}

	public BlockedState getBlockedState() {
		return Locked;
	}

	public void setBlockedState(BlockedState state) {
		Locked = state;
	}

	public Material getRingMaterial() {
		return ringMaterial;
	}

	public void setRingMaterial(Material ringMaterial) {
		this.ringMaterial = ringMaterial;
	}

	public Material getChevrons_frameMaterial() {
		return chevrons_frameMaterial;
	}

	public void setChevrons_frameMaterial(Material chevrons_frameMaterial) {
		this.chevrons_frameMaterial = chevrons_frameMaterial;
	}

	public Material getChevron_botMaterial() {
		return chevron_botMaterial;
	}

	public void setChevron_botMaterial(Material chevron_botMaterial) {
		this.chevron_botMaterial = chevron_botMaterial;
	}

	public Material getChevron_lightMaterial() {
		return chevron_lightMaterial;
	}

	public void setChevron_lightMaterial(Material chevron_lightMaterial) {
		this.chevron_lightMaterial = chevron_lightMaterial;
	}

	public Material getHorizonMaterial() {
		return horizonMaterial;
	}

	public void setHorizonMaterial(Material horizonMaterial) {
		this.horizonMaterial = horizonMaterial;
	}

	public Material getChevron_lightMaterial_on() {
		return chevron_lightMaterial_on;
	}

	public void setChevron_lightMaterial_on(Material chevron_lightMaterial_on) {
		this.chevron_lightMaterial_on = chevron_lightMaterial_on;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	public int getAcitveTime() {
		return acitveTime;
	}

	public void setAcitveTime(int acitveTime) {
		this.acitveTime = acitveTime;
	}

	public boolean isWormholeOutgoing() {
		return WormholeOutgoing;
	}

	public void setWormholeOutgoing(boolean wormholeOutgoing) {
		WormholeOutgoing = wormholeOutgoing;
	}

	public String getOwnerUUID() {
		return OwnerUUID;
	}

	public void setOwnerUUID(String ownerUUID) {
		OwnerUUID = ownerUUID;
	}

	public String getOwnerName() {
		return OwnerName;
	}

	public void setOwnerName(String ownerName) {
		OwnerName = ownerName;
	}

	public String getNetwork() {
		return Network;
	}

	public void setNetwork(String network) {
		Network = network;
	}

	public boolean isOpen() {
		return Open;
	}

	public void setOpen(boolean open) {
		Open = open;
	}

	public String getSecondaryNetwork() {
		return SecundaryNetwork;
	}

	public void setSecundaryNetwork(String secundaryNetwork) {
		SecundaryNetwork = secundaryNetwork;
	}

	public int getChunkX() {
		return ChunkX;
	}

	public void setChunkX(int chunkX) {
		ChunkX = chunkX;
	}

	public int getChunkZ() {
		return ChunkZ;
	}

	public void setChunkZ(int chunkZ) {
		ChunkZ = chunkZ;
	}

	public boolean isDialinginProssed() {
		return dialinginProssed;
	}

	public void setDialinginProssed(boolean dialinginProssed) {
		this.dialinginProssed = dialinginProssed;
	}

	public boolean isDialingout() {
		return dialingout;
	}

	public void setDialingout(boolean dialingout) {
		this.dialingout = dialingout;
	}

	public String getServer() {
		return Server;
	}

	public void setServer(String server) {
		Server = server;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}

	public Map<String, String> getCoOwner() {
		return coOwner;
	}

	public void setCoOwner(Map<String, String> coOwner) {
		this.coOwner = coOwner;
	}
	
	public void removeCoOwner(String uuid) {
		this.coOwner.remove(uuid);
	}

	public Set<Player> getPlayerInRange() {
		return PlayerInRange;
	}

	public Vector getFacingVector() {
		if(facingVector == null) {
			facingVector = GateMath.getFacingVector(new Vector(0,0,1), Math.toRadians(this.facing * 90)).normalize();
		}
		return facingVector;
	}

	public void setFacingVector(Vector facingVector) {
		this.facingVector = facingVector;
	}

	public String getWorldName() {
		return WorldName;
	}

	public void setWorldName(String worldName) {
		WorldName = worldName;
	}

	public ArrayList<String> getDisplayText() {
		return DisplayText;
	}

	public void setDisplayText(ArrayList<String> displayText) {
		DisplayText = displayText;
	}

	public Set<Player> getJoinedPlayers() {
		return JoinedPlayers;
	}
	
	public void addJoinedPlayer(Player p) {
		this.JoinedPlayers.add(p);
	}

	public GatePackages getPackages() {
		return packages;
	}

	public void setPackages(GatePackages packages) {
		this.packages = packages;
	}

	public Material getIrisMaterial() {
		for(IrisPart p : this.packages.getIris()) {
			return p.getIrisMaterial();
		}
		return this.irisMaterial;
	}

	public void setIrisMaterial(Material irisMaterial) {
		this.irisMaterial = irisMaterial;
		for(IrisPart p : this.packages.getIris()) {
			p.setIrisMaterial(irisMaterial);
		}
	}

	public boolean isIrisClosed() {
		return IrisClosed;
	}

	public void setIrisClosed(boolean irisClosed) {
		IrisClosed = irisClosed;
	}

	public IrisAnimation getIrisAnimaiton() {
		return irisAnimaiton;
	}

	public void setIrisAnimaiton(IrisAnimation irisAnimaiton) {
		this.irisAnimaiton = irisAnimaiton;
	}

	public String getIrisCode() {
		return IrisCode;
	}

	public void setIrisCode(String irisCode) {
		IrisCode = irisCode;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public Map<Location, Block> getBarrierBlocks() {
		return barrierBlocks;
	}

	public void setBarrierBlocks(Map<Location, Block> barrierBlocks) {
		this.barrierBlocks = barrierBlocks;
	}
	
	public Block getBarrierBlock(Location l) {
		return barrierBlocks.get(l);
	}

	public Map<CommandUseType, List<GateCommandInfo>> getGateCommands() {
		return gateCommands;
	}

	public void setGateCommands(Map<CommandUseType, List<GateCommandInfo>> gateCommands) {
		this.gateCommands = gateCommands;
	}

	public Player getLastActivatedBy() {
		return lastActivatedBy;
	}

	public void setLastActivatedBy(Player lastActivatedBy) {
		this.lastActivatedBy = lastActivatedBy;
	}

	public boolean isUseGatePerms() {
		return useGatePerms;
	}

	public void setUseGatePerms(boolean useGatePerms) {
		this.useGatePerms = useGatePerms;
	}

}
