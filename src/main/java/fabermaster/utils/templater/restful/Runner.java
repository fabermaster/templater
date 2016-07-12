package fabermaster.utils.templater.restful;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import fabermaster.utils.templater.exception.TemplaterException;
import fabermaster.utils.templater.helper.Assorted;
import fabermaster.utils.templater.model.restful.Api;
import fabermaster.utils.templater.model.restful.EntitySetting;
import fabermaster.utils.templater.model.restful.ExposureType;
import fabermaster.utils.templater.model.restful.ISettings.EFacadeMethod;
import fabermaster.utils.templater.model.restful.ISettings.EInternalTemplates;
import fabermaster.utils.templater.model.restful.ISettings.EMediaType;
import fabermaster.utils.templater.model.restful.ISettings.EMethod;
import fabermaster.utils.templater.model.restful.ISettings.EParamType;
import fabermaster.utils.templater.model.restful.ISettings.EServiceConsumeProduce;
import fabermaster.utils.templater.model.restful.MethodSetting;
import fabermaster.utils.templater.model.restful.ParamType;
import fabermaster.utils.templater.model.restful.ParamTypeSetting;
import fabermaster.utils.templater.model.restful.SettingType;

public class Runner
{
  
  //declare constants
  private static final String INTERFACE_PACKAGE         = "ia";
  private static final String INTERFACE_RESTFUL_PACKAGE = "restful";

  //declare used object(s)
  private Map<String, String>                   interfaces                 = new LinkedHashMap<String, String>(0);
  private Api                                   structure                  = null;
  private Map<ApiKey, Api>                      workItems                  = new LinkedHashMap<ApiKey, Api>(0);
  private StringBuilder                         template                   = null;
//  private String                                outFolder                  = null;
  private MethodSetting                         methodSettings             = null;
  private Map<ParamKey, List<ParamTypeSetting>> entityMethodQueryParamsMap = new HashMap<ParamKey, List<ParamTypeSetting>>(0);


  //declare class attribute(s)
  private String basePath;
  private String fileNameFilter;
  private String settings;

  /**
   * Class Constructor
   * 
   */
  public Runner(String basePath,
                String fileNameFilter,
                String settings)
  {
    this.basePath       = basePath;
    this.fileNameFilter = fileNameFilter;
    this.settings       = settings;
  }

  public void run()
  {
    try
    {
      //preliminary checks over provided parameters
      checkParams();
      //load settings;
      loadSettings();
      //gets interfaces to inspect
      loadInterfaces(inspectBasePath());
      //stripping data from settings and file
      inspectInterfaces();
      //write interfaces
      writeInterfaces();
      //write implementations
      writeImplementations();
    }
    catch ( TemplaterException ex )
    {
      System.err.println("Error   : [" + ex.getCode() + "]\nMessage : [" + ex.getMessage() +"]");
    }
  }

  /**
   * Checks provided parameters
   * 
   * @throws TemplaterException
   * @author Fabrizio Parlani
   */
  private void checkParams()
  throws TemplaterException
  {
    if (Assorted.isEmpty(basePath, true))
    {
      //throw custom exception
      throw new TemplaterException("TEX-VAL-001", "Needed base path has not been provided");
    }
    if (Assorted.isEmpty(fileNameFilter, true))
    {
      //throw custom exception
      throw new TemplaterException("TEX-VAL-002", "Needed file name filter has not been provided");
    }
    if (Assorted.isEmpty(settings, true))
    {
      //throw custom exception
      throw new TemplaterException("TEX-VAL-003", "Needed method settings path has not been provided");
    }
  }

  /**
   * Loads provided settings file
   * 
   * @throws TemplaterException
   * @author Fabrizio Parlani
   */
  private void loadSettings()
  throws TemplaterException
  {
    try
    {
      //load settings from provided file
      this.methodSettings = (new Gson()).fromJson(new String(Files.readAllBytes(Paths.get(this.settings)), 
                                                             StandardCharsets.UTF_8), 
                                                  MethodSetting.class);

      //load entity method query parameters map
      loadEntityMethodQueryParamsMap();
    }
    catch ( IOException ex )
    {
      //throw custom exception
      throw new TemplaterException("TEX-009", "An invalid settings file has been provided. Following I/O exception occurred [" + ex.getMessage() + "]");
    }
    catch ( JsonSyntaxException ex )
    {
      //throw custom exception
      throw new TemplaterException("TEX-010", "Unable to unmarshal provided settings file. Following JSON exception occurred [" + ex.getMessage() + "]");
    }
  }

