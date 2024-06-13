package it.unica.aibd.fabric.contract.IoT_Policies;

import java.time.LocalTime;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class Violation {

    @Property()
    private final String ID;

    @Property()
    private final String userID;

    @Property()
    private final LocalTime timestamp;

    @Property()
    private final String value;

    @Property()
    private final String type;
    
    public Violation( @JsonProperty("ID") final String ID,  @JsonProperty("userID") final String userID, @JsonProperty("timestamp") final LocalTime timestamp,
    @JsonProperty("value") final String value,@JsonProperty("type") final String type) {
        this.ID = ID;
        this.userID = userID;
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
    }

    public String getID() {
        return ID;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

}
