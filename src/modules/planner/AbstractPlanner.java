package modules.planner;

import madkit.kernel.Message;
import modules.actions.SimulationAction;
import modules.agents.SatelliteAgent;
import modules.measurements.Measurement;
import modules.measurements.MeasurementRequest;
import modules.measurements.Requirement;
import modules.measurements.RequirementPerformance;
import org.orekit.time.AbsoluteDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public abstract class AbstractPlanner {
    /**
     * Types of accepted planners
     */
    public static final String NONE = "none";
    public static final String TIME = "time";
    public static final String CCBBA = "CCBBA";
    public static final String RELAY = "relay";
    public static final String[] PLANNERS = {NONE, TIME, CCBBA, RELAY};

    /**
     * Parent agent being scheduled
     */
    protected SatelliteAgent parentAgent;

    /**
     * Planning horizon in seconds
     */
    protected final double planningHorizon;

    /**
     * threshold of new measurement requests required to trigger a plan reschedule
     */
    protected final int requestThreshold;

    /**
     * toggle that allows for planners to create schedules considering
     * inter-satellite cross links
     */
    protected final boolean crossLinks;

    /**
     * List of actions to be given to an agent to be performed
     */
    protected ArrayList<SimulationAction> plan;

    /**
     * List of known and active measurement requests
     */
    protected ArrayList<MeasurementRequest> activeRequests;

    /**
     * List of known measurement requests
     */
    protected ArrayList<MeasurementRequest> knownRequests;

    public AbstractPlanner(double planningHorizon, int requestThreshold, boolean crossLinks){
        this.planningHorizon = planningHorizon;
        this.requestThreshold = requestThreshold;
        this.crossLinks = crossLinks;
    }

    /**
     * Creates an initial plan at the beginning of the simulation
     * @return a linked list of actions for the agent
     */
    public abstract LinkedList<SimulationAction> initPlan();

    /**
     * Creates, modifies, or maintains the plan to be performed by an agent
     * @param messageMap : map of different types of messages received by an agent
     * @param agent : agent set to perform the plan
     * @return a linked list of actions to be performed by the agent
     * @throws Exception
     */
    public abstract LinkedList<SimulationAction> makePlan(HashMap<String, ArrayList<Message>> messageMap,
                                                          SatelliteAgent agent, AbsoluteDate currentDate) throws Exception;


    /**
     * Calculates the estimated or projected utility of performing a measurement
     * of a given performance or quality
     * @param request : measurement request being answered
     * @param performance : performance of the measurement being made
     * @return utility of the measurement
     */
    public abstract double calcUtility(MeasurementRequest request,
                                       HashMap<Requirement, RequirementPerformance> performance);

    /**
     * Assigns planner to its parent agent
     * @param agent
     */
    public void setParentAgent(SatelliteAgent agent){ this.parentAgent = agent; }

    protected LinkedList<SimulationAction> getAvailableActions(AbsoluteDate currentDate){
        LinkedList<SimulationAction> actions = new LinkedList<>();

        for(SimulationAction action : this.plan){
            if( currentDate.compareTo(action.getStartDate()) >= 0
                && currentDate.compareTo(action.getEndDate()) <= 0){
                actions.add(action);
            }
        }

        return actions;
    }

    protected LinkedList<SimulationAction> getOutdatedActions(AbsoluteDate currentDate){
        LinkedList<SimulationAction> actions = new LinkedList<>();

        for(SimulationAction action : this.plan){
            if( currentDate.compareTo(action.getEndDate()) > 0
                    && currentDate.compareTo(action.getEndDate()) <= 0){
                actions.add(action);
            }
        }

        return actions;
    }
}