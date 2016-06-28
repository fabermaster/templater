package fabermaster.utils.templater.exception;

public class TemplaterException extends Exception
{
  private static final long serialVersionUID = -3666355025143686925L;
  
  public String code;
  public String message;
  
  public TemplaterException(String code,
                            String message)
  {
    this.code    = code;
    this.message = message;
  }

  /**
   * @return the code
   */
  public String getCode()
  {
    return code;
  }

  /**
   * @return the message
   */
  public String getMessage()
  {
    return message;
  }

}