  /**
   * Strips query parameters mapping them by methods and then by entity, previously checking for existing settings by method
   * 
   * @author Fabrizio Parlani
   */
  private void loadEntityMethodQueryParamsMap()
  {
    //strip query parameters mapping them by methods and then by entity, previously checking for existing settings by method
    if ((this.methodSettings != null) &&
        (Assorted.isNotEmpty(this.methodSettings.getSettings())))
    {
      //cycle settings
      for (SettingType setting : this.methodSettings.getSettings())
      {
        //check for existing query parameters
        if (Assorted.isNotEmpty(setting.getQueryParams()))
        {
          //cycle query parameters list by entity
          for (EntitySetting entitySetting : setting.getQueryParams())
          {
            //check for existing parameters
            if ((Assorted.isNotEmpty(entitySetting.getEntityName(), true)) &&
                (Assorted.isNotEmpty(entitySetting.getParameters())))
            {
              //add parameters for each mapped method
              for (String methodName : setting.getName())
              {
                //add query parameters mapped by entity name and method
                this.entityMethodQueryParamsMap.put(new ParamKey(entitySetting.getEntityName(), 
                                                                 methodName),
                                                    entitySetting.getParameters());
//                //log loaded query parameters for entity and method
//                System.out.println("   -[Added Query parameters for Entity {" + entitySetting.getEntityName() + "} and Method {" + methodName + "}]-");
//                System.out.println("      -> (" + entitySetting.getParameters() + ")");
              }
            }
          }
        }
      }
    }
  }

  /**
   * Inspects provided base path
   * 
   * @throws TemplaterException
   * @author Fabrizio Parlani
   */
  private List<String> inspectBasePath()
  throws TemplaterException
  {
    //declare used object
    File          directory;
    File[]        fileList;
    List<String>  folders    = new ArrayList<String>(0);

    try
    {
      //point provided basePath
      directory = new File(this.basePath);
      // get all the files from a directory
      fileList  = directory.listFiles();

      //check for a valid provided 
      if (fileList != null)
      {
        //scanning provided basePath
        for (File file : fileList)
        {
          //check for a found directory
          if (file.isDirectory())
          {
            //add retrieved folder
            folders.add(file.getAbsolutePath());
          }
        }

        //check for retrieved working paths
        if (Assorted.isEmpty(folders))
        {
          //throw custom exception
          throw new TemplaterException("TEX-004", "Provided base path [" + this.basePath + "] does not contains folder(s) to inspect");
        }

        //return found working path(s)
        return folders;
      }
      else
      {
        //throw custom exception
        throw new TemplaterException("TEX-002", "Provided base path does not denote a directory [" + this.basePath + "]");
      }
    }
    catch ( NullPointerException ex )
    {
      //throw custom exception
      throw new TemplaterException("TEX-001", "Provided base path is invalid");
    }
    catch ( SecurityException ex )
    {
      //throw custom exception
      throw new TemplaterException("TEX-003", "Security exception occurred. Unable to get read access for directory [" + this.basePath + "]");
    }
  }

  /**
   * Loads service interfaces files from working folder(s)
   * 
   * @param workingPaths
   * @throws TemplaterException
   * @author Fabrizio Parlani
   */
  private void loadInterfaces(List<String> workingPaths)
  throws TemplaterException
  {
    //declare used object
    File          directory;
    File[]        fileList;
    String        workingPath = null;

    try
    {
      //cycle retrieved working paths
      for (String path : workingPaths)
      {
        //composes working path
        workingPath = path + File.separatorChar + INTERFACE_PACKAGE;
        //get interface agreement sub-path
        directory   = new File(workingPath);
        //gets filtered file list
        fileList  = directory.listFiles(new ServiceFileFilter(this.fileNameFilter));
      
        //check for a valid provided 
        if (fileList != null)
        {
          //scanning provided basePath
          for (File file : fileList)
          {
            //save retrieved interface
            interfaces.put(path, file.getAbsolutePath());
          }
        }
        else
        {
          //TODO : fill warnings collection
          System.out.println("Composed path used to retrieve service IA does not exist or does not contains a Service java file");
        }
      }
    }
    catch ( NullPointerException ex )
    {
      //throw custom exception
      throw new TemplaterException("TEX-005", "Composed working path is invalid [" + workingPath + "]");
    }
    catch ( SecurityException ex )
    {
      //throw custom exception
      throw new TemplaterException("TEX-006", "Security exception occurred. Unable to get read access for directory [" + workingPath + "]");
    }
  }

