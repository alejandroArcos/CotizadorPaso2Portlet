$( document ).ready( function() {
	showLoader();
	window.scrollTo( 0, 0 );
	generaInfoCot();
	$( "#tabs" ).tabs();
	validaErrorCotizacion();
	aplicaReglas();
	seleccionaUltimaTab();
	hideLoader();
} );

function seleccionaUltimaTab(){
	var ultima = $("#tabs .ui-tabs-nav li").last();

	infoUltimaTab.objeto = ultima;
	infoUltimaTab.numero = ultima.index();
	infoUltimaTab.etiqueta = $(ultima).find(".numUbicacionEndo").text();
	
	 $( "#tabs" ).tabs( "option", "active", infoUltimaTab.numero );

}

function validaErrorCotizacion(){
	if(infoCotJson.noUbicaciones == 0){
		regresaPaso1();
	}
}

function generaInfoCot() {
	if (!valIsNullOrEmpty( infCotString )) {
		infoCotJson = JSON.parse( infCotString );
	}
}

function aplicaReglas() {
	validaTipoMoneda();
	validaTipoUsoFamiliar();
	aplicaFiltros();
	seleccionaModo();
	validaRestriccionesSuscripcion();
	aplicaReglasEdoCotizacion();
}

function validaTipoMoneda() {
	$.each( $( ".acordeon .moneda" ), function(key, registro) {
		daFormatoMoneda( registro );
	} );
}

function validaTipoUsoFamiliar() {
	if (infoCotJson.tipoCotizacion == tipoCotizacion.FAMILIAR) {
		$.each( $( "#tabs .ubicacion" ), function(key, registro) {
			filtraTipUsoPorUbicacion( "#" + $( registro ).attr( "id" ) );
		} );
	}
}

function filtraTipUsoPorUbicacion(ubicacion) {
	$( ubicacion + " .acordeon div" ).removeClass( "d-none" );
	var tpoUso = $( ubicacion + " #tip_uso" ).val();
	console.log( "tpoUso : " + tpoUso );
	switch (tpoUso) {
		case "225":
			$( ubicacion + " .acordeon #PFCAMPCONTENIDOSDIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPCONTENIDOSDIV :input" ).val('');
			$( ubicacion + " .acordeon #PFCAMPCONTENIDOSDIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPCONTENIDOSDIV :input" ).val('');
			$( ubicacion + " .acordeon #PFCAMPRBSECIDIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPRBSECIDIV :input" ).val('');
			$( ubicacion + " .acordeon #PFCAMPRBSECIIDIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPRBSECIIDIV :input" ).val('');
			$( ubicacion + " .acordeon #PFCAMPRBSECIIIDIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPRBSECIIIDIV :input" ).val('');
			$( ubicacion + " .acordeon #PFCAMPDINVALDIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPDINVALDIV :input" ).val('');
			$( ubicacion + " .acordeon #PFCAMPEECDIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPEECDIV :input" ).val('');
			$( ubicacion + " .acordeon #PFCAMPEEMDIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPEEMDIV :input" ).val('');
			break;
		case "226":
			$( ubicacion + " .acordeon #PFCAMPPERDIDARDIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPPERDIDARDIV :input" ).val('');
			break;
		case "1009":
			$( ubicacion + " .acordeon #PFCAMPEDIFICIODIV" ).addClass( "d-none" );
			$( ubicacion + " .acordeon #PFCAMPEDIFICIODIV :input" ).val('');
			break;
		default:
			$( ubicacion + " .acordeon div" ).removeClass( "d-none" );
		$( ubicacion + " .acordeon div :input" ).val('');
			break;
	}
}

function aplicaFiltros() {
	verificarVisibilidad();
	verificarDisabled();
	setObservableElements();
	validaCamposPerfil();
}

$( "#tabs .tip_uso" ).change( function() {
	var ubicacionPadre = $( this ).closest( ".ubicacion" );
	filtraTipUsoPorUbicacion( "#" + $( ubicacionPadre ).attr( "id" ) );
} );

