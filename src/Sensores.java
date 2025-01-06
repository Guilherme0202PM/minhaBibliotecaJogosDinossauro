import java.awt.*;

public class Sensores {
    private GameWindow janela;

    public Sensores(GameWindow janela) {
        this.janela = janela;
    }

    public boolean tocandoPonteiroMouse(Rectangle objeto) {
        Point ponteiroMouse = MouseInfo.getPointerInfo().getLocation();
        return objeto.contains(ponteiroMouse);
    }

    public boolean tocandoBorda(CriaObjeto objeto) {
        Rectangle objetoRect = objeto.getRect();
        Rectangle tela = new Rectangle(0, 0, janela.getContentPane().getWidth(), janela.getContentPane().getHeight());
        return !tela.contains(objetoRect);
    }

    public boolean verificarColisao(CriaObjeto obj1, CriaObjeto obj2) {
        // Ignorar colisão entre player e player2
        if ((obj1 instanceof Player && obj2 instanceof PlayerIA) ||
                (obj1 instanceof PlayerIA && obj2 instanceof Player)) {
            return false;
        }
        return obj1.getRect().intersects(obj2.getRect());
    }

    // Metodo que desenha a area de identificação (contorno)
    public void AreaIdentificacao(Graphics g, CriaObjeto objeto) {
        Rectangle areaIdentificacao = objeto.getRect();
        g.setColor(Color.RED); // Cor do contorno
        g.drawRect(areaIdentificacao.x, areaIdentificacao.y, areaIdentificacao.width, areaIdentificacao.height);
    }
}
