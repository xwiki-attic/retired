addListener(window, "resize", resized)
addListener(window, "load", load)

var slidenum = 0;
var slidetype = "undefined";
var slides = new Array();   // array of div element objects corresponding to slides

var ie = (typeof window.pageYOffset=='undefined');
var khtml = ((navigator.userAgent).indexOf("KHTML") >= 0 ? true : false);
var opera = ((navigator.userAgent).indexOf("Opera") >= 0 ? true : false);
var isIE = document.all;

var focus = null; // hack to track which object has the focus

function load()
{
  var editdoc = getIFrameDocument("editWindow");
   editdoc.designMode = "on";

  var editfrm = document.getElementById("editWindow");
  addListener(editfrm, "load", iframeLoad);

  if (ie)
    ieButtonBehavior();

  insertSlide("title");
}

// function for label to 'click' a checkbox
// pity we can't send click event to the checkbox!
function toggleViewSrc(id)
{
  var checkbox = document.getElementById(id);

  checkbox.checked = ! checkbox.checked;
  setViewMode(checkbox.checked);
}

function isWysiwygMode()
{
  var checkbox = document.getElementById("viewsrc");

  return !checkbox.checked;
}

function setViewMode(checked)
{
  var doc = getIFrameDocument("editWindow");

  if (checked)
  {
//    var text = doc.createTextNode(doc.body.innerHTML);
    var text = doc.createTextNode(prettyprint(doc.body));
    doc.body.innerHTML = "";
    var pre = doc.createElement("PRE");
    pre.setAttribute("contentEditable", "true");
    pre.appendChild(text);
    doc.body.appendChild(pre);
  }
  else // switch from source view to wysiwyg view
  {
    if (doc.all)  //IE Recipe //TODO: do we need this ??
    {
      var iText = doc.body.innerText;
      doc.body.innerHTML = iText;
    }
    else // mozilla recipe
    {
      var html = doc.body.ownerDocument.createRange();
      html.selectNodeContents(doc.body);
      doc.body.innerHTML = html.toString();
   }
  }
}

// adjust height of iframe to use remaining window space
function resized()
{
  var hdr = document.getElementById("header");
  var editfrm = document.getElementById("editWindow");

  // twiddle factor for different browsers
  // doesn't work for zoomed fonts, though
  // is there an event for change in zoom factor?
  var offset = (ie ? 20 : (opera ? 25 : 30) );

//  alert("height: " + getWindowHeight() - hdr.offsetHeight - offset);

  var height = getWindowHeight() - hdr.offsetHeight - offset;
  editfrm.height = height + "px";
}

function getDocHeight(doc) // from document object
{
  if (!doc)
    doc = document;

  if (doc && doc.body && doc.body.offsetHeight)
    return doc.body.offsetHeight;  // ns/gecko syntax

  if (doc && doc.body && doc.body.scrollHeight)
    return doc.body.scrollHeight;

  alert("couldn't determine document height");
}

function getWindowHeight()
{
  if ( typeof( window.innerHeight ) == 'number' )
    return window.innerHeight;  // Non IE browser

  if (document.documentElement && document.documentElement.clientHeight)
    return document.documentElement.clientHeight;  // IE6

  if (document.body && document.body.clientHeight)
    return document.body.clientHeight; // IE4
}

function getIFrame(aID)
{
  var ifrm = document.getElementById(aID);

  // for IE5 resort to frames array
  if (!ifrm)
    ifrm = document.frames[aID];

  return ifrm;
}

function getIFrameDocument(aID)
{
  var ifrm = getIFrame(aID);

  // IE win and Mozilla support contentWindow for window obj
  // for Safari 1.2.4 have to get contentDocument instead
  var doc = (ifrm.contentWindow.document || ifrm.contentDocument);

  // if we got the window, use it to get the document object
//   if (doc.document)
//     doc = doc.document;

  return doc;
}

function addListener(obj, name, handler)
{
  if (obj.addEventListener)
    obj.addEventListener(name, handler, false);
  else if (obj.attachEvent)
    obj.attachEvent("on" + name, handler);
	else 
		alert("Failed to attach event to object.");
}

// function getElementById(doc, id)
// {
//   if (doc.getElementById)
//     return doc.getElementById(id);
// 
// //This can be removed as document.getElementById is support by every browser after 1998(after IE4)
//   if (doc.all) 
//     return doc.all[id];
// 
//   return null;  
// }

