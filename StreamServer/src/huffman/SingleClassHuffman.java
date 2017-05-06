package huffman;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by João Paulo on 06/05/2017.
 */
public class SingleClassHuffman {
    public static void descomprimir (File comprimido, File descomprimido) throws IOException {
        if (!comprimido.exists())
            throw new IOException("Files doesn't exists");

        TheBitsInputHandler inputBits = new TheBitsInputHandler(new BufferedInputStream(new FileInputStream(comprimido)));
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(descomprimido))) {
            System.out.println("[Iniciado] - Leituta dos Codigos de Restauração");
            CodificadorDeSimbolos callOfDuty = lerCodigoReconstrucao(inputBits);
            System.out.println("[Completo] - Leitura dos Codigos de Reconstrução");

            System.out.println("[Iniciado] - Criação da Arvore Huffman");
            AArvoreDiferentona arvoreDiferentona = callOfDuty.gerarArvoreDiferentonaMelhorada();
            System.out.println("[Completo] - Criação da Arvore Huffman");

            System.out.println("[Iniciado] - Descompressão");
            descomprimir(arvoreDiferentona, inputBits, out);
            System.out.println("[Completo] - Descompressão");
        } catch (ItsWrongException e) {
            throw new IOException("Right into the memes");
        }
        finally {
            inputBits.close();

        }

    }

    private static CodificadorDeSimbolos lerCodigoReconstrucao(TheBitsInputHandler input) throws IOException, ItsWrongException {
        int[] codegos = new int[257];
        for (int i = 0; i < codegos.length; i++) {
            int val = 0;

            for (int j = 0; j < 8; j++)
                val = val << 1 | input.readNoEof();

            codegos[i] = val;
        }
        return new CodificadorDeSimbolos(codegos);
    }

    private static void descomprimir(AArvoreDiferentona arvore, TheBitsInputHandler input, OutputStream out) throws IOException, ItsWrongException {
        DecodificadorHuff dh = new DecodificadorHuff(arvore, input);

        while(true) {
            int simbolo = dh.read();
            if (simbolo == 256)  //Simbolo do final.. Lembra?
                break;
            out.write(simbolo);
        }
    }
}

class AArvoreDiferentona {
    public NodeBipolar raiz;
    private List<List<Integer>> codigos; //Uma matriz praticamente.... isso poderia ser um Map... Ou um Pair do C++...

    public AArvoreDiferentona(NodeBipolar raiz, int limiteSimbolos) {
        this.raiz = raiz;
        codigos = new ArrayList<List<Integer>>(); //Inicializa cada lista com null pq o java tava reclamando

        for (int i = 0; i < limiteSimbolos; i++)
            codigos.add(null);

        inicializadorDasArvore(raiz, new ArrayList<Integer>());
    }

    public void inicializadorDasArvore(AbstractNode no, ArrayList<Integer> binario) {
        if (no instanceof NodeBipolar) {
            NodeBipolar bipolar = (NodeBipolar)no;

            binario.add(0);
            inicializadorDasArvore(bipolar.esquerda, binario); //A criança descerá um degrau com o 0 na sua vida
            binario.remove(binario.size() - 1);

            binario.add(1);
            inicializadorDasArvore(bipolar.direita, binario); //A criança descerá um degrau com o 1 na sua vida
            binario.remove(binario.size() - 1);
        } else if (no instanceof Node) {
            Node node = (Node)no;
            codigos.set(node.simbolo, new ArrayList<Integer>(binario)); //O codigo recebe o binario do simbolo... RIP
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString("", raiz, sb);
        return sb.toString();
    }

    private void toString(String string, AbstractNode no, StringBuilder sb) {
        if (no instanceof NodeBipolar) {
            NodeBipolar internalNode = (NodeBipolar)no;
            toString(string + "0", internalNode.esquerda , sb);
            toString(string + "1", internalNode.direita, sb);
        } else if (no instanceof Node) {
            sb.append(String.format("Cod %s: Simbolo %d%n", string, ((Node)no).simbolo));
        }
    }

    public List<Integer> getCodigo(int simbolo) {
        return codigos.get(simbolo);
    }
}

class AbstractNode {
    public AbstractNode() { }
}

class Node extends AbstractNode {
    public int simbolo;

