package LavaBucket.tre;

import java.util.ArrayList;

import LavaBucket.lib.Vect3d;

public class Node {
	Node parent;
	ArrayList<Node> children;
	ArrayList<Model> mods;

	float[] translate;

	public Node(float[] translate) {
		this.translate = translate;
		children = new ArrayList<Node>();
		mods = new ArrayList<Model>();
	}

	public Node getParent() {
		return parent;
	}

	public void move(float[] translate) {
		this.translate = Vect3d.vectAdd(this.translate, translate);
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public void addChild(Node child) {
		children.add(child);
	}

	public void removeChild(Node child) {
		children.remove(child);
	}

	public void removeParent() {
		parent.removeChild(this);
		parent = null;
	}

	public ArrayList<Model> getMods() {
		return mods;
	}

	public void setMods(ArrayList<Model> mods) {
		this.mods = mods;
	}

	public void addMod(Model m) {
		mods.add(m);
	}

	public void removeMod(Model m) {
		mods.remove(m);
	}

	public float[] getTranslate() {
		return translate;
	}

	public void setTranslate(float[] translate) {
		this.translate = translate;
	}

	public void setY(float y) {
		translate[1] = y;
	}

}
