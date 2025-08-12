package ru.podorozhnyk.application;

import ru.podorozhnyk.application.morse.MorseConverter;
import ru.podorozhnyk.application.ui.MainFrame;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


import static ru.podorozhnyk.application.morse.MorseUtils.DefaultDictionaries.*;

public class Main {


    public static void main(String[] args) throws LineUnavailableException, IOException, InterruptedException {
        //--gui (другие флаги игнорируются - запускается приложение), -t (text-to-morse), -m (morse-to-text), -s (sound)
        if (args.length > 0) {
            Arrays.stream(args).forEach(System.out::println);
            return;
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new MainFrame("Morse Converter", screenSize.width / 2, screenSize.height / 2);
    }



}
