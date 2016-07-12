package <packagePrefix>.<component>.service.<entity>.restful;

import java.math.BigInteger;

import javax.ws.rs.core.Response;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import <parentPackagePrefix>.api.utils.CommonUtils;
import <parentPackagePrefix>.api.utils.RESTOperationResult;
import <parentPackagePrefix>.api.utils.RESTOperationResult.ECRUDType;
import <packagePrefix>.<component>.data.<entity>.<object>AttributeListRetrieveType;
import <packagePrefix>.<component>.data.<entity>.<object>UpdateObjectType;
import <packagePrefix>.<component>.data.<entity>.Create<object>RequestType;
import <packagePrefix>.<component>.data.<entity>.Retrieve<object>DetailsRequestType;
import <packagePrefix>.<component>.data.<entity>.Retrieve<object>DetailsResponseType;
import <packagePrefix>.<component>.data.<entity>.Retrieve<object>RequestType;
import <packagePrefix>.<component>.data.<entity>.Retrieve<object>ResponseType;
import <packagePrefix>.<component>.data.<entity>.Update<object>RequestType;
import <packagePrefix>.<component>.service.ServiceResponseBuilder;
import <packagePrefix>.<component>.service.<entity>.ia.<object>Facade;
import <packagePrefix>.<component>.service.<entity>.restful.ia.<object>Service;

public class <object>ServiceImpl extends    ServiceResponseBuilder
                                     implements <object>Service
{
  //get logger instance  
  private static final Logger  LOGGER = LoggerFactory.getLogger(<object>ServiceImpl.class);

  //declare object that will be injected by blueprint 
  private <object>Facade   facade;

  /**
   * Gets the injected facade attribute value
   *
   * @return the facade
   * @author Fabrizio Parlani
   */
  public <object>Facade getFacade()
  {
    return facade;
  }

  /**
   * Sets the injected facade attribute value
   *
   * @param facade the facade to set
   * @author Fabrizio Parlani
   */
  public void setFacade(<object>Facade facade)
  {
    this.facade = facade;
  }

  <implementationContent>
  
}


