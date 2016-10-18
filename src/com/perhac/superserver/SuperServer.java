package com.perhac.superserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuperServer {

    private static final int PORT = 44556;

    private static final Pattern PATTERN = Pattern.compile("GET /([^?]*).* HTTP/1.1");
    private static final int SCREEN_HEIGHT;
    private static final int SCREEN_WIDTH;
    private static final Map<String, Integer> KEY_LOOKUP_TABLE = new HashMap<>();
    private static final String KILL_COMMAND = "kill";

    static {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        SCREEN_WIDTH = gd.getDisplayMode().getWidth();
        SCREEN_HEIGHT = gd.getDisplayMode().getHeight();
        KEY_LOOKUP_TABLE.put("enter", KeyEvent.VK_ENTER);
        KEY_LOOKUP_TABLE.put("esc", KeyEvent.VK_ESCAPE);
        KEY_LOOKUP_TABLE.put("space", KeyEvent.VK_SPACE);
        KEY_LOOKUP_TABLE.put("bspace", KeyEvent.VK_BACK_SPACE);

    }

    private final Robot robot;
    private final ExecutorService executorService;

    private SuperServer() {
        try {
            this.robot = new Robot();
            executorService = Executors.newFixedThreadPool(8);
        } catch (AWTException e) {
            throw new ExceptionInInitializerError("COULD NOT INITIALIZE ROBOT");
        }
    }

    public static void main(String[] args) throws IOException, AWTException {
        if (args.length > 0 && Arrays.asList(KILL_COMMAND, "restart").contains(args[0].toLowerCase())) {
            sendCommandToLocalhost(KILL_COMMAND); //in both cases the command sent to localhost is "kill"
            if (KILL_COMMAND.equalsIgnoreCase(args[0])) {
                System.exit(0);
                return;
            }
        }
        new SuperServer().run();
    }

    private void run() throws IOException {
        checkIfRunning();
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Listening on port " + server.getLocalPort());
            do {
                final Socket socket = server.accept();
                this.executorService.execute(() -> {
                    try (Socket s = socket; Scanner scanner = new Scanner(s.getInputStream()).useDelimiter("\\r\\n")) {
                        try {
                            Matcher matcher = PATTERN.matcher(scanner.next());
                            if (matcher.find()) {
                                String received = matcher.group(1);
                                String protocolString = String.format("%.20s", received).toLowerCase();
                                switch (protocolString) {
                                    case "hello":
                                        System.out.println("hello received from: " + socket.getRemoteSocketAddress());
                                        s.getOutputStream().write(String.format("HTTP/1.1 200 OK%n%nSuperServer says hello. Screen dimensions: %dx%d", SCREEN_WIDTH, SCREEN_HEIGHT).getBytes());
                                        return;
                                    case KILL_COMMAND:
                                        System.exit(0);
                                        break;
                                    case "favicon.ico":
                                        return;
                                }
                                System.out.println("Received: " + received);
                                if (this.interpret(received)) {
                                    s.getOutputStream().write("HTTP/1.1 200 OK\r\n".getBytes());
                                } else {
                                    throw new RuntimeException("failed to fully interpreted the command");
                                }
                            } else {
                                throw new ParseException("command not formed correctly", 0);
                            }
                        } catch (Exception e) {
                            s.getOutputStream().write("HTTP/1.1 400 Bad Request\r\n".getBytes());
                        }
                    } catch (IOException ignored) {
                    }
                    //the below sends a key release message just to make sure no modifier keys are left hanging
                    resetModifiers();
                });
            } while (!Thread.interrupted());
        }
    }

    private void checkIfRunning() {
        int responseCode = SuperServer.sendCommandToLocalhost("hello");
        if (responseCode == 200) {
            JOptionPane.showMessageDialog(null, "An instance of SuperServer is already running.");
            System.exit(-1);
        }
    }

    private static int sendCommandToLocalhost(String command) {
        int responseCode = 0;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http", Inet4Address.getLocalHost().getHostAddress(), PORT, "/" + command).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(500);
            responseCode = connection.getResponseCode();
            connection.disconnect();
        } catch (IOException ignored) {
        }
        return responseCode;
    }

    private void resetModifiers() {
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_WINDOWS);
        robot.keyRelease(KeyEvent.VK_BACK_SPACE);
    }

    private boolean interpret(String command) throws AWTException {
        final boolean[] allSuccessful = {true};
        Arrays.asList(command.split(";")).forEach(
                cmd -> allSuccessful[0] &= interpretCommand(cmd)
        );
        return allSuccessful[0];
    }

    private boolean interpretCommand(String cmd) {
        try {
            Thread.sleep(20);
            String[] strings = cmd.split("[,:]");
            switch (strings[0].toLowerCase()) {
                case "app":
                    char char0 = strings[1].charAt(0);
                    if (!Character.isDigit(char0)) {
                        return false;
                    }
                    keyPressAndRelease(char0, KeyEvent.VK_WINDOWS);
                    break;
                case "mm":
                    robot.mouseMove(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
                    break;
                case "click":
                case "rclick":
                    mousePressAndRelease(cmd.startsWith("c") ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
                    break;
                case "enter":
                case "esc":
                case "space":
                case "bspace":
                    keyPressAndRelease(KEY_LOOKUP_TABLE.get(strings[0].toLowerCase()));
                    break;
                case "vol+":
                case "vol-":
                    keyPressAndRelease(cmd.endsWith("+") ? KeyEvent.VK_PERIOD : KeyEvent.VK_COMMA, KeyEvent.VK_WINDOWS);
                    break;
                case "ctrl":
                    keyPressAndRelease(Character.toUpperCase(strings[1].charAt(0)), KeyEvent.VK_CONTROL);
                    break;
                case "desktop":
                    keyPressAndRelease(KeyEvent.VK_D, KeyEvent.VK_WINDOWS);
                    break;
                case "close":
                    keyPressAndRelease(KeyEvent.VK_F4, KeyEvent.VK_ALT);
                    break;
                case "type":
                    strings[1].chars().forEach(c -> keyPressAndRelease(Character.toUpperCase(c), Character.isUpperCase(c) ? new int[]{KeyEvent.VK_SHIFT} : new int[0]));
                    break;
                case "wait":
                    Thread.sleep(Integer.parseInt(strings[1]));
                    break;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private synchronized void keyPressAndRelease(int virtualKey, int... modifiers) {
        if (modifiers.length > 0) {
            robot.keyPress(modifiers[0]);
        }
        try {
            robot.keyPress(virtualKey);
            robot.keyRelease(virtualKey);
        } finally {
            if (modifiers.length > 0) {
                robot.keyRelease(modifiers[0]);
            }
        }
    }

    private synchronized void mousePressAndRelease(int virtualKey) {
        robot.mousePress(virtualKey);
        robot.mouseRelease(virtualKey);
    }

}
