package ru.podorozhnyk.application.morse;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MorseSoundPlayer {
    public static final int UNIT_MS = 100; // Базовая единица времени
    public static final int DOT_MS = UNIT_MS;
    public static final int DASH_MS = 3 * UNIT_MS;

    private static final int SAMPLE_RATE = 74100; // Частота дискретизации (Гц)
    private static final int FREQUENCY = 670;     // Частота тона (Гц)
    private static final float VOLUME = 1;

    private static Clip currentClip;
    private static boolean isPlaying;
    private static Thread thread;
    private static AudioInputStream audioStream;
    private static final List<byte[]> audioChunks = new ArrayList<>();

    public static void playDot() throws LineUnavailableException, IOException {
        playBeep(DOT_MS);
    }

    public static void playDash() throws LineUnavailableException, IOException {
        playBeep(DASH_MS);
    }

    private static void playBeep(int durationMs) throws LineUnavailableException, IOException {
        byte[] buffer = generateTone(durationMs);
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        audioStream = new AudioInputStream(
                new ByteArrayInputStream(buffer),
                format,
                buffer.length
        );

        audioChunks.add(buffer);
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);
        currentClip.start();

        try { Thread.sleep(durationMs); } catch (InterruptedException ignored) { }
    }

    private static byte[] generateTone(int durationMs) {
        int samples = durationMs * SAMPLE_RATE / 1000;
        byte[] buffer = new byte[samples];

        for (int i = 0; i < samples; i++) {
            double angle = 2.0 * Math.PI * i * FREQUENCY / SAMPLE_RATE;
            buffer[i] = (byte) (Math.sin(angle) * 127 * VOLUME);
        }

        return buffer;
    }

    public static synchronized void playMorseCode(String morseCode){
        Utils.requireNonBlank(morseCode, "\"morseCode\" is null or blank.");

        thread = new Thread(() -> {
            isPlaying = true;
            try {
                for (String word : morseCode.split(" {2}")) {
                    for (String letter : word.split(" ")) {
                        for (char unit : letter.toCharArray()) {
                            if (!isPlaying()) return;

                            switch (unit) {
                                case '.':
                                    playDot();
                                    break;
                                case '-':
                                    playDash();
                                    break;
                            }
                        }
                        Thread.sleep(UNIT_MS * 3);
                    }
                    Thread.sleep(UNIT_MS * 7);
                }
            }
            catch (InterruptedException | LineUnavailableException | IOException e) {
                throw new AssertionError(e);
            }
        });
        thread.start();
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static void stop() {
        currentClip.close();
        currentClip.stop();
        isPlaying = false;
    }
}