function seleccionaModo() {
	switch (infoCotJson.modo) {
		case modo.NUEVA:
			$( "#btnCls1" ).addClass( "d-none" );
			break;
		case modo.EDICION:
			$( "#btnCls1" ).addClass( "d-none" );
			break;
		case modo.COPIA:
			$( "#btnCls1" ).addClass( "d-none" );
			break;
		case modo.ALTA_ENDOSO:
			$( "#btnCls1" ).addClass( "d-none" );
			break;
		case modo.EDITAR_ALTA_ENDOSO:
			$( "#btnCls1" ).addClass( "d-none" );
			break;
		case modo.CONSULTA:
			$( "#tabs button.close" ).addClass( "d-none" );
			$( "#btn-add-tab-endosos" ).addClass( "d-none" );
			$( "#save_tot2" ).addClass( "d-none" );
			$("#tabs :input, textarea, select").attr("disabled", true);
			break;
		case modo.CONSULTAR_REVISION:
			$( "#tabs button.close" ).addClass( "d-none" );
			$( "#btn-add-tab-endosos" ).addClass( "d-none" );
			$( "#save_tot2" ).addClass( "d-none" );
			$("#tabs :input, textarea, select").attr("disabled", true);
			$("#paso2_next").addClass("d-none");
			break;
		case modo.BAJA_ENDOSO:
			$( "#tabs button.close" ).addClass( "d-none" );
			$("#btn-add-tab-endosos").addClass( "d-none" );
			$("#paso1_back2").addClass( "d-none" );
			$("#chkBjaEnd").removeClass( "d-none" );
			$("#regresarEndoso").removeClass( "d-none" );
			$("#save_tot2").addClass("d-none");
			$("#infoPrimas").addClass("d-none");
			$("#paso2_next").addClass("d-none");
			$("#con_baja").removeClass("d-none");
			$("#contenPaso2 input, textarea, select ").not("#chkBjaEnd .form-check-input, #comentariosDosSuscrip").attr("disabled", true);
			break;
		case modo.EDITAR_BAJA_ENDOSO:
			$( "#tabs button.close" ).addClass( "d-none" );
			$("#btn-add-tab-endosos").addClass( "d-none" );
			$("#paso1_back2").addClass( "d-none" );
			$("#chkBjaEnd").removeClass( "d-none" );
			$("#regresarEndoso").removeClass( "d-none" );
			$("#save_tot2").addClass("d-none");
			$("#infoPrimas").addClass("d-none");
			$("#paso2_next").addClass("d-none");
			$("#con_baja").removeClass("d-none");
			$("#contenPaso2 input, textarea, select ").not("#chkBjaEnd .form-check-input, #comentariosDosSuscrip").attr("disabled", true);
			break;
		default:

			break;
	}
}

$( "#btn-add-tab-endosos" ).click( function(e) {
	showLoader();
	e.preventDefault();
	if(validaNoTabs()){
		var faltaInfo = infoFaltanteUbicaciones();
		if (faltaInfo.code == 0) {
			infoCotJson.noUbicaciones += 1;
			llenaObjUbicacion( tipoGuardar.AGREGAR_UBICACION, NoEsELiminar );
			enviaUbicaciones();
		} else {
			showMessageError( '.navbar', faltaInfo.msg, 0 );
			hideLoader();
		}
	}else{
		 showMessageError('.navbar', 'No se pueden agragar más ubicaciones', 0);
		 hideLoader();
	}
} );

function validaNoTabs(){
	if($.isNumeric( infoUltimaTab.etiqueta )){
		if(infoCotJson.tipoCotizacion == tipoCotizacion.EMPRESARIAL){
			if(parseInt(infoUltimaTab.etiqueta) < maxUbicaciones.empresarial){
				return true;
			}
		}
		if(infoCotJson.tipoCotizacion == tipoCotizacion.FAMILIAR){
			if(parseInt(infoUltimaTab.etiqueta) < maxUbicaciones.familiar){
				return true;
			}
		}
	}
	return false;
}

$( "#save_tot2" ).click( function(e) {
	showLoader();
	e.preventDefault();
	var faltaInfo = infoFaltanteUbicaciones();
	if (faltaInfo.code == 0) {
		llenaObjUbicacion( tipoGuardar.GUARDAR_UBICACION, NoEsELiminar );
		enviaUbicaciones();
	} else {
		showMessageError( '.navbar', faltaInfo.msg, 0 );
		hideLoader();
	}
} );