    public Node(int simbolo) {
        this.simbolo = simbolo;
    }
}

class NodeBipolar extends AbstractNode {
    public AbstractNode esquerda;
    public AbstractNode direita;

    public NodeBipolar(AbstractNode esquerda, AbstractNode direita) {
        this.esquerda = esquerda;
        this.direita = direita;
    }
}

class NodeFrequenciado implements Comparable<NodeFrequenciado>{
    public AbstractNode no;
    public int menorSimbolo;
    public long frequencia;

    public NodeFrequenciado(AbstractNode no, int menorSimbolo, long frequencia) {
        this.no = no;
        this.menorSimbolo = menorSimbolo;
        this.frequencia = frequencia;
    }

    @Override
    public int compareTo(NodeFrequenciado o) {
        if (frequencia < o.frequencia)
            return -1;
        else if (frequencia > o.frequencia)
            return 1;
        else if (menorSimbolo < o.menorSimbolo)
            return -1;
        else if (menorSimbolo > o.menorSimbolo)
            return 1;
        else
            return 0;
    }
}

class OReiDasFrequencia {
    public int[] frequencia;

    public OReiDasFrequencia(int[] frequencia) {
        this.frequencia = frequencia;
    }

    public void aumentarFrequencia(int bbyte) {
        frequencia[bbyte]++;
    }

    public AArvoreDiferentona gerarArvoreDasFrequencia() throws ItsWrongException {
        Queue<NodeFrequenciado> filinha = new PriorityQueue<NodeFrequenciado>(); //Fila que se organiza sozinha de acordo com um comparador (O compareTo)

        //Adiciona todo mundo com frequencia acima de 1 na sila do SUS
        for (int i = 0; i < frequencia.length; i++) {
            if (frequencia[i] > 0)
                filinha.add(new NodeFrequenciado(new Node(i), i, frequencia[i])); // O no é criado com o um valor que corresponde a sua posição no vetor, o menor simbolo é setado como o feito na criação, e a frequencia é setada como a atual
        }

        //Se não tem pelo menos 2 na fila do SUS, é preciso forçar que 2 existam! (PARA SER UMA FILA ORA!)
        for (int i = 0; i < frequencia.length && filinha.size() < 2; i++) {
            if (i >= frequencia.length || frequencia[i] == 0)
                filinha.add(new NodeFrequenciado(new Node(i), i, 0));
        }

        if (filinha.size() < 2)
            throw new ItsWrongException("Você quer comprimir algo sem tamanho... Você é bixao mesmo hein...");

        while (filinha.size() > 1) {
            NodeFrequenciado n1 = filinha.remove(); // pega 2 nos
            NodeFrequenciado n2 = filinha.remove();
            //E junta eles dois... QUE MASSA!
            filinha.add(new NodeFrequenciado(new NodeBipolar(n1.no, n2.no), Math.min(n1.menorSimbolo, n2.menorSimbolo), n1.frequencia + n2.frequencia));
        }
        //Na verdade, o elemento que resta dessa fila ja é a arvore... Mas vamos chamar isso de uma classe diferente para ficar mais bonito, wlw flws

        return new AArvoreDiferentona((NodeBipolar)filinha.remove().no, frequencia.length);
    }

    public static OReiDasFrequencia frequenciar(File arquivo) throws IOException {
        OReiDasFrequencia rei = new OReiDasFrequencia(new int[257]);
        InputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
        try {
            while (true) {
                int bbyte = entrada.read();
                if (bbyte == -1)
                    break;
                rei.aumentarFrequencia(bbyte); //Hehehe, a cada vez que ele aparece, ele aumenta +1 hue
            }
        } finally {
            entrada.close();
        }
        return rei;
    }

}

class CodificadorDeSimbolos {
    public int[] codegos; //Representa o tamanho dos codegos

    public CodificadorDeSimbolos(int[] codegos) {
        this.codegos = codegos.clone();
    }

    public CodificadorDeSimbolos(AArvoreDiferentona arvoreDiferente, int tamanho) {
        codegos = new int[tamanho];
        gerenciartamanhoDeCodegos(arvoreDiferente.raiz, 0); //A raiz tem tamanho 0 dela até ela, neh?
    }

