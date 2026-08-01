package thegate.gate;

import org.bukkit.Material;

import thegate.main.ConfigManager;

public enum BlockedState {

	unlocked {
		@Override
		public Material getMaterial() {
			return Material.DARK_OAK_DOOR;
		}

		@Override
		public String getText() {
			return ConfigManager.getString("GUIS.EditGateGUI.Items.Unlock");
		}
	},
	locked {
		@Override
		public Material getMaterial() {
			return Material.BARRIER;
		}

		@Override
		public String getText() {
			return ConfigManager.getString("GUIS.EditGateGUI.Items.Lock");
		}
	},
	block_incoming {
		@Override
		public Material getMaterial() {
			return Material.SHIELD;
		}

		@Override
		public String getText() {
			return ConfigManager.getString("GUIS.EditGateGUI.Items.BlockIncoming");
		}
	},
	block_outgoing {
		@Override
		public Material getMaterial() {
			return Material.COBWEB;
		}

		@Override
		public String getText() {
			return ConfigManager.getString("GUIS.EditGateGUI.Items.BlockOutgoing");
		}
	};
	
	public abstract Material getMaterial();
	public abstract String getText();
	
}