<bodies>
  <insert>
    {
      LOGGER.debug("START - POST Method - [Creation of : <object>]");
      LOGGER.debug("        # Provided Request is : [{}]{}", ((request != null) ? "VALID" : "INVALID"), ((request == null) ? " a new one will be created" : ""));
      LOGGER.debug("        # Tenant              : [{}]", tenant);
  
      //declare request that wrap provided one
      Create<object>RequestType wrapper = (request != null) ? request : new Create<object>RequestType();
  
      //inject provided tenant
      wrapper.setTenant(tenant);
  
      //return properly created response for provided request
      return getResponseWithHttpCode(facade.create<object>(wrapper), 
                                     new RESTOperationResult(ECRUDType.CREATE, 
                                                             false));
    }
  </insert>
  
  <updateBulk>
    {
      LOGGER.debug("START - PUT Method - [Update of : <object>]");
      LOGGER.debug("        # Provided Request is : [{}]{}", ((request != null) ? "VALID" : "INVALID"), ((request == null) ? " a new one will be created" : ""));
      LOGGER.debug("        # Tenant              : [{}]", tenant);
  
      //declare request that wrap provided one
      Update<object>RequestType wrapper = (request != null) ? request : new Update<object>RequestType();
  
      //inject provided tenant
      wrapper.setTenant(tenant);
  
      //return properly created response for provided request
      return getResponseWithHttpCode(facade.update<object>(wrapper), 
                                     new RESTOperationResult(ECRUDType.UPDATE, 
                                                             false));
    }
  </updateBulk>
  
  <update>
    {
      LOGGER.debug("START - PUT Method - [Update of : <object>]");
      LOGGER.debug("        # Provided Request is : [{}]{}", ((request != null) ? "VALID" : "INVALID"), ((request == null) ? " a new one will be created" : ""));
      LOGGER.debug("        # Tenant              : [{}]", tenant);
      LOGGER.debug("        # Business User Id    : [{}]", businessUserId);
  
      //declare request that wrap provided one
      Update<object>RequestType wrapper     = (request != null) ? request : new Update<object>RequestType();
      //declare object used to create or to store single <entity> object to update
      <object>UpdateObjectType  firstEntity = null;
  
      //inject provided tenant
      wrapper.setTenant(tenant);
  
      //check for an already present <entity> list object
      if ((wrapper.get<object>UpdateList().get<object>s() != null) &&
          (!wrapper.get<object>UpdateList().get<object>s().isEmpty()))
      {
        //log operation
        LOGGER.debug("      - Saving existing first <entity> object and cleaning provided list");
        //save first provided business user object from business users list object
        firstEntity = wrapper.get<object>UpdateList().get<object>s().get(0);
        //clear provided list into request
        wrapper.get<object>UpdateList().get<object>s().clear();
        //overwrite saved object <entity> id
        firstEntity.set<object>Id(<objectIdPrefix>Id);
        
      }
      else
      {
        //log operation
        LOGGER.debug("      - Create business user object to add to list");
        //create a newly one business user entity
        firstEntity = new <object>UpdateObjectType();
        //overwrite saved object <entity> id
        firstEntity.set<object>Id(<objectIdPrefix>Id);
      }
  
      //attach worked or just created object to request <entity> list object
      wrapper.get<object>UpdateList().get<object>s().add(firstEntity);
  
      //return properly created response for provided request
      return getResponseWithHttpCode(facade.update<object>(wrapper), 
                                     new RESTOperationResult(ECRUDType.UPDATE, 
                                                             false));
    }
  </update>
  
  <retrieve>
    {
      LOGGER.debug("START - GET Method - [Retrieve of : <object> Detail]"); 
      LOGGER.debug("        # Transaction Id              : [{}]", transactionId);
      LOGGER.debug("        # Tenant                      : [{}]", tenant);
      LOGGER.debug("        # Business User Id            : [{}]", businessUserId);
      LOGGER.debug("        # Language Id                 : [{}]", languageId);
      LOGGER.debug("        # Retrieve Parents            : [{}]", parentsFlag);
      LOGGER.debug("        # Retrieve Business Children  : [{}]", businessChildrenFlag);
      LOGGER.debug("        # Retrieve Children           : [{}]", userChildrenFlag);
      LOGGER.debug("        # Retrieve Offerings          : [{}]", offeringFlag);
      LOGGER.debug("        # Retrieve Profile Attributes : [{}]", profileAttributeFlag);
  
      //create request object
      Retrieve<object>DetailsRequestType request  = new Retrieve<object>DetailsRequestType();
  
      //set request provided attributes
      request.setTransactionId(transactionId);
      request.setTenant(tenant);
  
      //check for a provided <entity> id
      if (<objectIdPrefix>Id != null)
      {
        //set <entity> id list into request object
        request.get<object>Ids().add(<objectIdPrefix>Id);
      }
      
      //set query string provided attributes 
      request.setLanguageId(languageId);
      request.setParentsFlag         (parentsFlag != null          ? parentsFlag.booleanValue() 
                                                                   : false);
      request.setBusinessChildrenFlag(businessChildrenFlag != null ? businessChildrenFlag.booleanValue() 
                                                                   : false);
      request.setUserChildrenFlag    (userChildrenFlag != null     ? userChildrenFlag.booleanValue() 
                                                                   : false);
      request.setOfferingFlag        (offeringFlag != null         ? offeringFlag.booleanValue() 
                                                                   : false);
      request.setProfileAttributeFlag(profileAttributeFlag != null ? profileAttributeFlag.booleanValue() 
                                                                   : false);
      
      
      //Invoke retrieve service and get returned response object
      Retrieve<object>DetailsResponseType response = facade.retrieve<object>Details(request);
      
      //return properly created response for provided request
      return getResponseWithHttpCode(response, 
                                     (response.get<object>DetailsRetrievedList() != null) ? new RESTOperationResult(ECRUDType.RETRIEVE, 
                                                                                                                        response.get<object>DetailsRetrievedList().get<object>sDetails().isEmpty())
                                                                                              : new RESTOperationResult(ECRUDType.RETRIEVE, 
                                                                                                                        true));
    }
  </retrieve>
  
  <retrieveList>
    {
      LOGGER.debug("START - GET Method - [Retrieve of : <object> List]"); 
      LOGGER.debug("        # Transaction Id              : [{}]", transactionId);
      LOGGER.debug("        # Tenant                      : [{}]", tenant);
      LOGGER.debug("        # Name                        : [{}]", name);
      LOGGER.debug("        # Parent Business User Id     : [{}]", parentBusinessUserId);
      LOGGER.debug("        # Status Id                   : [{}]", statusId);
      LOGGER.debug("        # Profile Id                  : [{}]", profileId);
      LOGGER.debug("        # Offering Id                 : [{}]", offeringId);
      LOGGER.debug("        # Product Id                  : [{}]", productId);
      LOGGER.debug("        # Attributes                  : [{}]", attributes);
      LOGGER.debug("        # Items Per Page              : [{}]", itemsPerPage);
      LOGGER.debug("        # Page Number                 : [{}]", pageNumber);
  
      //create request object
      Retrieve<object>RequestType request  = new Retrieve<object>RequestType();
  
      //set request provided attributes
      request.setTransactionId(transactionId);
      request.setTenant(tenant);
  
      //check for a provided business user attribute(s) filter
      if ((attributes != null) && (!attributes.trim().isEmpty()))
      {
        //set business user id list into request object
        request.set<object>AttributeList(CommonUtils.fromJsonToObject(attributes, <object>AttributeListRetrieveType.class));
      }
  
      //set query string provided attributes 
      request.setParentBusinessUserId(parentBusinessUserId);
      request.setStatusId(statusId);
      request.setProfileId(profileId);
      request.setOfferingId(offeringId);
      request.setProductId(productId);
      request.setItemsPerPage(itemsPerPage);
      request.setPageNumber(pageNumber);
      
      //Invoke retrieve service and get returned response object
      Retrieve<object>ResponseType response = facade.retrieve<object>(request);
  
      //return properly created response for provided request
      return getResponseWithHttpCode(response, 
                                     (response.get<object>RetrievedList() != null) ? new RESTOperationResult(ECRUDType.RETRIEVE, 
                                                                                                                 response.get<object>RetrievedList().get<object>s().isEmpty())
                                                                                       : new RESTOperationResult(ECRUDType.RETRIEVE, 
                                                                                                                 true));
    }
  </retrieveList>
</bodies>