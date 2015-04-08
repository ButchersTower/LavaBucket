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
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Height implements MouseListener, MouseMotionListener, KeyListener {

	JPanel panel;
	private int width = 400;
	private int height = 400;

	private Image image;
	private Graphics g;

	public Height() {
		panel = new JPanel();

		panel.setPreferredSize(new Dimension(width, height));
		panel.setFocusable(true);
		panel.requestFocus();

		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		panel.addKeyListener(this);

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		panel.setSize(new Dimension(width, height));
		pStart();
	}

	public JPanel getPanel() {
		return panel;
	}

	void pStart() {
		decypherMap("src/LavaBucket/res/Map2");
	}

	public void drwGm() {
		Graphics g2 = panel.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	// Do one pass and count up the number of exclamation marks.
	// Do one total pass with an arraylist.

	// "Map1"

	int[][] grid;

	void decypherMap(String s) {
		String a = Reader.inputModel(s).get(0);
		int inc = 0;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) == '!') {
				inc++;
			}
		}
		String[] values = new String[inc];
		inc = 0;
		int last = 0;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) == '!') {
				values[inc] = a.substring(last, i);
				last = i + 1;
				inc++;
			}
		}
		if (last != a.length()) {
			values[inc] = a.substring(last, a.length());
		}
		int[][] grid = new int[values.length][];
		// Count the number of spacesthat have characters bwtween them.
		for (int i = 0; i < values.length; i++) {
			last = 0;
			inc = 0;
			for (int j = 0; j < values[i].length(); j++) {
				if (values[i].charAt(j) == ' ') {
					if (j != last) {
						inc++;
					}
					last = j + 1;
				}
			}
			if (last != values[i].length()) {
				inc++;
			}
			grid[i] = new int[inc];
			last = 0;
			inc = 0;
			for (int j = 0; j < values[i].length(); j++) {
				if (values[i].charAt(j) == ' ') {
					if (j != last) {
						grid[i][inc] = Integer.parseInt(values[i].substring(
								last, j));
						inc++;
					}
					last = j + 1;
				}
			}
			if (last != values[i].length()) {
				grid[i][inc] = Integer.parseInt(values[i].substring(last,
						values[i].length()));
			}
		}
		this.grid = grid;
	}

	void fillScreen(int[][] grid) {
		int colorMult = 1;
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				if (grid[x][y] > 255) {
					g.setColor(Color.WHITE);
				} else if (grid[x][y] < 0) {
					g.setColor(Color.BLACK);
				} else {
					g.setColor(new Color(grid[x][y] * colorMult, grid[x][y]
							* colorMult, grid[x][y] * colorMult));
				}
				// System.out.println("grid[" + x + "][" + y + "]: " +
				// grid[x][y]);
				g.fillRect(x * width / grid.length,
						y * height / grid[x].length, height / grid.length,
						height / grid[x].length);
			}
		}
	}

	void save() {
		String s = "";
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				s += grid[x][y];
				if (y != grid[x].length - 1) {
					s += " ";
				}
			}
			s += "!";
		}
		// System.out.println(s);
		Reader.saveNew("src/LavaBucket/res/Map2", s);
	}

	void mouseHandle(int x, int y) {
		int wid = 40;
		int hei = 40;

		x = x * wid / width;
		y = y * hei / height;
		System.out.println("click (" + x + ", " + y + ")");
		grid[x][y] += 3;
		System.out.println("grid.length: " + grid.length);
		System.out.println("grid[" + x + "].length: " + grid[x].length);
		if (x > 0) {
			grid[x - 1][y] += 2;
			if (y > 0) {
				grid[x - 1][y - 1] += 1;
			}
			if (y < hei - 1) {
				grid[x - 1][y + 1] += 1;
			}
		}
		if (y > 0) {
			grid[x][y - 1] += 2;
		}
		if (x < wid - 1) {
			grid[x + 1][y] += 2;
			if (y > 0) {
				grid[x + 1][y - 1] += 1;
			}
			if (y < hei - 1) {
				grid[x + 1][y + 1] += 1;
			}
		}
		if (y < hei - 1) {
			grid[x][y + 1] += 2;
		}
		fillScreen(grid);
		drwGm();
	}

	// This can be done more efficiently. I think, idk.
	void avg(int x, int y) {
		int wid = 20;
		int hei = 20;
		// center
		int sum = grid[x][y];
		int num = 1;
		if (x > 0) {
			if (x > 1) {
				// left 1
				sum += grid[x - 1][y];
				// left 2
				sum += grid[x - 2][y];
				num += 2;
				if (y > 0) {
					// left 1 up 1
					sum += grid[x - 1][y - 1];
					// left 2 up 1
					sum += grid[x - 2][y - 1];
					// up 1
					sum += grid[x][y - 1];
					num += 3;
					if (y > 1) {
						// left 1 up 2
						sum += grid[x - 1][y - 2];
						// up 2
						sum += grid[x][y - 2];
						num += 2;
					}
				}
				if (y < hei - 1) {
					// left 1 down 1
					sum += grid[x - 1][y + 1];
					// left 2 down 1
					sum += grid[x - 2][y + 1];
					// down 1
					sum += grid[x][y + 1];
					num += 3;
					if (y < hei - 2) {
						// left 1 down 2
						sum += grid[x - 1][y + 2];
						// down 2
						sum += grid[x][y + 2];
						num += 2;
					}
				}
			} else {
				// left 1
				sum += grid[x - 1][y];
				num += 1;
				if (y > 0) {
					// left 1 up 1
					sum += grid[x - 1][y - 1];
					// up 1
					sum += grid[x][y - 1];
					num += 2;
					if (y > 1) {
						// left 1 up 2
						sum += grid[x - 1][y - 2];
						num += 1;
					}
				}
				if (y < hei - 1) {
					// left 1 down 1
					sum += grid[x - 1][y + 1];
					// down 1
					sum += grid[x][y + 1];
					num += 2;
					if (y < hei - 2) {
						// left 1 down 2
						sum += grid[x - 1][y + 2];
						// down 2
						sum += grid[x][y + 2];
						num += 2;
					}
				}
			}
		}
		if (x < wid - 1) {
			if (x < wid - 2) {
				// right 1
				sum += grid[x + 1][y];
				// right 2
				sum += grid[x + 2][y];
				num += 2;
				if (y > 0) {
					// right 1 up 1
					sum += grid[x + 1][y - 1];
					// right 2 up 1
					sum += grid[x + 2][y - 1];
					num += 2;
					if (y > 1) {
						// right 1 up 2
						sum += grid[x + 1][y - 2];
						num += 1;
					}
				}
				if (y < hei - 1) {
					// right 1 down 1
					sum += grid[x + 1][y + 1];
					// right 2 down 1
					sum += grid[x + 2][y + 1];
					num += 2;
					if (y < hei - 2) {
						// right 1 down 2
						sum += grid[x + 1][y + 2];
						num += 1;
					}
				}
			} else {
				// right 1
				sum += grid[x + 1][y];
				num += 1;
				if (y > 0) {
					// right 1 up 1
					sum += grid[x + 1][y - 1];
					num += 1;
					if (y > 1) {
						// right 1 up 2
						sum += grid[x + 1][y - 2];
						num += 1;
					}
				}
				if (y < hei - 1) {
					// right 1 down 1
					sum += grid[x + 1][y + 1];
					num += 1;
					if (y < hei - 2) {
						// left 1 down 2
						sum += grid[x + 1][y + 2];
						num += 1;
					}
				}
			}
		}
		System.out.println("sum: " + sum);
		System.out.println("num: " + num);
		System.out.println((float) sum / num);
		avgOut(x, y, sum / num, wid, hei);
	}

	void avgOut(int x, int y, float avg, int wid, int hei) {
		singlePoint(x, y, avg);
		if (x > 0) {
			singlePoint(x - 1, y, avg);
			// grid[x - 1][y] += 2;
			if (y > 0) {
				// grid[x - 1][y - 1] += 1;
				singlePoint(x - 1, y - 1, avg);
			}
			if (y < hei - 1) {
				// grid[x - 1][y + 1] += 1;
				singlePoint(x - 1, y + 1, avg);
			}
		}
		if (y > 0) {
			// grid[x][y - 1] += 2;
			singlePoint(x, y - 1, avg);
		}
		if (x < wid - 1) {
			// grid[x + 1][y] += 2;
			singlePoint(x + 1, y, avg);
			if (y > 0) {
				// grid[x + 1][y - 1] += 1;
				singlePoint(x + 1, y - 1, avg);
			}
			if (y < hei - 1) {
				// grid[x + 1][y + 1] += 1;
				singlePoint(x + 1, y + 1, avg);
			}
		}
		if (y < hei - 1) {
			// grid[x][y + 1] += 2;
			singlePoint(x, y + 1, avg);
		}
	}

	void singlePoint(int x, int y, float avg) {
		float delta = avg - grid[x][y];
		System.out.println("delta: " + delta);
		if (delta < 1) {
			grid[x][y] += delta;
		} else if (delta < 10) {
			grid[x][y] += (int) (delta / 10);
		}
	}

	@Override
	public void mousePressed(MouseEvent me) {
		if (me.getX() > 0 && me.getX() < width && me.getY() > 0
				&& me.getY() < height) {
			if (me.getButton() == MouseEvent.BUTTON1) {
				mouseHandle(me.getX(), me.getY());
			}
			if (me.getButton() == MouseEvent.BUTTON3) {
				int wid = 40;
				int hei = 40;

				int x = me.getX() * wid / width;
				int y = me.getY() * hei / height;
				avg(x, y);
				fillScreen(grid);
				drwGm();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
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
	public void mouseDragged(MouseEvent me) {
		if (me.getX() > 0 && me.getX() < width && me.getY() > 0
				&& me.getY() < height) {
			mouseHandle(me.getX(), me.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	boolean ctrlP = false;

	@Override
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_S) {
			if (ctrlP) {
				save();
			}
		} else if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
			ctrlP = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
			ctrlP = false;
		}
	}
}
