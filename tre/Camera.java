package LavaBucket.tre;

import LavaBucket.lib.JaMa;
import LavaBucket.lib.Vect2d;
import LavaBucket.lib.Vect3d;

public class Camera {
	private float angleY;
	private float angleX;
	private float dist;
	private Node parent;

	public Camera() {
		angleY = 0;
		angleX = 0;
		dist = 0;
		parent = null;
	}

	public Camera(float dist) {
		angleY = 0;
		angleX = 0;
		this.dist = dist;
		parent = null;
	}

	public void setParent(Node n) {
		parent = n;
	}

	public Node getParent() {
		return parent;
	}

	public float[] getLoc() {
		// Returns the location depending on distance, angleY, angleX
		// this is the x and z
		// Projects out the point around the y axis.

		// float[] xy = Vect2d.theaToPoint(angleX, dist);

		// float[] xz = Vect2d.rotate(dist, 0, angleY);

		float downscale = (float) Math.cos(angleX);
		float z = (float) Math.sin(angleY) * dist * downscale;
		float x = (float) Math.cos(angleY) * dist * downscale;

		float y = (float) Math.sin(angleX) * dist;
		// Rotates the projected point around the x axis
		// float[] xz = Vect2d.rotate(xy[0], 0, angleY);
		// float[] loc = new float[] { -xy[0], xy[1], xz[1] };
		// float[] loc = new float[] { xy[0], xy[1], 00 };
		// float[] loc = new float[] { xz[0], 0, xz[1] };
		float[] loc = new float[] { x, y, z };
		return loc;
	}

	public float[] getLocOld() {
		// Returns the location depending on distance, angleY, angleX
		// this is the x and z
		// Projects out the point around the y axis.
		float[] xz = Vect2d.theaToPoint(angleY, dist);
		// Rotates the projected point around the x axis
		float[] xy = Vect2d.rotate(xz[0], 0, angleX);
		float[] loc = new float[] { -xy[0], xy[1], xz[1] };
		return loc;
	}

	public void rotY(float angle) {
		angleY = JaMa.theaAdd(angleY, angle);
	}

	public void rotX(float angle) {
		angleX = JaMa.theaAdd(angleX, angle);
	}

	public float getAngleX() {
		return angleX;
	}

	public float getAngleY() {
		return angleY;
	}

	public void setDist(float dist) {
		this.dist = dist;
	}

	public void addDist(float dist) {
		this.dist += dist;
	}

}
