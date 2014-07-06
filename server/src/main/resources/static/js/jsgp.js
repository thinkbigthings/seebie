
// here are two ways to implement function composition
function compose(func1, func2) {
  return function() {
    return func1(func2.apply(null, arguments));
  };
}
Function.prototype.compose  = function(argFunction) {
   var invokingFunction = this;
   return function() {
       return invokingFunction.call(this,argFunction.apply(this,arguments));
   }
}

// from JSGP
// this allows you to sort arrays by a particular field
// and handles sorting of numbers and strings better than the default array sort
// TODO sort on multiple keys, see JSGP pg 81
var by = function(name) {
   return function(o,p) {
      if(typeof o === 'object' && typeof p === 'object' && o && p) {
         a = o[name];
         b = p[name];
         if(a===b) {
            return 0;
         }
         if(typeof a === typeof b) {
            return a < b ? -1 : 1;
         }
         return typeof a < typeof b ? -1 : 1;
      }
      else {
         throw {
            name: 'Error',
            message: 'Expected an object when sorting by ' + name 
         };
      }
   };    
};

// from JSGP
// By augmenting Function.prototype with a "method" method, 
// we no longer have to type the name of the prototype property
Function.prototype.method = function(name,func) {
   this.prototype[name] = func;
   return this;
}

// from JSGP
Function.method('curry', function() {
   var slice = Array.prototype.slice;
   var args = slice.apply(arguments);
   var that = this;
   return function() {
      return that.apply(null, args.concat(slice.apply(arguments)));
   };
});


// from JSGP
// js does not have a separate integer type,
// so sometimes it is necessary to extract the the integer part of a number
// We can fix it by adding an integer method to Number.prototype
Number.method('integerPart', function() {
   return Math[this < 0 ? 'ceil' : 'floor'](this);
});

// from JSGP
// js doesn't have a method that removes spaces from the ends of a string
String.method('trim', function() {
   return this.replace(/^\s+|\s+$/g, '');
});

