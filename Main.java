import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static class Move {
        int y;
        int x;
    }

    public static void main(String[] args) {
        char[][] map = new char[5][5];
        try (Scanner scanner = new Scanner(System.in)) {
            initMap(map);
            while (true) {
                System.out.print("Input command: ");
                String[] strings;
                try {
                    strings = scanner.nextLine().split(" ");
                } catch (NoSuchElementException e) {
                    System.out.println("Error!");
                    continue;
                }
                if (strings[0].equals("exit")) {
                    break;
                }
                if (strings.length != 3) {
                    System.out.println("Bad parameters!");
                    continue;
                }
                if (!strings[0].equals("start")) {
                    System.out.println("Bad parameters!");
                    continue;
                }
                startGame(strings[1], strings[2], map, scanner);
            }
        }
    }

    private static void startGame(String mode1, String mode2, char[][] map, Scanner scanner) {
        if (checkModes(mode1, mode2)) {
            return;
        }
        printMap(map);
        while (true) {
            chooseMove(mode1, map, scanner, true);
            if (checkWin(map)) {
                break;
            }
            chooseMove(mode2, map, scanner, false);
            if (checkWin(map)) {
                break;
            }
        }
    }

    private static boolean checkModes(String mode1, String mode2) {
        String[] modes = {"easy", "medium", "hard", "user"};
        int countMode1 = 0;
        int countMode2 = 0;
        for (String mode : modes) {
            if (mode.equals(mode1)) {
                countMode1++;
            }
            if (mode.equals(mode2)) {
                countMode2++;
            }
        }
        if (countMode1 == 0 || countMode2 == 0) {
            System.out.println("Bad parameters!");
            return true;
        }
        return false;
    }

    private static void chooseMove(String mode, char[][] map, Scanner scanner, boolean flag) {
        switch (mode) {
            case "easy":
                easyAIMove(map, flag);
                break;
            case "medium":
                mediumAIMove(map, flag);
                break;
            case "hard":
                hardAIMove(map, flag);
                break;
            case "user":
                userMove(map, scanner, flag);
                break;
        }
    }

    private static void hardAIMove(char[][] map, boolean flag) {
        System.out.println("Making move level \"hard\"");
        Move best = findBestMove(map, flag);
        if (flag) {
            map[best.y][best.x] = 'X';
        } else {
            map[best.y][best.x] = 'O';
        }
        printMap(map);
    }

    private static Move findBestMove(char[][] map, boolean flag) {
        char hardAi = 'O';
        if (flag) {
            hardAi = 'X';
        }
        int bestVal = -1000;
        Move best = new Move();
        for (int y = 1; y < 4; y++) {
            for (int x = 1; x < 4; x++) {
                if (map[y][x] == ' ') {
                    map[y][x] = hardAi;
                    int moveVal = minimax(map, 0, false, flag);
                    map[y][x] = ' ';
                    if (moveVal > bestVal) {
                        best.y = y;
                        best.x = x;
                        bestVal = moveVal;
                    }
                }
            }
        }
        return best;
    }

    private static int winnerValue(char[][] map, boolean flag) {
        if (flag) {
            if (checkWinForSymbol(map, 'X')) {
                return 10;
            } else if (checkWinForSymbol(map, 'O')){
                return -10;
            }
        } else {
            if (checkWinForSymbol(map, 'O')) {
                return 10;
            } else if (checkWinForSymbol(map, 'X')) {
                return -10;
            }
        }
        return 0;
    }

    private static int minimax(char[][] map, int depth, boolean isMax, boolean flag) {
        char human = 'X';
        char hardAi = 'O';
        if (flag) {
            hardAi = 'X';
            human = 'O';
        }
        int score = winnerValue(map, flag);
        if (score == 10) {
            return score;
        }
        if (score == -10) {
            return score;
        }
        if (countEmptyCells(map) == 0) {
            return 0;
        }
        int best;
        if (isMax) {
            best = -1000;
            for (int y = 1; y < 4; y++) {
                for (int x = 1; x < 4; x++) {
                    if (map[y][x] == ' ') {
                        map[y][x] = hardAi;
                        best = Math.max(best, minimax(map, depth + 1, false, flag));
                        map[y][x] = ' ';
                    }
                }
            }
        } else {
            best = 1000;
            for (int y = 1; y < 4; y++) {
                for (int x = 1; x < 4; x++) {
                    if (map[y][x] == ' ') {
                        map[y][x] = human;
                        best = Math.min(best, minimax(map, depth + 1, true, flag));
                        map[y][x] = ' ';
                    }
                }
            }
        }
        return best;
    }

    private static void mediumAIMove(char[][] map, boolean flag) {
        Random random = new Random();
        System.out.println("Making move level \"medium\"");
        if (flag) {
            searchPreWinMove(map, 'X', random);
        } else {
            searchPreWinMove(map, 'O', random);
        }
        printMap(map);
    }

    private static void searchPreWinMove(char[][] map, char c, Random random) {
        String[] lines = new String[3];
        String[] columns = new String[3];
        lines[0] = new String(new char[]{map[1][1], map[1][2], map[1][3]});
        lines[1] = new String(new char[]{map[2][1], map[2][2], map[2][3]});
        lines[2] = new String(new char[]{map[3][1], map[3][2], map[3][3]});
        columns[0] = new String(new char[]{map[1][1], map[2][1], map[3][1]});
        columns[1] = new String(new char[]{map[1][2], map[2][2], map[3][2]});
        columns[2] = new String(new char[]{map[1][3], map[2][3], map[3][3]});
        String diagonal1 = new String(new char[]{map[1][1], map[2][2], map[3][3]});
        String diagonal2 = new String(new char[]{map[1][3], map[2][2], map[3][1]});
        int[] yx = new int[2];
        checkLinesAndColumns(lines, columns, yx);
        if (yx[0] == 0 || yx[1] == 0) {
            checkDiagonals(diagonal1, diagonal2, yx);
        }
        if (yx[0] != 0 && yx[1] != 0) {
            map[yx[0]][yx[1]] = c;
        } else {
            while (true) {
                yx[0] = random.nextInt(3) + 1;
                yx[1] = random.nextInt(3) + 1;
                if (map[yx[0]][yx[1]] == ' ') {
                    map[yx[0]][yx[1]] = c;
                    break;
                }
            }
        }
    }

    private static void checkDiagonals(String diagonal1, String diagonal2, int[] yx) {
        String d1 = findMatch(diagonal1);
        String d2 = findMatch(diagonal2);
        if (d1 != null || d2 != null) {
            if (d1 != null) {
                yx[0] = d1.indexOf(' ') + 1;
                yx[1] = d1.indexOf(' ') + 1;
            } else {
                yx[0] = d2.indexOf(' ') + 1;
                yx[1] = d2.length() - d2.indexOf(' ');
            }
        }
    }

    private static void checkLinesAndColumns(String[] lines, String[] columns, int[] yx) {
        for (int i = 0; i < lines.length; i++) {
            String line = findMatch(lines[i]);
            String column = findMatch(columns[i]);
            if (line != null || column != null) {
                if (line != null) {
                    yx[0] = i + 1;
                    yx[1] = line.indexOf(' ') + 1;
                } else {
                    yx[0] = column.indexOf(' ') + 1;
                    yx[1] = i + 1;
                }
                break;
            }
        }
    }

    private static String findMatch(String s) {
        String[] variations = {" XX", "X X", "XX ", " OO", "O O", "OO "};
        for (String variation : variations) {
            if (s.equals(variation)) {
                return s;
            }
        }
        return null;
    }

    private static void initMap(char[][] map) {
        for (int y = 1; y < 4; y++) {
            for (int x = 1; x < 4; x++) {
                map[y][x] = ' ';
            }
        }
    }

    private static void printMap(char[][] map) {
        System.out.println("---------");
        for (int y = 1; y < 4; y++) {
            System.out.print("|");
            for (int x = 1; x < 4; x++) {
                System.out.print(" " + map[y][x]);
            }
            System.out.println(" |");
        }
        System.out.println("---------");
    }

    private static void userMove(char[][] map, Scanner scanner, boolean flag) {
        while (true) {
            System.out.print("Enter the coordinates: ");
            String[] strings;
            int y, x;
            try {
                strings = scanner.nextLine().split(" ");
                y = Integer.parseInt(strings[0]);
                x = Integer.parseInt(strings[1]);
            } catch (NoSuchElementException e) {
                System.out.println("Error!");
                continue;
            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
                continue;
            }
            if (y < 1 || y > 3 || x < 1 || x > 3) {
                System.out.println("Coordinates should be from 1 to 3!");
                continue;
            }
            if (map[y][x] != ' ') {
                System.out.println("This cell is occupied! Choose another one!");
                continue;
            }
            if (flag) {
                map[y][x] = 'X';
            } else {
                map[y][x] = 'O';
            }
            printMap(map);
            break;
        }
    }

    private static void easyAIMove(char[][] map, boolean flag) {
        Random random = new Random();
        System.out.println("Making move level \"easy\"");
        while (true) {
            int y = random.nextInt(3) + 1;
            int x = random.nextInt(3) + 1;
            if (map[y][x] == ' ') {
                if (flag) {
                    map[y][x] = 'X';
                } else {
                    map[y][x] = 'O';
                }
                break;
            }
        }
        printMap(map);
    }

    private static boolean checkWin(char[][] map) {
        boolean flag = false;
        if (checkWinForSymbol(map, 'X')) {
            System.out.println("X wins");
            flag = true;
        } else if (checkWinForSymbol(map, 'O')) {
            System.out.println("O wins");
            flag = true;
        } else if (countEmptyCells(map) == 0) {
            System.out.println("Draw");
            flag = true;
        }
        if (flag) {
            initMap(map);
            return true;
        }
        return false;
    }

    private static int countEmptyCells(char[][] map) {
        int count = 0;
        for (int y = 1; y < 4; y++) {
            for (int x = 1; x < 4; x++) {
                if (map[y][x] == ' ') {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean checkWinForSymbol(char[][] map, char c) {
        boolean flag = false;
        if (map[1][1] == c && map[1][2] == c && map[1][3] == c) {
            flag = true;
        } else if (map[2][1] == c && map[2][2] == c && map[2][3] == c) {
            flag = true;
        } else if (map[3][1] == c && map[3][2] == c && map[3][3] == c) {
            flag = true;
        } else if (map[1][1] == c && map[2][2] == c && map[3][3] == c) {
            flag = true;
        } else if (map[3][1] == c && map[2][2] == c && map[1][3] == c) {
            flag = true;
        } else if (map[1][1] == c && map[2][1] == c && map[3][1] == c) {
            flag = true;
        } else if (map[1][2] == c && map[2][2] == c && map[3][2] == c) {
            flag = true;
        } else if (map[1][3] == c && map[2][3] == c && map[3][3] == c) {
            flag = true;
        }
        return flag;
    }
}
