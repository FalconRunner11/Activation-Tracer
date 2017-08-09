package nv.activation_tracer;

public class Environment {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	//-----------------------------------------------------------------//
	
	/** Declare global variables **/
	
	private String envName;
	private String olConnString;
	private String olUrl;
	private String olUsername;
	private String olPassword;
	private String cimConnString;
	private String cimUrl;
	private String cimUsername;
	private String cimPassword;
	private String alConnString;
	private String alUrl;
	private String alUsername;
	private String alPassword;
	private String mlConnString;
	private String mlUrl;
	private String mlUsername;
	private String mlPassword;
	private final String urlPrefix = "jdbc:oracle:thin:@";
	
	//-----------------------------------------------------------------//
	
	/** Constructors **/
	
	protected Environment(String eN, String oCS, String oUn, String oP, String cCs, String cUn, String cP,
							  String aCS, String aUn, String aP, String mCS, String mUn, String mP) {
		envName = eN;
		olConnString = oCS;
		olUrl = urlPrefix + olConnString;
		olUsername = oUn;
		olPassword = oP;
		cimConnString = cCs;
		cimUrl = urlPrefix + cimConnString;
		cimUsername = cUn;
		cimPassword = cP;
		alConnString = aCS;
		alUrl = urlPrefix + alConnString;
		alUsername = aUn;
		alPassword = aP;
		mlConnString = mCS;
		mlUrl = urlPrefix + mlConnString;
		mlUsername = mUn;
		mlPassword = mP;
	}
	
	protected Environment(String eN) {
		envName = eN;
		olConnString = "";
		olUrl = urlPrefix + olConnString;
		olUsername = "";
		olPassword = "";
		cimConnString = "";
		cimUrl = urlPrefix + cimConnString;
		cimUsername = "";
		cimPassword = "";
		alConnString = "";
		alUrl = urlPrefix + alConnString;
		alUsername = "";
		alPassword = "";
		mlConnString = "";
		mlUrl = urlPrefix + mlConnString;
		mlUsername = "";
		mlPassword = "";
	}

	//-----------------------------------------------------------------//
	
	/** Methods **/
	
	protected String getEnvironmentName() {
		return envName;
	}
	
	protected String getOlConnString() {
		return olConnString;
	}
	
	protected String getOlUrl() {
		return olUrl;
	}
	
	//-----------------------------------------------------------------//
	
	/** Private Methods **/
	
	//-----------------------------------------------------------------//
	
	/** Mutator Methods **/
	
	//-----------------------------------------------------------------//
	
	/** Accessor Methods **/
	
	protected String getOlUsername() {
		return olUsername;
	}
	
	protected String getOlPassword() {
		return olPassword;
	}
	
	protected String getCimConnString() {
		return cimConnString;
	}
	
	protected String getCimUrl() {
		return cimUrl;
	}
	
	public String getCimUsername() {
		return cimUsername;
	}
	
	protected String getCimPassword() {
		return cimPassword;
	}
	
	protected String getAlConnString() {
		return alConnString;
	}
	
	public String getAlUrl() {
		return alUrl;
	}
	
	protected String getAlUsername() {
		return alUsername;
	}
	
	protected String getAlPassword() {
		return alPassword;
	}
	
	protected String getMlConnString() {
		return mlConnString;
	}
	
	protected String getMlUrl() {
		return mlUrl;
	}
	
	protected String getMlUsername() {
		return mlUsername;
	}
	
	protected String getMlPassword() {
		return mlPassword;
	}
	
	//-----------------------------------------------------------------//
	
}
