import java.awt.Color;
import java.awt.*;
import java.util.ArrayList;

public class Node {
	private int x, y;
	private ArrayList<Node> neighbors;
	private boolean slow;
	public Node(int x, int y, boolean slow) {
		this.x = x;
		this.y = y;
		this.slow = slow;
		neighbors = new ArrayList<Node>();
	}

	public boolean isSlow() {
		return slow;
	}

	public void addNeighbor(Node n) {
		if (!neighbors.contains(n))
			neighbors.add(n);
		if (!n.neighbors.contains(this))
			n.neighbors.add(this);
	}

	public ArrayList<Node> getNeighbors() {
		return neighbors;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isNeighbor(Node n) {
		return neighbors.contains(n);
	}

	public double distance(Node n) {
		return Math.sqrt(Math.pow(n.x - x, 2) + Math.pow(n.y - y, 2));
	}

	public double cost(Node n) {
		double d = distance(n);
		if (n.slow || slow)
			d *= 2;
		return d;
	}

	public static void drawRotate(Graphics2D g2d, double x, double y, double r, String text) 
	{    
		Font f = new Font("Consolas", Font.PLAIN, 8);
		g2d.setFont(f); 
		FontMetrics metrics = g2d.getFontMetrics(f);
		g2d.translate(x,y);
		g2d.rotate(r);
		g2d.drawString(text,-metrics.stringWidth(text) / 2,-2);
		g2d.rotate(-r);
		g2d.translate(-x,-y);
	}    

	public void draw(Color c, Color c2, Graphics2D g, boolean dn, ArrayList<Node> drawn) {
		if (dn) {
			g.setStroke(new BasicStroke(1f));
			for (Node n : neighbors) {
				if (drawn.contains(n))
					continue;
				if (cost(n) > distance(n))
					g.setColor(Color.RED);
				else
					g.setColor(c);
				drawRotate(g, (x + n.x) / 2, (y + n.y) / 2, Math.atan((double)(y - n.y) / (double)(x - n.x)), String.format("%.1f", cost(n)));
				g.drawLine(x, y, n.x, n.y);
			}
		}
		g.setColor(c2);
		g.fillOval(x - 10, y - 10, 20, 20);
		g.setStroke(new BasicStroke(2f));
		if (!slow)
			g.setColor(c);
		else
			g.setColor(Color.RED);
		g.drawOval(x - 10, y - 10, 20, 20);
	}
}