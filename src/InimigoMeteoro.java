import java.awt.*;

public class InimigoMeteoro extends Inimigo {

    public InimigoMeteoro(int x, int y, int largura, int altura, String nomeImagem, int velocidadeX, int velocidadeY, Movimento movimento, Sensores sensores, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem, velocidadeX, velocidadeY, movimento, sensores, janela);
    }
}
