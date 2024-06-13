package it.unica.aibd.fabric.contract.IoT_Policies;

import java.time.LocalTime;
import java.time.Duration;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

@Contract(name = "IoT_Policies", info = @Info(title = "IoT_Policies", description = "IoT_Policies chaincode", version = "1.0.0"))
@Default
public final class IoT_Policies implements ContractInterface {

	private final Gson gson = new Gson();

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public User getUser(final Context ctx, final String ID) {
		ChaincodeStub stub = ctx.getStub();

		String userState = stub.getStringState("user" + ID);
		if (userState == null || userState.isEmpty()) {
			String errorMessage = String.format("User %s does not exist", ID);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}
		System.out.println("User deserialized " + userState);

		return gson.fromJson(userState, User.class);
	}

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public Measure getMeasure(final Context ctx, final String measureID) {
		ChaincodeStub stub = ctx.getStub();

		String measureState = stub.getStringState("measure" + measureID);
		if (measureState == null || measureState.isEmpty()) {
			String errorMessage = String.format("Measure %s does not exist", measureID);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}
		System.out.println("Measure deserialized " + measureState);

		return gson.fromJson(measureState, Measure.class);
	}

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public Policy getPolicy(final Context ctx, final String ID) {
		ChaincodeStub stub = ctx.getStub();

		String policyState = stub.getStringState("policy" + ID);
		if (policyState == null || policyState.isEmpty()) {
			String errorMessage = String.format("Policy %s does not exist", ID);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}
		System.out.println("Policy deserialized " + policyState);

		return gson.fromJson(policyState, Policy.class);
	}

	public Measure getLastMeasure(final Context ctx, final String userID) {
		ChaincodeStub stub = ctx.getStub();

		User user = getUser(ctx, userID);

		if (user == null) {
			String errorMessage = String.format("User %s does not exist", userID);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}

		QueryResultsIterator<KeyValue> results = stub.getStateByRange("measure", "measurez");
		Measure measure = null;
		for (KeyValue result : results) {
			String measureState = result.getStringValue();
			Measure measureTemp = gson.fromJson(measureState, Measure.class);
			if (measureTemp.getUserID().equals(userID)) {
				if (measure == null || measureTemp.getTimestamp().isAfter(measure.getTimestamp())) {
					measure = measureTemp;
				}
			}
		}

		return measure;
	}

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public String getAllMeasures(final Context ctx) {
		ChaincodeStub stub = ctx.getStub();

		QueryResultsIterator<KeyValue> results = stub.getStateByRange("measure", "measurez");
		List<Measure> queryResults = new ArrayList<Measure>();

		for (KeyValue result : results) {
			String value = result.getStringValue();
			Measure measure = gson.fromJson(value, Measure.class);
			queryResults.add(measure);
		}

		String queryResultsJ = gson.toJson(queryResults);

		return queryResultsJ;
	}

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public String getAllUsers(final Context ctx) {
		ChaincodeStub stub = ctx.getStub();

		QueryResultsIterator<KeyValue> results = stub.getStateByRange("user", "userz");
		List<User> queryResults = new ArrayList<User>();

		for (KeyValue result : results) {
			String value = result.getStringValue();
			User user = gson.fromJson(value, User.class);
			queryResults.add(user);
		}

		String queryResultsJ = gson.toJson(queryResults);

		return queryResultsJ;
	}

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public String getAllViolations(final Context ctx) {
		ChaincodeStub stub = ctx.getStub();

		QueryResultsIterator<KeyValue> results = stub.getStateByRange("violation", "violationz");
		List<Violation> queryResults = new ArrayList<Violation>();

		for (KeyValue result : results) {
			String value = result.getStringValue();
			Violation violation = gson.fromJson(value, Violation.class);
			queryResults.add(violation);
		}

		String queryResultsJ = gson.toJson(queryResults);

		return queryResultsJ;
	}

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public String getAll(final Context ctx) {
		ChaincodeStub stub = ctx.getStub();

		QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");
		List<String> queryResults = new ArrayList<String>();

		for (KeyValue result : results) {
			String value = result.getStringValue();
			queryResults.add(value);
		}

		String queryResultsJ = gson.toJson(queryResults);

		return queryResultsJ;
	}

