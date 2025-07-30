package nl.geostandaarden.mim.error;

public abstract class MimSerializationApiException extends Exception {

  private static final long serialVersionUID = 1L;
  
  public MimSerializationApiException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public MimSerializationApiException(String message) {
    super(message);
  }

}