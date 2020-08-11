package modules.simulation;

import jmetal.encodings.variable.Int;
import jxl.read.biff.BiffException;
import madkit.kernel.AbstractAgent;
import modules.environment.Environment;
import modules.planner.Task;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import jxl.*;

public class ProblemStatement {
    /**
     *  Reads Problem Statements folder location. Initiates Environment and the Tasks Located in it
     */
    private Environment environment;
    private final String problemStatement;
    private final String problemStatementDir;
    private final String inputFileDir;
    private final String outputFileDir;
    private final JSONObject inputDataSettings;
    private final JSONObject inputDataMission;
    private String loggerLevel;
    private AbsoluteDate startDate;
    private AbsoluteDate endDate;
    private AbsoluteDate currentDate;
    private double timeStep;

    private TimeScale utc;

    public ProblemStatement(String inputFile, String problemStatement) throws Exception {
        // read input JSON
        this.problemStatement = problemStatement;
        this.problemStatementDir = "./src/scenarios/" + problemStatement;
        this.inputFileDir = "./src/inputs/" + inputFile;
        this.outputFileDir = "./src/outputs/" + problemStatement;
        this.inputDataSettings = (JSONObject) readJSON().get("settings");
        this.inputDataMission = (JSONObject) readJSON().get("mission");

        // load orekit-data
        loadOrekitData();

        // initiate environment
        initiateEnvironment();
    }

    private void loadOrekitData(){
        File orekitData = new File("./src/data/orekit-data");
        DataProvidersManager manager = DataProvidersManager.getInstance();
        try {
            manager.addProvider(new DirectoryCrawler(orekitData));
        } catch (OrekitException e) {
            e.printStackTrace();
        }
    }

    private JSONObject readJSON(){
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(this.inputFileDir));
            return (JSONObject) obj;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initiateEnvironment() throws Exception {
        this.utc = TimeScalesFactory.getUTC();
        this.loggerLevel = inputDataSettings.get("logger").toString();
        this.startDate = stringToDate( inputDataMission.get("start").toString() );
        this.currentDate = stringToDate( inputDataMission.get("start").toString() );
        double duration = stringToDuration( inputDataMission.get("duration").toString() );
        this.endDate = startDate.shiftedBy(duration);
        this.timeStep = (double) inputDataMission.get("timeStep");
    }

    private AbsoluteDate stringToDate(String startDate) throws Exception {
        if(startDate.length() != 20){
            throw new Exception("Date format not supported");
        }

        int YYYY = Integer.parseInt(String.valueOf(startDate.charAt(0))
                                    + String.valueOf(startDate.charAt(1))
                                    + String.valueOf(startDate.charAt(2))
                                    + String.valueOf(startDate.charAt(3)));
        int MM = Integer.parseInt(String.valueOf(startDate.charAt(5))
                + String.valueOf(startDate.charAt(6)));
        int DD = Integer.parseInt(String.valueOf(startDate.charAt(8))
                + String.valueOf(startDate.charAt(9)));

        int hh = Integer.parseInt(String.valueOf(startDate.charAt(11))
                + String.valueOf(startDate.charAt(12)));
        int mm = Integer.parseInt(String.valueOf(startDate.charAt(14))
                + String.valueOf(startDate.charAt(15)));
        int ss = Integer.parseInt(String.valueOf(startDate.charAt(17))
                + String.valueOf(startDate.charAt(18)));

        return new AbsoluteDate(YYYY, MM, DD, hh, mm, ss, utc);
    }

    private double stringToDuration(String duration) throws Exception {
        if(duration.length() != 10){
            throw new Exception("Mission duration format not supported");
        }

        int YY = Integer.parseInt(String.valueOf(duration.charAt(1))
                + String.valueOf(duration.charAt(2)));
        int MM = Integer.parseInt(String.valueOf(duration.charAt(4))
                + String.valueOf(duration.charAt(5)));
        int DD = Integer.parseInt(String.valueOf(duration.charAt(7))
                + String.valueOf(duration.charAt(8)));

        double yy = YY * 365.25 * 24 * 3600;
        double mm = MM * 365.25/12 * 24 * 3600;
        double dd = DD * 24 * 3600;
        return yy + mm + dd;
    }

    public Environment getEnvironment() { return environment; }
    public void setEnvironment(Environment environment) { this.environment = environment; }
    public String getProblemStatement() { return problemStatement; }
    public String getProblemStatementDir() { return problemStatementDir; }
    public String getInputFileDir() { return inputFileDir; }
    public String getOutputFileDir() { return outputFileDir; }
    public JSONObject getInputDataSettings() { return inputDataSettings; }
    public JSONObject getInputDataMission() { return inputDataMission; }
    public String getLoggerLevel() { return loggerLevel; }
    public void setLoggerLevel(String loggerLevel) { this.loggerLevel = loggerLevel; }
    public AbsoluteDate getStartDate() { return startDate; }
    public void setStartDate(AbsoluteDate startDate) { this.startDate = startDate; }
    public AbsoluteDate getEndDate() { return endDate; }
    public void setEndDate(AbsoluteDate endDate) { this.endDate = endDate; }
    public AbsoluteDate getCurrentDate() { return currentDate; }
    public void setCurrentDate(AbsoluteDate currentDate) { this.currentDate = currentDate; }
    public double getTimeStep() { return timeStep; }
    public void setTimeStep(double timeStep) { this.timeStep = timeStep; }
    public TimeScale getUtc() { return utc; }
    public void setUtc(TimeScale utc) { this.utc = utc; }
}
