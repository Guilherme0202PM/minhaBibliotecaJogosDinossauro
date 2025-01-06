import java.awt.*;
import java.awt.event.KeyEvent;

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

    public boolean verificarColisao2(CriaObjeto obj1, CriaObjeto obj2) {
        // Ignorar colisão entre player e player2
        if ((obj1 instanceof Player && obj2 instanceof PlayerIA) ||
                (obj1 instanceof PlayerIA && obj2 instanceof Player)) {
            return false;
        }

        if ((obj1 instanceof Chao && obj2 instanceof PlayerIA) ||
                (obj1 instanceof PlayerIA && obj2 instanceof Chao)) {
            return false;
        }

        if (obj1 instanceof PlayerIA && obj2 instanceof PlayerIA) {
            return false;
        }

        return obj1.getRect().intersects(obj2.getRect());
    }

    // Metodo que desenha a area de identificação (contorno)
    public void CriaAreaIdentificacao(Graphics g, CriaObjeto objeto) {
        Rectangle areaIdentificacao = objeto.getRect();

        //Variavel para dobrar ou triplicar o tamanho
        int ajuste = 3;

        // Aumentando o tamanho da área de identificação
        int novaLargura = areaIdentificacao.width * ajuste;
        int novaAltura = areaIdentificacao.height * ajuste;

        //Variavel que centraliza o retangulo com base na area do dinossauro
        int ajusteCentralizacao = 25 * (ajuste-1);

        // Desenhando o retângulo com a área de identificação centralizada
        g.setColor(Color.GREEN); // Cor do contorno
        g.drawRect(areaIdentificacao.x-ajusteCentralizacao, areaIdentificacao.y-ajusteCentralizacao, novaLargura, novaAltura);
    }
}