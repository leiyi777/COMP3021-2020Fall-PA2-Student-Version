package castle.comp3021.assignment.gui.controllers;

import castle.comp3021.assignment.gui.GUIMain;
import castle.comp3021.assignment.protocol.exception.ResourceNotFoundException;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper class for loading resources from the filesystem.
 */
public class ResourceLoader {
    /**
     * Path to the resources directory.
     */
    @NotNull
    private static final Path RES_PATH;

    static {
        // TODO: Initialize RES_PATH
        RES_PATH = Path.of(Paths.get("").toAbsolutePath() + "/src/main/resources");
    }

    /**
     * Retrieves a resource file from the resource directory.
     *
     * @param relativePath Path to the resource file, relative to the root of the resource directory.
     * @return Absolute path to the resource file.
     * @throws ResourceNotFoundException If the file cannot be found under the resource directory.
     */
    @NotNull
    public static String getResource(@NotNull final String relativePath) {
        // TODO
        String absolute_path = Path.of(RES_PATH + "/" + relativePath).toString();
        if(!new File(absolute_path).exists())
            throw new ResourceNotFoundException("Can not find resource in: " + absolute_path);
        else
            return absolute_path;
    }

    /**
     * Return an image {@link Image} object
     * @param typeChar a character represents the type of image needed.
     *                 - 'K': white knight (whiteK.png)
     *                 - 'A': white archer (whiteA.png)
     *                 - 'k': black knight (blackK.png)
     *                 - 'a': black archer (blackA.png)
     *                 - 'c': central x (center.png)
     *                 - 'l': light board (lightBoard.png)
     *                 - 'd': dark board (darkBoard.png)
     * @return an image
     */
    @NotNull
    public static Image getImage(char typeChar) {
        // TODO
        Image image;
        if(typeChar == 'K')
            image = new Image(RES_PATH + "/assets/images/whiteK.png");
        else if(typeChar == 'A')
            image = new Image(RES_PATH + "/assets/images/whiteA.png");
        else if(typeChar == 'k')
            image = new Image(RES_PATH + "/assets/images/blackK.png");
        else if(typeChar == 'a')
            image = new Image(RES_PATH + "/assets/images/blackA.png");
        else if(typeChar == 'l')
            image = new Image(RES_PATH + "/assets/images/lightBoard.png");
        else if(typeChar == 'd')
            image = new Image(RES_PATH + "/assets/images/darkBoard.png");
        else
            image = new Image(RES_PATH + "/assets/images/center.png");
        return image;
    }


}