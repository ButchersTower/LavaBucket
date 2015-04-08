package LavaBucket.old;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

public class TreesOld6 extends JPanel implements Runnable, KeyListener {
	/**
	 * BUG
	 */

	private int width = 720;
	private int height = 540;

	private Thread thread;
	private Image image;
	private Graphics g;

	// Vars for gLoop Below
	private int tps = 10;
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

	private float[][] vertexes = { { -60.0f, 0.0f, -59.999996f },
			{ -60.000008f, 0.0f, -49.999985f }, { -60.0f, 0.0f, -40.0f },
			{ -60.0f, 0.0f, -29.999992f }, { -60.0f, 0.0f, -20.0f },
			{ -60.0f, 0.0f, -9.999999f }, { -60.0f, 0.0f, -5.245367E-6f },
			{ -60.0f, 0.0f, 9.999999f }, { -60.0f, 0.0f, 20.0f },
			{ -60.0f, 0.0f, 29.999992f }, { -60.0f, 0.0f, 40.0f },
			{ -60.000008f, 0.0f, 49.999985f }, { -60.0f, 0.0f, 59.999996f },
			{ -50.000004f, 0.0f, -60.0f }, { -50.0f, 0.0f, -50.0f },
			{ -50.0f, -2.0f, -40.0f }, { -50.0f, -3.0f, -29.999998f },
			{ -50.000004f, -3.0f, -19.99999f }, { -50.0f, -2.0f, -9.999996f },
			{ -50.0f, -1.0f, -4.371139E-6f }, { -50.0f, 0.0f, 9.999996f },
			{ -50.000004f, 0.0f, 19.99999f }, { -50.0f, 0.0f, 29.999998f },
			{ -50.0f, 0.0f, 40.0f }, { -50.0f, 0.0f, 50.0f },
			{ -50.000004f, 0.0f, 60.0f }, { -40.0f, 0.0f, -60.0f },
			{ -40.0f, -1.0f, -50.0f }, { -40.0f, -3.0f, -40.0f },
			{ -40.000004f, -4.0f, -29.999992f },
			{ -40.000004f, -4.0f, -19.999996f }, { -40.0f, -4.0f, -9.999996f },
			{ -40.0f, -3.0f, -3.4969112E-6f }, { -40.0f, -1.0f, 9.999996f },
			{ -40.000004f, 0.0f, 19.999996f },
			{ -40.000004f, 0.0f, 29.999992f }, { -40.0f, 0.0f, 40.0f },
			{ -40.0f, 0.0f, 50.0f }, { -40.0f, 0.0f, 60.0f },
			{ -29.999996f, 0.0f, -60.0f }, { -30.000011f, -2.0f, -49.999992f },
			{ -30.000004f, -4.0f, -39.999996f },
			{ -30.0f, -7.0f, -29.999998f }, { -30.0f, -13.0f, -20.0f },
			{ -30.0f, -10.0f, -10.0f }, { -30.0f, -8.0f, -2.6226835E-6f },
			{ -30.0f, -4.0f, 10.0f }, { -30.0f, -1.0f, 20.0f },
			{ -30.0f, 0.0f, 29.999998f }, { -30.000004f, 0.0f, 39.999996f },
			{ -30.000011f, 0.0f, 49.999992f }, { -29.999996f, 0.0f, 60.0f },
			{ -20.000008f, 0.0f, -60.0f }, { -20.000002f, -3.0f, -50.0f },
			{ -19.999998f, -6.0f, -40.0f }, { -20.0f, -12.0f, -30.0f },
			{ -20.0f, -20.0f, -20.0f }, { -20.000002f, -14.0f, -9.999998f },
			{ -20.0f, -8.0f, -1.7484556E-6f },
			{ -20.000002f, -6.0f, 9.999998f }, { -20.0f, -4.0f, 20.0f },
			{ -20.0f, -2.0f, 30.0f }, { -19.999998f, 0.0f, 40.0f },
			{ -20.000002f, 0.0f, 50.0f }, { -20.000008f, 0.0f, 60.0f },
			{ -10.000006f, 0.0f, -60.0f }, { -10.000002f, -2.0f, -50.0f },
			{ -10.000001f, -4.0f, -39.999996f },
			{ -10.000004f, -7.0f, -30.0f }, { -9.999999f, -14.0f, -20.0f },
			{ -10.0f, -10.0f, -10.0f }, { -10.0f, -8.0f, -8.742278E-7f },
			{ -10.0f, -5.0f, 10.0f }, { -9.999999f, -3.0f, 20.0f },
			{ -10.000004f, -1.0f, 30.0f }, { -10.000001f, 0.0f, 39.999996f },
			{ -10.000002f, 0.0f, 50.0f }, { -10.000006f, 0.0f, 60.0f },
			{ -2.6226835E-6f, 0.0f, -60.0f }, { -2.1855694E-6f, 0.0f, -50.0f },
			{ -1.7484556E-6f, -2.0f, -40.0f },
			{ -1.3113417E-6f, -4.0f, -30.0f },
			{ -8.742278E-7f, -8.0f, -20.0f }, { -4.371139E-7f, -6.0f, -10.0f },
			{ 0.0f, -3.0f, 0.0f }, { -4.371139E-7f, -2.0f, 10.0f },
			{ -8.742278E-7f, -2.0f, 20.0f }, { -1.3113417E-6f, 0.0f, 30.0f },
			{ -1.7484556E-6f, 0.0f, 40.0f }, { -2.1855694E-6f, 0.0f, 50.0f },
			{ -2.6226835E-6f, 0.0f, 60.0f }, { 10.000001f, 0.0f, -60.0f },
			{ 9.999998f, 0.0f, -50.0f }, { 9.999998f, -1.0f, -40.0f },
			{ 10.000001f, -2.0f, -30.0f }, { 9.999999f, -5.0f, -20.0f },
			{ 9.999999f, -4.0f, -10.0f }, { 10.0f, -1.0f, 0.0f },
			{ 9.999999f, -1.0f, 10.0f }, { 9.999999f, 0.0f, 20.0f },
			{ 10.000001f, 0.0f, 30.0f }, { 9.999998f, 0.0f, 40.0f },
			{ 9.999998f, 0.0f, 50.0f }, { 10.000001f, 0.0f, 60.0f },
			{ 20.000002f, 0.0f, -60.0f }, { 19.999998f, 0.0f, -50.0f },
			{ 19.999998f, 0.0f, -40.0f }, { 19.999998f, -1.0f, -30.0f },
			{ 19.999998f, -2.0f, -20.0f }, { 20.0f, -1.0f, -10.0f },
			{ 20.0f, -1.0f, 0.0f }, { 20.0f, 0.0f, 10.0f },
			{ 19.999998f, 0.0f, 20.0f }, { 19.999998f, 0.0f, 30.0f },
			{ 19.999998f, 0.0f, 40.0f }, { 19.999998f, 0.0f, 50.0f },
			{ 20.000002f, 0.0f, 60.0f }, { 29.999998f, 0.0f, -60.0f },
			{ 30.0f, 0.0f, -49.999996f }, { 30.0f, 0.0f, -40.0f },
			{ 29.999998f, 0.0f, -30.0f }, { 30.0f, 0.0f, -20.0f },
			{ 30.0f, 0.0f, -10.0f }, { 30.0f, 0.0f, 0.0f },
			{ 30.0f, 0.0f, 10.0f }, { 30.0f, 0.0f, 20.0f },
			{ 29.999998f, 6.0f, 30.0f }, { 30.0f, 6.0f, 40.0f },
			{ 30.0f, 0.0f, 49.999996f }, { 29.999998f, 0.0f, 60.0f },
			{ 39.999996f, 0.0f, -60.0f }, { 40.0f, 0.0f, -50.0f },
			{ 39.999996f, 0.0f, -40.0f }, { 40.0f, 0.0f, -30.0f },
			{ 40.0f, 0.0f, -20.0f }, { 40.0f, 0.0f, -10.0f },
			{ 40.0f, 0.0f, 0.0f }, { 40.0f, 0.0f, 10.0f },
			{ 40.0f, 0.0f, 20.0f }, { 40.0f, 6.0f, 30.0f },
			{ 39.999996f, 6.0f, 40.0f }, { 40.0f, 0.0f, 50.0f },
			{ 39.999996f, 0.0f, 60.0f }, { 50.0f, 0.0f, -60.0f },
			{ 50.0f, 0.0f, -50.0f }, { 50.0f, 0.0f, -40.0f },
			{ 50.0f, 0.0f, -30.0f }, { 50.0f, 0.0f, -20.0f },
			{ 50.0f, 0.0f, -10.0f }, { 50.0f, 0.0f, 0.0f },
			{ 50.0f, 0.0f, 10.0f }, { 50.0f, 0.0f, 20.0f },
			{ 50.0f, 0.0f, 30.0f }, { 50.0f, 0.0f, 40.0f },
			{ 50.0f, 0.0f, 50.0f }, { 50.0f, 0.0f, 60.0f },
			{ 59.999996f, 0.0f, -60.0f }, { 60.0f, 0.0f, -50.0f },
			{ 60.0f, 0.0f, -40.0f }, { 60.0f, 0.0f, -30.0f },
			{ 60.0f, 0.0f, -20.0f }, { 60.0f, 0.0f, -10.0f },
			{ 60.0f, 0.0f, 0.0f }, { 60.0f, 0.0f, 10.0f },
			{ 60.0f, 0.0f, 20.0f }, { 60.0f, 0.0f, 30.0f },
			{ 60.0f, 0.0f, 40.0f }, { 60.0f, 0.0f, 50.0f },
			{ 59.999996f, 0.0f, 60.0f } };

