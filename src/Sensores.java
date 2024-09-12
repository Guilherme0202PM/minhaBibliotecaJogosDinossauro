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

    public boolean verificarColisao(CriaObjeto obj1, CriaObjeto obj2) {
        return obj1.getRect().intersects(obj2.getRect());
    }


    // Novo metodo para verificar se dois objetos estão colidindo
    public boolean tocandoObjeto(CriaObjeto obj1, CriaObjeto obj2) {
        return obj1.getRect().intersects(obj2.getRect());
    }}