	@Transaction(intent = Transaction.TYPE.SUBMIT)
	public void createUser(final Context ctx, final String ID, final String name, final String surname,
			final String email) {
		ChaincodeStub stub = ctx.getStub();

		if (userExists(ctx, ID)) {
			String errorMessage = String.format("User %s already exists", ID);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}

		User user = new User(ID, name, surname, email);

		String userState = gson.toJson(user);
		stub.putStringState("user" + ID, userState);
	}

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public boolean userExists(final Context ctx, final String ID) {
		ChaincodeStub stub = ctx.getStub();
		String userState = stub.getStringState("user" + ID);

		return (userState != null && !userState.isEmpty());
	}

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public boolean policyExists(final Context ctx, final String ID) {
		ChaincodeStub stub = ctx.getStub();
		String policyState = stub.getStringState("policy" + ID);

		return (policyState != null && !policyState.isEmpty());
	}

	@Transaction(intent = Transaction.TYPE.SUBMIT)
	public void createPolicy(final Context ctx, final String userID, final String ID, final String samplingInterval,
			final String policyType,
			final String valueNames, final String valueThresholds, final String operatorThresholds) {
		PolicyType metricType = null;

		String valueNamesString = valueNames.substring(1, valueNames.length() - 1);
		valueNamesString = valueNamesString.replace("\"", "");
		List<String> valueNamesList = Arrays.asList(valueNamesString.split(","));

		String valueThresholdsString = valueThresholds.substring(1, valueThresholds.length() - 1);
		valueThresholdsString = valueThresholdsString.replace("\"", "");
		List<String> valueThresholdsList = new ArrayList<String>();

		String operatorThresholdsString = operatorThresholds.substring(1, operatorThresholds.length() - 1);
		operatorThresholdsString = operatorThresholdsString.replace("\"", "");
		List<String> operatorThresholdsList = new ArrayList<String>();

		if (valueThresholds.equals("null")) {
			valueThresholdsList = null;
		} else {
			valueThresholdsList = Arrays.asList(valueThresholdsString.split(","));
		}

		if (operatorThresholds.equals("null")) {
			operatorThresholdsList = null;
		} else {
			operatorThresholdsList = Arrays.asList(operatorThresholdsString.split(","));
		}

		if (policyExists(ctx, ID)) {
			String errorMessage = String.format("Policy %s already exists", ID);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}

		if (policyType.equals("TIME")) {
			metricType = PolicyType.TIME;
		} else if (policyType.equals("VALUE")) {
			metricType = PolicyType.VALUE;
		} else if (policyType.equals("TIMEVALUE")) {
			metricType = PolicyType.TIMEVALUE;
		} else if (metricType == null) {
			String errorMessage = String.format("Metric %s is not valid", policyType);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}

		User user = getUser(ctx, userID);
		if (user == null) {
			String errorMessage = String.format("User %s does not exist", userID);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}

		System.out.println("Initialized, Creating policy " + ID);
		Policy policy = user.createPolicy(ID, samplingInterval, metricType, valueNamesList, valueThresholdsList,
				operatorThresholdsList);
		ChaincodeStub stub = ctx.getStub();
		String policyState = gson.toJson(policy);
		stub.putStringState("policy" + ID, policyState);
		String userState = gson.toJson(user);
		stub.putStringState("user" + userID, userState);
	}

	public Policy getAdminPolicy(final Context ctx){
		ChaincodeStub stub = ctx.getStub();
		String policyState = stub.getStringState("policyadmin");
		if (policyState == null || policyState.isEmpty()) {
			String errorMessage = String.format("policyadmin does not exist");
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}
		System.out.println("policyadmin deserialized " + policyState);

		return gson.fromJson(policyState, Policy.class);
	}