	private int[][] faces = { { 0, 14, 1 }, { 0, 13, 14 }, { 1, 15, 2 },
			{ 1, 14, 15 }, { 2, 16, 3 }, { 2, 15, 16 }, { 3, 17, 4 },
			{ 3, 16, 17 }, { 4, 18, 5 }, { 4, 17, 18 }, { 5, 19, 6 },
			{ 5, 18, 19 }, { 6, 20, 7 }, { 6, 19, 20 }, { 7, 21, 8 },
			{ 7, 20, 21 }, { 8, 22, 9 }, { 8, 21, 22 }, { 9, 23, 10 },
			{ 9, 22, 23 }, { 10, 24, 11 }, { 10, 23, 24 }, { 11, 25, 12 },
			{ 11, 24, 25 }, { 13, 27, 14 }, { 13, 26, 27 }, { 14, 28, 15 },
			{ 14, 27, 28 }, { 15, 29, 16 }, { 15, 28, 29 }, { 16, 30, 17 },
			{ 16, 29, 30 }, { 17, 31, 18 }, { 17, 30, 31 }, { 18, 32, 19 },
			{ 18, 31, 32 }, { 19, 33, 20 }, { 19, 32, 33 }, { 20, 34, 21 },
			{ 20, 33, 34 }, { 21, 35, 22 }, { 21, 34, 35 }, { 22, 36, 23 },
			{ 22, 35, 36 }, { 23, 37, 24 }, { 23, 36, 37 }, { 24, 38, 25 },
			{ 24, 37, 38 }, { 26, 40, 27 }, { 26, 39, 40 }, { 27, 41, 28 },
			{ 27, 40, 41 }, { 28, 42, 29 }, { 28, 41, 42 }, { 29, 43, 30 },
			{ 29, 42, 43 }, { 30, 44, 31 }, { 30, 43, 44 }, { 31, 45, 32 },
			{ 31, 44, 45 }, { 32, 46, 33 }, { 32, 45, 46 }, { 33, 47, 34 },
			{ 33, 46, 47 }, { 34, 48, 35 }, { 34, 47, 48 }, { 35, 49, 36 },
			{ 35, 48, 49 }, { 36, 50, 37 }, { 36, 49, 50 }, { 37, 51, 38 },
			{ 37, 50, 51 }, { 39, 53, 40 }, { 39, 52, 53 }, { 40, 54, 41 },
			{ 40, 53, 54 }, { 41, 55, 42 }, { 41, 54, 55 }, { 42, 56, 43 },
			{ 42, 55, 56 }, { 43, 57, 44 }, { 43, 56, 57 }, { 44, 58, 45 },
			{ 44, 57, 58 }, { 45, 59, 46 }, { 45, 58, 59 }, { 46, 60, 47 },
			{ 46, 59, 60 }, { 47, 61, 48 }, { 47, 60, 61 }, { 48, 62, 49 },
			{ 48, 61, 62 }, { 49, 63, 50 }, { 49, 62, 63 }, { 50, 64, 51 },
			{ 50, 63, 64 }, { 52, 66, 53 }, { 52, 65, 66 }, { 53, 67, 54 },
			{ 53, 66, 67 }, { 54, 68, 55 }, { 54, 67, 68 }, { 55, 69, 56 },
			{ 55, 68, 69 }, { 56, 70, 57 }, { 56, 69, 70 }, { 57, 71, 58 },
			{ 57, 70, 71 }, { 58, 72, 59 }, { 58, 71, 72 }, { 59, 73, 60 },
			{ 59, 72, 73 }, { 60, 74, 61 }, { 60, 73, 74 }, { 61, 75, 62 },
			{ 61, 74, 75 }, { 62, 76, 63 }, { 62, 75, 76 }, { 63, 77, 64 },
			{ 63, 76, 77 }, { 65, 79, 66 }, { 65, 78, 79 }, { 66, 80, 67 },
			{ 66, 79, 80 }, { 67, 81, 68 }, { 67, 80, 81 }, { 68, 82, 69 },
			{ 68, 81, 82 }, { 69, 83, 70 }, { 69, 82, 83 }, { 70, 84, 71 },
			{ 70, 83, 84 }, { 71, 85, 72 }, { 71, 84, 85 }, { 72, 86, 73 },
			{ 72, 85, 86 }, { 73, 87, 74 }, { 73, 86, 87 }, { 74, 88, 75 },
			{ 74, 87, 88 }, { 75, 89, 76 }, { 75, 88, 89 }, { 76, 90, 77 },
			{ 76, 89, 90 }, { 78, 92, 79 }, { 78, 91, 92 }, { 79, 93, 80 },
			{ 79, 92, 93 }, { 80, 94, 81 }, { 80, 93, 94 }, { 81, 95, 82 },
			{ 81, 94, 95 }, { 82, 96, 83 }, { 82, 95, 96 }, { 83, 97, 84 },
			{ 83, 96, 97 }, { 84, 98, 85 }, { 84, 97, 98 }, { 85, 99, 86 },
			{ 85, 98, 99 }, { 86, 100, 87 }, { 86, 99, 100 }, { 87, 101, 88 },
			{ 87, 100, 101 }, { 88, 102, 89 }, { 88, 101, 102 },
			{ 89, 103, 90 }, { 89, 102, 103 }, { 91, 105, 92 },
			{ 91, 104, 105 }, { 92, 106, 93 }, { 92, 105, 106 },
			{ 93, 107, 94 }, { 93, 106, 107 }, { 94, 108, 95 },
			{ 94, 107, 108 }, { 95, 109, 96 }, { 95, 108, 109 },
			{ 96, 110, 97 }, { 96, 109, 110 }, { 97, 111, 98 },
			{ 97, 110, 111 }, { 98, 112, 99 }, { 98, 111, 112 },
			{ 99, 113, 100 }, { 99, 112, 113 }, { 100, 114, 101 },
			{ 100, 113, 114 }, { 101, 115, 102 }, { 101, 114, 115 },
			{ 102, 116, 103 }, { 102, 115, 116 }, { 104, 118, 105 },
			{ 104, 117, 118 }, { 105, 119, 106 }, { 105, 118, 119 },
			{ 106, 120, 107 }, { 106, 119, 120 }, { 107, 121, 108 },
			{ 107, 120, 121 }, { 108, 122, 109 }, { 108, 121, 122 },
			{ 109, 123, 110 }, { 109, 122, 123 }, { 110, 124, 111 },
			{ 110, 123, 124 }, { 111, 125, 112 }, { 111, 124, 125 },
			{ 112, 126, 113 }, { 112, 125, 126 }, { 113, 127, 114 },
			{ 113, 126, 127 }, { 114, 128, 115 }, { 114, 127, 128 },
			{ 115, 129, 116 }, { 115, 128, 129 }, { 117, 131, 118 },
			{ 117, 130, 131 }, { 118, 132, 119 }, { 118, 131, 132 },
			{ 119, 133, 120 }, { 119, 132, 133 }, { 120, 134, 121 },
			{ 120, 133, 134 }, { 121, 135, 122 }, { 121, 134, 135 },
			{ 122, 136, 123 }, { 122, 135, 136 }, { 123, 137, 124 },
			{ 123, 136, 137 }, { 124, 138, 125 }, { 124, 137, 138 },
			{ 125, 139, 126 }, { 125, 138, 139 }, { 126, 140, 127 },
			{ 126, 139, 140 }, { 127, 141, 128 }, { 127, 140, 141 },
			{ 128, 142, 129 }, { 128, 141, 142 }, { 130, 144, 131 },
			{ 130, 143, 144 }, { 131, 145, 132 }, { 131, 144, 145 },
			{ 132, 146, 133 }, { 132, 145, 146 }, { 133, 147, 134 },
			{ 133, 146, 147 }, { 134, 148, 135 }, { 134, 147, 148 },
			{ 135, 149, 136 }, { 135, 148, 149 }, { 136, 150, 137 },
			{ 136, 149, 150 }, { 137, 151, 138 }, { 137, 150, 151 },
			{ 138, 152, 139 }, { 138, 151, 152 }, { 139, 153, 140 },
			{ 139, 152, 153 }, { 140, 154, 141 }, { 140, 153, 154 },
			{ 141, 155, 142 }, { 141, 154, 155 }, { 143, 157, 144 },
			{ 143, 156, 157 }, { 144, 158, 145 }, { 144, 157, 158 },
			{ 145, 159, 146 }, { 145, 158, 159 }, { 146, 160, 147 },
			{ 146, 159, 160 }, { 147, 161, 148 }, { 147, 160, 161 },
			{ 148, 162, 149 }, { 148, 161, 162 }, { 149, 163, 150 },
			{ 149, 162, 163 }, { 150, 164, 151 }, { 150, 163, 164 },
			{ 151, 165, 152 }, { 151, 164, 165 }, { 152, 166, 153 },
			{ 152, 165, 166 }, { 153, 167, 154 }, { 153, 166, 167 },
			{ 154, 168, 155 }, { 154, 167, 168 } };

