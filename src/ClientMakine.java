import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientMakine {
    private static JFrame frame;
    private JTextField id_TextField;
    private JTextField ad_TextField;
    private JComboBox tur_ComboBox;
    private JTextField hiz_TextField;
    private JTextField durum_TextField;
    private JButton baglan_Button;
    private JPanel panelMain;

    private static InetAddress host;
    private static final int PORT = 1234;
    Socket socket = null;
    static PrintWriter networkOutput;
    static Scanner networkInput;


    public ClientMakine() {
        baglan_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    socket = new Socket(host, PORT);
                    networkInput = new Scanner(socket.getInputStream());
                    networkOutput = new PrintWriter(socket.getOutputStream(), true);
                    networkOutput.println("makine_bagla");
                    networkOutput.println(id_TextField.getText());
                    networkOutput.println(ad_TextField.getText());
                    networkOutput.println(tur_ComboBox.getSelectedItem().toString());
                    networkOutput.println(hiz_TextField.getText());
                    networkOutput.println(durum_TextField.getText());
                    baglan_Button.setEnabled(false);
                    Thread t1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                String gelenMesaj = networkInput.nextLine();
                                String isId = networkInput.nextLine();
                                String uzunluk = networkInput.nextLine();
                                durum_TextField.setText("BUSY");
                                try {
                                    Thread.sleep(Integer.parseInt(uzunluk) / Integer.parseInt(hiz_TextField.getText())* 1000L);
                                } catch (InterruptedException interruptedException) {
                                    interruptedException.printStackTrace();
                                }
                                networkOutput.println("is_bitti");
                                durum_TextField.setText("EMPTY");
                            }
                        }
                    });
                    t1.start();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }


        frame = new JFrame("Client Makine");
        ClientMakine makine = new ClientMakine();
        frame.setContentPane(makine.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void isBekle() throws InterruptedException {

    }
}
