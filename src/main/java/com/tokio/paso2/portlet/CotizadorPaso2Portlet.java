package com.tokio.paso2.portlet;

import com.google.gson.Gson;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.cotizador.jsonformservice.JsonFormService;
import com.tokio.cotizadorModular.Bean.CpData;
import com.tokio.cotizadorModular.Bean.CpResponse;
import com.tokio.cotizadorModular.Bean.InfoCotizacion;
import com.tokio.cotizadorModular.Bean.ListaRegistro;
import com.tokio.cotizadorModular.Bean.Ubicacion;
import com.tokio.cotizadorModular.Bean.UbicacionesResponse;
import com.tokio.cotizadorModular.Constants.CotizadorModularServiceKey;
import com.tokio.cotizadorModular.Enum.ModoCotizacion;
import com.tokio.cotizadorModular.Enum.TipoCotizacion;
import com.tokio.cotizadorModular.Interface.CotizadorGenerico;
import com.tokio.cotizadorModular.Interface.CotizadorPaso2;
import com.tokio.cotizadorModular.Util.CotizadorModularUtil;
import com.tokio.paso2.constants.CotizadorPaso2PortletKeys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author jonathanfviverosmoreno
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=CotizadorPaso2Portlet Portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + CotizadorPaso2PortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"com.liferay.portlet.private-request-attributes=false" }, service = Portlet.class)

public class CotizadorPaso2Portlet extends MVCPortlet {

	InfoCotizacion infCotizacion;
	User user;
	int idPerfilUser;

	@Reference
	CotizadorPaso2 _CMServicesP2;
	@Reference
	CotizadorGenerico _CMServicesGenerico;
	@Reference
	JsonFormService _JsonFormService;

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws PortletException, IOException {

		llenaInfCotizacion(renderRequest);
		recuperaInfoUbicaciones(renderRequest);
		fLlenaCatalogos(renderRequest);
		recuperaSubGiroRiesgo(renderRequest);
		recuperaInfoSuma(renderRequest);
		recuperaInfoPaso1(renderRequest);
		recuperaInfoBajaEndoso(renderRequest);

		String infCotJson = CotizadorModularUtil.objtoJson(infCotizacion);

		renderRequest.setAttribute("perfilSuscriptor", perfilSuscriptor());
		renderRequest.setAttribute("infCotJson", infCotJson);
		renderRequest.setAttribute("infCotizacion", infCotizacion);
		renderRequest.setAttribute("perfilMayorEjecutivo", perfilPermisosGeneral());
		super.render(renderRequest, renderResponse);
	}

	

	private void llenaInfCotizacion(RenderRequest renderRequest) {
		try {
			HttpServletRequest originalRequest = PortalUtil
					.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));

			user = (User) renderRequest.getAttribute(WebKeys.USER);
			idPerfilUser = (int) originalRequest.getSession().getAttribute("idPerfil");

			System.out.println("-------------------aqui-------------------------------");
			String inf = "";
			if (Validator.isNull(renderRequest.getAttribute("infoCotizacionString"))) {
				System.out.println("recupere de original");
				inf = originalRequest.getParameter("infoCotizacion");
				infCotizacion = CotizadorModularUtil.decodeURL(inf);
				System.out.println(inf);
			} else {
				System.out.println("recupere de render");
				Gson gson = new Gson();
				inf = (String) renderRequest.getAttribute("infoCotizacionString");
				infCotizacion = gson.fromJson(inf, InfoCotizacion.class);
				System.out.println(infCotizacion.toString());
				System.out.println(inf);
			}

			String nombreCotizador = "";

			System.err.println(infCotizacion.toString());

