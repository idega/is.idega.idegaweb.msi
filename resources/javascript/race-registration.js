/**
 * 
 */
var RaceRegistrationHelper = {
		ttPricesActions : function(ttPrices){
			var useTTCheckbox = jQuery('[name="prm_rent_tt"]');
			var useTTLayer = useTTCheckbox.parents('tr').first();
			useTTLayer.addClass("tt-layer");
//			useTTLayer.prev().addClass("tt-layer");
			jQuery(".tt-layer").hide();
			jQuery('[name="prm_event"]').change(function(){
				var useTTCheckbox = jQuery('[name="prm_rent_tt"]');
				var useTTLayers = jQuery(".tt-layer");
				var price = ttPrices[jQuery(this).val()];
				var pLabel = jQuery('.tt-price');
				if(price && (price > 0)){
					pLabel.text(price);
					useTTLayer.show();
				}else{
					useTTCheckbox.prop('checked',false);
					pLabel.text('');
					useTTLayer.hide();
				}
			});
		}
};
