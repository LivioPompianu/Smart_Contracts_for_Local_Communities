package it.unica.aibd.fabric.contract.IoT_Policies;

import java.time.LocalTime;
import java.util.List;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class Policy {

    @Property()
    private final String ID;
    
    @Property()
    private String samplingInterval;

    @Property()
    private final PolicyType policyType;

    @Property()
    private List<String> valueNames;

    @Property()
    private List<String> valueThresholds;

    @Property()
    private List<String> operatorThresholds;

    @Property()
    private LocalTime timer;

    public Policy(@JsonProperty("ID") final String ID, 
                @JsonProperty("samplingInterval") final String samplingInterval,
                @JsonProperty("policyType") final PolicyType policyType,
                @JsonProperty("valueNames") final List<String> valueNames,
                @JsonProperty("valueThresholds") final List<String> valueThresholds,
                @JsonProperty("operatorThresholds") final List<String> operatorThresholds,
                @JsonProperty("timer") final LocalTime timer) {
        this.ID = ID;
        this.policyType = policyType;
        this.valueNames = valueNames;
        if (policyType == PolicyType.TIMEVALUE){
            this.samplingInterval = samplingInterval;
            this.valueThresholds = valueThresholds;
            this.operatorThresholds = operatorThresholds;
        } else if (policyType == PolicyType.TIME){
            this.samplingInterval = samplingInterval;
            this.valueThresholds = null;
            this.operatorThresholds = null;
        } else if (policyType == PolicyType.VALUE){
            this.valueThresholds = valueThresholds;
            this.operatorThresholds = operatorThresholds;
            this.samplingInterval = null;
        }
        this.timer = timer;
    }

    public String getSamplingInterval() {
        return samplingInterval;
    }

    public PolicyType getpolicyType() {
        return policyType;
    }

    public String getID() {
        return ID;
    }

    public List<String> getValueNames() {
        return valueNames;
    }

    public List<String> getValueThresholds() {
        return valueThresholds;
    }

    public List<String> getOperatorThresholds() {
        return operatorThresholds;
    }

    public LocalTime getTimer() {
        return timer;
    }

}