			switch (infCotizacion.getTipoCotizacion()) {
				case FAMILIAR:
					infCotizacion.setPantalla(CotizadorPaso2PortletKeys.PANTALLA_FAMILIAR);
					nombreCotizador = CotizadorPaso2PortletKeys.TITULO_FAMILIAR;
					break;
				case EMPRESARIAL:
					infCotizacion.setPantalla(CotizadorPaso2PortletKeys.PANTALLA_EMPRESARIAL);
					nombreCotizador = CotizadorPaso2PortletKeys.TITULO_EMPRESARIAL;
					break;
				default:
					infCotizacion.setPantalla("");
					nombreCotizador = "";
					break;
			}
			renderRequest.setAttribute("tituloCotizador", nombreCotizador);
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("------------------ llenaInfoCotizacion:");
			e.printStackTrace();
			SessionErrors.add(renderRequest, "errorConocido");
			renderRequest.setAttribute("errorMsg", "Error al cargar la cotización");
			SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
					+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		}
	}

	private void recuperaInfoUbicaciones(RenderRequest renderRequest) {
		try {
			final PortletSession psession = renderRequest.getPortletSession();
			Map<Integer, Integer> relacionUbicaciones = new HashMap<Integer, Integer>();

			Gson gson = new Gson();
			String auxNombre = "LIFERAY_SHARED_F=" + infCotizacion.getFolio() + "_C="
					+ infCotizacion.getCotizacion() + "_V=" + infCotizacion.getVersion()
					+ "_UBICACIONRESPONSE";

			System.out.println("auxNombre" + auxNombre);
			String ubicacion = (String) psession.getAttribute(auxNombre,
					PortletSession.APPLICATION_SCOPE);

			System.out.println(ubicacion);

			UbicacionesResponse ubi = gson.fromJson(ubicacion, UbicacionesResponse.class);

			if (Validator.isNotNull(ubi)) {

				infCotizacion.setNoUbicaciones(ubi.getUbicaciones().size());

				int auxCont = 1;
				for (Ubicacion u : ubi.getUbicaciones()) {
					relacionUbicaciones.put(auxCont, u.getIdubicacion());
					auxCont++;
				}

				generaAcordeones(renderRequest, ubi);

				generaListColonias(renderRequest, ubi);

				renderRequest.setAttribute("ubicacion", ubi);
				renderRequest.setAttribute("relacionUbicaciones", relacionUbicaciones);

			} else {
				infCotizacion.setNoUbicaciones(0);
				SessionErrors.add(renderRequest, "errorConocido");
				renderRequest.setAttribute("errorMsg", "Error al traer su informacion");
				SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
						+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("------------------ info ubicaciones:");
			e.printStackTrace();
			SessionErrors.add(renderRequest, "errorConocido");
			renderRequest.setAttribute("errorMsg", "Error al cargar las ubicaciones");
			SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
					+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		}
	}

	private void generaListColonias(RenderRequest renderRequest, UbicacionesResponse ubi) {
		// TODO Auto-generated method stub
		try {
			ArrayList<List<CpData>> colonias = new ArrayList<List<CpData>>();
			List<CpData> dataVacia = null;
			for (Ubicacion u : ubi.getUbicaciones()) {
				String cp = u.getCpData().getCp();
				if (Validator.isNotNull(cp)) {
					CpResponse cpdata = _CMServicesP2.getCP(cp, user.getScreenName(),
							infCotizacion.getPantalla());
					if (cpdata.getCode() == 0) {
						colonias.add(cpdata.getListaCpData());
					} else {
						colonias.add(dataVacia);
					}
				} else {
					colonias.add(dataVacia);
				}
			}
			System.out.println("colonias: " + colonias);
			renderRequest.setAttribute("colonias", colonias);
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("------------------ Colonias");
			e.printStackTrace();
			SessionErrors.add(renderRequest, "errorConocido");
			renderRequest.setAttribute("errorMsg", "Error al cargar las ubicaciones");
			SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
					+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		}
	}

	private void generaAcordeones(RenderRequest renderRequest, UbicacionesResponse ubi) {
		try {
			Gson gson = new Gson();
			Map<Integer, String> jsonFiels = new HashMap<Integer, String>();

			String dataProvide = gson.toJson(ubi.getDataProviders());
			int i = 1;
			for (Ubicacion ubicacion : ubi.getUbicaciones()) {

				String jsonformfields = "{\"fields\":" + ubicacion.getField() + "}";
				String jsonlayout = ubicacion.getLayouts();
				String jsonDataProviders = "{\"dataProviders\":" + dataProvide + "}";
				String u = ubicacion.getIdubicacion() + "";
				String htmlUbicacion = _JsonFormService.parse(jsonformfields, jsonlayout,
						jsonDataProviders, u);
				jsonFiels.put(i, htmlUbicacion);
				i++;
			}
			renderRequest.setAttribute("jsonFiels", jsonFiels);
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("------------------ generaAcordeones");
			e.printStackTrace();
			SessionErrors.add(renderRequest, "errorConocido");
			renderRequest.setAttribute("errorMsg", "Error al cargar las ubicaciones");
			SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
					+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		}

	}

	private void fLlenaCatalogos(RenderRequest renderRequest) {
		ListaRegistro listaNiveles = fGetCatalogos(CotizadorModularServiceKey.TMX_CTE_ROW_TODOS,
				CotizadorModularServiceKey.TMX_CTE_TRANSACCION_GET,
				CotizadorModularServiceKey.LIST_CAT_NIVELES,
				CotizadorModularServiceKey.TMX_CTE_CAT_ACTIVOS, user.getScreenName(),
				infCotizacion.getPantalla(), renderRequest);

		renderRequest.setAttribute("listaNiveles", listaNiveles.getLista());

		if (infCotizacion.getTipoCotizacion().equals(TipoCotizacion.FAMILIAR)) {
			ListaRegistro listaInmuebles = fGetCatalogos(
					CotizadorModularServiceKey.TMX_CTE_ROW_TODOS,
					CotizadorModularServiceKey.TMX_CTE_TRANSACCION_GET,
					CotizadorModularServiceKey.LIST_CAT_INMUEBLE,
					CotizadorModularServiceKey.TMX_CTE_CAT_ACTIVOS, user.getScreenName(),
					infCotizacion.getPantalla(), renderRequest);

			ListaRegistro listaUso = fGetCatalogos(CotizadorModularServiceKey.TMX_CTE_ROW_TODOS,
					CotizadorModularServiceKey.TMX_CTE_TRANSACCION_GET,
					CotizadorModularServiceKey.LIST_CAT_TPOUSO,
					CotizadorModularServiceKey.TMX_CTE_CAT_ACTIVOS, user.getScreenName(),
					infCotizacion.getPantalla(), renderRequest);

			renderRequest.setAttribute("listaInmuebles", listaInmuebles.getLista());
			renderRequest.setAttribute("listaUso", listaUso.getLista());
		}

	}

	private ListaRegistro fGetCatalogos(int p_rownum, String p_tiptransaccion, String p_codigo,
			int p_activo, String p_usuario, String p_pantalla, RenderRequest renderRequest) {
		try {
			ListaRegistro lr = _CMServicesGenerico.getCatalogo(p_rownum, p_tiptransaccion, p_codigo,
					p_activo, p_usuario, p_pantalla);

			// lr.getLista().sort(Comparator.comparing(Registro::getDescripcion));
			return lr;
		} catch (Exception e) {
			System.err.print("----------------- error en traer los catalogos");
			e.printStackTrace();
			SessionErrors.add(renderRequest, "errorConocido");
			renderRequest.setAttribute("errorMsg", "Error en catalogos");
			SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
					+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
			return null;
		}
	}

	private void recuperaSubGiroRiesgo(RenderRequest renderRequest) {
		final PortletSession psession = renderRequest.getPortletSession();

		String auxNombre = "LIFERAY_SHARED_F=" + infCotizacion.getFolio() + "_C="
				+ infCotizacion.getCotizacion() + "_V=" + infCotizacion.getVersion()
				+ "_SUBGIRORIESGO";

		if (Validator
				.isNotNull(psession.getAttribute(auxNombre, PortletSession.APPLICATION_SCOPE))) {
			int subGiroRiesgo = (int) psession.getAttribute(auxNombre,
					PortletSession.APPLICATION_SCOPE);

			renderRequest.setAttribute("subGiroRiesgo", subGiroRiesgo);

		} else {
			SessionErrors.add(renderRequest, "errorConocido");
			renderRequest.setAttribute("errorMsg", "Error al recuperar su información");
			SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
					+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		}
	}

	private void recuperaInfoSuma(RenderRequest renderRequest) {
		try {
			final PortletSession psession = renderRequest.getPortletSession();

			String auxNombre = "LIFERAY_SHARED_F=" + infCotizacion.getFolio() + "_C="
					+ infCotizacion.getCotizacion() + "_V=" + infCotizacion.getVersion()
					+ "_ACEPTASUSCRIPCION";
			String auxNombre2 = "LIFERAY_SHARED_F=" + infCotizacion.getFolio() + "_C="
					+ infCotizacion.getCotizacion() + "_V=" + infCotizacion.getVersion()
					+ "_EXCEDELIMITES";

			System.out.println("las variables al recuperar son:");
			System.out.println(auxNombre);
			System.out.println(auxNombre2);

			if (Validator.isNotNull(
					psession.getAttribute(auxNombre, PortletSession.APPLICATION_SCOPE))) {
				int aceptaSuscripcion = (int) psession.getAttribute(auxNombre,
						PortletSession.APPLICATION_SCOPE);

				renderRequest.setAttribute("aceptaSuscripcion", aceptaSuscripcion);

			}
			if (Validator.isNotNull(
					psession.getAttribute(auxNombre, PortletSession.APPLICATION_SCOPE))) {
				int excedeLimites = (int) psession.getAttribute(auxNombre2,
						PortletSession.APPLICATION_SCOPE);

				renderRequest.setAttribute("excedeLimites", excedeLimites);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private int perfilSuscriptor() {
		try {
			switch (idPerfilUser) {
				case CotizadorPaso2PortletKeys.PERFIL_SUSCRIPTORJR:
					return 1;
				case CotizadorPaso2PortletKeys.PERFIL_SUSCRIPTORSR:
					return 1;
				case CotizadorPaso2PortletKeys.PERFIL_SUSCRIPTORMR:
					return 1;
			}
			return 0;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	private boolean perfilPermisosGeneral() {
		try {
			switch (idPerfilUser) {
				case CotizadorPaso2PortletKeys.PERFIL_EJECUTIVO:
					return true;
				case CotizadorPaso2PortletKeys.PERFIL_SUSCRIPTORJR:
					return true;
				case CotizadorPaso2PortletKeys.PERFIL_SUSCRIPTORSR:
					return true;
				case CotizadorPaso2PortletKeys.PERFIL_SUSCRIPTORMR:
					return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	private void recuperaInfoPaso1(RenderRequest renderRequest) {
		String auxNombre = "LIFERAY_SHARED_F=" + infCotizacion.getFolio() + "_C="
				+ infCotizacion.getCotizacion() + "_V=" + infCotizacion.getVersion() + "_DATOSP1";

		final PortletSession psession = renderRequest.getPortletSession();
		if (Validator
				.isNotNull(psession.getAttribute(auxNombre, PortletSession.APPLICATION_SCOPE))) {
			String infoP1 = (String) psession.getAttribute(auxNombre,
					PortletSession.APPLICATION_SCOPE);

			System.out.println("infoP1 del paso 2 : " + infoP1);

			renderRequest.setAttribute("infoP1", infoP1);

		}
	}
	
	
	private void recuperaInfoBajaEndoso(RenderRequest renderRequest) {
		// TODO Auto-generated method stub
		
		if(infCotizacion.getModo().equals(ModoCotizacion.BAJA_ENDOSO)){
			String auxNombre = "LIFERAY_SHARED_F=" + infCotizacion.getFolio() + "_C="
					+ infCotizacion.getCotizacion() + "_V=" + infCotizacion.getVersion() + "_DATOSP1";

			final PortletSession psession = renderRequest.getPortletSession();
			if (Validator
					.isNotNull(psession.getAttribute(auxNombre, PortletSession.APPLICATION_SCOPE))) {
				String infoP1 = (String) psession.getAttribute(auxNombre,
						PortletSession.APPLICATION_SCOPE);

				System.out.println("infoP1 del paso 2 : " + infoP1);

				renderRequest.setAttribute("infoP1", infoP1);

			}

		}
		
	}

}