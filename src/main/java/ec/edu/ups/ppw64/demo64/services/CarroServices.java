package ec.edu.ups.ppw64.demo64.services;

import java.util.List;

import config.ConfigJaeger;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import ec.edu.ups.ppw64.demo64.business.GestionCarros;
import ec.edu.ups.ppw64.demo64.model.Carro;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("carros")
public class CarroServices {
	
	private final Tracer tracer = GlobalTracer.get();
	
	@Inject
	private GestionCarros gCarros;
	
	@Inject
	private ConfigJaeger configjaeger;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response guardarCarro(Carro carro) {
		Span spanGuardarCarro = tracer.buildSpan("Creacion carros").start();
		System.out.println(carro);
		try{
			gCarros.guardarCarros(carro);
			ErrorMessage error = new ErrorMessage(1, "OK");
			return Response.status(Response.Status.CREATED)
					.entity(error)
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			ErrorMessage error = new ErrorMessage(99, e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(error)
					.build();
		} finally {
			spanGuardarCarro.finish();
		}
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response actualizarCarro(Carro carro) {
		Span spanActualizarCarro = tracer.buildSpan("Actualizacion de carros").start();
		try{
			gCarros.actualizarCarro(carro);
			return Response.ok(carro).build();
		}catch (Exception e) {
			// TODO: handle exception
			ErrorMessage error = new ErrorMessage(99, e.getMessage());
			return Response.status(Response.Status.NOT_FOUND)
					.entity(error)
					.build();
		}finally {
			spanActualizarCarro.finish();
		}
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response eliminarCarro(@QueryParam("placa") String placa) {
		Span spanEliminarCarro = tracer.buildSpan("Eliminar carros").start();
		try{
			this.gCarros.borrarCarro(placa);
			ErrorMessage error = new ErrorMessage(1, "OK");
			return Response.status(Response.Status.CREATED)
					.entity(error)
					.build();
		}catch (Exception e) {
			ErrorMessage error = new ErrorMessage(99, e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(error)
					.build();
		} finally {
			spanEliminarCarro.finish();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCarroPorCodigo(@QueryParam("placa") String placa) {
		Span spanGetCarroPorCodigo = tracer.buildSpan("Carro por codigo").start();
		try{
			System.out.println("cedula " +  placa);
			Carro carro = gCarros.getCarroPorPlaca(placa);
			return Response.ok(carro).build();
		}catch (Exception e) {
			// TODO: handle exception
			ErrorMessage error = new ErrorMessage(4, "No hay carro");
			return Response.status(Response.Status.NOT_FOUND)
					.entity(error)
					.build();
		}finally {
			spanGetCarroPorCodigo.finish();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("list")
	public Response getCarros(){
		Span spanGetCarros = tracer.buildSpan("Obtener carros").start();
		
		try {
			List<Carro> carros = gCarros.getCarros();
			if(carros.size()>0) {
				return Response.ok(carros).build();
			} else {
				ErrorMessage error = new ErrorMessage(6, "No se registran carros");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(error)
						.build();
			}
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMessage error = new ErrorMessage(6, "No se registran carros");
			return Response.status(Response.Status.NOT_FOUND)
					.entity(error)
					.build();
		} finally {
			spanGetCarros.finish();
		}

	}

}
