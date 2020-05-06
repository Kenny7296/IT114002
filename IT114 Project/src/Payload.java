import java.io.Serializable;

public class Payload implements Serializable
{
	private static final long serialVersionUID = -6625037986217386003L;

	private String message;

	public void setMessage(String s)
	{
		this.message = s;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	private PayloadType payloadType;
	
	public void setPayloadType(PayloadType pt)
	{
		this.payloadType = pt;
	}
	
	public PayloadType getPayloadType()
	{
		return this.payloadType;
	}
	
	private String target;
	
	public void setTarget(String t)
	{
		this.target = t;
	}
	
	public String getTarget()
	{
		return this.target;
	}
	
	public Payload(PayloadType type, String message)
	{
		this(type, message, null);
	}
	
	public Payload(PayloadType type, String message, String target)
	{
			this.payloadType = type;
			this.message = message;
			this.target = target;
	}
	
	@Override
	public String toString()
	{
		return String.format("Type[%s], Message[%s]",
					getPayloadType().toString(), getMessage());
	}
}