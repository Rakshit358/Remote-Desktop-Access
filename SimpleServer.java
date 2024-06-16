import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;

public class SimpleServer {
    public static void main(String[] args) {
        int port = 12345;
        final String password = "abcde";
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                ServerThread serverThread = new ServerThread(socket, password);
                serverThread.start(); // Ensure thread starts after initialization
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

class ServerThread extends Thread {
    private Socket socket;
    private String password;

    public ServerThread(Socket socket, String password) {
        this.socket = socket;
        this.password = password;
        System.out.println("Initialized the thread with socket and password");
    }

    public void run() {
        try (InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)) {

            // Perform password authentication
            writer.println("Enter the password:");
            String authPass = reader.readLine();
            if (!authPass.equals(password)) {
                System.out.println("Password authentication failed");
                socket.close();
                return; // Exit thread if authentication fails
            }
            System.out.println("Password authentication successful");
            writer.println("Password authenticated");

            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            while (true) {
                BufferedImage screenshot = robot.createScreenCapture(screenRect);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(screenshot, "png", bos);
                bos.flush();
                byte[] imageBytes = bos.toByteArray();

                DataOutputStream dos = new DataOutputStream(output);
                dos.writeInt(imageBytes.length);

                output.write(imageBytes);
                output.flush();

                Thread.sleep(200);
            }
        } catch (IOException | AWTException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}