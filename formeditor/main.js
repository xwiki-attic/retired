var cellCounter;
var groupsCounter;
var selectedCell;
var cellWidth;
var newFieldsCounter;
var descriptor;
var inputForm;
function init(availableFields, xWikiClassName, oldForm, inputDescriptor) {
	cellCounter = 0;
	groupsCounter = 0;
	selectedCell = null;
	cellWidth = 140;
	newFieldsCounter = 0;
	descriptor = inputDescriptor;
	inputForm = oldForm;
	

	var fieldContainers = document.getElementsByClassName('fieldContainer');
	$("fields").xWikiClassName = xWikiClassName;
	var tbdFields = $('tbdFields');
	var colsCount = 2;
	var rowsCount = availableFields.length;
	tbdFields.colsCount = colsCount;
	var fieldsCellCounter = 0;
	for (var j = 0; j < rowsCount; j++) {
		var trw = document.createElement('tr');
		tbdFields.appendChild(trw);
		for (var k = 0; k < colsCount/2; k++) {
			if (availableFields.length <= fieldsCellCounter)
				break;
			var td = document.createElement('td');
			td.style.width = "100px";
			var d = document.createElement('div');
			d.className = 'fieldContainer';
			d.draggable = new Draggable(d, {revert: true});
			var i = document.createElement('img');
			i.src = '/xwiki/bin/download/Main/WebHome/TextLabel.png';
			d.appendChild(i);
			d.appendChild(document.createElement('br'));
			d.appendChild(document.createTextNode(availableFields[fieldsCellCounter].prettyName))
			d.fieldModel = availableFields[fieldsCellCounter].fieldModel;
			d.isPrettyName = true;
			d.fieldType = availableFields[fieldsCellCounter].type;
			d.fieldName = availableFields[fieldsCellCounter].name;
			d.fieldPrettyName = availableFields[fieldsCellCounter].prettyName;
			d.id = d.fieldName + "|" + d.isPrettyName;
			td.appendChild(d);
			trw.appendChild(td);

			var td = document.createElement('td');
			td.style.width = "100px";
			var d = document.createElement('div');
			d.className = 'fieldContainer';
			d.draggable = new Draggable(d, {revert: true});
			var i = document.createElement('img');
			i.src = '/xwiki/bin/download/Main/WebHome/' + availableFields[fieldsCellCounter].type + '.png';
			d.appendChild(i);
			d.appendChild(document.createElement('br'));
			d.appendChild(document.createTextNode(availableFields[fieldsCellCounter].name))
			d.fieldModel = availableFields[fieldsCellCounter].fieldModel;
			d.isPrettyName = false;
			d.fieldType = availableFields[fieldsCellCounter].type;
			d.fieldName = availableFields[fieldsCellCounter].name;
			d.fieldPrettyName = availableFields[fieldsCellCounter].prettyName;
			d.id = d.fieldName + "|" + d.isPrettyName;
			td.appendChild(d);
			trw.appendChild(td);
			
			fieldsCellCounter++;
		}
		Droppables.add(tbdFields, {accept: ['fieldContainer'], 
									onDrop: function(droppedElement) {
										if (droppedElement.parentNode.className == 'divCell')
											emptyCell();
									}
								  });
	}

	var arrNewFields = [{type: 'String'},
						{type: 'Number'},
						{type: 'Password'},
						{type: 'Date'},
						{type: 'Boolean'},
						{type: 'StaticList'},
						{type: 'DBList'},
						{type: 'TextArea'}
						]
	var tbdNewFields = $('tbdNewFields');
	tbdNewFields.colsCount = colsCount;
	var rowsCount = parseInt(arrNewFields.length / colsCount) + 1;
	var fieldsCellCounter = 0;
	for (var j = 0; j < rowsCount; j++) {
		var trw = document.createElement('tr');
		tbdNewFields.appendChild(trw);
		for (var k = 0; k < colsCount; k++) {
			if (arrNewFields.length <= fieldsCellCounter)
				break;
			var td = document.createElement('td');
			td.style.width = "100px";
			var d = document.createElement('div');
			d.className = 'newFieldContainer';
			d.draggable = new Draggable(d, {revert: true});
			var i = document.createElement('img');
			i.src = '/xwiki/bin/download/Main/WebHome/' + arrNewFields[fieldsCellCounter].type + '.png';
			d.appendChild(i);
			td.appendChild(d);
			trw.appendChild(td);
			fieldsCellCounter++;
		}
	}

	if (oldForm != null) {
		var versionNum = inputForm.getElementsByTagName('version')[0].innerHTML;
		if (parseFloat(versionNum) == parseFloat(descriptor.version)) {
			loadForm();
		}
		else {
			alert('IMPORTANT! The form you wish to edit has been changed outside the XWiki FormEditor.\n \
					The FormEditor has saved the form as version: ' + versionNum + ' and the current version of the form is ' + descriptor.version + '. \n \
					Any saving of the form from the FormEditor will create new version of the form and outer changes will be deleted.');
		}
	}
}

