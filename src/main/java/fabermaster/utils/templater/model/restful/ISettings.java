package fabermaster.utils.templater.model.restful;

import java.io.File;
import java.util.List;

import fabermaster.utils.templater.helper.Assorted;


public interface ISettings
{
  
  /**
   * Enumeration for internal used templates
   * 
   * @author Fabrizio Parlani
   *
   */
  public static enum EInternalTemplates
  {
    INTERFACES      ("Interface.tpl"),
    IMPLEMENTATIONS ("Implementation.tpl");
   
    private static final String RESOURCE_PATH = "fabermaster" + File.separatorChar + "template" + File.separatorChar + "restful" + File.separatorChar;
    private String              templateName;

    /**
     * Enumerator constructor
     * 
     * @param templateName
     * @author Fabrizio Parlani
     */
    private EInternalTemplates(String templateName)
    {
      this.templateName = templateName;
    }

    /**
     * @return the templateName with full resource path
     */
    public String getFullTemplateName()
    {
      return RESOURCE_PATH + templateName;
    }
    
    /**
     * @return the templateName
     */
    public String getTemplateName()
    {
      return templateName;
    }
  }
  
  /**
   * Enumeration for API method definition
   * 
   * @author Fabrizio Parlani
   *
   */
  public static enum EMethod
  {
    POST   ("POST",   "@POST", "Create"),
    PUT    ("PUT",    "@PUT",  "Update"),
    GET    ("GET",    "@GET"),
    DELETE ("DELETE", "@DELETE");
    
    private String code;
    private String type;
    private String requestParameterPrefix;
    
    private EMethod(String code,
                    String type,
                    String requestParameterPrefix)
    {
      this.code                   = code;
      this.type                   = type;
      this.requestParameterPrefix = requestParameterPrefix;
    }

    private EMethod(String code,
                    String type)
    {
      this(code, type, null);
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

    /**
     * Composes service method request parameter where needed
     * 
     * @param object
     * @return
     * @author Fabrizio Parlani
     */
    private String getRequestParameter(String object)
    {
      //check if method accepts input request parameter
      if (Assorted.isNotEmpty(this.requestParameterPrefix, true))
      {
        //return composed request parameter
        return new StringBuilder(this.requestParameterPrefix)
                         .append(Assorted.checkNullString(object, true))
                         .append("RequestType request,\n")
                         .toString();
      }

      //method foesn't need request parameter
      return "";
    }

    /**
     * Composes method signature header
     * 
     * @param exposure
     * @param object
     * @return
     */
    public String getMethodSignatureHeader(ExposureType exposure,
                                           String       object)
    {
      return new StringBuilder("  public Response ")
                       .append(exposure.getName())
                       .append("(")
                       .append(this.getRequestParameter(object))
                       .toString();
    }

    /**
     * Composes method implementation header
     * 
     * @param exposure
     * @param object
     * @return
     */
    public String getMethodImplementationHeader(ExposureType exposure,
                                                String       object)
    {
      return new StringBuilder("  @Override\n  public Response ")
                       .append(exposure.getName())
                       .append("(")
                       .append(this.getRequestParameter(object))
                       .toString();
    }

    /**
     * Gets enumerator instance from provided type
     * 
     * @param type
     * @return
     * @author Fabrizio Parlani
     */
    public static EMethod fromType(String type)
    {
      //check provided type
      if (Assorted.isNotEmpty(type, true))
      {
        //cycle enumerator types
        for (EMethod method : EMethod.values())
        {
          //check if cycled type matches provided one
          if (Assorted.equals(method.getType(), type))
          {
            //return found enumerator instance
            return method;
          }
        }
      }

      //provided type is either invalid or doesn't match any enumerator type
      return null;
    }
  }

  /**
   * Enumerator used to map service consumers and producers
   * 
   * @author Fabrizio Parlani
   */
  public static enum EServiceConsumeProduce
  {
    PRODUCES ("@Produces"),
    CONSUMES ("@Consumes");
    
    private String consumeProduce;
    
    /**
     * Enumerator constructor
     * 
     * @param consumeProduce
     * @author Fabrizio Parlani
     */
    private EServiceConsumeProduce(String consumeProduce)
    {
      this.consumeProduce = consumeProduce;
    }
 