$( "#paso2_next" ).click( function(e) {
	showLoader();
	e.preventDefault();
	var faltaInfo = infoFaltanteUbicaciones();
	if (faltaInfo.code == 0) {
		continuar = true;
		llenaObjUbicacion( tipoGuardar.GUARDAR_UBICACION, NoEsELiminar );
		enviaUbicaciones();
	} else {
		showMessageError( '.navbar', faltaInfo.msg, 0 );
		hideLoader();
	}
} );

function enviaUbicaciones() {
	var axinfp1 = JSON.parse(infoP1);
	if(axinfp1.subEstado == "CPS"){
		redirigeP3();
	}else{
		$.post( guardaUbicacionURL, {
			listaUbicaciones : JSON.stringify( listaUbicaciones ),
			infCotString : JSON.stringify( infoCotJson )
		} ).done( function(data) {
			var response = JSON.parse( data );
			if (response.code == 0) {
				if(subGiroRiesgo != '1'){
					$("#paso2_next").removeClass("d-none");
					$("#infoPrimas").removeClass("d-none");
					$("#btnsuscontinuar").addClass("d-none");				
				}
				if (continuar) {
					redirigeP3();
				} else {
					location.reload();
				}
			} else {
				if(response.code == 5){
					
					
						$("#paso2_next").addClass("d-none");
					$("#infoPrimas").addClass("d-none");
					$("#btnsuscontinuar").addClass("d-none");
					$("#suscripMontoExedeTxt").text(response.msg);
					$( '#modalSuscripMontoExede' ).modal( {
						show : true
					} );
					
					
					
				}else{
					showMessageError( '.navbar', response.msg, 0 );				
				}
				continuar = false;
				hideLoader();
			}
	
		} );
	}
}

$("#btnSuscripMontoSi").click(function(){
	showLoader();
	var aux = "LIFERAY_SHARED_F=" + infoCotJson.folio + "_C="
	+ infoCotJson.cotizacion + "_V=" + infoCotJson.version
	+ "_ACEPTASUSCRIPCION";
	var aux2 = "LIFERAY_SHARED_F=" + infoCotJson.folio + "_C="
	+ infoCotJson.cotizacion + "_V=" + infoCotJson.version
	+ "_EXCEDELIMITES";
	$.post( aceptaSuscripcionURL, {
		nombre : aux,
		nombre2 : aux2
	} ).done( function(data) {
		var response = JSON.parse( data );
		/*location.reload();*/
		excedeLimites = '1';
		aceptaSuscripcion = '1';
		fExcedeLimites();
		$('#modalSuscripMontoExede').modal('hide');	
		location.reload();
	});
});

function redirigeP3() {
	$.post( redirigeURL, {
		infoCot : JSON.stringify( infoCotJson ),
		paso : "/paso3"
	} ).done( function(data) {
		var response = JSON.parse( data );
		if (response.code == 0) {
			window.location.href = response.msg;
		} else {
			showMessageError( '.navbar', response.msg, 0 );
			hideLoader();
		}
	} );
}

function llenaObjUbicacion(tipoGuardar, ubicacionBorrar) {
	listaUbicaciones.p_cotizacion = infoCotJson.cotizacion + "";
	listaUbicaciones.p_version = infoCotJson.version;
	listaUbicaciones.p_tipoGuardar = tipoGuardar;
	listaUbicaciones.p_ubicacion = generainfoUbicaciones( ubicacionBorrar );

}

function generainfoUbicaciones(ubicacionBorrar) {
	var infUbicaciones = new Array();
	var totUbicaciones = $( ".numUbicacionEndo" ).length;

	for (var i = 0; i < totUbicaciones; i++) {
		if ((i + 1) != ubicacionBorrar) {
			var ub = new Object();
			ub.idUbicacion = $( $( ".numUbicacionEndo" )[i] ).text();
			ub.calle = $( "#ubicacion-" + (i + 1) + " #dr_calle" ).val();
			ub.numero = $( "#ubicacion-" + (i + 1) + " #dr_numero" ).val();
			ub.cpData = getInfoCp( i + 1 );
			ub.niveles = $( "#ubicacion-" + (i + 1) + " #dr_nivel" ).val();
			ub.valorInmueble = 0.0;
			ub.descripcionConstruccion = $( "#ubicacion-" + (i + 1) + " #dr_DescripcionConstruccion" ).val();
			ub.descripcionMedidaSeguridad = $( "#ubicacion-" + (i + 1) + " #dr_descripcionSeguridad" ).val();

			if (infoCotJson.tipoCotizacion == tipoCotizacion.FAMILIAR) {
				ub.tipoInmueble = $( "#ubicacion-" + (i + 1) + " #tip_inm" ).val();
				ub.tipoUso = $( "#ubicacion-" + (i + 1) + " #tip_uso" ).val();
			}
			ub.fields = generaFils( i + 1 );

			infUbicaciones.push( ub );
		}
	}

	return infUbicaciones;
}

