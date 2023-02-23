function daFormatoMoneda(campo){
	if(!valIsNullOrEmpty($( campo ).val())){
		$( campo ).val(formatter.format($( campo ).val()));
	}
}


$(".acordeon .moneda" ).on("blur", function(){
	this.value = this.value.replace(/[^0-9\.]/g,'');
	daFormatoMoneda($(this));
});

$(".acordeon .moneda" ).on("keyup", function(event){
	var aux = $(event.target).val().split('.');
	$(event.target).val(aux[0]);
	 $(event.target).val(function (index, value ) {
	        return value.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d)\.?)/g, ",");
	    });
});

$(".acordeon .moneda" ).on("focus", function(){
	/*this.value = this.value.replace(/[^0-9\.,]/g,'');*/
	var abc = $(this).val().replace(/[^0-9\.,]/g,'');
	$(this).val(abc.split('.')[0]);
});

$("#tabs .cpValido" ).on("keyup", function(){
	this.value = this.value.replace(/[^0-9\.]/g,'');
});


$(".infReq").focus(function(){
	$(".alert-danger").remove();
    $('.invalid').removeClass('invalid');
});


function selectDestroy(objeto, enabled) {
    $(objeto).prop("disabled", enabled);
    $(objeto).material_select('destroy');
    $(objeto).material_select();
}