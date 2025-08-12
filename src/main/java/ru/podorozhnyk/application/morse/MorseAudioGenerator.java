package ru.podorozhnyk.application.morse;

import javax.sound.sampled.*;
import java.io.*;

public final class MorseAudioGenerator {
    private static final float SAMPLE_RATE = 8000f; // 8 kHz
    private static final int SAMPLE_SIZE_BITS = 16;
    private static final int CHANNELS = 1; // mono channel
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;

    private static final int UNIT_MS = 100;
    private static final int DOT_DURATION = UNIT_MS;
    private static final int DASH_DURATION = 3 * UNIT_MS;
    private static final int SYMBOL_GAP = UNIT_MS;
    private static final int LETTER_GAP = 3 * UNIT_MS;
    private static final int WORD_GAP = 7 * UNIT_MS;

    private static final int TONE_FREQUENCY = 800;

    private static Clip currentClip;

    private static final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public static void playMorse() {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        AudioInputStream audioStream = new AudioInputStream(
                new ByteArrayInputStream(outputStream.toByteArray()),
                format,
                outputStream.toByteArray().length
        );
        try {
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);
            currentClip.start();
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void stop() {
        currentClip.stop();
        currentClip.close();
    }

    public static ByteArrayOutputStream generateMorseCode(String morseCode) {
        Utils.requireNonBlank(morseCode, "\"morseCode\" is null or blank.");
        outputStream.reset();
        try {
            for (String word : morseCode.split(" {2}")) {
                for (String letter : word.split(" ")) {
                    for (char unit : letter.toCharArray()) {
                        switch (unit) {
                            case '.':
                                addDot();
                                break;
                            case '-':
                                addDash();
                                break;
                        }
                    }
                    addSilence(LETTER_GAP);
                }
                addSilence(WORD_GAP);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream;
    }

    private static void addDot() throws IOException {
        addTone(DOT_DURATION);
        addSilence(SYMBOL_GAP);
    }

    private static void addDash() throws IOException {
        addTone(DASH_DURATION);
        addSilence(SYMBOL_GAP);
    }

    private static void addTone(int durationMs) throws IOException {
        int numSamples = (int) (durationMs * SAMPLE_RATE / 1000);
        byte[] buffer = new byte[numSamples * 2];

        double anglePerSample = 2.0 * Math.PI * TONE_FREQUENCY / SAMPLE_RATE;

        for (int i = 0; i < numSamples; i++) {
            double angle = i * anglePerSample;
            short sample = (short) (Math.sin(angle) * Short.MAX_VALUE);
            buffer[2 * i] = (byte) (sample & 0xFF);
            buffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        outputStream.write(buffer);
    }

    private static void addSilence(int durationMs) throws IOException {
        int numSamples = (int) (durationMs * SAMPLE_RATE / 1000);
        byte[] buffer = new byte[numSamples * 2];
        outputStream.write(buffer);
    }

    public static void saveToFile(String filename) throws IOException {
        if (isAudioStreamEmpty()) throw new IllegalStateException("Audio stream is empty. Cannot write to file.");

        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        InputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, outputStream.toByteArray().length / 2);

        File fileOut = new File(filename);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);
        audioInputStream.close();
    }

    public static boolean isAudioStreamEmpty() {
        return outputStream.size() == 0;
    }
}