function getInfoCp(i) {
	var infoCp = new Object();
	infoCp.idCp = parseInt( $( "#ubicacion-" + i + " #dr_colonia" ).val() );
	infoCp.codigoPostal = $( "#ubicacion-" + i + " #dr_cp" ).val();
	infoCp.colonia = $( "#ubicacion-" + i + " #dr_colonia option:selected" ).text();
	infoCp.delegacion = $( "#ubicacion-" + i + " #dr_municipio" ).val();
	infoCp.estado = $( "#ubicacion-" + i + " #dr_estado" ).val();
	return infoCp;
}

function generaFils(i) {
	var fils = new Object();
	$.each( $( "#ubicacion-" + i + " .acordeon input" ), function(key, value) {
		if ($( value ).is( ":text" )) {
			if ($( value ).hasClass( "select-dropdown" )) {
				var sel = $( value ).siblings( "select" );
				if (valIsNullOrEmpty( $( sel ).val() )) {
					fils[$( sel ).attr( "name" )] = "";
				} else {
					fils[$( sel ).attr( "name" )] = $( sel ).val();
				}
			} else {
				fils[$( value ).attr( "name" )] = quitaTipoMoneda( $( value ).val() );
			}
		} else if ($( value ).is( ":checkbox" )) {
			fils[$( value ).attr( "name" )] = $( value ).is( ":checked" );
		}
	} );
	return fils;
}

function quitaTipoMoneda(data) {
	return data.replace( /[$,]/g, '' );
}

function infoFaltanteUbicaciones() {
	$( ".alert-danger" ).remove();
	$( '.invalid' ).removeClass( 'invalid' );
	var totUbicaciones = $( ".numUbicacionEndo" ).length;
	var info = new Object();
	info.code = 0;
	info.msg = "";
	for (var i = 0; i < totUbicaciones; i++) {
		var errorPestania = false;
		$.each( $( "#ubicacion-" + (i + 1) + " input.infReq" ), function(key, value) {
			if (valIsNullOrEmpty( $( value ).val() )) {
				$( value ).addClass( 'invalid' );
				$( value ).parent().append(
						"<div class=\"alert alert-danger\" role=\"alert\"> <span class=\"glyphicon glyphicon-ban-circle\"></span>"
								+ " Hace falta información requerida </div>" );

				errorPestania = true;
			}
		} );

		$.each( $( "#ubicacion-" + (i + 1) + " select.infReq" ), function(key, value) {
			if ($( value ).val() == "-1") {
				$( value ).siblings( "input" ).addClass( 'invalid' );
				$( value ).parent().append(
						"<div class=\"alert alert-danger\"> <span class=\"glyphicon glyphicon-ban-circle\"></span> "
								+ "Hace falta información requerida </div>" );
				errorPestania = true;
			}
		} );

		if (errorPestania) {
			info.code = 2;
			if (valIsNullOrEmpty( info.msg )) {
				info.msg = "Falta información requerida en la pestaña " + $( $( ".numUbicacionEndo" )[i] ).text();
			} else {
				info.msg += "<br/>Falta información requerida en la pestaña " + $( $( ".numUbicacionEndo" )[i] ).text();
			}

		}
	}
	return info;
}

$( ".cpValido" ).blur( function() {
	showLoader();
	cp = $( this ).val();
	var ubicacionPadre = $( this ).closest( ".ubicacion" )
	if ((cp.length < 5) && (cp.length > 0)) {
		$( this ).focus();
		showMessageError( '.navbar', 'Por favor verifique el código postal a 5 dígitos', 0 );
		hideLoader();
	} else if (cp.length == 5) {
		limpiaCamposCp( ubicacionPadre );
		llenaInfoByCP( ubicacionPadre, cp );
	} else {
		limpiaCamposCp( ubicacionPadre );
		showMessageSuccess( '.navbar', ' Código postal vacio', 0 );
		hideLoader();
	}
} );