  /**
   * 
   * @throws TemplaterException
   */
  private void inspectInterfaces()
  throws TemplaterException
  {
    //cycle retrieved interfaces
    for (Entry<String, String> entry : interfaces.entrySet())
    {
      try
      {
        //create new structure to put into map of working items
        structure = new Api();

        //set API object stripping from file name
        structure.setObject((new RangeHelper("" + File.separatorChar, 
                                                this.fileNameFilter)).getRange(entry.getValue(), 
                                                                               true, 
                                                                               false));
        //set API object id field prefix from stripped API object
        structure.setObjectId(new StringBuilder(structure.getObject().substring(0, 1).toLowerCase())
                                        .append(structure.getObject().substring(1))
                                        .toString());
        //set API entity stripping from file name
        structure.setEntity((new RangeHelper("service" + File.separatorChar, 
                                             File.separatorChar + INTERFACE_PACKAGE)).getRange(entry.getValue(), 
                                                                                               true, 
                                                                                               false));
        //set API component stripping from file name
        structure.setComponent((new RangeHelper("exposure" + File.separatorChar, 
                                                File.separatorChar + "service")).getRange(entry.getValue(), 
                                                                                          true, 
                                                                                          false));
        //set API package prefix stripping from file name
        structure.setPackagePrefix((new RangeHelper("src" + File.separatorChar + "main" + File.separatorChar + "java" + File.separatorChar, 
                                                    File.separatorChar + structure.getComponent())).getRange(entry.getValue(), 
                                                                                                             false, 
                                                                                                             false));
        structure.setPackagePrefix(structure.getPackagePrefix().replaceAll("\\" + File.separatorChar, "."));

        //set API absolute file name
        structure.setAbsolutePath(new StringBuilder(entry.getKey())
                                            .append(File.separatorChar)
                                            .append(INTERFACE_RESTFUL_PACKAGE)
                                            .append(File.separatorChar)
                                            .append(INTERFACE_PACKAGE)
                                            .append(File.separatorChar)
                                            .append(structure.getObject())
                                            .append(this.fileNameFilter)
                                            .toString());

        //set API exposures
        structure.getExposures().addAll(fillExposure(structure));

        //add cycled structure
        workItems.put(new ApiKey(entry.getKey(),
                                 new String(Files.readAllBytes(Paths.get(entry.getValue())), 
                                                               StandardCharsets.UTF_8)), 
                      structure);

//        //log structure
//        System.out.println(" - Structure   [" + structure + "]");
      }
      catch ( TemplaterException ex )
      {
        throw ex;
      }
      catch ( IOException ex )
      {
        throw new TemplaterException("TEX-007", "Unable to get file content from [" + entry.getValue()  + "] due to following I/O exception [" + ex.getMessage() + "]");
      }
      catch ( Exception ex )
      {
        throw new TemplaterException("TEX-008", "Unable to strip object name from [" + entry.getValue()  + "] due to following exception [" + ex.getMessage() + "]");
      }
      finally
      {
        //destroy used structure
        structure = null;
      }
    }
  }

  /**
   * Fill facade method exposure attributes
   * 
   * @param structure
   * @throws TemplaterException
   * @return
   */
  private List<ExposureType> fillExposure(Api structure)
  throws TemplaterException
  {
    //declare returning objects
    List<ExposureType> exposures = new ArrayList<ExposureType>(0);

    //declare used objects
    ExposureType       exposure  = null;

    //cycle across mapped exposures
    for (EFacadeMethod facadeMethod : EFacadeMethod.values())
    {
      //create exposure type object
      exposure = new ExposureType();

      //fill exposure attributes
      exposure.setMethod(facadeMethod.getMethodType().getType());
      exposure.setName(facadeMethod.getMethodName());
      exposure.setBulk(facadeMethod.isMethodBulk());

      //set API media type produces
      fillMediaTypes(facadeMethod.getMethodProduces(),
                     exposure.getProduces());

      //set API media type consumes
      fillMediaTypes(facadeMethod.getMethodConsumes(),
                     exposure.getConsumes());

      //set path parameters from provided settings
      fillMethodParameters(exposure,
                           structure.getObject(),
                           structure.getEntity());

      //add filled exposure into returning list
      exposures.add(exposure);

      //destroy used object
      exposure = null;
    }

    //return filled exposure(s)
    return exposures;
  }

