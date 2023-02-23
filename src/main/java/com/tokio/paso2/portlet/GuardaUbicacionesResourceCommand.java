package com.tokio.paso2.portlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.cotizadorModular.Bean.InfoCotizacion;
import com.tokio.cotizadorModular.Bean.UbicacionesResponse;
import com.tokio.cotizadorModular.Enum.TipoCotizacion;
import com.tokio.cotizadorModular.Exception.CotizadorModularException;
import com.tokio.cotizadorModular.Interface.CotizadorPaso2;
import com.tokio.cotizadorModular.Util.CotizadorModularUtil;
import com.tokio.paso2.constants.CotizadorPaso2PortletKeys;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = {
		"javax.portlet.name=" + CotizadorPaso2PortletKeys.PORTLET_NAME,
		"mvc.command.name=/cotizadores/guardaubicaciones", }, service = MVCResourceCommand.class)

public class GuardaUbicacionesResourceCommand extends BaseMVCResourceCommand {

	@Reference
	CotizadorPaso2 _CMServicesP2;

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

		String listaUbicaciones = ParamUtil.getString(resourceRequest, "listaUbicaciones");
		String infoCotizacionString = ParamUtil.getString(resourceRequest, "infCotString");

		InfoCotizacion infCotizacion = gson.fromJson(infoCotizacionString, InfoCotizacion.class);
		JsonObject objLisUbi = gson.fromJson(listaUbicaciones, JsonObject.class);

		guardaUbicaciones(resourceRequest, infCotizacion, objLisUbi, resourceResponse);

		resourceRequest.setAttribute("infoCotizacionString", infoCotizacionString);

	}

	private void guardaUbicaciones(ResourceRequest resourceRequest, InfoCotizacion infCotizacion,
			JsonObject objLisUbi, ResourceResponse resourceResponse) {

		HttpServletRequest originalRequest = PortalUtil
				.getOriginalServletRequest(PortalUtil.getHttpServletRequest(resourceRequest));
		User user = (User) resourceRequest.getAttribute(WebKeys.USER);
		int idPerfilUser = (int) originalRequest.getSession().getAttribute("idPerfil");

		objLisUbi.addProperty("p_usuario", user.getScreenName());
		objLisUbi.addProperty("idPerfil", idPerfilUser);
		objLisUbi.addProperty("p_pantalla", infCotizacion.getPantalla());

		try {
			if (infCotizacion.getTipoCotizacion().equals(TipoCotizacion.EMPRESARIAL)) {
				guardaEmpresarial(resourceRequest, objLisUbi, resourceResponse);
			} else if (infCotizacion.getTipoCotizacion().equals(TipoCotizacion.FAMILIAR)) {
				guardaFamiliar(resourceRequest, objLisUbi, resourceResponse);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void guardaEmpresarial(ResourceRequest resourceRequest, JsonObject objLisUbi,
			ResourceResponse resourceResponse) throws IOException {
		JsonObject respuesta = new JsonObject();

		PrintWriter writer = resourceResponse.getWriter();

		try {
			UbicacionesResponse ubicacionResponse = _CMServicesP2
					.guardaUbicacionEmpresarial(objLisUbi);

			respuesta.addProperty("code", ubicacionResponse.getCode());
			respuesta.addProperty("msg", ubicacionResponse.getMsg());
			final PortletSession psession = resourceRequest.getPortletSession();

			if (ubicacionResponse.getCode() == 0) {

				System.out.println("entre en codigo 0");

				String ubicacionString = CotizadorModularUtil.objtoJson(ubicacionResponse);
				String auxNombre = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() + "_C="
						+ ubicacionResponse.getCotizacion() + "_V=" + ubicacionResponse.getVersion()
						+ "_UBICACIONRESPONSE";

				String auxNombre2 = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() + "_C="
						+ ubicacionResponse.getCotizacion() + "_V=" + ubicacionResponse.getVersion()
						+ "_EXCEDELIMITES";

				psession.setAttribute(auxNombre2, 0, PortletSession.APPLICATION_SCOPE);
				psession.setAttribute(auxNombre, ubicacionString, PortletSession.APPLICATION_SCOPE);

			}

			if (ubicacionResponse.getCode() == 5) {

				System.out.println("entre en codigo 5");

				String ubicacionString = CotizadorModularUtil.objtoJson(ubicacionResponse);
				String auxNombre = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() + "_C="
						+ ubicacionResponse.getCotizacion() + "_V=" + ubicacionResponse.getVersion()
						+ "_UBICACIONRESPONSE";

				String auxNombre2 = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() + "_C="
						+ ubicacionResponse.getCotizacion() + "_V=" + ubicacionResponse.getVersion()
						+ "_EXCEDELIMITES";

				psession.setAttribute(auxNombre2, 1, PortletSession.APPLICATION_SCOPE);
				psession.setAttribute(auxNombre, ubicacionString, PortletSession.APPLICATION_SCOPE);

				System.out.println("los nombres de variables son:");
				System.out.println(auxNombre);
				System.out.println(auxNombre2);
			}
			writer.write(respuesta.toString());
		} catch (CotizadorModularException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			respuesta.addProperty("code", 2);
			respuesta.addProperty("msg", "Error al consultar su información");
			writer.write(respuesta.toString());
		}
	}

	private void guardaFamiliar(ResourceRequest resourceRequest, JsonObject objLisUbi,
			ResourceResponse resourceResponse) throws IOException {
		JsonObject respuesta = new JsonObject();
		PrintWriter writer = resourceResponse.getWriter();
		try {

			System.out.println("---------------->aqui");
			UbicacionesResponse ubicacionResponse = _CMServicesP2
					.guardaUbicacionFamiliar(objLisUbi);

			respuesta.addProperty("code", ubicacionResponse.getCode());
			respuesta.addProperty("msg", ubicacionResponse.getMsg());

			System.out.println(ubicacionResponse.toString());
			final PortletSession psession = resourceRequest.getPortletSession();
			if (ubicacionResponse.getCode() == 0) {


				String ubicacionString = CotizadorModularUtil.objtoJson(ubicacionResponse);
				String auxNombre = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() + "_C="
						+ ubicacionResponse.getCotizacion() + "_V=" + ubicacionResponse.getVersion()
						+ "_UBICACIONRESPONSE";

				System.out.println(".........................>2");
				System.out.println(ubicacionString);
				psession.setAttribute(auxNombre, ubicacionString, PortletSession.APPLICATION_SCOPE);

			}
			if (ubicacionResponse.getCode() == 5) {

				System.out.println("entre en codigo 5");

				String ubicacionString = CotizadorModularUtil.objtoJson(ubicacionResponse);
				String auxNombre = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() + "_C="
						+ ubicacionResponse.getCotizacion() + "_V=" + ubicacionResponse.getVersion()
						+ "_UBICACIONRESPONSE";

				String auxNombre2 = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() + "_C="
						+ ubicacionResponse.getCotizacion() + "_V=" + ubicacionResponse.getVersion()
						+ "_EXCEDELIMITES";

				psession.setAttribute(auxNombre2, 1, PortletSession.APPLICATION_SCOPE);
				psession.setAttribute(auxNombre, ubicacionString, PortletSession.APPLICATION_SCOPE);

				System.out.println("los nombres de variables son:");
				System.out.println(auxNombre);
				System.out.println(auxNombre2);
			}
			writer.write(respuesta.toString());
		} catch (CotizadorModularException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			respuesta.addProperty("code", 2);
			respuesta.addProperty("msg", "Error al consultar su información");
			writer.write(respuesta.toString());
		}
	}

}
