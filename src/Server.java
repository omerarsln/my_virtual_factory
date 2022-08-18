import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static ServerSocket serverSocket;
    public static HashMap<String, String> users = new HashMap<>();
    public static List<Makine> makineListesi = new ArrayList<>();
    public static List<Is> bekleyenIsListesi = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        try {
            serverSocket = new ServerSocket(1234);
        } catch (IOException ioEx) {
            System.out.println("Unable to set up port!");
            System.exit(1);
        }
        users.put("omer", "123");
        users.put("arslan", "321");

        while (true) {
            Socket client = serverSocket.accept();
            System.out.print("New client accepted -> " + client.getPort() + "\n");
            ClientHandler handler = new ClientHandler(client);
            handler.start();
        }
    }
}

class ClientHandler extends Thread {
    private final Socket client;
    private Scanner input;
    private PrintWriter output;

    public ClientHandler(Socket socket) {
        client = socket;
        try {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public void run() {
        String received;
        do {
            received = input.nextLine();
            switch (received) {
                case "makine_bagla":
                    String id, ad, tur, hiz, durum;
                    id = input.nextLine();
                    ad = input.nextLine();
                    tur = input.nextLine();
                    hiz = input.nextLine();
                    durum = input.nextLine();
                    Makine makine = new Makine(client, id, ad, tur, hiz, durum);
                    Server.makineListesi.add(makine);
                    break;
                case "kimlik_dogrula":
                    String username, password;
                    username = input.nextLine();
                    password = input.nextLine();
                    if (Server.users.get(username) != null) {
                        if (!password.equals(Server.users.get(username)))
                            output.println("login_false");
                        else
                            output.println("login_true");
                    } else
                        output.println("login_false");
                    break;
                case "makineleri_listele":
                    output.println(Server.makineListesi.size());
                    Server.makineListesi.forEach((mkn) -> {
                        output.println(mkn.id);
                        output.println(mkn.ad);
                        output.println(mkn.tur);
                        output.println(mkn.hiz);
                        output.println(mkn.durum);
                    });
                    break;
                case "makine_sorgula":
                    String makineId = input.nextLine();
                    AtomicBoolean bulundu = new AtomicBoolean(false);
                    AtomicInteger index = new AtomicInteger();
                    Server.makineListesi.forEach((mkn) -> {
                        if (mkn.id.equals(makineId)) {
                            bulundu.set(true);
                            index.set(Server.makineListesi.indexOf(mkn));
                        }
                    });
                    if (bulundu.get()) {
                        output.println("makine_bulundu");
                        output.println(Server.makineListesi.get(index.get()).id);
                        output.println(Server.makineListesi.get(index.get()).ad);
                        output.println(Server.makineListesi.get(index.get()).tur);
                        output.println(Server.makineListesi.get(index.get()).hiz);
                        output.println(Server.makineListesi.get(index.get()).durum);
                        // Yaptıgı işleri Gönder
                        output.println(Server.makineListesi.get(index.get()).yapilanIsListesi.size());
                        Server.makineListesi.get(index.get()).yapilanIsListesi.forEach((is) -> {
                            output.println(is.id);
                            output.println(is.uzunluk);
                            output.println(is.tur);
                        });
                    } else
                        output.println("makine_bulunamadi");
                    break;
                case "yeni_is":
                    String isId, uzunluk, isTuru;
                    isId = input.nextLine();
                    uzunluk = input.nextLine();
                    isTuru = input.nextLine();
                    Is yeniIs = new Is(isId, uzunluk, isTuru, false);
                    boolean gonderildi = false;
                    int i = 0;
                    while (i < Server.makineListesi.size()) {
                        Makine isiAlacakMakine = Server.makineListesi.get(i);
                        if (isiAlacakMakine.tur.equals(yeniIs.tur) && isiAlacakMakine.durum.equals("EMPTY")) {
                            try {
                                PrintWriter makineOutput = new PrintWriter(isiAlacakMakine.socket.getOutputStream(), true);
                                makineOutput.println("is_emri");
                                makineOutput.println(yeniIs.id);
                                makineOutput.println(yeniIs.uzunluk);
                                isiAlacakMakine.yapilanIsListesi.add(yeniIs);
                                gonderildi = true;
                                isiAlacakMakine.durum = "BUSY";
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        i++;
                    }
                    if (!gonderildi) {
                        Server.bekleyenIsListesi.add(yeniIs);
                    }
                    break;
                case "is_bitti":
                    int j = 0;
                    Makine m = null;
                    while (j < Server.makineListesi.size()) {
                        m = Server.makineListesi.get(j);
                        if (m.socket.equals(client)) {
                            m.durum = "EMPTY";
                            break;
                        }
                        j++;
                    }
                    j = 0;
                    while (j < Server.bekleyenIsListesi.size()) {
                        Is gonderilecekIs = Server.bekleyenIsListesi.get(j);
                        assert m != null;
                        if (gonderilecekIs.tur.equals(m.tur)) {
                            PrintWriter makineOutput = null;
                            try {
                                makineOutput = new PrintWriter(m.socket.getOutputStream(), true);
                                makineOutput.println("is_emri");
                                makineOutput.println(gonderilecekIs.id);
                                makineOutput.println(gonderilecekIs.uzunluk);
                                m.yapilanIsListesi.add(gonderilecekIs);
                                m.durum = "BUSY";
                                Server.bekleyenIsListesi.remove(j);
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        j++;
                    }
                    break;
                case "bekleyen_isleri_listele":
                    output.println(Server.bekleyenIsListesi.size());
                    Server.bekleyenIsListesi.forEach((is) -> {
                        output.println(is.id);
                        output.println(is.tur);
                        output.println(is.uzunluk);
                    });
                    break;
                default:
                    break;
            }

        } while (!received.equals("QUIT"));

        try {
            if (client != null) {
                System.out.println("Closing down connection...");
                client.close();
            }
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
        }
    }
}
