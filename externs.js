// v8
var write = function() {};

// phantomjs
var phantom = {};
phantom.exit = function() {};

// rhino
var java = {};
java.lang = {};
java.lang.System = {};
java.lang.System.out = {};
java.lang.System.out.print = function() {};
