import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

import java.util.logging.*;

public class SimpleClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private static final Logger LOGGER = Logger.getLogger(SimpleClient.class.getName());

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            // Read server's password prompt
            String serverMessage = reader.readLine();
            System.out.println(serverMessage);

            // Send password to server
            System.out.print("Enter password: ");
            String password = consoleReader.readLine();
            writer.println(password);

            // Read server's response to password
            String temp = reader.readLine();
            System.out.println(temp);

            if (temp != null) {
                JFrame frame = new JFrame("Desktop Stream Client");
                JLabel label = new JLabel();
                frame.getContentPane().add(label);
                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                while (true) {
                    DataInputStream dis = new DataInputStream(input);
                    int imgSize = dis.readInt();
                    byte[] imageBytes = new byte[imgSize];
                    dis.readFully(imageBytes);

                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                    BufferedImage img = ImageIO.read(bis);

                    ImageIcon imageIcon = new ImageIcon(img);
                    label.setIcon(imageIcon);
                    label.repaint();

                    Thread.sleep(200);
                }
            } else {
                System.out.println("Authentication failed. Closing connection.");
            }
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Server not found: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            // LOGGER.log(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
