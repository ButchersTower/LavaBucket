package lavaBucket;

public class Face {
	float z;
	float[] normal;
	float[][] points;
	int origNum;

	public Face(float z, float[] normal, float[][] points) {
		this.z = z;
		this.normal = normal;
		this.points = points;
	}

	public Face(float z, float[] normal, float[][] points, int origNum) {
		this.z = z;
		this.normal = normal;
		this.points = points;
		this.origNum = origNum;
	}

	float getZ() {
		return z;
	}

	float[] getNormal() {
		return normal;
	}

	float[][] getPoints() {
		return points;
	}

	int getOrigNum() {
		return origNum;
	}
}