function displayParams() {
	var val = $('selectParams').value;
	$('divGroupProperties').style.display = 'none';
	$('divRowProperties').style.display = 'none';
	$('divCellProperties').style.display = 'none';
	$('divFieldProperties').style.display = 'none';
	$('div' + val + 'Properties').style.display = 'inline';
}

function createTable(colsCount, rowsCount) {
	var divForm = $("divForm");
	if (colsCount > 6) {
		alert('Too many columns!')
		return;
	}
	if (colsCount == null || colsCount == "" || isNaN(colsCount) || rowsCount == null || rowsCount == "" || isNaN(rowsCount)) {
		alert('Invalid value(s)');
		return;
	}
	colsCount = colsCount == "" ? 2 : colsCount;
	rowsCount = rowsCount == "" ? 1 : rowsCount;

	var divGroup = document.createElement("div");
	var divPresText = document.createTextNode("New presentation text");
	var fstTable = document.createElement("fieldset");
	var title = document.createElement("legend");
	var divTable = document.createElement("div");
	divTable.colsCount = colsCount;
	divTable.rowsCount = rowsCount;

//	divGroup.appendChild(document.createElement("hr"));
	fstTable.appendChild(title);
	fstTable.appendChild(divTable);
	divGroup.appendChild(divPresText);
	divGroup.appendChild(fstTable);

	divGroup.className = "group";
//	divPresText.className = "groupPresentationText";
	fstTable.className = "fstTable";
	title.className = "groupTitle";
	divTable.className = "divTable";

	title.innerHTML = "New Group " + groupsCounter++;
//	divPresText.innerHTML = "New presentation text";

	divForm.appendChild(divGroup);
	divTable.style.width = Number(colsCount * cellWidth) + "px";
	// mora da bude 0 da bi bilo najmanje moguce sirine (tj. da se ponasa u odnosu na tabelu)
	fstTable.style.width = "0px";
	for (var i = 0; i < rowsCount; i++) {
		var divRow = document.createElement("div");
		divRow.className = "divRow";
		divRow.style.cssFloat = "";
		divRow.style.border = "none";
		divRow.style.width = "100%";
		divRow.style.padding = "0px";
		divRow.style.margin = "1px";
		divTable.appendChild(divRow);
		for (var j = 0; j < colsCount; j++) {
			var d = document.createElement("div");
			d.id = ++cellCounter;
			d.className = "divCell";
			d.style.height = "95%"
			d.style.width = Number(100 / colsCount * 0.98) + "%";
			d.style.margin = "0px";
			d.spannedCount = 1;
			divRow.appendChild(d);
			Droppables.add(d, {accept: ['fieldContainer', 'newFieldContainer'], onDrop: onFieldDrop, hoverclass: "divCellHover"})
			d.onclick = selectCell;
		}
	}
	divGroup.appendChild(document.createElement('br'));
	$('velocityZone').style.display = 'none';
	$('previewForm').style.display = 'none';
	$('divForm').style.display = 'inline';
	return divGroup;
}

