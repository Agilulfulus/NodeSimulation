import java.awt.*;
import java.util.ArrayList;

import javax.swing.JPanel;

import java.awt.Graphics2D;

public class Visualization extends JPanel {
	public ArrayList<Node> nodes, path, path_raw;
	public Node start, end;
	public int WIDTH, HEIGHT;
	
	public Visualization(ArrayList<Node> nodes, ArrayList<Node> path, ArrayList<Node> path_raw, int WIDTH, int HEIGHT) {
		updateMap(nodes, path, path_raw);
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	public void updateMap(ArrayList<Node> nodes, ArrayList<Node> path, ArrayList<Node> path_raw) {
		this.nodes = nodes;
		this.path = path;
		this.path_raw = path_raw;
		this.start = nodes.get(0);
		this.end = nodes.get(1);
	}

    public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 0, 100, HEIGHT);
		g2d.fillRect(WIDTH - 100, 0, 100, HEIGHT);
		ArrayList<Node> drawn = new ArrayList<Node>();
		for (Node n : nodes) {
			drawn.add(n);
			n.draw(Color.BLACK, Color.WHITE, g2d, true, drawn);
		}
		if (path.size() == 1) {
			g.setColor(Color.BLACK);
			Font f = new Font("Consolas", Font.PLAIN, 64);
			g2d.setFont(f); 
			FontMetrics metrics = g2d.getFontMetrics(f);
			g2d.drawString("No Path Found", WIDTH / 2 - metrics.stringWidth("No Path Found") / 2, HEIGHT / 2);
		} else {
			Font f = new Font("Consolas", Font.PLAIN, 12);
			g2d.setFont(f);
			Node last = null;
			double total_cost = 0;
			double total_dist = 0;
			drawn = new ArrayList<Node>();
			for (Node n : path) {
				if (last != null) {
					g2d.setColor(new Color(0, 255, 0, 100));
					g2d.setStroke(new BasicStroke(3f));
					g2d.drawLine(n.getX(), n.getY(), last.getX(), last.getY());
					total_cost += n.cost(last);
					total_dist += n.distance(last);
				}
				drawn.add(n);
				n.draw(new Color(0, 255, 0, 100), new Color(0, 255, 0, 100), g2d, false, drawn);
				last = n;
			}
			g2d.setColor(Color.BLACK);
			g2d.drawString("Optimal/Cost: (D-" + String.format("%.1f", total_dist) + ", C-" + String.format("%.1f", total_cost) + ")", 10, HEIGHT - 30);

			last = null;
			total_dist = 0;
			total_cost = 0;
			drawn = new ArrayList<Node>();
			for (Node n : path_raw) {
				if (last != null) {
					g2d.setColor(new Color(0, 0, 255, 100));
					g2d.setStroke(new BasicStroke(3f));
					g2d.drawLine(n.getX(), n.getY(), last.getX(), last.getY());
					total_dist += n.distance(last);
					total_cost += n.cost(last);
				}
				drawn.add(n);
				n.draw(new Color(0, 0, 255, 100), new Color(0, 0, 255, 100), g2d, false, drawn);
				last = n;
			}
			g2d.setColor(Color.BLACK);
			g2d.drawString("Optimal/Dist: (D-" + String.format("%.1f", total_dist) + ", C-" + String.format("%.1f", total_cost) + ")", 10, HEIGHT - 10);
		}
    }
}