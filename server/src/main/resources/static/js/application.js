
var setDisabled = function(jqSelector, disabled) {
   if(disabled) {
      $(jqSelector).attr('disabled', 'disabled');
   }
   else {
      $(jqSelector).removeAttr('disabled');
   }       
};
 
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
var no_op = function(){};

// pass in a selector for a form object
// returns: a json object whose keys are contained inputs' name property, and whose values are the contents of the inputs
function getObjectFromForm(formSelector) {
	
    var formSerialized= $(formSelector).serializeArray();

    var addKeyValsToObject = function(obj, keyVals) {
    var keyValsName = keyVals.name.trim();
        if(keyValsName.length !== 0) {
            obj[keyValsName] = keyVals.value;
        }
        return obj;
     };

     return formSerialized.reduce( addKeyValsToObject, {} );
}

var APP = (function() {

   var currentUserUrl = '/user/current';
   var my = {};
   
   // keys into the session storage
   var key = {};
   key.currentUserUrl = currentUserUrl;
   key.credentials = 'credentials';
   key.loggedIn = 'loggedIn';
   key.screen = 'screen';
   
   var user;
   var credentials = {'username':'', 'password':''};
   var loggedIn = false;
   var currentScreen;
   
   var setCurrentScreen = function(screen) {
      sessionStorage[key.screen] = screen;
      currentScreen = screen;
   };
   
   var getCurrentScreen = function() {
      if (!currentScreen) {
        if(sessionStorage[key.screen]) {
          currentScreen = sessionStorage[key.screen];
        }
        else {
          currentScreen = 'home';
        }
      }
      return currentScreen;
   };
   
   var getCurrentUser = function() {
      if (!user && sessionStorage[key.currentUserUrl]) {
          user = JSON.parse(sessionStorage[key.currentUserUrl]);
      }
      return user;
   };
   
   var isLoggedIn = function() {
      if (sessionStorage[key.loggedIn]) {
          loggedIn = JSON.parse(sessionStorage[key.loggedIn]);
      }
      return loggedIn;
    };

   var addBasicAuth = function(request) {
      request.headers = {
        "Authorization": "Basic " + btoa(credentials.username + ":" + credentials.password)
      };
      return request;
    };
  
   // validates credentials, and gives the current user object to the specified function, lazily loading if necessary
   function login(username, password, loginSuccess)
   {
      credentials.username = username;
      credentials.password = password;
      sessionStorage[key.credentials] = JSON.stringify(credentials);
      
      var onGetUserSuccess = function (data, textStatus, jqXHR) {
          user = data;
          setLoggedIn(true);
          sessionStorage[key.currentUserUrl] = JSON.stringify(data);
          loginSuccess(user);
      };

      $.ajax(addBasicAuth(getJson(currentUserUrl, '', onGetUserSuccess, no_op)));

   };
   
   function logout() {
       setLoggedIn(false);
       sessionStorage.clear();
       currentScreen = 'home';
   };
   
   function setLoggedIn(value) {
      loggedIn = value;
      sessionStorage[key.loggedIn] = JSON.stringify(loggedIn);
   }

   my.login = login;
   my.logout = logout;
   my.isLoggedIn = isLoggedIn;
   my.getCurrentUser = getCurrentUser;
   my.getCurrentScreen = getCurrentScreen;
   my.setCurrentScreen = setCurrentScreen;
   
   return my;

}());