function onFieldDrop(droppedElement, cell) {
	if (droppedElement.parentNode == cell)
		return;
	// ako je celija zauzeta, trazi prazno mesto ili je obrisi
	if (cell.isOccupied == true) {
		var emptyCellExists = false;
		var tmpcell = cell.nextSibling;
		// ispituje da li u redu postoji prazna celija
		while (!emptyCellExists && tmpcell != null) {
			if (tmpcell.firstChild == null)
				emptyCellExists = true;
			else
				tmpcell = tmpcell.nextSibling;
		}
		// ako nema prazna celija, onda obrisi onu na koju se dropuje i nastavi sa dodavanjem
		if (!emptyCellExists) {
			if (droppedElement.parentNode.parentNode.parentNode.id == 'tbdFields')
				droppedElement.parentNode.removeChild(droppedElement)
//			alert("The content of this cell will be deleted!")
			emptyCell(cell);
		}
		// ako ima prazna, siftuj nadesno od trenutne do prazne (petlja ide obrnutim redom)
		else {
			var row = cell.parentNode;
			for (var el = tmpcell; el != cell; el = el.previousSibling) {
				el.appendChild(el.previousSibling.firstChild);
				el.isOccupied = true;
			}
		}
	}
	
	if (droppedElement.className == 'fieldContainer') {
//		if (droppedElement.fieldModel.size > 15 
//			|| (droppedElement.fieldModel.cols != null 
//				&& droppedElement.fieldModel.cols > 15)
//			&& !cell.isSpanned) {
//			alert('This field is bigger than the selected cell.\n Please increase the cell size by spanning.')
//			return;
////			selectGivenCell(cell);
////			spanGivenCell(cell);
//		}
		var tmp = document.createElement('img');
		tmp.className = 'fieldContainer';
		tmp.src = droppedElement.firstChild.src;
		cell.isOccupied = true;
		cell.innerHTML = '';
		// pomera polje iz fields boxa
		if (droppedElement.parentNode != null) {
			droppedElement.parentNode.isOccupied = false;
			droppedElement.parentNode.innerHTML = '';
		}
		var elName = droppedElement.firstChild.src;
		var ind1 = elName.lastIndexOf('/') + 1;
		var ind2 = elName.lastIndexOf('.');
		elName = elName.substring(ind1, ind2);
	
		var d = document.createElement('div');
		d.appendChild(tmp);
		cell.appendChild(d);
		d.appendChild(document.createElement('br'));
		if (droppedElement.isPrettyName)
			d.appendChild(document.createTextNode(droppedElement.fieldPrettyName));
		else
			d.appendChild(document.createTextNode(droppedElement.fieldName));
		d.fieldModel = droppedElement.fieldModel;
		d.fieldName = droppedElement.fieldName;
		d.fieldPrettyName = droppedElement.fieldPrettyName;
		d.fieldType = droppedElement.fieldType;
		if (droppedElement.isPrettyName)
			d.fieldType = 'TextLabel';
		d.xwClassPropertyName = droppedElement.xwClassPropertyName;
		d.className = 'fieldContainer';
		d.draggable = new Draggable(d, {revert: true})
	}
	else if(droppedElement.className == 'newFieldContainer') {

//////// VAZNO - kreirati fieldModel onda kada se spusta novo polje		
		
		// kreiraj sliku za form edit mode
		var tmp = document.createElement('img');
		tmp.className = 'fieldContainer';
		tmp.src = droppedElement.firstChild.src;
		cell.isOccupied = true;
		cell.innerHTML = '';
		cell.appendChild(tmp);
		selectGivenCell(cell);
		var elName = droppedElement.firstChild.src;
		var ind1 = elName.lastIndexOf('/') + 1;
		var ind2 = elName.lastIndexOf('.');
		var elType = elName.substring(ind1, ind2);
	
		// dodaj ime i setuj ostale parametre polja
		var d = document.createElement('div');
		d.appendChild(tmp);
		cell.appendChild(d);
		d.appendChild(document.createElement('br'));
		d.appendChild(document.createTextNode('newField' + newFieldsCounter));
		d.fieldModel = {};
		d.fieldName = 'newField' + newFieldsCounter;
		d.fieldType = elType;
		d.xwClassPropertyName = 'newField' + newFieldsCounter;
		d.className = 'fieldContainer';
		d.draggable = new Draggable(d, {revert: true})
		
		// dodaj pretty name u tabelu za class fields
		var tbdFields = $('tbdFields');
		// kreiraj polje za pretty name
		var d = document.createElement('div');
		d.className = 'fieldContainer';
		d.draggable = new Draggable(d, {revert: false});
		var i = document.createElement('img');
		i.src = '/xwiki/bin/download/Main/WebHome/TextLabel.png';
		d.appendChild(i);
		d.appendChild(document.createElement('br'));
		d.appendChild(document.createTextNode('newField' + newFieldsCounter + 'PrettyName'))
		d.fieldType = 'TextLabel';
		d.fieldName = 'newField' + newFieldsCounter + 'PrettyName';
		d.xwClassPropertyName = 'newField' + newFieldsCounter;

		putFieldInFieldsBox(d);
		newFieldsCounter++;
	}

	selectGivenCell(cell);
}

