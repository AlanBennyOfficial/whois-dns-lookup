import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class WhoisExample {
    public static void main(String[] args) throws UnknownHostException, IOException {
        String domain = "google.com";
        String server = "whois.verisign-grs.com";
        int port = 43;
        String query = domain + "\r\n";
        try (Socket socket = new Socket(server, port)) {
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write(query);
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            writer.close();
        }
    }
}