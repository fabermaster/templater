package <packagePrefix>.<component>.service.<entity>.restful.ia;

import java.math.BigInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import <packagePrefix>.<component>.data.<entity>.Create<object>RequestType;
import <packagePrefix>.<component>.data.<entity>.Retrieve<object>DetailsResponseType;
import <packagePrefix>.<component>.data.<entity>.Update<object>RequestType;
import <packagePrefix>.<component>.data.<entity>.Update<object>ResponseType;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/")
public interface <object>Service
{
  <interfaceContent>
}
