package be.rubus.microstream.demo.cluster.app.client.controller;

import be.rubus.microstream.demo.cluster.app.client.service.ProductService;
import be.rubus.microstream.demo.cluster.app.model.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/product")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    private ProductService productService;

    @GET
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @POST
    public Response addProduct(Product product, @Context UriInfo uriInfo) {
        boolean success = productService.addProduct(product);
        if (success) {
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(product.getId());
            return Response.created(uriBuilder.build()).entity(product).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }
    }

    @PUT
    @Path("/{productId}")
    public Response updateProduct(Product product, @PathParam("productId") String productId) {
        product.setId(productId);
        boolean success = productService.updateProduct(product);
        if (success) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{productId}")
    public Response deleteProduct(@PathParam("productId") String productId) {
        productService.deleteProduct(productId);
        return Response.ok().build();
    }
}