  /**
   * Fills a consumes/produces media type collection
   * 
   * @param mediaTypes
   * @param container
   * @author Fabrizio Parlani
   */
  private void fillMediaTypes(EMediaType[] mediaTypes,
                              List<String> container)
  {
    //check for valid provided media types collection
    if (Assorted.isNotEmpty(mediaTypes))
    {
      //cycle media type produces
      for (EMediaType mediaType : mediaTypes)
      {
        container.add(mediaType.getMediaType());
      }
    }
  }

  /**
   * Fills API method parameters
   * 
   * @param exposure
   * @param object
   * @param entity
   * @throws TemplaterException
   * @author Fabrizio Parlani
   */
  private void fillMethodParameters(ExposureType exposure,
                                    String       object,
                                    String       entity)
  throws TemplaterException
  {
    //check for valid provided settings
    if (methodSettings != null)
    {

      System.out.println("  - Object is [" + object + "] - Entity is [" + entity + "] - Method is [" + exposure.getName()  + "]");

      //cycle settings
      for (SettingType methodSetting : methodSettings.getSettings())
      {
        System.out.println("      # Path Params  [" + methodSetting.getPathParams() + "]");
        //check method setting
        if (Assorted.isNotEmpty(methodSetting.getName()))
        {
          //check if cycled method name list contains provided one from exposure
          if (methodSetting.getName().contains(exposure.getName()))
          {
//            System.out.println("      # Method Name                 [" + exposure.getName() + "]");
//            System.out.println("      # Method Contains Path Params [" + Assorted.isNotEmpty(methodSetting.getPathParams()) + "]");
//            System.out.println("      # Method Path Params          [" + methodSetting.getPathParams() + "]");
            //check for existing path parameters
            if (Assorted.isNotEmpty(methodSetting.getPathParams()))
            {
              //fills method path parameters
              fillParameters(exposure.getPathParams(),
                             methodSetting.getPathParams(),
                             object);
            }
            else
            {
              //throw custom exception
              throw new TemplaterException("TEX-013", "Method(s) setting must contains at least one path parameters defined");
            }

            //fills probably existing header parameters
            fillParameters(exposure.getHeaderParams(),
                           methodSetting.getHeaderParams(),
                           object);

            //fills probably existing query parameters
            fillParameters(exposure.getQueryParams(),
                           entityMethodQueryParamsMap.containsKey(new ParamKey(entity, exposure.getName())) ?  entityMethodQueryParamsMap.get(new ParamKey(entity, exposure.getName())) : null,
                           object);

            //exit cycle
            break;
          }
        }
        else
        {
          //throw custom exception
          throw new TemplaterException("TEX-012", "Names list into settings file must have at least one value");
        }
      }
    }
    else
    {
      //throw custom exception
      throw new TemplaterException("TEX-011", "An empty settings file has been provided");
    }
  }

  /**
   * Fills generic parameters collection
   * 
   * @param apiParameters
   * @param parameterSettings
   * @param object
   * @throws TemplaterException
   * @author Fabrizio Parlani
   */
  private void fillParameters(List<ParamType>        apiParameters,
                              List<ParamTypeSetting> parameterSettings,
                              String                 object)
  throws TemplaterException
  {
    //check provided parameters setting collection
    if (Assorted.isNotEmpty(parameterSettings))
    {
      for (ParamTypeSetting setting : parameterSettings)
      {
        //save cycled setting into API method parameters
        ParamType apiParam = ParamType.class.cast(setting);
        
//        System.out.println("     ----- SETTING PARAM NAME ----- [" + setting.getName() + "]");
//        System.out.println("     ----- API PARAM NAME -----     [" + apiParam.getName() + "]");
        
        //check for name handling
        if ((setting.isToComplete() != null) && (setting.isToComplete()))
        {
          //complete parameter name with object type prefix
          apiParam.setName(object.toUpperCase() + apiParam.getName());
          apiParam.setMethodParam(object.toLowerCase() + apiParam.getMethodParam());
//          System.out.println("  Object [" + object + "] has been enriched with following built Path Param [" + apiParam + "]");
        }
        //check is to decode parameter flag
        apiParam.setToDecode((apiParam.isToDecode() == null) ? false : apiParam.isToDecode());

        
        
        //add parameter to returning collection
        apiParameters.add(apiParam);

        //destroy used object
        apiParam = null;
      }
    }
  }

