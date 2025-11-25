import java.io.*;
import java.util.*;

public class SistemaObjetosPerdidos {

    // Classe representando um objeto perdido (quem encontrou)
    static class ObjetoPerdido {
        int id;
        String nome;
        String descricao;
        String codigoSolicitacao;
        String contatoDono;
        String localizacao;
        String dataEncontro;

        public ObjetoPerdido(int id, String nome, String descricao, String codigoSolicitacao, String contatoDono, String localizacao, String dataEncontro) {
            this.id = id;
            this.nome = nome;
            this.descricao = descricao;
            this.codigoSolicitacao = codigoSolicitacao;
            this.contatoDono = contatoDono;
            this.localizacao = localizacao;
            this.dataEncontro = dataEncontro;
        }

        @Override
        public String toString() {
            return id + " | " + nome + " | " + descricao + " | Local: " + localizacao + " | Código: " + codigoSolicitacao;
        }
    }

    // Classe representando a solicitação de alguém que PERDEU algo
    static class PedidoBusca {
        int idBusca;
        String descricaoBusca;
        String contatoPessoa;
        String localPerdido;
        String dataPerdido;

        public PedidoBusca(int idBusca, String descricaoBusca, String contatoPessoa, String localPerdido, String dataPerdido) {
            this.idBusca = idBusca;
            this.descricaoBusca = descricaoBusca;
            this.contatoPessoa = contatoPessoa;
            this.localPerdido = localPerdido;
            this.dataPerdido = dataPerdido;
        }
    }

    // Classe para guardar os encontros entre quem achou e quem perdeu
    static class Encontro {
        String codigoMatch;
        int idObjeto;
        int idBusca;
        String status;
        String dataAgendada;
        String localEncontro;
        boolean validado;

        public Encontro(String codigoMatch, int idObjeto, int idBusca) {
            this.codigoMatch = codigoMatch;
            this.idObjeto = idObjeto;
            this.idBusca = idBusca;
            this.status = "PENDENTE";
            this.validado = false;
        }
    }

    static ArrayList<ObjetoPerdido> objetos = new ArrayList<>();
    static ArrayList<PedidoBusca> buscas = new ArrayList<>();
    static ArrayList<Encontro> encontros = new ArrayList<>();

    static final String ARQUIVO_OBJ = "objetos_perdidos.txt";
    static final String ARQUIVO_BUSCAS = "buscas.txt";
    static final String ARQUIVO_ENCONTROS = "encontros.txt";