function pprintChildren(content, indent, i)
{
  if (i < content.length)
    return pprint(content[i], indent, i) + pprintChildren(content, indent, i+1);

  return "";
}

// function nonEmpty(text)
// {
//   if (!text)
//     return false;
// 
//   var i, c;
// 
//   for (i = 0; i < text.length; ++i)
//   {
//     c = text.charAt(i);
// 
//     if (c == ' ' || c == '\t' || c == '\r' || c == '\n')
//       continue;
// 
//     return true;
//   }
// 
//   return false;
// }

function isWhite(c)
{
  return (c == ' ' || c == '\t' || c == '\r' || c == '\n')
}

function pptext(text)
{
  if (!text || text.length == 0)
    return "";

  while (text.length > 0 && isWhite(text.charAt(0)))
    text = text.substring(1, text.length);

  while (text.length > 0 && isWhite(text.charAt(text.length - 1)))
    text = text.substring(0, text.length - 1);

  return text;
}

function isBlock(name)
{
  return (name == "DIV" || name == "P" || name == "ADDRESS" ||
     name == "H1" ||  name == "H2" || name == "H3" || name == "H4" ||
     name == "H5" || name == "H6" || name == "TABLE" ||
     name == "FORM" || name == "UL" || name == "OL" || name == "LI" ||
     name == "DL" || name == "DT" || name == "DD");
}

// pretty print DOM tree to text property on out object
// knows about which html elements are inline or block
// and that hr, br and img are special empty elements
function pprintContent(out, content, indent) 
{
  for (var i = 0; i < content.length; ++i)
    pprintNode(out, content[i], indent);
}


function pprintNode(out, node, indent)
{
  if (node.nodeType == 1) // element node
  {
    if (isBlock(node.nodeName))
    {
      out.text += "\r\n" + indent;
      pprintElement(out, node, indent);
      out.text += "\r\n";
    }
    else
      pprintElement(out, node, indent);
  }
  else if (node.nodeType == 3) // text node
  {
      // strip leading and trailing whitespace
      out.text += pptext(node.nodeValue);
  }
}

function pprintElement(out, node, indent)
{
  out.text += "<" + node.nodeName;

  var i, attribute;

  for (i = 0; i < node.attributes.length; ++i)
  {
    attribute = node.attributes.item(i);

    if (attribute.value != "null" && attribute.value != "")
      out.text += " " + attribute.name + '="' + attribute.value + '"';
  }

  out.text += ">";
  pprintContent(out, node.childNodes, indent + "  ");

  if (isBlock(node.nodeName))
    out.text += indent;

  out.text += "</" + node.nodeName + ">";
}

function prettyprint(node)
{
  var obj = new Object();
  obj.text = "";
  pprintContent(obj, node.childNodes, "");
  return obj.text;
}

function help()
{
  alert("not yet implemented");
}

function goto()
{
  alert("not yet implemented");
}

function prev()
{
  alert("not yet implemented");
}

function next()
{
  alert("not yet implemented");
}

function test()
{
  var doc = getIFrameDocument("editWindow");
  alert(prettyprint(doc.body));
}

function syncSlideNum()
{
  var status = document.getElementById("slidenum");
  status.innerHTML = "Slide " + slidenum + "/" + slides.length;
//  var kind = document.getElementById("slidetype");
//  kind.innerHTML = slidetype;
}

// relies on load event on iframe to init edit doc event handlers
function insertSlide(kind)
{
  var editfrm = document.getElementById("editWindow");

  if (!editfrm)
    alert("couldn't find editor iframe object");

  if (kind == "title")
    editfrm.src ="/xwiki/presentation/title.html";
  else if (kind == "regular")
    editfrm.src = "/xwiki/presentation/regular.html";

  // set selection field back to "insert slide"
  var select = document.getElementById("insert");
  select.selectedIndex = 0;
}

function isEditable(node)
{
  if (!node)
    return false;

  if (node.nodeType == 1)
  {
    var editable = node.getAttribute("contentEditable");

    if (editable)
    {
      if (editable == "true")
      {
        //node.designMode = "on";
        return true;
      }
      //node.designMode = "off";
      return false;
    }
  }

  return isEditable(node.parentNode);
}

