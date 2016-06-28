package fabermaster.utils.templater.model.restful;


public interface ISettings
{
  /**
   * Enumeration for API method definition
   * 
   * @author fabrizio.parlani
   *
   */
  public static enum EMethod
  {
    POST   ("POST",   "@POST"),
    PUT    ("PUT",    "@PUT"),
    GET    ("GET",    "@GET"),
    DELETE ("DELETE", "@DELETE");
    
    private String code;
    private String type;
    
    private EMethod(String code,
                    String type)
    {
      this.code = code;
      this.type = type;
    }

    /**
     * @return the code
     */
    public String getCode()
    {
      return code;
    }

    /**
     * @return the type
     */
    public String getType()
    {
      return type;
    }
  }

  public static enum EMediaType
  {
    JSON ("application/json", "MediaType.APPLICATION_JSON"),
    XML ("application/xml", "MediaType.APPLICATION_XML");
    
    private String contentType;
    private String mediaType;

    private EMediaType (String contentType,
                        String mediaType)
    {
      this.contentType = contentType;
      this.mediaType   = mediaType;
    }

    /**
     * @return the contentType
     */
    public String getContentType()
    {
      return contentType;
    }

    /**
     * @return the mediaType
     */
    public String getMediaType()
    {
      return mediaType;
    }
  }

  /**
   * Enumeration for API implementation
   * 
   * @author Fabrizio Parlani
   */
  public static enum EFacadeMethod
  {
    INSERT           ("insert",       EMethod.POST, false, new EMediaType[] { EMediaType.JSON }, new EMediaType[] { EMediaType.JSON }),
    UPDATE           ("update",       EMethod.PUT,  false, new EMediaType[] { EMediaType.JSON }, new EMediaType[] { EMediaType.JSON }),
    UPDATE_BULK      ("updateBulk",   EMethod.PUT,  true,  new EMediaType[] { EMediaType.JSON }, new EMediaType[] { EMediaType.JSON }),
    RETRIEVE_DETAILS ("retrieve",     EMethod.GET,  false, new EMediaType[] { EMediaType.JSON }, null),
    RETRIEVE_BULK    ("retrieveList", EMethod.GET,  false, new EMediaType[] { EMediaType.JSON }, null);
    
    private String       methodName;
    private EMethod      methodType;
    private boolean      methodBulk;
    private EMediaType[] methodProduces;
    private EMediaType[] methodConsumes;
    
    
    private EFacadeMethod(String       methodName,
                          EMethod      methodType,
                          boolean      methodBulk,
                          EMediaType[] methodProduces,
                          EMediaType[] methodConsumes)
    {
      this.methodName     = methodName;
      this.methodType     = methodType;
      this.methodBulk     = methodBulk;
      this.methodProduces = methodProduces;
      this.methodConsumes = methodConsumes;
    }

    /**
     * @return the methodName
     */
    public String getMethodName()
    {
      return methodName;
    }

    /**
     * @return the methodType
     */
    public EMethod getMethodType()
    {
      return methodType;
    }

    /**
     * @return the methodBulk
     */
    public boolean isMethodBulk()
    {
      return methodBulk;
    }

    /**
     * @return the methodProduces
     */
    public EMediaType[] getMethodProduces()
    {
      return methodProduces;
    }

    /**
     * @return the methodConsumes
     */
    public EMediaType[] getMethodConsumes()
    {
      return methodConsumes;
    }

    

  }
}
