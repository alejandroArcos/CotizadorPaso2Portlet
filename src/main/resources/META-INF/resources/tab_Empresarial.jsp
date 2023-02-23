<%@ include file="./init.jsp"%>

<c:set var="oculto" value="${ perfilMayorEjecutivo ? '' : 'd-none' }"></c:set>

<h6 id="tit_tab">
	<liferay-ui:message key="CotizadorPaso2Portlet.titDatsRiesgP2" />
</h6>
<div class="datosRiesgo">
	<div class="row">
		<div class="col-md-2">
			<div class="md-form">
				<input type="text" name="dr_cp" id="dr_cp" idCp="${ ubicacion.ubicaciones[count].cpData.idCp}" maxlength="5" class="form-control cpValido infReq" value="${ ubicacion.ubicaciones[count].cpData.cp}">
				<label for="dr_cp">
					<liferay-ui:message key="CotizadorPaso2Portlet.lblCodPosP2" />
				</label>
			</div>
		</div>
		<div class="col-md-3">
			<div class="md-form">
				<input type="text" name="dr_calle" id="dr_calle" class="form-control infReq" value="${ ubicacion.ubicaciones[count].calle}">
				<label for="dr_calle">
					<liferay-ui:message key="CotizadorPaso2Portlet.lblCalleP2" />
				</label>
			</div>
		</div>
		<div class="col-md-2">
			<div class="md-form form-group">
				<input type="text" name="dr_numero" id="dr_numero" class="form-control infReq" value="${ ubicacion.ubicaciones[count].numero}">
				<label for="dr_numero">
					<liferay-ui:message key="CotizadorPaso2Portlet.lblNumeroP2" />
				</label>
			</div>
		</div>
		<div class="col-md-2">
			<div class="md-form form-group">
				<select name="dr_colonia" id="dr_colonia" class="mdb-select form-control-sel  infReq">
					<option value="-1" selected><liferay-ui:message key="CotizadorPaso2Portlet.selectOpDefoult" /></option>
					<c:forEach items="${colonias[count]}" var="option">
						<c:set var="seleccionado" 
						value="${ option.idCp == ubicacion.ubicaciones[count].cpData.idCp ? 'selected' : '' }"></c:set>
						<option ${ seleccionado } value="${option.idCp}">${option.colonia}</option>
					</c:forEach>
				</select>
				<label for="dr_colonia">
					<liferay-ui:message key="CotizadorPaso2Portlet.lblColoniaP2" />
				</label>
			</div>
		</div>
		<div class="col-md-3">
			<div class="md-form form-group">
				<input type="text" name="dr_municipio" id="dr_municipio" class="form-control infReq" value="${ ubicacion.ubicaciones[count].cpData.delegacion}">
				<label for="dr_municipio">
					<liferay-ui:message key="CotizadorPaso2Portlet.lblDelMuniP2" />
				</label>
			</div>
		</div>
	</div>



	<div class="row">
		<div class="col-md-2">
			<div class="md-form form-group">
				<input type="text" name="dr_estado" id="dr_estado" class="form-control infReq" value="${ ubicacion.ubicaciones[count].cpData.estado}">
				<label for="dr_estado">
					<liferay-ui:message key="CotizadorPaso2Portlet.lblEdoP2" />
				</label>
			</div>
		</div>
		<div class="col-md-2">
			<div class="md-form form-group">
				<select name="dr_nivel" id="dr_nivel" class="mdb-select form-control-sel infReq">
					<option value="-1" selected><liferay-ui:message key="CotizadorPaso2Portlet.selectOpDefoult" /></option>
					<c:forEach items="${listaNiveles}" var="option">
						<c:set var="seleccionado" value="${ option.idCatalogoDetalle == ubicacion.ubicaciones[count].niveles ? 'selected' : '' }"></c:set>
						<option ${ seleccionado } value="${option.idCatalogoDetalle}">${option.descripcion}</option>
					</c:forEach>
				</select>
				<label for="dr_nivel">
					<liferay-ui:message key="CotizadorPaso2Portlet.lblNivelesP2" />
				</label>
			</div>
		</div>
		<div class="col-md-4">
			<div class="md-form ${ oculto }">
				<textarea id="dr_DescripcionConstruccion" name="dr_DescripcionConstruccion" class="md-textarea form-control " rows="3" maxlength="300" style="text-transform: uppercase;">${ ubicacion.ubicaciones[count].descripcionConstruccion }</textarea>
				<label for="dr_DescripcionConstruccion">
					<liferay-ui:message key="CotizadorPaso2Portlet.descConstruccion" />
				</label>
			</div>
		</div>
		<div class="col-md-4">
			<div class="md-form ${ oculto }">
				<textarea id="dr_descripcionSeguridad" name="dr_descripcionSeguridad" class="md-textarea form-control " rows="3" maxlength="300" style="text-transform: uppercase;">${ ubicacion.ubicaciones[count].descripcionMedidaSeguridad }</textarea>
				<label for="dr_descripcionSeguridad">
					<liferay-ui:message key="CotizadorPaso2Portlet.descMedidasSeguridad" />
				</label>
			</div>
		</div>
	</div>
</div>

<h6 class="p-4">
	<liferay-ui:message key="CotizadorPaso2Portlet.titSumaAseguraCoberP2" />
</h6>