function limpiaCamposCp(ubicacionPadre) {
	$( ubicacionPadre ).find( "#dr_calle" ).val( "" );
	$( ubicacionPadre ).find( "#dr_numero" ).val( "" );
	$( ubicacionPadre ).find( "#dr_municipio" ).val( "" );
	$( ubicacionPadre ).find( "#dr_estado" ).val( "" );
	$( ubicacionPadre ).find( "#dr_colonia option:not(:first)" ).remove();
	selectDestroy( $( ubicacionPadre ).find( '#dr_colonia' ), false );
}

function llenaInfoByCP(ubicacionPadre, cp) {
	$.post( getCpURL, {
		cp : cp,
		pantalla : infoCotJson.pantalla
	} ).done( function(data) {
		var response = JSON.parse( data );
		console.log( response );
		if (response.code == 0) {
			llenaInfoCp( ubicacionPadre, response.cpData );
		} else {
			if (response.code == 4) {
				
				$( "#suscripCpRiesgo" ).text( response.msg );
				$( '#modalSuscripCPRiesgo' ).modal( {
					show : true
				} );
			}
			$( ubicacionPadre ).find( "#dr_cp" ).val("");
			
			showMessageError( '.navbar', response.msg, 0 );
		}
		hideLoader();
	} );
}

function llenaInfoCp(ubicacionPadre, cpData) {
	$.each( cpData, function(key, value) {
		$( ubicacionPadre ).find( "#dr_cp" ).attr( "idcp", value.idCp );
		$( ubicacionPadre ).find( "#dr_municipio" ).val( value.delegacion );
		$( ubicacionPadre ).find( "#dr_municipio" ).siblings( "label" ).addClass( "active" );
		$( ubicacionPadre ).find( "#dr_estado" ).val( value.estado );
		$( ubicacionPadre ).find( "#dr_estado" ).siblings( "label" ).addClass( "active" );
		$( ubicacionPadre ).find( '#dr_colonia' ).append( new Option( value.colonia, value.idCp ) );
		selectDestroy( $( ubicacionPadre ).find( '#dr_colonia' ), false );
	} );
}

$( "#tabs button.close" ).click( function(e) {
	var liSelect = $( this ).closest( "li" );
	var ubica = $( liSelect ).attr( "aria-controls" );
	var sepUbica = ubica.split( "-" );

	if (sepUbica.length > 1) {
		$('#ubicacionEliminalbl').text( $(this).siblings("a").find(".numUbicacionEndo").text() );
		$( '#modalCerrarTab' ).modal( {
			show : true
		} );
	} else {
		showMessageError( '.navbar', "Error al guardar su información", 0 );
		hideLoader();
	}

} );

$( "#btnEliminarPestania" ).click( function(e) {
	showLoader();
	e.preventDefault();
	
	var liElimina = $("#tabs > ul li ").has("a .numUbicacionEndo:contains(" + $('#ubicacionEliminalbl').text() + ")");
	var ubica = $( liElimina ).attr( "aria-controls" );
	var sepUbica = ubica.split( "-" );
	
	infoCotJson.noUbicaciones -= 1;
	llenaObjUbicacion( tipoGuardar.ELIMINAR_UBICACION, parseInt(sepUbica[1]) );
	enviaUbicaciones();


} );


$("#btnRegresarPasoAnt").click(function (){
	regresaPaso1();
});

function regresaPaso1(){
	showLoader();
	actualizainfoCot();
	$.post( redirigeURL, {
		infoCot : JSON.stringify( infoCotJson ),
		paso : seleccionaVentana()
	} ).done( function(data) {
		var response = JSON.parse( data );
		if (response.code == 0) {
			window.location.href = response.msg;
		} else {
			showMessageError( '.navbar', response.msg, 0 );
			hideLoader();
		}
	} );
}

function seleccionaVentana(){
	if(infoCotJson.tipoCotizacion == tipoCotizacion.EMPRESARIAL){
		return "/paquete-empresarial";
	}
	return "/paquete-familiar";
}

function actualizainfoCot(){
	switch (infoCotJson.modo) {
		case modo.NUEVA:
			infoCotJson.modo = modo.EDICION;
			break;
		case modo.COPIA:
			infoCotJson.modo = modo.EDICION;
			break;
		case modo.ALTA_ENDOSO:
			infoCotJson.modo = modo.EDITAR_ALTA_ENDOSO;
			break;
		case modo.BAJA_ENDOSO:
			infoCotJson.modo = modo.EDITAR_BAJA_ENDOSO;
			break;

		default:
			break;
	}
	
}


