package daggerok.facebook;

import daggerok.facebook.axon.Axon;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.config.Configuration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;

@Slf4j
@Stateless
@Path("facebook")
@Produces(APPLICATION_JSON)
public class FacebookCommandsResource {

  @Inject @Axon Configuration axon;

  @POST
  @Path("create")
  public Response index(final Map<String, String> id,
                        @Context final UriInfo uriInfo) {

    final String postId = id.getOrDefault("postId", UUID.randomUUID().toString());

    axon.commandGateway().send(new CreatePostCommand(postId), LoggingCallback.INSTANCE);
/*
    final BiFunction<String, String, String> uri = (final String s1, final String s2) ->
        uriInfo.getBaseUriBuilder()
               .path(FacebookCommandsResource.class)
               .path("/{postId}/{operation}")
               .build(s1, s2)
               .toString();
*/
    final BiFunction<String, String, String> uri = (final String s1, final String s2) ->
        uriInfo.getBaseUriBuilder()
               .path(FacebookCommandsResource.class)
               .path(FacebookCommandsResource.class, s2)
               .build(s1)
               .toString();

    return Response.ok(Json.createObjectBuilder()
                           .add("love it HTTP PUT", uri.apply(postId, "like"))
                           .add("hate it HTTP PUT", uri.apply(postId, "dislike"))
                           .build())
                   .build();
  }

  @PUT
  @Path("{postId}/like")
  public void like(@PathParam("postId") String postId) {
    axon.commandBus().dispatch(asCommandMessage(new LikePostCommand(postId)), LoggingCallback.INSTANCE);
  }

  @PUT
  @Path("{postId}/dislike")
  public void dislike(@PathParam("postId") String postId) {
    axon.commandBus().dispatch(asCommandMessage(new DislikePostCommand(postId)), LoggingCallback.INSTANCE);
  }
}
