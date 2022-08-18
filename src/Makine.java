import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Makine {
    Socket socket;
    String id;
    String ad;
    String tur;
    String hiz;
    String durum;

    public List<Is> yapilanIsListesi = new ArrayList<>();

    public Makine(Socket socket, String id,String ad,String tur,String hiz,String durum){
        this.socket = socket;
        this.id = id;
        this.ad = ad;
        this.tur = tur;
        this.hiz = hiz;
        this.durum = durum;
    }
    public Makine(String id,String ad,String tur,String hiz,String durum){
        this.id = id;
        this.ad = ad;
        this.tur = tur;
        this.hiz = hiz;
        this.durum = durum;
    }
}
