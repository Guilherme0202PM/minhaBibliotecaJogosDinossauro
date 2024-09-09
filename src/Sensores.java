import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;

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

    public boolean tocandoObjeto(Rectangle objeto1, Rectangle objeto2) {
        return objeto1.intersects(objeto2);
    }
}
