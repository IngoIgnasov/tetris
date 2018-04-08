package tetrispackage;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tetromino {
    private Map<Integer, Character> rectToStatus = new HashMap<>();
    private int drawingTurns = 2;
    private boolean allowTetrominoDrawing = true;
    private Color activeTetrominoColor;
    private Rectangle[][] ruudud;
    private boolean allActiveToPassive = false;
    private char tetrominoType;
    private int tetrominoRotationTracker = 0;

    public Tetromino(Rectangle[][] ristkülik) {
        ruudud = ristkülik;
        for (int i = 0; i < ruudud.length; i++) {
            for (int j = 0; j < ruudud[i].length; j++) {
                setRectStatusAt(i, j, 'B'); //B for background
            }
        }
    }

    public char getRectStatusAt(int i, int j) {
        return rectToStatus.get(ruudud[i].length * i + j);
    }

    public void setRectStatusAt(int i, int j, char status) {
        rectToStatus.put(ruudud[i].length * i + j, status);
        if (status == 'B') {
            ruudud[i][j].setFill(Color.DARKKHAKI);
        } else if (status == 'A') {
            ruudud[i][j].setFill(activeTetrominoColor);
        }
    }

    void moveRight() {
        List<Integer> activeRectCoordI = new ArrayList<>();
        List<Integer> activeRectCoordJ = new ArrayList<>();
        boolean movingRightPossible = true;
        for (int i = ruudud.length - 1; i >= 0; i--) {
            for (int j = ruudud[i].length - 1; j >= 0; j--) {
                if (getRectStatusAt(i, j) == 'A') {
                    activeRectCoordI.add(i); //remember where the active blocks are
                    activeRectCoordJ.add(j);
                    if (j + 1 >= ruudud[i].length || getRectStatusAt(i, j + 1) == 'P') {
                        movingRightPossible = false;
                    }
                }
            }
        }
        if (movingRightPossible) {
            for (int i = 0; i < activeRectCoordI.size(); i++) {
                setRectStatusAt(activeRectCoordI.get(i), activeRectCoordJ.get(i) + 1, 'A');
                setRectStatusAt(activeRectCoordI.get(i), activeRectCoordJ.get(i), 'B');
            }
        }
    }

    void moveLeft() {
        List<Integer> activeRectCoordI = new ArrayList<>();
        List<Integer> activeRectCoordJ = new ArrayList<>();
        boolean movingLeftPossible = true;
        for (int i = ruudud.length - 1; i >= 0; i--) {
            for (int j = 0; j < ruudud[i].length; j++) {
                if (getRectStatusAt(i, j) == 'A') {
                    activeRectCoordI.add(i); //remember where the active blocks are
                    activeRectCoordJ.add(j);
                    if (j - 1 < 0 || getRectStatusAt(i, j - 1) == 'P') {
                        movingLeftPossible = false;
                    }
                }
            }
        }
        if (movingLeftPossible) {
            for (int i = 0; i < activeRectCoordI.size(); i++) {
                setRectStatusAt(activeRectCoordI.get(i), activeRectCoordJ.get(i) - 1, 'A');
                setRectStatusAt(activeRectCoordI.get(i), activeRectCoordJ.get(i), 'B');
            }
        }
    }
    void rotateLeft() {
        List<Integer> activeRectCoordI = new ArrayList<>();
        List<Integer> activeRectCoordJ = new ArrayList<>();
        for (int i = 0; i < ruudud.length; i++) {  //Find all active blocks
            for (int j = 0; j < ruudud[i].length; j++) {
                if (getRectStatusAt(i, j) == 'A') {
                    activeRectCoordI.add(i); //remember where the active blocks are
                    activeRectCoordJ.add(j);
                    /*if (i + 1 == ruudud.length || getRectStatusAt(i+1, j) == 'P') {
                        allActiveToPassive = true;
                    }*/
                }
            }
        }
        List<Integer> changedActiveRectCoordI = new ArrayList<>();
        List<Integer> changedActiveRectCoordJ = new ArrayList<>();
        boolean canRotate = true;
        System.out.println("Rotate left triggered");
        if (tetrominoType == 'S'){
            if (tetrominoRotationTracker == 0) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0));
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1) - 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3));
            }
            else if (tetrominoRotationTracker == 1){
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(1) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3));
            }
            else if (tetrominoRotationTracker == 2){
                changedActiveRectCoordI.add(activeRectCoordI.get(0));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0));
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2) + 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3));
            }
            else if (tetrominoRotationTracker == 3){
                changedActiveRectCoordI.add(activeRectCoordI.get(0));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0));
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) - 2);
            }
        }
        if (tetrominoType == 'Z') {
            if (tetrominoRotationTracker == 0) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0));
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) - 2);
            } else if (tetrominoRotationTracker == 1) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0));
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) + 2);
            } else if (tetrominoRotationTracker == 2) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3));
            } else if (tetrominoRotationTracker == 3) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) - 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3));
            }
        }
        if (tetrominoType == 'T') {
            if (tetrominoRotationTracker == 0) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0));
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) - 1);
            } else if (tetrominoRotationTracker == 1) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3));
            } else if (tetrominoRotationTracker == 2) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3));
            } else if (tetrominoRotationTracker == 3) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0));
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) - 1);
            }
        }
        if (tetrominoType == 'L') {
            if (tetrominoRotationTracker == 0) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) - 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(1) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) - 1);
            } else if (tetrominoRotationTracker == 1) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0));
                changedActiveRectCoordI.add(activeRectCoordI.get(1) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1) - 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) + 1);
            } else if (tetrominoRotationTracker == 2) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) + 1);
            } else if (tetrominoRotationTracker == 3) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2) - 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3));
            }
        }
        if (tetrominoType == 'J') {
            if (tetrominoRotationTracker == 0) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) - 1);
            } else if (tetrominoRotationTracker == 1) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) - 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2) + 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) + 1);
            } else if (tetrominoRotationTracker == 2) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) - 1);
            } else if (tetrominoRotationTracker == 3) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) - 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1) - 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) + 1);
            }
        }
        else if (tetrominoType == 'I') {
            if (tetrominoRotationTracker == 0) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2) - 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(3) + 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) - 2);
            }
            else if (tetrominoRotationTracker == 1){
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) - 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(1) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1));
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) + 2);

            }
            else if (tetrominoRotationTracker == 2) {
                changedActiveRectCoordI.add(activeRectCoordI.get(0) - 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) + 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(1) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1) + 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(2));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) - 1);
            }
            else if (tetrominoRotationTracker == 3){
                changedActiveRectCoordI.add(activeRectCoordI.get(0) + 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(0) - 2);
                changedActiveRectCoordI.add(activeRectCoordI.get(1));
                changedActiveRectCoordJ.add(activeRectCoordJ.get(1) - 1);
                changedActiveRectCoordI.add(activeRectCoordI.get(2) - 1);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(2));
                changedActiveRectCoordI.add(activeRectCoordI.get(3) - 2);
                changedActiveRectCoordJ.add(activeRectCoordJ.get(3) + 1);
            }
        }
        for (int i = 0; i < changedActiveRectCoordI.size(); i++) {
            if (changedActiveRectCoordI.get(i) < 0 || changedActiveRectCoordJ.get(i) < 0
                    || changedActiveRectCoordI.get(i) >= ruudud.length ||
                    changedActiveRectCoordJ.get(i) >= ruudud[0].length ||
            getRectStatusAt(changedActiveRectCoordI.get(i),changedActiveRectCoordJ.get(i)) == 'P'){
                canRotate = false;
            }
        }
        System.out.println("Can rotate: " + canRotate);
        if (canRotate) {
            tetrominoRotationTracker += 1;
            if (tetrominoRotationTracker == 4){
                tetrominoRotationTracker = 0;
            }
            for (int i = 0; i < activeRectCoordI.size(); i++) {
                setRectStatusAt(activeRectCoordI.get(i), activeRectCoordJ.get(i), 'B');
            }
            for (int i = 0; i < changedActiveRectCoordI.size(); i++) {
                setRectStatusAt(changedActiveRectCoordI.get(i), changedActiveRectCoordJ.get(i), 'A');
            }
        }
    }

    private void transponeeri(char[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix[i].length; j++) {
                char ajutine = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = ajutine;
            }
        }
        System.out.println("Transponeeritud:");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
    }

    private void vahetaRead(char[][] matrix) {
        for (int i = 0; i < matrix.length / 2; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                char ajutine = matrix[i][j];
                matrix[i][j] = matrix[(matrix.length - 1) - i][j];
                matrix[(matrix.length - 1) - i][j] = ajutine;
            }
        }
        System.out.println("Read vahetatud:");
        for (int k = 0; k < matrix.length; k++) {
            for (int l = 0; l < matrix[k].length; l++) {
                System.out.print(matrix[k][l]);
            }
            System.out.println();
        }
    }

    void tick() {
        List<Integer> activeRectCoordI = new ArrayList<>();
        List<Integer> activeRectCoordJ = new ArrayList<>();
        for (int i = ruudud.length - 1; i >= 0; i--) {  //Find all active blocks and check if all active blocks need to be set to passive
            for (int j = ruudud[i].length - 1; j >= 0; j--) {
                if (getRectStatusAt(i, j) == 'A') {
                    activeRectCoordI.add(i); //remember where the active blocks are
                    activeRectCoordJ.add(j);
                    if (i + 1 == ruudud.length || getRectStatusAt(i + 1, j) == 'P') {
                        allActiveToPassive = true;
                    }

                }
            }
        }
        if (allActiveToPassive) {
            System.out.println("All active to passive!");
            for (int k = 0; k < ruudud.length; k++) {
                for (int l = 0; l < ruudud[k].length; l++) {
                    if (getRectStatusAt(k, l) == 'A') {
                        setRectStatusAt(k, l, 'P');
                    }
                }
            }
            drawingTurns = 2;
            allowTetrominoDrawing = true;
            allActiveToPassive = false;
        } else if (!allActiveToPassive) {
            for (int i = 0; i < activeRectCoordI.size(); i++) {
                setRectStatusAt(activeRectCoordI.get(i) + 1, activeRectCoordJ.get(i), 'A');
                setRectStatusAt(activeRectCoordI.get(i), activeRectCoordJ.get(i), 'B');
            }


        }
    }

    //Tetriminos I O T J L S Z
    void draw(char tetrominoType) {
        this.tetrominoType = tetrominoType;
        drawingTurns -= 1;
        tetrominoRotationTracker = 0;
        if (tetrominoType == 'I') {
            int i = 0;
            int j = 0;
            activeTetrominoColor = Color.CYAN;
            for (; j < 4; j++) {
                setRectStatusAt(i, j, 'A');
            }
            drawingTurns -= 1;
        } else if (tetrominoType == 'O') {
            activeTetrominoColor = Color.YELLOW;
            setRectStatusAt(0, 0, 'A');
            setRectStatusAt(0, 1, 'A');
        } else if (tetrominoType == 'Z') {
            if (drawingTurns == 1) {
                activeTetrominoColor = Color.RED;
                setRectStatusAt(0, 1, 'A');
                setRectStatusAt(0, 2, 'A');
            } else {
                setRectStatusAt(0, 0, 'A');
                setRectStatusAt(0, 1, 'A');
            }
        } else if (tetrominoType == 'S') {
            activeTetrominoColor = Color.LIME;
            if (drawingTurns == 1) {
                setRectStatusAt(0, 0, 'A');
                setRectStatusAt(0, 1, 'A');
            } else {
                setRectStatusAt(0, 1, 'A');
                setRectStatusAt(0, 2, 'A');
            }
        } else if (tetrominoType == 'T') {
            activeTetrominoColor = Color.PURPLE;
            if (drawingTurns == 1) {
                setRectStatusAt(0, 0, 'A');
                setRectStatusAt(0, 1, 'A');
                setRectStatusAt(0, 2, 'A');

            } else {
                setRectStatusAt(0, 1, 'A');
            }
        }
        else if (tetrominoType == 'L') {
            activeTetrominoColor = Color.ORANGE;
            if (drawingTurns == 0) {
                setRectStatusAt(0, 2, 'A');

            } else {
                setRectStatusAt(0, 0, 'A');
                setRectStatusAt(0, 1, 'A');
                setRectStatusAt(0, 2, 'A');
            }
        }
        else if (tetrominoType == 'J') {
            activeTetrominoColor = Color.BLUE;
            if (drawingTurns == 0) {
                setRectStatusAt(0, 0, 'A');

            } else {
                setRectStatusAt(0, 0, 'A');
                setRectStatusAt(0, 1, 'A');
                setRectStatusAt(0, 2, 'A');
            }
        }
        if (drawingTurns == 0) {
            System.out.println("TetriminoDrawing false!");
            setDrawingPermission(false);
        }
    }

    public void setDrawingPermission(boolean allowTetrominoDrawing) {
        this.allowTetrominoDrawing = allowTetrominoDrawing;
    }

    public boolean isDrawingAllowed() {
        return allowTetrominoDrawing;
    }

    public int getDrawingTurns() {
        return drawingTurns;
    }

    public void setDrawingTurns(int drawingTurns) {
        this.drawingTurns = drawingTurns;
    }
}
