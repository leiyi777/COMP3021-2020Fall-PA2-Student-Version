package castle.comp3021.assignment.protocol.io;

import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.exception.InvalidConfigurationError;
import castle.comp3021.assignment.protocol.exception.InvalidGameException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class Deserializer {
    @NotNull
    private Path path;

    private Configuration configuration;

    private Integer[] storedScores;

    Place centralPlace;

    private ArrayList<MoveRecord> moveRecords = new ArrayList<>();



    public Deserializer(@NotNull final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) {
            throw new FileNotFoundException("Cannot find file to load!");
        }

        this.path = path;
    }

    /**
     * Returns the first non-empty and non-comment (starts with '#') line from the reader.
     *
     * @param br {@link BufferedReader} to read from.
     * @return First line that is a parsable line, or {@code null} there are no lines to read.
     * @throws IOException if the reader fails to read a line
     * @throws InvalidGameException if unexpected end of file
     */
    @Nullable
    private String getFirstNonEmptyLine(@NotNull final BufferedReader br) throws IOException {
        // TODO
        String lineRead = "";
        while(true) {
            lineRead = br.readLine();
            if(!lineRead.isEmpty() && !lineRead.contains("#"))
                break;
        }
        return lineRead;
    }

    public void parseGame() throws InvalidGameException, InvalidConfigurationError{
        try (var reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;

            int size;
            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                // TODO: get size here
                size = Integer.parseInt(line.substring("size:".length()));
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing number of board size");
            }

            int numMovesProtection;
            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                // TODO: get numMovesProtection here
                numMovesProtection = Integer.parseInt(line.substring("numMovesProtection:".length()));
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing number of columns");
            }

            //TODO
            /**
             *  read central place here
             *  If success, assign to {@link Deserializer#centralPlace}
             *  Hint: You may use {@link Deserializer#parsePlace(String)}
             */
            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                centralPlace = parsePlace(line);
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing the central place");
            }

            int numPlayers;
            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                //TODO: get number of players here
                numPlayers = Integer.parseInt(line.substring("numPlayers:".length()));
                if(numPlayers != 2)
                    throw new InvalidGameException("Unexpected EOF when parsing number of players");
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing number of players");
            }


            // TODO:
            /**
             * create an array of players {@link Player} with length of numPlayers, and name it by the read-in name
             * Also create an array representing scores {@link Deserializer#storedScores} of players with length of numPlayers
             */
            Player[] players = new Player[numPlayers];
            storedScores = new Integer[numPlayers];
            for(int i = 0; i < numPlayers; i++) {
                line = getFirstNonEmptyLine(reader);
                if (line != null) {
                    //name:White; score:9
                    String[] info = line.split("; ");
                    players[i] = new ConsolePlayer(info[0].substring("name:".length()));
                    storedScores[i] = Integer.parseInt(info[1].substring("score:".length()));
                } else {
                    throw new InvalidGameException("Unexpected EOF when parsing player info");
                }
            }

            // TODO
            /**
             * try to initialize a configuration object  with the above read-in variables
             * if fail, throw InvalidConfigurationError exception
             * if success, assign to {@link Deserializer#configuration}
             */
            try {
                configuration = new Configuration(size, players, numMovesProtection);
            } catch (InvalidConfigurationError e) {
                throw new InvalidConfigurationError(e.getMessage());
            }


            // TODO
            /**
             * Parse the string of move records into an array of {@link MoveRecord}
             * Assign to {@link Deserializer#moveRecords}
             * You should first implement the following methods:
             * - {@link Deserializer#parseMoveRecord(String)}}
             * - {@link Deserializer#parseMove(String)} ()}
             * - {@link Deserializer#parsePlace(String)} ()}
             */
            for(line = getFirstNonEmptyLine(reader); !line.equals("END"); line = getFirstNonEmptyLine(reader))
                moveRecords.add(parseMoveRecord(line));

        } catch (IOException ioe) {
            throw new InvalidGameException(ioe);
        }
    }

    public Configuration getLoadedConfiguration(){
        return configuration;
    }

    public Integer[] getStoredScores(){
        return storedScores;
    }

    public ArrayList<MoveRecord> getMoveRecords(){
        return moveRecords;
    }

    /**
     * Parse the string into a {@link MoveRecord}
     * Handle InvalidConfigurationError if the parse fails.
     * @param moveRecordString a string of a move record
     * @return a {@link MoveRecord}
     */
    private MoveRecord parseMoveRecord(String moveRecordString){
        // TODO
        String[] split = moveRecordString.split("; ");
        String playerName = split[0].substring("player:".length());
        Move move = parseMove(split[1]);
        return new MoveRecord(getLoadedConfiguration().getPlayers()[0].getName().equals(playerName) ?
                getLoadedConfiguration().getPlayers()[0] : getLoadedConfiguration().getPlayers()[1], move);
    }

    /**
     * Parse a string of move to a {@link Move}
     * Handle InvalidConfigurationError if the parse fails.
     * @param moveString given string
     * @return {@link Move}
     */
    private Move parseMove(String moveString) {
        // TODO
        //move:2,0)   (3,2
        String subMoveString = moveString.substring("move:(".length(), moveString.length() - 1);
        String[] sourceAndDest = subMoveString.split("->");
        String[] source = sourceAndDest[0].split(",");
        String[] dest = sourceAndDest[1].split(",");

        int sourceX = Integer.parseInt(source[0].substring(0,1));
        int sourceY = Integer.parseInt(source[1].substring(0,1));
        int destX = Integer.parseInt(dest[0].substring(1,2));
        int destY = Integer.parseInt(dest[1].substring(0,1));

        return new Move(sourceX, sourceY, destX, destY);
    }

    /**
     * Parse a string of move to a {@link Place}
     * Handle InvalidConfigurationError if the parse fails.
     * @param placeString given string
     * @return {@link Place}
     */
    private Place parsePlace(String placeString) {
        //TODO
        String temp = placeString.substring("centralPlace:(".length(), placeString.length() - 1);
        String[] split = temp.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        return new Place(x, y);
    }


}
