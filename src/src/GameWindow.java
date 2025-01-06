import javax.swing.*;

//public class GameWindow extends JFrame implements MouseMotionListener {
public class GameWindow extends JFrame{
    private GamePanel gamePanel;
    private Movimento movimento; // Cria uma instância da classe Movimento


    public GameWindow() {
        gamePanel = new GamePanel();
        add(gamePanel);
        setTitle("Meu Jogo");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        //addMouseMotionListener(this); // Adiciona o listener à janela
        movimento = new Movimento(); // Cria uma instância da classe Movimento
    }

    public void adicionarObjeto(CriaObjeto objeto) {
        gamePanel.adicionarObjeto(objeto);
    }

    public void removerObjeto(CriaObjeto objeto) {
        gamePanel.removeObjeto(objeto);
    }

    public void setFundo(Fundo fundo) {
        gamePanel.setFundo(fundo);
    }

    public void addComponentToGamePanel(JComponent component) {
        gamePanel.add(component);
    }

    public int getLarguraTela() {
        return getWidth();
    }

    public int getAlturaTela() {
        return getHeight();
    }
}