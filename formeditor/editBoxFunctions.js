// EDIT BOX FUNCTIONS (bez setera)
function selectCell() {
	selectGivenCell(this);
}

function selectGivenCell(cell) {
	if (selectedCell != null) {
		selectedCell.style.border = "solid 1px gray";
	}
	selectedCell = cell;
	$('selectCellNote').style.display = 'none';
	$('editingFields').style.display = 'block';
	cell.style.border = "dashed 1px orange";

	updateEditingFields(selectedCell);
	updateFieldSettings(selectedCell);
}

function updateFieldSettings(cell) {
	var fieldSettings = $('fieldSettings');
	var tbdFieldSettings = $('tbdFieldSettings');
	tbdFieldSettings.innerHTML = '';
	if (cell.firstChild == null || cell.firstChild.fieldModel == null)
		return;
	var fieldModel = cell.firstChild.fieldModel;
	for (var prop in fieldModel) {
		if (prop == 'type')
			continue;
		var trw = document.createElement('tr');
		var td = document.createElement('td');
		td.innerHTML = prop;
		trw.appendChild(td);
		var td = document.createElement('td');
		td.innerHTML = fieldModel[prop];
		td.firstChild.className = 'settingsFields';
		td.firstChild.disabled = 'disabled';
		trw.appendChild(td);
		tbdFieldSettings.appendChild(trw);
	}
	var button = document.createElement('input');
	button.type = 'submit';
	button.value = 'Change settings';
	var trw = document.createElement('tr');
	var td = document.createElement('td');
	td.setAttribute('colspan', 2);
	td.appendChild(button);
	trw.appendChild(td);
	tbdFieldSettings.appendChild(trw);
		
}

function updateEditingFields(cell) {
	var group = getGroupFromCell(cell);
	var titleObj = getTitleObjectFromGroup(group);
	var titleText = getTitleTextFromTitleObject(titleObj);
	$('editGroupTitleInput').value = titleText;
	
	var presText = getPresentationTextFromGroup(group);
	$('editPresentationTextInput').value = presText;

	var row = getRowFromCell(cell);
	var rowHeight = getRowHeight(row);
	$("editRowHeightInput").value = rowHeight;
	
	if (getSeparatorBelowRow(row) != null) {
		$('editAddSeparator').style.display = 'none';
		$('editRemoveSeparator').style.display = '';
	}
	else {
		$('editAddSeparator').style.display = '';
		$('editRemoveSeparator').style.display = 'none';
	}
	
	if (getFieldFromCell(cell) == null) {
		$('editDeleteField').style.display = 'none';
		$('editCellText').style.display = '';
		$('editCellTextInput').value = getTextFromCell(cell);
	}
	else {
		$('editDeleteField').style.display = '';
		$('editCellText').style.display = 'none';
	}
	
	if (cell.nextSibling == null)
		$('editSpanCell').style.display = 'none';
	else
		$('editSpanCell').style.display = '';
	
	if (cell.isSpanned) {
//		$('editSpanCell').style.display = 'none';
		$('editSplitCell').style.display = '';
	}
	else {
//		$('editSpanCell').style.display = '';
		$('editSplitCell').style.display = 'none';
	}
}

function emptyCell(cell) {
	if (cell == null)
		cell = selectedCell;
	var el = cell.firstChild;
	cell.innerHTML = "";
	cell.isOccupied = false;
	// uzmi polje iz celije i stavi ga na prvo prazno mesto u fields box
	putFieldInFieldsBox(el);

	updateEditingFields(cell);
}

function addSeparator() {
	if (selectedCell == null)
		return;
	addSeparatorForGivenCell(selectedCell);
}

function addSeparatorForGivenCell(cell) {
	var row = cell.parentNode;
	var hr = document.createElement("hr");
	hr.style.cssFloat = "left";
	hr.style.width = "100%";
	if (row.nextSibling != null) {
		row.parentNode.insertBefore(hr, row.nextSibling);
	}
	else {
		row.parentNode.appendChild(hr);
	}

	updateEditingFields(cell);
	
}

function removeSeparator() {
	if (selectedCell == null)
		return;
	var row = getRowFromCell(selectedCell);
	row.parentNode.removeChild(row.nextSibling);

	updateEditingFields(selectedCell);
}

// CELL METHODS

function spanCell() {
	if (selectedCell == null)
		return;
	spanGivenCell(selectedCell);
}

