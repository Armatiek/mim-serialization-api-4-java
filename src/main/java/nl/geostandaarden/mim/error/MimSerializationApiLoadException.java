package nl.geostandaarden.mim.error;

/**
 * MIM serialization specific exception class for all errors that can occur during load of a serialization
 */
public class MimSerializationApiLoadException extends MimSerializationApiException {
  
  private static final long serialVersionUID = 1L;

  public MimSerializationApiLoadException(String message, Throwable cause) {
    super(message, cause);
  }

}