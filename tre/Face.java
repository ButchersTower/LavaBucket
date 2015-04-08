package LavaBucket.tre;

import LavaBucket.lib.Vect2d;
import LavaBucket.lib.Vect3d;

public class Face {
	private float[][] verts;
	private float dist;

	private float l;

	public Face(float[][] verts) {
		this.verts = verts;
	}

	public Face(float l) {
		this.l = l;
	}

	private void serVerts(float[][] verts) {
		this.verts = verts;
	}

	private void serDist(float dist) {
		this.dist = dist;
	}

	public void translate(float[] t) {
		for (int i = 0; i < verts.length; i++) {
			verts[i] = Vect3d.vectAdd(verts[i], t);
		}
	}

	public float[][] getVerts() {
		return verts;
	}

	public void setVerts(float[][] verts) {
		this.verts = verts;
	}

	public float getDist() {
		return dist;
	}

	public void setDist(float dist) {
		this.dist = dist;
	}

	public void makeDist() {
		float[] sum = { 0, 0, 0 };
		// System.out.println("verts.length: " + verts.length);
		for (int i = 0; i < verts.length; i++) {
			sum = Vect3d.vectAdd(sum, verts[i]);
		}
		// Vect3d.sayVect("sum", sum);
		sum = Vect3d.vectDivScalar(verts.length, sum);
		dist = Vect3d.norm(sum);
		// System.out.println("dist: " + dist);
	}

	public void rotateY(float angle) {
		for (int i = 0; i < verts.length; i++) {
			float[] rot = Vect2d.rotate(verts[i][0], verts[i][2], angle);
			verts[i][0] = rot[0];
			verts[i][2] = rot[1];
		}
	}

	public void rotateX(float angle) {
		for (int i = 0; i < verts.length; i++) {
			float[] rot = Vect2d.rotate(verts[i][0], verts[i][1], angle);
			verts[i][0] = rot[0];
			verts[i][1] = rot[1];
		}
	}

	public float getL() {
		return l;
	}

	public void setL(float l) {
		this.l = l;
	}
}
