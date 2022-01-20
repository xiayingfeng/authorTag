package utils;

import entity.LineTag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static constant.Constant.PLATFORM;
import static constant.Constant.WIN;

public class ShellCaller {
    private static final Logger logger = Logger.getLogger(ShellCaller.class.getName());
    /**
     * Runtime is an instance served by jvm, it's one and only
     */
    private static final Runtime RT = Runtime.getRuntime();

    public static List<String> call(String cmdStr) {
        List<String> linesList = new LinkedList<>();
        try {
            Process pr = RT.exec(cmdStr);

            BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                linesList.add(line);
            }
            while ((line = stdError.readLine()) != null) {
                logger.log(Level.SEVERE, line);
            }
            stdError.close();
            reader.close();
            pr.waitFor();
            // TODO enter 'q'
            kill9(pr);
            RT.gc();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return  linesList;
    }

    public static  String localizeCmd(String cmdStr) {
        if (WIN.equals(PLATFORM)) {
            cmdStr = "cmd /c " + cmdStr;
        }
        return cmdStr;
    }

    private static void kill9(Process pr) throws IOException {
        long pid = pr.pid();
        String killCmd = "kill -KILL " + pid;
        killCmd = localizeCmd(killCmd);
        RT.exec(killCmd);
    }
}

