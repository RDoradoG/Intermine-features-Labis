/*===============================================================
=					Phycomine Base Library						=
=																=
▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒
= Labis - IQ, USP. São Paulo									=
= @author Rodrigo Dorado						█║█║║█║█║█║║█	=
===============================================================*/

function APIExecuteQuery(query, idCall, callback, sz, strt) {
	var size     = (sz == undefined) ? 1000 : sz;
	var start    = (strt == undefined) ? 0 : strt;
	var settings = {
	  async: true,
	  url:  'service/query/results/tablerows',
	  method: 'POST',
	  headers: {
	    "Content-Type": 'application/x-www-form-urlencoded',
	    Accept: 'application/json',
	    Authorization: 'Token ' + api_key
	  },
	  data: {
	    start: start,
	    size: size,
	    query: query,
	    format: 'json'
	  }
	}

	jQuery.ajax(settings).done(function (response) {
	  var result = getResultFormat(response);
	  if (callback == undefined) {
	  	getResult(result, idCall);
	  } else {
	  	callback(result, idCall);
	  }
	});
}

function getResultFormat(response) {
	var result = [];
	var row;
	var data;
	for (var i = 0; i < response.results.length; i++) {
		row = {};
		for (var j = 0; j < response.results[i].length; j++) {
			row[response.results[i][j].column] = {
				value: response.results[i][j].value,
				id: response.results[i][j].id,
				class: response.results[i][j].class,
				url: response.results[i][j].url
			};
		}
		result.push(row);
	}
	return result;
}

function clone(obj) {
	var copy;

	if (null == obj || "object" != typeof obj) {
		return obj;
	}

	if (obj instanceof Array) {
		copy = [];
		for (var i = 0, len = obj.length; i < len; i++) {
			copy[i] = clone(obj[i]);
		}
		return copy;
	}

	if (obj instanceof Object) {
		copy = {};
		for (var attr in obj) {
			if (obj.hasOwnProperty(attr)) {
				copy[attr] = clone(obj[attr]);
			}
		}
		return copy;
	}
}

function fillAOption(id, value, selected) {
	var option = jQuery("<option></option>").attr("value", value).text(value);
	if (selected) {
		option.attr("selected", "selected");
	}
	jQuery('#' + id).append(option);
}

/**
 * Open a model pop up
 * @param  {String} id Id of the modal
 * @return {null}
 * @author Rodrigo Dorado
 */
function openModal(id) {
	jQuery('#' + id).modal('show');
}

/**
 * Close a model pop up
 * @param  {String} id Id of the modal
 * @return {null}
 * @author Rodrigo Dorado
 */
function closeModal(id) {
	jQuery('#' + id).modal('hide');
}

/**
 * Close a modal pop up
 * @param  {String} id Id of the modal
 * @return {null}
 * @author Rodrigo Dorado
 */
function closeModal(id) {
	jQuery('#' + id).modal('hide');
	jQuery('body').removeClass('modal-open');
	jQuery('.modal-backdrop').remove();
}