  /**
   * Writes interfaces from gathered informations
   * 
   * @throws TemplaterException
   * @author Fabrizio Parlani
   */
  private void writeInterfaces()
  throws TemplaterException
  {
    try
    {
      //load interface template
      loadInternalTemplate(EInternalTemplates.INTERFACES.getTemplateName());
      
      //cycle across loaded structure
      for (Entry<ApiKey, Api> workItem : workItems.entrySet())
      {
        //save loaded resource template into a temporary string
        String        temp    = template.toString();
        StringBuilder methods = new StringBuilder("\n");
        Api           filler  = workItem.getValue();

        //fill template
        temp = temp.replaceAll("<packagePrefix>", filler.getPackagePrefix());
        temp = temp.replaceAll("<component>",     filler.getComponent());
        temp = temp.replaceAll("<entity>",        filler.getEntity());
        temp = temp.replaceAll("<object>",        filler.getObject());

        //cycle API interface methods to map
        for (ExposureType exposure : filler.getExposures())
        {
          //composes API methods
          methods.append("  ").append(exposure.getMethod()).append("\n")
                 .append(buildServicePath(exposure.getPathParams()))
                 .append(EServiceConsumeProduce.CONSUMES.getMediaTypes(exposure.getConsumes()))
                 .append(EServiceConsumeProduce.PRODUCES.getMediaTypes(exposure.getProduces()))
                 .append(EMethod.fromType(exposure.getMethod()).getMethodSignatureHeader(exposure, 
                                                                                         filler.getObject()))
                 .append(EParamType.HEADER.getMethodParameters(exposure.getHeaderParams()))
                 .append(EParamType.PATH.getMethodParameters(exposure.getPathParams()))
                 .append(EParamType.QUERY.getMethodParameters(exposure.getQueryParams()));
          //replace last inserted comma with method signature closure
          methods = methods.replace(methods.lastIndexOf(","), methods.length(), ");\n")
                           .append("\n");
        }

        //placeholder substitution for mapped interface methods 
        temp = temp.replace("<interfaceContent>", methods);

        try
        {
          //properly composes out file destination
          String filePath = new StringBuilder(Assorted.checkNullString(this.methodSettings.getOutFolder(), true))
                                      .append(File.separatorChar)
                                      .append(filler.getAbsolutePath().substring(filler.getAbsolutePath().indexOf("src" + File.separatorChar)))
                                      .toString();
          //write interface file
          Assorted.writeFile(filePath,
                             temp.getBytes());
          
          //log created file 
          System.out.println("  Interface successfully created into [" + filePath + "]");
        }
        catch ( Exception ex )
        {
          ex.printStackTrace();
        }
//        //log composed interface
//        System.out.println(temp + "\n\n");
      }
    }
    catch ( TemplaterException ex )
    {
      //throw back exception
      throw ex;
    }
  }