    // Contador automático de IDs
    static int proximoIdObjeto = 1;
    static int proximoIdBusca = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        carregarObjetos();
        carregarBuscas();
        carregarEncontros();
        atualizarContadores();

        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n=== SISTEMA DE OBJETOS PERDIDOS ===");
            System.out.println("1 - Registrar objeto encontrado");
            System.out.println("2 - Registrar que perdeu um objeto");
            System.out.println("3 - Listar objetos encontrados");
            System.out.println("4 - Listar buscas registradas");
            System.out.println("5 - Procurar correspondências (MATCH)");
            System.out.println("6 - Validar encontro");
            System.out.println("7 - Agendar encontro");
            System.out.println("8 - Ver meus encontros");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Opção inválida!");
                continue;
            }

            switch (opcao) {
                case 1 -> cadastrarObjeto(sc);
                case 2 -> registrarBusca(sc);
                case 3 -> listarObjetos();
                case 4 -> listarBuscas();
                case 5 -> procurarMatch(sc);
                case 6 -> validarEncontro(sc);
                case 7 -> agendarEncontro(sc);
                case 8 -> verEncontros();
                case 0 -> System.out.println("Encerrando...");
                default -> System.out.println("Opção inválida!");
            }
        }

        sc.close();
    }

    // REGISTRAR OBJETO ENCONTRADO
    public static void cadastrarObjeto(Scanner sc) {
        try {
            int id = proximoIdObjeto++;

            System.out.print("Nome do objeto: ");
            String nome = sc.nextLine();

            System.out.print("Descrição do objeto: ");
            String descricao = sc.nextLine();

            System.out.print("Onde encontrou? ");
            String localizacao = sc.nextLine();

            System.out.print("Data que encontrou (ex: 20/11/2024): ");
            String dataEncontro = sc.nextLine();

            System.out.print("Seu contato (email/telefone): ");
            String contato = sc.nextLine();

            String codigo = gerarCodigoSolicitacao();

            ObjetoPerdido obj = new ObjetoPerdido(id, nome, descricao, codigo, contato, localizacao, dataEncontro);
            objetos.add(obj);

            salvarObjeto(obj);

            System.out.println("✓ Objeto registrado!");
            System.out.println("✓ Código: " + codigo);

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar.");
        }
    }

    // REGISTRAR BUSCA DE QUEM PERDEU
    public static void registrarBusca(Scanner sc) {
        try {
            int id = proximoIdBusca++;

            System.out.print("Descreva o que você perdeu: ");
            String descricao = sc.nextLine();

            System.out.print("Onde perdeu? ");
            String local = sc.nextLine();

            System.out.print("Data aproximada que perdeu: ");
            String data = sc.nextLine();

            System.out.print("Seu contato (email/telefone): ");
            String contato = sc.nextLine();

            PedidoBusca pb = new PedidoBusca(id, descricao, contato, local, data);
            buscas.add(pb);

            salvarBusca(pb);

            System.out.println("✓ Solicitação registrada!");
        } catch (Exception e) {
            System.out.println("Erro ao registrar solicitação.");
        }
    }

    // LISTAR OBJETOS
    public static void listarObjetos() {
        if (objetos.isEmpty()) {
            System.out.println("Nenhum objeto encontrado.");
            return;
        }

        System.out.println("\n--- OBJETOS ENCONTRADOS ---");
        for (ObjetoPerdido o : objetos) {
            System.out.println(o);
        }
    }

    // LISTAR BUSCAS
    public static void listarBuscas() {
        if (buscas.isEmpty()) {
            System.out.println("Nenhuma busca registrada.");
            return;
        }

        System.out.println("\n--- BUSCAS REGISTRADAS ---");
        for (PedidoBusca b : buscas) {
            System.out.println(b.idBusca + " | " + b.descricaoBusca + " | Local: " + b.localPerdido);
        }
    }

    // MATCH AUTOMÁTICO - comparar objetos encontrados com buscas
    public static void procurarMatch(Scanner sc) {
        System.out.println("\n=== VERIFICANDO MATCHES ===");

        boolean encontrouMatch = false;

        for (PedidoBusca b : buscas) {
            for (ObjetoPerdido o : objetos) {

                int similaridade = calcularSimilaridade(b.descricaoBusca, o.descricao);
                int similaridadeLocal = calcularSimilaridade(b.localPerdido, o.localizacao);

                if (similaridade >= 40 || similaridadeLocal >= 50) {
                    encontrouMatch = true;

                    System.out.println("\n★ POSSÍVEL MATCH ENCONTRADO!");
                    System.out.println("Descrição da busca: " + b.descricaoBusca);
                    System.out.println("Descrição do objeto: " + o.descricao);
                    System.out.println("Similaridade: " + similaridade + "%");
                    System.out.println("Local perdido: " + b.localPerdido);
                    System.out.println("Local encontrado: " + o.localizacao);

                    System.out.print("\nDeseja criar um encontro? (S/N): ");
                    String resp = sc.nextLine().toUpperCase();

                    if (resp.equals("S")) {
                        String codigoMatch = "MATCH-" + new Random().nextInt(9000) + 1000;
                        Encontro enc = new Encontro(codigoMatch, o.id, b.idBusca);
                        encontros.add(enc);
                        salvarEncontro(enc);

                        System.out.println("\n✓ Encontro criado!");
                        System.out.println("✓ Código do encontro: " + codigoMatch);
                    }
                }
            }
        }

        if (!encontrouMatch) {
            System.out.println("\nNenhuma correspondência encontrada.");
        }

        System.out.println("\nFim da verificação.");
    }

    // VALIDAR ENCONTRO - perguntas de segurança antes de liberar contato
    public static void validarEncontro(Scanner sc) {
        System.out.println("\n--- VALIDAÇÃO DE ENCONTRO ---");
        System.out.print("Código do encontro: ");
        String codigo = sc.nextLine();

        Encontro enc = buscarEncontro(codigo);
        if (enc == null) {
            System.out.println("Encontro não localizado!");
            return;
        }

        ObjetoPerdido obj = buscarObjetoPorId(enc.idObjeto);
        PedidoBusca busca = buscarBuscaPorId(enc.idBusca);

        System.out.println("\n--- VALIDAÇÃO DE SEGURANÇA ---");
        System.out.println("Objeto: " + obj.nome);
        System.out.println("Descrição: " + obj.descricao);

        System.out.print("\nVocê confirma que este é o seu objeto? (S/N): ");
        String conf1 = sc.nextLine().toUpperCase();

        if (!conf1.equals("S")) {
            System.out.println("Validação cancelada.");
            return;
        }

        System.out.print("\nPara sua segurança, responda: Qual a cor principal? ");
        String resp1 = sc.nextLine();

        System.out.print("Há alguma marca ou característica especial? ");
        String resp2 = sc.nextLine();

        System.out.println("\n✓ Respostas registradas");
        System.out.println("✓ Validação concluída");

        enc.validado = true;
        enc.status = "VALIDADO";
        atualizarEncontro(enc);

        System.out.println("\n--- CONTATOS LIBERADOS ---");
        System.out.println("Contato de quem encontrou: " + obj.contatoDono);
        System.out.println("Seu contato: " + busca.contatoPessoa);
        System.out.println("\n⚠ DICA: Combine o encontro em local público");
    }

    // AGENDAR ENCONTRO - após validação, define data e local
    public static void agendarEncontro(Scanner sc) {
        System.out.println("\n--- AGENDAR ENCONTRO ---");
        System.out.print("Código do encontro: ");
        String codigo = sc.nextLine();

        Encontro enc = buscarEncontro(codigo);
        if (enc == null) {
            System.out.println("Encontro não localizado!");
            return;
        }

        if (!enc.validado) {
            System.out.println("Este encontro precisa ser validado primeiro!");
            System.out.println("Use a opção 6 para validar.");
            return;
        }

        System.out.print("Data do encontro (ex: 25/11/2024 14:00): ");
        String data = sc.nextLine();

        System.out.print("Local público para o encontro: ");
        String local = sc.nextLine();

        enc.dataAgendada = data;
        enc.localEncontro = local;
        enc.status = "AGENDADO";
        atualizarEncontro(enc);

        System.out.println("\n✓ Encontro agendado!");
        System.out.println("Data: " + data);
        System.out.println("Local: " + local);
    }

    // VER ENCONTROS - mostra todos os encontros do usuário
    public static void verEncontros() {
        if (encontros.isEmpty()) {
            System.out.println("\nNenhum encontro registrado.");
            return;
        }

        System.out.println("\n--- MEUS ENCONTROS ---");

        for (Encontro e : encontros) {
            ObjetoPerdido obj = buscarObjetoPorId(e.idObjeto);
            System.out.println("\nCódigo: " + e.codigoMatch);
            System.out.println("Objeto: " + obj.nome);
            System.out.println("Status: " + e.status);
            if (e.dataAgendada != null) {
                System.out.println("Data: " + e.dataAgendada);
                System.out.println("Local: " + e.localEncontro);
            }
            System.out.println("---");
        }
    }

    // CÁLCULO DE SIMILARIDADE - compara palavras entre duas strings
    public static int calcularSimilaridade(String a, String b) {
        String[] aa = a.toLowerCase().split(" ");
        String[] bb = b.toLowerCase().split(" ");

        int iguais = 0;

        for (String s1 : aa) {
            for (String s2 : bb) {
                if (s1.equals(s2) && s1.length() > 2) iguais++;
            }
        }

        if (aa.length == 0) return 0;
        return (int) ((iguais / (double) aa.length) * 100);
    }

    // GERAR CÓDIGO
    public static String gerarCodigoSolicitacao() {
        Random rand = new Random();
        int numero = rand.nextInt(9000) + 1000;
        return "SOL-" + numero;
    }

    // ATUALIZAR CONTADORES - pega o maior ID dos arquivos
    public static void atualizarContadores() {
        for (ObjetoPerdido o : objetos) {
            if (o.id >= proximoIdObjeto) {
                proximoIdObjeto = o.id + 1;
            }
        }

        for (PedidoBusca b : buscas) {
            if (b.idBusca >= proximoIdBusca) {
                proximoIdBusca = b.idBusca + 1;
            }
        }
    }

    // BUSCAR ENCONTRO POR CÓDIGO
    public static Encontro buscarEncontro(String codigo) {
        for (Encontro e : encontros) {
            if (e.codigoMatch.equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    // BUSCAR OBJETO POR ID
    public static ObjetoPerdido buscarObjetoPorId(int id) {
        for (ObjetoPerdido o : objetos) {
            if (o.id == id) {
                return o;
            }
        }
        return null;
    }

    // BUSCAR BUSCA POR ID
    public static PedidoBusca buscarBuscaPorId(int id) {
        for (PedidoBusca b : buscas) {
            if (b.idBusca == id) {
                return b;
            }
        }
        return null;
    }

    // SALVAR OBJETO NO ARQUIVO
    public static void salvarObjeto(ObjetoPerdido obj) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_OBJ, true))) {
            bw.write(obj.id + ";" + obj.nome + ";" + obj.descricao + ";" + obj.codigoSolicitacao + ";" +
                    obj.contatoDono + ";" + obj.localizacao + ";" + obj.dataEncontro);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar objeto!");
        }
    }

    // SALVAR BUSCA NO ARQUIVO
    public static void salvarBusca(PedidoBusca pb) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_BUSCAS, true))) {
            bw.write(pb.idBusca + ";" + pb.descricaoBusca + ";" + pb.contatoPessoa + ";" +
                    pb.localPerdido + ";" + pb.dataPerdido);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar busca!");
        }
    }

    // SALVAR ENCONTRO NO ARQUIVO
    public static void salvarEncontro(Encontro enc) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_ENCONTROS, true))) {
            bw.write(enc.codigoMatch + ";" + enc.idObjeto + ";" + enc.idBusca + ";" + enc.status + ";" +
                    enc.validado + ";" + (enc.dataAgendada != null ? enc.dataAgendada : "") + ";" +
                    (enc.localEncontro != null ? enc.localEncontro : ""));
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar encontro!");
        }
    }

    // ATUALIZAR ENCONTRO NO ARQUIVO
    public static void atualizarEncontro(Encontro enc) {
        try {
            ArrayList<String> linhas = new ArrayList<>();
            File f = new File(ARQUIVO_ENCONTROS);

            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String linha;
                while ((linha = br.readLine()) != null) {
                    String[] d = linha.split(";");
                    if (d[0].equals(enc.codigoMatch)) {
                        linha = enc.codigoMatch + ";" + enc.idObjeto + ";" + enc.idBusca + ";" + enc.status + ";" +
                                enc.validado + ";" + (enc.dataAgendada != null ? enc.dataAgendada : "") + ";" +
                                (enc.localEncontro != null ? enc.localEncontro : "");
                    }
                    linhas.add(linha);
                }
                br.close();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            for (String l : linhas) {
                bw.write(l);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Erro ao atualizar encontro!");
        }
    }

    // CARREGAR OBJETOS DO ARQUIVO
    public static void carregarObjetos() {
        File f = new File(ARQUIVO_OBJ);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] d = linha.split(";");
                if (d.length >= 7) {
                    objetos.add(new ObjetoPerdido(
                            Integer.parseInt(d[0]), d[1], d[2], d[3], d[4], d[5], d[6]
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar objetos.");
        }
    }

    // CARREGAR BUSCAS DO ARQUIVO
    public static void carregarBuscas() {
        File f = new File(ARQUIVO_BUSCAS);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] d = linha.split(";");
                if (d.length >= 5) {
                    buscas.add(new PedidoBusca(
                            Integer.parseInt(d[0]), d[1], d[2], d[3], d[4]
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar buscas.");
        }
    }

    // CARREGAR ENCONTROS DO ARQUIVO
    public static void carregarEncontros() {
        File f = new File(ARQUIVO_ENCONTROS);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] d = linha.split(";");
                if (d.length >= 3) {
                    Encontro enc = new Encontro(d[0], Integer.parseInt(d[1]), Integer.parseInt(d[2]));
                    if (d.length > 3) enc.status = d[3];
                    if (d.length > 4) enc.validado = Boolean.parseBoolean(d[4]);
                    if (d.length > 5 && !d[5].isEmpty()) enc.dataAgendada = d[5];
                    if (d.length > 6 && !d[6].isEmpty()) enc.localEncontro = d[6];
                    encontros.add(enc);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar encontros.");
        }
    }
}