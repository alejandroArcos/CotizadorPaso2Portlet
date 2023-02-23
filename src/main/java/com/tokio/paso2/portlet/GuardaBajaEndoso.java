/**
 * 
 */
package com.tokio.paso2.portlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.cotizadorModular.Bean.InfoCotizacion;
import com.tokio.cotizadorModular.Bean.InfoCotizacionOrigen;
import com.tokio.cotizadorModular.Bean.SimpleResponse;
import com.tokio.cotizadorModular.Interface.CotizadorGenerico;
import com.tokio.paso2.constants.CotizadorPaso2PortletKeys;

import java.io.PrintWriter;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author jonathanfviverosmoreno
 *
 */

@Component(immediate = true, property = {
		"javax.portlet.name=" + CotizadorPaso2PortletKeys.PORTLET_NAME,
		"mvc.command.name=/cotizadores/paso2/guardaBajaEndoso", }, service = MVCResourceCommand.class)


public class GuardaBajaEndoso extends BaseMVCResourceCommand {
	
	@Reference
	CotizadorGenerico _CMServicesGen;


	/* (non-Javadoc)
	 * @see com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand#doServeResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
	 */
	@Override
	protected void doServeResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws Exception {
		// TODO Auto-generated method stub
		/************************** Validación metodo post **************************/
		if ( !resourceRequest.getMethod().equals("POST")  ){
			JsonObject requestError = new JsonObject();
			requestError.addProperty("code", 500);
			requestError.addProperty("msg", "Error en tipo de consulta");
			PrintWriter writer = resourceResponse.getWriter();
			writer.write(requestError.toString());
			return;
		}
		/************************** Validación metodo post **************************/
		
		Gson gson = new Gson();
		
		PrintWriter writer = resourceResponse.getWriter();

		String listaUbicaciones = ParamUtil.getString(resourceRequest, "listaUbicaciones");
		String infoCotizacionString = ParamUtil.getString(resourceRequest, "infCotString");
		User user = (User) resourceRequest.getAttribute(WebKeys.USER);
		
		InfoCotizacion infCotizacion = gson.fromJson(infoCotizacionString, InfoCotizacion.class);

		System.out.println(infCotizacion.getCotizacion());
		System.out.println(infCotizacion.getVersion());
		System.out.println(user.getScreenName());
		System.out.println(infCotizacion.getPantalla());
		
		InfoCotizacionOrigen cotOrigen = _CMServicesGen.GetOrigenEndoso(infCotizacion.getCotizacion(), 
				infCotizacion.getVersion(), user.getScreenName(),  infCotizacion.getPantalla());
		
		SimpleResponse respuesta = _CMServicesGen.guardarCotizacionEndosoBaja(infCotizacion.getCotizacion(),
				infCotizacion.getVersion(), listaUbicaciones, 2, 0, 0,
				user.getScreenName() , infCotizacion.getPantalla(),  cotOrigen.getP_cotizacion_origen(), cotOrigen.getP_version_origen());
		
		respuesta.setFolio(infCotizacion.getFolio()+ "");
	//	generaGlobal(respuesta, resourceRequest);
		writer.write(gson.toJson(respuesta));
	}
	
	private void generaGlobal(SimpleResponse ubicacionResponse, ResourceRequest resourceRequest){
		final PortletSession psession = resourceRequest.getPortletSession();
		String paso1 = ParamUtil.getString(resourceRequest, "infP1");
		String nombreDatosGenerales = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() +
				"_C=" + ubicacionResponse.getCotizacion() +
				"_V=" + ubicacionResponse.getVersion() +
				"_DATOSP1";
		psession.setAttribute(nombreDatosGenerales, paso1, PortletSession.APPLICATION_SCOPE);
	}
	
	

}
