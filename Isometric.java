package lavaBucket;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import java.awt.geom.AffineTransform;

public class Isometric extends JPanel implements MouseListener, KeyListener,
		MouseMotionListener {

	// when that box is upside down the l vector is negative for the walls.
	// how to accurately monitor the locations of the points after rotation
	// because visualy its bugging and hard to tell.

	// Clicking mouse draws the screen.

	int width = 360;
	int height = 420;

	Image[] imageAr;

	Thread thread;
	Image image;
	Graphics g;

	// Vars for gLoop Above

	float rotateY = -(float) Math.PI / 90;
	// float rotateX = -(float) Math.PI / 90;
	float rotateX = 0;

	float[] faceColor = { 20, 255, 40 };

	public Isometric() {
		super();

		setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		this.setSize(new Dimension(width, height));

		pStart();
	}

	/**
	 * Methods go below here.
	 * 
	 */

	Model mod = new Model();

	public void pStart() {
		imageInit();
		mod.makeCube();
	}

	void projectModel(Model m) {
		System.out.println("***projM***");
		float scalar = m.getScalar();
		float[] translation = m.getTranslation().clone();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		int[][] faces = m.getFaces();
		float[][] points = m.getPoints().clone();

		// instead of rotating manually use rot point

		for (int p0 = 0; p0 < points.length; p0++) {
			points[p0] = Vect3d.vectAdd(m.getPoints()[p0], new float[] { 0, 0,
					0 });
			// for y rot turn (x, z) into a 2d vector then rot that.
			float[] xz = { points[p0][0], points[p0][2] };
			float[] newXz = rotPoint(rotateY, xz);
			points[p0] = new float[] { newXz[0], points[p0][1], newXz[1] };
			float[] yz = { points[p0][1], points[p0][2] };
			float[] newYz = rotPoint(rotateX, yz);
			points[p0] = new float[] { points[p0][0], newYz[0], newYz[1] };

			for (int p1 = 0; p1 < points[p0].length; p1++) {
				points[p0][p1] *= scalar;
			}

			points[p0] = Vect3d.vectAdd(points[p0], translation);
		}

		// get the y of the face after rotations.

		Face[] faceAr = new Face[faces.length];

		// into faces add avg Z, normal, points.
		// run through each face
		for (int f = 0; f < faces.length; f++) {
			// Face[] faceAr
			float avg = 0;
			float[][] inPoints = new float[faces.length][];
			float[][] pertainingFace = new float[faces[f].length][];
			// runs throguth each point on the face
			for (int fp = 0; fp < faces[f].length; fp++) {
				avg += points[faces[f][fp]][2];
				inPoints[fp] = points[faces[f][fp]];
				pertainingFace[fp] = points[faces[f][fp]];
			}
			avg /= faces[f].length;

			// four points of that face.

			float[] normal = normalOfPlane(faces[f], points);

			// Face temp = new Face(avg, normal, points);
			// System.out.println("avg: " + avg);
			faceAr[f] = new Face(avg, normal, pertainingFace, f);
		}

		// get the average z of each wall and sort them.

		faceAr = sortFaces(faceAr);

		// this draws faces in order or farthest to closest.
		for (int fa = 0; fa < faceAr.length; fa++) {
			int[] xs = new int[faceAr.length + 1];
			int[] ys = new int[faceAr.length + 1];
			float[][] facePoints = faceAr[fa].getPoints();
			// load all x and y from faceAr into 2 int[]'s;
			for (int pl = 0; pl < facePoints.length; pl++) {
				xs[pl] = (int) (facePoints[pl][0] + .5f);
				ys[pl] = (int) (facePoints[pl][1] + .5f);
				System.out.println("facePoints[" + pl + "]  ("
						+ facePoints[pl][0] + ", " + facePoints[pl][1] + ", "
						+ facePoints[pl][2] + ")");
				// xs[pl] = (int) (((facePoints[pl][0]) / facePoints[pl][2]) +
				// .5f)
				// + (width / 2);
				// ys[pl] = (int) (((facePoints[pl][1]) / facePoints[pl][2]) +
				// .5f)
				// + (height / 2);
				System.out.println("(x, y)[" + pl + "]  (" + xs[pl] + ", "
						+ ys[pl] + ")");

			}
			xs[facePoints.length] = (int) (facePoints[0][0] + .5f);
			ys[facePoints.length] = (int) (facePoints[0][1] + .5f);
			// xs[facePoints.length] = (int) (((facePoints[0][0]) /
			// facePoints[0][2]) + .5f)
			// + (width / 2);
			// ys[facePoints.length] = (int) (((facePoints[0][1]) /
			// facePoints[0][2]) + .5f)
			// + (height / 2);

			// Im assuming points in that direction.
			float[] lightVector = Vect3d.normalise(new float[] { 1, 0, -2 });
			float l = Vect3d.dot(lightVector,
					Vect3d.normalise(faceAr[fa].getNormal()));

			int[] color = { (int) (l * faceColor[0]), (int) (l * faceColor[1]),
					(int) (l * faceColor[2]) };
			if (l >= 0) {
				g.setColor(new Color(color[0], color[1], color[2]));
				System.out.println("color[" + faceAr[fa].getOrigNum() + "] ("
						+ color[0] + ", " + color[1] + ", " + color[2]);
				g.fillPolygon(xs, ys, faceAr.length);
			} else {
				// g.setColor(new Color(100, 0, 0));
				// System.out.println("red[" + faceAr[fa].getOrigNum() + "]  " +
				// l);
				// g.fillPolygon(xs, ys, faceAr.length);
			}
		}
	}

	float[] normalOfPlane(int[] face, float[][] nodes) {
		float[] n1 = nodes[face[0]];
		float[] n2 = nodes[face[1]];
		float[] n3 = nodes[face[2]];

		float[] v1 = Vect3d.vectSub(n1, n2);
		float[] v2 = Vect3d.vectSub(n1, n3);

		float[] v3 = { (v1[1] * v2[2] - v1[2] * v2[1]),
				(v1[2] * v2[0] - v1[0] * v2[2]),
				(v1[0] * v2[1] - v1[1] * v2[0]) };

		v3 = Vect3d.vectMultScalar(-1, v3);

		return v3;
	}

	Face[] sortFaces(Face[] faces) {
		// run through and find the lowest a's
		//
		// [0] = a
		// [1] = o
		// old number should be unimportant. No it is needed in order to shape
		// the other variable into order too.
		float[][] order = { { faces[0].getZ(), 0 } };
		// System.out.println("faces.length: " + faces.length);
		for (int o = 1; o < faces.length; o++) {
			boolean stuckIn = false;
			// System.out.println("order.length: " + order.length);
			// System.out.println("preloop");
			bloop: for (int l = 0; l < order.length; l++) {
				// System.out.println("faces[" + o + "].getZ(): "
				// + faces[o].getZ());
				// System.out.println("order[" + l + "][0]: " + order[l][0]);
				if (faces[o].getZ() > order[l][0]) {
					// System.out.println("add");
					// stick in before and kill loop
					// System.out.println("order.i: " + order.length);
					order = JaMa.injectFloatArAr(order,
							new float[] { faces[o].getZ(), o }, l);
					// System.out.println("order.i: " + order.length);
					stuckIn = true;
					break bloop;
				} else {
					// check the next
				}
			}
			if (stuckIn == false) {
				// System.out.println("order.a: " + order.length);
				order = JaMa.appendFloatArAr(order,
						new float[] { faces[o].getZ(), o });
				// System.out.println("order.a: " + order.length);
			}
		}
		Face[] temp = new Face[faces.length];
		for (int o = 0; o < order.length; o++) {
			temp[o] = faces[(int) order[o][1]];
			// System.out.println("temp[" + o + "].g: " + temp[o].getZ());
		}
		// System.out.println("ENDDED");
		// System.out.println("faces.l: " + faces.length);
		// System.out.println("temp.l: " + temp.length);
		return temp;
	}

	float[] rotateAroundY(float[] v, float thea) {
		float y = v[1];
		float oldThea = (float) Math.atan(v[2] / v[0]);
		float hyp = Vect2d.norm(new float[] { v[0], v[2] });
		System.out.println("oldTheaY: " + oldThea);
		thea += oldThea;
		float x = (float) Math.cos(thea) * hyp;
		float z = (float) Math.sin(thea) * hyp;
		float[] newLoc = { x, y, z };
		return new float[] { x, y, z };
	}

	// sometimes bugs when thea is negative.
	// the points seem to be correct but the y might be negated.
	// what makes thea negative in the first place.
	// if y is pos z is neg beforehand it turns y negative and z positive.
	float[] rotateAroundX(float[] v, float thea) {
		// how to correctly keep track of signs.
		float x = v[0];
		float oldThea = (float) Math.atan(v[1] / v[2]);
		float hyp = Vect2d.norm(new float[] { v[1], v[2] });
		System.out.println("oldTheaX: " + oldThea);
		thea += oldThea;
		float z = (float) Math.cos(thea) * hyp;
		float y = (float) Math.sin(thea) * hyp;
		float[] newLoc = { x, y, z };
		return new float[] { x, y, z };
	}

	float[][] rotateAllPoints(float[][] points, float thea) {
		for (int p = 0; p < points.length; p++) {
			points[p] = rotateAroundY(points[p], thea);
		}
		return points;
	}

	/**
	 * imported math
	 */

	float pointToThea(float[] point) {
		float pointThea = (float) Math.atan(point[1] / point[0]);
		// sayVect("point", point);
		// System.out.println("pointFirst: " + pointThea + " ("
		// + (pointThea * (180 / Math.PI) + ")"));
		if (point[1] > 0 && pointThea < 0) {
			// System.out.println("change1");
			pointThea = (float) Math.PI + pointThea;
		} else if (point[1] < 0 && pointThea > 0) {
			// y is less than zero and thea is greater than zero.
			// System.out.println("change2");
			pointThea = -(float) Math.PI + pointThea;
		}
		// System.out.println("pointMid: " + pointThea + " ("
		// + (pointThea * (180 / Math.PI) + ")"));
		if (pointThea == 0 && point[0] < 0) {
			// System.out.println("zero to 360.");
			pointThea = (float) Math.PI;
		}
		return pointThea;
	}

	float[] theaToPoint(float thea, float radius) {
		return new float[] { (float) Math.cos(thea) * radius,
				(float) Math.sin(thea) * radius };
	}

	float[] rotPoint(float thea, float[] point) {
		// System.out.println("**rotPoint**");
		// System.out.println("inThea: " + thea + " (" + ((thea) * (180 /
		// Math.PI)));
		float pointa = Vect2d.norm(point);
		boolean bothNeg = false;
		if (point[0] < 0 && point[1] < 0) {
			// bothNeg = true;
		}
		// converts point to thea.
		float pointThea = pointToThea(point);
		// sayVect("point", point);
		// System.out.println("pointThea: " + pointThea);

		// adds theatas.
		float newThea = pointThea + thea;
		// System.out.println("old THEA: " + newThea + " ("
		// + (newThea * (180 / Math.PI)) + ")");
		if (newThea > Math.PI) {
			newThea = newThea - (float) Math.PI * 2;
		} else if (newThea < -Math.PI) {
			newThea = (float) (2 * Math.PI) + newThea;
		}

		// thea to point.
		// newPoint is hypotnuse.
		float[] newPoint = new float[2];
		// float nx = (float) Math.cos(newThea) * pointa;
		// float ny = (float) Math.tan(newThea) * nx;
		// System.out.println("sin: " + Math.sin(newThea) + "    pointa: "
		// + pointa);
		// System.out.println("tan: " + Math.tan(newThea));
		// System.out.println("NEW THEA: " + newThea + " ("
		// + (newThea * (180 / Math.PI)) + ")");
		/**
		 * CHEATE. radius should be pointa, but in order to make the program
		 * lookbetter it is set to radius. Fix the code and make it so pinta is
		 * equal to radius anyway.
		 */
		// System.out.println("newThea: " + newThea + "(" + newThea + ")");
		float ny = (float) Math.sin(newThea) * pointa;
		float nx = 1 / ((float) Math.tan(newThea) / ny);
		if (newThea > Math.PI / 2) {
			// System.out.println("bug ++");
		} else if (newThea < -Math.PI / 2) {
			// System.out.println("bug --");
		}
		if (Float.isNaN(nx)) {
			nx = pointa;
		}
		// System.out.println("bothNeg: " + bothNeg);
		if (bothNeg) {
			nx = -nx;
			ny = -ny;
		}
		// System.out.println("afterRot (" + nx + ", " + ny + ")");

		return new float[] { nx, ny };
	}

	float theaAdd(float thea1, float thea2) {
		// adds two theas between -180 and 180 (in radians) and returned a thea
		// between -180 and 180
		float tempThea = thea1 + thea2;
		if (tempThea > Math.PI) {
			tempThea = -(float) Math.PI * 2 + tempThea;
		} else if (tempThea < -Math.PI) {
			tempThea = (float) Math.PI * 2 + tempThea;
		}
		return tempThea;
	}

	float theaSub(float thea1, float thea2) {
		// System.out.println("thea1: " + thea1 + "   thea2: " + thea2);
		// adds two theas between -180 and 180 (in radians) and returned a thea
		// between -180 and 180
		float tempThea = thea1 - thea2;
		if (tempThea > Math.PI) {
			tempThea = -(float) Math.PI * 2 + tempThea;
		} else if (tempThea < -Math.PI) {
			tempThea = (float) Math.PI * 2 + tempThea;
		}
		// System.out.println("tempThea: " + tempThea);
		return tempThea;
	}

	/**
	 * Methods go above here.
	 * 
	 */

	public void drwGm() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	public void imageInit() {
		/**
		 * imageAr = new Image[1]; ImageIcon ie = new
		 * ImageIcon(this.getClass().getResource( "res/image.png")); imageAr[0]
		 * = ie.getImage();
		 */

	}

	@Override
	public void mousePressed(MouseEvent me) {
		// rotateY += (float) Math.PI / 90;
		projectModel(mod);
		drwGm();

		lastX = me.getX();
		lastY = me.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_A) {
			// rotateY += (float) Math.PI / 90;
			rotateY = theaAdd(rotateY, (float) Math.PI / 90);
			projectModel(mod);
			drwGm();
		} else if (ke.getKeyCode() == KeyEvent.VK_D) {
			// rotateY -= (float) Math.PI / 90;
			rotateY = theaSub(rotateY, (float) Math.PI / 90);
			projectModel(mod);
			drwGm();
		} else if (ke.getKeyCode() == KeyEvent.VK_X) {
			// rotateX += (float) Math.PI / 90;
			rotateX = theaAdd(rotateX, (float) Math.PI / 90);
			projectModel(mod);
			drwGm();
		} else if (ke.getKeyCode() == KeyEvent.VK_Z) {
			// rotateX -= (float) Math.PI / 90;
			rotateX = theaSub(rotateX, (float) Math.PI / 90);
			projectModel(mod);
			drwGm();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	float lastX = 0;
	float lastY = 0;

	@Override
	public void mouseDragged(MouseEvent me) {
		System.out.println("drag");
		int thisX = me.getX();
		int thisY = me.getY();
		float deltaX = thisX - lastX;
		float deltaY = thisY - lastY;
		deltaX /= 80;
		deltaY /= 80;
		System.out.println("deltaX: " + deltaX);
		System.out.println("deltaY: " + deltaY);
		// rotateX3D(deltaY);
		// rotateY3D(deltaX);
		rotateX += deltaY;
		rotateY += deltaX;
		projectModel(mod);
		// draw(m);
		drwGm();
		lastX = thisX;
		lastY = thisY;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
