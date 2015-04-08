package LavaBucket;

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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import LavaBucket.lib.JaMa;
import LavaBucket.lib.Vect2d;
import LavaBucket.lib.Vect3d;
import LavaBucket.tre.Camera;
import LavaBucket.tre.Face;
import LavaBucket.tre.Model;
import LavaBucket.tre.Node;

public class Trees extends JPanel implements Runnable, KeyListener,
		MouseListener, MouseMotionListener, MouseWheelListener {
	/**
	 * BUG
	 */

	private int width = 720;
	private int height = 540;

	private Thread thread;
	private Image image;
	private Graphics g;

	// Vars for gLoop Below
	private int tps = 20;
	private int mpt = 1000 / tps;
	private int sleepTime = 0;
	private long lastSec = 0;
	private int ticks = 0;
	private long startTime;
	private long nextTick = 0;
	private boolean running = false;

	// Vars for gLoop Above

	private float[] faceColor = { 20, 255, 40 };
	private float[] lightVector = Vect3d.normalise(new float[] { 1, 2, 2 });

	private float[] camLoc = { 0, 0, 0 };

	private float mid = (float) Math.sqrt(2) / 2;

	// Cylinder

	private float[][] cylinderVerts = { { 0, .5f, 0 }, { 1, .5f, 0 },
			{ mid, .5f, mid }, { 0, .5f, 1 }, { -mid, .5f, mid },
			{ -1, .5f, 0 }, { -mid, .5f, -mid }, { 0, .5f, -1 },
			{ mid, .5f, -mid }, { 1, 0, 0 }, { mid, 0, mid }, { 0, 0, 1 },
			{ -mid, 0, mid }, { -1, 0, 0 }, { -mid, 0, -mid }, { 0, 0, -1 },
			{ mid, 0, -mid }, { 1, -.5f, 0 }, { mid, -.5f, mid },
			{ 0, -.5f, 1 }, { -mid, -.5f, mid }, { -1, -.5f, 0 },
			{ -mid, -.5f, -mid }, { 0, -.5f, -1 }, { mid, -.5f, -mid },
			{ 0, -.5f, 0 } };

	private int[][] cylinderFaces = { { 1, 0, 2 }, { 2, 0, 3 }, { 3, 0, 4 },
			{ 4, 0, 5 }, { 5, 0, 6 }, { 6, 0, 7 }, { 7, 0, 8 }, { 8, 0, 9 },
			{ 1, 10, 9 }, { 1, 2, 10 }, { 2, 11, 10 }, { 2, 3, 11 },
			{ 3, 12, 11 }, { 3, 4, 12 }, { 4, 13, 12 }, { 4, 5, 13 },
			{ 5, 14, 13 }, { 5, 6, 14 }, { 6, 15, 14 }, { 6, 7, 15 },
			{ 7, 16, 15 }, { 7, 8, 16 }, { 8, 9, 16 }, { 8, 1, 9 },
			{ 9, 18, 17 }, { 9, 10, 18 }, { 10, 19, 18 }, { 10, 11, 19 },
			{ 11, 20, 19 }, { 11, 12, 20 }, { 12, 21, 20 }, { 12, 13, 21 },
			{ 13, 22, 21 }, { 13, 14, 22 }, { 14, 23, 22 }, { 14, 15, 23 },
			{ 15, 24, 23 }, { 15, 16, 24 }, { 16, 17, 24 }, { 16, 9, 17 },
			{ 24, 25, 23 }, { 23, 25, 22 }, { 22, 25, 21 }, { 21, 25, 20 },
			{ 20, 25, 19 }, { 19, 25, 18 }, { 18, 25, 17 }, { 17, 25, 24 } };

	// Terrain

	private float[][] vertexes;
	private int[][] faces;
	private int[][] map;

	private Model terrain;
	private Model cylinder;

	private Node world;
	private Node terr;
	private Node unit1;

	private Camera cam;

	private float gridScale = 1;

	public Trees() {
		super();

		setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();
	}

	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		this.setSize(new Dimension(width, height));

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		startTime = System.currentTimeMillis();
		gStart();
	}

	/**
	 * Methods go below here.
	 * 
	 */

	private void gStart() {
		cam = new Camera(10);

		map = Reader.decypherMap("src/LavaBucket/res/Map1");
		vertexes = conVert(map);
		faces = conFace(map);

		world = new Node(new float[] { 0, 0, 0 });
		terr = new Node(new float[] { 0, 1, 0 });
		unit1 = new Node(new float[] { 0, 0, 0 });

		cam.setParent(unit1);

		terrain = new Model(vertexes, faces);
		cylinder = new Model(cylinderVerts, cylinderFaces);

		unit1.addMod(cylinder);
		terr.addMod(terrain);

		world.addChild(unit1);
		world.addChild(terr);

		running = true;
		gLoop();
	}

	private void gLoop() {
		while (running) {
			ticks++;

			nextTick = timer() + mpt;

			// Runs once a second and keeps track of ticks;
			// 1000 ms since last output
			if (timer() - lastSec > 1000) {
				if (ticks < tps - 1 || ticks > tps + 1) {
					if (timer() - startTime < 2000) {
						System.out.println("Ticks this second: " + ticks);
						System.out.println("timer(): " + timer());
						System.out.println("nextTick: " + nextTick);
					}
				}

				ticks = 0;
				lastSec = (System.currentTimeMillis() - startTime);
			}

			// Do the things you want the gLoop to do below here
			tic();
			drwGm();

			// And above here.

			// Limits the ticks per second
			// if nextTick is later then timer then sleep till next tick
			if (nextTick > timer()) {
				sleepTime = (int) (nextTick - timer());
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void tic() {
		mouseQ();
		terrainHeight(unit1.getTranslate()[0], unit1.getTranslate()[2]);

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		renderPipeline();
	}

	private ArrayList<Integer> kc = new ArrayList<Integer>();

	private void mouseQ() {
		for (int i = 0; i < kc.size(); i++) {
			oneClick(kc.get(i));
		}
	}

	private void oneClick(int i) {
		if (i == KeyEvent.VK_W) {
			unitMove(.6f);
		} else if (i == KeyEvent.VK_S) {
			unitMove(-.6f);
		} else if (i == KeyEvent.VK_Q) {
			unitMoveR(.6f);
		} else if (i == KeyEvent.VK_E) {
			unitMoveR(-.6f);
		} else if (i == KeyEvent.VK_X) {
			cam.rotX((float) -Math.PI / 30);
		} else if (i == KeyEvent.VK_A) {
			cam.rotY((float) Math.PI / 30);
			cylinder.rotate((float) Math.PI / 30);
		} else if (i == KeyEvent.VK_D) {
			cam.rotY((float) -Math.PI / 30);
			cylinder.rotate((float) -Math.PI / 30);
		} else if (i == KeyEvent.VK_Z) {
			cam.rotX((float) Math.PI / 30);
		} else if (i == KeyEvent.VK_R) {
			cylinder.addScalar(.1f);
		} else if (i == KeyEvent.VK_F) {
			cylinder.addScalar(-.1f);
		}
	}

	private void renderPipeline() {
		camLoc = getAbsolute(cam.getParent(), cam.getLoc());
		// camLoc = getAbsolute(cam.getParent(), new float[] { 0, 0, -10 });
		ArrayList<Face> faces = new ArrayList<Face>();
		iterateFaces(world, faces, Vect3d.vectMultScalar(-1, camLoc));
		faces = sortFaceList(faces);

		for (int i = faces.size() - 1; i >= 0; i--) {
			boolean neg = false;
			for (int j = 0; j < faces.get(i).getVerts().length; j++) {
				if (faces.get(i).getVerts()[j][2] <= .0001) {
					neg = true;
				}
			}
			if (!neg) {
				int[] color = { (int) (faces.get(i).getL() * faceColor[0]),
						(int) (faces.get(i).getL() * faceColor[1]),
						(int) (faces.get(i).getL() * faceColor[2]) };
				if (color[0] > 0 && color[1] > 0 && color[2] > 0) {
					g.setColor(new Color(color[0], color[1], color[2]));
				} else {
					g.setColor(new Color(0, 0, 0));
				}
				float[][] proj = new float[faces.get(i).getVerts().length][2];
				for (int a = 0; a < faces.get(i).getVerts().length; a++) {
					proj[a][0] = faces.get(i).getVerts()[a][0]
							/ faces.get(i).getVerts()[a][2] * width / 2;
					proj[a][1] = faces.get(i).getVerts()[a][1]
							/ faces.get(i).getVerts()[a][2] * height / 2;
				}
				/**
				 * BUG
				 */
				/*-
				   for (int j = 0; j < proj.length; j++) { if (proj[j][1] >
				   100000) { System.out.println("(" + j + "): " + proj[j][0] +
				   ", " + proj[j][1] + ")"); System.out.println("faces.get(i:" +
				   i + ").getVerts()[j:" + j + "][2]: " +
				   faces.get(i).getVerts()[j][2]); } }
				 */

				int[] xP = { JaMa.round(proj[0][0]) + width / 2,
						JaMa.round(proj[1][0]) + width / 2,
						JaMa.round(proj[2][0]) + width / 2 };
				int[] yP = { JaMa.round(proj[0][1]) + height / 2,
						JaMa.round(proj[1][1]) + height / 2,
						JaMa.round(proj[2][1]) + height / 2 };

				g.fillPolygon(xP, yP, xP.length);
			}
		}
	}

	private void iterateFaces(Node n, ArrayList<Face> faces, float[] trans) {
		trans = Vect3d.vectAdd(trans, n.getTranslate());
		for (int i = 0; i < n.getMods().size(); i++) {
			ArrayList<Face> fs = n
					.getMods()
					.get(i)
					.makeFacesOld3(trans, lightVector, cam.getAngleX(),
							cam.getAngleY());
			for (int j = 0; j < fs.size(); j++) {
				faces.add(fs.get(j));
			}
		}
		for (int i = 0; i < n.getChildren().size(); i++) {
			iterateFaces(n.getChildren().get(i), faces, trans);
		}
	}

	private ArrayList<Face> sortFaceList(ArrayList<Face> f) {
		ArrayList<Face> i = new ArrayList<Face>();
		for (int a = 0; a < f.size(); a++) {
			boolean added = false;
			int b = 0;
			while (!added) {
				if (b >= i.size()) {
					added = true;
					i.add(f.get(a));
				} else if (f.get(a).getDist() < i.get(b).getDist()) {
					added = true;
					i.add(b, f.get(a));
				}
				b++;
			}
		}
		return i;
	}

	// Returns the location of the node including trans and its entire parent
	// node heirarchy.
	// Should not return anything if World is not a parent of its.
	private float[] getAbsolute(Node n, float[] trans) {
		trans = Vect3d.vectAdd(trans, n.getTranslate());
		if (n.getParent() != null) {
			trans = getAbsolute(n.getParent(), trans);
		}
		return trans;
	}

	// Moves unit1 node in direction of cam.getAngleY and with a magnitude of
	// dist.
	private void unitMove(float dist) {
		// Moves the unit1 node according to dist and the Y angle of cam.
		float[] xz = Vect2d
				.theaToPoint(cam.getAngleY() + (float) Math.PI, dist);
		unit1.move(new float[] { xz[0], 0, xz[1] });
	}

	// Moves unit1 node in direction perpendicular of cam.getAngleY and with a
	// magnitude of dist.
	private void unitMoveR(float dist) {
		// Moves the unit1 node according to dist and the Y angle of cam.
		float[] xz = Vect2d.theaToPoint(cam.getAngleY() - (float) Math.PI / 2,
				dist);
		unit1.move(new float[] { xz[0], 0, xz[1] });
	}

	// Returns the y at that x and y.
	private void terrainHeight(float x, float z) {
		int vertW = 40;
		if (x <= (vertW - 1) * gridScale && x >= 0) {
			if (z <= (vertW - 1) * gridScale && z >= 0) {
				// x % 10 = remainder in that box.
				// if x remainder is less than y remainder then its in top tri.

				int i0 = 0;
				int i1 = 0;
				int i2 = 0;
				float xM = x % gridScale;
				float zM = z % gridScale;
				xM = xM < 0 ? xM + gridScale : xM;
				zM = zM < 0 ? zM + gridScale : zM;
				float[] vect1;
				float[] vect2;
				float[] vecta;
				if (xM > zM) {
					i1 = (int) (x / gridScale) * vertW + (int) (z / gridScale)
							+ vertW;
					i0 = i1 + 1;
					i2 = i1 - vertW;
					vect1 = Vect3d.vectSub(vertexes[i2], vertexes[i1]);
					vect2 = Vect3d.vectSub(vertexes[i0], vertexes[i1]);

					i0 = i0 >= vertW * vertW ? i0 % vertW * vertW : i0;
					i1 = i1 >= vertW * vertW ? i1 % vertW * vertW : i1;
					i2 = i2 >= vertW * vertW ? i2 % vertW * vertW : i2;
					vecta = terH(1 - (xM / gridScale), (zM / gridScale),
							vertexes[i1], vect1, vect2);
				} else {

					i0 = (int) (x / gridScale) * vertW + (int) (z / gridScale);
					i1 = i0 + 1;
					i2 = i0 + vertW + 1;
					vect1 = Vect3d.vectSub(vertexes[i2], vertexes[i1]);
					vect2 = Vect3d.vectSub(vertexes[i0], vertexes[i1]);
					vecta = terH((xM / gridScale), 1 - (zM / gridScale),
							vertexes[i1], vect1, vect2);

				}
				unit1.setY(vecta[1]);
			}
		}
	}

	// all axis aligned.
	// gives x, z scalar, xVect, zVect. Returns the y at that point.
	private float[] terH(float x, float y, float[] vert, float[] v0, float[] v1) {
		float[] e0 = Vect3d.vectMultScalar(x, v0);
		float[] e1 = Vect3d.vectMultScalar(y, v1);
		return Vect3d.vectAdd(vert, e0, e1);
	}

	public static float[] rot3d(float[] loc, float rotX, float rotY) {
		if (loc[0] == 0 && loc[1] == 0 && loc[2] == 0) {
			return new float[] { 0, 0, 0 };
		}
		// hyp, angX, angY;
		float[] all = new float[4];
		// all[0] = Vect3d.norm(loc);
		all[0] = Vect2d.norm(loc[2], loc[1]);
		all[1] = Vect2d.norm(loc[0], loc[2]);
		all[3] = (float) Vect2d.pTT(loc[0], loc[2]);

		/*-
		 * yz
		 * y sin
		 * sin of theaY  gives Z
		 * sin of theaX givesY
		 */

		// all[2] = Vect2d.pointToThea(loc[0], loc[2]);
		// float downscale = (float) Math.cos(all[1] + rotX);
		float z = (float) Math.sin(all[3] + rotY) * all[1];
		float x = (float) Math.cos(all[3] + rotY) * all[1];
		all[0] = Vect2d.norm(z, loc[1]);
		all[2] = (float) Vect2d.pTT(z, loc[1]);
		float y = (float) Math.sin(all[2] + rotX) * all[0];
		z = (float) Math.cos(all[2] + rotX) * all[0];
		// Rotates the projected point around the x axis
		float[] loca = new float[] { x, y, z };
		return loca;
	}

	/**
	 * Converting height map to verts and faces.
	 */

	private float[][] conVert(int[][] hmap) {
		float[][] verts = new float[hmap.length * hmap[0].length][3];
		for (int x = 0; x < hmap.length; x++) {
			for (int z = 0; z < hmap[x].length; z++) {
				verts[x * hmap.length + z][0] = x * gridScale;
				verts[x * hmap.length + z][1] = -hmap[x][z] / 10f;
				verts[x * hmap.length + z][2] = z * gridScale;
			}
		}
		return verts;
	}

	// numFaces = (x - 1) * (y -1) * 2
	private int[][] conFace(int[][] hmap) {
		int w = hmap.length;
		int h = hmap[0].length;
		int[][] faces = new int[(w - 1) * (h - 1) * 2][];

		for (int x = 0; x < w - 1; x++) {
			for (int z = 0; z < h - 1; z++) {
				int i0 = x * w + z;
				int i1 = x * w + z + 1;
				int i2 = (x + 1) * w + z + 1;
				int i3 = (x + 1) * w + z;

				// System.out.println("[(x(" + x + ") * (w(" + w + ") -1) + z("
				// + z + ") * 2)]: " + (x * (w - 1) + z) * 2);
				// System.out.println("[(x * (w -1) + z) * 2 + 1]: "
				// + ((x * (w - 1) + z) * 2 + 1));

				faces[(x * (w - 1) + z) * 2] = new int[] { i0, i2, i1 };
				faces[(x * (w - 1) + z) * 2 + 1] = new int[] { i0, i3, i2 };

				// if (x != 0 || z != 0) {
				// System.out.print(", ");
				// }
				// System.out.print("{" + i0 + ", " + i2 + ", " + i1 + "}, {" +
				// i0
				// + ", " + i3 + ", " + i2 + "}");
			}
		}
		System.out.println();
		return faces;
	}

	/**
	 * Methods go above here.
	 * 
	 */

	private long timer() {
		return System.currentTimeMillis() - startTime;

	}

	private void drwGm() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		kc.add(ke.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent ke) {
		for (int i = 0; i < kc.size(); i++) {
			if (kc.get(i) == ke.getKeyCode()) {
				kc.remove(i);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent me) {
		int notches = me.getWheelRotation();
		if (notches < 0) {
			cam.addDist(-.2f);
		} else {
			cam.addDist(.2f);
		}
	}
}
