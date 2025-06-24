import java.awt.*;

public class InimigoTerrestre extends Inimigo {

    public InimigoTerrestre(int x, int y, int largura, int altura, String nomeImagem, int velocidadeX, int velocidadeY, Movimento movimento, Sensores sensores, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem, velocidadeX, velocidadeY, movimento, sensores, janela);
    }

    // A subclasse herda o metodo atualizar() da classe pai Inimigo.
    // Se não for necessário alterar o comportamento, não precisa sobrescrever este método.
}
