package ru.podorozhnyk.application.morse;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;

public final class FileUtils {

    private FileUtils() {
    }

    public static void saveTextToFile(String fileName, String text) {

    }

    public static void saveAudioToFile(String fileName, AudioInputStream stream) throws IOException {
        File file = new File(fileName);
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);
    }
}