function setContentHandlers(doc, node)
{
  if (!node)
    return;

  if (node.nodeType == 1)
  {
    if (isEditable(node))
    {
//     alert("setting event handers on " + node.nodeName);

      addListener(node, "focus", documentFocus);
      addListener(node, "focusout", documentUnfocus);
      addListener(node, "keypress", keypress);

      //addListener(node, "DOMFocusOut", documentUnfocus);
      //node.addEventListener("DomFocusIn", documentFocus, false);
      //node.addEventListener("DomFocusOut", documentUnfocus, false);
    }

    for (var i = 0; i < node.childNodes.length; ++i)
      setContentHandlers(doc, node.childNodes[i]);
  }
}

function initSlide()
{
  var doc = getIFrameDocument("editWindow");
  doc.designMode = "on"; //Firefox 2 Hack
  //var win = document.getElementById("editWindow").contentWindow; //a:Can be removed

//TODO No need of adding following two listeners for IE
  addListener(doc, "mouseup", documentClick); //FF2 (both focus and focusout)
  addListener(doc, "keypress", documentKeyPress); //FF2

//   addListener(doc, "click", documentFocus);
//     addListener(doc, "unfocus", documentUnfocus);
//   addListener(win, "DOMFocusOut", documentUnfocus);

  setContentHandlers(doc, doc.body); //FF2 is not listening from events designed in this function

  var checkbox = document.getElementById("viewsrc");

  if (checkbox.checked)
    setViewMode(checkbox.checked);
}

//FF2
function documentKeyPress(e)
{
  if (!e)
    var e = window.event;

  var target = getTarget(e);

//  alert("you clicked on " + target.nodeName +
//     " with parent node " + target.parentNode.nodeName);

  // simulate a keypress event on target
  var e = new Object();
  e.target = target;
  keypress(e);
}

//FF2
function documentClick(e)
{
  if (!e)
    var e = window.event;

  var target = getTarget(e);
  var doc = getIFrameDocument("editWindow");

  //Send documentFocus to focused element and documentUnfocus to the rest.
  for (var i = 0; i < doc.body.childNodes.length; ++i)
  {
    node = doc.body.childNodes[i];
    if (node.nodeType == 1)
    {
      if (isEditable(node))
      {
         var e = new Object();
         e.target = node;
         if (node.id == "points") {
         } else if (node == target) {
           documentFocus(e);
         } else if (node.focusedOnce) {
            documentUnfocus(e);
         }
     }
     }
  }
//  addListener(target, "focus", documentFocus);
//  addListener(target, "focusout", documentUnfocus);

//  alert("you clicked on " + target.nodeName +
//     " with parent node " + target.parentNode.nodeName);

  // simulate a focus event on target
}

function hasFocus()
{
  var edoc = getIFrameDocument("editWindow");
  alert(edoc.activeElement.nodeName + " has the focus");
}

function iframeLoad(e)
{
  initSlide();

  resized();
  syncSlideNum();
}

function getTarget(e)
{
  var target; 
  if (e.target)
   target = e.target; //w3c/netscape
  else if (e.srcElement)
    target = e.srcElement; //IE

  // work around Safari bug
  if (target.nodeType == 3)
    target = target.parentNode;

  return target;
}

function documentFocus(e)
{
  var target;

  if (!e)
    var e = window.event;

  target = getTarget(e);
  target.style.color = "#000000"; //a:Text color
  focus = target; //a:HTML Heading element
  target.focusedOnce = true;
  if (!target.used && target.getAttribute("contentEditable") == "true")
  {
    if (!target.saved) {
      target.saved = target.innerHTML; //Saving the text for use later.
     } 
    target.innerHTML = (ie ? "" : "&nbsp;"); //Disabling the written content( "Click here to add title" etc.)
  }

  dismisscolorpalette(); 
//  alert(target.nodeName + " has got the focus");
//   window.status = target.nodeName + " got the focus";
}

function documentUnfocus(e)
{
  var target;
  if (!e)
    var e = window.event;
  target = getTarget(e)
  if (target && target.getAttribute("contentEditable") == "true")
  {
    if (!target.used)
    {
      target.style.color = "#C0C0C0";
      target.innerHTML = target.saved;
    }
  }

//  if (!target.innerHTML || target.innerHTML == "&nbsp;")
//  {
//    target.innerHTML = el.initialText;
//    target.style.color = "#C0C0C0";
//  }
//  else
//    target.updated = true;

  window.status = target.nodeName + " lost the focus";
}

