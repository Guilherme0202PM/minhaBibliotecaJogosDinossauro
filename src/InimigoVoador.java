import java.awt.*;

public class InimigoVoador extends Inimigo {

    public InimigoVoador(int x, int y, int largura, int altura, String nomeImagem, int velocidadeX, int velocidadeY, Movimento movimento, Sensores sensores, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem, velocidadeX, velocidadeY, movimento, sensores, janela);
    }
}
