package modules.measurements;

import org.hipparchus.util.FastMath;
import org.orekit.time.AbsoluteDate;
import seakers.orekit.object.CoverageDefinition;
import seakers.orekit.object.CoveragePoint;

import java.util.HashMap;

public class MeasurementRequest {
    private final int id;
    private final CoverageDefinition covDef;
    private final CoveragePoint location;
    private final AbsoluteDate simStartDate;
    private final AbsoluteDate announceDate;
    private final AbsoluteDate startDate;
    private final AbsoluteDate endDate;
    private final String type;
    private final HashMap<String, Requirement> requirements;
    private final double maxUtility;

    public MeasurementRequest(int id, CoverageDefinition covDef, CoveragePoint location, AbsoluteDate announceDate, AbsoluteDate startDate, AbsoluteDate endDate, String type, HashMap<String, Requirement> requirements, AbsoluteDate simStartDate, double maxUtility){
        this.id = id;
        this.covDef = covDef;
        this.location = location;
        this.announceDate = announceDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.requirements = new HashMap<>(requirements);
        this.simStartDate = simStartDate;
        this.maxUtility = maxUtility;
    }

    public MeasurementRequest copy(){
        return new MeasurementRequest(id, covDef, location, announceDate, startDate, endDate, type, requirements, simStartDate, maxUtility);
    }

    public String toString(){
        StringBuilder out = new StringBuilder();

        int id = this.getId();
        String type = this.getType();
        CoveragePoint location = this.getLocation();
        double lat = FastMath.toDegrees( location.getPoint().getLatitude() );
        double lon = FastMath.toDegrees( location.getPoint().getLongitude() );
        double announceDate = this.getAnnounceDate().durationFrom( this.simStartDate );
        double startDate = this.getStartDate().durationFrom( this.simStartDate );
        double endDate = this.getEndDate().durationFrom( this.simStartDate);

        HashMap<String, Requirement> requirements = this.getRequirements();

        out.append(id + "," + type + "," + lat + "," + lon + "," + announceDate + "," + startDate + "," + endDate + ",");

        int i = 0;
        for(String requirementName : requirements.keySet()){
            Requirement requirement = requirements.get(requirementName);

            String name = requirement.getName();
            double goal = requirement.getGoal();
            double breakThrough = requirement.getBreakThrough();
            double threshold = requirement.getThreshold();
            String units = requirement.getUnits();

            out.append(name + "," + goal + "," +breakThrough + "," + threshold + "," + units);

            if(i < requirements.keySet().size() - 1) out.append(",");
            i++;
        }
        out.append("\n");

        return out.toString();
    }

    public int getId() { return id; }
    public CoverageDefinition getCovDef(){return covDef;}
    public CoveragePoint getLocation() { return location; }
    public AbsoluteDate getAnnounceDate() { return announceDate; }
    public AbsoluteDate getStartDate() { return startDate; }
    public AbsoluteDate getEndDate() { return endDate; }
    public String getType() { return type; }
    public HashMap<String, Requirement> getRequirements() { return requirements; }
    public double getMaxUtility(){return maxUtility;}

    public boolean equals(MeasurementRequest request){
        return id == request.getId();
    }
}
