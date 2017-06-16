var app = angular.module('agentsPlayground.routes', ['ngRoute']);

app.config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
	
	$routeProvider
		.when('/agents', {
			templateUrl: "html/agents.html"
		});
}]);