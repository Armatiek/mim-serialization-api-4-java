package nl.geostandaarden.mim.error;

/**
 * MIM serialization specific exception class for all errors that can occur during save of a serialization
 */
public class MimSerializationApiSaveException extends MimSerializationApiException {
  
  private static final long serialVersionUID = 1L;

  public MimSerializationApiSaveException(String message, Throwable cause) {
    super(message, cause);
  }

}