import java.awt.event.*;

public class Keyboard implements KeyListener {
	Simulation simulation;

	public Keyboard(Simulation simulation) {
		this.simulation = simulation;
	}

	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == ' ') {
			simulation.init();
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {}
}