  /**
   * Writes service implementations from gathered informations
   * 
   * @throws TemplaterException
   * @author Fabrizio Parlani
   */
  private void writeImplementations()
  throws TemplaterException
  {
    try
    {
      //load interface template
      loadInternalTemplate(EInternalTemplates.IMPLEMENTATIONS.getTemplateName());

      //cycle across loaded structure
      for (Entry<ApiKey, Api> workItem : workItems.entrySet())
      {
        //save loaded resource template into a temporary string
        String        temp    = template.toString();
        StringBuilder methods = new StringBuilder("\n");
        Api           filler  = workItem.getValue();

        //fill template
        temp = temp.replaceAll("<packagePrefix>",       filler.getPackagePrefix());
        temp = temp.replaceAll("<parentPackagePrefix>", filler.getPackagePrefix().substring(0, filler.getPackagePrefix().lastIndexOf(".")));
        temp = temp.replaceAll("<component>",           filler.getComponent());
        temp = temp.replaceAll("<entity>",              filler.getEntity());
        temp = temp.replaceAll("<object>",              filler.getObject());
        temp = temp.replaceAll("<objectIdPrefix>",      filler.getObjectId());

        //cycle API interface methods to map
        for (ExposureType exposure : filler.getExposures())
        {
          //composes API methods implementation
          methods.append(EMethod.fromType(exposure.getMethod()).getMethodImplementationHeader(exposure, 
                                                                                              filler.getObject()))
                 .append(EParamType.getImplementationParameters(meltParamLists(exposure)));
          //replace last inserted comma with method signature closure
          methods = methods.replace(methods.lastIndexOf(","), methods.length(), ")\n")
                           .append("\n");
          //insert method body
          methods.append(getMethodBody(exposure.getName(),
                                       temp))
                 .append("\n");
        }

        //placeholder substitution for mapped implementation methods 
        temp = temp.replace("<implementationContent>", methods);
        //clean methods body template section
        temp = temp.substring(0, temp.indexOf("<bodies>"))
                   .trim();
        //tries to avoid code collection errors for entities that end with 'y' and replace 'ys()' suffix with 'ies()' suffix
        temp = temp.replaceAll("ys\\(", "ies(");

        
        try
        {
          //properly composes out file destination
          String filePath = new StringBuilder(Assorted.checkNullString(this.methodSettings.getOutFolder(), true))
                                      .append(File.separatorChar)
                                      .append(filler.getAbsolutePath().substring(filler.getAbsolutePath().indexOf("src" + File.separatorChar),
                                                                                 filler.getAbsolutePath().indexOf(filler.getEntity() + File.separatorChar) + filler.getEntity().length() + 1))
                                      .append(INTERFACE_RESTFUL_PACKAGE + File.separatorChar)
                                      .append(filler.getObject())
                                      .append("ServiceImpl.java")
                                      .toString();
          //write interface file
          Assorted.writeFile(filePath,
                             temp.getBytes());
          
          //log created file 
          System.out.println("  Implementation successfully created into [" + filePath + "]");
        }
        catch ( Exception ex )
        {
          ex.printStackTrace();
        }

//      //log composed implementation
//      System.out.println(temp + "\n\n");
      }
    }
    catch ( TemplaterException ex )
    {
      //throw back exception
      throw ex;
    }
  }

  /**
   * Load internal template
   * 
   * @param resourceFile
   * @throws TemplaterException
   * @author Fabrizio Parlani
   */
  private void loadInternalTemplate(String resourceFile)
  throws TemplaterException
  {
    //get current class loader
    //ClassLoader classLoader = getClass().getClassLoader();

    //load template
    File        resource    = new File(Runner.class.getResource(resourceFile).getFile());

    //try to load resource
    try (Scanner scanner = new Scanner(resource)) 
    {
      //reset working template
      template = new StringBuilder("");

      //cycle loaded file
      while (scanner.hasNextLine()) 
      {
        //fill template
        template.append(scanner.nextLine()).append("\n");
      }

      //close opened resource file
      scanner.close();
    }
    catch ( IOException ex )
    {
      //throw custom exception
      throw new TemplaterException("TEX-014", "Error during internal template loading [" + resourceFile + "]. Following I/O Exception occurred [" + ex.getMessage() + "]");
    }
  }

  /**
   * Composes service path parameters exposure
   * 
   * @param parameters
   * @return
   * @author Fabrizio Parlani
   */
  private String buildServicePath(List<ParamType> parameters)
  {
    //
    StringBuilder servicePath = new StringBuilder("");
    
    //check for provided parameters
    if (Assorted.isNotEmpty(parameters))
    {
      //composes service path exposure
      servicePath.append("  @Path(\"/");
      
      //cycle parameters
      for (ParamType parameter : parameters)
      {
        servicePath.append("{").append(parameter.getName()).append("}/");
      }
      
      servicePath.replace(0, servicePath.length(), servicePath.substring(0, servicePath.length() - 1)) ;
      
      servicePath.append("\")\n");
    }
    
    return servicePath.toString();
  }