    /**
     * Return composed media types
     * 
     * @param items
     * @return
     * @author Fabrizio Parlani
     */
    public String getMediaTypes(List<String> items)
    {
      //convert media types list into string
      String list = fillList(items);

      //check for empty list
      if (Assorted.isNotEmpty(list, true))
      {
        //return composed consumes/produces annotation
        return new StringBuilder("  ")
                         .append(this.consumeProduce)
                         .append("(")
                         .append(list)
                         .append(")\n")
                         .toString();
      }

      // return empty string
      return "";
    }

    /**
     * Converts provided item list into a string
     * 
     * @param items
     * @return
     * @author Fabrizio Parlani
     */
    private String fillList(List<String> items)
    {
      // declare returning object
      StringBuilder builtList = new StringBuilder("");

      // check for provided values
      if (Assorted.isNotEmpty(items))
      {
        // cycle across item list
        for (String item : items)
        {
          // append item
          builtList.append(item).append(", ");
        }

        // return composed list
        return builtList.substring(0, builtList.length() - 2);
      }

      // return invalid String
      return null;
    }
  }

  /**
   * Enumerator used to map consumer/producer media types
   * 
   * @author Fabrizio Parlani
   *
   */
  public static enum EMediaType
  {
    JSON ("application/json", "MediaType.APPLICATION_JSON"),
    XML  ("application/xml", "MediaType.APPLICATION_XML");
    
    private String contentType;
    private String mediaType;

    /**
     * Enumerator constructor
     * 
     * @param contentType
     * @param mediaType
     * @author Fabrizio Parlani
     */
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
   * Enumerator for method parameter types
   * 
   * @author Fabrizio Parlani
   */
  public static enum EParamType
  {
    HEADER ("@HeaderParam"),
    PATH   ("@PathParam"),
    QUERY  ("@QueryParam");
    
    private static final String IMPLEMENTATION_HEADER_PARAM = "@Header(value=\"";
    private String type;

    /**
     * Enumerator constructor
     * 
     * @param type
     * @author Fabrizio Parlani
     */
    private EParamType(String type)
    {
      this.type = type;
    }

    /**
     * @return the type
     */
    public String getType()
    {
      return type;
    }

    /**
     * Build service method parameters 
     * 
     * @param parameters
     * @return
     * @author Fabrizio Parlani
     */
    public String getMethodParameters(List<ParamType> parameters)
    {
      //declare object where to store built parameters list
      StringBuilder serviceParams = new StringBuilder("");
      
      //check for provided parameters
      if (Assorted.isNotEmpty(parameters))
      {
        //cycle parameters
        for (ParamType parameter : parameters)
        {
          //composes method parameters list
          serviceParams.append(String.format("%20s(\"", this.getType()))
                       .append((this.equals(PATH)) ? parameter.getName() : parameter.getName())
                       .append(String.format("\")%20s%20s,\n", parameter.getType(), parameter.getMethodParam()));
        }
      }

      //return composed parameters list if present
      return serviceParams.toString();
    }

    /**
     * Builds implementation parameters list mapping all parameters as Camel Header parameters
     * 
     * @param parameters
     * @return
     * @author Fabrizio Parlani
     */
    public static String getImplementationParameters(List<ParamType> parameters)
    {
      //declare object where to store built parameters list
      StringBuilder serviceParams = new StringBuilder("");
      
      //check for provided parameters
      if (Assorted.isNotEmpty(parameters))
      {
        //cycle parameters
        for (ParamType parameter : parameters)
        {
          //composes method implementation parameters list
          serviceParams.append(String.format("%20s%s\")%20s%20s,\n", IMPLEMENTATION_HEADER_PARAM, 
                                                                     parameter.getMethodParam(), 
                                                                     parameter.getType(), 
                                                                     parameter.getMethodParam()));
        }
      }

      //return composed parameters list if present
      return serviceParams.toString();
      
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
    
    /**
     * Enumerator constructor
     * 
     * @param methodName
     * @param methodType
     * @param methodBulk
     * @param methodProduces
     * @param methodConsumes
     * @author Fabrizio Parlani
     */
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
