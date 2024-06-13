package it.unica.aibd.fabric.contract.IoT_Policies;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class User {
    
    @Property()
    private final String userID;

    @Property()
    private final String name;

    @Property()
    private final String surname;

    @Property()
    private final String email;

    @Property()
    private Boolean hasVoted = false;

    @Property()
    private final List<String> subscription = new ArrayList<String>();

    @Property()
    private final List<Policy> policies = new ArrayList<Policy>();

    @Property()
    private Measure lastMeasure;

    public User(@JsonProperty("userID") final String ID, @JsonProperty("name") final String name,
    @JsonProperty("surname") final String surname, @JsonProperty("email") final String email) {
        this.userID = ID;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public String getID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(Boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public List<String> getSubscription() {
        return subscription;
    }

    public Policy createPolicy(String ID, String samplingInterval, PolicyType policyType, List<String> valueNames, List<String> valueThresholds, List<String> operatorThresholds) {
        Policy policy = new Policy(ID, samplingInterval, policyType, valueNames, valueThresholds, operatorThresholds, LocalTime.now().withNano(0));
        policies.add(policy);
        return policy;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setLastMeasure(Measure measure) {
        this.lastMeasure = measure;
    }

    public Measure getLastMeasure() {
        return lastMeasure;
    }

}