	public TreesOld6() {
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

		startTime = System.currentTimeMillis();
		gStart();
	}

	/**
	 * Methods go below here.
	 * 
	 */

	private Model terrain;
	private Model cylinder;

	private Node world;
	private Node terr;
	private Node unit1;

	private Camera cam;

	private void gStart() {

		// int[][] t = { { 0, 0, 0 }, { 0, -2, 0 }, { 0, 1, 0 } };
		// float[] t = { .5f, .866f, 0 };
		// float[] t = { 0, 0, 1 };
		// float[] t = { .6123f, .5f, .6123f };
		// float[] t = { -1, .5f, 0 };
		// rot3d(t, 0, 0);
		// vertexes = conVert(t);
		// faces = conFace(t);

		cam = new Camera(10);

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

		for (int i = 0; i < cylinder.getVerts().length; i++) {
			// cylinder.getVerts()[i] = rot3d(cylinder.getVerts()[i],
			// cam.getAngleX(), cam.getAngleY());
			// cylinder.getVerts()[i] = rot3d(cylinder.getVerts()[i],
			// (float) Math.PI / 6, 0);

			// cylinder.getVerts()[i] = rot3d(cylinder.getVerts()[i],
			// (float) Math.PI / 6, (float) Math.PI / 2);

			// cylinder.getVerts()[i] = rot3d(cylinder.getVerts()[i], 0, 0);

		}

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
		System.out.println("******");
		mouseQ();
		terrainHeight(unit1.getTranslate()[0], unit1.getTranslate()[2]);

		// for (int i = 0; i < cylinder.getVerts().length; i++) {
		// cylinder.getVerts()[i] = rot3d(cylinder.getVerts()[i],
		// cam.getAngleX(), cam.getAngleY());
		// }

		// float[] t = { .5f, .866f, 0 };
		// System.out.println("camAngles: (" + cam.getAngleX() + ", "
		// + cam.getAngleY() + ")");
		// float[] t = { 1.5f, 3, 2.6f };
		// rot3d(t, 0, 0);

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
			unitMove(1);
		} else if (i == KeyEvent.VK_S) {
			unitMove(-1);
		} else if (i == KeyEvent.VK_Q) {
			unitMoveR(1);
		} else if (i == KeyEvent.VK_E) {
			unitMoveR(-1);
		} else if (i == KeyEvent.VK_X) {
			cam.rotX((float) -Math.PI / 20);
		} else if (i == KeyEvent.VK_A) {
			cam.rotY((float) Math.PI / 20);
			cylinder.rotate((float) Math.PI / 20);
		} else if (i == KeyEvent.VK_D) {
			cam.rotY((float) -Math.PI / 20);
			cylinder.rotate((float) -Math.PI / 20);
		} else if (i == KeyEvent.VK_SPACE) {
			cam.rotX((float) Math.PI / 20);
		}
	}

	private void renderPipeline() {
		camLoc = getAbsolute(cam.getParent(), cam.getLoc());
		// camLoc = getAbsolute(cam.getParent(), new float[] { 0, 0, -10 });
		ArrayList<Face> faces = new ArrayList<Face>();
		iterateFaces(world, faces, Vect3d.vectMultScalar(-1, camLoc));
		// for (int i = 0; i < faces.size(); i++) {
		// float[] normal = normalOfPlane(faces.get(i).getVerts()[0], faces
		// .get(i).getVerts()[1], faces.get(i).getVerts()[2]);
		// faces.get(i)
		// .setL(Vect3d.dot(lightVector, Vect3d.normalise(normal)));
		// faces.get(i).makeDist();
		// }
		faces = sortT(faces);

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

				// int[] xP = { (int) (proj[0][0] + .5f) + width / 2,
				// (int) (proj[1][0] + .5f) + width / 2,
				// (int) (proj[2][0] + .5f) + width / 2 };
				// int[] yP = { (int) (proj[0][1] + .5f) + height / 2,
				// (int) (proj[1][1] + .5f + height / 2),
				// (int) (proj[2][1] + .5f) + height / 2 };
				g.fillPolygon(xP, yP, xP.length);
			}
		}
	}

	private float[] normalOfPlane(float[] n1, float[] n2, float[] n3) {
		float[] v1 = Vect3d.vectSub(n1, n2);
		float[] v2 = Vect3d.vectSub(n1, n3);
		float[] v3 = { (v1[1] * v2[2] - v1[2] * v2[1]),
				(v1[2] * v2[0] - v1[0] * v2[2]),
				(v1[0] * v2[1] - v1[1] * v2[0]) };
		v3 = Vect3d.vectMultScalar(-1, v3);
		return v3;
	}

	private void iterateFaces(Node n, ArrayList<Face> faces, float[] trans) {
		trans = Vect3d.vectAdd(trans, n.getTranslate());
		for (int i = 0; i < n.getMods().size(); i++) {
			ArrayList<Face> fs = n
					.getMods()
					.get(i)
					.makeFacesOld3(trans, lightVector, cam.getAngleX(),
							cam.getAngleY());
			// ArrayList<Face> fs = n.getMods().get(i)
			// .makeFacesOld3(trans, lightVector, 0, 0);

			// ArrayList<Face> fs = n.getMods().get(i)
			// .makeFacesOld2(trans, lightVector);

			for (int j = 0; j < fs.size(); j++) {
				// fs.get(j).translate(trans);
				faces.add(fs.get(j));
			}
		}
		for (int i = 0; i < n.getChildren().size(); i++) {
			iterateFaces(n.getChildren().get(i), faces, trans);
		}
	}

	// Run through models and add their faces, run throught children recurse.
	private void iterateFacesOld1(Node n, ArrayList<Face> faces, float[] trans) {
		trans = Vect3d.vectAdd(trans, n.getTranslate());
		for (int i = 0; i < n.getMods().size(); i++) {
			// ArrayList<Face> fs = n
			// .getMods()
			// .get(i)
			// .makeFacesOld3(trans, lightVector, cam.getAngleX(),
			// cam.getAngleY());

			ArrayList<Face> fs = n.getMods().get(i)
					.makeFacesOld2(trans, lightVector);

			for (int j = 0; j < fs.size(); j++) {
				// fs.get(j).translate(trans);
				faces.add(fs.get(j));
			}
		}
		for (int i = 0; i < n.getChildren().size(); i++) {
			iterateFaces(n.getChildren().get(i), faces, trans);
		}
	}

	private ArrayList<Face> sortT(ArrayList<Face> f) {
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
		float[] xz = Vect2d.theaToPoint(-cam.getAngleY() - (float) Math.PI / 2,
				dist);
		unit1.move(new float[] { xz[0], 0, xz[1] });
	}

	// Takes in x and z and gets the y of those coords on the terrain vert
	// grid. sets unit1.getY to the y
	// This currently treets the
	private void terrainHeight(float x, float z) {
		Vect3d.sayVect("unit1.t", unit1.getTranslate());
		// System.out.println("x: " + x);
		// System.out.println("z: " + z);
		int vertW = 13;
		int vW = (vertW - 1) / 2;
		x += vW * 10;
		z += vW * 10;
		// plug in x and z and get teh y of that tri for that box.
		if (x <= vW * 20 && x >= 0) {
			if (z <= vW * 20 && z >= 0) {
				// x % 10 = remainder in that box.
				// if x remainder is less than y remainder then its in top tri.

				int i0 = 0;
				int i1 = 0;
				int i2 = 0;
				float xM = x % 10;
				float zM = z % 10;
				xM = xM < 0 ? xM + 10 : xM;
				zM = zM < 0 ? zM + 10 : zM;
				// System.out.println("xM: " + xM);
				// System.out.println("zM: " + zM);
				float[] vect1;
				float[] vect2;
				float[] vecta;
				if (xM > zM) {
					// System.out.println("(x / 10) : " + (x / 10));
					// System.out.println("vertW: " + vertW);
					// System.out.println("(z / 10): " + (z / 10));
					i0 = (int) (x / 10) + (int) (z / 10) * vertW;
					i1 = i0 + 1;
					i2 = i0 + vertW + 1;
					vect1 = Vect3d.vectSub(vertexes[i0], vertexes[i1]);
					vect2 = Vect3d.vectSub(vertexes[i2], vertexes[i1]);
					vecta = terH(1 - (xM / 10), zM / 10, vertexes[i1], vect1,
							vect2);
				} else {
					// System.out.println("(x / 10) : " + (x / 10));
					// System.out.println("vertW: " + vertW);
					// System.out.println("(z / 10): " + (z / 10));
					i1 = (int) (x / 10) + (int) (z / 10) * vertW + vertW;
					i0 = i1 + 1;
					i2 = i1 - vertW;
					vect1 = Vect3d.vectSub(vertexes[i0], vertexes[i1]);
					vect2 = Vect3d.vectSub(vertexes[i2], vertexes[i1]);

					i0 = i0 >= vertW * vertW ? i0 % vertW * vertW : i0;
					i1 = i1 >= vertW * vertW ? i1 % vertW * vertW : i1;
					i2 = i2 >= vertW * vertW ? i2 % vertW * vertW : i2;
					vecta = terH(xM / 10, 1 - (zM / 10), vertexes[i1], vect1,
							vect2);
				}
				// If verts is over max then it brings it back to the beginning

				System.out.println("i0: " + i0);
				System.out.println("i1: " + i1);
				System.out.println("i2: " + i2);

				Vect3d.sayVect("vecta", vecta);
				// unit1.setTranslate(vecta);
				unit1.setY(vecta[1]);
			}
		}
	}

	private void terrainHeightOld(float x, float z) {
		Vect3d.sayVect("unit1.t", unit1.getTranslate());
		// System.out.println("x: " + x);
		// System.out.println("z: " + z);
		int vertW = 13;
		int vW = (vertW - 1) / 2;
		x += vW * 10;
		z += vW * 10;
		// plug in x and z and get teh y of that tri for that box.
		if (x <= vW * 20 && x >= 0) {
			if (z <= vW * 20 && z >= 0) {
				// x % 10 = remainder in that box.
				// if x remainder is less than y remainder then its in top tri.

				int i0 = 0;
				int i1 = 0;
				int i2 = 0;
				float xM = x % 10;
				float zM = z % 10;
				xM = xM < 0 ? xM + 10 : xM;
				zM = zM < 0 ? zM + 10 : zM;
				// System.out.println("xM: " + xM);
				// System.out.println("zM: " + zM);
				float[] vect1;
				float[] vect2;
				float[] vecta;
				if (xM > zM) {
					// System.out.println("(x / 10) : " + (x / 10));
					// System.out.println("vertW: " + vertW);
					// System.out.println("(z / 10): " + (z / 10));
					i0 = (int) (x / 10) + (int) (z / 10) * vertW;
					i1 = i0 + 1;
					i2 = i0 + vertW + 1;
					vect1 = Vect3d.vectSub(vertexes[i0], vertexes[i1]);
					vect2 = Vect3d.vectSub(vertexes[i2], vertexes[i1]);
					vecta = terH(1 - (xM / 10), zM / 10, vertexes[i1], vect1,
							vect2);
				} else {
					// System.out.println("(x / 10) : " + (x / 10));
					// System.out.println("vertW: " + vertW);
					// System.out.println("(z / 10): " + (z / 10));
					i1 = (int) (x / 10) + (int) (z / 10) * vertW + vertW;
					i0 = i1 + 1;
					i2 = i1 - vertW;
					vect1 = Vect3d.vectSub(vertexes[i0], vertexes[i1]);
					vect2 = Vect3d.vectSub(vertexes[i2], vertexes[i1]);

					i0 = i0 >= vertW * vertW ? i0 % vertW * vertW : i0;
					i1 = i1 >= vertW * vertW ? i1 % vertW * vertW : i1;
					i2 = i2 >= vertW * vertW ? i2 % vertW * vertW : i2;
					vecta = terH(xM / 10, 1 - (zM / 10), vertexes[i1], vect1,
							vect2);
				}
				// If verts is over max then it brings it back to the beginning

				System.out.println("i0: " + i0);
				System.out.println("i1: " + i1);
				System.out.println("i2: " + i2);

				Vect3d.sayVect("vecta", vecta);
				// unit1.setTranslate(vecta);
				unit1.setY(vecta[1]);
			}
		}
	}

	// all axis aligned.
	private float[] terH(float x, float y, float[] vert, float[] v0, float[] v1) {
		float[] e0 = Vect3d.vectMultScalar(x, v0);
		float[] e1 = Vect3d.vectMultScalar(y, v1);
		return Vect3d.vectAdd(vert, e0, e1);
	}

	private float[] getLoc(float hyp, float angleX, float angleY) {
		float downscale = (float) Math.cos(angleX);
		float z = (float) Math.cos(angleY) * hyp * downscale;
		float x = (float) Math.sin(angleY) * hyp * downscale;

		float y = (float) Math.sin(angleX) * hyp;
		// Rotates the projected point around the x axis
		// float[] xz = Vect2d.rotate(xy[0], 0, angleY);
		// float[] loc = new float[] { -xy[0], xy[1], xz[1] };
		// float[] loc = new float[] { xy[0], xy[1], 00 };
		// float[] loc = new float[] { xz[0], 0, xz[1] };
		float[] loc = new float[] { x, y, z };
		return loc;
	}

	private float[] getLoc(float[] all, float rotX, float rotY) {
		if (all[0] == 0 && all[1] == 0 && all[2] == 0) {
			return all;
		}
		float downscale = (float) Math.cos(JaMa.theaAdd(all[1], rotX));
		float z = (float) Math.cos(JaMa.theaAdd(all[2], rotY)) * all[0]
				* downscale;
		float x = (float) Math.sin(JaMa.theaAdd(all[2], rotY)) * all[0]
				* downscale;
		float y = (float) Math.sin(JaMa.theaAdd(all[1], rotX)) * all[0];

		float[] loc = new float[] { x, y, z };
		Vect3d.sayVect("locb", loc);
		return loc;
	}

	// x, z = neither
	// x, y = neither
	// z = x
	// x, y, z = y

	// 0, -1 = 0, 1 ( ? ? ? ? )

	// 0,0-1 -> 0,0,1

	/**
	 * New
	 */

	// 0, .5, -1 => 0, .5, 1

	// How to get y thea of a 3d point.

	public static float[] rot3d(float[] loc, float rotX, float rotY) {
		if (loc[0] == 0 && loc[1] == 0 && loc[2] == 0) {
			return new float[] { 0, 0, 0 };
		}
		Vect3d.sayVect("loca", loc);
		// hyp, angX, angY;
		float[] all = new float[4];
		// all[0] = Vect3d.norm(loc);
		all[0] = Vect2d.norm(loc[2], loc[1]);
		all[1] = Vect2d.norm(loc[0], loc[2]);
		// all[1] = (float) Math.asin(loc[1] / all[0]);
		// all[2] = (float) Vect2d.pTT(loc[2], loc[1]);
		// all[2] = (float) Math.atan(loc[0] / loc[2]);
		all[3] = (float) Vect2d.pTT(loc[0], loc[2]);

		// Vect3d.sayVect("all", all);
		System.out.println("all: ( " + all[0] + ", " + all[1] + ", " + all[2]
				+ ", " + all[3] + " )");

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
		// float[] xz = Vect2d.rotate(xy[0], 0, angleY);
		// float[] loc = new float[] { -xy[0], xy[1], xz[1] };
		// float[] loc = new float[] { xy[0], xy[1], 00 };
		// float[] loc = new float[] { xz[0], 0, xz[1] };
		float[] loca = new float[] { x, y, z };
		Vect3d.sayVect("locb", loca);
		System.out.println("|locb|: " + Vect3d.norm(loca));
		return loca;
	}

	public static float[] rot3dOld2(float[] loc, float rotX, float rotY) {
		if (loc[0] == 0 && loc[1] == 0 && loc[2] == 0) {
			return new float[] { 0, 0, 0 };
		}
		Vect3d.sayVect("loca", loc);
		// hyp, angX, angY;
		float[] all = new float[4];
		// all[0] = Vect3d.norm(loc);
		all[0] = Vect2d.norm(loc[2], loc[1]);
		all[1] = Vect2d.norm(loc[0], loc[2]);
		// all[1] = (float) Math.asin(loc[1] / all[0]);
		all[2] = (float) Vect2d.pTT(loc[2], loc[1]);
		// all[2] = (float) Math.atan(loc[0] / loc[2]);
		all[3] = (float) Vect2d.pTT(loc[0], loc[2]);

		// Vect3d.sayVect("all", all);
		System.out.println("all: ( " + all[0] + ", " + all[1] + ", " + all[2]
				+ ", " + all[3] + " )");

		/*-
		 * yz
		 * y sin
		 * sin of theaY  gives Z
		 * sin of theaX givesY
		 */

		// all[2] = Vect2d.pointToThea(loc[0], loc[2]);
		// float downscale = (float) Math.cos(all[1] + rotX);
		// float z = (float) Math.sin(all[3] + rotY) * all[1];
		float x = (float) Math.cos(all[3] + rotY) * all[1];

		float y = (float) Math.sin(all[2] + rotX) * all[0];
		float z = (float) Math.cos(all[2] + rotX) * all[0];
		// Rotates the projected point around the x axis
		// float[] xz = Vect2d.rotate(xy[0], 0, angleY);
		// float[] loc = new float[] { -xy[0], xy[1], xz[1] };
		// float[] loc = new float[] { xy[0], xy[1], 00 };
		// float[] loc = new float[] { xz[0], 0, xz[1] };
		float[] loca = new float[] { x, y, z };
		Vect3d.sayVect("locb", loca);
		System.out.println("|locb|: " + Vect3d.norm(loca));
		return loca;
	}

	public static float[] rot3dOld1(float[] loc, float rotX, float rotY) {
		if (loc[0] == 0 && loc[1] == 0 && loc[2] == 0) {
			return new float[] { 0, 0, 0 };
		}
		Vect3d.sayVect("loca", loc);
		// hyp, angX, angY;
		float[] all = new float[3];
		all[0] = Vect3d.norm(loc);
		all[1] = (float) Math.asin(loc[1] / all[0]);
		// all[2] = (float) Math.atan(loc[0] / loc[2]);
		all[2] = (float) Vect2d.pTT(loc[0], loc[2]);

		Vect3d.sayVect("all", all);

		// all[2] = Vect2d.pointToThea(loc[0], loc[2]);
		float downscale = (float) Math.cos(all[1] + rotX);
		float z = (float) Math.sin(all[2] + rotY) * all[0] * downscale;
		float x = (float) Math.cos(all[2] + rotY) * all[0] * downscale;

		float y = (float) Math.sin(all[1] + rotX) * all[0];
		// Rotates the projected point around the x axis
		// float[] xz = Vect2d.rotate(xy[0], 0, angleY);
		// float[] loc = new float[] { -xy[0], xy[1], xz[1] };
		// float[] loc = new float[] { xy[0], xy[1], 00 };
		// float[] loc = new float[] { xz[0], 0, xz[1] };
		float[] loca = new float[] { x, y, z };
		Vect3d.sayVect("locb", loca);
		System.out.println("|locb|: " + Vect3d.norm(loca));
		return loca;
	}

	private float[] decyphLoc(float[] loc) {
		if (loc[0] == 0 && loc[1] == 0 && loc[2] == 0) {
			return new float[] { 0, 0, 0 };
		}
		Vect3d.sayVect("loca", loc);
		// hyp, angX, angY;
		float[] all = new float[3];
		all[0] = Vect3d.norm(loc);
		all[1] = (float) Math.asin(loc[1] / all[0]);
		boolean neg = false;
		if (loc[0] < 0 && loc[2] < 0) {
			neg = true;
		}
		if (loc[2] != 0) {
			all[2] = (float) Math.atan(loc[0] / loc[2]);
			if (neg) {
				all[2] = -all[2];
			}
			// System.out.println("loc[0] / loc[2]: " + (loc[0] / loc[2]));
		} else if (loc[0] == 0) {
			all[2] = 0;
		} else {
			all[2] = (float) Math.PI / 2;
		}

		if (loc[2] < 0) {
			// all[2] += (float) Math.PI;
		}
		// Vect3d.sayVect("all", all);

		return all;
	}

	/**
	 * Converting height map to verts and faces.
	 */

	private float[][] conVert(int[][] hmap) {
		float[][] verts = new float[hmap.length * hmap[0].length][3];
		for (int x = 0; x < hmap.length; x++) {
			for (int z = 0; z < hmap[x].length; z++) {
				verts[x * hmap.length + z][0] = x;
				verts[x * hmap.length + z][1] = hmap[x][z];
				verts[x * hmap.length + z][2] = z;
			}
		}
		// for (int i = 0; i < verts.length; i++) {
		// Vect3d.sayVect("" + i, verts[i]);
		// }
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

				System.out.println("[(x(" + x + ") * (w(" + w + ") -1) + z("
						+ z + ") * 2)]: " + (x * (w - 1) + z) * 2);
				System.out.println("[(x * (w -1) + z) * 2 + 1]: "
						+ ((x * (w - 1) + z) * 2 + 1));

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

	private void makeTerrainMod() {
		System.out.print("int[][] faces = { ");
		int w = 13;
		int h = 13;
		for (int x = 0; x < w - 1; x++) {
			for (int z = 0; z < h - 1; z++) {
				int i0 = x * w + z;
				int i1 = x * w + z + 1;
				int i2 = (x + 1) * w + z + 1;

				int i3 = (x + 1) * w + z;
				System.out.print("{" + i0 + ", " + i2 + ", " + i1 + "}, {" + i0
						+ ", " + i3 + ", " + i2 + "}");
				if (!(x == w - 2 && z == h - 2)) {
					System.out.print(", ");
				}
			}
		}
		System.out.print(" };");
		System.out.println();
	}

	private void makeCylinder() {
		// for (int i = 0; i < 8; i++) {
		// System.out.print(",{" + (i + 1) + "," + (i + 10) + "," + (i + 9)
		// + "}");
		// System.out.print(",{" + (i + 1) + "," + (i + 2) + "," + (i + 10)
		// + "}");
		// }
		// for (int i = 0; i < 8; i++) {
		// System.out.print(",{" + (24 - i) + "," + 25 + "," + (23 - i) + "}");
		// }
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
}
