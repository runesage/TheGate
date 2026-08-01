package thegate.math;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GateMath {

	public static Vector getRotZY(Vector StartVector, double ax0, double ax1) {

		double x = StartVector.getX();
		double y = StartVector.getY();
		double z = StartVector.getZ();

		double dx = x * Math.cos(ax0) - y * Math.sin(ax0);
		double dy = x * Math.sin(ax0) + y * Math.cos(ax0);
		double dz = z;

		double dx2 = dx * Math.cos(ax1) - dz * Math.sin(ax1);
		double dy2 = dy;
		double dz2 = dx * Math.sin(ax1) + dz * Math.cos(ax1);

		return new Vector(dx2, dy2, dz2);
	}
	
	public static Vector getRotYZ(Vector StartVector, double ax0, double ax1) {

		double x = StartVector.getX();
		double y = StartVector.getY();
		double z = StartVector.getZ();

		double dx = x * Math.cos(ax0) - z * Math.sin(ax0);
		double dy = y;
		double dz = x * Math.sin(ax0) + z * Math.cos(ax0);
		
		double dx2 = dx * Math.cos(ax1) - dy * Math.sin(ax1);
		double dy2 = dx * Math.sin(ax1) + dy * Math.cos(ax1);
		double dz2 = dz;

		return new Vector(dx2, dy2, dz2);
	}
	
	public static Vector getRotZ(Vector StartVector, double ax0) {

		double x = StartVector.getX();
		double y = StartVector.getY();
		double z = StartVector.getZ();

		double dx = x * Math.cos(ax0) - y * Math.sin(ax0);
		double dy = x * Math.sin(ax0) + y * Math.cos(ax0);
		double dz = z;

		return new Vector(dx, dy, dz);
	}
	
	public static Vector getRotY(Vector StartVector, double ax0) {

		double x = StartVector.getX();
		double y = StartVector.getY();
		double z = StartVector.getZ();

		double dx = x * Math.cos(ax0) - z * Math.sin(ax0);
		double dy = y;
		double dz = x * Math.sin(ax0) + z * Math.cos(ax0);
		
		return new Vector(dx, dy, dz);
	}
	
	public static Vector getRotYX(Vector StartVector, double ax0, double ax1) {

		double x = StartVector.getX();
		double y = StartVector.getY();
		double z = StartVector.getZ();

		double dx = x * Math.cos(ax0) - z * Math.sin(ax0);
		double dy = y;
		double dz = x * Math.sin(ax0) + z * Math.cos(ax0);
		
		double dx2 = dx;
		double dy2 = dy * Math.cos(ax1) - dz * Math.sin(ax1);
		double dz2 = dy * Math.sin(ax1) + dz * Math.cos(ax1);

		return new Vector(dx2, dy2, dz2);
	}
	
	public static Vector getRotXY(Vector StartVector, double ax0, double ax1) {

		double x = StartVector.getX();
		double y = StartVector.getY();
		double z = StartVector.getZ();

		double dx = x;
		double dy = y * Math.cos(ax0) - z * Math.sin(ax0);
		double dz = y * Math.sin(ax0) + z * Math.cos(ax0);

		double dx2 = dx * Math.cos(ax1) - dz * Math.sin(ax1);
		double dy2 = dy;
		double dz2 = dx * Math.sin(ax1) + dz * Math.cos(ax1);

		return new Vector(dx2, dy2, dz2);
	}

	public static Vector getFacingVector(Vector baseVector, double ax0) {
		double x = baseVector.getX();
		double y = baseVector.getY();
		double z = baseVector.getZ();


		double dx2 = x * Math.cos(ax0) - z * Math.sin(ax0);
		double dy2 = y;
		double dz2 = x * Math.sin(ax0) + z * Math.cos(ax0);

		return new Vector(dx2, dy2, dz2);
	}
	
	public static Vector getVector(Vector v1, Vector v2) {
		double Vx = v2.getX() - v1.getX();
		double Vy = v2.getY() - v1.getY();
		double Vz = v2.getZ() - v1.getZ();
		
		return new Vector(Vx, Vy, Vz); 
	}
	
	public static double getDistance(Vector v1, Vector v2) {
		double Vx = v1.getX() - v2.getX();
		double Vy = v1.getY() - v2.getY();
		double Vz = v1.getZ() - v2.getZ();
		
		double distance = Math.sqrt((Vx * Vx) + (Vy * Vy) + (Vz * Vz));
		return distance;
	}
	
	public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double Vx = x1 - x2;
		double Vy = y1 - y2;
		double Vz = z1 - z2;
		
		double distance = Math.sqrt((Vx * Vx) + (Vy * Vy) + (Vz * Vz));
		return distance;
	}
	
	public static Vector RotateVectorY(Vector baseVector, double ax0) {
		// BaseVector = vector for gate created by looking north (ArmorStand Facing)
		double x = baseVector.getX();
		double z = baseVector.getZ();

		double dx = x * Math.cos(ax0) - z * Math.sin(ax0);
		double dz = x * Math.sin(ax0) + z * Math.cos(ax0);

		return new Vector(dx, baseVector.getY(), dz);
	}
	
	public static Vector CrossProduct(Vector a, Vector b) {
		
		double x = (a.getY() * b.getZ()) - (a.getZ() * b.getY());
		double y = (a.getZ() * b.getX()) - (a.getX() * b.getZ());
		double z = (a.getX() * b.getY()) - (a.getY() * b.getX());
		
		return new Vector(x, y, z);
	}
	
	public static double DistancePointPlane(Vector PointToPlane, Vector PlanePos, Vector vn) {
		
		double nlength = Math.sqrt((vn.getX()*vn.getX()) + (vn.getY()*vn.getY()) + (vn.getZ()*vn.getZ()));
		vn.setX(vn.getX()/nlength);
		vn.setY(vn.getY()/nlength);
		vn.setZ(vn.getZ()/nlength);
		
		
		double n0 = (-vn.getX() * PlanePos.getX())+(-vn.getY() * PlanePos.getY())+(-vn.getZ() * PlanePos.getZ());
		
		double distance = (vn.getX() * PointToPlane.getX()) + (vn.getY() * PointToPlane.getY()) + (vn.getZ() * PointToPlane.getZ()) + n0; 
		
		return distance;
	}
	
	public static double DistancePointLine(Vector Point, Vector StarLine, Vector Line) {
		Vector startToPoint = new Vector(Point.getX() - StarLine.getX(),
				Point.getY() - StarLine.getY(),
				Point.getZ() - StarLine.getZ());
		Vector c = CrossProduct(startToPoint, Line);
		double lc = Math.sqrt(c.getX()*c.getX() + c.getY()*c.getY() + c.getZ()*c.getZ());
		double ll = Math.sqrt(Line.getX()*Line.getX() + Line.getY()*Line.getY() + Line.getZ()*Line.getZ());
		return lc/ll;
	}
	
	public static double getVectorLength(Vector v) {
		return Math.sqrt((v.getX()*v.getX()) + (v.getY()*v.getY()) + (v.getZ()*v.getZ()));
	}
	
	public static double dotProduct(Vector v1, Vector v2) {
		return v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ();
	}
	
	public static double getAngleRad(Vector v1, Vector v2) {
		return Math.acos(dotProduct(v1, v2)/(getVectorLength(v1)*getVectorLength(v2)));
	}
	
	public static double getAngleDeg(Vector v1, Vector v2) {
		return Math.toDegrees(getAngleRad(v1, v2));
	}
	
	public static float getPlayerDirection(Player playerSelf) {
		float dir = 0f;
		float y = playerSelf.getLocation().getYaw();
		if (y < 0) {
			y += 360;
		}
		y %= 360;
		if(y > 337.5 || y < 22.5) {
			dir = 2f;
		}else if(y >= 22.5 && y <= 67.5) {
			dir = 2.5f;
		} else if(y > 67.5 && y < 112.5) {
			dir = 3f;
		}else if(y >= 112.5 && y <= 157.5) {
			dir = 3.5f;
		} else if(y > 157.5 && y < 202.5) {
			dir = 0f;
		}else if(y >= 202.5 && y <= 247.5) {
			dir = 0.5f;
		}else if(y > 247.5 && y < 292.5) {
			dir = 1f;
		}else if(y >= 292.5 && y <= 337.5) {
			dir = 1.5f;
		}else {
			dir = 0f;
		}
		return dir;
	}
	
}
