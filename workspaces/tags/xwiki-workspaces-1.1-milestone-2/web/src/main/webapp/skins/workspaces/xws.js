/**
 * XWiki URL Builders 
 */
function getSkinFileURL(filename)
{
	var result = "/xwiki/skins/workspaces/" + filename;
	//return getSkinFileURL(skin,filename);	
	return result;
}

function getXWikiURL(space, page) {
  return getXWikIURL(wikiPage, "", "");
}
function getXWikiURL(space, page, mode) {
  return getXWikIURL(space, page, mode, "");
}
function getXWikiURL(space, page, mode, args) {
  var surl = "/xwiki/bin/" + mode + "/" + space + "/" + page;
  if (args != "")
    surl = surl + "?" + args;
  return surl;
}

 
function getAbsolutePos(element) {
  var SL = 0, ST = 0;
  var is_div = /^div$/i.test(element.tagName);
  if (is_div && element.scrollLeft) {
    SL = element.scrollLeft;
  }
  if (is_div && element.scrollTop) {
    ST = element.scrollTop;
  }
  var r = { x: element.offsetLeft - SL, y: element.offsetTop - ST };
  if (element.offsetParent) {
    var tmp = this.getAbsolutePos(element.offsetParent);
    r.x += tmp.x;
    r.y += tmp.y;
  }
  return r;
}

function setLoadingBg(id, state) {
  if (state == true) {
    $(id).style.background = "url(" + getSkinFileURL('images/lb-loader.gif') + ") center no-repeat"
    $(id).innerHTML = "";
  } else {
    $(id).style.background = "";
  }
}
