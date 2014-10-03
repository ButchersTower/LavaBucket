package LavaBucket;

public class Model {
	float[][] points;
	int[][] faces;
	// sides are each point in faces + the next point, or back to the original
	// point if there is no next.

	float[] translation = { 180, 200, 60 };
	// float[] translation = { 0, 0, 0 };

	float scalar = 100;

	// [all far] (bot right) (bot left) (top left) (top right)
	// [all close] (bot right) (bot left) (top left) (top right)
	// higher z should be farther in. but right now it doesn't mean anything.
	// no higher z is farther in that is why it draws the faces with the low z
	// last.
	float[][] cubePoints = { { .5f, .5f, .5f }, { -.5f, .5f, .5f },
			{ -.5f, -.5f, .5f }, { .5f, -.5f, .5f }, { .5f, .5f, -.5f },
			{ -.5f, .5f, -.5f }, { -.5f, -.5f, -.5f }, { .5f, -.5f, -.5f } };
	// float[][] cubePoints = { { 2f, 2f, 2f }, { 1f, 2f, 2f }, { 1f, 1f, 2f },
	// { 2f, 1f, 2f }, { 2f, 2f, 1f }, { 1f, 2f, 1f }, { 1f, 1f, 1f },
	// { 2f, 1f, 1f } };
	// float[][] cubePoints = { { 2f, 3f, 2f }, { 1f, 3f, 2f }, { 1f, 0f, 2f },
	// { 2f, 0f, 2f }, { 2f, 2f, 1f }, { 1f, 2f, 1f }, { 1f, 1f, 1f },
	// { 2f, 1f, 1f } };
	// float[][] cubePoints = { { 2f, 2f, 2f }, { .1f, 2f, 2f }, { .1f, .1f, 2f
	// }, { 2f, .1f, 2f }, { 2f, 2f, .1f }, { .1f, 2f, .1f }, { .1f, .1f, .1f },
	// { 2f, .1f, .1f } };
	// float[][] cubePoints = { { 3f, 3f, 3f }, { 1f, 3f, 3f }, { 1f, 1f, 3f },
	// { 3f, 1f, 3f }, { 3f, 3f, 1f }, { 1f, 3f, 1f }, { 1f, 1f, 1f },
	// { 3f, 1f, 1f } };
	// [2] = { 2, 1, 5, 6 }
	// int[][] cubeFaces = { { 0, 1, 2, 3 }, { 0, 3, 7, 4 }, { 1, 2, 6, 5 },
	// { 0, 1, 4, 5 }, { 2, 3, 6, 7 }, { 7, 6, 5, 4 } };
	// int[][] cubeFaces = { { 3, 2, 1, 0 }, { 4, 7, 3, 0 }, { 1, 2, 6, 5 },
	// { 0, 1, 5, 4 }, { 2, 3, 7, 6 }, { 4, 5, 6, 7 } };

	// (far) (right) (left) (bot) (top) (close)

	// inverting left face is weird.

	// this is the invert of the one below.
	int[][] cubeFaces = { { 3, 2, 1, 0 }, { 4, 7, 3, 0 }, { 1, 2, 6, 5 },
			{ 0, 1, 5, 4 }, { 2, 3, 7, 6 }, { 4, 5, 6, 7 } };

	// int[][] cubeFaces = { { 0, 1, 2, 3 }, { 0, 3, 7, 4 }, { 5, 6, 2, 1 },
	// { 4, 5, 1, 0 }, { 6, 7, 3, 2 }, { 7, 6, 5, 4 } };

	void makeCube() {
		points = cubePoints;
		faces = cubeFaces;
	}

	float[][] getPoints() {
		return points;
	}

	int[][] getFaces() {
		return faces;
	}

	float getScalar() {
		return scalar;
	}

	float[] getTranslation() {
		return translation;
	}
}
