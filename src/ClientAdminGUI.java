import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientAdminGUI {
    private static JFrame frame;
    private JPanel rootPanel;
    private JButton login_Button;
    private JPanel loginPanel;
    private JPanel appPanel;
    private JTextField username_textField;
    private JPasswordField password_passwordField;
    private JTextArea textArea;
    private JButton makineleriListeleButton;
    private JButton makineSorgulaButton;
    private JButton isEmirleriniListeleButton;
    private JButton yeniIsEmriButton;
    private JTextField uzunluk_textField;
    private JComboBox tur_ComboBox;
    private JTextField isID_textField;
    private JTextField makineID_textField;


    static Socket socket = null;
    private static Scanner networkInput;
    private static PrintWriter networkOutput;
    private static InetAddress host;
    private static final int PORT = 1234;

    public static List<Makine> makineListesi = new ArrayList<>();
    public static List<Is> isListesi = new ArrayList<>();
    public static List<Is> bekleyenisListesi = new ArrayList<>();
    public ClientAdminGUI() {

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                networkOutput.println("QUIT");
                e.getWindow().dispose();
            }
        });
        login_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                networkOutput.println("kimlik_dogrula");
                networkOutput.println(username_textField.getText());
                networkOutput.println(password_passwordField.getPassword());
                String response = networkInput.nextLine();
                if(response.equals("login_true")){
                    textArea.setEditable(false);
                    frame.getContentPane().removeAll();
                    frame.setTitle("Hoşgeldiniz :" + username_textField.getText());
                    frame.setContentPane(appPanel);
                    frame.setSize(1000,600);
                    frame.validate();
                    frame.repaint();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Kullanıcı Adı veya Şifre Yanlış");
                }
            }
        });
        makineleriListeleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientAdminGUI.makineListesi.clear();
                textArea.setText("");
                networkOutput.println("makineleri_listele");
                String size = networkInput.nextLine();
                for(int i=0;i<Integer.parseInt(size);i++){
                    String id,ad,tur,hiz,durum;
                    id= networkInput.nextLine();
                    ad= networkInput.nextLine();
                    tur= networkInput.nextLine();
                    hiz= networkInput.nextLine();
                    durum= networkInput.nextLine();
                    Makine makine = new Makine(id,ad,tur,hiz,durum);
                    makineListesi.add(makine);
                }
                textArea.append("CNC \n");
                makineListesi.forEach((mkn) -> {
                    if(mkn.tur.equals("CNC")){
                        textArea.append("\t" + "ID: " + mkn.id + " Ad: " + mkn.ad +  " Hız: " + mkn.hiz+ " Durum: " + mkn.durum + "\n");
                    }
                });
                textArea.append("DOKUM \n");
                makineListesi.forEach((mkn) -> {
                    if(mkn.tur.equals("DOKUM")){
                        textArea.append("\t" + "ID: " + mkn.id + " Ad: " + mkn.ad +  " Hız: " + mkn.hiz+ " Durum: " + mkn.durum + "\n");
                    }
                });
                textArea.append("KILIF \n");
                makineListesi.forEach((mkn) -> {
                    if(mkn.tur.equals("KILIF")){
                        textArea.append("\t" + "ID: " + mkn.id + " Ad: " + mkn.ad +  " Hız: " + mkn.hiz+ " Durum: " + mkn.durum + "\n");
                    }
                });
                textArea.append("KAPLAMA \n");
                makineListesi.forEach((mkn) -> {
                    if(mkn.tur.equals("KAPLAMA")){
                        textArea.append("\t" + "ID: " + mkn.id + " Ad: " + mkn.ad +  " Hız: " + mkn.hiz+ " Durum: " + mkn.durum + "\n");
                    }
                });
            }
        });
        isEmirleriniListeleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                bekleyenisListesi.clear();
                networkOutput.println("bekleyen_isleri_listele");
                String bekleyenIsSayisi = networkInput.nextLine();
                for(int i = 0; i < Integer.parseInt(bekleyenIsSayisi); i++){
                    String id = networkInput.nextLine();
                    String tur = networkInput.nextLine();
                    String uzunluk = networkInput.nextLine();
                    Is is = new Is(id,uzunluk,tur);
                    bekleyenisListesi.add(is);
                }
                textArea.append("Bekleyen İş Listesi \n");
                bekleyenisListesi.forEach((is) -> {
                    textArea.append("\t" + "İş ID:" + is.id + "  İş Türü:" + is.tur + "   İş Uzunluğu" + is.uzunluk + "\n");
                });
            }
        });
        makineSorgulaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                String id,ad,tur,hiz,durum,yapilanIsSayisi;
                String response;
                networkOutput.println("makine_sorgula");
                networkOutput.println(makineID_textField.getText());
                response = networkInput.nextLine();
                if(response.equals("makine_bulunamadi"))
                    textArea.append("Makine Bulunamadi");
                else {
                    isListesi.clear();
                    id = networkInput.nextLine();
                    ad = networkInput.nextLine();
                    tur = networkInput.nextLine();
                    hiz = networkInput.nextLine();
                    durum = networkInput.nextLine();
                    yapilanIsSayisi = networkInput.nextLine();
                    for(int i = 0; i< Integer.parseInt(yapilanIsSayisi); i++){
                        String isId = networkInput.nextLine();
                        String isUzunluk = networkInput.nextLine();
                        String isTur = networkInput.nextLine();
                        Is is = new Is(isId,isUzunluk,isTur);
                        isListesi.add(is);
                    }
                    textArea.append("ID: " + id +"\nAD: " + ad +"\nTUR: " + tur +"\nHIZ: " + hiz +"\nDURUM: " + durum);
                    textArea.append("\nSimdiye Kadar Yapılan Isler: ");
                    isListesi.forEach((is) ->{
                        textArea.append("\n\t" + "İşin Idsi: " + is.id + "--Uzunuluğu: " + is.uzunluk + "--Türü: " + is.tur);
                    });
                }
            }
        });
        yeniIsEmriButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                networkOutput.println("yeni_is");
                networkOutput.println(isID_textField.getText());
                networkOutput.println(uzunluk_textField.getText());
                networkOutput.println(tur_ComboBox.getSelectedItem().toString());
            }
        });
    }

    public static void main(String[] args) throws IOException {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        socket = new Socket(host, PORT);
        networkInput = new Scanner(socket.getInputStream());
        networkOutput = new PrintWriter(socket.getOutputStream(), true);

        frame = new JFrame("Login");
        frame.setContentPane(new ClientAdminGUI().loginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

