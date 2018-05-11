package chati_leiutis;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import tetrispackage.TetrisGraafika;
import tetrispackage.TetrisGraafikaMultiplayer;
import tetrispackage.TetrisReplay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Klient extends Application {
    Klient self;
    private Integer laius;
    private Integer kõrgus;
    private String nimi;
    private BlockingQueue<Integer> toLoginorNot = new ArrayBlockingQueue<>(5);
    private boolean challengeOpen = false;
    private boolean mpgameopen = false;
    private boolean loggedIN = false;
    private boolean lobbyOpen = false;
    private HashMap<Integer, String> online_users = new HashMap<>();
    private HashMap<String, Integer> name_2_ID = new HashMap<>();
    private Socket connection;
    private DataOutputStream out;
    private TextArea ekraan;
    private ObservableList<String> observableUsers;
    private ObservableList<String> observableReplays;
    private ListView<String> userListView = new ListView<>();
    private ListView<String> replayListView = new ListView<>();
    private TextField konsool;
    private PasswordField loginpasswordfield;
    private TextField loginnamefield;
    private PasswordField regpasswordfield;
    private TextField regnamefield;
    private TetrisGraafikaMultiplayer multiplayerGame;
    OpenChallengeWindow challengewindow;
    //kompaktne soundifaili saamine
    private MediaPlayer chatsound = new MediaPlayer(new Media(new File(Klient.class.getClassLoader().getResource("chattick.wav").getFile()).toURI().toString()));
    private MediaPlayer gamenotificationsound = new MediaPlayer(new Media(new File(Klient.class.getClassLoader().getResource("gamenotification.mp3").getFile()).toURI().toString()));

    public String getNimi() {
        return nimi;
    }

    public TetrisGraafikaMultiplayer getMultiplayerGame() {
        return multiplayerGame;
    }


    public boolean isMpgameopen() {
        return mpgameopen;
    }
    public void setMpgameopen(boolean mpgameopen) {
        this.mpgameopen = mpgameopen;
    }

    public Klient() {
    }

    public Klient(DataOutputStream out) {
        this.out = out;
    }


    public void showRegistration() {
        Stage newStage = new Stage();
        VBox comp = new VBox();
        Label namelabel = new Label("Enter your credentials below:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name here...");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password here");
        regnamefield = nameField;
        regpasswordfield = passwordField;

        comp.getChildren().add(namelabel);
        comp.getChildren().add(nameField);
        Button registernupp = new Button("Register and close");
        registernupp.setOnMouseClicked((event) -> {
            try {
                //registreering
                sendSomething(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            newStage.close();
        });

        Scene stageScene = new Scene(comp, 250, 270);
        comp.getChildren().add(passwordField);
        comp.getChildren().add(registernupp);
        newStage.setScene(stageScene);
        newStage.show();
    }

    public void showLogIn() {
        Stage newStage = new Stage();

        BorderPane border = new BorderPane();
        Button singleplayerbutton = new Button("Singleplayer");
        singleplayerbutton.setFont(new Font(20));
        border.setBottom(singleplayerbutton);
        //Singeplayeri käivitamine
        singleplayerbutton.setOnMouseClicked((MouseEvent) -> {

            try {
                TetrisGraafika tetris = new TetrisGraafika();
                TetrisGraafika tetris2 = new TetrisGraafika();
                Stage lava = new Stage();
                tetris.start(lava);
                tetris2.start(lava);

            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        });


        VBox comp = new VBox();
        HBox nupudkõrvuti = new HBox();
        Label namelabel = new Label("Enter your credentials below:");
        Label errorlabel = new Label("");                       //siia läheb pärast errormessage
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name here...");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password here");
        loginnamefield = nameField;
        loginpasswordfield = passwordField;

        comp.getChildren().add(namelabel);
        comp.getChildren().add(nameField);

        Button login_nupp = new Button("Log in");
        login_nupp.setDefaultButton(true);

        //login event- ootame blockqueuest vastust,vastavalt sellele tegutseme
        login_nupp.setOnAction(ev -> {
            try {
                if (connection == null || connection.isClosed()) {
                    errorlabel.setText("Error, connection error. Please restart.");
                } else {
                    sendSomething(2);
                    //sätin cliendi siseseks nimeks proovitud nime
                    nimi = loginnamefield.getText();
                    int loginvastus = toLoginorNot.take();
                    switch (loginvastus) {
                        case 1:
                            loggedIN = true;
                            newStage.close();
                            break;
                        case -1:
                            errorlabel.setText("Error. Please try again.");
                            break;
                        case 0:
                            errorlabel.setText("Unexpected input, something went wrong.");

                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        loginnamefield.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                login_nupp.fire();
            }
        });

        Button registreerimis_nupp = new Button("Register new user");
        registreerimis_nupp.setOnMouseClicked((event) -> {
            showRegistration();
        });

        //kui ristist kinni panna, jätan seisma
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Closing application...");
                loggedIN = false;
                //listener.shutDown();   kas seda rida on ikka vaja?
                Platform.exit();

            }
        });
        comp.getChildren().add(passwordField);
        nupudkõrvuti.getChildren().addAll(login_nupp, registreerimis_nupp);
        comp.getChildren().add(nupudkõrvuti);
        comp.getChildren().add(errorlabel);

        border.setCenter(comp);
        border.setBottom(singleplayerbutton);
        Scene stageScene = new Scene(border, 300, 300);

        newStage.setScene(stageScene);
        newStage.showAndWait();
    }

    public void showLobby() throws Exception {

        //säti suurus
        laius = 700;
        kõrgus = 600;
        Stage primaryStage = new Stage();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Disconnecting...");
                loggedIN = false;
                lobbyOpen = false;
                try {
                    sendSomething(4);

                    //connecter.getOut().close();
                } catch (Exception e) {
                    System.out.println("Socket juba kinni, jätkan välja logimist");
                }
            }
        });
        Group juur = new Group();
        TextArea messagearea = new TextArea();
        ekraan = messagearea;
        //online olevate userite list
        observableUsers = FXCollections.observableArrayList(online_users.values());
        userListView.setItems(observableUsers);
        userListView.setPrefSize((laius / 4), (kõrgus / 4.5) * 3 - 20);

        Font labelfont = new Font(16);
        Label userlabel = new Label("Users");
        userlabel.setFont(labelfont);
        Label chatlabel = new Label("Messages");
        chatlabel.setFont(labelfont);

        //muudan aknad mitteklikitavaks
        messagearea.setEditable(false);
        messagearea.setWrapText(true);
        messagearea.setPrefSize((laius / 4) * 3, (kõrgus / 4.5) * 3);
        messagearea.setPromptText("Messages...");

        //panen userid paika
        sendSomething(3);

        //siia panene kõik chati teksti
        TextField messagefield = new TextField();
        messagefield.setPromptText("Enter message here...");
        messagefield.setFont(labelfont);
        messagefield.setPrefWidth((laius / 3.5) * 3);
        konsool = messagefield;

        //pilt
        Image chatImage = new Image("/Tetris.png", laius, 200, true, false);
        ImageView pilt = new ImageView(chatImage);
        pilt.setFitHeight(kõrgus / 5);
        pilt.setFitWidth(laius);


        Button singleplayerbtn = new Button("Singleplayer");
        TetrisGraafika tetris = new TetrisGraafika();
        singleplayerbtn.setOnMouseClicked((MouseEvent) -> {

            try {
                Stage lava = new Stage();
                tetris.start(lava);

            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        });

        //siit läheb replay käima
        Button replaybtn = new Button("Replays");

        replaybtn.setOnMouseClicked((MouseEvent) -> {
            showReplays();
        });

        Button challengeButton = new Button("Challenge");
        challengeButton.setPrefWidth(laius / 4);
        challengeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!challengeOpen) {
                    showOpenChallengeWindow();
                }
            }
        });

        StackPane stackPane = new StackPane();
        BorderPane border = new BorderPane();

        border.setBottom(stackPane);
        stackPane.getChildren().add(pilt);
        stackPane.getChildren().add(singleplayerbtn);
        stackPane.getChildren().add(replaybtn);
        StackPane.setAlignment(replaybtn, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(singleplayerbtn, Pos.BOTTOM_CENTER);


        //send nupp
        Button sendbtn = new Button("Send");
        sendbtn.setFont(new Font(20));
        sendbtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    sendSomething(5);
                } catch (Exception e2) {
                    throw new RuntimeException(e2);
                }
            }
        });

        messagefield.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                //kui kirjutame logout, siis logime välja
                try {
                    if (messagefield.getText().equals("logout")) {
                        sendSomething(4);
                        messagefield.clear();
                        ekraan.appendText("You have been disconnected...");
                        loggedIN = false;
                    } else
                        sendSomething(5);
                } catch (Exception e3) {
                    throw new RuntimeException(e3);
                }
            }
        });
        VBox outervbox = new VBox();
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        VBox vbox2 = new VBox();
        HBox hbox2 = new HBox();

        vbox.getChildren().addAll(userlabel, userListView, challengeButton);
        vbox2.getChildren().addAll(chatlabel, messagearea);
        hbox.getChildren().addAll(vbox, vbox2);
        hbox.setSpacing(2);
        hbox2.getChildren().addAll(messagefield, sendbtn);
        outervbox.getChildren().addAll(hbox, hbox2, border);
        juur.getChildren().add(outervbox);

        Scene lava = new Scene(juur, laius, kõrgus);
        primaryStage.setResizable(false);
        primaryStage.setScene(lava);
        primaryStage.setTitle("Client");
        primaryStage.centerOnScreen();
        primaryStage.showAndWait();
    }

    public void showMultiplayer(Integer opponentID) {
        mpgameopen = true;
        TetrisGraafikaMultiplayer mp = new TetrisGraafikaMultiplayer();
        multiplayerGame = mp;
        Stage mpstage = new Stage();
        mp.start(mpstage, this, opponentID);
    }

    public void showIncomingChallengeWindow(Integer ID, String user) {
        Stage newStage = new Stage();
        VBox comp = new VBox();
        comp.setAlignment(Pos.CENTER);
        HBox hboxnupud = new HBox();
        Label messagelaber = new Label("You have been challenged by " + user + "!");

        Button acceptbutton = new Button("Accept");
        Button declinebutton = new Button("Decline");

        acceptbutton.setOnMouseClicked((event) -> {
            try {
                out.writeInt(7);
                out.writeInt(ID);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            newStage.close();
        });

        declinebutton.setOnMouseClicked((event) -> {
            try {
                sendSomething(9);
                out.writeInt(9);
                out.writeInt(ID);
                challengeOpen = false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            newStage.close();
        });

        hboxnupud.getChildren().addAll(acceptbutton, declinebutton);
        comp.getChildren().addAll(messagelaber, hboxnupud);
        Scene stageScene = new Scene(comp, 250, 270);

        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                if (challengeOpen) {
                    try {
                        out.writeInt(9);
                        out.writeInt(ID);
                        //connecter.getOut().close();
                    } catch (IOException e) {
                        System.out.println("Error on closing challengedwindow)");
                    }
                }
            }
        });

        newStage.setScene(stageScene);
        newStage.show();
    }

    void showOpenChallengeWindow() {
        try {
            String selectedUserName = userListView.getSelectionModel().getSelectedItem();
            if (!selectedUserName.equals(nimi)) {
                Stage inchallengewindow = new Stage();
                OpenChallengeWindow challenge = new OpenChallengeWindow(inchallengewindow);
                challengewindow = challenge;
                challenge.start(inchallengewindow, selectedUserName, self);
                sendSomething(7);
            }
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    void showReplays() {
        //todo päris andmete serverilt saamine ja siis nende kasutamine
        //todo hetkel lihtsalt faken mingid andmed
        String mäng1 = "Ingo-Theodor   Duration-2651s";
        String mäng2 = "Theodor-Karl    Duration-1m23s";
        String mäng3 = "Ingo-Karl   Duration-3m55s";

        observableReplays = FXCollections.observableArrayList(Arrays.asList(mäng1, mäng2, mäng3));
        replayListView.setItems(observableReplays);
        replayListView.setPrefSize(600, 270);

        Button watchButton = new Button("Watch game");
        watchButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedReplayname = replayListView.getSelectionModel().getSelectedItem();
                TetrisReplay replay = new TetrisReplay();
                try {
                    Stage replaylava = new Stage();
                    //todo vali õige replay, ava TetrisReplay vastava andmetega
                    replay.start(replaylava, selectedReplayname, "1000,RIGHT;150,RIGHT;50,LEFT;200,RIGHT;100,LEFT;300,RIGHT;100,LEFT;500,LEFT;100,UP;500,UP;600,DOWN",
                            "1000,RIGHT;150,RIGHT;50,LEFT;200,RIGHT;100,LEFT;300,RIGHT;100,LEFT;500,LEFT;100,UP;500,UP;600,DOWN");

                } catch (Exception e) {
                    throw new RuntimeException(e);

                }
            }
        });

        Stage newStage = new Stage();
        newStage.setResizable(false);

        VBox comp = new VBox();
        comp.getChildren().addAll(replayListView, watchButton);

        Scene stageScene = new Scene(comp, 600, 300);

        newStage.setScene(stageScene);
        newStage.show();
    }

    public void sendSomething(Integer type) {
        try {
            System.out.println("Saatsin " + type);
            //vastavalt prokokollile:
            switch (type) {
                case 1:
                    out.writeInt(type);
                    String regname = regnamefield.getText();
                    String regpass = regpasswordfield.getText();
                    logIn_or_Register(regname, regpass);
                    break;
                case 2:
                    out.writeInt(type);
                    String loginname = loginnamefield.getText();
                    String loginpass = loginpasswordfield.getText();
                    logIn_or_Register(loginname, loginpass);
                    break;
                case 3:
                    out.writeInt(type);
                    //jääme ootama userlisti
                    break;
                case 4:
                    out.writeInt(type);
                    //väljalogimine
                    break;
                case 5:
                    out.writeInt(type);
                    sendAndClearField(konsool);
                    break;
                case 6:
                    out.writeInt(type);
                    //ootame tagasi käivate mängude listi
                    break;
                case 7:
                    out.writeInt(type);
                    String challengeeName = userListView.getSelectionModel().getSelectedItem();
                    out.writeInt(name_2_ID.get(challengeeName));
                    break;
            /*case 9:
                //todo keeldumine
                break;*/
                case 102:
                    out.writeInt(102);
                    break;
                case 105:
                    System.out.println("Saatsin kirja tetriseaknas kasutajale " + multiplayerGame.getOpponentID());
                    out.writeInt(105);
                    out.writeInt(multiplayerGame.getOpponentID());
                    out.writeUTF(multiplayerGame.getPrivateChat().sendMessageandclearMP());

                    break;
                default:
                    // ei tee midagi
            }
        } catch (IOException e) {
            disconnectionError();
            konsool.setDisable(true);

        }


    }

    public void requestRandomTetro() throws IOException {
        out.writeInt(103);
    }

    public void sendKeypress(Integer tickID, char key) throws IOException {
        System.out.println("Saatsin " + 101 + " " + String.valueOf(key));
        out.writeInt(101);
        out.writeInt(tickID);
        out.writeChar(key);
    }

    public void logIn_or_Register(String nimi, String parool) throws IOException {
        //nimi
        out.writeUTF(nimi);
        out.writeUTF(parool);
    }

    public void recieveMessage(int userID, String username, String message) {
        ekraan.appendText(username + ">> " + message + "\n");

        chatsound.seek(new Duration(0));
        chatsound.play();
    }

    public void handleUserList(Integer type, Integer ID, String name) {
        if (loggedIN) {
            switch (type) {
                case 3:
                    online_users.put(ID, name);
                    name_2_ID.put(name, ID);
                    // userListView.refresh();
                    break;
                case 4:
                    online_users.remove(ID, name);
                    name_2_ID.remove(name, ID);
                    // userListView.refresh();
                    break;
            }
            if (lobbyOpen) {
                observableUsers.clear();
                for (Integer id : online_users.keySet()) {
                    String nameofid = online_users.get(id);
                    observableUsers.add(nameofid);
                }
            }
        }
    }

    public void sendAndClearField(TextField ekraan) {
        try {
            out.writeUTF(ekraan.getText());
            out.flush();
            ekraan.clear();
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    void disconnectionError() {
        ekraan.appendText("Connection lost... Please restart to reconnect.");
        konsool.setDisable(true);
    }

    public void setChallengeOpen(boolean challengeOpen) {
        this.challengeOpen = challengeOpen;
    }

    public boolean isChallengeOpen() {
        return challengeOpen;
    }

    public TextArea getEkraan() {
        return ekraan;
    }

    public TextField getKonsool() {
        return konsool;
    }

    public MediaPlayer getGamenotificationsound() {
        return gamenotificationsound;
    }

    @Override
    public void start(Stage primaryStage) {
        self = this;
        //üritame käivitamisel ühenduse luua
        try (Socket socket = new Socket("tetris.carlnet.ee", 54321);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {
            this.connection = socket;
            this.out = output;
            System.out.println("Connection to tetris.carlnet.ee established...");

            Listener listener = new Listener(connection, this, input, toLoginorNot);
            Thread clienthread = new Thread(listener);
            clienthread.start();
            loggedIN = true;

            //näitame loginekraani
            showLogIn();
            if (loggedIN) {
                lobbyOpen = true;
                showLobby();
            }
        } catch (Exception e) {
            System.out.println("Error connecting to server.");
            loggedIN = false;
            showLogIn();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
