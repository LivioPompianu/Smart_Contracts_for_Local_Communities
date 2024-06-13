package it.unica.aibd.fabric.contract.IoT_Policies;

import java.time.LocalTime;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import java.util.List;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class Measure {
    
    @Property()
    private final String ID;

    @Property()
    private final String userID;

    @Property()
    private final LocalTime timestamp;

    @Property()
    private LocalTime timestampFromUser;

    @Property()
    private Boolean checkViolation = false;

    @Property()
    private final List<String> values;

    @Property()
    private final List<String> valueNames;

    public Measure(@JsonProperty("userID") final String userID, @JsonProperty("ID") final String ID, @JsonProperty("timestamp") final LocalTime timestamp,
    @JsonProperty("value") final List<String> values, @JsonProperty("valueNames") final List<String> valueNames) {
        this.userID = userID;
        this.ID = ID;
        this.timestamp = timestamp;
        this.values = values;
        this.valueNames = valueNames;
    }

    public String getID() {
        return ID;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public List<String> getValues() {
        return values;
    }

    public List<String> getValueNames() {
        return valueNames;
    }

    public Boolean getCheckViolation() {
        return checkViolation;
    }

    public void setCheckViolation(Boolean checkViolation) {
        this.checkViolation = checkViolation;
    }

    public String getUserID() {
        return userID;
    }
}