function validaRestriccionesSuscripcion(){
	validaSubgiroRiesgo();
	fExcedeLimites();
	fCotizadoPorSuscriptor();
}

function validaSubgiroRiesgo(){
	if(subGiroRiesgo == '1'){
		if(perfilSuscriptor == '0'){
			$("#paso2_next").addClass("d-none");
			$("#infoPrimas").addClass("d-none");
			$("#btnsuscontinuar").removeClass("d-none");
		}
	}
}

function fExcedeLimites(){
	if(excedeLimites == '1'){
		if(aceptaSuscripcion == '1'){
			$("#paso2_next").addClass("d-none");
			$("#infoPrimas").addClass("d-none");
			$("#btnsuscontinuar").removeClass("d-none");
		}
		
	}
}

function fCotizadoPorSuscriptor(){
	if(infoCotJson.modo == modo.CONSULTA){
		var axinfp1 = JSON.parse(infoP1);
		if(axinfp1.subEstado == "CPS"){
			$("#paso2_next").removeClass("d-none");
			$("#btnsuscontinuar").addClass("d-none");
		}
	}
}

function aplicaReglasEdoCotizacion(){
	var infoP1Json = JSON.parse(infoP1);
	if(!valIsNullOrEmpty(infoP1Json.subEstado)){
		if(perfilSuscriptor == '1'){
			switch (infoP1Json.subEstado) {
				case edoCotizacion.COTIZADO_SUSCRIPTOR:
					ocultaCamposSubEdo();
					break;
				case edoCotizacion.RECHAZO_VBA_492:
					ocultaCamposSubEdo();
					break;
				default:
					break;
			}
		}else{
			
			if(infoCotJson.modo == modo.CONSULTA){
				ocultaCamposSubEdo();				
			}
		}
	}
}

function ocultaCamposSubEdo(){
	$("#contenPaso2 input, textarea, select ").attr("disabled", true);
	$("#chkBjaEnd .form-check-input, #comentariosDosSuscrip ").attr("disabled", false);
	$("#btn-add-tab-endosos").addClass("d-none");
	$("#save_tot2").addClass("d-none");
	$("#infoPrimas").addClass("d-none");
	$( "#tabs button.close" ).addClass( "d-none" );
}


$("#btnsuscontinuar").click(function(){
	$("#chkBjaEnd .form-check-input, #comentariosDosSuscrip ").attr("disabled", false);
	$('#fileModal').modal({
        show: true,
        backdrop: 'static',
        keyboard: false
    });
});

$('#fileModal #docAgenSusc').change(function(evt) {
    cargaDocumentos(evt, '#fileModal', '');
});

function cargaDocumentos(evt, padre, iddoc) {
    var listMimetypeValid = ["application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/pdf"
    ];
   
    var btn = $("#docAgenSusc");
    var maxSize = 5;
    var files = evt.target.files; /* FileList object */
    var nomInvalidos = "";
    const dt = new DataTransfer();
    $(padre + ' #infDocSuc' + iddoc).val('');
   /* $.each(files, function(key, file) {
        if (listMimetypeValid.indexOf(file.type) < 0) {
            nomInvalidos += (nomInvalidos == "") ? file.name : ", " + file.name;
        } else {
            dt.items.add(file);
            var nombres = $(padre + ' #infDocSuc' + iddoc).val();
            nombres += (nombres == '') ? file.name : ", " + file.name;
            $(padre + ' #infDocSuc' + iddoc).val(nombres);
        }
    });*/
    $.each(files, function(key, file) {

        var pesoArchivo = file.size / 1024 / 1024; /*peso en megas*/

        var agregarArchivo = true;
        if (listMimetypeValid.indexOf(file.type) < 0) {
            agregarArchivo = false;
            nomInvalidos += (nomInvalidos == "") ? file.name : ",<br>" + file.name;
            nomInvalidos += "- Tipo de archivo no admitido";
        }
        if (pesoArchivo > maxSize) {
            agregarArchivo = false;
            nomInvalidos += (nomInvalidos == "") ? file.name : ",<br>" + file.name;
            nomInvalidos += " - El archivo que quiere cargar pesa más de 5 MB, favor de reducir la resolución o cargar otro más ligero"
        }

        if (agregarArchivo) {
            dt.items.add(file);
            var nombres = $(padre + ' #infDocSuc' + iddoc).val();
            nombres += (nombres == '') ? file.name : ", " + file.name;
            $(padre + ' #infDocSuc' + iddoc).val(nombres);
            $(btn).parent().addClass('btn-green');
            $(btn).parent().removeClass('btn-blue');
            $(btn).parent().removeClass('btn-purple');
            $(btn).parent().removeClass('btn-red');
        } else {

            $(btn).parent().addClass('btn-purple');
            $(btn).parent().removeClass('btn-blue');
            $(btn).parent().removeClass('btn-green');
            $(btn).parent().removeClass('btn-red');
            $(btn).val('');
        }
    });
    $(padre + ' #docAgenSusc' + iddoc).files = dt.files;
    if (nomInvalidos != "") {
        showMessageError(padre + ' .modal-header', "Archivo(s) invalido(s): " + nomInvalidos, 0);

    }
}

