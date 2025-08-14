package nl.geostandaarden.mim.error;

/**
 * MIM serialization specific exception class for all errors that can occur during setting/getting XHTML as string
 */
public class MimSerializationApiXhtmlException extends MimSerializationApiException {
  
  private static final long serialVersionUID = 1L;

  public MimSerializationApiXhtmlException(String message, Throwable cause) {
    super(message, cause);
  }

}