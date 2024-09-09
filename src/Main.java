import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        GameWindow janela = new GameWindow();
        Som som = new Som("Meow.wav");

        Movimento movimento = new Movimento();
        Sensores sensores = new Sensores(janela);

        // Cria uma instância de Player
        Player player = new Player(50, 50, 50,50, "Personagem1.png", movimento, sensores, som, janela);

        // Adiciona o Player à janela
        janela.adicionarObjeto(player);

        // Configura o listener para o Player
        player.adicionarListener();

        // Cria alguns inimigos e os adiciona à janela
        Inimigo inimigo2 = new Inimigo(50, 50, 20, 20, "naoExiste.png", -2, -3, movimento, sensores, janela);
        Inimigo inimigo3 = new Inimigo(50, 200, 20, 20, "teste2.png", 0, -5, movimento, sensores, janela);
        //Inimigo inimigo4 = new Inimigo(50, 50, 20, "teste2.png", 5, 0, movimento, sensores);
       // Inimigo inimigo5 = new Inimigo(200, 200, 20, "teste2.png", -4, 4, movimento, sensores);
       // Inimigo inimigo6 = new Inimigo(200, 50, 20, "teste2.png", -2, 2, movimento, sensores);

        janela.adicionarObjeto(inimigo2);
        janela.adicionarObjeto(inimigo3);
        //janela.adicionarObjeto(inimigo4);
       // janela.adicionarObjeto(inimigo5);
      //  janela.adicionarObjeto(inimigo6);

        // Cria um chão (Plataforma do tipo Chao)
        Chao chao = new Chao(0, 400, 600, 50); // Chão que cobre a largura da janela

        // Adiciona o chão à janela
        janela.adicionarObjeto(chao);

        // Exemplo de movimentação do jogador
        movimento.goDeslizarPosicao(player, 300, 300, 2);
        movimento.goDeslizarPosicao(inimigo2, 700, 50, 3);

        // loop infinito para manter a janela aberta
        while (true) {
            // Atualiza a posição dos inimigos
            //Inimigo[] inimigos = new Inimigo[] {inimigo2, inimigo3, inimigo4, inimigo5, inimigo6};
            Inimigo[] inimigos = new Inimigo[] {inimigo2, inimigo3};
            movimento.aplicarGravidade(player, chao); // Aplica gravidade no player
            movimento.controlarSalto(player); // Permite o controle da altura do salto

            for (Inimigo inimigo : inimigos) {
                inimigo.atualizar();
            }

            janela.repaint();
            try {
                Thread.sleep(16); // 60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