// GENERISI PREVIEW FORM
function generateFormPreview() {
	var divPreview = $('previewForm');
	if (divPreview.style.display != 'none')
		return;

	divPreview.innerHTML = '';
	var groups = document.getElementsByClassName('group');
	var titles = document.getElementsByClassName('groupTitle');
	var tmpdivs = document.getElementsByTagName('div');
//	var presTexts = [];
//	var presTexts = document.getElementsByClassName('groupPresentationText');
	var tables = document.getElementsByClassName('divTable');

	for (var i = 0; i < groups.length; i++) {
		var group = groups[i];
		var divGroup = document.createElement("div");
		var divPresText = document.createTextNode('');
		var fstTable = document.createElement("fieldset");
		var title = document.createElement("legend");
		var divTable = document.createElement("table");
		var divTBody = document.createElement("tbody");

		divTable.style.width = Number(tables[i].colsCount * cellWidth) + "px";
		divTable.setAttribute('cellspacing', 0);

		var c = 0;
		var tmpDivs = tables[i].getElementsByTagName('div');
		var rows = [];
		for (var j = 0; j < tmpDivs.length; j++) {
			if (tmpDivs[j].parentNode == tables[i])
			rows.push(tmpDivs[j]);
		}

		for (var j = 0; j < rows.length; j++) {
			var trw = document.createElement('tr');
			divTBody.appendChild(trw);
			if (rows[j].nextSibling != null && rows[j].nextSibling.nodeName == "HR") {
				var hr = document.createElement('hr');
				hr.style.width = Number(tables[i].colsCount * 100) + "%";
				divTBody.appendChild(hr);
			}
			trw.style.height = rows[j].offsetHeight + "px";
			var defaultWidth = 100 / tables[i].colsCount;
			for (var k = 0; k < rows[j].childNodes.length; k++) {
				var td = document.createElement("td");
				trw.appendChild(td);
				td.className = 'cellPreview';
				var tmpCellWidth = parseInt(rows[j].childNodes[k].style.width);
				if (tmpCellWidth > defaultWidth) {
					var colspan = Math.ceil(tmpCellWidth / defaultWidth);
					td.setAttribute('colspan', colspan)
				}

				if (rows[j].childNodes[k].isOccupied)
					td.appendChild(generateField(rows[j].childNodes[k]));
				else
					td.innerHTML = rows[j].childNodes[k].innerHTML;
//				td. style.width = '';
			}
		}

		fstTable.style.width = "0px";

		divTable.appendChild(divTBody);
		fstTable.appendChild(title);
		fstTable.appendChild(divTable);
		divGroup.appendChild(divPresText);
		divGroup.appendChild(fstTable);

		title.innerHTML = titles[i].innerHTML;
		divPresText.data = getPresentationTextFromGroup(groups[i]);
//		divPresText.innerHTML = presTexts[i].innerHTML;

//		divPreview.appendChild(document.createElement("hr"));
		divPreview.appendChild(divGroup);
	}
	$('velocityZone').style.display = 'none';
	$('previewForm').style.display = 'inline';
	$('divForm').style.display = 'none';
}