function blur(e)
{
  var target;

  if (!e)
    var e = window.event;

  if (e.target)
    target = e.target;
  else if (e.srcElement)
    target = e.srcElement;

  if (!target) alert("focus: target is null");

  // work around Safari bug
  if (target.nodeType == 3)
    target = target.parentNode;

  window.status = target.nodeName + " has lost the focus";
}

function keypress(e)
{
  if (!e)
    e = window.event;

  var key = (e.keyCode ? e.keyCode : e.which);

  if (focus)
  {
    focus.used = true;
//    window.status = focus.nodeName + "marked as used";
  }
/*
  var mod = (e.altKey ? "Alt " : "");
  if (e.ctrlKey) mod += "Ctrl ";
  if (e.shiftKey) mod += "Shift ";

  // ignore Ctrl, Shift and Alt as keydown events

  // default handling for Alt can't be inhibited
  // IE and Mozilla key code 20 for Caps lock
  // IE key code 46 for Delete, and 190 for '.'
  // but sometimes Delete is not passed on
  // Mozilla uses key code 46 for both Delete and '.'
  // Esc is key code 27 as expected.
  // cursor up is keycode 38
  // cursor left is keycode 37
  // cursor right is keycode 39
  // cursor down is keycode 40
  // home is keycode 36
  // end is keycode 35
  // Pg Up is keycode 33
  // Pg Dn is keycode 34
  // Insert is keycode 45

  if (key != 17 && key != 16 && key != 18 && key != 20)
    window.status += "["+ mod  + key + "]";
*/

/*
  var c = String.fromCharCode(key);

  if (32 <= key && key < 127)
    window.status += c;
  {
    if (key == 8)
      window.status += "\\b";
    else if (key == 10)
      window.status += "\\n";
    else if (key == 13)
      window.status += "\\r";
    else if (key == 9)
      window.status += "\\t";
    else if (key == 127)
      window.status += "\\d";
  }

  // hide key on Mozilla (works on keypress not keydown)

  if (e.preventDefault)
    e.preventDefault();

  // hide key on Internet Explorer
  return false;
*/
  return true;
}

function applyCommand(aName)
{
  getIFrameDocument('editWindow').execCommand(aName,false, null);
  document.getElementById('editWindow').contentWindow.focus();  //??
}

function setMode(aName)
{
  var button = document.getElementById(aName);
  var editor = getIFrameDocument('editWindow')

  if (!editor) alert("Can't find edit iframe document");
  editor.execCommand(aName,false, null);

  if (editor.queryCommandState(aName))
    button.style.backgroundColor = "black";
  else
    button.style.backgroundColor = "#C0C0C0";
}

function setJustify(mode)
{
	var left = document.getElementById("justifyleft");
	var center = document.getElementById("justifycenter");
	var right = document.getElementById("justifyright");
	var full = document.getElementById("justifyfull");
	var editor = getIFrameDocument('editWindow')

	editor.execCommand(mode,false,null);

	if (mode == "justifyleft")
	{
		left.style.backgroundColor = "#000000";
		center.style.backgroundColor = "#C0C0C0";
		right.style.backgroundColor = "#C0C0C0";
		full.style.backgroundColor = "#C0C0C0";
	}
	else if (mode == "justifycenter")
	{
		left.style.backgroundColor = "#C0C0C0";
		center.style.backgroundColor = "#000000";
		right.style.backgroundColor = "#C0C0C0";
		full.style.backgroundColor = "#C0C0C0";
	}
	else if (mode == "justifyright")
	{
		left.style.backgroundColor = "#C0C0C0";
		center.style.backgroundColor = "#C0C0C0";
		right.style.backgroundColor = "#000000";
		full.style.backgroundColor = "#C0C0C0";
	}
	else if (mode == "justifyfull")
	{
		left.style.backgroundColor = "#C0C0C0";
		center.style.backgroundColor = "#C0C0C0";
		right.style.backgroundColor = "#C0C0C0";
		full.style.backgroundColor = "#000000";
	}
}

function addLink()
{
	var myUrl = prompt("Please enter a URL:", 'http://');
	getIFrameDocument("editWindow").execCommand("createLink", false, myUrl);
}

function addImage()
{
	var imagePath = prompt('Enter Image URL:', 'http://');
	if ((imagePath != null) && (imagePath != ""))
		getIFrameDocument("editWindow").execCommand('InsertImage', false, imagePath);
}