  /**
   * Melts Header, Path and Query parameters list into a single list
   * 
   * @param exposure
   * @return
   * @author Fabrizio Parlani
   */
  private List<ParamType> meltParamLists(ExposureType exposure)
  {
    //declare returning object
    List<ParamType> parameters = new ArrayList<ParamType>(0);

    //check for provided object
    if (exposure != null)
    {
      //check for existing header parameters list
      if (Assorted.isNotEmpty(exposure.getHeaderParams()))
      {
        //add existing list to returning collection
        parameters.addAll(exposure.getHeaderParams());
      }
      //check for existing path parameters list
      if (Assorted.isNotEmpty(exposure.getPathParams()))
      {
        //add existing list to returning collection
        parameters.addAll(exposure.getPathParams());
      }
      //check for existing query parameters list
      if (Assorted.isNotEmpty(exposure.getQueryParams()))
      {
        //add existing list to returning collection
        parameters.addAll(exposure.getQueryParams());
      }
    }

    //returns composed collection or an empty one if no parameters have been found
    return parameters;
  }

  /**
   * Gets method body from provided template stripping from provided method name
   * 
   * @param methodName
   * @param implementationTemplate
   * @return
   * @author Fabrizio Parlani
   */
  private String getMethodBody(String methodName,
                               String implementationTemplate)
  {
    //declare returning object
    String methodBody = new String("");
    try
    {
      //strip proper method body from provided template
      methodBody = (new RangeHelper(new StringBuilder("<").append(methodName).append(">").toString(),
                                    new StringBuilder("</").append(methodName).append(">").toString()))
                          .getRange(implementationTemplate,  
                                    false, 
                                    false);
    }
    catch ( Exception ex )
    {
      // TODO : Add a warning list
      System.out.println("  Error during template method body stripping for method [" + methodName + "]. Occurred Exception is : [" + ex.getMessage() + "]");
    }

    //return stripped method body or empty string if method body doesn't exist or an exception has occurred
    return methodBody;
  }

  /**
   * Private class used to filter proper file by name
   * 
   * @author Fabrizio Parlani
   */
  private class ServiceFileFilter implements FilenameFilter
  {
    private String filter;
    
    /**
     * Class constructor
     */
    public ServiceFileFilter(String fileSuffix)
    {
      filter = fileSuffix;
    }

    /* (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File   dir, 
                          String name)
    {
      //applies filter on cycled file
      return name.endsWith(filter);
    }
  }

  /**
   * Strip a substring from a content
   * 
   * @author Fabrizio Parlani
   */
  private class RangeHelper
  {
    private int    startIndex;
    private int    endIndex;
    private int    offset;
    private String startBlock;
    private String endBlock;
    
    /**
     * Class constructor
     * 
     * @param startBlock
     * @param endBlock
     */
    public RangeHelper(String startBlock,
                       String endBlock)
    {
      this.startBlock = startBlock;
      this.endBlock   = endBlock;
    }

    /**
     * 
     * @param content
     * @param lastStartBlockSearch
     * @param lastEndBlockSearch
     */
    public String getRange(String  content,
                           boolean lastStartBlockSearch,
                           boolean lastEndBlockSearch)
    throws Exception
    {
      //calculate indexes
      calculateIndexes(content, 
                       lastStartBlockSearch, 
                       lastEndBlockSearch);

      //check for valid block to retrieve
      if ((startIndex >= 0) &&
          (endIndex >= 0)   &&
          (offset >= 0))
      {
        try
        {
          //return stripped Range
          return content.substring(startIndex, 
                                   endIndex);
        }
        catch ( IndexOutOfBoundsException ex )
        {
          //throws custom exception
          throw new Exception("Unable to get range due to an Index Out Of Bound Exception [" + ex.getMessage() + "]");
        }
      }
      else
      {
        //throws custom exception
        throw new Exception("Unable to strip range");
      }
    }

    /**
     * Calculate cut indexes
     * 
     * @param content
     * @param lastStartBlockSearch
     * @param lastEndBlockSearch
     */
    private void calculateIndexes(String  content,
                                  boolean lastStartBlockSearch,
                                  boolean lastEndBlockSearch)
    {
      //check for provided string
      if (Assorted.isNotEmpty(content, true))
      {
        //calculate start index
        this.startIndex = calculateIndex(content,
                                         startBlock,
                                         lastStartBlockSearch,
                                         true);
        //calculate end index
        this.endIndex   = calculateIndex(content,
                                         endBlock,
                                         lastEndBlockSearch,
                                         false);
        //calculate offset
        this.offset     = endIndex - startIndex;
      }
    }

