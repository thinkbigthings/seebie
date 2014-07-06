
// this function creates request objects that can be passed to $.ajax()
var ajaxObjectMaker = function(httpMethod, url, requestBody, successCallback, failureCallback) {
   if(typeof(requestBody) !== 'string') {
      requestBody = JSON.stringify(requestBody);
   }
   return {
      'contentType': 'application/json',
      'dataType': 'json',
      'type': httpMethod,
      'url': url,
      'data': requestBody,
      'success': successCallback,
      'error': failureCallback
   };
};
var postJson = ajaxObjectMaker.curry('POST');
var putJson = ajaxObjectMaker.curry('PUT');
var getJson = ajaxObjectMaker.curry('GET');
var deleteJson = ajaxObjectMaker.curry('DELETE');
var none = function(){};


// pass in a selector for a form object
// returns: a json object whose keys are contained inputs' name property, and whose values are the contents of the inputs
function getObjectFromForm(formSelector) {
	
	// this works the same as my other test code that maps form element to name/value pair object
	var formSerialized= $(formSelector).serializeArray();

	var addKeyValsToObject = function(obj, keyVals) {
       var keyValsName = keyVals.name.trim();
	    if(keyValsName.length == 0) {
          return obj;
	    }

       obj[keyValsName] = keyVals.value;
       
	    return obj;
	 };

	 var formObject = formSerialized.reduce( addKeyValsToObject, {} );
	 return formObject;
}
