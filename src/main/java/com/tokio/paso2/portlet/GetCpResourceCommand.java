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
import com.tokio.cotizadorModular.Bean.CpResponse;
import com.tokio.cotizadorModular.Interface.CotizadorPaso2;
import com.tokio.paso2.constants.CotizadorPaso2PortletKeys;

import java.io.PrintWriter;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author jonathanfviverosmoreno
 *
 */

@Component(
		immediate = true,
		property = {
				"javax.portlet.name=" + CotizadorPaso2PortletKeys.PORTLET_NAME,
					"mvc.command.name=/cotizadores/paso2/getCP" 
				},
		service = MVCResourceCommand.class)
public class GetCpResourceCommand extends BaseMVCResourceCommand {

	/* (non-Javadoc)
	 * @see com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand#doServeResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
	 */
	
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
		PrintWriter writer = resourceResponse.getWriter();
		String cp = ParamUtil.getString(resourceRequest, "cp");
		String pantalla = ParamUtil.getString(resourceRequest, "pantalla");
		User user = (User) resourceRequest.getAttribute(WebKeys.USER);
		String p_usuario = user.getScreenName();
		CpResponse cpResponse = _CMServicesP2.getCP(cp, p_usuario, pantalla);
		String responseString = gson.toJson(cpResponse);
		writer.write(responseString);
	}

}
