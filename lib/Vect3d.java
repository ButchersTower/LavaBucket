package LavaBucket.lib;

public class Vect3d {
	public static float dot(float[] a, float[] b) {
		float dp = (a[0] * b[0]) + (a[1] * b[1]) + (a[2] * b[2]);
		return dp;
	}

	public static float[] normalise(float[] v) {
		float h = (float) Math.sqrt(dot(v, v));
		return new float[] { v[0] / h, v[1] / h, v[2] / h };
	}

	public static float norm(float[] v) {
		return (float) Math.sqrt(dot(v, v));
	}

	public static double norm(double[] v) {
		return (double) Math.sqrt(dot(v, v));
	}

	public static double dot(double[] a, double[] b) {
		double dp = (a[0] * b[0]) + (a[1] * b[1]) + (a[2] * b[2]);
		return dp;
	}

	public static float[] vectMultScalar(float scalar, float[] vect) {
		// this only works for 3d arrays but can make one that is less efficient
		// that works for anything. (i think less effiecient)
		return new float[] { vect[0] * scalar, vect[1] * scalar,
				vect[2] * scalar };
	}

	public static float[] vectDivScalar(float scalar, float[] vect) {
		return new float[] { vect[0] / scalar, vect[1] / scalar,
				vect[2] / scalar };
	}

	public static float[] vectAdd(float[] a, float[] b) {
		return new float[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
	}

	public static float[] vectAdd(float[] a, float[] b, float[] c) {
		return new float[] { a[0] + b[0] + c[0], a[1] + b[1] + c[1],
				a[2] + b[2] + c[2] };
	}

	public static float[] vectSub(float[] a, float[] b) {
		// a minus b.
		// b subtracted from a.
		return new float[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
	}

	public static void sayVect(String name, float[] vect) {
		System.out.println(name + " (" + vect[0] + ", " + vect[1] + ", "
				+ vect[2] + ")");
	}

	public static void sayVect(String name, int[] vect) {
		System.out.println(name + " (" + vect[0] + ", " + vect[1] + ", "
				+ vect[2] + ")");
	}

	public static void sayVect(String name, double[] vect) {
		System.out.println(name + " (" + vect[0] + ", " + vect[1] + ", "
				+ vect[2] + ")");
	}

	public static float[] normalOfPlane(float[] n1, float[] n2, float[] n3) {
		float[] v1 = Vect3d.vectSub(n1, n2);
		float[] v2 = Vect3d.vectSub(n1, n3);
		float[] v3 = { (v1[1] * v2[2] - v1[2] * v2[1]),
				(v1[2] * v2[0] - v1[0] * v2[2]),
				(v1[0] * v2[1] - v1[1] * v2[0]) };

		v3 = Vect3d.vectMultScalar(-1, v3);
		return v3;
	}
}
