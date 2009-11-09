//Please note that this is for testing purposes
//insert The following function in xwiki.js just below the displayDocExtra()
/*
 displayPageContent: function (dtAnchor,url, scrollToAnchor) {	   
	 var dhtmlSwitch = function(dtAnchor) {
	    var tab = document.getElementById(dtAnchor + "tab");
	    var pane = document.getElementById(dtAnchor + "pane");
	    if (window.activeDtmTab != null) {
	        window.activeDtmTab.className="";
	        window.activeDtmPane.className="hidden";
	    }
	    window.activeDtmTab = tab;
	    window.activeDtmPane = pane;
	    window.activeDtmTab.className="active";
	    window.activeDtmPane.className="";
	    tab.blur();
        document.fire("xwiki:dtm:activated", {"id": dtAnchor});
	 };  
	 if ($(dtAnchor + "pane").className.indexOf("empty") != -1) {
	    if (window.activeDtmPane != null) {
	        window.activeDtmPane.className="invisible";
	    }	    
	    new Ajax.Updater(
	    		dtAnchor + "pane",
	            url+"?xpage=plain",
	            {
	                method: 'post',
	                evalScripts: true,
	                onComplete: function(transport){            
	                    document.fire("xwiki:dtm:loaded", {
	                    "id" : dtAnchor,
	                    "element": $(dtAnchor + "pane")
	                  }); 
	                  dhtmlSwitch(dtAnchor);
	                  if (scrollToAnchor) {	                     
	                    $(dtAnchor + 'anchor').id = dtAnchor;
	                    location.href='#' + dtAnchor;
	                    $(dtAnchor).id = dtAnchor + 'anchor';
	                  }
	                }
	            });
	 } else {
	    dhtmlSwitch(dtAnchor);
	    if (scrollToAnchor) {
	        $(dtAnchor + 'anchor').id = dtAnchor;
	        location.href='#' + dtAnchor;
	        $(dtAnchor).id = dtAnchor + 'anchor';
	    }
	 }
  },
 */
 