package at.tuwien.ase.tripidude.api;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class FieldError {
	
    public List<String> getCodes() {
		return codes;
	}
	public void setCodes(List<String> codes) {
		this.codes = codes;
	}
	public String getArguments() {
		return arguments;
	}
	public void setArguments(String arguments) {
		this.arguments = arguments;
	}
	public String getDefaultMessage() {
		return defaultMessage;
	}
	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getRejectedValue() {
		return rejectedValue;
	}
	public void setRejectedValue(String rejectedValue) {
		this.rejectedValue = rejectedValue;
	}
	public Boolean getBindingFailure() {
		return bindingFailure;
	}
	public void setBindingFailure(Boolean bindingFailure) {
		this.bindingFailure = bindingFailure;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	private List<String> codes;	
	private String arguments;
	private String defaultMessage;
	private String objectName;
	private String field;
	private String rejectedValue;
	private Boolean bindingFailure;
	private String code;
}
