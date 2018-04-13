function APIExecuteQuery(query, idCall, sz, strt) {
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
	  getResult(result, idCall);
	});
}

function getResultFormat(response) {
	var result = [];
	var row;
	var data;
	for (var i = 0; i < response.results.length; i++) {
		row = {};
		for (var j = 0; j < response.results[i].length; j++) {
			row[response.results[i][j].column] = response.results[i][j].value;
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