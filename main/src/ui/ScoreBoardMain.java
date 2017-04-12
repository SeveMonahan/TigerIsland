package ui;

import tigerisland.datalogger.DataReader;
import tigerisland.datalogger.LoggerFactory;
import tigerisland.datalogger.SQLiteReader;
import ui.GenerateOverallScoreboard;
import ui.GenerateScoreBoard;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class ScoreBoardMain {
    private final static String FILENAME = "./ScoreBoard.html";
    private final static String FILENAMEOVERALLSCORE = "./OverallScoreBoard.html";
    public static void main(String[] args) {
        LoggerFactory.createTables();
        while(true) {

            DataReader sqliteDataReader = new SQLiteReader(LoggerFactory.getDbConnection());
            GenerateScoreBoard scoreBoardGenerator = new GenerateScoreBoard(sqliteDataReader);
            GenerateOverallScoreboard overallScoreboardGenerator = new GenerateOverallScoreboard(LoggerFactory.getDataBaseUrl());

            String overallScoreboard = (overallScoreboardGenerator.getScoreBoard());
            String scoreboard = (scoreBoardGenerator.getScoreBoard());

            BufferedWriter bufferedWriter = null;
            BufferedWriter bufferedWriterOverall = null;

            FileWriter fileWriter = null;
            FileWriter fileWriterOverall = null;
            try {
                fileWriterOverall = new FileWriter(FILENAMEOVERALLSCORE);
                fileWriter = new FileWriter(FILENAME);
                bufferedWriterOverall = new BufferedWriter(fileWriterOverall);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(scoreboard);
                bufferedWriterOverall.write(overallScoreboard);
            } catch (IOException e) {

                e.printStackTrace();
            } finally {
                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                    if (bufferedWriterOverall != null){
                        bufferedWriterOverall.close();
                    }
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                    if (fileWriterOverall != null){
                        fileWriterOverall.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                sleep(5000);
            } catch (Exception ex) {
                System.out.print(ex);
            }
        }
    }


}