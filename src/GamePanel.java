import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private List<CriaObjeto> objetos;

    public GamePanel() {
        objetos = new ArrayList<>();
    }


    public void adicionarObjeto(CriaObjeto objeto) {
        objetos.add(objeto);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (CriaObjeto objeto : objetos) {
            objeto.desenhar(g);

        }
    }
}
