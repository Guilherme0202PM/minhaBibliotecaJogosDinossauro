import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        GameWindow janela = new GameWindow();
        Som som = new Som("Meow.wav");

        Movimento movimento = new Movimento();
        Sensores sensores = new Sensores(janela);

        Fundo fundo = new Fundo(600, 600, "Fundo.png");
        janela.setFundo(fundo); // Define o fundo no GamePanel

        int pontuacaoAlvo = 100;
        int pontuacao = 0;

        Player player = new Player(50, 50, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
        janela.adicionarObjeto(player);
        player.adicionarListener();

        // Criação de um vetor para armazenar múltiplos PlayerIA
        int numPlayers = 5; // Número de PlayerIA
        PlayerIA[] player2Array = new PlayerIA[numPlayers];
        RedeNeuralTeste2[] redesNeurais = new RedeNeuralTeste2[numPlayers]; // Array para armazenar redes neurais

        for (int i = 0; i < numPlayers; i++) {
            int posX = 200 + i * 60; // Posicione-os com um espaçamento entre si
            player2Array[i] = new PlayerIA(posX, 50, 50, 50, "dinoIA andandoo_andando_0.png", movimento, sensores, som, janela);
            janela.adicionarObjeto(player2Array[i]); // Adiciona o PlayerIA à janela
            player2Array[i].adicionarListener();
            redesNeurais[i] = new RedeNeuralTeste2(4, 6, 2); // Configure a rede neural conforme necessário
        }

        int maxInimigos = pontuacaoAlvo;
        Inimigo[] inimigos = new Inimigo[maxInimigos];
        Random random = new Random();
        for (int i = 0; i < maxInimigos; i++) {
            int tipoInimigo = random.nextInt(2);
            if (tipoInimigo == 0) {
                inimigos[i] = new InimigoTerrestre(1000, 350, 70, 50, "triceraptor_0.png", -5, 0, movimento, sensores, janela);
            } else {
                inimigos[i] = new InimigoVoador(1000, 320, 70, 50, "pterodáctilo_0.png", -5, 0, movimento, sensores, janela);
            }
            janela.adicionarObjeto(inimigos[i]);
        }

        // Geração de múltiplos blocos de chão
        int larguraChao = 500; // Largura do chão
        int alturaChao = 200;   // Altura do chão
        int numeroDeChao = 3;  // Número de blocos de chão
        Chao[] chaoBlocos = new Chao[numeroDeChao];

        for (int i = 0; i < numeroDeChao; i++) {
            chaoBlocos[i] = new Chao(i * larguraChao, 400, larguraChao, alturaChao); // Posição inicial dos blocos
            janela.adicionarObjeto(chaoBlocos[i]);
        }

        JLabel pontuacaoLabel = new JLabel("Pontuacao: 0");
        pontuacaoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        pontuacaoLabel.setForeground(Color.BLACK);
        pontuacaoLabel.setBounds(30, 30, 200, 30);
        janela.addComponentToGamePanel(pontuacaoLabel);

        long startTime = System.currentTimeMillis();

        int limiteProximidade = 80; // Defina um limite adequado para a proximidade

        while (true) {
            for (int i = 0; i < maxInimigos; i++) {
                Inimigo inimigo = inimigos[i];
                if (inimigo != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - startTime >= i * 2000) {
                        inimigo.atualizar();
                    }

                    if (sensores.verificarColisao(player, inimigo)) {
                        janela.removerObjeto(inimigo);
                        inimigos[i] = null;
                        System.out.println("Colisão detectada! Inimigo removido.");
                    } else if (inimigo.getRect().x < player.getRect().x) {
                        pontuacao++;
                        System.out.println("Pontuação: " + pontuacao);
                        pontuacaoLabel.setText("Pontuacao: " + pontuacao);

                        if (inimigo.getRect().x < -inimigo.getRect().width) {
                            janela.removerObjeto(inimigo);
                            inimigos[i] = null;
                        }
                    }

                    // Atualizando a interação com os PlayerIA
                    for (int j = 0; j < numPlayers; j++) {
                        PlayerIA player2 = player2Array[j];

                        if (sensores.analisarProximidade(player2, inimigo, limiteProximidade)) {
                            // Verifica se a altura/pontoY do inimigo é igual a 350
                            if (inimigo.getY() == 350) {
                                // Simula o "apertar" da tecla espaço para o player pular
                                player2.apertarEspaco();
                                // Se houver colisão direta entre o player2 e o inimigo
                                if (sensores.verificarColisao(player2, inimigo)) {
                                    janela.removerObjeto(player2);
                                    System.out.println("Colisão detectada! Inimigo removido.");
                                }
                            } else if (inimigo.getY() < 350) {
                                // Simula o "apertar" da tecla S para o player abaixar
                                player2.apertarS();
                                // Se houver colisão direta entre o player2 e o inimigo
                                if (sensores.verificarColisao(player2, inimigo)) {
                                    janela.removerObjeto(player2);
                                    System.out.println("Colisão detectada! Inimigo removido.");
                                }
                            }
                        }
                    }
                }
            }

            // Atualiza a posição do chão
            for (Chao chao : chaoBlocos) {
                chao.setX(chao.getX() - 5); // Move o chão para a esquerda
                if (chao.getX() < -larguraChao) {
                    chao.setX(larguraChao * (numeroDeChao - 1)); // Reposiciona à direita
                }
            }

            // Atualiza a posição do player e aplica gravidade
            movimento.aplicarGravidade(player, chaoBlocos[0]); // Use o primeiro bloco como referência para gravidade
            movimento.controlarSalto(player);

            // Atualiza a posição dos PlayerIA
            for (int i = 0; i < numPlayers; i++) {
                movimento.aplicarGravidade(player2Array[i], chaoBlocos[0]);
                movimento.controlarSalto(player2Array[i]);
            }

            pontuacaoLabel.setText("Pontuacao: " + pontuacao);
            janela.repaint();
            try {
                Thread.sleep(16); // Aproximadamente 60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