    private void gerenciartamanhoDeCodegos(AbstractNode node, int tamanho) {
        if (node instanceof NodeBipolar) {
            NodeBipolar bipolar = (NodeBipolar)node;
            gerenciartamanhoDeCodegos(bipolar.esquerda, tamanho + 1);
            gerenciartamanhoDeCodegos(bipolar.direita , tamanho + 1);
        } else if (node instanceof Node) {
            Node no = (Node)node;
            int simbolo = no.simbolo;
            codegos[simbolo] = tamanho;
        }
    }

    public AArvoreDiferentona gerarArvoreDiferentonaMelhorada() throws ItsWrongException {
        List<AbstractNode> nudes = new ArrayList<AbstractNode>();

        for (int i = maiorValorArray(codegos); i >= 1; i--) { //Acha o garibaldo e vai dele até o primeiro elemento
            List<AbstractNode> maisNudes = new ArrayList<AbstractNode>();

            for (int j = 0; j < codegos.length; j++)
                if (codegos[j] == i) //Agrupador de frequencias de mesmo tamanho
                    maisNudes.add(new Node(j));

            for (int j = 0; j < nudes.size(); j += 2)
                maisNudes.add(new NodeBipolar(nudes.get(j), nudes.get(j + 1)));

            nudes = maisNudes;
            if (nudes.size() % 2 != 0)
                throw new ItsWrongException("Deu ruim!");
        }

        if (nudes.size() != 2)
            throw new ItsWrongException("Deu muito ruim");

        return new AArvoreDiferentona(new NodeBipolar(nudes.get(0), nudes.get(1)), codegos.length);
    }

    public int maiorValorArray(int[] vetor) {
        int resposta = vetor[0];
        for (int x : vetor)
            resposta = Math.max(x, resposta);
        return resposta;
    }

    public int getLimiteSimbolos() {
        return codegos.length;
    }

    public int getTamanhoDoCodigo(int i) {
        return codegos[i];
    }

}

class TheBitsOutputHandler {
    public OutputStream stream;
    public int bitsAtual;
    public int theFinalByte;

    public TheBitsOutputHandler(OutputStream stream) {
        this.stream = stream;
        bitsAtual = 0;
        theFinalByte = 0;
    }

    public void write(int bit) throws IOException {
        theFinalByte = theFinalByte << 1 | bit; //Desloca 1 casa e adiciona a criança no final
        bitsAtual++;

        if (bitsAtual == 8) {
            bitsAtual = 0;
            stream.write(theFinalByte);
        }
    }

    public void close() throws IOException {
        if (bitsAtual != 0)
            stream.write(theFinalByte);
        stream.close();
    }
}

class TheBitsInputHandler {
    public InputStream stream;
    public int bbyte;
    public int bitsRestantes;
    public boolean streamIsOver;

    public TheBitsInputHandler(InputStream stream) {
        this.stream = stream;
        bitsRestantes = 0;
        streamIsOver = false;
    }

    public int read() throws IOException {
        if (streamIsOver)
            return -1;
        if (bitsRestantes == 0) {
            bbyte = stream.read();
            if (bbyte == -1) {
                streamIsOver = true;
                return -1;
            }
            bitsRestantes = 8;
        }
        bitsRestantes--;

        return (bbyte >>> bitsRestantes) & 1;
    }

    public void close() throws IOException {
        stream.close();
    }

    public int readNoEof() throws IOException, ItsWrongException {
        int b = read();
        if (b != -1)
            return b;
        else
            throw new ItsWrongException("FIM DA STRIM");
    }

}

class ItsWrongException extends Exception {
    private static final long serialVersionUID = -9060664301936673176L;

    public ItsWrongException() {
        // TODO Auto-generated constructor stub
    }

    public ItsWrongException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public ItsWrongException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public ItsWrongException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    public ItsWrongException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
        // TODO Auto-generated constructor stub
    }

}

class DecodificadorHuff {
    public AArvoreDiferentona arvore;
    public TheBitsInputHandler input;

    public DecodificadorHuff(AArvoreDiferentona arvore, TheBitsInputHandler input) {
        this.arvore = arvore;
        this.input = input;
    }

