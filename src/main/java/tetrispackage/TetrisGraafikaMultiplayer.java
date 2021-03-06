package tetrispackage;

import chati_leiutis.Klient;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class TetrisGraafikaMultiplayer {
    private int numberOfPlayers = 2;
    private final int resoWidth = 150 * 3;
    private final int resoHeight = 330;
    private IntegerProperty tickProperty = new SimpleIntegerProperty();
    private int randomTetroRequestSent = 0;
    public static final char UP = 0;
    public static final char DOWN = 1;
    public static final char LEFT = 2;
    public static final char RIGHT = 3;
    private int syncproblem = 0;

    private Tetromino myTetromino;
    private Tetromino opponentTetromino;
    private Map<KeyCode, Boolean> myCurrentActiveKeys = new HashMap<>();
    private Klient client;

    //chati
    private Integer opponentID;
    private IntegerProperty opponentMoved = new SimpleIntegerProperty();
    private int opponentMoveTiksuID = 0;
    private PrivateChat privateChat;

    public TetrisGraafikaMultiplayer() {
        opponentMoved.setValue(-1);
    }

    //lisasin client, et kasutada Klient klassi meetodeid
    public void start(Stage peaLava, Klient client, Integer opponentID) {
        this.client = client;
        privateChat = new PrivateChat(client);
        this.opponentID = opponentID;
        HBox root = new HBox(10);
        Group localTetrisArea = new Group(); // luuakse localTetrisArea
        Group opponentTetrisArea = new Group();

        TetrisRectangle localTetrisRect = new TetrisRectangle();
        localTetrisRect.fill(localTetrisArea);
        TetrisRectangle opponentTetrisRect = new TetrisRectangle();
        opponentTetrisRect.fill(opponentTetrisArea);

        myTetromino = new Tetromino(localTetrisRect.getRistkülik());
        opponentTetromino = new Tetromino(opponentTetrisRect.getRistkülik());
        ScoreHandler scoreHandler = new ScoreHandler(myTetromino, opponentTetromino);

        //noded-e paigutamine
        root.getChildren().add(localTetrisArea);
        root.getChildren().add(scoreHandler.getScoreArea());
        root.getChildren().add(opponentTetrisArea);
        root.getChildren().add(privateChat.getChatArea());

        tickProperty.addListener((o, oldVal, newVal) -> {
            tickAndDrawForMe();
            tickAndDrawForOpponent();
        });
        opponentMoved.addListener((o, oldVal, newVal) -> {
            moveOpponentTetro();
            opponentMoved.setValue(-1);
        });

        //kood selleks, et klikkides tetrise mängule deselectib chatirea.(Muidu ei saa klotse liigutada peale chattimist)
        localTetrisArea.setOnMouseClicked(me -> root.requestFocus());
        opponentTetrisArea.setOnMouseClicked(me -> root.requestFocus());

        peaLava.setOnCloseRequest((we) -> closeGame());
        Scene tetrisStseen = new Scene(root, TetrisGraafika.getResoWidth() * 2 + privateChat.getWidth() * 2, resoHeight, Color.SNOW);  // luuakse stseen
        tetrisStseen.setOnKeyPressed(event -> {
            myCurrentActiveKeys.put(event.getCode(), true);
            checkPressedKeysAndMove();

        });
        tetrisStseen.setOnKeyReleased(event ->
                myCurrentActiveKeys.put(event.getCode(), false)
        );

        peaLava.setTitle("Tetris");  // lava tiitelribale pannakse tekst

        peaLava.setScene(tetrisStseen);  // lavale lisatakse stseen
        peaLava.show();  // lava tehakse nähtavaks
    }

    void closeGame() {
        System.out.println("Tetris stage closed!");
        //multiplayer läheb kinni
        client.setMpgameopen(false);
        try {
            client.sendSomething(102);
        } catch (Exception e) {
            System.out.println("Failed to send info about closing/exiting the MP game");
        }
        Platform.exit();
    }

    void checkPressedKeysAndMove() {
        if (!myTetromino.isDrawingAllowed() && !myTetromino.gameStateOver()) {
            if (myCurrentActiveKeys.containsKey(KeyCode.RIGHT) && myCurrentActiveKeys.get(KeyCode.RIGHT)) {
                if (myTetromino.moveRight()) {
                    try {
                        client.sendKeypress(tickProperty.getValue(), RIGHT);
                    } catch (IOException error) {
                        privateChat.keyPressSendingFailed();
                    }
                }
            }
            if (myCurrentActiveKeys.containsKey(KeyCode.LEFT) && myCurrentActiveKeys.get(KeyCode.LEFT)) {
                if (myTetromino.moveLeft()) {
                    try {
                        client.sendKeypress(tickProperty.getValue(), LEFT);
                    } catch (IOException error) {
                        privateChat.keyPressSendingFailed();
                    }
                }
            }
            if (myCurrentActiveKeys.containsKey(KeyCode.UP) && myCurrentActiveKeys.get(KeyCode.UP)) {
                if (myTetromino.rotate()) {
                    try {
                        client.sendKeypress(tickProperty.getValue(), UP);
                    } catch (IOException error) {
                        privateChat.keyPressSendingFailed();
                    }
                }
            }
            if (myCurrentActiveKeys.containsKey(KeyCode.DOWN) && myCurrentActiveKeys.get(KeyCode.DOWN)) {
                try {
                    client.sendKeypress(tickProperty.getValue(), DOWN);
                } catch (IOException error) {
                    privateChat.keyPressSendingFailed();
                }
                myTetromino.drop();
                myTetromino.setNewRandomTetroReceived(false);
                randomTetroRequestSent = 0;
            }
        }
    }

    void moveOpponentTetro() {
        if (!opponentTetromino.isDrawingAllowed() && !opponentTetromino.gameStateOver()) {
            if (opponentMoved.getValue() == 2) {
                opponentTetromino.moveLeft();
            } else if (opponentMoved.getValue() == 3) {
                opponentTetromino.moveRight();
            } else if (opponentMoved.getValue() == 0) {
                opponentTetromino.rotate();
            } else if (opponentMoved.getValue() == 1) {
                opponentTetromino.drop();
            }
        }
    }

    void tickAndDrawForMe() {
        if (!myTetromino.gameStateOver()) {
            if (myTetromino.isDrawingAllowed()) {
                if (randomTetroRequestSent == 0) //Only send one request, 1 by default
                {
                    randomTetroRequestSent = 1;
                    myTetromino.setNewRandomTetroReceived(false);  //When it is time to draw a new tetro, disallow ticking and drawing until received
                    try {
                        client.requestRandomTetro();
                        System.out.println("Requesting random tetro");
                    } catch (Exception error) {
                        System.out.println("Socket closed. Keypress sending failed!");
                    }
                }
            }
            if (myTetromino.isNewRandomTetroReceived()) {//wait until new randomtetro received
                if (myTetromino.isDrawingAllowed()) {
                    myTetromino.draw("multiplayer"); //after drawing, getDrawingTurns is no longer 2
                }
                myTetromino.tick();

                if (myTetromino.getDrawingTurns() == 2) {
                    randomTetroRequestSent = 0;
                }
            }
        } else {
            client.sendSomething(104);
        }
    }

    void tickAndDrawForOpponent() {
        if (!opponentTetromino.gameStateOver()) {
            if (!opponentTetromino.isDrawingAllowed()){
                opponentTetromino.tick();
            }
            if (opponentTetromino.isDrawingAllowed() && opponentTetromino.isNewRandomTetroReceived()) {
                opponentTetromino.tick();
                opponentTetromino.draw("multiplayer");
                if (opponentTetromino.getDrawingTurns() == 0) {
                    opponentTetromino.setNewRandomTetroReceived(false);
                }
            }


        }
    }


    public Integer getOpponentID() {
        return opponentID;
    }

    public void setTickValue(int value) {
        tickProperty.set(value);
    }

    public void setOpponentMoved(int state) {
        this.opponentMoved.setValue(state);
    }

    public Tetromino getMyTetromino() {
        return myTetromino;
    }

    public Tetromino getOpponentTetromino() {
        return opponentTetromino;
    }

    public void setOpponentMoveTiksuID(int opponentMoveTiksuID) {
        this.opponentMoveTiksuID = opponentMoveTiksuID;
    }

    public PrivateChat getPrivateChat() {
        return privateChat;
    }
}
