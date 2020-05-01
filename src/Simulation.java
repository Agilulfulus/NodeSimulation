import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import javax.swing.*;

public class Simulation {
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 600;
	public static final int NODE_NUMBER = 100;
	public static final double MAX_NODE_DISTANCE = 100;
	public static final double SLOW_PERCENT = 0.2;

	public static Random rand;

	public ArrayList<Node> nodes, path, path_raw;
	public JPanel canvas = null;

	public static void main(String[] args) {
		rand = new Random();

		JFrame frame = new JFrame("Shortest Path");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Simulation simulation = new Simulation(frame);
		frame.pack();
		frame.addKeyListener(new Keyboard(simulation));
		frame.setVisible(true);
	}

	public Simulation(JFrame frame) {
		init();
		frame.add(canvas);
	}

	public void init() {
		long init_time = System.currentTimeMillis();
		System.out.print("Generating map...");
		nodes = generate();
		System.out.println(" (" + (System.currentTimeMillis() - init_time) + "ms)");

		init_time = System.currentTimeMillis();
		System.out.print("Generating paths...");
		path = findPath(nodes.get(0), nodes.get(1), nodes);
		path_raw = findPathRaw(nodes.get(0), nodes.get(1), nodes);
		System.out.println(" (" + (System.currentTimeMillis() - init_time) + "ms)");

		init_time = System.currentTimeMillis();
		System.out.print("Drawing Nodes...");
		if (canvas == null)
			canvas = new Visualization(nodes, path, path_raw, WIDTH, HEIGHT);
		else {
			((Visualization) canvas).updateMap(nodes, path, path_raw);
			canvas.repaint(0, 0, WIDTH, HEIGHT);
		}
		System.out.println(" (" + (System.currentTimeMillis() - init_time) + "ms)");
		System.out.println("Done.");
	}

	public ArrayList<Node> generate() {
		ArrayList<Node> nodes = new ArrayList<Node>();
		Node start = new Node(50, rand.nextInt(HEIGHT - 100) + 50, false);
		Node end = new Node(WIDTH - 50, rand.nextInt(HEIGHT - 100) + 50, false);
		nodes.add(start);
		nodes.add(end);
		for (int i = 0; i < NODE_NUMBER; i++) {
			int x = 0;
			int y = 0;
			boolean flag = false;
			while (!flag) {
				x = rand.nextInt(WIDTH - 240) + 120;
				y = rand.nextInt(HEIGHT - 40) + 20;
				flag = true;
				for (Node n : nodes) {
					if (Math.sqrt(Math.pow(x - n.getX(), 2) + Math.pow(y - n.getY(), 2)) < 40)
						flag = false;
				}
			}
			nodes.add(new Node(x, y, rand.nextDouble() <= SLOW_PERCENT ? true : false));
		}
		while (true) {
			Node n1 = null;
			Node n2 = null;
			for (Node n : nodes) {
				for (Node e : nodes) {
					if (n == e)
						continue;
					if (n.distance(e) > MAX_NODE_DISTANCE)// && n != start && n != end && e != start && e != end)
						continue;
					if (!n.isNeighbor(e) && !intersect(n, e, nodes)) {
						if (n1 == null) {
							n1 = n;
							n2 = e;
						} else if (n.distance(e) < n1.distance(n2)) {
							n1 = n;
							n2 = e;
						}
					}
				}
			}
			if (n1 == null)
				break;
			n1.addNeighbor(n2);
		}
		if (start.getNeighbors().isEmpty()) {
			Node closest = null;
			for (Node n : nodes) {
				if (n == start)
					continue;
				if (closest == null)
					closest = n;
				else if (n.distance(start) < closest.distance(start))
					closest = n;
			}
			start.addNeighbor(closest);
		}
		if (end.getNeighbors().isEmpty()) {
			Node closest = null;
			for (Node n : nodes) {
				if (n == end)
					continue;
				if (closest == null)
					closest = n;
				else if (n.distance(end) < closest.distance(end))
					closest = n;
			}
			end.addNeighbor(closest);
		}
		return nodes;
	}

