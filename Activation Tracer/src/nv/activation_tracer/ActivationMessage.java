package nv.activation_tracer;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class ActivationMessage {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	//-----------------------------------------------------------------//
	
	/** Declare global variables **/
	
	private String appTaskID;
	private String messageType;
	private String messageAction;
	private String messageDirection;
	private String hostApp;
	private String externalApp;
	private String messageTag;
	private String messageTimestampString;
	private DateTime messageTimestampDate;
	
	//-----------------------------------------------------------------//
	
	/** Constructors **/
	
	protected ActivationMessage(String aTI, String mType, String mAction, String hApp, String eApp, String mTag, String mTimestamp) {
		appTaskID = aTI;
		messageType = mType;
		messageAction = mAction;
		if (mAction.equals("Sent")) {
			messageDirection = "to";
		}
		else {
			messageDirection = "from";
		}
		hostApp = hApp;
		externalApp = eApp;
		messageTag = mTag;
		messageTimestampString = mTimestamp;
		//Convert messageTimestampString into messageTimestampDate
		messageTimestampDate = convertStringToDate(messageTimestampString);
		//Rewrite messageTimestampString from messageTimestampDate
		messageTimestampString = messageTimestampDate.toString("dd-MMM-yy hh.mm.ss.SSSSSSSSS a");
	}
	
	//-----------------------------------------------------------------//
	
	/** Private Methods **/
	
	private DateTime convertStringToDate(String stringToConvert) {
		// "yyyy-MM-dd hh:mm:ss.SSSSSSSSS"
		String[] splitString = stringToConvert.split("-|\\.|:| ", 7);
		int year = Integer.parseInt(splitString[0]);
		int month = Integer.parseInt(splitString[1]);
		int date = Integer.parseInt(splitString[2]);
		int hour = Integer.parseInt(splitString[3]);
		int minute = Integer.parseInt(splitString[4]);
		int second = Integer.parseInt(splitString[5]);
		String milliTemp;
		if (splitString.length == 6) {
			milliTemp = "000";
		}
		else {
			milliTemp = splitString[6];
			if (milliTemp.length() == 0) {
				milliTemp += "000";
			}
			else if (milliTemp.length() == 1) {
				milliTemp += "00";
			}
			else if (milliTemp.length() == 2) {
				milliTemp += "0";
			}
			else if (milliTemp.length() > 3) {
				milliTemp = milliTemp.substring(0, 3);
			}
		}
		int milli = Integer.parseInt(milliTemp);
		DateTime tempDate = new DateTime(year, month, date, hour, minute, second, milli);
		return tempDate;
	}
	
	//-----------------------------------------------------------------//
	
	/** Mutator Methods **/
	
	//-----------------------------------------------------------------//
	
	/** Accessor Methods **/
	
	protected String getAppTaskID() {
		return appTaskID;
	}
	
	protected String getMessageType() {
		return messageType;
	}
	
	protected String getMessageAction() {
		return messageAction;
	}
	
	protected String getMessageDirection() {
		return messageDirection;
	}
	
	protected String getHostApp() {
		return hostApp;
	}
	
	protected String getExternalApp() {
		return externalApp;
	}
	
	protected String getMessageTag() {
		return messageTag;
	}
	
	protected String getMessageTimestampString() {
		return messageTimestampString;
	}
	
	protected DateTime getMessageTimestampDate() {
		return messageTimestampDate;
	}
	
	protected String getElapsedTime(ActivationMessage message) {
		String elapsedTime;
		DateTime endTime = message.getMessageTimestampDate();
		Period period = new Period(messageTimestampDate, endTime);
		PeriodFormatter formatter = new PeriodFormatterBuilder()
			.printZeroAlways()
			.minimumPrintedDigits(2)
			.appendHours().appendSuffix(":")
			.appendMinutes().appendSuffix(":")
			.appendSeconds().appendSuffix(".")
			.appendMillis().appendSuffix("")
			.toFormatter();
		elapsedTime = formatter.print(period);
		return elapsedTime;
	}
	
	public String toString() {
		String returnString = hostApp + " " + messageAction + " " + messageType + " " + messageDirection + "  " + externalApp + 
							  "; \""+ messageTag + "\": " + messageTimestampString + " .  " + appTaskID;
		return returnString;
	}
	
	//-----------------------------------------------------------------//
	
}
