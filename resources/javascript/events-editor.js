(function($) {
	var MAIN_CLASS = 'events-editor';
	var MAIN_SELECTOR = '.' + MAIN_CLASS;
	var ACTIVE_SELECTOR = '.active-events';
	var INACTIVE_SELECTOR = '.inactive-events';
	var enableEvents = function(){
		var editor = $(this).parents(MAIN_SELECTOR).first();
		var select = editor.find(INACTIVE_SELECTOR);
		var selected = select.find(':selected');
		var ids = [];
		for(var i = 0;i < selected.length;i++){
			ids.push($(selected[i]).val());
		}
		RaceBusiness.enableEvents(ids, {
			callback: function(ids) {
				if(!ids){
					return;
				}
				var editor = $(MAIN_SELECTOR);
				var active = editor.find(ACTIVE_SELECTOR);
				var inactive = editor.find(INACTIVE_SELECTOR);
				for(var i = 0;i < ids.length;i++){
					var option = inactive.find('[value="'+ids[i]+'"]');
					active.prepend(option);
				}
			} 
		});
	}
	var disableEvents = function(){
		var editor = $(this).parents(MAIN_SELECTOR).first();
		var select = editor.find(ACTIVE_SELECTOR);
		var selected = select.find(':selected');
		var ids = [];
		for(var i = 0;i < selected.length;i++){
			ids.push($(selected[i]).val());
		}
		RaceBusiness.disableEvents(ids, {
			callback: function(ids) {
				if(!ids){
					return;
				}
				var editor = $(MAIN_SELECTOR);
				var active = editor.find(ACTIVE_SELECTOR);
				var inactive = editor.find(INACTIVE_SELECTOR);
				for(var i = 0;i < ids.length;i++){
					var option = active.find('[value="'+ids[i]+'"]');
					inactive.prepend(option);
				}
			} 
		});
	}
	$.fn.eventsEditor = function(options){
		return this.each(function(){
			var editor = $(this);
			editor.addClass(MAIN_CLASS);
			editor.find('.enable').click(enableEvents);
			editor.find('.disable').click(disableEvents);
		} ); 
	}
})(jQuery);