	@Transaction(intent = Transaction.TYPE.SUBMIT)
	public void sendMeasure(final Context ctx, final String userId, final String measureID, final String values, final String valueNames) {
		ChaincodeStub stub = ctx.getStub();

		String valueNamesString = valueNames.substring(1, valueNames.length() - 1);
		valueNamesString = valueNamesString.replace("\"", "");
		List<String> valueNamesList = Arrays.asList(valueNamesString.split(","));

		String valuesString = values.substring(1, values.length() - 1);
		valuesString = valuesString.replace("\"", "");
		List<String> valuesList = Arrays.asList(valuesString.split(","));

		List<Float> valuesListFloat = new ArrayList<Float>();
		for (String value : valuesList) {
			valuesListFloat.add(Float.parseFloat(value));
		}

		Policy policy = getAdminPolicy(ctx);
		LocalTime timer = policy.getTimer();

		User user = getUser(ctx, userId);
		System.out.println("Actual timer of the policy:" + timer);

		if (policy.getpolicyType() == PolicyType.TIME) {
			LocalTime time = LocalTime.now().withNano(0);
			Measure measure = new Measure(user.getID(), measureID, time, valuesList, valueNamesList);
			String policySamplingInterval = policy.getSamplingInterval();

			Measure lastMeasure = user.getLastMeasure();
			if (lastMeasure != null) {
				LocalTime lastTick = lastMeasure.getTimestamp();
				if (Duration.between(lastTick, time).toSeconds() > Integer.parseInt(policySamplingInterval)) {
					Violation violation = new Violation(measureID, userId, time,
							Duration.between(lastTick, time).toSeconds() + "",
							Duration.between(lastTick, time).toSeconds() + " seconds, instead of "
									+ policySamplingInterval + " seconds");
					measure.setCheckViolation(true);

					String violationState = gson.toJson(violation);
					stub.putStringState("violation" + violation.getID(), violationState);
				}
			} else {
				if (Duration.between(timer, time).toSeconds() > Integer.parseInt(policySamplingInterval)) {
					Violation violation = new Violation(measureID, userId, time,
							Duration.between(timer, time).toSeconds() + "",
							Duration.between(timer, time).toSeconds() + " seconds, instead of "
									+ policySamplingInterval + " seconds");
					measure.setCheckViolation(true);

					String violationState = gson.toJson(violation);
					stub.putStringState("violation" + violation.getID(), violationState);
				}
			}

			String measureState = gson.toJson(measure);
			stub.putStringState("measure" + measureID, measureState);
			user.setLastMeasure(measure);
			String userState = gson.toJson(user);
			stub.putStringState("user" + userId, userState);
		} else if (policy.getpolicyType() == PolicyType.VALUE) {
			LocalTime time = LocalTime.now().withNano(0);
			Measure measure = new Measure(user.getID(), measureID, time, valuesList, valueNamesList);
			List<String> valueThresholdsPolicy = policy.getValueThresholds();
			List<String> operatorThresholdsPolicy = policy.getOperatorThresholds();
			List<String> valueNamesPolicy = policy.getValueNames();

			List<Float> valueThresholdsPolicyFloat = new ArrayList<Float>();
			for (String value : valueThresholdsPolicy) {
				valueThresholdsPolicyFloat.add(Float.parseFloat(value));
			}

			for (int i = 0; i < valueNamesPolicy.size(); i++) {
				int index = valueNamesList.indexOf(valueNamesPolicy.get(i));
				if (operatorThresholdsPolicy.get(i).equals("==")) {
					if (!valuesListFloat.get(index).equals(valueThresholdsPolicyFloat.get(i))) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId,
						time, valuesListFloat.get(index) + "", "Value " + valueNamesPolicy.get(i)
										+ " is not equal to " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals(">")) {
					if (valuesListFloat.get(index) <= valueThresholdsPolicyFloat.get(i)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId,
						time, valuesListFloat.get(index) + "", "Value " + valueNamesPolicy.get(i)
										+ " is less-equal than " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals("<")) {
					if (valuesListFloat.get(index) >= valueThresholdsPolicyFloat.get(i)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId, 
						time, valuesListFloat.get(index) + "", "Value " + valueNamesPolicy.get(i)
										+ " is greater-equal than " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals("<=")) {
					if (valuesListFloat.get(index) > valueThresholdsPolicyFloat.get(i)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId, 
						time, valuesListFloat.get(index) + "",
								"Value " + valueNamesPolicy.get(i) + " is greater to " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals(">=")) {
					if (valuesListFloat.get(index) < valueThresholdsPolicyFloat.get(i)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId,
						time, valuesListFloat.get(index) + "",
								"Value " + valueNamesPolicy.get(i) + " is less to " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals(">=<")) {
					System.out.println("ValueThresholdsPolicyFloat: " + valueThresholdsPolicyFloat);
					System.out.println("Now comparing " + valuesListFloat.get(index) + " with "
							+ valueThresholdsPolicyFloat.get(i) + " and " + valueThresholdsPolicyFloat.get(i + 1));
					if (valuesListFloat.get(index) < valueThresholdsPolicyFloat.get(i)
							|| valuesListFloat.get(index) > valueThresholdsPolicyFloat.get(i + 1)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId,
						time, valuesListFloat.get(index) + "",
								"Value " + valueNamesPolicy.get(i) + " is not between "
										+ valueThresholdsPolicyFloat.get(i) + " and "
										+ valueThresholdsPolicyFloat.get(i + 1));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
					System.out.println("Removing value at index " + (i + 1) + " with value "
							+ valueThresholdsPolicyFloat.get(i + 1));
					System.out.println(valueThresholdsPolicyFloat.remove(i + 1));
					System.out.println("New valueThresholdsPolicyFloat: " + valueThresholdsPolicyFloat);
				} else {
					String errorMessage = String.format("Operator %s is not valid", operatorThresholdsPolicy.get(i));
					System.out.println(errorMessage);
					throw new ChaincodeException(errorMessage);
				}
			}

			String measureState = gson.toJson(measure);
			stub.putStringState("measure" + measureID, measureState);
			user.setLastMeasure(measure);
			String userState = gson.toJson(user);
			stub.putStringState("user" + userId, userState);
		} else if (policy.getpolicyType() == PolicyType.TIMEVALUE) {
			LocalTime time = LocalTime.now().withNano(0);
			Measure measure = new Measure(user.getID(), measureID, time, valuesList, valueNamesList);
			String policySamplingInterval = policy.getSamplingInterval();

			Measure lastMeasure = user.getLastMeasure();
			if (lastMeasure != null) {
				LocalTime lastTick = lastMeasure.getTimestamp();
				if (Duration.between(lastTick, time).toSeconds() > Integer.parseInt(policySamplingInterval)) {
					Violation violation = new Violation(measureID + "-TIME", userId, time,
							Duration.between(lastTick, time).toSeconds() + "",
							Duration.between(lastTick, time).toSeconds() + " seconds, instead of "
									+ policySamplingInterval + " seconds");
					measure.setCheckViolation(true);

					String violationState = gson.toJson(violation);
					stub.putStringState("violation" + violation.getID(), violationState);
				}
			} else {
				if (Duration.between(timer, time).toSeconds() > Integer.parseInt(policySamplingInterval)) {
					Violation violation = new Violation(measureID + "-TIME", userId, time,
							Duration.between(timer, time).toSeconds() + "",
							Duration.between(timer, time).toSeconds() + " seconds, instead of "
									+ policySamplingInterval + " seconds");
					measure.setCheckViolation(true);

					String violationState = gson.toJson(violation);
					stub.putStringState("violation" + violation.getID(), violationState);
				}
			}

			List<String> valueThresholdsPolicy = policy.getValueThresholds();
			List<String> operatorThresholdsPolicy = policy.getOperatorThresholds();
			List<String> valueNamesPolicy = policy.getValueNames();

			List<Float> valueThresholdsPolicyFloat = new ArrayList<Float>();
			for (String value : valueThresholdsPolicy) {
				valueThresholdsPolicyFloat.add(Float.parseFloat(value));
			}

			System.out.println("ValueNamesPolicy: " + valueNamesPolicy + "with size " + valueNamesPolicy.size());

			for (int i = 0; i < valueNamesPolicy.size(); i++) {
				int index = valueNamesList.indexOf(valueNamesPolicy.get(i));
				System.out.println("Index: " + index + " Value: " + valuesListFloat.get(index) + " Threshold: "
						+ valueThresholdsPolicyFloat.get(i) + " Operator: " + operatorThresholdsPolicy.get(i) + " Name: "
						+ valueNamesPolicy.get(i));
				if (operatorThresholdsPolicy.get(i).equals("==")) {
					if (!valuesListFloat.get(index).equals(valueThresholdsPolicyFloat.get(i))) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId,
								time, valuesListFloat.get(index) + "",
								"Value " + valueNamesPolicy.get(i) + " is equal to " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals(">")) {
					if (valuesListFloat.get(index) <= valueThresholdsPolicyFloat.get(i)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId,
								time, valuesListFloat.get(index) + "", "Value " + valueNamesPolicy.get(i)
										+ " is less-equal than " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals("<")) {
					if (valuesListFloat.get(index) >= valueThresholdsPolicyFloat.get(i)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId,
								time, valuesListFloat.get(index) + "", "Value " + valueNamesPolicy.get(i)
										+ " is greater-equal than " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals("<=")) {
					if (valuesListFloat.get(index) > valueThresholdsPolicyFloat.get(i)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId,
								time, valuesListFloat.get(index) + "",
								"Value " + valueNamesPolicy.get(i) + " is greater to " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals(">=")) {
					if (valuesListFloat.get(index) < valueThresholdsPolicyFloat.get(i)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId,
								time, valuesListFloat.get(index) + "",
								"Value " + valueNamesPolicy.get(i) + " is less to " + valueThresholdsPolicy.get(i));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
				} else if (operatorThresholdsPolicy.get(i).equals(">=<")) {
					System.out.println("ValueThresholdsPolicyFloat: " + valueThresholdsPolicyFloat);
					System.out.println("Now comparing " + valuesListFloat.get(index) + " with "
							+ valueThresholdsPolicyFloat.get(i) + " and " + valueThresholdsPolicyFloat.get(i + 1));
					if (valuesListFloat.get(index) < valueThresholdsPolicyFloat.get(i)
							|| valuesListFloat.get(index) > valueThresholdsPolicyFloat.get(i + 1)) {
						Violation violation = new Violation(measureID + "_" + String.valueOf(i), userId, 
								time, valuesListFloat.get(index) + "",
								"Value " + valueNamesPolicy.get(i) + " is not between "
										+ valueThresholdsPolicyFloat.get(i) + " and "
										+ valueThresholdsPolicyFloat.get(i + 1));
						measure.setCheckViolation(true);

						String violationState = gson.toJson(violation);
						stub.putStringState("violation" + violation.getID(), violationState);
					}
					System.out.println("Removing value at index " + (i + 1) + " with value "
							+ valueThresholdsPolicyFloat.get(i + 1));
					System.out.println(valueThresholdsPolicyFloat.remove(i + 1));
					System.out.println("New valueThresholdsPolicyFloat: " + valueThresholdsPolicyFloat);
				} else {
					String errorMessage = String.format("Operator %s is not valid", operatorThresholdsPolicy.get(i));
					System.out.println(errorMessage);
					throw new ChaincodeException(errorMessage);
				}
			}

			String measureState = gson.toJson(measure);
			stub.putStringState("measure" + measureID, measureState);
			user.setLastMeasure(measure);
			String userState = gson.toJson(user);
			stub.putStringState("user" + userId, userState);
		} else {
			String errorMessage = String.format("Policy type %s is not valid", policy.getpolicyType());
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage);
		}
	}
}
