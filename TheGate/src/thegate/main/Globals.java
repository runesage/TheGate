package thegate.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Sound;

public class Globals {
	public static Material DefaultringMaterial;
	public static Material Defaultchevrons_frameMaterial;
	public static Material Defaultchevron_botMaterial;
	public static Material Defaultchevron_lightMaterial;
	public static Material Defaultchevron_lightMaterial_ON;
	public static Material DefaulthorizonMaterial;
	public static Material DefaultDHDMaterial;

	public static final Set<Integer> GateBuilderUUID = new HashSet<Integer>(
			Arrays.asList(-1238144815, 1762632487, -1202865366));
	public static boolean DeadlyVortex = false;
	public static boolean DeadlyIris = false;
	public static boolean GateCanBreakBlocks = false;
	public static boolean CreateBarrier = true;
	public static int GateTime = 300;
	public static boolean UseDummyOwner = false;
	public static String dummyOwnerName = "null";

	public static Material DefaultGateCreationTool = Material.FEATHER;
	public static Material DefaultGateEditTool = Material.BLAZE_ROD;
	public static Material DefaultAbydosCartouche = Material.BOOK;
	public static Material DefaultGateCrystal = Material.QUARTZ;
	public static Material DefaultIDCTransmitter = Material.FIRE_CHARGE;
	public static Material DefaultIrisMaterial = Material.GRAY_CARPET;

	public static Sound DefaultGateActivateSound;
	public static float DefaultGateActivateVolume = 1f;
	public static float DefaultGateActivatePitch = 0.5f;

	public static Sound DefaultGateAmbientSound;
	public static float DefaultGateAmbientVolume = 1f;
	public static float DefaultGateAmbientePitch = 1f;

	public static Sound DefaultChevronLockSound;
	public static float DefaultChevronLockVolume = 1f;
	public static float DefaultChevronLockPitch = 0.5f;

	public static Sound DefaultChevronOpenSound;
	public static float DefaultChevronOpenVolume = 1f;
	public static float DefaultChevronOpenPitch = 0.5f;

	public static Sound DefaultSpinningSound;
	public static float DefaultSpinningVolume = 0.8f;
	public static float DefaultSpinningPitch = 0.3f;

	public static Sound DefaultGateEnterSound;
	public static float DefaultGateEnterVolume = 1f;
	public static float DefaultGateEnterPitch = 1f;

	public static Sound DefaultGateExitSound;
	public static float DefaultGateExitVolume = 1f;
	public static float DefaultGateExitPitch = 1f;

	public static Material[] SymbolMaterial = new Material[16];

	public static boolean MultiDile = false;
	public static boolean AllowQuickDial = true;
	public static int PlayerGateAmmount = 5;
	public static int VisibilityRadius = 40;
	public static int GateExclusionRadius = 150;
	public static boolean DisplayName = false;

	public static boolean UseBungee = false;
	public static String ServerName = "0"; // only important for bugnee

	public static String SaveFromat;
	public static String MySQLUserName;
	public static String MySQLUserPassword;
	public static String MySQLPath;
	public static String playerTable;
	public static String coownerTable;
	public static String gatesTable;
	public static String gatesCommandsTable;
	public static int AutoSyncDatabase;
	public static boolean MoreInfo = true;

	public static Set<Material> excludeList = new HashSet<>();
	public static int SelectionX = 12;
	public static int Selection_X = -12;
	public static int SelectionY = 4;
	public static int Selection_Y = -1;
	public static double Radius = 2.5;
	public static double DestructionDistance = 5;
	public static double DestructionDistanceMult = 0.8;

	public static HashMap<String, String> WorldNames = new HashMap<String, String>();
	public static HashMap<String, Boolean> WorldDefaultSpawn = new HashMap<String, Boolean>();

	public static ArrayList<String> Networks = new ArrayList<String>();

	public static float DialingSpeed;

	public static int DialingAnimationTicks = 1;
	public static int IrisAnimaitonTicks = 1;
	public static double IrisSpeed = 1.0;
	public static double VortexSpeed = 5.0;
	public static boolean DoAnimation = true;
	public static boolean DoDialing = true;
	public static boolean DoVortex = true;
	public static boolean DoHorizonEffect = true;
	public static boolean DoIrisAnimaiton = true;
	public static boolean AllowDialSuggestions = true;
	
}