$("#btnSuscripEnvSus").click(function(){
	showLoader();
	adjuntaArchivos();
});



async function adjuntaArchivos() {

    var url = new URL(window.location.href);
    var data = new FormData();
    var auxiliarDoc = '{';

    $.each($('#docAgenSusc')[0].files, function(i, file) {
        data.append('file-' + i, file);
        var nomAux = file.name.split('.');
        if (i == 0) {
            auxiliarDoc += '\"file-' + i + '\" : {';
        } else {
            auxiliarDoc += ', \"file-' + i + '\" : {';
        }
        auxiliarDoc += '\"nom\" : \"' + nomAux[0] + '\",';
        auxiliarDoc += '\"ext\" : \"' + nomAux[1] + '\"}';
    });
    auxiliarDoc += '}';

/*    data.append('isRevire', $('#txtAuxEnvDoc').val()); */
    data.append('auxiliarDoc', auxiliarDoc);
    data.append('comentarios', $('#comentariosDosSuscrip').val());
    data.append('infoCot', JSON.stringify(infoCotJson));
    data.append('url', url.origin + url.pathname); 
    data.append('url2', url.origin);
    data.append('totArchivos', $('#docAgenSusc')[0].files.length);

    $.ajax({
        url: sendMailAgenteSuscriptorURL,
        data: data,
        processData: false,
        contentType: false,
        type: 'POST',
        success: function(data) {
            if (data != "") {
                var response = jQuery.parseJSON(data);
                if (response.code > 0) {
                    $('#fileModal').modal('hide');
                    hideLoader();
                    showMessageError('.navbar', response.msg, 0);
                } else {
                    window.location.href = url.origin + url.pathname.replace("/paso2", seleccionaVentana());
                    
   
                }
            } else {
            	showMessageError('.navbar', "Error al enviar la información", 0);
            }
        }
    });
}


$("#con_baja").click(function(){
	var tot_checked = 0;
	var ub = "";
	$.each( $("#chkBjaEnd .form-check :checkbox"), function(key, chek) {
		if($(chek).is(":checked")){
			tot_checked++;
			if(valIsNullOrEmpty(ub)){
				ub =  $(chek).siblings("label").text();
			}else{
				ub += ", " +  $(chek).siblings("label").text();
			}
		}
	} );
	if(tot_checked > 0){
		$("#modalBajaUb #spnUbElim").text(ub);
		$("#modalBajaUb").modal("show");
	}else{
		showMessageError( '.navbar', "Para continuar, seleccione al menos una ubicación", 0 );
	}
	
});

$("#btnAceptarBajaMdl").click(function(e){
	showLoader();
	/**/
	$.post( guardaBajaEndosoURL, {
		listaUbicaciones : $("#modalBajaUb #spnUbElim").text(),
		infCotString : JSON.stringify( infoCotJson ),
		infP1 : infoP1
	} ).done( function(data) {
		var response = JSON.parse( data );
		if(response.code > 0){
			$('#modalBajaUb').modal('hide');
			hideLoader();
			showMessageError( '.navbar', response.msg , 0 );
			
		}else{
			infoCotJson.folio = response.folio;
			infoCotJson.cotizacion = response.cotizacion;
			infoCotJson.version = response.version;
			redirigeP3();
		}
		
	});
});