function spanGivenCell(cell) {
	var c1 = cell;
	var c2 = c1.nextSibling;
	if (c2 == null)
		return;
	var par = c1.parentNode;
	par.removeChild(c2);
	c1.style.width = parseFloat(c1.style.width) + parseFloat(c2.style.width) + "%";
	c1.isSpanned = true;
	if (c1.spannedCount == null)
		c1.spannedCount = 1;
	c1.spannedCount++;

	updateEditingFields(cell);
}

function splitCell() {
	if (selectedCell == null)
		return;
	var par = selectedCell.parentNode;
	var group = getGroupFromCell(selectedCell);
	var table = getTableFromGroup(group);
	var row = getRowFromCell(selectedCell);

	var newWidth1 = 100 / table.colsCount * 0.98;
	var newWidth2 = parseFloat(selectedCell.style.width) - newWidth1;
	selectedCell.style.width = newWidth2 + "%";
		
	var d = document.createElement("div");
	d.id = ++cellCounter;
	d.className = "divCell";
	d.style.height = "95%"
	d.style.width = newWidth1 + "%";
	d.style.margin = "0px";
	Droppables.add(d, {accept: 'fieldContainer', onDrop: onFieldDrop, hoverclass: "divCellHover"})
	d.onclick = selectCell;
	
	// ako je celija na kraju reda, mora da se doda nova, ako nije, onda mora da se umetne
	if (selectedCell.nextSibling == null) 
		par.appendChild(d);				
	else 
		par.insertBefore(d, selectedCell.nextSibling)		

	// ako ima jednak broj celija kao na pocetku, onda nije spanovana
	if (table.colsCount != getColsCountFromRow(row))
		selectedCell.isSpanned = true;
	else
		selectedCell.isSpanned = false;
	selectedCell.spannedCount--;

	updateEditingFields(selectedCell);
}

function deleteGroup() {
	if (selectedCell == null)
		return;
	var group = getGroupFromCell(selectedCell);
	group.parentNode.removeChild(group);

	updateEditingFields(selectedCell);
}


function addRow() {
	if (selectedCell == null)
		return;
	var group = getGroupFromCell(selectedCell);
	var table = getTableFromGroup(group);
	var colsCount = table.colsCount;
	var rowsCount = table.rowsCount;
	var row = getRowFromCell(selectedCell);

	var divRow = document.createElement("div");
	divRow.className = "divRow";
	divRow.style.cssFloat = "";
	divRow.style.border = "none";
	divRow.style.width = "100%";
	divRow.style.padding = "0px";
	divRow.style.margin = "1px";
	if (row.nextSibling == null)
		table.appendChild(divRow);
	else
		table.insertBefore(divRow, row.nextSibling);
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
	table.rowsCount++;
}

function addColumn() {
	if (selectedCell == null)
		return;
	var group = getGroupFromCell(selectedCell);
	var table = getTableFromGroup(group);
	var colsCount = table.colsCount;
	colsCount++;
	var rowsCount = table.rowsCount;
	var row = getRowFromCell(selectedCell);
	// nadji mesto celije u redu
	var cellIndex = 0;
	for (var el = row.firstChild; el != null && el != selectedCell; el = el.nextSibling) {
		if (el.className == 'divCell')
			cellIndex++;
	}
	var rows = document.getElementsByClassName('divRow', table);
	table.style.width = Number(colsCount * cellWidth) + "px";

	for (var i = 0; i < rows.length; i++) {
		var cells = document.getElementsByClassName('divCell', rows[i]);
		for (var j = 0; j < cells.length; j++) {
			cells[j].style.width = Number(cells[j].spannedCount * (100 / colsCount * 0.98)) + "%";
		}
		var d = document.createElement("div");
		d.id = ++cellCounter;
		d.className = "divCell";
		d.style.height = "95%"
		d.style.width = Number(100 / colsCount * 0.98) + "%";
		d.style.margin = "0px";
		d.spannedCount = 1;
		rows[i].appendChild(d);
		Droppables.add(d, {accept: ['fieldContainer', 'newFieldContainer'], onDrop: onFieldDrop, hoverclass: "divCellHover"})
		d.onclick = selectCell;
		if (cellIndex == colsCount-1) {
			rows[i].appendChild(d);
		}
		else {
			rows[i].insertBefore(d, cells[cellIndex+1])
		}
	}
	table.colsCount++;	
}























