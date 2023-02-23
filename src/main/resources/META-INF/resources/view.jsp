<%@ include file="./init.jsp"%>
<jsp:include page="modales.jsp" />

<portlet:resourceURL id="/cotizadores/guardaubicaciones" var="guardaUbicacionURL" />
<portlet:resourceURL id="/cotizadores/paso2/getCP" var="getCpURL" />
<portlet:resourceURL id="/cotizadores/paso2/redirigePasoX" var="redirigeURL" />
<portlet:resourceURL id="/cotizadores/paso2/aceptaSuscripcion" var="aceptaSuscripcionURL" />
<portlet:resourceURL id="/paso2/sendMailAgenteSuscriptor" var="sendMailAgenteSuscriptorURL" />
<portlet:resourceURL id="/cotizadores/paso2/guardaBajaEndoso" var="guardaBajaEndosoURL" />

<c:set var="version" scope="session" value="DES.20220804" />


<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css?v=${versionEntrega}">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/jquery-ui.css?v=${versionEntrega}">

<fmt:setLocale value="es_MX" />

<section id="cotizadores-p2" class="upper-case-all">

	<div class="section-heading">
		<div class="container-fluid">
			<h4 class="title text-left">${tituloCotizador}</h4>
		</div>
	</div>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<ul class="stepper stepper-horizontal container-fluid">
					<li id="step1">
						<a href="javascript:void(0)">
							<span class="circle">1</span>
							<span class="label">
								<liferay-ui:message key="CotizadorPaso2Portlet.titPasoUno" />
							</span>
						</a>
					</li>
					<li id="step2" class="active">
						<a href="javascript:void(0)">
							<span class="circle">2</span>
							<span class="label">
								<liferay-ui:message key="CotizadorPaso2Portlet.titPasoDos" />
							</span>
						</a>
					</li>
					<li id="step3">
						<a href="javascript:void(0)">
							<span class="circle">3</span>
							<span class="label">
								<liferay-ui:message key="CotizadorPaso2Portlet.titPasoTres" />
							</span>
						</a>
					</li>
					<li id="step4">
						<a href="javascript:void(0)">
							<span class="circle">4</span>
							<span class="label">
								<liferay-ui:message key="CotizadorPaso2Portlet.titPasoCuatro" />
							</span>
						</a>
					</li>
				</ul>

			</div>
		</div>
	</div>

	<div style="position: relative;">
		<liferay-ui:success key="consultaExitosa" message="CotizadorPaso2Portlet.exito" />
		<liferay-ui:error key="errorConocido" message="${errorMsg}" />
		<liferay-ui:error key="errorDesconocido" message="CotizadorPaso2Portlet.erorDesconocido" />
	</div>

	<div class="padding70" id="contenPaso2">


		<div class="container-fluid paso2Datos">
			<div class="row">
				<div class="col-md-9"></div>
				<div class="col-md-3" style="text-align: right;">
					<div class="md-form form-group">
						<input id="txtFolioP2_2" type="text" name="txtFolioP2_2" class="form-control" disabled value="${ ubicacion.folio} - ${ ubicacion.version}">
						<label for="txtFolioP2_2">
							<liferay-ui:message key="CotizadorPaso2Portlet.titFolio" />
						</label>
					</div>
				</div>
			</div>
			<div id="chkBjaEnd" class="row d-none">
				<div class="col-md-12">
					<div class="text-center">
						<h2>
							Seleccione las ubicaciones que quiere dar de baja
						</h2>
						<br>
						<c:forEach var="j" begin="1" end="${ infCotizacion.noUbicaciones }">
							<div class="form-check form-check-inline">
							  <input type="checkbox" class="form-check-input"
							   id="chk-${ j }" ${ j == 1 ? 'disabled' : '' } >
							  <label class="form-check-label" for="chk-${ j }">${relacionUbicaciones[j]}</label>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<button id="btn-add-tab-endosos" type="button" class="btn btn-blue btn-sm waves-effect waves-light float-right">Agregar Ubicación</button>
				</div>
				<div class="col-md-12">
					<div id="tabs">

						<ul>
							<c:forEach var="i" begin="1" end="${ infCotizacion.noUbicaciones }">
								<li id="tab-${i}">
									<a href="#ubicacion-${i}">
										<i class="fa fa-map-marker" aria-hidden="true"></i>
										<span class="numUbicacionEndo">${relacionUbicaciones[i]}</span>
									</a>
									<button id="btnCls${i}" type="button" class="close my-1 mr-2" aria-label="Close">
										<span aria-hidden="true">×</span>
									</button>
								</li>
							</c:forEach>
						</ul>

						<c:forEach var="i" begin="1" end="${ infCotizacion.noUbicaciones }">
							<div id="ubicacion-${i}" style="padding: 50px;" class="ubicacion">
								<div class="row">
									<div class="col-md-12">
										<c:set var="count" value="${ i - 1 }" scope="request"></c:set>
										<c:choose>
											<c:when test="${ infCotizacion.tipoCotizacion == 'FAMILIAR'}">
												<jsp:include page="tab_Familiar.jsp" />
											</c:when>
											<c:when test="${ infCotizacion.tipoCotizacion == 'EMPRESARIAL'}">
												<jsp:include page="tab_Empresarial.jsp" />
											</c:when>
											<c:otherwise>
												<h1>Error al consultar su información</h1>
											</c:otherwise>
										</c:choose>
										
										<div class="row">
											<div class="col-md-12 acordeon" id="acordeon${ i }">
												${jsonFiels[i]}
											</div>
										</div>

									</div>
								</div>
							</div>
						</c:forEach>

					</div>
				</div>
			</div>


			<div class="row mt-4">
				<div  class="col-md-8">
					<c:if test="${perfilSuscriptor == 1}">
						<div id="infoPrimas" class="row">
							<table class="table tablaTit" id="tabla_Tittotal">
								<thead class="blue-grey lighten-4">
									<tr class="triTabTot">
										<th>
											<liferay-ui:message key="CotizadorPaso2Portlet.lblUbicacionP2" />
										</th>
									</tr>
								</thead>
								<tbody>
									<tr class="trsTabParcial">
										<td scope="row">
											<liferay-ui:message key="CotizadorPaso2Portlet.lblPrimaNetaP2" />
										</td>
									</tr>
								</tbody>
							</table>
							<div class="table-wrapper-scroll-table">
								<table class="table tablaTotal" id="tabla_total" >
									<thead class="blue-grey lighten-4">
										<tr class="triTabTot">
	
											<c:forEach items="${ ubicacion.ubicaciones }" var="u">
												<th>${ u.idubicacion }</th>
											</c:forEach>
										</tr>
									</thead>
									<tbody>
										<tr class="trsTabParcial">
											<c:forEach items="${ ubicacion.ubicaciones }" var="u">
												<td><fmt:formatNumber value="${u.primaNeta}" type="currency"  /> </td>
											</c:forEach>
										</tr>
									</tbody>
								</table>
							</div>
							<table class="table col-md-4 tablaTotal2" id="tabla_total_2">
								<thead class="blue-grey lighten-4">
									<tr>
										<th id="tit_table">
											<liferay-ui:message key="CotizadorPaso2Portlet.lblPrimaTotalP2" />
										</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td id="tol_table" class="totPrimaTab"> 
											<fmt:formatNumber value="${ubicacion.primaTotal}" type="currency" />
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</c:if>
				</div>




				<div class="col-md-4">
					<div class="row">
						<div class="col-md-12">
							<a class="btn btn-blue waves-effect waves-light float-right btn-ubicacion " id="save_tot2">
								<liferay-ui:message key="CotizadorPaso2Portlet.btnGuardarUbicaP2" />
							</a>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<!-- 							<a class="btn btn-pink waves-effect waves-light btn-ubicacion d-none" id="paso2_suscripcion_next" > -->
							<%-- 								<liferay-ui:message key="CotizadorPaso2Portlet.btnContinuarP1" /> --%>
							<!-- 							</a> -->
							<a class="btn btn-pink waves-effect waves-light btn-ubicacion float-right" id="paso2_next">
								<liferay-ui:message key="CotizadorPaso2Portlet.btnContinuarP3" />
							</a>
							<a class="btn btn-blue waves-effect waves-light btn-ubicacion float-right" data-toggle="modal" href="#modalRegresarP1" id="paso1_back2">
								<liferay-ui:message key="CotizadorPaso2Portlet.btnRegresarP2" />
							</a>
							<a class="btn btn-blue waves-effect waves-light btn-ubicacion float-right d-none" id="regresarEndoso" onclick="regresaPaso1();">
								<liferay-ui:message key="CotizadorPaso2Portlet.btnRegresarP2" />
							</a>

							<a class="btn btn-pink waves-effect waves-light btn-ubicacion d-none float-right" id="con_baja">
								<liferay-ui:message key="CotizadorPaso2Portlet.btnContinuarP3" />
							</a>
							<a class="btn btn-pink waves-effect waves-light btn-ubicacion  d-none float-right" id="btnsuscontinuar"> A SUSCRIPCIÓN </a>
						</div>
					</div>
				</div>
			</div>
					
			
		</div>
	</div>
</section>



<script src="<%=request.getContextPath()%>/js/jquery-ui.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/objetos.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/funcionesGenericas.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/visibilidad.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/main.js?v=${version}"></script>


<script>
 var infCotString  = '${infCotJson}';
 
 var guardaUbicacionURL = '${guardaUbicacionURL}';
 var getCpURL = '${getCpURL}';
 var redirigeURL = '${redirigeURL}';
 var aceptaSuscripcionURL = '${aceptaSuscripcionURL}';
 var sendMailAgenteSuscriptorURL = '${sendMailAgenteSuscriptorURL}';
 var guardaBajaEndosoURL = '${guardaBajaEndosoURL}';
 
 
 var subGiroRiesgo = '${subGiroRiesgo}';
 var excedeLimites = '${excedeLimites}';
 var aceptaSuscripcion = '${aceptaSuscripcion}';
 var perfilSuscriptor = '${perfilSuscriptor}';
 var infoP1 = '${infoP1}';
</script>