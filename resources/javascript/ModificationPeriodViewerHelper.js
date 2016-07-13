jQuery(document).ready(function() {
	jQuery(".windowLink").fancybox({
		type:		"iframe",
		autoSize:	false,
		beforeShow:	function () {
			var iframe = jQuery(".fancybox-iframe");
			if (iframe != null && iframe.length > 0) {
				this.height = jQuery(iframe).contents().find('.editorTable').height() + 160;
				this.width = jQuery(iframe).contents().find('.editorTable').width() + 40;

				jQuery(iframe).load(function() {
					var saved = jQuery(this).contents().find(".savedText");
					if (saved != null && saved.length > 0) {
						parent.jQuery.fancybox.close();

						// On Firefox uncheck edit -> preferences -> advanced -> Tell me when a website...
						parent.location.reload(true);
					}
				});
			}
		}
	});
	
	LazyLoader.loadMultiple(['/idegaweb/bundles/com.idega.block.web2.0/resources/javascript/tablesorter/jquery.tablesorter.min.js'], function() {
		$("#editorTable").tablesorter(); 
	}, null);
});

var ModificationPeriodViewerHelper = {
		remove : function (row) {
			if (row == null || row.length <= 0 || row.nodeName != "TR") {
				return;
			}

			/* 
			 * I don't wait for callback. What is the point in waiting? 
			 * If this won't work, then we have a bug to be fixed
			 */
			var id = jQuery("input:hidden", row).val();
			ModificationPeriodDAO.remove(id);
			jQuery(row).remove();
		}
}