function generateField(model) {
	var fieldType = model.firstChild.fieldType;
	var fieldWidth = "80%";
	switch(fieldType) {
		case 'String':
		case 'Number':
		case 'Date':
		var toReturn = document.createElement('input');
		break;
		case 'Password':
		var toReturn = document.createElement('input');
		toReturn.type = 'password';
		break;
		case 'Boolean':
		var toReturn = document.createElement('select');
		var optionYes = document.createElement('option');
		optionYes.innerHTML  = 'yes';
		var optionNo = document.createElement('option');
		optionNo.innerHTML  = 'no';
		toReturn.appendChild(optionYes);
		toReturn.appendChild(optionNo);
		break;
		case 'StaticList':
		case 'DBList':
		var toReturn = document.createElement('select');
		var optionYes = document.createElement('option');
		optionYes.innerHTML  = '1';
		var optionNo = document.createElement('option');
		optionNo.innerHTML  = '2';
		toReturn.appendChild(optionYes);
		toReturn.appendChild(optionNo);
		break;
		case 'TextArea':
		var toReturn = document.createElement("textArea");
		toReturn.rows = model.firstChild.fieldModel.size;
		break;
		case 'TextLabel':
		var toReturn = document.createElement('div');
		toReturn.innerHTML = model.firstChild.fieldPrettyName;
		break;
	}
	var fieldModel = model.firstChild.fieldModel;
	for (var p in fieldModel) {
		if (p == 'type')
			continue;
		toReturn[p] = fieldModel[p];
	}
	return toReturn;
}

// GENERISI VELOCITY
function generateVelocity() {
	var velocityZone = $('velocityZone');
	if (velocityZone.style.display != 'none')
	return;
	var velocityCode = document.createElement('textArea');
	velocityCode.cols = 60;
	velocityCode.rows = 20;
	velocityCode.id = 'velocityCode';
	velocityZone.innerHTML = '';
	velocityZone.appendChild(velocityCode);

	var groups = document.getElementsByClassName('group');
	var titles = document.getElementsByClassName('groupTitle');
//	var presTexts = document.getElementsByClassName('groupPresentationText');
	var tables = document.getElementsByClassName('divTable');
	$('velocityCode').innerHTML = '';
	var s = '';
	s += '$doc.use("' + $('fields').xWikiClassName + '")';
	s += '\n';
	s += '#set($classname = "' + $('fields').xWikiClassName + '")';
	s += '\n';
	s += '#set($class = $xwiki.getDocument($classname).xWikiClass)';
	s += '\n';
	
	s += '<div id="formVariables" style="display: none" >';
	s += '\n';
	var vers = String(descriptor.version);
	var ind1 = vers.indexOf('.') + 1;
	var vers1 = vers.substring(0, ind1);
	vers = vers.substring(ind1);
	vers = parseInt(vers) + 1;
	vers = vers1 + vers;
	s += '<version>' + vers + '</version>'
	s += '\n';
	s += '</div>';
	s += '\n';
	
	for (var i = 0; i < groups.length; i++) {
		s += '\n';
		s += getPresentationTextFromGroup(groups[i]); // divPresText, end divPresText
		s += '\n';
		var group = groups[i];
		s += '<fieldset style="width: 0px">'; //fstTable
		s += '\n';
		s += '<legend>' + titles[i].innerHTML + '</legend>'; // title, end title
		s += '\n';
		s += '<table cellspacing=0 style="width:' + Number(tables[i].colsCount * cellWidth) + 'px"><tbody>'; // divTable
		s += '\n';

		var tmpDivs = tables[i].getElementsByTagName('div');
		var rows = [];
		for (var j = 0; j < tmpDivs.length; j++) {
			if (tmpDivs[j].parentNode == tables[i])
			rows.push(tmpDivs[j]);
		}

		for (var j = 0; j < rows.length; j++) {
			s += '<tr style="height:' + rows[j].offsetHeight + 'px">'; // trw
			s += '\n';
			if (rows[j].nextSibling != null && rows[j].nextSibling.nodeName == "HR") {
				s += '<hr style="width:' + Number(tables[i].colsCount * 100) + '%;">'
				s += '\n';
			}
			for (var k = 0; k < rows[j].childNodes.length; k++) {
				var defaultWidth = 100 / tables[i].colsCount;
				var tmpCellWidth = parseInt(rows[j].childNodes[k].style.width);
				var colspan = Math.ceil(tmpCellWidth / defaultWidth);
				s += '<td class="cellPreview" colspan=' + colspan + '>'; // td
				if (rows[j].childNodes[k].isOccupied) {
					if (rows[j].childNodes[k].firstChild.fieldType == "TextLabel") {
						s += '$class.get("' + rows[j].childNodes[k].firstChild.fieldName + '").prettyName'
					}
					else {
						s += '$doc.display("' + rows[j].childNodes[k].firstChild.fieldName + '")';
					}
				}
				else {
					s += rows[j].childNodes[k].innerHTML;
				}
				s += '</td>'; // end td
				s += '\n';
			}
			s += "</tr>" // end trw
			s += '\n';
		}

		s += '</tbody></table>'
		s += '\n';
		s += '</fieldset>'; // end fstTable
		s += '\n';
	}
	s += '\n';
	$('velocityCode').innerHTML = s;

	$('velocityZone').style.display = 'inline';
	$('previewForm').style.display = 'none';
	$('divForm').style.display = 'none';
	
//	generateFormModel();
}

