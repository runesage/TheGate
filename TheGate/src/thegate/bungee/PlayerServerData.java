package thegate.bungee;

public class PlayerServerData {

	public double PlayerX = 0D;
	public double PlayerY = 0D;
	public double PlayerZ = 0D;
	public double PlayerYaw = 0D;
	public double PlayerPitch = 0D;
	public String AddressFrom = "";
	public String AddressTo = "";
	
	public double getPlayerX() {
		return PlayerX;
	}
	public void setPlayerX(double playerX) {
		PlayerX = playerX;
	}
	public double getPlayerY() {
		return PlayerY;
	}
	public void setPlayerY(double playerY) {
		PlayerY = playerY;
	}
	public double getPlayerZ() {
		return PlayerZ;
	}
	public void setPlayerZ(double playerZ) {
		PlayerZ = playerZ;
	}
	public double getPlayerYaw() {
		return PlayerYaw;
	}
	public void setPlayerYaw(double playerYaw) {
		PlayerYaw = playerYaw;
	}
	public double getPlayerPitch() {
		return PlayerPitch;
	}
	public void setPlayerPitch(double playerPitch) {
		PlayerPitch = playerPitch;
	}
	public String getAddressFrom() {
		return AddressFrom;
	}
	public void setAddressFrom(String addressFrom) {
		AddressFrom = addressFrom;
	}
	public String getAddressTo() {
		return AddressTo;
	}
	public void setAddressTo(String addressTo) {
		AddressTo = addressTo;
	}
	
}