	public boolean intersect(Node a, Node b, ArrayList<Node> nodes) {
		for (Node n : nodes) {
			if (n == a || n == b)
				continue;
			for (Node c : n.getNeighbors()) {
				if (c == a || c == b)
					continue;
				boolean i = Line2D.linesIntersect(n.getX(), n.getY(), c.getX(), c.getY(), a.getX(), a.getY(), b.getX(),
						b.getY());
				if (i)
					return true;
			}
		}
		return false;
	}

	public static ArrayList<Node> findPath(Node start, Node end, ArrayList<Node> nodes) {
		HashMap<Node, Double> g_score = new HashMap<Node, Double>();
		HashMap<Node, Double> f_score = new HashMap<Node, Double>();
		HashMap<Node, Node> prev = new HashMap<Node, Node>();
		ArrayList<Node> close_set = new ArrayList<Node>();
		ArrayList<Node> open_set = new ArrayList<Node>();
		open_set.add(start);

		for (Node n : nodes) {
			g_score.put(n, Double.MAX_VALUE);
			f_score.put(n, Double.MAX_VALUE);
			prev.put(n, null);
		}
		g_score.put(start, 0.0);
		f_score.put(start, start.distance(end));

		while (true) {
			Node c = null;
			for (Node n : open_set) {
				if (c == null)
					c = n;
				else if (f_score.get(n) < f_score.get(c))
					c = n;
			}
			if (c != null) {
				close_set.add(c);
				open_set.remove(c);
				for (Node n : c.getNeighbors()) {
					// COST gSCORE
					double tent_g = g_score.get(c) + c.cost(n);
					if (tent_g < g_score.get(n)) {
						g_score.put(n, tent_g);
						f_score.put(n, tent_g + n.distance(end));
						prev.put(n, c);
						if (!close_set.contains(n))
							open_set.add(n);
					}
				}
			} else
				break;
		}

		ArrayList<Node> path = new ArrayList<Node>();

		Node c = end;
		while (c != null) {
			path.add(c);
			c = prev.get(c);
		}

		Collections.reverse(path);

		return path;
	}

	public static ArrayList<Node> findPathRaw(Node start, Node end, ArrayList<Node> nodes) {
		HashMap<Node, Double> g_score = new HashMap<Node, Double>();
		HashMap<Node, Double> f_score = new HashMap<Node, Double>();
		HashMap<Node, Node> prev = new HashMap<Node, Node>();
		ArrayList<Node> close_set = new ArrayList<Node>();
		ArrayList<Node> open_set = new ArrayList<Node>();
		open_set.add(start);

		for (Node n : nodes) {
			g_score.put(n, Double.MAX_VALUE);
			f_score.put(n, Double.MAX_VALUE);
			prev.put(n, null);
		}
		g_score.put(start, 0.0);
		f_score.put(start, start.distance(end));

		while (true) {
			Node c = null;
			for (Node n : open_set) {
				if (c == null)
					c = n;
				else if (f_score.get(n) < f_score.get(c))
					c = n;
			}
			if (c != null) {
				close_set.add(c);
				open_set.remove(c);
				for (Node n : c.getNeighbors()) {
					// DISTANCE gSCORE
					double tent_g = g_score.get(c) + c.distance(n);
					if (tent_g < g_score.get(n)) {
						g_score.put(n, tent_g);
						f_score.put(n, tent_g + n.distance(end));
						prev.put(n, c);
						if (!close_set.contains(n))
							open_set.add(n);
					}
				}
			} else
				break;
		}

		ArrayList<Node> path = new ArrayList<Node>();

		Node c = end;
		while (c != null) {
			path.add(c);
			c = prev.get(c);
		}

		Collections.reverse(path);

		return path;
	}
}