function viewMode() {
	$('velocityZone').style.display = 'none';
	$('previewForm').style.display = 'none';
	$('divForm').style.display = 'inline';
}

function switchFieldsSource(sourceToDisplay) {
//	if ($('fields').style.display != 'none') {
	if (sourceToDisplay != 'classFields') {
		$('fields').style.display = 'none';
		$('newFields').style.display = '';
	}
	else if (sourceToDisplay != 'newFields') {
		$('fields').style.display = '';
		$('newFields').style.display = 'none';
	}
}

function putFieldInFieldsBox(field) {
	var tbdFields = $('tbdFields');
	var tds = tbdFields.getElementsByTagName('td');
	for (var i = 0; i < tds.length; i++) {
		if (tds[i].firstChild == null) {
			tds[i].appendChild(field);
			return true;
		}
	}
	// ako nema praznog td, kreiraj ga
	var td = document.createElement('td');
	td.appendChild(field);
	// ako ima prazno mesto u redu za prazan td dodaj ga, ako ne, dodaj nov red (tr) u tabelu
	if (tds.length % $('tbdFields').colsCount == 0) {
		var trw = document.createElement('tr');
		tbdFields.appendChild(trw);
		trw.appendChild(td);
	}
	else {
		tds[tds.length-1].parentNode.appendChild(td);
	}
	return true;
}

