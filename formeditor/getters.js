// GETTERS

function getGroupFromCell(cell) {
	var obj = cell;
	while (obj != null && obj.className != "group")
		obj = obj.parentNode;
	return obj;
}

function getTitleObjectFromGroup(group) {
	return title = group.getElementsByTagName('legend')[0];
}

function getTitleTextFromTitleObject(titleObj) {
	return titleObj.innerHTML;
}

function getPresentationTextObjectFromGroup(group) {
	if (group.firstChild.nodeName == '#text')
		var div = group.firstChild;
	return div;
}

function getPresentationTextFromGroup(group) {
	var obj = getPresentationTextObjectFromGroup(group);
	return obj.data;
//	var divs = group.getElementsByTagName('div');
//	for (var i = 0; i < divs.length; i++) {
//		if (divs[i].className == 'groupPresentationText') {
//			var div = divs[i];
//			break;
//		}
//	}
//	return div.innerHTML;
}

function getColsCountFromRow(row) {
	var divs = row.getElementsByTagName('div');
	var count = 0;
	for (var i = 0; i < divs.length; i++) {
		if (divs[i].className == 'divCell') {
			count++;
		}
	}
	return count;
	
}

function getRowFromCell(cell) {
	return cell.parentNode;
}

function getRowHeight(row) {
	return row.offsetHeight;
}

function getSeparatorBelowRow(row) {
	if (row.nextSibling != null && row.nextSibling.nodeName == "HR")
		return row.nextSibling;
}

function getFieldFromCell(cell) {
	if (cell.isOccupied)
		return cell.firstChild;
	else
		return null;
}

function getTextFromCell(cell) {
	if (cell.firstChild != null && cell.firstChild.nodeName == '#text') 
		return cell.firstChild.data;
	else
		return null;
}

function getTableFromGroup(group) {
	var divs = group.getElementsByTagName('div');
	for (var i = 0; i < divs.length; i++) {
		if (divs[i].className == 'divTable') {
			var div = divs[i];
			break;
		}
	}
	return div;
}

// SETTERS (for selected cell)

function setGroupTitleText() {
	var group = getGroupFromCell(selectedCell);
	var titleTextDiv = getTitleObjectFromGroup(group);
	titleTextDiv.innerHTML = $("editGroupTitleInput").value;

	updateEditingFields(selectedCell);
}

function setGroupPresentationText() {
	var group = getGroupFromCell(selectedCell);
	var presTextDiv = getPresentationTextObjectFromGroup(group);
	presTextDiv.data = $("editPresentationTextInput").value;

	updateEditingFields(selectedCell);
}

function setRowHeight() {
	var row = getRowFromCell(selectedCell);
	row.style.height = $('editRowHeightInput').value + "px";

	updateEditingFields(selectedCell);
}

function setCellText() {
	selectedCell.innerHTML = $('editCellTextInput').value;

	updateEditingFields(selectedCell);
}