const modo = {
    NUEVA: "NUEVA",
    EDICION: "EDICION",
    COPIA: "COPIA",
    AUX_PASO4: "AUX_PASO4",
    ALTA_ENDOSO: "ALTA_ENDOSO",
    BAJA_ENDOSO: "BAJA_ENDOSO",
    EDITAR_ALTA_ENDOSO : "EDITAR_ALTA_ENDOSO",
    EDITAR_BAJA_ENDOSO : "EDITAR_BAJA_ENDOSO",
    CONSULTA : "CONSULTA",
    CONSULTAR_REVISION : "CONSULTAR_REVISION"
};


const tipoCotizacion = {
	ERROR : "ERROR",
	FAMILIAR : "FAMILIAR",
	EMPRESARIAL : "EMPRESARIAL"
};

const tipoPersona = {
		FISICA : "FISICA",
		MORAL : "MORAL"
};

const formatter = new Intl.NumberFormat('en-US', {
	  style: 'currency',
	  currency: 'USD',
	  minimumFractionDigits: 2
});

const tipoGuardar = {
		GUARDAR_UBICACION : 1,
		AGREGAR_UBICACION : 2,
		ELIMINAR_UBICACION : 3
};

const NoEsELiminar = -1;

const edoCotizacion = {
		 SUBGIRO_RIESGO : "XS",
		 EXEDE_LIMITES : "XEL",
		 COTIZADO_SUSCRIPTOR : "CPS",
		 REVIRE_SUSCRIPTOR : "RAS",
		 TURNADO_A_492 : "T492",
		 VBA_492 : "VB492",
		 RECHAZO_VBA_492 : "RVB492"
};

const maxUbicaciones = {
		familiar : 5,
		empresarial : 20
};

/* es el objeto donde se setea la informacion de la cotizacion */
var infoCotJson =  null;

/* Es el objeto donde se setea la informacion de las ubicaciones */
var listaUbicaciones = new Object();

var continuar = false;

var idPestaniaElimina = -1;

var infoUltimaTab = {
		objeto : "",
		numero : "",
		etiqueta : ""
}