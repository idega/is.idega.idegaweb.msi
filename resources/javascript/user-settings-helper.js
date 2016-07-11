jQuery(document).ready(function() {
	
	UserSettingsHelper.changeVisibility("select[name='us_race_number_snocross']");
	UserSettingsHelper.changeVisibility("select[name='us_race_number_mx']");

	jQuery("select[name='us_race_number_snocross']").change(function() {
		UserSettingsHelper.changeVisibility("select[name='us_race_number_snocross']");
	});
	
	jQuery("select[name='us_race_number_mx']").change(function() {
		UserSettingsHelper.changeVisibility("select[name='us_race_number_mx']");
	});
});

var UserSettingsHelper = {
		changeVisibility:	function(selector) {
			var snocrossSelect = jQuery(selector);
			if (snocrossSelect != null && snocrossSelect.length > 0) {
				var snocrossInfoText = snocrossSelect.siblings(".race_number_info");
				var selectedValue = snocrossSelect.find("option:selected").text();
				if (selectedValue != null && snocrossInfoText != null && snocrossInfoText.length > 0) {
					if (selectedValue > 100) {
						snocrossInfoText.hide();
					} else {
						snocrossInfoText.show();
					}
				} else {
					snocrossInfoText.hide();
				}
			}
		}
}