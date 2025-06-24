import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Deleta extends CriaObjeto {
    private Movimento movimento;
    private Sensores sensores;
    private Som som;
    private GameWindow janela;

    private int contagemEliminados = 0;


//    public Deleta(int x, int y, int largura, int altura, Movimento movimento, Sensores sensores, Som som, GameWindow janela) {
//        super(x, y, largura, altura);
//        this.movimento = movimento;
//        this.sensores = sensores;
//        this.som = som;
//        this.janela = janela;
//    }

    public Deleta(int x, int y, int largura, int altura, String nomeImagem, Movimento movimento, Sensores sensores, Som som, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem);
        this.movimento = movimento;
        this.sensores = sensores;
        this.som = som;
        this.janela = janela;
    }

    public void teleporte(int newX, int newY) {
        movimento.goPosicao(Deleta.this, newX, newY); // Inicia o salto do DeletaIA
    }

//    public void elimina(int contador) {
//        contagemEliminados = contador;
//        System.out.println("Colisão detectada! Deleta " + contador);
//
//        if (contador < 5) {
//            teleporte(550, 350);
//        } else {
//            teleporte(550, 50);
//        }
//    }
}