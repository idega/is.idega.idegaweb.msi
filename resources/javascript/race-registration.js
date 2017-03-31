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
				RaceBusiness.getTransmitterPricesAndTeamInfo(raceId, eventId, {
					callback: function(data) {
						closeAllLoadingMessages();
						
						var price = data == null ? null : data.id;
						var pLabel = jQuery('.tt-price');
						if ((price != null) && (price != undefined)) {
							pLabel.text(price);
							useTTLayer.show();
						} else {
							useTTCheckbox.prop('checked',false);
							pLabel.text('');
							useTTLayer.hide();
						}
						
						var teamCount = data == null ? null : data.value;
						if (teamCount != null) {
							teamCount++;
							teamCount--;
						}
						if (teamCount == null || teamCount <= 1) {
							jQuery('input.partner-one').attr('style', 'display: none;');
							jQuery('input.partner-one').parent().attr('style', 'display: none;');
							
							jQuery('input.partner-two').attr('style', 'display: none;');
							jQuery('input.partner-two').parent().attr('style', 'display: none;');
						} else {
							jQuery('input.partner-one').attr('style', 'display: block;');
							jQuery('input.partner-one').parent().attr('style', 'display: block;');
							
							jQuery('input.partner-two').attr('style', 'display: block;');
							jQuery('input.partner-two').parent().attr('style', 'display: block;');
						}
					}
				});
			});
		}
};
