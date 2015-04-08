package LavaBucket.tre;

import java.util.ArrayList;

import LavaBucket.Trees;
import LavaBucket.lib.JaMa;
import LavaBucket.lib.Vect2d;
import LavaBucket.lib.Vect3d;

public class Model {
	private float angle = 0;
	private float[][] verts;
	private int[][] faces;
	float scalar = 1;

	public Model(float[][] verts, int[][] faces) {
		this.verts = verts;
		this.faces = faces;
	}

	public void rotate(float thea) {
		angle = JaMa.theaAdd(angle, thea);
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	// Apply rotation to all verts and then plugs thoes verts into faces.
	public ArrayList<Face> makeFacesOld3(float[] trans, float[] lightVector,
			float camX, float camY) {
		float[][] nverts = new float[verts.length][3];
		for (int i = 0; i < verts.length; i++) {
			// Rotates the verts
			float[] rot = Vect2d.rotate(verts[i][0] * scalar, verts[i][2]
					* scalar, angle);
			nverts[i][0] = rot[0];
			nverts[i][1] = verts[i][1] * scalar;
			nverts[i][2] = rot[1];
			// Translate the verts
			nverts[i] = Vect3d.vectAdd(nverts[i], trans);
			// Get normal, color dist.
		}

		ArrayList<Face> fs = new ArrayList<Face>();

		// Once all verts are effected run throught face and get normals.
		for (int i = 0; i < faces.length; i++) {
			float[] normal = Vect3d.normalOfPlane(nverts[faces[i][0]],
					nverts[faces[i][1]], nverts[faces[i][2]]);
			fs.add(new Face(Vect3d.dot(lightVector, Vect3d.normalise(normal))));
			// faces.get(i).makeDist();
		}
		// Once normals are acheived rot verts a final time then assign to
		// faces.
		for (int i = 0; i < nverts.length; i++) {
			nverts[i] = Trees.rot3d(nverts[i], camX, -camY - (float) Math.PI
					/ 2);
			// nverts[i] = Trees.rot3d(nverts[i], 0, -camY - (float) Math.PI /
			// 2);
		}
		// Give faces the verts.
		for (int i = 0; i < faces.length; i++) {
			float[][] tv = new float[faces[i].length][];
			for (int j = 0; j < tv.length; j++) {
				tv[j] = nverts[faces[i][j]];
			}
			fs.get(i).setVerts(tv);
			fs.get(i).makeDist();
		}
		// for (int i = 0; i < faces.length; i++) {
		// float[][] vs = new float[faces[i].length][];
		// for (int j = 0; j < faces[i].length; j++) {
		// vs[j] = nverts[faces[i][j]];
		// }
		// fs.add(new Face(vs));
		// fs.get(i).makeDist();
		// }
		// System.out.println("fs.get(4).getVerts()[2][1]: "
		// + fs.get(4).getVerts()[2][1]);

		// Vect3d.sayVect("trans", trans);

		for (int i = 0; i < fs.size(); i++) {
			// Vect3d.sayVect("fs.get(i).getVerts()[0]",
			// fs.get(i).getVerts()[0]);
		}

		return fs;
	}

	// Apply rotation to all verts and then plugs thoes verts into faces.
	public ArrayList<Face> makeFaces(float[] trans, float[] lightVector,
			float camX, float camY) {
		float[][] nverts = new float[verts.length][3];
		for (int i = 0; i < verts.length; i++) {
			// Rotates the verts
			float[] rot = Vect2d.rotate(verts[i][0], verts[i][2], angle);
			nverts[i][0] = rot[0];
			nverts[i][1] = verts[i][1];
			nverts[i][2] = rot[1];
			// Translate the verts
			nverts[i] = Vect3d.vectAdd(nverts[i], trans);
			// Get normal, color dist.
		}

		ArrayList<Face> fs = new ArrayList<Face>();

		// Once all verts are effected run throught face and get normals.
		for (int i = 0; i < faces.length; i++) {
			float[] normal = Vect3d.normalOfPlane(verts[faces[i][0]],
					verts[faces[i][1]], verts[faces[i][2]]);
			fs.add(new Face(Vect3d.dot(lightVector, Vect3d.normalise(normal))));
			// faces.get(i).makeDist();
		}
		// Once normals are acheived rot verts a final time then assign to
		// faces.
		for (int i = 0; i < verts.length; i++) {
			// verts[i] = Trees.rot3d(verts[i], camX, camY);
		}
		// Give faces the verts.
		for (int i = 0; i < faces.length; i++) {
			float[][] tv = new float[faces[i].length][];
			for (int j = 0; j < tv.length; j++) {
				tv[j] = verts[faces[i][j]];
			}
			fs.get(i).setVerts(tv);
		}
		for (int i = 0; i < faces.length; i++) {
			float[][] vs = new float[faces[i].length][];
			for (int j = 0; j < faces[i].length; j++) {
				vs[j] = nverts[faces[i][j]];
			}
			fs.add(new Face(vs));
			fs.get(i).makeDist();
		}
		return fs;
	}

	// Apply rotation to all verts and then plugs thoes verts into faces.
	public ArrayList<Face> makeFacesOld2(float[] trans, float[] lightVector) {
		float[][] nverts = new float[verts.length][3];
		for (int i = 0; i < verts.length; i++) {
			// Rotates the verts
			float[] rot = Vect2d.rotate(verts[i][0], verts[i][2], angle);
			nverts[i][0] = rot[0];
			nverts[i][1] = verts[i][1];
			nverts[i][2] = rot[1];
			// Translate the verts
			nverts[i] = Vect3d.vectAdd(nverts[i], trans);
			// Get normal, color dist.
		}
		ArrayList<Face> fs = new ArrayList<Face>();
		for (int i = 0; i < faces.length; i++) {
			float[][] vs = new float[faces[i].length][];
			for (int j = 0; j < faces[i].length; j++) {
				vs[j] = nverts[faces[i][j]];
			}
			fs.add(new Face(vs));
		}
		for (int i = 0; i < fs.size(); i++) {
			float[] normal = Vect3d.normalOfPlane(fs.get(i).getVerts()[0], fs
					.get(i).getVerts()[1], fs.get(i).getVerts()[2]);
			fs.get(i).setL(Vect3d.dot(lightVector, Vect3d.normalise(normal)));
			fs.get(i).makeDist();
		}

		// System.out.println("fs.get(4).getVerts()[2][1]: "
		// + fs.get(4).getVerts()[2][1]);

		Vect3d.sayVect("trans", trans);

		return fs;
	}

	// Apply rotation to all verts and then plugs thoes verts into faces.
	public ArrayList<Face> makeFacesOld1() {
		float[][] nverts = new float[verts.length][3];
		for (int i = 0; i < verts.length; i++) {
			float[] rot = Vect2d.rotate(verts[i][0], verts[i][2], angle);
			nverts[i][0] = rot[0];
			nverts[i][1] = verts[i][1];
			nverts[i][2] = rot[1];
		}
		ArrayList<Face> fs = new ArrayList<Face>();
		for (int i = 0; i < faces.length; i++) {
			float[][] vs = new float[faces[i].length][];
			for (int j = 0; j < faces[i].length; j++) {
				vs[j] = nverts[faces[i][j]];
			}
			fs.add(new Face(vs));
		}
		return fs;
	}

	public float[][] getVerts() {
		return verts;
	}

	public void setScalar(float scalar) {
		this.scalar = scalar;
	}

	public float getScalar() {
		return scalar;
	}

	public void addScalar(float scalar) {
		this.scalar += scalar;
	}
}