    /**
     * Calculate index
     * 
     * @param content
     * @param block
     * @param lastOccurence
     * @return
     */
    private int calculateIndex(String  content,
                               String  block,
                               boolean lastOccurence,
                               boolean addBlockLength)
    {
      //declare returning index
      int index;
      
      if (Assorted.isNotEmpty(block))
      {
        //get probably retrieved block
        index = (!lastOccurence) ? content.indexOf(block)
                                 : content.lastIndexOf(block);
        //check for retrieved block
        if ((index != -1) && (addBlockLength))
        {
          //calculate index adding block length
          index += block.length();
        }
      }
      else
      {
        //set invalid index
        index = -2;
      }

      //return index
      return index;
    }
  }

  /**
   * 
   * @author Fabrizio Parlani
   */
  private class ApiKey
  {
    String apiBasePath;
    String apiContent;

    /**
     * Class constructor
     * 
     * @param apiBasePath
     * @param apiContent
     */
    public ApiKey(String apiBasePath,
                  String apiContent)
    {
      this.apiBasePath = apiBasePath;
      this.apiContent  = apiContent;
    }

    /**
     * @return the apiBasePath
     */
    public String getApiBasePath()
    {
      return apiBasePath;
    }

    /**
     * @return the apiContent
     */
    public String getApiContent()
    {
      return apiContent;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((apiContent == null)  ? 0 : apiContent.length());
      result = prime * result + ((apiBasePath == null) ? 0 : apiContent.length() % apiBasePath.length());
      return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
      if ((obj != null) && (obj instanceof ApiKey))
      {
        if (((ApiKey)obj).getApiBasePath() != null)
        {
          return ((ApiKey)obj).getApiBasePath().equalsIgnoreCase(this.getApiBasePath());
        }
        else
        {
          return (this.getApiBasePath() == null);
        }
      }
      
      return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("ApiKey [apiBasePath=");
      builder.append(apiBasePath);
      builder.append(", apiContent=");
      builder.append(apiContent);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * 
   * @author Fabrizio Parlani
   */
  private class ParamKey
  {
    String entityName;
    String methodName;

    /**
     * Class constructor
     * 
     * @param entityName
     * @param methodName
     */
    public ParamKey(String entityName,
                    String methodName)
    {
      this.entityName = entityName;
      this.methodName = methodName;
    }

    /**
     * @return the entityName
     */
    public String getEntityName()
    {
      return entityName;
    }

    /**
     * @return the methodName
     */
    public String getMethodName()
    {
      return methodName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((entityName == null) ? 0 : entityName.hashCode());
      result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
      return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
      if ((obj != null) && (obj instanceof ParamKey))
      {
        if ((((ParamKey)obj).getEntityName() != null) && 
            (((ParamKey)obj).getMethodName() != null))
        {
          return (((ParamKey)obj).getEntityName().equalsIgnoreCase(this.getEntityName()) && 
                  ((ParamKey)obj).getMethodName().equalsIgnoreCase(this.getMethodName()));
        }
        else
        {
          if (((ParamKey)obj).getEntityName() == null)
          {
            return ((this.getEntityName() == null) && 
                    ((ParamKey)obj).getMethodName().equalsIgnoreCase(this.getMethodName()));
          }
          else
          {
            if (((ParamKey)obj).getMethodName() == null)
            {
              return ((this.getMethodName() == null) && 
                      ((ParamKey)obj).getEntityName().equalsIgnoreCase(this.getEntityName()));
            }
            else
            {
              return ((this.getEntityName() == null) && 
                      (this.getMethodName() == null));
            }
          }
        }
      }

      return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("ParamKey [entityName=");
      builder.append(entityName);
      builder.append(", methodName=");
      builder.append(methodName);
      builder.append("]");
      return builder.toString();
    }
  }
  
  public static void main(String... params)
  {
    Runner runner = new Runner("C:/Develop/Accenture/DCPP-MMS/J2EE/git/crsm/crsm-up-client/src/main/java/com/accenture/cpaas/dcpp/enabler/crsm/exposure/up/service",
                               "Service.java",
                               "C:/Develop/Accenture/DCPP-MMS/J2EE/git/templater/MethodSettings.json");
    
    
    runner.run();
    
    
  }
}