    public int read() throws IOException, ItsWrongException {
        NodeBipolar noAtual = arvore.raiz;
        while (true) {
            int simb = input.readNoEof();
            AbstractNode node = null;
            if (simb == 0)
                node = noAtual.esquerda;
            else if (simb == 1)
                node = noAtual.direita;
//			else throw new AssertionError();

            if (node instanceof Node)
                return ((Node)node).simbolo;
            else if (node instanceof NodeBipolar)
                noAtual = (NodeBipolar)node;
        }
    }

}

class CodificadorHuff {
    public AArvoreDiferentona arvore;
    public TheBitsOutputHandler saida;

    public CodificadorHuff(AArvoreDiferentona arvore, TheBitsOutputHandler saida) {
        this.arvore = arvore;
        this.saida = saida;
    }

    public void write(int simbolo) throws IOException {
        List<Integer> bits = arvore.getCodigo(simbolo);
        // if bits ja foram salvos, apontar a posição
        for (int b : bits)
            saida.write(b);
    }

}

class CompressorTekpix {

    public static void comprimirArquivo(File arquivo, File destinoCompressao) throws ItsWrongException, IOException {
        if (!arquivo.exists())
            throw new ItsWrongException("Arquivo não existe...");
        System.out.println("[Iniciado] - Gerador de frequencias");
        OReiDasFrequencia frequenciador = OReiDasFrequencia.frequenciar(arquivo); //Frequencia de repetição de cada byte do arquivo
        frequenciador.aumentarFrequencia(256); //A frequencia 256 irá simbolizar o final do arquivo pois ele existe e deve ser respeitado
        System.out.println("[Completo] - Frequencias");

        System.out.println("[Iniciado] - Criador da Arvore de Frequencias");
        AArvoreDiferentona arvoreDiferente = frequenciador.gerarArvoreDasFrequencia(); // Cria uma arvore com as frequencias
        System.out.println("[Completo] - Arvore de frequencias");

        System.out.println("[Iniciado] - Codificação de simbolos");
        CodificadorDeSimbolos callOfDuty = new CodificadorDeSimbolos(arvoreDiferente, 257); //Verifica o tamanho das frequencias
        System.out.println("[Completo] - Codificação de simbolos");

        System.out.println("[Iniciado] - Arvore de Frequencias Huffman");
        arvoreDiferente = callOfDuty.gerarArvoreDiferentonaMelhorada(); //Codifica as frequencias de mesmo tamanho e cria uma nova arvore.
        System.out.println("[Completo] - Arvore de Frequencias Huffman");

        InputStream entrada = new BufferedInputStream(new FileInputStream(arquivo)); // Ler byte a byte o arquivo
        TheBitsOutputHandler saidaBits = new TheBitsOutputHandler(new BufferedOutputStream(new FileOutputStream(destinoCompressao))); //Escrever 0's e 1's na saida hehehehe

        try {
            System.out.println("[Iniciado] - Salvamento dos codigos de recuperação");
            salvarCodigoReconstrucao(callOfDuty, saidaBits); //Sem explicação...
            System.out.println("[Completo] - Codigos de Reconstrução Salvos");

            System.out.println("[Iniciado] - Compressor");
            compressor(arvoreDiferente, entrada, saidaBits);
            System.out.println("[Completo] - Compressão");
        } finally {
            entrada.close();
            saidaBits.close();
        }
    }

    private static void compressor(AArvoreDiferentona arvore, InputStream entrada, TheBitsOutputHandler saida) throws IOException {
        CodificadorHuff ch = new CodificadorHuff(arvore, saida);
        //Finalmente salvar a arvore que representa o arquivo comprimido
        while (true) {
            int bbyte = entrada.read();
            if (bbyte == -1)
                break;
            ch.write(bbyte);
        }

        ch.write(256); //Grava o final :v
    }

    //AKA DICIONARIO
    private static void salvarCodigoReconstrucao(CodificadorDeSimbolos simbolos, TheBitsOutputHandler saida) throws IOException {
        for (int i = 0; i < simbolos.getLimiteSimbolos(); i++) {
            int val = simbolos.getTamanhoDoCodigo(i);

            for (int j = 7; j >= 0; j--)
                saida.write((val >>> j) & 1);
        }
    }
}