function addTable()
{
	alert("Sorry, tables are not yet supported!");
}

function isdefined(variable)
{
	return (typeof(window[variable]) == "undefined")?  false: true;
}

//id is forecolor and hilitecolor
function setColor(id)
{
	command = id;
	var palette = document.getElementById("colorpalette");

	var buttonElement = document.getElementById(id);
	var leftOffset = getOffsetLeft(buttonElement);

	if (palette.style.visibility == "visible" && palette.style.left == leftOffset + "px") {
		palette.style.visibility = "hidden"; //Already visible for same command.
		return;
	}

	palette.style.left = leftOffset;
	palette.style.width = "222px";
	palette.style.top = getOffsetTop(buttonElement) + buttonElement.offsetHeight;
	palette.style.height = "160px";

	palette.style.visibility="visible";
}

function applyColor(color)
{
	// show selected color in transparent area of button image
	document.getElementById(command).style.backgroundColor = color;

	// IE uses BackColor for the current selection whereas Mozilla
	// uses BackColor for the page and hilitecolor for the selection
	if (isIE && command == "hilitecolor")
		command = "BackColor";

	if (command == "forecolor")
		command = "ForeColor";

	getIFrameDocument('editWindow').execCommand(command, false, color);

	document.getElementById("colorpalette").style.visibility="hidden";
	document.getElementById("editWindow").contentWindow.focus();
}

function dismisscolorpalette()
{
  document.getElementById("colorpalette").style.visibility="hidden";
}

function getOffsetLeft(elem)
{
  var mOffsetLeft = elem.offsetLeft;
  var mOffsetParent = elem.offsetParent;

  while(mOffsetParent)
  {
    mOffsetLeft += mOffsetParent.offsetLeft;
    mOffsetParent = mOffsetParent.offsetParent;
  }
 
  return mOffsetLeft;
}

function getOffsetTop(elem)
{

  var mOffsetTop = elem.offsetTop;
  var mOffsetParent = elem.offsetParent;

  while(mOffsetParent)
  {
    mOffsetTop += mOffsetParent.offsetTop;
    mOffsetParent = mOffsetParent.offsetParent;
  }
 
  return mOffsetTop;
}

////////////////////////////////////////////////////////////////////////////////////////////
     
function doRichEditCommand(aName, aArg)
{
  getIFrameDocument('editWindow').execCommand(aName,false, aArg);
  document.getElementById('editWindow').contentWindow.focus()
} 

// view source
function getSource()
{
  document.getElementById("sourceView").innerHTML =
    replace(getIFrameDocument("editWindow").body.innerHTML,"<","&lt;");
}

function replace(string,text,by)
{
  var stringLength = string.length
  var textLength = text.length;

  if ((stringLength == 0) || (textLength == 0))
    return string;

  var i = string.indexOf(text);

  if ((!i) && (text != string.substring(0,textLength)))
    return string;

  if (i == -1)
	return string;

  var newstr = string.substring(0,i) + by;

  if ( (i+textLength) < stringLength)
	newstr += replace(string.substring(i+textLength,stringLength),text,by);

  return newstr;
}



// work around IE's lack of support for hover etc.
function ieButtonBehavior()
{
	var buttons = document.getElementsByTagName("IMG");
	var button;

	for (var i = 0; i < buttons.length; ++i)
	{
		button = buttons[i];

		if (button.getAttribute("className") == "button")
		{
			button.onmouseover = hoverButton;
			button.onmouseout  = normalButton;
			button.onmousedown = activeButton;
			button.onmouseup   = hoverButton;
		}
	}
}

function normalButton(e)
{
	var button = window.event.srcElement;

	// border: 1px solid #C0C0C0
	button.style.borderStyle = "solid";
	button.style.borderWidth = "2px";
	button.style.borderColor = "#C0C0C0";
}

function hoverButton(e)
{
	var button = window.event.srcElement;

	// border: 1px outset white
	button.style.borderStyle = "outset";
	button.style.borderWidth = "2px";
	button.style.borderColor = "white";
}

function activeButton(e)
{
	var button = window.event.srcElement;

	// border: 1px inset white
	button.style.borderStyle = "inset";
	button.style.borderWidth = "2px";
	button.style.borderColor = "white";
}
                       
