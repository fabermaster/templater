package fabermaster.utils.templater.restful;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fabermaster.utils.templater.exception.TemplaterException;
import fabermaster.utils.templater.helper.Assorted;
import fabermaster.utils.templater.model.restful.Api;
import fabermaster.utils.templater.model.restful.ExposureType;
import fabermaster.utils.templater.model.restful.ISettings.EFacadeMethod;
import fabermaster.utils.templater.model.restful.ISettings.EMediaType;

public class Runner
{
  //declare constants
  private static final String INTERFACE_PACKAGE         = "ia";
  private static final String INTERFACE_RESTFUL_PACKAGE = "restful";

  //declare used object(s)
  private Map<String, String> interfaces = new LinkedHashMap<String, String>(0);
  
  private Api                 structure  = null;
  private Map<ApiKey, Api>    workItems  = new LinkedHashMap<ApiKey, Api>(0);


  //declare class attribute(s)
  private String basePath;
  private String fileNameFilter;

  /**
   * Class Constructor
   * 
   */
  public Runner(String basePath,
                String fileNameFilter)
  {
    this.basePath       = basePath;
    this.fileNameFilter = fileNameFilter;
  }

  public void run()
  {
    try
    {
      //gets interfaces to inspect
      loadInterfaces(inspectBasePath());
      //some magic
      inspectInterfaces();
    }
    catch ( TemplaterException ex )
    {
      System.err.println("Error   : [" + ex.getCode() + "]\nMessage : [" + ex.getMessage() +"]");
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

        //log structure
        System.out.println(" - Structure   [" + structure + "]");
      }
      catch ( IOException ex )
      {
        System.err.println("Unable to get file content from [" + entry.getValue()  + "] due to following I/O exception [" + ex.getMessage() + "]");
      }
      catch ( Exception ex )
      {
        System.err.println("Unable to strip object name from [" + entry.getValue()  + "] due to following exception [" + ex.getMessage() + "]");
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
   * @return
   */
  private List<ExposureType> fillExposure(Api structure)
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

      //set path parameters
      
      
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
  }

  public static void main(String... params)
  {
    Runner runner = new Runner("C:/Develop/Accenture/DCPP-MMS/J2EE/git/crsm/crsm-up-client/src/main/java/com/accenture/cpaas/dcpp/enabler/crsm/exposure/up/service",
                               "Service.java");
    
    
    runner.run();
    
    
  }
}