function generateFormModel() {
	var velocityZone = $('velocityZone');
//	if (velocityZone.style.display != 'none')
//		return;

	var groups = document.getElementsByClassName('group');
	var titles = document.getElementsByClassName('groupTitle');
	var presTexts = document.getElementsByClassName('groupPresentationText');
	var tables = document.getElementsByClassName('divTable');
	var s = '';
	s += 'XWikiClassName:"' + $('fields').xWikiClassName + '"';
	s += '\n';
	for (var i = 0; i < groups.length; i++) {
		s += '\n';
		s += 'Horizontal break width: null';
		s += '\n';
		s += 'Presentation Text: "' + presTexts[i].innerHTML + '"'; // divPresText, end divPresText
		s += '\n';
		var group = groups[i];
		s += 'Fieldset'; //fstTable
		s += '\n';
		s += 'Title text: "' + titles[i].innerHTML + '"'; // title, end title
		s += '\n';
		s += 'Table width: "' + Number(tables[i].colsCount * cellWidth) + '"'; // divTable
		s += '\n';

		var tmpDivs = tables[i].getElementsByTagName('div');
		var rows = [];
		for (var j = 0; j < tmpDivs.length; j++) {
			if (tmpDivs[j].parentNode == tables[i])
			rows.push(tmpDivs[j]);
		}

		for (var j = 0; j < rows.length; j++) {
			s += 'Table row height: "' + rows[j].offsetHeight + '"'; // trw
			s += '\n';
			if (rows[j].nextSibling != null && rows[j].nextSibling.nodeName == "HR") {
				s += 'Horizontal break width: "' + Number(tables[i].colsCount * 100) + '"'
				s += '\n';
			}
			for (var k = 0; k < rows[j].childNodes.length; k++) {
				var defaultWidth = 100 / tables[i].colsCount;
				var tmpCellWidth = parseInt(rows[j].childNodes[k].style.width);
				var colspan = Math.ceil(tmpCellWidth / defaultWidth);
				s += 'Table cell colspan=: "' + colspan + '"'; // td
				s += '\n';
				if (rows[j].childNodes[k].isOccupied) {
					if (rows[j].childNodes[k].firstChild.fieldType == "TextLabel") {
						s += 'Pretty name: "' + rows[j].childNodes[k].firstChild.fieldName + '"'
					}
					else {
						s += 'Field: "' + rows[j].childNodes[k].firstChild.fieldName + '"';
					}
				}
				else {
					s += rows[j].childNodes[k].innerHTML;
				}
				s += '\n';
//				s += '</td>'; // end td
	//			s += '\n';
			}
		//	s += "</tr>" // end trw
			//s += '\n';
		}

//		s += '</tbody></table>'
	//	s += '\n';
		//s += '</fieldset>'; // end fstTable
		s += '\n';
	}
	s += '\n';
	alert(s);

	$('velocityZone').style.display = 'inline';
	$('previewForm').style.display = 'none';
	$('divForm').style.display = 'none';
}

function loadForm() {
	var groups = inputForm.getElementsByTagName('fieldset');
	for (var i = 0; i < groups.length; i++) {
		var trs = groups[i].getElementsByTagName('tr');
		var maxTds = 0;
		for (var j = 0; j < trs.length; j++) {
			var tds = trs[j].getElementsByTagName('td');
			var colspan = 0;
			for (var k = 0; k < tds.length; k++) {
				colspan += parseInt(tds[k].getAttribute('colspan'));
			}
			if (colspan > maxTds)
				maxTds = colspan;
		}
		var divGroup = createTable(maxTds, trs.length);
		var trs = groups[i].getElementsByTagName('tr');
		for (var j = 0; j < trs.length; j++) {
			if (trs[j].getElementsByTagName('hr').length != 0) {
				var hrs = trs[j].getElementsByTagName('hr');
				for (var k = 0; k < hrs.length; k++) {
					var cellId = j * maxTds  + 1;
					var cell = $(cellId.toString());
					addSeparatorForGivenCell(cell);
				}
			}
			var tds = trs[j].getElementsByTagName('td');
			var prevColspan = 0;
			for (var k = 0; k < tds.length; k++) {
				var cellId = j * maxTds + k + 1 + prevColspan;
				var cell = $(cellId.toString());
				var html = tds[k].innerHTML;
				var colspan = parseInt(tds[k].getAttribute('colspan'));
				if (colspan > 1) {
					for (var a = 1; a < colspan; a++) {
						spanGivenCell(cell);
					}
					prevColspan = colspan - 1;
				}
				if (html == null || html == '' ) 
					continue;
				var ind1 = html.indexOf('("') + 2;
				var ind2 = html.indexOf('")');
				var name = html.substring(ind1, ind2);
				if (html.indexOf('$doc') == -1) {
					// pretty name
					var id = name + "|" + "true";
					var field = $(id);
				}
				else {
					// field
					var id = name + "|" + "false";
					var field = $(id);
				}
				onFieldDrop(field, cell);
			}
		}
	}
}

function mouseOverEditBoxField(evt) {
	evt.target.className = 'editingTextHover';
}

function mouseOutEditBoxField(evt) {
	evt.target.className = 'editingText';
}