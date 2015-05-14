var RaceRegistrationHelper = {
		ttPricesActions : function(ttPrices) {
			var useTTCheckbox = jQuery('[name="prm_rent_tt"]');
			var useTTLayer = useTTCheckbox.parents('tr').first();
			useTTLayer.addClass("tt-layer");
			jQuery(".tt-layer").hide();
			jQuery('[name="prm_event"]').change(function(event) {
				var useTTCheckbox = jQuery('[name="prm_rent_tt"]');
				var useTTLayers = jQuery(".tt-layer");
				var raceId = jQuery('input[type="hidden"][name="prm_race"]').val();
				var eventId = jQuery('#' + event.target.id).val();
				showLoadingMessage('');
				RaceBusiness.getTransmitterPrices(raceId, eventId, {
					callback: function(price) {
						closeAllLoadingMessages();
						var pLabel = jQuery('.tt-price');
						if ((price != null) && (price != undefined)) {
							pLabel.text(price);
							useTTLayer.show();
						} else {
							useTTCheckbox.prop('checked',false);
							pLabel.text('');
							useTTLayer.hide();
						}
					}
				});
			});
		}
};
